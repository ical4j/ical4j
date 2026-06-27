## 1. Implement ZoneRulesBuilder fix

- [x] 1.1 In `ZoneRulesBuilder.buildTransitionRules`, add the no-effect guard `!offsetFrom.getOffset().equals(offsetTo.getOffset())` to the rule-building condition (mirroring `buildDSTTransitions`)
- [x] 1.2 Replace the unconditional `getDayList().get(0)` access with branching: when the day list is non-empty keep existing offset/`MONTHDAY` logic; when empty but `MONTHDAY` present, use `monthDayList.get(0)` with `null` day-of-week; otherwise derive `dayOfMonth` from `DTSTART` with `null` day-of-week
- [x] 1.3 Guard the existing `dayOfMonth == 0` `MONTHDAY` fallback so an empty `monthDayList` falls back to the `DTSTART` day instead of throwing
- [x] 1.4 Confirm `ZoneOffsetTransitionRule.of(...)` is called with the derived `dayOfMonth` and (possibly null) `dayOfWeek`, leaving month/time/offset arguments unchanged

## 2. Tests

- [x] 2.1 Add a regression test that parses the Apple `Asia/Tokyo` calendar (single STANDARD, `TZOFFSETFROM==TZOFFSETTO`, `RRULE:FREQ=YEARLY;BYMONTH=1`) and asserts no exception is thrown
- [x] 2.2 Assert the registered `ZoneRules` is usable and the VEVENT `DTSTART;TZID=Asia/Tokyo` resolves to the expected offset (+09:00)
- [x] 2.3 Add a unit test for `buildTransitionRules` (or via `ZoneRulesBuilder.build`) covering a `BYMONTH`-only observance: assert the transition rule uses the `DTSTART` day-of-month and a null day-of-week
- [x] 2.4 Add a test asserting an observance with equal from/to offsets produces no transition rule
- [x] 2.5 Add/confirm a test for a standard DST observance with `BYDAY` (e.g. `FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU`) to verify unchanged behavior

## 3. Verify

- [x] 3.1 Run the new tests plus the existing `ZoneRulesBuilder` / timezone test suite and confirm all pass
- [x] 3.2 Confirm the original example calendar from the report parses end-to-end (calendar + VEVENT)
