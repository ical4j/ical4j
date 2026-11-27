## 1. Regression tests first (red)

- [x] 1.1 Add `"verify parsing trailing whitespace"` to `CalendarParserImplSpec` with rows for `END:VCALENDAR   `, `END:VCALENDAR   \r\n`, `END:VCALENDAR\r\n   \r\n`, `END:VCALENDAR\r\n\t\r\n\r\n`, and `BEGIN:VEVENT  `/`END:VEVENT  `; assert one `VEVENT` named exactly `VEVENT`. Confirm the space rows fail on current `develop`.
- [x] 1.2 Add `ExperimentalPropertyParameterSpec`: the #617 calendar parses with no hints and retains `RANGE=ALL` on `X-LOTUS-RECURID`; `RECURRENCE-ID;RANGE=ALL` still fails strictly; with the Notes hint a `Range` is produced. Confirm the first case fails on current `develop`.
- [x] 1.3 Add `AbstractCalendarValidatorFactoryTest`: an isolating class loader re-defines the factory classes and hides the service resource; assert `getInstance()` returns a `DefaultCalendarValidatorFactory`. Confirm it fails with `ExceptionInInitializerError` on current `develop`.
- [x] 1.4 Add `TimeZoneLoaderLocaleSpec`: `transitionRuleText(3, -1, SUNDAY)` is `FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU` under `fa-IR`/`ar-EG-u-nu-arab`/`th-TH-u-nu-thai`; generated `Europe/London` observances serialise as ASCII.

## 2. Fixes

- [x] 2.1 `CalendarParserImpl`: `stripTrailing()` the name token after `BEGIN:` (`ComponentParser.parse`) and the word token compared in `assertToken(String, boolean, boolean)` (D1).
- [x] 2.2 `PropertyBuilder.hasExperimentalName()`; `DefaultContentHandler.parameter()` catches `IllegalArgumentException` from `ParameterBuilder.build()` and substitutes `new XParameter(name, value)` when the property name is experimental, else rethrows (D2).
- [x] 2.3 `AbstractCalendarValidatorFactory`: `hasNext()` guard with `new DefaultCalendarValidatorFactory()` fallback (D3).
- [x] 2.4 `TimeZoneLoader`: extract `transitionRuleText(int, int, DayOfWeek)` formatted with `Locale.US` and use it from `addTransitionRules` (D4). Grep `src/main` for any other `String.format` with `%d` and no locale.

## 3. Verify

- [x] 3.1 Focused tests green: `CalendarParserImplSpec`, `ExperimentalPropertyParameterSpec`, `AbstractCalendarValidatorFactoryTest`, `TimeZoneLoaderLocaleSpec`, plus `DefaultContentHandlerTest` and `RangeTest`.
- [x] 3.2 `./gradlew check` passes (full suite, RevAPI, checkstyle, NullAway). Record any RevAPI justification in `.palantir/revapi.yml`.
- [x] 3.3 `openspec validate parser-loader-robustness --strict`.

## 4. Land

- [x] 4.1 Commit and open a PR against `develop` with "Fixes #175, #617, #125, #458" and a note that the ServiceLoader aspect of #29/#115 is covered; include release-note wording. **PR #935.**
