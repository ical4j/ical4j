## Why

`CalendarBuilder.build` throws `ParserException` (caused by `IndexOutOfBoundsException`) on a large class of real-world calendars: any embedded `VTIMEZONE` whose observance carries a `BYMONTH`-only `RRULE` with no `BYDAY` or `BYMONTHDAY`. This is exactly the shape Apple Calendar (macOS/iOS) emits for non-DST zones such as `Asia/Tokyo` — a single `STANDARD` observance with `TZOFFSETFROM == TZOFFSETTO` and `RRULE:FREQ=YEARLY;BYMONTH=1`. ical4j currently cannot ingest its own such timezones, so these calendars fail to parse entirely.

## What Changes

- `ZoneRulesBuilder.buildTransitionRules` derives the transition day-of-month from the observance `DTSTART` and uses a `null` day-of-week (a fixed-date transition rule) when the `RRULE` has neither `BYDAY` nor `BYMONTHDAY`, instead of unconditionally indexing `getDayList().get(0)`.
- `buildTransitionRules` skips observances whose `TZOFFSETFROM` equals `TZOFFSETTO` (no-effect transitions), mirroring the existing guard in `buildDSTTransitions`.
- As a result, non-DST and fixed-date `VTIMEZONE` definitions register successfully and the surrounding calendar parses.

## Capabilities

### New Capabilities
- `vtimezone-zonerules`: Deriving a JSR-310 `java.time.zone.ZoneRules` instance from a `VTimeZone` component, including future transition rules built from observance `RRULE`s — distinct from generic property parsing.

### Modified Capabilities
<!-- None: no existing spec covers ZoneRules construction from VTIMEZONE. -->

## Impact

- Code: `net.fortuna.ical4j.model.ZoneRulesBuilder` (`buildTransitionRules`, and `build` which invokes it). Indirectly fixes `TimeZoneRegistryImpl.register` and `CalendarBuilder.build` for affected calendars.
- APIs: No public API signature changes; behavior change is that previously-failing parses now succeed.
- Dependencies: None.
- Risk: Low — the crashing branch produced a transition rule that JSR-310 ignores when from-offset equals to-offset; behavior for valid DST rules with `BYDAY` is unchanged.
