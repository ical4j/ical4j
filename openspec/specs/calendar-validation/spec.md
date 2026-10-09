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

### Requirement: Validator factory lookup SHALL fall back to the default implementation

`AbstractCalendarValidatorFactory.getInstance()` SHALL return the `CalendarValidatorFactory` registered via `ServiceLoader` when one is visible to `DefaultCalendarValidatorFactory`'s class loader. When no registration is visible (for example because `META-INF/services` was stripped by ProGuard/R8 or a packaging tool), it SHALL return a `DefaultCalendarValidatorFactory` instance. Class initialisation SHALL NOT fail because a registration is absent.

#### Scenario: No service registration visible

- **WHEN** `AbstractCalendarValidatorFactory` is initialised in a class loader that returns no resources for `META-INF/services/net.fortuna.ical4j.validate.CalendarValidatorFactory`
- **THEN** no `ExceptionInInitializerError` or `NoSuchElementException` is thrown
- **AND** `getInstance()` returns a `DefaultCalendarValidatorFactory`

#### Scenario: Service registration present

- **WHEN** the bundled service registration is on the classpath
- **THEN** `getInstance()` returns the registered factory (`DefaultCalendarValidatorFactory` by default)

