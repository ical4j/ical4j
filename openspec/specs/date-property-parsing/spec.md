# Date property parsing

## Purpose

Defines how `net.fortuna.ical4j.model.property.DateProperty` and its subclasses parse string values into `java.time.Temporal` instances. Covers the strict-format contract per RFC 5545 §3.3.4 (DATE) and §3.3.5 (DATE-TIME), the relaxed-parsing fallback gated by `CompatibilityHints.KEY_RELAXED_PARSING`, and the additional invariants enforced for properties that implement `UtcProperty`.

## Requirements

### Requirement: UtcProperty subclasses SHALL hold an Instant value after setValue(String) succeeds

After `DateProperty.setValue(String)` returns successfully on an instance that implements `UtcProperty` (DTSTAMP, CREATED, LAST-MODIFIED, COMPLETED, ACKNOWLEDGED, TZUNTIL, TRIGGER with DATE-TIME value), the internal `TemporalAdapter` value SHALL hold an `Instant`. This invariant SHALL hold regardless of whether `CompatibilityHints.KEY_RELAXED_PARSING` is enabled.

#### Scenario: DTSTAMP getValue() never throws after a successful setValue() in relaxed mode

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is enabled
- **AND** a `DtStamp` constructed with any string value that `setValue(String)` accepts without exception
- **WHEN** `getValue()` is invoked
- **THEN** the call returns a non-null `String` and does not propagate `UnsupportedTemporalTypeException` or any other runtime exception derived from the parsed value's temporal type

#### Scenario: Created getValue() returns a UTC date-time string after a date-only input in relaxed mode

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is enabled
- **AND** `created = new Created("20240601")`
- **WHEN** `created.getValue()` is invoked
- **THEN** the result is `"20240601T000000Z"`

### Requirement: UtcProperty relaxed parsing SHALL coerce non-Instant temporals to a UTC Instant per a defined table

When `DateProperty.setValue(String)` produces a temporal that is not already an `Instant` — whether from the strict-format parse or from the relaxed-parsing fallback — the value SHALL be coerced to an `Instant` for any property implementing `UtcProperty`, according to this table:

- `OffsetDateTime` → the result of `offsetDateTime.toInstant()` (offset converted to UTC).
- `ZonedDateTime` → the result of `zonedDateTime.toInstant()`.
- `LocalDateTime` → the result of `localDateTime.atOffset(ZoneOffset.UTC).toInstant()` (wall-clock fields treated as UTC).
- `LocalDate` → the result of `localDate.atStartOfDay(ZoneOffset.UTC).toInstant()` (midnight UTC).
- Any other instant-bearing `Temporal` → the result of `Instant.from(t)`.

#### Scenario: Date-only input coerces to midnight UTC

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is enabled
- **AND** `dtStamp = new DtStamp("20240601")`
- **WHEN** `dtStamp.getValue()` is invoked
- **THEN** the result is `"20240601T000000Z"`

#### Scenario: Floating date-time input coerces to UTC wall-clock

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is enabled
- **AND** `dtStamp = new DtStamp("20240601T120000")`
- **WHEN** `dtStamp.getValue()` is invoked
- **THEN** the result is `"20240601T120000Z"`

#### Scenario: Offset date-time input coerces to the equivalent UTC instant

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is enabled
- **AND** `dtStamp = new DtStamp("20240601T120000+0500")`
- **WHEN** `dtStamp.getValue()` is invoked
- **THEN** the result is `"20240601T070000Z"`

#### Scenario: Already-UTC input is preserved unchanged

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is enabled
- **AND** `dtStamp = new DtStamp("20240601T120000Z")`
- **WHEN** `dtStamp.getValue()` is invoked
- **THEN** the result is `"20240601T120000Z"`

### Requirement: UtcProperty strict parsing SHALL continue to reject non-UTC inputs

When `CompatibilityHints.KEY_RELAXED_PARSING` is NOT enabled, `setValue(String)` on a `UtcProperty` subclass SHALL throw `DateTimeParseException` for any input that does not match `CalendarDateFormat.UTC_DATE_TIME_FORMAT` (pattern `yyyyMMdd'T'HHmmss'Z'`). The coercion behaviour defined above SHALL NOT apply in strict mode — strict mode never reaches a state where a non-Instant value needs coercing, because the parse itself fails first.

#### Scenario: Strict DTSTAMP rejects a date-only string

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is disabled
- **WHEN** `new DtStamp("20240601")` is invoked
- **THEN** a `DateTimeParseException` is thrown from the constructor

#### Scenario: Strict DTSTAMP rejects a floating date-time string

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is disabled
- **WHEN** `new DtStamp("20240601T120000")` is invoked
- **THEN** a `DateTimeParseException` is thrown from the constructor

#### Scenario: Strict DTSTAMP rejects an offset date-time string

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is disabled
- **WHEN** `new DtStamp("20240601T120000+0500")` is invoked
- **THEN** a `DateTimeParseException` is thrown from the constructor

### Requirement: The relaxed coercion SHALL NOT affect non-UtcProperty DateProperty subclasses

Properties that extend `DateProperty` but do NOT implement `UtcProperty` (e.g. `DtStart`, `DtEnd`, `Due`, `RecurrenceId`, `ExDate`, `RDate`) SHALL retain their existing relaxed-parsing behaviour. In particular, a date-only input on `DtStart` under relaxed parsing SHALL continue to produce a `LocalDate` value (not be coerced to an `Instant`).

#### Scenario: DtStart preserves LocalDate parse result under relaxed parsing

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is enabled
- **AND** `dtStart = new DtStart<>("20240601")`
- **WHEN** `dtStart.getDate()` is invoked
- **THEN** the returned `Temporal` is a `LocalDate` (not an `Instant`)
- **AND** `dtStart.getValue()` is `"20240601"` (date-only format preserved)

### Requirement: The relaxed coercion SHALL apply uniformly to every UtcProperty subclass

The coercion behaviour SHALL be implemented in a shared code path, so adding a future `UtcProperty` subclass automatically inherits the correct behaviour without per-subclass overrides.

#### Scenario: Every UtcProperty subclass returns a UTC instant string for a date-only input under relaxed parsing

- **GIVEN** `CompatibilityHints.KEY_RELAXED_PARSING` is enabled
- **AND** any class C that extends `DateProperty<Instant>` and implements `UtcProperty`
- **AND** a constructor on C that accepts a `String` value (e.g. `DtStamp(String)`, `Created(String)`, `LastModified(String)`, `Completed(String)`, `Acknowledged(ParameterList, String)`, `TzUntil(ParameterList, String)`, `Trigger(ParameterList, String)` with DATE-TIME value type)
- **WHEN** the constructor is invoked with `"20240601"`
- **THEN** the resulting instance's `getValue()` returns a string ending with `"T000000Z"`
- **AND** no exception is thrown

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
