## Context

`ComponentGroup` (`src/main/java/net/fortuna/ical4j/model/ComponentGroup.java`) groups the revisions of one calendar entity — the master component(s) sharing a `UID` plus any `RECURRENCE-ID` overrides — and `calculateRecurrenceSet(period)` is meant to return the effective occurrences: the master's recurrence set with each overridden instance replaced by its override.

Current implementation:

```java
for (Component component : getRevisions()) {           // filtered by componentPredicate
    if (component.getProperty(RECURRENCE_ID).isPresent()) overrides.add(component);
    else periods.addAll(component.calculateRecurrenceSet(period));
}
overrides.forEach(component -> {
    RecurrenceId<?> recurrenceId = component.getRequiredProperty(RECURRENCE_ID);
    finalPeriods.removeIf(p -> p.getStart().equals(recurrenceId.getDate()));
    component.calculateRecurrenceSet(period).stream()
            .filter(p -> p.getStart().equals(recurrenceId.getDate()))   // (1)
            .forEach(p -> finalPeriods.add((Period<T>) p));
});
```

Three defects interact:

1. **Rescheduled overrides are dropped** — filter (1), added in `d0e113da5` for #472, keeps only periods starting exactly at the `RECURRENCE-ID`. An override whose `DTSTART` differs from its `RECURRENCE-ID` (the whole point of rescheduling) is discarded. This is #510.
2. **Overrides are often never seen** — the `ComponentGroup(components, uid)` constructor (since `438f9de99`, 4.0.0-beta9) sets `componentPredicate = uid AND NOT exists(RECURRENCE-ID)`, and `getRevisions()` applies it. `PropertyExistsRule.PropertyExists.compareTo` compares the property name *and parameter list* against a bare `new RecurrenceId<>()`, so only overrides whose `RECURRENCE-ID` has no parameters are actually excluded. A `RECURRENCE-ID;TZID=UTC:…` or `;VALUE=DATE` override reaches the loop; a `RECURRENCE-ID:20210715T000000Z` override does not. This is why the bug is intermittent across calendars.
3. **Cross-type matching fails** — `p.getStart().equals(recurrenceId.getDate())` uses `Temporal.equals`, so a `ZonedDateTime` master instance never matches an `Instant`/`OffsetDateTime` `RECURRENCE-ID` for the same moment, and the override is applied without the master instance being removed (duplicate).

`ComponentGroupTest."CalculateRecurrenceSetWithException"` asserts `master.size() - 1` for an override moved to the next day, i.e. it codifies defect 1.

Constraints: `calculateRecurrenceSet` is public API (signature must not change; RevAPI runs in `check`); `Component.calculateRecurrenceSet` is `final` and its duration-derivation rules (DTEND/DUE/DURATION, 1-day default for DATE values) are the reference behaviour; `TemporalComparator.INSTANCE` is the project's sanctioned cross-type comparator (see `openspec/specs/temporal-comparison`).

## Goals / Non-Goals

**Goals:**
- Every `RECURRENCE-ID` override in the group participates in `calculateRecurrenceSet`, regardless of how its `RECURRENCE-ID` is parameterised.
- A rescheduled override contributes its own occurrence in place of the master instance it names.
- An override carrying a stray `RRULE`/`RDATE` still contributes exactly one occurrence (#472 stays fixed).
- `RECURRENCE-ID` matching is by absolute instant across `Instant`/`OffsetDateTime`/`ZonedDateTime`, and by value for `LocalDate`/`LocalDateTime`.
- No change to the public signature; existing behaviour for groups without overrides is unchanged.

**Non-Goals:**
- `RANGE=THISANDFUTURE` on `RECURRENCE-ID` (still treated as a single-instance override).
- Changing what the `ComponentGroup(uid)` predicate means for `add`/`replace`/`getRevisions()`; those keep their current contract.
- Sequence/`DTSTAMP`-based selection among multiple revisions of the *same* override (`getLatestRevision` semantics). If several components share a `UID`+`RECURRENCE-ID`, each is applied in list order as today.

## Decisions

### D1. Select revisions for recurrence calculation by `UID` only, inside `calculateRecurrenceSet`

`calculateRecurrenceSet` will filter `getComponents()` with a `UID`-only predicate rather than calling `getRevisions()`. The constructor predicates and `getRevisions()` are left untouched.

*Alternative — change the `ComponentGroup(uid)` predicate to `UID` only:* fixes discovery but silently changes `add`/`replace`/`getRevisions`/`getLatestRevision` for every caller, and the 2023 javadoc states the exclusion is deliberate. Too broad for a bug fix; can be revisited separately.

*Alternative — fix `PropertyExistsRule` to ignore parameters:* its javadoc says parameter comparison is intentional, and changing it would alter filter semantics library-wide. Out of scope.

Implementation note: the `UID` is not stored on `ComponentGroup` today (only the composed predicate is). Store the `Uid` in a private final field set by every constructor so it can be reused; no public accessor is required.

### D2. An override contributes only its own occurrence

For each override, build a `RecurrenceSet.Builder` with `start`/`end`/`duration` taken from the override's `DTSTART`, `DTEND`|`DUE`, `DURATION`, and the query `period`, with **no** recurrence rules, recurrence dates, exception dates or exception rules. `RecurrenceSet.Builder.build()`'s no-rules branch already produces exactly "the initial instance if it intersects the period", using the same effective-duration rules as `Component.calculateRecurrenceSet`. Set `Period.setComponent(override)` on the result so callers can trace the instance to its override.

*Alternative — keep calling `component.calculateRecurrenceSet(period)` and drop the filter:* regresses #472 (an override with the master's `RRULE` would spawn the whole series again).

*Alternative — copy the override and strip `RRULE`/`RDATE`/`EXDATE`/`EXRULE` before calling `calculateRecurrenceSet`:* works but allocates a full component copy per override and relies on `Component.copy()`; the builder approach is cheaper and expresses the intent directly.

Edge: an override whose own `DTSTART` falls outside the query period but whose `RECURRENCE-ID` is inside removes the master instance and adds nothing — correct (the instance moved out of view). The converse (`RECURRENCE-ID` outside, `DTSTART` inside) adds the override's occurrence; there is no master instance in range to remove.

### D3. Match master instances to `RECURRENCE-ID` with `TemporalComparator`

Replace `p.getStart().equals(recurrenceId.getDate())` with `TemporalComparator.INSTANCE.compare(p.getStart(), recurrenceId.getDate()) == 0`. For instant-bearing types this compares the absolute instant; for `LocalDate`/`LocalDateTime` it compares the value. Mixed local/zoned pairs are resolved by the comparator's existing rules; this change does not try to be cleverer than that.

### D4. Correct the existing test rather than preserve it

`"CalculateRecurrenceSetWithException"` becomes `master.size()` (one removed, one added) and additionally asserts that the override's `DTSTART` is present and the `RECURRENCE-ID` date is absent. New cases cover: bare (parameter-less) `RECURRENCE-ID` rescheduled to a different day; override with a stray `RRULE`; override not rescheduled (same start, different duration); `ZonedDateTime` master vs UTC `RECURRENCE-ID`; override moved outside the query period; `VALUE=DATE` override of a date-time master (the #510 input).

## Risks / Trade-offs

- [Consumers may have worked around the bug by expanding overrides themselves and will now see those instances twice] → Call out in the release notes under "Fixed"; the fix restores RFC-correct output and the workaround is easy to remove.
- [`Period.getComponent()` for an override instance now returns the override component rather than the master] → This is more useful, not less, but note it in the changelog; `Period.getComponent()` was never documented to return the master.
- [`TemporalComparator` semantics for local vs zoned mixes differ from `equals`] → Only affects calendars mixing floating and zoned values for the same `UID`, which RFC 5545 §3.8.4.4 already says must share the `DTSTART` value type. Covered by the comparator's own spec; no new behaviour invented here.
- [RevAPI] → No signature changes; a private field addition is not reported. If RevAPI flags anything, record the justification in `.palantir/revapi.yml`.

## Migration Plan

Straight fix on a branch off `develop`; no data or configuration migration. Rollback is a revert of the single commit.

## Open Questions

- None blocking. Whether `ComponentGroup(uid)` should stop excluding overrides altogether (D1 alternative) is worth a separate issue once this fix lands.
