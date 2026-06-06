## ADDED Requirements

### Requirement: An unresolvable TZID SHALL fall back to a floating value under relaxed validation

When `DateProperty.setValue(String)` has stored a value carrying a `TZID` parameter that resolves to no known zone (neither a VTIMEZONE in the enclosing calendar's registry nor a globally available `java.time.ZoneId`), and `CompatibilityHints.KEY_RELAXED_VALIDATION` is enabled, every value-access method SHALL interpret the value as a floating `java.time.LocalDateTime`, ignoring the TZID, and SHALL NOT propagate an exception derived from the unresolvable zone.

This applies uniformly to `getTemporal()`, `getDate()`, `getValue()`, and `toString()` — they SHALL agree.

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

### Requirement: An unresolvable TZID SHALL propagate a DateTimeException under strict validation

When the stored value carries a `TZID` that resolves to no known zone and `CompatibilityHints.KEY_RELAXED_VALIDATION` is NOT enabled (the default), value-access methods SHALL propagate a `java.time.DateTimeException`. The strict outcome SHALL be consistent across `getTemporal()`, `getDate()`, `getValue()`, and `toString()` — none of them SHALL silently return a fallback value.

#### Scenario: getValue() propagates a DateTimeException by default

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is disabled (default)
- **AND** a `DtStart` parsed from `DTSTART;TZID=Unknown:20260605T120000`
- **WHEN** `getValue()` is invoked
- **THEN** a `java.time.DateTimeException` is thrown

#### Scenario: getDate() propagates a DateTimeException by default

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is disabled (default)
- **AND** a `DtStart` parsed from `DTSTART;TZID=Unknown:20260605T120000`
- **WHEN** `getDate()` is invoked
- **THEN** a `java.time.DateTimeException` is thrown

### Requirement: A resolvable TZID SHALL be unaffected by the unresolvable-TZID fallback

The relaxed fallback SHALL apply only when the TZID resolves to no known zone. A `TZID` that resolves to a known zone — whether via a VTIMEZONE in the registry or a globally available `ZoneId` — SHALL continue to produce a zoned value, in both relaxed and strict modes, exactly as before this change.

#### Scenario: A known TZID produces a zoned value regardless of the relaxed-validation hint

- **GIVEN** a `DtStart` parsed from `DTSTART;TZID=Australia/Melbourne:20260605T120000` (with a matching VTIMEZONE in the calendar)
- **WHEN** `getDate()` is invoked, with `CompatibilityHints.KEY_RELAXED_VALIDATION` either enabled or disabled
- **THEN** the returned `Temporal` is a `ZonedDateTime` that the registry maps back to TZID `Australia/Melbourne`
- **AND** `getValue()` returns `"20260605T120000"` (the local wall-clock form)
- **AND** no exception is thrown

### Requirement: Validation SHALL report an unresolvable TZID as an error

Tolerating an unresolvable TZID for *value access* SHALL NOT make the calendar *valid*. `DateProperty.validate()` SHALL add an ERROR-severity `ValidationEntry` when the property carries a `TZID` parameter (and a timezone applies, i.e. not `VALUE=DATE` and not UTC) that resolves to no known zone. This error SHALL be reported regardless of the `KEY_RELAXED_VALIDATION` hint — value access is leniently tolerant, but validation still flags the structural defect.

#### Scenario: validate() flags an unresolvable TZID under relaxed validation

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is enabled
- **AND** a calendar whose event has `DTSTART;TZID=Unknown:20260605T120000` (no matching VTIMEZONE, not a known global zone)
- **WHEN** the calendar is validated
- **THEN** the `ValidationResult` reports errors (`hasErrors()` is true)
- **AND** the value remains readable: `getValue()` returns `"20260605T120000"` without throwing

#### Scenario: validate() does not flag a resolvable TZID

- **GIVEN** `CompatibilityHints.KEY_RELAXED_VALIDATION` is enabled
- **AND** a calendar whose event has `DTSTART;TZID=Australia/Melbourne:20260605T120000` with a matching VTIMEZONE
- **WHEN** the calendar is validated
- **THEN** the `ValidationResult` reports no errors attributable to the TZID
