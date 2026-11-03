## Context

`ZoneRulesBuilder.build()` passes the "latest" STANDARD and DAYLIGHT observances to `buildTransitionRules`, which turns each observance `RRULE` into a yearly `ZoneOffsetTransitionRule`. The loop only builds a rule for an observance that has an `RRULE` with `BYMONTH` and whose `TZOFFSETFROM` differs from `TZOFFSETTO`.

Before the loop, a guard returns no rules if every observance's `RRULE` has a past `UNTIL`. The guard checked *every* observance, and an observance without a qualifying `RRULE` answered "not expired". So in a zone like `Europe/Moscow`, where the latest STANDARD observance carries the 2014 change as an `RDATE` and the latest DAYLIGHT observance has an `RRULE` that expired in 2010, the guard never applied and the expired DAYLIGHT rule was projected forever.

## Goals / Non-Goals

**Goals:**
- An expired `RRULE` is never projected as a future rule merely because another observance has no rule to expire.
- The guard and the loop agree on which observances produce rules.
- No zone's offsets get worse.

**Non-Goals:**
- Fixing `buildStandardOffsetTransitions`, which ignores `RDATE`s when deriving the standard-offset history (affects `getStandardOffset` and `isDaylightSavings` for `Europe/Moscow` and `Europe/Istanbul`).
- Ramadan-dependent zones whose bundled data diverges from the JDK from late 2026.

## Decisions

### D1. Evaluate the guard over rule-producing observances only

Filter the observances with the same predicate the loop uses, then require every remaining one to have a past `UNTIL`. If none remain, `allMatch` is true and the method returns no rules, which is also what the loop would have produced.

*Alternative, the one-line fix proposed in #903:* return `true` for an observance with no `RRULE` or no `BYMONTH`. It fixes the reported zones and produces identical results across all bundled zones, but it still lets an active `RRULE` with no offset change keep an expired rule alive, and it leaves the guard and loop conditions written out twice.

### D2. One shared predicate that doesn't throw

`producesTransitionRule(Observance)` reads `RRULE`, `TZOFFSETFROM` and `TZOFFSETTO` with `getProperty`, treating a missing property as "no rule". The loop still uses `getRequiredProperty`, so a malformed observance is reported exactly where it was before, and the guard adds no new failure path.

### D3. Test with fixed offsets, not the JDK

Regression tests assert known offsets at fixed instants rather than comparing with `ZoneId.of(...)`. A JDK comparison would break whenever the JDK's tzdata and the bundled tzdata differ, which is common for zones like `Africa/Casablanca`.

## Verification

A harness compared `getOffset` for every bundled zone against the JDK on the 1st and 15th of each month, 2000 to 2035:

| | Zones that disagree |
|---|---|
| `develop` | 69 of 339 |
| this change | 5 of 339 |

No zone that agreed before disagrees after. The five remaining are `Africa/Cairo`, `Africa/Casablanca`, `Africa/El_Aaiun`, `Asia/Gaza` and `Asia/Hebron`, all diverging only from late 2026.

## Risks / Trade-offs

- [Callers relying on the old, wrong offsets for these zones] → Release note under "Fixed"; the new values match tzdb.
- [`isDaylightSavings` for Moscow and Istanbul] → On `develop` it returned `false` only because the offset was wrong in the same direction as the standard offset. With correct offsets it now returns `true`, exposing the separate `buildStandardOffsetTransitions` defect listed in the non-goals.
