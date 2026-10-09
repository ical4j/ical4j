## Context

`RecurrenceSet.Builder.build()` (`src/main/java/net/fortuna/ical4j/model/RecurrenceSet.java`) is the single place where a component's `DTSTART`/`DTEND`/`DURATION`, `RDATE`, `RRULE`, `EXDATE` and `EXRULE` are combined with a query `Period` into a set of occurrence periods. It is used by `Component.calculateRecurrenceSet` and `ComponentGroup.overrideOccurrence`.

The query period is applied inconsistently today:

| source | filter | end boundary |
|---|---|---|
| non-recurring initial instance | `bounds.intersects(initialPeriod)` | exclusive (`Period.intersects` uses strict `<` both ways) |
| `RDATE;VALUE=PERIOD` | `bounds::intersects` | exclusive |
| `RDATE` dates | `bounds::includes` | **inclusive** (`Period.includes` is closed) |
| `RRULE` dates | `rrule.getDates(seed, start - duration, end)` | **inclusive** (`RecurDateSpliterator` only rejects candidates *after* `periodEnd`, despite its "exclusive of periodEnd" comment) |
| `EXRULE` dates | same as `RRULE` | inclusive |

Issue #603 is the `RRULE` row. Fixing it in `RecurDateSpliterator` was tried first: it breaks 17 existing tests (`RecurSpec` DST, BYMONTHDAY, BYYEARDAY and UNTIL cases, `VEventTest.getOccurrence`, `ComponentGroupTest`), because `Recur.getDates(seed, periodStart, periodEnd)` is used throughout the codebase and its tests with an inclusive `periodEnd`. That contract is documented only by usage, but it is extensive.

`VEvent.getOccurrence(date)` calls `getConsumedTime(new Period<>(date, date))` and expects the instance starting at `date` back; a strictly half-open rule would make a zero-length period match nothing.

## Goals / Non-Goals

**Goals:**
- `Component.calculateRecurrenceSet(period)` never returns an occurrence that starts at `period.getEnd()`, regardless of whether it comes from `RRULE`, `RDATE` or the non-recurring instance.
- Occurrences starting before `period.getStart()` that overlap the period are still returned; an occurrence ending exactly at `period.getStart()` (e.g. yesterday's all-day instance when querying today) is not.
- `VEvent.getOccurrence(date)` keeps working.
- `Recur.getDates` is untouched.

**Non-Goals:**
- Changing `Period.includes` (closed) or `Period.intersects` (half-open); those are public and used by the filter package.

## Decisions

### D1. Apply the half-open bound inside `RecurrenceSet.Builder.build()`, not in `Recur`

Every occurrence source is mapped to its `Period` first and then filtered by one private predicate, `withinBounds(bounds, occurrence)`: the occurrence overlaps the half-open window `[bounds.start, bounds.end)`, i.e.

```
bounds.start < occurrence.end && occurrence.start < bounds.end
```

using `TemporalComparator.INSTANCE` so that `Instant`, `OffsetDateTime` and `ZonedDateTime` compare by instant and `LocalDate`/`LocalDateTime` by value. This is exactly `Period.intersects`, re-stated locally so the point-query exception (D2) can be layered on without touching the public `Period` API. The same predicate now governs `RRULE` dates, `RDATE` dates, `RDATE;VALUE=PERIOD` values and the non-recurring initial instance, which removes the per-source inconsistencies in the table above. `EXRULE` dates are left unfiltered: they are only used to remove periods, so an extra candidate is a no-op.

A first attempt filtered only the upper edge of `RRULE` dates. That left the symmetric lower-edge defect in place: `RRULE` dates are fetched from `start - effectiveDuration`, so yesterday's all-day instance (ending exactly at today's `period.getStart()`) was still returned for a one-day query. Filtering by overlap fixes both edges at once.

*Alternative — fix `RecurDateSpliterator`:* the honest reading of its comment, but it changes `Recur.getDates` for every caller and 17 tests encode the inclusive end. Rejected as too broad for a bug fix; can be revisited with a deprecation cycle if ever wanted.

*Alternative — pass `end.minus(1 unit)` to `getDates`:* unit depends on the temporal type (`LocalDate` vs date-time) and nanosecond arithmetic on zoned values is fragile. Rejected.

The lower bound is deliberately *not* tightened: `RRULE` dates are fetched from `start - effectiveDuration` so that instances overlapping the window start are kept. `withinBounds` only tightens the upper edge for those sources; the lower-bound check in the predicate exists for the `RDATE` date path, which previously used `includes` (closed on both ends) and now needs an explicit start-inclusive check.

### D2. A zero-length query period is a point query

When `period.getStart()` equals `period.getEnd()`, an occurrence that starts exactly at that instant, or is in progress at that instant, is included. A strictly half-open window would match nothing, which is not a useful meaning for an empty period and would break `VEvent.getOccurrence(date)`. It is stated in the spec so the exception is explicit rather than accidental. A side effect is that `getOccurrence` now also works for a non-recurring event whose `DTSTART` equals `date` (previously `bounds.intersects` rejected the point query).

### D3. Correct, don't preserve, the two `ComponentGroupTest` expectations

`'rescheduled override with a bare RECURRENCE-ID replaces the master instance'` and `'override moved outside the query period removes the master instance and adds nothing'` assert `[2021-07-14T00:00Z, 2021-07-16T00:00Z]` for a query ending at `2021-07-16T00:00Z`. The 16 July instance is the #603 defect; the expectations become `[2021-07-14T00:00Z]`. The `component-group-recurrence-set` spec scenarios do not mention the 16 July instance, so no spec delta is needed.

### D3. Zero-length occurrences use the same half-open rule as `Period.intersects`

The general overlap test (`bounds.start < occurrence.end`) is `0 < 0` for an occurrence with no extent whose start equals `bounds.start`, which would wrongly exclude it. PR #934 (issue #82) changed `Period.intersects` so a zero-length period intersects when its instant lies in `[start, end)`; `withinBounds` now applies the same rule before the general test, so `build()` stays consistent with `Period.intersects` even though it no longer calls it. A point query against a zero-length occurrence at the same instant is already handled by D2.

## Risks / Trade-offs

- [Consumers depending on the inclusive end for `calculateRecurrenceSet`] → Release-notes entry under "Changed"; the fix is RFC-neutral but alters output. Workaround for anyone needing the old behaviour is to extend the query period by one unit.
- [`RDATE` dates equal to `period.getEnd()` are now excluded too; `RDATE` dates starting before the window but overlapping it are now included] → Intentional, for consistency with `RRULE` instances and the non-recurring instance; the former is covered by a scenario.
- [Mixed temporal types between the query period and the component] → `TemporalComparator` is the sanctioned comparator (see `openspec/specs/temporal-comparison`); no new semantics introduced.
- [RevAPI] → private method only; nothing to report.

## Migration Plan

Single commit on a branch off `develop`; rollback is a revert.

## Open Questions

- None blocking. Whether `RecurDateSpliterator` should eventually honour its own "exclusive of periodEnd" comment is a separate discussion.
