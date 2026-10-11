## 1. Regression tests first (red)

- [x] 1.1 Add `RecurrenceSetBoundsSpec` covering: daily RRULE at period end excluded; RDATE date at period end excluded; date-valued daily event queried for one day returns one instance; instance at period start included; instance ending exactly at period start excluded; instance overlapping period start included; `getOccurrence` point query works; point query between instances is empty. Confirm the end-boundary cases fail on `develop`.
- [x] 1.2 Correct `ComponentGroupTest` (and the `ComponentSpec` RDATE row whose period ended exactly on the last RDATE) `'rescheduled override with a bare RECURRENCE-ID replaces the master instance'` and `'override moved outside the query period removes the master instance and adds nothing'` to expect only the `2021-07-14T00:00Z` master instance for the `14–16 July` query.

## 2. Fix `RecurrenceSet.Builder.build()`

- [x] 2.1 Add a private `withinBounds(Period bounds, Period occurrence)` predicate: overlap with the half-open window, with the zero-length point-query exception (D1, D2), using `TemporalComparator.INSTANCE`.
- [x] 2.2 Apply it to `RRULE` instances, `RDATE` dates (replacing `bounds::includes`), `RDATE;VALUE=PERIOD` values and the initial instance (replacing `bounds.intersects`).
- [x] 2.3 Document the bound semantics in the `RecurrenceSet.Builder.period` / `build` javadoc and in `Component.calculateRecurrenceSet` javadoc.

## 3. Verify

- [x] 3.1 `./gradlew test --tests 'net.fortuna.ical4j.model.RecurrenceSetBoundsSpec' --tests 'net.fortuna.ical4j.model.ComponentGroupTest' --tests 'net.fortuna.ical4j.model.component.VEventTest'` green.
- [x] 3.2 `./gradlew check` green (full suite, RevAPI, checkstyle).
- [x] 3.3 `openspec validate recurrence-set-period-end-exclusive --strict`.

## 4. Land

- [x] 4.1 PR against `develop` referencing #603 with a "Behaviour change" release note. **PR #936, merged 2026-10-09.**
