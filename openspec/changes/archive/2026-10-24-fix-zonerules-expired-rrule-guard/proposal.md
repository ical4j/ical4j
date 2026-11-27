## Why

`ZoneRulesBuilder.buildTransitionRules` has a guard, added in #876, that stops an expired observance `RRULE` from being projected forever as a yearly `ZoneOffsetTransitionRule`. It only applies when *every* latest observance has an `RRULE` with a past `UNTIL`, and it counts an observance with no `RRULE` as "not expired" (issue #903).

Many zones that abolished DST have exactly that shape: an expired DAYLIGHT `RRULE` beside a STANDARD observance with no `RRULE`, only an `RDATE` or a single `DTSTART`. The guard never applies, so the expired rule survives with nothing to balance it, and the zone is an hour ahead for all or part of every year. `Europe/Moscow` has been at +04:00 instead of +03:00 since 2014. `America/Costa_Rica` enters DST every January and never leaves. Because `DateProperty` re-resolves zoned values through these rules, the error also reaches output: a `DTSTART` in `Europe/Moscow` is written an hour late.

Comparing every bundled zone's offsets with the JDK's tzdb on the 1st and 15th of each month from 2000 to 2035, 69 of the 339 bundled zones disagree on `develop`. With this change, 5 do. No zone gets worse.

The code comment kept the behaviour on purpose, claiming `Australia/Darwin` and `America/Sao_Paulo` rely on the resulting single-sided rule. Both are wrong on `develop` today (Darwin is at +10:30 instead of +09:30), and both are corrected by this change.

#903 was closed by mistake when #904 merged: #904's description said "This does not fix #903", and GitHub's closing-keyword parser matched "fix #903".

## What Changes

- The expired-rule guard considers only observances that would produce a transition rule: an `RRULE` with `BYMONTH`, and a real change of offset. An observance with no `RRULE`, an `RRULE` without `BYMONTH`, or a no-effect offset change has nothing to expire and no longer stops the guard from applying.
- The guard and the rule-building loop share one predicate, `producesTransitionRule`, so they can't drift apart again. The predicate reads offsets without throwing, so the guard raises no errors the previous code didn't.
- The out-of-date comment about Darwin and Sao_Paulo is replaced.

Out of scope:
- `buildStandardOffsetTransitions` derives the standard-offset history from each STANDARD observance's `DTSTART` only, ignoring `RDATE`s. For `Europe/Moscow` and `Europe/Istanbul`, whose later standard-offset changes are stored as `RDATE`s on older observances, `ZoneRules.getStandardOffset` and `isDaylightSavings` are therefore wrong. Wall-clock offsets (`getOffset`) are correct with this change, and nothing in ical4j calls those standard-offset APIs. It's a separate defect.
- `Africa/Cairo`, `Africa/Casablanca`, `Africa/El_Aaiun`, `Asia/Gaza` and `Asia/Hebron` still differ from the JDK from late 2026. Their DST depends on Ramadan or irregular dates; the differences come from the bundled tzdata (2025a) and finite date lists, not from this guard.

## Capabilities

### New Capabilities
<!-- None. -->

### Modified Capabilities
- `vtimezone-zonerules`: adds a requirement for when expired observance `RRULE`s stop producing future transition rules.

## Impact

- **Code:** `net.fortuna.ical4j.model.ZoneRulesBuilder` (`buildTransitionRules` plus a new private `producesTransitionRule`).
- **Behaviour change:** zone rules built from bundled or parsed VTIMEZONEs for 64 zones that abolished DST now give the correct offset after the zone's last real transition. Calendars written with `TZID`s in those zones produce local times one hour earlier than before, which is the correct value.
- **APIs and dependencies:** none.
- **Tests:** `ZoneRulesBuilderTest` asserts fixed offsets for six affected zones in January and July; `DtStartTestSpec` asserts the `DTSTART` output from the issue.
