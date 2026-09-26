## Why

`ZoneRulesBuilder.buildTransitionRules` passes an observance `RRULE`'s `BYDAY` ordinal straight through as the `dayOfMonthIndicator` of `java.time.zone.ZoneOffsetTransitionRule` (issue #902). The two are not the same quantity. With a non-null day-of-week, the indicator names a day-of-month *anchor*: a positive value selects the first matching weekday on or after that day, and a negative value selects the last matching weekday on or before that day, counting back from the end of the month. Passing `2` for `BYDAY=2SU` therefore means "the first Sunday on or after the 2nd", which falls on days 2–8. The second Sunday falls on days 8–14, so the two agree only when the 1st of the month is a Sunday (e.g. March 2026). In every other year the transition lands a week early. This affects future transitions for the US and Canadian March rule (`BYMONTH=3;BYDAY=2SU`) and any other `2..4`/`-2..-4` ordinal.

Zones using only `1SU` or `-1SU` (the EU rules and the US November rule) were unaffected, because `1` and `-1` happen to be correct anchors.

The current spec encodes the bug. Its requirement "Existing BYDAY-based transition rules unchanged" demands ordinal handling stay "exactly as before", so the fix in PR #904 cannot satisfy it as written.

## What Changes

- Map a non-zero `BYDAY` ordinal *n* to the indicator that selects exactly the *n*th weekday:
  - *n* > 0 → `(n-1)*7 + 1` (1, 8, 15, 22)
  - *n* < 0 → `-((|n|-1)*7 + 1)` (−1, −8, −15, −22)
- Ordinal `0` (plain `BYDAY=SU`) keeps its existing `BYMONTHDAY` / `DTSTART`-day fallback.
- Clamp ordinals of magnitude 5 so that building never throws. `+5` is capped at `28`: an uncapped value of `29` makes `ZoneOffsetTransitionRule.createTransition` throw `DateTimeException` for February in non-leap years. `−5` is floored at `−28`, which in a 28-day February anchors on the 1st and can select a weekday in the previous month (e.g. January 31, 2027). Both are approximations, since "5th weekday" cannot be expressed as a single anchor.
- Replace the spec requirement "Existing BYDAY-based transition rules unchanged" with one that states this mapping, keeps the `±1` backward-compatibility guarantee explicitly, and adds scenarios checked against tzdb.

## Capabilities

### New Capabilities
<!-- None. -->

### Modified Capabilities
- `vtimezone-zonerules`: the BYDAY requirement is renamed and rewritten from "unchanged" to an explicit ordinal-to-indicator contract.

## Impact

- **Code:** `net.fortuna.ical4j.model.ZoneRulesBuilder` (`buildTransitionRules` plus a new private `dayOfMonthIndicator`). Implemented by PR #904; the `+5` cap is requested in review of that PR.
- **Behaviour change:** future transitions for VTIMEZONEs whose rules use `BYDAY` ordinals `2..4` or `-2..-4` move to the correct week. This is a correction, but anyone who compensated for the old output will see different results.
- **APIs and dependencies:** none.
- **Risk:** low. The mapping was checked against the JDK's own `createTransition` for every month and weekday from 1900 to 2100, with zero mismatches for `±1..4`. The full test suite passes on the PR branch.
