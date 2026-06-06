## 1. Baseline

- [x] 1.1 Capture pre-change test counts: `./gradlew test` → baseline green; full suite 2788 tests, 2 failed only AFTER the change (see §6), 0 before.
- [x] 1.2 Confirm the failure reproduces: the strict-mode test (§4.3) is the permanent capture — building `DTSTART;TZID=Unknown:20260605T120000` and calling `getValue()`/`getDate()` throws `ZoneRulesException: Unknown time-zone ID: Unknown` (via `TzId.toZoneId` → `TimeZoneRegistry.getGlobalZoneId`).
- [x] 1.3 Confirm the relaxed path *also* threw before the fix (verified by the dead `catch (DateTimeParseException)` — `ZoneRulesException` is a sibling, not caught).

## 2. Fix the lazy-parse fallback in TemporalAdapter

- [x] 2.1 In `TemporalAdapter.getTemporal()` (the `if (tzId != null)` branch), broaden the relaxed-fallback `catch (DateTimeParseException e)` to `catch (DateTimeException e)` so an unresolvable-zone `ZoneRulesException` is caught.
- [x] 2.2 Fallback body parses with `CalendarDateFormat.DEFAULT_PARSE_FORMAT.parse(valueString)` (floating `LocalDateTime`) under relaxed validation, and rethrows `e` under strict.
- [x] 2.3 Imports: `java.time.DateTimeException` already available via the `java.time.*` wildcard import in `TemporalAdapter`.
- [x] 2.4 Run `./gradlew test --tests UnresolvableTzIdTest` — green.
- [ ] 2.5 Commit: "fix: catch DateTimeException so unresolvable TZID falls back to floating under relaxed validation".

## 3. Guard the eager resolution in DateProperty (getValue + getDate)

- [x] 3.1 In `DateProperty.getValue()`, wrap `return date.toString(tzId.get().toZoneId(timeZoneRegistry));` in a `try`/`catch (DateTimeException e)`.
- [x] 3.2 In the catch: if `CompatibilityHints.isHintEnabled(KEY_RELAXED_VALIDATION)` return `Strings.valueOf(date)`; otherwise `throw e`.
- [x] 3.3 **Discovered third call site:** `DateProperty.getDate()` (line ~160) has the identical unguarded `tzId.get().toZoneId(...)` (it does not delegate to `getValue()`). Apply the same guard; relaxed branch returns `date.getTemporal()` (the floating `LocalDateTime`).
- [x] 3.4 Add `import java.time.DateTimeException;` to `DateProperty`.
- [x] 3.5 Run `./gradlew test --tests UnresolvableTzIdTest` — green.
- [ ] 3.6 Commit: "fix: tolerate unresolvable TZID in DateProperty.getValue()/getDate() under relaxed validation".

## 4. Tests (date-property-parsing capability)

- [x] 4.1 Add `UnresolvableTzIdTest` with `KEY_RELAXED_VALIDATION` cleared via `@AfterEach` (global mutable state).
- [x] 4.2 Relaxed ON: build the reporter's calendar; assert `getValue()` == `"20260605T120000"`, `getDate()` instanceof `LocalDateTime`, no exception.
- [x] 4.3 Relaxed OFF (default): assert `getValue()` and `getDate()` each throw `DateTimeException`.
- [x] 4.4 Regression: `DTSTART;TZID=Australia/Melbourne` **with an embedded VTIMEZONE** (hermetic, no network) in both modes → `getDate()` instanceof `ZonedDateTime`; `builder.getRegistry().getTzId(zone.getId())` == `Australia/Melbourne`; `getValue()` == `"20260605T120000"`.
- [x] 4.5 Run `./gradlew test --tests UnresolvableTzIdTest` — green (6 cases).
- [ ] 4.6 Commit: "test: cover unresolvable-TZID handling across relaxed/strict modes".

## 5. Validation still flags an unresolvable TZID (design decision: option 2)

- [x] 5.1 In `DateProperty.validate()`, after delegating to `DatePropertyValidator`, add an ERROR `ValidationEntry` when `getParameter(TZID)` is present, `shouldApplyTimezone()`, and `tzId.toZoneId(timeZoneRegistry)` throws `DateTimeException`. Reported regardless of the hint.
- [x] 5.2 Add `import net.fortuna.ical4j.validate.ValidationEntry;` to `DateProperty`.
- [x] 5.3 Add tests: relaxed ON, unknown TZID → `calendar.validate().hasErrors()` true (value still readable); known TZID → no errors.
- [x] 5.4 Confirm `CalendarBuilderTest.testBuildInvalid` (incl. `samples/invalid/talios.ics`) passes again — talios stays invalid via the validation error, not a throw.
- [ ] 5.5 Commit: "fix: report unresolvable TZID as a validation error so tolerant value access does not mask structural invalidity".

## 6. Validation gates

- [x] 6.1 `./gradlew test --tests UnresolvableTzIdTest --tests CalendarBuilderTest` — green.
- [x] 6.2 Full `./gradlew test` → 2790 tests, 1 failed. The sole failure is `TimeZoneLoaderTest > initializationError` ("Could not find a valid Docker environment") — a Spock/Testcontainers integration test, environmental and pre-existing, unrelated to this change. No other failures; `talios.ics` passes.
- [x] 6.3 No new compiler warnings (only pre-existing deprecation/unchecked notes).
- [x] 6.4 JaCoCo `jacocoTestCoverageVerification` — BUILD SUCCESSFUL (≥ 0.7 maintained).
- [x] 6.5 Spock/Groovy untouched (`git diff --name-only -- src/test/groovy/` empty).
- [ ] 6.6 Open PR; reference RFC 5545 §3.2.19 (TZID), §3.3.5 (DATE-TIME floating form), and this change.
