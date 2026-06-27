## ADDED Requirements

### Requirement: VTODO STATUS values SHALL be one of NEEDS-ACTION, COMPLETED, IN-PROCESS, or CANCELLED

When a `VTodo` component carries a STATUS property, that property's value SHALL be one of the four RFC 5545 §3.8.1.11 allowed values for VTODO. The check SHALL apply across both the no-method (`Calendar.validate()`) path and the iTIP-method path.

#### Scenario: Invalid VTODO STATUS produces a ValidationEntry

- **WHEN** a `VTodo` carries a STATUS property whose value is not NEEDS-ACTION, COMPLETED, IN-PROCESS, or CANCELLED (for example "TENTATIVE", which is a VEVENT-only value)
- **AND** the component is validated either via `Calendar.validate()` or `component.validate(method)`
- **THEN** the resulting `ValidationResult` contains a `ValidationEntry` mentioning STATUS

#### Scenario: Valid VTODO STATUS does not produce a STATUS entry

- **WHEN** a `VTodo` carries STATUS:COMPLETED (one of the four valid values)
- **THEN** the resulting `ValidationResult` contains no entry mentioning STATUS

### Requirement: VTIMEZONE SHALL contain at least one STANDARD or DAYLIGHT observance

A `VTimeZone` component without any STANDARD or DAYLIGHT sub-component SHALL produce an ERROR-severity `ValidationEntry` on validation, rather than causing a `ValidationException` to be thrown.

#### Scenario: VTIMEZONE missing both observances produces a ValidationEntry

- **WHEN** a `VTimeZone` is validated and contains no STANDARD and no DAYLIGHT sub-component
- **THEN** the resulting `ValidationResult` contains a `ValidationEntry` mentioning observance presence
- **AND** no `ValidationException` is thrown

#### Scenario: VTIMEZONE with a STANDARD observance passes the presence check

- **WHEN** a `VTimeZone` with at least one STANDARD sub-component is validated
- **THEN** the resulting `ValidationResult` contains no observance-presence entry

### Requirement: Alarm validation SHALL apply in both the no-method and iTIP-method paths

VALARM sub-components of a parent (VEVENT, VTODO) SHALL be validated against the VALARM_ITIP rule set whether the parent is validated via the no-method `Calendar.validate()` path or via the iTIP `component.validate(method)` path. When iTIP rules forbid alarms for a given (component, method) combination (e.g., VEVENT/CANCEL), the presence of any VALARM SHALL produce a `ValidationEntry`.

#### Scenario: VEvent with a valid VALARM via no-method validate

- **WHEN** a `VEvent` containing one well-formed VALARM is validated via `event.validate()`
- **THEN** the resulting `ValidationResult` contains no entries from the VALARM_ITIP rule set

#### Scenario: VEvent with a malformed VALARM via no-method validate

- **WHEN** a `VEvent` containing a VALARM that is missing the required ACTION property is validated via `event.validate()`
- **THEN** the resulting `ValidationResult` contains a `ValidationEntry` mentioning ACTION

#### Scenario: VEvent CANCEL with alarms produces an entry

- **WHEN** a `VEvent` containing one VALARM is validated via `event.validate(CANCEL)` (RFC 5546 disallows alarms on CANCEL)
- **THEN** the resulting `ValidationResult` contains a `ValidationEntry` mentioning VALARM presence

### Requirement: The six deprecated component validator subclasses SHALL not exist

The deprecated component validator subclasses under `net.fortuna.ical4j.validate.component.*` SHALL not exist in the source tree after this change. The full removal list: `VEventValidator`, `VToDoValidator`, `VAvailabilityValidator`, `VFreeBusyValidator`, `VTimeZoneValidator`, `AvailableValidator`.

#### Scenario: validate/component directory empty (or removed)

- **WHEN** the source tree under `src/main/java/net/fortuna/ical4j/validate/component/` is inspected after this change is applied
- **THEN** none of the six listed `*Validator.java` files exists

#### Scenario: No internal references to the deleted classes remain

- **WHEN** `grep -rn "VEventValidator\|VToDoValidator\|VAvailabilityValidator\|VFreeBusyValidator\|VTimeZoneValidator\|AvailableValidator" src/main src/test --include="*.java"` is run
- **THEN** the only remaining matches (if any) are within Spock test files under `src/test/groovy/` or in archived OpenSpec change documents, neither of which is changed by this work
