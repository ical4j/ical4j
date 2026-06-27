## Context

`ZoneRulesBuilder.build()` derives `java.time.zone.ZoneRules` from a `VTimeZone`. Historical DST changes are produced by `buildDSTTransitions(List<Observance>)`:

```java
LocalDateTime startDate = (LocalDateTime) start.getDate();
LocalDateTime periodEnd = LocalDateTime.now();
...
observance.calculateRecurrenceSet(new Period<>(startDate, periodEnd)).forEach(p ->
    transitions.add(ZoneOffsetTransition.of(LocalDateTime.from(p.getStart()),
        offsetFrom.get().getOffset(), offsetTo.getOffset())));
```

The seed (`startDate`/`periodEnd`) is a **floating `LocalDateTime`**. `Observance.calculateRecurrenceSet` feeds it to `Recur.getDates`, which filters candidates against the RRULE `UNTIL` at `Recur.java:750` / `Recur.java:772` via `TemporalAdapter.isAfter(candidate, getUntil())`. When the candidate is a floating `LocalDateTime` and `UNTIL` is a UTC `Instant` (the `...Z` form mandated for VTIMEZONE), `TemporalComparator` resolves the floating value using `defaultZoneId = TimeZones.getDefault().toZoneId()` (`TemporalComparator.java:24-27`) — the **JVM default zone**.

For `Asia/Tokyo` the bundled definition encodes expired 1948–1951 DST:

```
DAYLIGHT DTSTART:19500507T000000 (offsetFrom +0900) RRULE ... UNTIL=19510505T150000Z
STANDARD DTSTART:19480912T010000 (offsetFrom +1000) RRULE ... UNTIL=19510908T150000Z
```

The final DAYLIGHT candidate `1951-05-06T00:00`:
- default `UTC` → `1951-05-06T00:00Z` > `UNTIL` → excluded.
- default `+10` → `1951-05-05T14:00Z` < `UNTIL` → included.

The closing STANDARD (Sep 1951) is dropped in both cases, so in non-UTC zones the kept 1951 DAYLIGHT start has no matching end and the zone is stuck at +10:00 forever.

`Observance.getLatestOnset` (`Observance.java:199-205`) already avoids this: it seeds `Recur.getDates` with an offset-aware `initialOnset` built at `offsetFrom`, so candidates are `OffsetDateTime` and the `UNTIL` comparison is deterministic. `buildDSTTransitions` is the outlier.

## Goals / Non-Goals

**Goals:**
- Make derived `ZoneRules` deterministic across JVM default time zones.
- Fix `Asia/Tokyo` (and any zone with UTC-`UNTIL`-bounded historical DST) resolving to a wrong offset off-UTC.
- Add a regression test that runs under a non-UTC default zone.

**Non-Goals:**
- No change to `TemporalComparator` / `Recur` `UNTIL` semantics generally; the fix is localized to how the VTIMEZONE recurrence seed is constructed.
- No attempt to faithfully reproduce every historical DST instance (e.g. the 1951 Tokyo season). The contract is determinism and a correct present-day offset, not perfect historical fidelity — the pre-existing UTC behavior already omits 1951.
- No change to future transition rules (`buildTransitionRules`).

## Decisions

**Decision: Seed `calculateRecurrenceSet` with offset-aware temporals at `TZOFFSETFROM`.**

In `buildDSTTransitions`, build the period from `startDate.atOffset(offsetFrom)` and `periodEnd.atOffset(offsetFrom)` so the recurrence candidates are `OffsetDateTime`. The `UNTIL` comparison then resolves against the observance offset, independent of the JVM default zone. The downstream `LocalDateTime.from(p.getStart())` recovers the same wall-clock onset as before (applying then extracting `offsetFrom` is lossless for the local part), so transition local times are unchanged.

Validated against the live `Asia/Tokyo` data: the AU default-zone recurrence count for the DAYLIGHT observance collapses from 2 → 1, matching the UTC result of 1 in every zone — deterministic, balanced transitions, +09:00 present-day.

*Alternative considered — change `Observance.calculateRecurrenceSet` to always apply the observance offset:* more general, but `calculateRecurrenceSet` is a shared generic `Component` method used by `VEVENT`/`VTODO` etc.; broadening it risks unrelated recurrence behavior. The targeted seed change keeps blast radius to timezone derivation, where `offsetFrom` is the correct and available anchor.

*Alternative considered — skip all expired-`UNTIL` observances entirely:* would also yield +09:00 for Tokyo, but discards legitimate historical transitions for other zones and doesn't address the underlying non-determinism. Rejected.

## Risks / Trade-offs

- [Other zones' historical transitions shift slightly] → The seed change only affects boundary instances at the `UNTIL` edge; non-boundary occurrences are unaffected. Existing `ZoneRulesBuilderTest` rows (Melbourne, LA, London, Sao Paulo, Sydney, etc.) guard against regressions and must continue to pass under the new test's forced zone too.
- [Determinism test must force a non-UTC zone] → Set the default zone within the test and restore it in cleanup so other tests are unaffected; assert `Asia/Tokyo` resolves to +09:00 under that zone.

## Migration Plan

Internal bug fix; no data or API migration. Rollback is reverting the one-method change. Consumers in non-UTC zones simply begin getting correct offsets.

## Open Questions

- None blocking.
