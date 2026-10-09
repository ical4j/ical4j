## Context

`TemporalAmountAdapter` wraps either a `java.time.Duration` (exact seconds) or a `java.time.Period` (years, months, days) and renders it as an RFC 5545 `dur-value`:

```
dur-value = (["+"] / "-") "P" (dur-date / dur-time / dur-week)
dur-week  = 1*DIGIT "W"
dur-date  = dur-day [dur-time]
```

A `Period` with years or months has no fixed length, so `toString(Temporal seed)` already anchors it to a seed: `adjustedSeed = seed.plus(period)`. The current `periodToString` then branches on which fields are set and, for years or months, emits `seed.until(adjustedSeed, WEEKS)` — which truncates to whole weeks. For a pure-days period it emits weeks when `days % 7 == 0` and days otherwise, which is correct.

`durationToString` has no comparable defect: it measures whole calendar days and then the exact remainder.

## Goals / Non-Goals

**Goals:**
- A `Period` of any composition renders to a `dur-value` that, applied to the seed, lands on `seed.plus(period)` exactly.
- Whole-week spans keep the `PnW` form (`P7D` → `P1W`, 28 days → `P4W`).
- Negative periods keep the leading `-`.

**Non-Goals:**
- Changing `Duration` rendering or parsing.
- Rendering `dur-date` with a time part for a `Period` (a `Period` has no time component).
- The deprecated `Date`-based helpers (`fromDateRange`, `getTime`); they do not truncate and are out of scope.

## Decisions

### D1. One rule: exact calendar days, weeks only when divisible by seven

`periodToString` computes `days = |seed.until(seed.plus(period), DAYS)|` once and emits `P(days/7)W` when `days % 7 == 0`, else `P(days)D`. The years/months/days branches collapse into this. The seed is already required by the method, so no API change is needed.

*Alternative — emit `PnWnD` (weeks plus days):* not a valid `dur-value`; RFC 5545 forbids mixing `W` with `D`.

*Alternative — keep weeks for years/months and only fix the test:* would preserve a representation that is shorter than the real span by up to six days, which is a data-loss bug, not a style choice.

### D2. Make the existing #419 tests deterministic

The two `@Ignore`d tests call `toString()` with no seed, which uses `LocalDateTime.now()` and so would pass or fail depending on the current month (February in a non-leap year *is* exactly four weeks). They are rewritten with explicit seeds: one month from 2020-06-01 is `P30D`; one year from 2021-04-01 is `P365D` and from 2020-01-01 is `P366D`; one month from 2021-02-01 legitimately remains `P4W`.

## Risks / Trade-offs

- [Output of existing callers changes from `PnW` to `PnD` for non-week spans] → Release-notes entry; the new value is the one that round-trips. Callers that explicitly wanted weeks should pass `Period.ofWeeks`.
- [`seed.until(..., DAYS)` on a `LocalDate` seed with a `Period` of days is unchanged; on a `ZonedDateTime` seed across a DST transition `plus(Period)` keeps wall time, so the day count is calendar-based, as intended for a nominal duration.]
- [RevAPI] → private method only.

## Migration Plan

Single commit on a branch off `develop`; rollback is a revert.

## Open Questions

- None.
