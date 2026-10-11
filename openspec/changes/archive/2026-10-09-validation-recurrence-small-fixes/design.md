## Context

Six small, unrelated-by-code but related-by-size defects, each confirmed against `develop` (4.4.1-develop-SNAPSHOT) during the 2026-10-09 open-issue triage. Each has a contained fix and a regression test; they are grouped to keep PR count manageable. The relevant code paths:

- `Calendar.validate(boolean recurse)` → `validator.validate(this)` (a `CalendarValidatorImpl`) and, only when `recurse`, `validateProperties()` + `validateComponents()`. `validateComponents()` calls `c.validate(method)` when METHOD is present. `CalendarValidatorImpl.validate` **also** loops `component.validate(method)` (introduced by `3fcf64d11` to stop the loop's results being dropped), so the loop runs in both places.
- `ComponentValidator.VEVENT/VFREEBUSY/VAVAILABILITY` list UID and DTSTAMP under both `One` and `OneOrLess`. `PropertyContainerRuleSet` evaluates each rule independently, so a doubled DTSTAMP violates both (`size() != 1` and `size() > 1`).
- `ComponentBuilder.name()` upper-cases and stores; `hasName()` uses `String.equals`, so only an upper-case argument matches. `PropertyBuilder.hasName` already uses `equalsIgnoreCase`.
- `TzHelper` and `TimeZoneRegistryImpl` load `msTimezones` / `msTimezoneNames`; `Central America Standard Time` maps to `US/Central` (alias of `America/Chicago`).
- `RecurrenceSet.Builder.build()` computes `startMinusDuration = bounds.getStart().minus(effectiveDuration)`. For DATE-bounded periods (`LocalDate`) and a `java.time.Duration` (any date-time DTSTART/DTEND), `LocalDate.minus(Duration)` throws.
- `Period.intersects(other)` is `other.equals(this) || this.start < other.end && other.start < this.end` (via `TemporalComparator`). A zero-length `other` can never satisfy `this.start < other.end` when `other.start == this.start`.

## Goals / Non-Goals

**Goals**
- `Calendar.validate(false)` validates only the calendar level; `validate(true)` reports each component-level entry once.
- A doubled required property yields one entry.
- Component names match case-insensitively in the builder.
- Central America maps to a non-DST zone.
- `calculateRecurrenceSet` accepts DATE-bounded periods for date-time events.
- Zero-length periods intersect by half-open containment of their instant.

**Non-Goals**
- Changing `Period.includes` (inclusive at both ends) or `PeriodList.normalise` (drops empty periods; free/busy semantics).
- Auditing the remaining Microsoft timezone entries (only the reported one is corrected; the spec invites more scenarios).
- Making `Recur.getDates` itself accept mixed bound types differently from today.

## Decisions

### D1 (#363): remove the component loop from `CalendarValidatorImpl`, keep `ITIPValidator`
The calendar validator is the "calendar level" of a two-level scheme; `Calendar.validateComponents()` is the component level and already merges `c.validate(method)` results. Removing the loop restores the `recurse` contract and removes the duplication. `ITIPValidator` (PRODID/VERSION/METHOD presence, one component type) stays, as it is a calendar-level rule.
*Alternative:* keep the loop and drop `validateComponents()`'s METHOD branch — rejected, because then `validate(false)` would still descend into components.
*Consequence:* `new CalendarValidatorImpl().validate(cal)` alone no longer reports component iTIP violations; the spec scenarios that said so are re-scoped to `Calendar.validate()`.

### D2 (#692): drop UID/DTSTAMP from `OneOrLess`
`One` already implies at most one. No rule is lost; the duplicate entry disappears.

### D3 (#691): `equalsIgnoreCase` in `ComponentBuilder.hasName`
Mirrors `PropertyBuilder`. `name` is stored upper-cased, so `equalsIgnoreCase` is sufficient and allocation-free.

### D4 (#709): `America/Guatemala`
CLDR `windowsZones.xml` territory `001` for `Central America Standard Time` is `America/Guatemala`. Both resource files are updated so `TzHelper` (compliance transform) and `TimeZoneRegistryImpl` aliases agree. A bundled `zoneinfo/America/Guatemala.ics` exists.

### D5 (#736): widen the look-back by whole days for DATE bounds
When `effectiveDuration` is a `Duration` and `bounds.getStart()` does not support `ChronoUnit.SECONDS`, use `bounds.getStart().minus(duration.toDays() + 1, DAYS)`. The look-back only needs to be *at least* the duration (it exists to catch instances that start before the period but overlap it); over-widening by up to a day is harmless because the resulting periods are still filtered by the `Recur` upper bound and the caller's expectations on `[start, end)`. Keeping the bound a `LocalDate` avoids changing the types handed to `Recur.getDates`, which already compares mixed temporals via `TemporalComparator`.
*Alternative:* convert DATE bounds to `LocalDateTime` at start of day — rejected; changes the comparison type for every rule evaluation and the behaviour for floating seeds.

### D6 (#82): half-open containment for zero-length periods
`intersects` gains two guards before the general case: if `other` is zero-length, return `this.start <= other.start < this.end`; symmetrically if `this` is zero-length. Equal periods short-circuit to `true` as before. The end stays exclusive so a zero-length period at exactly `this.end` does not intersect, consistent with the existing half-open semantics for non-empty periods. Zero-length is detected with `TemporalComparator`, not `isEmpty()`, to avoid an `Interval` conversion through the default zone.

## Risks / Trade-offs

- [D1] A consumer that validated through `CalendarValidatorImpl` directly and depended on component iTIP entries loses them. → Called out in the proposal and release notes; `Calendar.validate()` is the documented entry point.
- [D6] Any code that relied on zero-length periods never intersecting (e.g. to filter them out) now sees them. → Searched `src/main`: `PeriodList.normalise` drops empties explicitly and is unaffected; `RecurrenceSet` is the intended beneficiary.
- [D5] Instances up to one day before the period start may be examined; they are discarded by the existing filters, so output is unchanged for previously working inputs.
- [D4] Users who depended on the wrong `US/Central` mapping see a one-hour change in summer. → This is the correction.

## Migration Plan

Straight fix on a branch off `develop`; no data or configuration migration. Rollback is a revert.

## Open Questions

- None blocking. A full audit of `msTimezones` against current CLDR data would be a sensible follow-up issue.
