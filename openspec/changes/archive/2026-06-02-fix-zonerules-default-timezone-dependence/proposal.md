## Why

`ZoneRulesBuilder` produces **different `ZoneRules` depending on the JVM default time zone**, so the same calendar resolves to different offsets on different machines. `Asia/Tokyo` resolves to +09:00 when the default zone is UTC (e.g. GitHub Actions CI) but +10:00 when it is east of UTC (e.g. `Australia/*`). This makes `ZoneRulesBuilderTest`'s `Asia/Tokyo` expectations pass on CI and fail locally, and — worse — means consumers in non-UTC zones get incorrect offsets.

Root cause: `buildDSTTransitions` computes an observance's recurrence set from a *floating* `LocalDateTime` seed. When `Recur` compares a floating recurrence candidate against an RRULE `UNTIL` expressed in UTC, `TemporalComparator` resolves the floating value using `TimeZones.getDefault()` (the JVM default zone). For `Asia/Tokyo`'s 1948–1951 historical DST, this drops or keeps the 1951 boundary transitions inconsistently: in non-UTC zones a 1951 DAYLIGHT start is kept while the closing 1951 STANDARD is dropped, leaving the zone permanently in DST (+10:00).

## What Changes

- `ZoneRulesBuilder.buildDSTTransitions` seeds `Observance.calculateRecurrenceSet` with **offset-aware** temporals at the observance `TZOFFSETFROM` offset, instead of a floating `LocalDateTime`. This mirrors what `Observance.getLatestOnset` already does, making the `UNTIL` comparison resolve against the observance's own offset rather than the JVM default zone.
- The resulting `ZoneRules` become **deterministic across JVM default time zones**.
- A regression test exercises `ZoneRulesBuilder` under a forced non-UTC default zone so this class of bug is caught by CI in future.

## Capabilities

### New Capabilities
<!-- None. -->

### Modified Capabilities
- `vtimezone-zonerules`: Add the guarantee that derived `ZoneRules` are independent of the JVM default time zone; historical DST transitions bounded by a UTC `UNTIL` are computed using the observance offset.

## Impact

- Code: `net.fortuna.ical4j.model.ZoneRulesBuilder#buildDSTTransitions`.
- Behavior: zones with historical (expired) DST defined via `RRULE ... UNTIL=<UTC>` now resolve consistently regardless of host time zone; `Asia/Tokyo` resolves to +09:00 everywhere.
- Tests: `ZoneRulesBuilderTest` (existing `Asia/Tokyo`/`Asia/Shanghai` rows now pass everywhere) plus a new non-UTC-default-zone regression test.
- Risk: Low and targeted — change is confined to how the recurrence seed is constructed in one method; `getLatestOnset` already uses the offset-aware approach, so this aligns the two recurrence paths.
