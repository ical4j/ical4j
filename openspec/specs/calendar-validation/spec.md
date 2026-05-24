# Calendar validation

## Purpose

Defines how ical4j validates `Calendar`, `Component`, and `Property` instances against RFC 5545 (iCalendar) and RFC 5546 (iTIP). Covers calendar-level invariants (PRODID/VERSION presence, METHOD-aware dispatch), per-component iTIP rule sets, and the unsupported-method fallback behaviour.

## Requirements

### Requirement: VCALENDAR objects SHALL require PRODID and VERSION

`CalendarValidatorImpl.validate(Calendar)` SHALL produce an ERROR-severity `ValidationEntry` when the input calendar omits the PRODID property, the VERSION property, or both. This check SHALL apply unconditionally, independent of any `ValidationRule` instances passed to the constructor.

#### Scenario: Calendar missing PRODID

- **WHEN** `CalendarValidatorImpl.validate` is invoked with a `Calendar` whose property list contains no `PRODID`
- **THEN** the returned `ValidationResult` contains a `ValidationEntry` at `Calendar.VCALENDAR` with severity `ERROR` and message containing `PRODID`

#### Scenario: Calendar missing VERSION

- **WHEN** `CalendarValidatorImpl.validate` is invoked with a `Calendar` whose property list contains no `VERSION`
- **THEN** the returned `ValidationResult` contains a `ValidationEntry` at `Calendar.VCALENDAR` with severity `ERROR` and message containing `VERSION`

#### Scenario: Calendar missing both PRODID and VERSION

- **WHEN** `CalendarValidatorImpl.validate` is invoked with a `Calendar` whose property list contains neither
- **THEN** the returned `ValidationResult` contains at least one `ValidationEntry` for PRODID AND at least one for VERSION
- **AND** both entries have severity `ERROR`

### Requirement: VEVENT/PUBLISH iTIP rule SHALL permit ATTENDEE

The iTIP rule set for `(VEVENT, PUBLISH)` SHALL NOT forbid the ATTENDEE property. RFC 5546 §3.2.1.1 explicitly allows zero or more ATTENDEE properties on PUBLISH messages.

#### Scenario: PUBLISH VEVENT with one ATTENDEE validates clean

- **WHEN** a `Calendar` with `METHOD:PUBLISH` contains a `VEVENT` that has DTSTAMP, DTSTART, ORGANIZER, SUMMARY, UID, and exactly one ATTENDEE
- **AND** `Calendar.validate()` is invoked
- **THEN** no `ValidationEntry` mentions ATTENDEE
- **AND** the iTIP rule set for `(VEVENT, PUBLISH)` does not contribute an error for ATTENDEE presence

#### Scenario: PUBLISH VEVENT with multiple ATTENDEEs validates clean

- **WHEN** a `Calendar` with `METHOD:PUBLISH` contains a `VEVENT` with two or more ATTENDEE properties
- **AND** `Calendar.validate()` is invoked
- **THEN** no `ValidationEntry` mentions ATTENDEE

### Requirement: VFREEBUSY/COUNTER SHALL have a defined iTIP rule set

The iTIP rule registry SHALL include an entry for `(VFREEBUSY, COUNTER)` defining at minimum:
- `One`: ATTENDEE, DTEND, DTSTAMP, DTSTART, ORGANIZER, UID
- `OneOrLess`: COMMENT, DURATION, URL

#### Scenario: COUNTER VFREEBUSY with required properties validates clean

- **WHEN** a `Calendar` with `METHOD:COUNTER` contains a `VFREEBUSY` with all six required properties present (ATTENDEE, DTEND, DTSTAMP, DTSTART, ORGANIZER, UID)
- **AND** `Calendar.validate()` is invoked
- **THEN** the `ValidationResult` contains no entries from the `(VFREEBUSY, COUNTER)` rule set
- **AND** no `ValidationException` is thrown

#### Scenario: COUNTER VFREEBUSY missing UID is rejected

- **WHEN** a `Calendar` with `METHOD:COUNTER` contains a `VFREEBUSY` that is missing UID
- **AND** `Calendar.validate()` is invoked
- **THEN** the `ValidationResult` contains a `ValidationEntry` for `UID` with severity `ERROR`

### Requirement: Unsupported (component, method) pairs SHALL return a ValidationResult, not throw

When a component's `validate(Method)` is invoked with a method that has no registered rule set for that component, the call SHALL return a `ValidationResult` containing one `ValidationEntry` with severity `ERROR` and a message stating that the method is not applicable to the component. The call SHALL NOT throw `ValidationException`.

#### Scenario: VJOURNAL with METHOD:REQUEST returns a ValidationEntry

- **WHEN** `CalendarValidatorImpl.validate` is invoked with a `Calendar` containing `METHOD:REQUEST` and a `VJOURNAL` component
- **AND** RFC 5546 does not define a `(VJOURNAL, REQUEST)` rule set (only ADD, CANCEL, PUBLISH)
- **THEN** the calendar-level `ValidationResult` contains at least one `ValidationEntry` whose message indicates that REQUEST is not applicable to VJOURNAL
- **AND** no `ValidationException` propagates out of `Calendar.validate()`

#### Scenario: Unknown extension method does not throw

- **WHEN** `CalendarValidatorImpl.validate` is invoked with a `Calendar` containing a custom `METHOD` value (e.g., `X-COMPANY-METHOD`) and a `VEVENT`
- **THEN** no `ValidationException` propagates
- **AND** the calendar-level `ValidationResult` contains a `ValidationEntry` indicating the method is not applicable

### Requirement: Calendar-level validation SHALL merge per-component iTIP results

`CalendarValidatorImpl.validate(Calendar)` SHALL merge the `ValidationResult` returned by each `component.validate(method)` call into the calendar-level result, such that any per-method rule violation surfaces in the final `ValidationResult`.

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

### Requirement: iTIP rule definitions SHALL be co-located in the validate package

The per-(component, method) iTIP rule sets SHALL be defined in a single source file under `net.fortuna.ical4j.validate`. Model classes (`VEvent`, `VToDo`, `VJournal`, `VFreeBusy`) SHALL NOT carry static initialiser blocks defining iTIP rule sets.

#### Scenario: VEvent.java contains no iTIP rule definitions

- **WHEN** the source file `src/main/java/net/fortuna/ical4j/model/component/VEvent.java` is inspected after the change is applied
- **THEN** it contains no `methodValidators.put(...)` calls
- **AND** it contains no `new VEventValidator(...)` instantiations

#### Scenario: validate package owns iTIP rules

- **WHEN** the source tree under `src/main/java/net/fortuna/ical4j/validate/` is inspected
- **THEN** a single source file (named per the design — default `ITIPRuleRegistry.java`) holds the full per-(component, method) rule set table
- **AND** that file is referenced by each component's `validate(Method)` method via a single delegation call
