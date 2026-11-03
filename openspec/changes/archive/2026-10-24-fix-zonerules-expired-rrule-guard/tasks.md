## 1. Regression tests first (red)

- [x] 1.1 In `ZoneRulesBuilderTest`, add a data-driven case asserting fixed January and July 2026 offsets for `Europe/Moscow`, `America/Costa_Rica`, `America/Santo_Domingo`, `Australia/Darwin`, `America/Sao_Paulo` and `Europe/Istanbul`. Confirm every zone has at least one failing row on `develop` (9 of 12 rows fail).
- [x] 1.2 In `DtStartTestSpec`, add `DTSTART` output rows for `Europe/Moscow` (2026-10-25) and `America/Costa_Rica` (2026-07-01). Confirm both fail on `develop`.

## 2. Fix the expired-rule guard

- [x] 2.1 Add private `producesTransitionRule(Observance)`: `RRULE` present with `BYMONTH`, and `TZOFFSETFROM` ≠ `TZOFFSETTO`, reading properties without throwing (D2).
- [x] 2.2 Evaluate the guard over observances filtered by that predicate (D1).
- [x] 2.3 Use the same predicate in the rule-building loop.
- [x] 2.4 Replace the out-of-date comment about `Australia/Darwin` and `America/Sao_Paulo`, referencing #903.

## 3. Verify

- [x] 3.1 New tests pass; `ZoneRulesBuilderTest` and `DtStartTestSpec` fully green.
- [x] 3.2 All-zone harness: 69 → 5 zones differing from the JDK, no zone worse; identical to the one-line fix proposed in #903.
- [x] 3.3 `./gradlew check` passes (full suite, RevAPI, checkstyle, NullAway).
- [x] 3.4 `openspec validate fix-zonerules-expired-rrule-guard --strict`.

## 4. Land

- [x] 4.1 Reopen #903 with an explanation of the mistaken auto-close.
- [x] 4.2 PR against `develop` referencing #903, with a "Fixed" release note listing the affected zones. **PR #955.**
- [x] 4.3 Follow-up issue for `buildStandardOffsetTransitions` ignoring `RDATE`s (wrong `getStandardOffset` / `isDaylightSavings` for `Europe/Moscow` and `Europe/Istanbul`). **Filed as #956.**
