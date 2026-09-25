## 1. Implementation (PR #904)

- [ ] 1.1 Map non-zero `BYDAY` ordinals to day-of-month indicators in `ZoneRulesBuilder.buildTransitionRules` via a private `dayOfMonthIndicator(int)`. **Done in PR #904.**
- [ ] 1.2 Cap positive ordinals at `28`, so `+5` no longer maps to `29` and throws for February in non-leap years. **Requested in review of #904.**
- [ ] 1.3 Document in the `dayOfMonthIndicator` javadoc that `±5` are clamped approximations.

## 2. Tests

- [ ] 2.1 `ZoneRulesBuilderTest`: indicator rows for `1SU..4SU` and `-1SU..-4SU`. **In #904.**
- [ ] 2.2 US/Canada March `2SU` transitions match tzdb, including a year where March 1 is not a Sunday. **In #904 (New York, Chicago, Los Angeles); confirm the rows cover a non-2026 year.**
- [ ] 2.3 Add `BYMONTH=2;BYDAY=5SU` and `-5SU`, and assert `ZoneRules` build and yield transitions for a non-leap year without throwing.
- [ ] 2.4 Add a positive-ordinal February case, e.g. `BYMONTH=2;BYDAY=2SU`.
- [ ] 2.5 Confirm the plain-`BYDAY` + `BYMONTHDAY` fallback case is still covered.

## 3. Verify

- [ ] 3.1 CI runs on #904 (needs maintainer approval for a first-time contributor) and passes.
- [ ] 3.2 `./gradlew test --tests '*ZoneRules*' --tests '*VTimeZone*' --tests '*TimeZone*'` passes.
- [ ] 3.3 `openspec validate fix-zonerules-byday-ordinals --strict`.
