## Why

`ComponentGroup.calculateRecurrenceSet` silently drops `RECURRENCE-ID` overrides that have been rescheduled to a different time, and depending on how the `RECURRENCE-ID` is written it may not apply the override at all. Events disappear from real-world calendars (issue #510, reported in 2021 and reconfirmed against 4.4.0 on 2026-10-03), which is wrong per RFC 5545 §3.8.4.4: an override replaces the master instance it names, wherever its own `DTSTART` lands.

## What Changes

- Overrides are selected for recurrence calculation by `UID` alone. Today the `ComponentGroup(components, uid)` constructor's predicate excludes any component carrying a `RECURRENCE-ID`, and `getRevisions()` applies that predicate, so the override branch of `calculateRecurrenceSet` only ever sees overrides whose `RECURRENCE-ID` happens to carry parameters (`PropertyExistsRule` compares parameter lists, so a bare `RECURRENCE-ID:20210715T000000Z` is excluded while `RECURRENCE-ID;TZID=UTC:…` slips through).
- Each override contributes **its own single occurrence** (derived from its `DTSTART` and `DTEND`/`DUE`/`DURATION`, clipped to the query period) in place of the master instance named by its `RECURRENCE-ID`. The current code instead takes the override's full `calculateRecurrenceSet` and then keeps only periods whose start equals the `RECURRENCE-ID` date (added in `d0e113da5` for #472) — which discards every rescheduled override. Contributing only the override's own occurrence preserves the #472 guarantee that a stray `RRULE` on an override cannot spawn extra instances.
- Master instances are matched to a `RECURRENCE-ID` by absolute instant via `TemporalComparator`, not `Temporal.equals`, so a `ZonedDateTime` master instance and a UTC `RECURRENCE-ID` for the same moment match.
- `ComponentGroupTest."CalculateRecurrenceSetWithException"` currently asserts the buggy outcome (`master.size() - 1` for an override moved to the following day); its expectation is corrected to `master.size()`.
- Behaviour change of a public method's output, but it corrects output that violates RFC 5545; no signature or type changes.

Out of scope: `RANGE=THISANDFUTURE` on `RECURRENCE-ID`; the meaning of the `ComponentGroup(uid)` predicate for `add`/`replace` (left as is).

## Capabilities

### New Capabilities
- `component-group-recurrence-set`: how `ComponentGroup.calculateRecurrenceSet` combines master revisions and `RECURRENCE-ID` overrides into a single recurrence set — override discovery, instance replacement, rescheduled overrides, overrides carrying an `RRULE`, and cross-type `RECURRENCE-ID` matching.

### Modified Capabilities
<!-- none — no existing spec covers ComponentGroup -->

## Impact

- `src/main/java/net/fortuna/ical4j/model/ComponentGroup.java` — `calculateRecurrenceSet` only; the constructor predicates are untouched.
- `src/test/groovy/net/fortuna/ical4j/model/ComponentGroupTest.groovy` — one corrected expectation, new regression cases.
- Consumers that iterate `ComponentGroup.calculateRecurrenceSet` will now receive rescheduled override instances they were previously missing, and will receive override instances for bare `RECURRENCE-ID`s that were previously ignored. `Period.getComponent()` on an override instance now links to the override component rather than the master.
- No dependency or build changes. RevAPI: no API-shape change expected.
- Closes #510.
