## MODIFIED Requirements

### Requirement: An unresolvable TZID SHALL fall back to a floating value under relaxed validation

When a `DateProperty` or `DateListProperty` has stored a value carrying a `TZID` parameter that resolves to no known zone (neither a VTIMEZONE in the enclosing calendar's registry nor a globally available `java.time.ZoneId`), and `CompatibilityHints.KEY_RELAXED_VALIDATION` is enabled, **typed access** SHALL interpret each value as a floating `java.time.LocalDateTime`, ignoring the TZID, and SHALL NOT propagate an exception derived from the unresolvable zone. Typed access means `getTemporal()`, `DateProperty.getDate()` and `DateListProperty.getDates()`.

Textual access (`getValue()`, `toString()`) is governed by the requirement "Textual access SHALL return the original value text for an unresolvable TZID". For a parsed value, its result equals the floating representation.

#### Scenario: getValue() returns the floating representation under relaxed validation

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is enabled
- **AND** a calendar is built from an event whose `DTSTART;TZID=Unknown:20260605T120000` references a zone not present in any VTIMEZONE and not a known global zone
- **WHEN** `getValue()` is invoked on the parsed `DtStart`
- **THEN** the result is `"20260605T120000"`
- **AND** no exception is thrown

#### Scenario: getDate() returns a LocalDateTime under relaxed validation

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is enabled
- **AND** a `DtStart` parsed from `DTSTART;TZID=Unknown:20260605T120000`
- **WHEN** `getDate()` is invoked
- **THEN** the returned `Temporal` is a `LocalDateTime`
- **AND** no exception is thrown

#### Scenario: getDates() returns LocalDateTimes under relaxed validation

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is enabled
- **AND** an `ExDate` parsed from `EXDATE;TZID=Unknown:20260606T120000,20260607T120000`
- **WHEN** `getDates()` is invoked
- **THEN** it returns two `LocalDateTime` values, `2026-06-06T12:00` and `2026-06-07T12:00`
- **AND** no exception is thrown

### Requirement: An unresolvable TZID SHALL propagate a DateTimeException under strict validation

When the stored value carries a `TZID` that resolves to no known zone and `CompatibilityHints.KEY_RELAXED_VALIDATION` is NOT enabled (the default), **typed access** SHALL propagate a `java.time.DateTimeException`. Typed access means `getTemporal()`, `DateProperty.getDate()` and `DateListProperty.getDates()`. None of them SHALL silently return a fallback value.

Textual access (`getValue()`, `toString()`) SHALL NOT throw in this case; see "Textual access SHALL return the original value text for an unresolvable TZID".

#### Scenario: getDate() propagates a DateTimeException by default

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is disabled (default)
- **AND** a `DtStart` parsed from `DTSTART;TZID=Unknown:20260605T120000`
- **WHEN** `getDate()` is invoked
- **THEN** a `java.time.DateTimeException` is thrown

#### Scenario: getDates() propagates a DateTimeException by default

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is disabled (default)
- **AND** an `ExDate` parsed from `EXDATE;TZID=Unknown:20260606T120000`
- **WHEN** `getDates()` is invoked
- **THEN** a `java.time.DateTimeException` is thrown

### Requirement: Validation SHALL report an unresolvable TZID as an error

Tolerating an unresolvable TZID for *value access* SHALL NOT make the calendar *valid*. `DateProperty.validate()` and `DateListProperty.validate()` (including `ExDate` and `RDate`) SHALL add an ERROR-severity `ValidationEntry` when the property carries a `TZID` parameter that resolves to no known zone and a timezone applies to its values, i.e. they are not `VALUE=DATE` and not UTC. This error SHALL be reported regardless of the `KEY_RELAXED_VALIDATION` hint: value access is tolerant, but validation still flags the structural defect. Validation itself SHALL NOT throw because of the unresolvable zone.

#### Scenario: validate() flags an unresolvable TZID under relaxed validation

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is enabled
- **AND** a calendar whose event has `DTSTART;TZID=Unknown:20260605T120000` (no matching VTIMEZONE, not a known global zone)
- **WHEN** the calendar is validated
- **THEN** the `ValidationResult` reports errors (`hasErrors()` is true)
- **AND** the value remains readable: `getValue()` returns `"20260605T120000"` without throwing

#### Scenario: validate() flags an unresolvable TZID on a date list

- **GIVEN** an `ExDate` parsed from `EXDATE;TZID=Unknown:20260606T120000`, in either strict or relaxed mode
- **WHEN** `validate()` is invoked on it
- **THEN** it completes without throwing
- **AND** the `ValidationResult` contains an ERROR entry for the unresolvable TZID

#### Scenario: validate() does not flag a resolvable TZID

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is enabled
- **AND** a calendar whose event has `DTSTART;TZID=Australia/Melbourne:20260605T120000` with a matching VTIMEZONE
- **WHEN** the calendar is validated
- **THEN** the `ValidationResult` reports no errors attributable to the TZID

## ADDED Requirements

### Requirement: Textual access SHALL return the original value text for an unresolvable TZID

When a `DateProperty` or `DateListProperty` carries a `TZID` that resolves to no known zone, `getValue()` SHALL return the stored value text without resolving the zone, in every mode, strict (default) and relaxed alike. For a parsed value this is the text as it appeared in the input. For a list it's each value's text, joined with `,`. `toString()` SHALL therefore not throw either, and a calendar containing such properties SHALL serialise.

A `TZID` that resolves to a known zone SHALL continue to produce the local wall-clock form in that zone, as before.

#### Scenario: Strict getValue() returns the raw DTSTART text

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is disabled (default)
- **AND** a `DtStart` parsed from `DTSTART;TZID=Unknown:20260605T120000`
- **WHEN** `getValue()` is invoked
- **THEN** the result is `"20260605T120000"`
- **AND** no exception is thrown

#### Scenario: getValue() returns the raw text of every EXDATE value

- **GIVEN** an `ExDate` parsed from `EXDATE;TZID=Unknown:20260606T120000,20260607T120000`, in either strict or relaxed mode
- **WHEN** `getValue()` is invoked
- **THEN** the result is `"20260606T120000,20260607T120000"`
- **AND** no exception is thrown

#### Scenario: A calendar with unresolvable TZIDs round-trips in strict mode

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is disabled (default)
- **AND** a calendar whose event has `DTSTART;TZID=Unknown:20260605T120000`, `RRULE:FREQ=DAILY;COUNT=3` and `EXDATE;TZID=Unknown:20260606T120000`
- **WHEN** `calendar.toString()` is invoked
- **THEN** no exception is thrown
- **AND** the output contains `DTSTART;TZID=Unknown:20260605T120000` and `EXDATE;TZID=Unknown:20260606T120000`

#### Scenario: A resolvable TZID on a date list is unaffected

- **GIVEN** an `ExDate` parsed from `EXDATE;TZID=Europe/Paris:20260606T120000`, in either mode
- **WHEN** `getValue()` and `getDates()` are invoked
- **THEN** `getValue()` returns `"20260606T120000"`
- **AND** `getDates()` returns a `ZonedDateTime` in a zone that resolves `Europe/Paris`
