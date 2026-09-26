## 1. Implementation (PR #904)

- [x] 1.1 Map non-zero `BYDAY` ordinals to day-of-month indicators in `ZoneRulesBuilder.buildTransitionRules` via a private `dayOfMonthIndicator(int)`. **Merged in #904 (`749dcaf30`).**
- [x] 1.2 Cap positive ordinals at `28`, so `+5` no longer maps to `29` and throws for February in non-leap years. **Done in #904 (commit `47329b9fb`).**
- [x] 1.3 Document in the `dayOfMonthIndicator` javadoc that `±5` are clamped approximations. **#904 added a paragraph claiming the anchors "pick the last or first matching weekday that still exists in every month"; corrected in this PR. A JDK sweep over 1900–2100 shows both clamps are exact whenever a fifth weekday exists (5,878/5,878) and otherwise land on the fourth weekday or in the adjacent month.**

## 2. Tests

- [x] 2.1 `ZoneRulesBuilderTest`: indicator rows for `1SU..4SU` and `-1SU..-4SU`. **In #904.**
- [x] 2.2 US/Canada March `2SU` transitions match tzdb, including a year where March 1 is not a Sunday. **In #904: New York 2025/2026/2027, Chicago 2025/2027, Los Angeles 2028.**
- [x] 2.3 Add `BYMONTH=2;BYDAY=5SU` and `-5SU`, and assert `ZoneRules` build and yield transitions for a non-leap year without throwing. **`5SU` done in #904 (2026 → 1 March, 2021 → 28 Feb). `-5SU` added in #909: 2027 → 31 Jan (previous month), 2026 → 1 Feb (fourth-from-last), 2032 → 1 Feb (exact); also `5SU` 2032 → 29 Feb (exact). All three `-5SU` rows fail with the lower clamp loosened to -29.**
- [x] 2.4 Add a positive-ordinal February case, e.g. `BYMONTH=2;BYDAY=2SU`. **In #904 (`2SU` 2024/2025, `4SU` 2026).**
- [x] 2.5 Confirm the plain-`BYDAY` + `BYMONTHDAY` fallback case is still covered. **`BYDAY without an ordinal falls back to BYMONTHDAY or DTSTART`.**

## 3. Verify

- [x] 3.1 CI runs on #904 (needs maintainer approval for a first-time contributor) and passes. **Runs approved; build/CodeQL green; merged 2026-09-26.**
- [x] 3.2 `./gradlew test --tests '*ZoneRules*' --tests '*VTimeZone*' --tests '*TimeZone*'` passes. **Passes on develop + this PR's javadoc fix (2026-09-26).**
- [ ] 3.3 `openspec validate fix-zonerules-byday-ordinals --strict`.
