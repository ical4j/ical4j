## 1. Regression tests first (red)

- [x] 1.1 Add `DegenerateVTimeZoneTest` (Spock) with the #531 input: empty `VTIMEZONE` `TZID:UTC` + `DTSTART;TZID=UTC`, strict mode; assert the calendar builds, `DTSTART` resolves to UTC via the global zone id, the definition is retained, no zone rules exist, and the output round-trips. Confirm it fails on current `develop`.
- [x] 1.2 Add the #847/#750 natuurhuisje input (observances without `DTSTART`) under `KEY_RELAXED_PARSING` and under `KEY_RELAXED_VALIDATION`; assert the calendar builds, the UTC event instant is unchanged, the definition is retained with both observances, and no zone rules exist.
- [x] 1.3 Add the strict-mode case for the same input: `ParserException` whose message contains `UTC` and `DTSTART`.
- [x] 1.4 Add a direct `register` case for an observance-less definition.
- [x] 1.5 Add a well-formed `Europe/Paris` control asserting zone rules are still built and the offset is `+02:00`.
- [x] 1.6 Clear both hints in `cleanup()` so they cannot leak into other specs.

## 2. Fix `TimeZoneRegistryImpl.register`

- [x] 2.1 After storing the definition and the fallback-mode return, skip zone-rules building when `getObservances()` is empty: log WARN with the `TZID` and return (D2).
- [x] 2.2 Wrap `ZoneRulesBuilder.build()` in a `try`/`catch (RuntimeException)`: under `KEY_RELAXED_PARSING` or `KEY_RELAXED_VALIDATION` log WARN with the `TZID` and cause message and return; otherwise throw `IllegalArgumentException("Unable to build zone rules for VTIMEZONE [<id>]: <cause>")` with the cause attached (D3, D4).
- [x] 2.3 Document the degenerate-definition behaviour in the `TimeZoneRegistryImpl` class javadoc, pointing to `VTimeZone.validate()` for RFC cardinality reporting.

## 3. Verify

- [x] 3.1 `./gradlew test --tests 'net.fortuna.ical4j.model.DegenerateVTimeZoneTest'` — all green.
- [x] 3.2 `./gradlew check` passes (full suite, RevAPI, checkstyle).
- [x] 3.3 `openspec validate degenerate-vtimezone-definitions --strict`.

## 4. Land

- [x] 4.1 Open a PR against `develop` referencing #531, #847 and #750, with the before/after behaviour table and the strict/relaxed decision in the description.
- [x] 4.2 After merge, comment on the three issues with the `develop` snapshot coordinates; close on confirmation or at the next release. **PR #933 merged 2026-10-09; #531, #847 and #750 closed automatically via `Fixes #`.**
