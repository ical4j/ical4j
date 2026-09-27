## 1. Pin down current behaviour

- [x] 1.1 In `UnresolvableTzIdTest`, add failing tests for the new behaviour before changing code:
  - strict `DtStart.getValue()` returns `"20260605T120000"`;
  - `ExDate.getValue()` returns the joined raw text in both modes;
  - relaxed `ExDate.getDates()` returns `LocalDateTime`s;
  - strict `ExDate.getDates()` throws `DateTimeException`;
  - strict `calendar.toString()` round-trips `DTSTART`/`EXDATE` with `TZID=Unknown`.

  Confirm they fail on `develop` for the reasons the proposal gives. **Done: 5 fail as expected; strict `getDates()` already throws.**
- [x] 1.2 Change the existing assertion `assertThrows(DateTimeException.class, dtStart::getValue)` (`UnresolvableTzIdTest.java:140`) to the new contract: `getValue()` returns the raw text. It encodes the strict-textual rule this change deliberately removes. Keep the `getDate()` strict assertion.

## 2. Value-text accessors

- [x] 2.1 Add a public `TemporalAdapter.getValueString()` that returns the stored value text without resolving a zone, with javadoc: the parsed text, or the formatted temporal for adapters built from one.
- [x] 2.2 Add `DateList.toValueString()`: each adapter's `getValueString()` joined with `,`, or `""` for an empty list.

## 3. Textual access (B)

- [x] 3.1 `DateProperty.getValue()`: on `DateTimeException` from `toZoneId(...)`, return `date.getValueString()` in every mode. Remove the relaxed-only branch and keep the `try` scoped to the zone resolution (D5). **Also guards `shouldApplyTimezone()`, which resolves the TZID indirectly via `isUtc()` → `getTemporal()`; see design D5.**
- [x] 3.2 `DateListProperty.getValue()`: resolve the zone in a `try`; on `DateTimeException`, return `dates.toValueString()` in every mode.

## 4. Typed access for date lists (A)

- [x] 4.1 `DateListProperty.getDates()`: resolve the zone once in a `try`. On `DateTimeException`, return `dates.getDates()` (floating) under `KEY_RELAXED_VALIDATION`, otherwise rethrow.

## 5. Validation

- [x] 5.1 Check what `ExDate.validate()` / `RDate.validate()` do today for `TZID=Unknown` in both modes: do they throw, pass, or report? **They passed silently (no error, no exception) in both modes, for plain and `VALUE=PERIOD` RDATE alike.**
- [x] 5.2 Add an ERROR `ValidationEntry` for an unresolvable TZID to `DateListProperty` validation (shared by `ExDate`/`RDate`), mirroring `DateProperty.validate()`, and ensure validation doesn't throw on it. Test both modes. **Package-private `DateListProperty.validateTzId(...)`, called from `ExDate`/`RDate`. It skips `VALUE=DATE` and all-UTC values, detecting UTC from the stored text so it never resolves the values.**

## 6. Edge cases

- [x] 6.1 `RDATE;VALUE=PERIOD;TZID=Unknown:...`: check `getValue()`/`toString()`. Apply the same text fallback if it goes through an unguarded resolution, otherwise record it as out of scope in design.md. **Already returns its raw text (periods don't go through the unguarded resolution); now also gets the validation error. Covered by `rDatePeriod_unknownTimeZone_serialisesAndReportsError`.**
- [x] 6.2 Resolvable TZID on a date list (`EXDATE;TZID=Europe/Paris`) is unchanged in both modes: `getValue()` is local text, `getDates()` zoned.

## 7. Verify

- [x] 7.1 `./gradlew check` passes (tests + revapi). The new accessors should be non-breaking; record any RevAPI finding. **Passed; RevAPI flagged nothing.**
- [x] 7.2 `openspec validate unresolvable-tzid-value-access --strict`.
- [ ] 7.3 After merge, comment on #889 (and re-check #452's scenario) and close them, noting the strict-mode behaviour change for `getValue()`/`toString()`.
