## ADDED Requirements

### Requirement: CalendarValidatorImpl SHALL validate only the calendar level

`CalendarValidatorImpl.validate(Calendar)` SHALL evaluate calendar-level rules only (calendar property rules, PRODID/VERSION presence, the VERSION value, at-least-one-component, calendar property types, and the `ITIPValidator` rules when METHOD is present). It SHALL NOT invoke `component.validate(...)` on the calendar's components. Component-level validation, including per-method iTIP validation, SHALL be performed by `Calendar.validate(true)` (and therefore `Calendar.validate()`), and SHALL NOT be performed by `Calendar.validate(false)`.

#### Scenario: Non-recursive validation ignores component iTIP violations

- **WHEN** a `Calendar` has `METHOD:REQUEST` and contains a `VEVENT` that lacks the ORGANIZER required by RFC 5546
- **AND** `Calendar.validate(false)` is invoked
- **THEN** the returned `ValidationResult` has no errors

#### Scenario: The calendar validator alone does not descend into components

- **WHEN** `new CalendarValidatorImpl().validate(calendar)` is invoked on the same calendar
- **THEN** the returned `ValidationResult` has no errors

#### Scenario: Recursive validation reports each component violation exactly once

- **WHEN** `Calendar.validate(true)` is invoked on the same calendar
- **THEN** the returned `ValidationResult` has errors
- **AND** exactly one `ValidationEntry` mentions ORGANIZER

### Requirement: A duplicated required property SHALL be reported once

For VEVENT, VFREEBUSY and VAVAILABILITY the `ComponentValidator` rule sets SHALL list UID and DTSTAMP only under the `One` rule, so that a component carrying two of either property yields a single `ValidationEntry` for that property.

#### Scenario: VEVENT with two DTSTAMP properties

- **WHEN** a `VEVENT` carries two DTSTAMP properties and `validate()` is invoked
- **THEN** the `ValidationResult` has errors
- **AND** exactly one `ValidationEntry` mentions DTSTAMP

#### Scenario: Two UID properties on VEVENT, VFREEBUSY and VAVAILABILITY

- **WHEN** a `VEVENT`, `VFREEBUSY` or `VAVAILABILITY` carries two UID properties and `validate()` is invoked
- **THEN** exactly one `ValidationEntry` mentions UID

## MODIFIED Requirements

### Requirement: Unsupported (component, method) pairs SHALL return a ValidationResult, not throw

When a component's `validate(Method)` is invoked with a method that has no registered rule set for that component, the call SHALL return a `ValidationResult` containing one `ValidationEntry` with severity `ERROR` and a message stating that the method is not applicable to the component. The call SHALL NOT throw `ValidationException`. These entries surface at calendar level through the recursive `Calendar.validate()` path, not through `CalendarValidatorImpl` alone.

#### Scenario: VJOURNAL with METHOD:REQUEST returns a ValidationEntry

- **WHEN** `Calendar.validate()` is invoked on a `Calendar` containing `METHOD:REQUEST` and a `VJOURNAL` component
- **AND** RFC 5546 does not define a `(VJOURNAL, REQUEST)` rule set (only ADD, CANCEL, PUBLISH)
- **THEN** the returned `ValidationResult` contains at least one `ValidationEntry` whose message indicates that REQUEST is not applicable to VJOURNAL
- **AND** no `ValidationException` propagates out of `Calendar.validate()`

#### Scenario: Unknown extension method does not throw

- **WHEN** `Calendar.validate()` is invoked on a `Calendar` containing a custom `METHOD` value (e.g., `X-COMPANY-METHOD`) and a `VEVENT`
- **THEN** no `ValidationException` propagates
- **AND** the returned `ValidationResult` contains a `ValidationEntry` indicating the method is not applicable

### Requirement: Calendar-level validation SHALL merge per-component iTIP results

`Calendar.validate(true)` SHALL merge the `ValidationResult` returned by each `component.validate(method)` call into the calendar-level result, such that any per-method rule violation surfaces in the final `ValidationResult` exactly once.

#### Scenario: A VEVENT failing its PUBLISH rule surfaces at calendar level

- **WHEN** a `Calendar` has `METHOD:PUBLISH` and contains a `VEVENT` that is missing the required ORGANIZER property
- **AND** `Calendar.validate()` is invoked
- **THEN** the calendar-level `ValidationResult` contains a `ValidationEntry` for ORGANIZER

#### Scenario: A VTODO failing its REQUEST rule surfaces at calendar level

- **WHEN** a `Calendar` has `METHOD:REQUEST` and contains a `VTODO` that is missing the required ATTENDEE property
- **AND** `Calendar.validate()` is invoked
- **THEN** the calendar-level `ValidationResult` contains a `ValidationEntry` for ATTENDEE

#### Scenario: Multiple components each contribute their own entries

- **WHEN** a `Calendar` has `METHOD:REQUEST` and contains two VEVENTs, each missing a different required property
- **AND** `Calendar.validate()` is invoked
- **THEN** the calendar-level `ValidationResult` contains at least two entries, one for each component's missing property
