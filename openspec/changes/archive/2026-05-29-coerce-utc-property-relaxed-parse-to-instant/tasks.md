## 1. Baseline

- [x] 1.1 Capture pre-change test counts: `./gradlew test` → record total/skipped/failures
- [x] 1.2 Write a quick reproduction in a scratch test (not committed) to confirm the bug:
    - `CompatibilityHints.setHintEnabled(KEY_RELAXED_PARSING, true)` then `new DtStamp("20240601").getValue()` throws `UnsupportedTemporalTypeException`
    - `new DtStamp("20240601T120000").getValue()` returns `"20240601T120000Z"` (silent wrong data — floating treated as UTC)
    - `new DtStamp("20240601T120000+0500").getValue()` returns `"20240601T120000Z"` (silent wrong data — offset not converted)
    - Discard the scratch test after confirming.

## 2. Add the coercion helper (D3)

- [x] 2.1 In `src/main/java/net/fortuna/ical4j/model/property/DateProperty.java`, add a private static `coerceToInstant(TemporalAdapter<?> adapter) → TemporalAdapter<Instant>` helper per design.md D3 (D2 coercion table):
    - `Instant` → unchanged
    - `OffsetDateTime` → `toInstant()`
    - `LocalDateTime` → `atOffset(ZoneOffset.UTC).toInstant()`
    - `LocalDate` → `atStartOfDay(ZoneOffset.UTC).toInstant()`
    - Any other instant-bearing Temporal → `Instant.from(t)` (defensive last-resort branch)
- [x] 2.2 Verify imports: `java.time.OffsetDateTime`, `java.time.LocalDate` may need to be added (`Instant`, `LocalDateTime`, `ZoneId`, `ZoneOffset` are likely already imported).

## 3. Wire into the catch block (D3 + D4)

- [x] 3.1 In `DateProperty.setValue(String)`'s catch block, extract the parse into a `TemporalAdapter<?> parsed = ...` local variable (covering both the tzId-present and tzId-absent sub-paths).
- [x] 3.2 After the parse, add `if (this instanceof UtcProperty) parsed = coerceToInstant(parsed);`
- [x] 3.3 Assign with the existing unchecked cast: `this.date = (TemporalAdapter<T>) parsed;`
- [x] 3.4 Confirm strict-mode path (no relaxed parsing) is unchanged — `throw dtpe;` branch untouched.
- [x] 3.5 Run `./gradlew compileJava` — verify the file compiles.

## 4. Test class (D6)

- [x] 4.1 Create `src/test/java/net/fortuna/ical4j/model/property/UtcPropertyRelaxedParseTest.java` (JUnit 5).
- [x] 4.2 Use `@BeforeEach` / `@AfterEach` to set/clear `CompatibilityHints.KEY_RELAXED_PARSING` so cases don't leak global state across tests.
- [x] 4.3 Add point cases for DTSTAMP:
    - `"20240601"` → `getValue()` returns `"20240601T000000Z"` (no exception)
    - `"20240601T120000"` → `getValue()` returns `"20240601T120000Z"` (floating treated as UTC)
    - `"20240601T120000+0500"` → `getValue()` returns `"20240601T070000Z"` (offset converted to UTC)
    - `"20240601T120000Z"` → `getValue()` returns `"20240601T120000Z"` (unchanged)
- [x] 4.4 Add equivalent cases for CREATED to confirm parity across subclasses.
- [x] 4.5 Add a parameterised test (`@ParameterizedTest` + `@MethodSource`) over the seven UtcProperty subclass constructors (`DtStamp::new`, `Created::new`, `LastModified::new`, `Completed::new`, `Acknowledged::new`, `TzUntil::new`, `Trigger::new`) with input `"20240601"`. For each, assert `getValue()` ends with `"T000000Z"`. (For Trigger, the constructor that accepts a date-time string and uses the DATE-TIME value type.)
- [x] 4.6 Add strict-mode assertions (relaxed OFF): the same three inputs (`"20240601"`, `"20240601T120000"`, `"20240601T120000+0500"`) throw `DateTimeParseException` on DTSTAMP construction.
- [x] 4.7 Run `./gradlew test --tests "net.fortuna.ical4j.model.property.UtcPropertyRelaxedParseTest"` — verify pass.

## 5. Full test sweep

- [x] 5.1 Run `./gradlew clean test` — every test green, no regressions
- [x] 5.2 Confirm coverage gate: `./gradlew jacocoTestReport jacocoTestCoverageVerification` — coverage ≥ 0.7
- [x] 5.3 Spot-check downstream callers: search for callers of `DateProperty.setValue` to confirm no side-effect of the new coerce path leaks beyond UtcProperty subclasses.

## 6. Commit

- [x] 6.1 Single commit: `fix: coerce non-Instant relaxed-parse results to Instant for UtcProperty` (commit `a23f71fc6`)
    - Subject line ≤ 70 chars.
    - Body: reference defect inventory D-1..D-4, link to `openspec/changes/coerce-utc-property-relaxed-parse-to-instant/`.
    - `Co-Authored-By:` trailer.
- [x] 6.2 Push to `develop`. (`6c8299166..a23f71fc6`)
- [x] 6.3 Archive the change folder via the openspec archive workflow.

## 7. Validation gates (at every commit boundary)

- [x] 7.1 `./gradlew clean test` passes
- [x] 7.2 No new compiler warnings (other than the existing unchecked cast which is unchanged)
- [x] 7.3 JaCoCo ≥ 0.7
- [x] 7.4 No new `.groovy` files created (per `test-framework` spec)
