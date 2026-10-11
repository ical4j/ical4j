## Why

`Component.calculateRecurrenceSet(period)` returns one occurrence too many when a recurring component has an instance starting exactly at the query period's end: an `RRULE` instance at `period.getEnd()` is included, while a plain (non-recurring) instance or an `RDATE` period at the same instant is excluded. Issue #603 reports this for midnight-starting events queried day-by-day, where every day's query also returns the next day's instance.

## What Changes

- `RecurrenceSet.Builder.build()` treats the query period as half-open `[start, end)` for every source of occurrences: an occurrence from an `RRULE`, an `RDATE` date, an `RDATE;VALUE=PERIOD` value or the component's own `DTSTART` is included only when it overlaps that window. An instance starting exactly at `period.getEnd()`, or ending exactly at `period.getStart()` (yesterday's all-day instance when querying today), is no longer returned. This is the `Period.intersects` semantics that already applied to the non-recurring initial instance and to `RDATE;VALUE=PERIOD` values.
- A zero-length query period (`start == end`) is a point query and includes an occurrence starting at, or in progress at, that instant. This preserves `VEvent.getOccurrence(date)`, which queries `new Period<>(date, date)`.
- `Recur.getDates(seed, periodStart, periodEnd)` is **not** changed: its inclusive period end is relied on throughout `RecurSpec` and by callers, and the fix for #603 belongs where the `Period` abstraction is applied.
- **Behaviour change**: consumers of `Component.calculateRecurrenceSet`, `VEvent.getConsumedTime` and `ComponentGroup.calculateRecurrenceSet` no longer receive an instance that starts exactly at the end of the requested period. Two `ComponentGroupTest` cases and one `ComponentSpec` RDATE row that asserted the extra boundary instance are corrected.

## Capabilities

### New Capabilities
- `recurrence-set-bounds`: how the query period passed to `RecurrenceSet.Builder` (and therefore to `Component.calculateRecurrenceSet`) bounds the returned occurrences — start inclusive, end exclusive, point queries.

### Modified Capabilities
<!-- none — `component-group-recurrence-set` scenarios do not assert instances at the period end -->

## Impact

- `src/main/java/net/fortuna/ical4j/model/RecurrenceSet.java` — `Builder.build()` filtering only; no signature changes.
- `src/test/groovy/net/fortuna/ical4j/model/ComponentGroupTest.groovy` — two expectations corrected (instance at `2021-07-16T00:00Z` no longer returned for a query ending there); `ComponentSpec` RDATE row split into an inclusive and an end-exclusive case.
- New `src/test/groovy/net/fortuna/ical4j/model/RecurrenceSetBoundsSpec.groovy`.
- Consumers iterating day-sized windows will stop seeing the following day's midnight instance. Consumers that relied on the inclusive end should extend their period by one unit.
- No dependency or API-shape changes; RevAPI not expected to report.
- Fixes #603.
