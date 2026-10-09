## Context

`Recur<T>` generates instances by incrementing the seed by the rule's frequency (`Recur.increment`: `seed.plus(k * interval, unit)`) and expanding each candidate seed through the BY* rules (`getCandidates`). For `T = ZonedDateTime`, java.time's arithmetic (`plus`, `with`) resolves the resulting local date-time with `ZonedDateTime.ofLocal(ldt, zone, preferredOffset)`, where the preferred offset is the offset of the value being adjusted. When the result lands in a fall-back overlap (the local hour that occurs twice) and the preferred offset is one of the two valid offsets, that offset is kept.

Consequences, verified by `RecurDstOverlapSpec` on `develop`:

- `FREQ=WEEKLY;BYDAY=FR,SA,SU,MO`, seed `2000-02-01T01:30` America/Los_Angeles (PST, `-08:00`): the Sunday `2019-11-03T01:30` candidate is produced by `ByDayRule.WeeklyExpansionFilter` from the Tuesday `2019-11-05T01:30-08:00` candidate seed, so it resolves to `-08:00`. With `WKST=MO` the same Sunday is produced from the previous week's Tuesday (`-07:00`) and resolves to `-07:00`. Hence the `WKST` dependence in #716.
- `FREQ=DAILY`, same zone: seeded in February (`-08:00`) the `2019-11-03T01:30` instance resolves to `-08:00`; seeded in July (`-07:00`) it resolves to `-07:00`. The plain increment path has the same defect.

RFC 5545 §3.3.5 (DATE-TIME, form #3): "If the local time described does not occur ... or occurs twice (when the clock is turned back), ... the time value is interpreted using the UTC offset before the transition." For an overlap that is the earlier offset, i.e. `ZonedDateTime.withEarlierOffsetAtOverlap()`. For a gap java.time already shifts forward by the gap length, which is the interpretation 4.x has always applied and `RecurSpec` asserts.

Constraints: `Recur.getDates`/`getNextDate` are public and heavily used; no signature changes. Only `ZonedDateTime` carries an ambiguous offset — `OffsetDateTime` and `Instant` are unambiguous, local types have no offset.

## Goals / Non-Goals

**Goals:**
- Every `ZonedDateTime` instance returned by `getDates`, `getDatesAsStream` and `getNextDate` that falls in a fall-back overlap carries the earlier (pre-transition) offset, independent of seed offset, `WKST` or which BY* rule produced it.
- Gap handling and all non-`ZonedDateTime` behaviour unchanged.

**Non-Goals:**
- Changing how `DTSTART` values themselves are parsed in an overlap (that is `CalendarDateFormat`/`TemporalAdapter` territory and already follows java.time's earlier-offset default when no preferred offset exists).
- Changing `Period`/`RecurrenceSet` arithmetic (`Period(start, duration)` uses `plus`, which keeps the start's offset; a one-hour event starting at `01:30-07:00` correctly ends at `02:30-07:00`, i.e. `01:30-08:00` wall time).

## Decisions

### D1. Normalise once, at the end of `Recur.getCandidates`

`getCandidates(rootSeed, date)` is the single path through which every candidate reaches `RecurDateSpliterator` (and so `getDates`) and `getNextDate`. When `rootSeed` is a `ZonedDateTime`, its result list is mapped through a private `withEarlierOffsetAtOverlap` helper. For other seed types the list is returned as-is, so there is no cost or behaviour change for them.

*Alternative — fix `AbstractDateExpansionRule.withTemporalField` (the issue's diagnosis):* covers the BYDAY/BYMONTHDAY expansions but not the increment path (`FREQ=DAILY` seeded in standard time still yields `-08:00`), nor `ByMonthDayRule`'s own `plus`/`minus`. Rejected as incomplete.

*Alternative — also fix `Recur.increment`:* together with the rule fix this would cover today's code, but every future rule would have to remember the same step. The choke point is simpler and provably complete.

*Alternative — normalise in `RecurDateSpliterator` and `getNextDate` separately:* two sites instead of one, and `getCandidates` is package-visible and already the documented "list of possible dates" boundary.

### D2. Earlier offset, not "offset of the seed"

3.x produced `-07:00` because `java.util.Calendar` resolves an ambiguous wall time to the first occurrence. The RFC rule is the same. Keeping the seed's offset would be wrong in the opposite direction for seeds in daylight time whose instances fall in a standard-time overlap (none exist for a simple overlap, but the RFC rule is unconditional and simpler to state).

### D3. Tests assert offsets, not only instants

`RecurDstOverlapSpec` checks `offset == -07:00` for the overlap instance under `WKST` absent, `SU` and `MO`, for the daily rule seeded in PST and PDT, and that the neighbours (`11-02` at `-07:00`, `11-04` at `-08:00`) and the spring-forward gap (`02:00` → `03:00-07:00`) are unchanged. The existing `RecurSpec` DST cases (seeded at `00:59`, outside the overlap) continue to pass.

## Risks / Trade-offs

- [Consumers comparing instants across the fall-back hour will see the overlap instance one hour earlier than 4.x produced in the affected (seed-dependent) cases] → This is the RFC and 3.x behaviour; called out as a behaviour change in the release notes. Unaffected cases (seed already in daylight time, `WKST=MO` in the issue's example) do not change.
- [Performance: one extra list map per candidate seed for `ZonedDateTime`] → `withEarlierOffsetAtOverlap` is a cheap `ZoneRules.getValidOffsets` lookup; candidate lists are small (≤ days in a period). Guarded by an `instanceof` on the seed so other types pay nothing.
- [RevAPI] → private helper only; nothing to report.

## Migration Plan

Single commit on a branch off `develop`; rollback is a revert.

## Open Questions

- None.
