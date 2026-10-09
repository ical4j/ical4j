## ADDED Requirements

### Requirement: A VTIMEZONE without observances is retained without zone rules

When a `VTIMEZONE` with neither a `STANDARD` nor a `DAYLIGHT` observance is registered with `TimeZoneRegistryImpl.register`, the system SHALL store the definition so that `getTimeZone(id)` returns it, SHALL NOT build zone rules or allocate a synthetic zone id for it, SHALL NOT throw, and SHALL log a warning naming the `TZID`. This applies regardless of compatibility hints and regardless of whether the zone-rules provider is available. A `TZID` referencing such a definition SHALL resolve via `TimeZoneRegistry.getGlobalZoneId`, exactly as a `TZID` with no registered definition does.

#### Scenario: Calendar with an empty VTIMEZONE parses in strict mode

- **WHEN** a calendar containing `BEGIN:VTIMEZONE`, `TZID:UTC`, `END:VTIMEZONE` and a `VEVENT` with `DTSTART;TZID=UTC:20260528T120000` is built with no relaxed hints enabled
- **THEN** `CalendarBuilder.build` completes without throwing
- **AND** the event's `DTSTART` is a `ZonedDateTime` at local time `2026-05-28T12:00` with offset `+00:00` in the zone `TimeZoneRegistry.getGlobalZoneId("UTC")`
- **AND** the registry's `getTimeZone("UTC")` returns the parsed, observance-less definition
- **AND** the registry's `getZoneRules()` contains no entry for it
- **AND** the serialised calendar still contains the empty `VTIMEZONE`

#### Scenario: Direct registration of an observance-less definition

- **WHEN** `register` is called with a `TimeZone` whose `VTimeZone` has a `TZID` and no observances
- **THEN** `getTimeZone(id)` returns that definition
- **AND** `getZoneRules()` is unchanged

### Requirement: A VTIMEZONE whose observances cannot produce zone rules is retained under relaxed hints

When zone-rules derivation for a registered `VTIMEZONE` fails with a runtime exception (for example an observance without `DTSTART`, or no observance applicable to the current time) and either `ical4j.parsing.relaxed` or `ical4j.validation.relaxed` is enabled, the system SHALL behave as for an observance-less definition: store it, build no zone rules, log a warning naming the `TZID` and the cause, and not throw. When neither hint is enabled, `register` SHALL throw an `IllegalArgumentException` whose message names the `TZID` and includes the underlying cause's message, with the original exception as its cause.

#### Scenario: Observances without DTSTART under relaxed parsing

- **WHEN** `ical4j.parsing.relaxed` is enabled and a calendar containing `VTIMEZONE` `TZID:UTC` whose `STANDARD` and `DAYLIGHT` observances carry only `TZOFFSETFROM`/`TZOFFSETTO`, plus a `VEVENT` with `DTSTART:20250509T000000Z`, is built
- **THEN** `CalendarBuilder.build` completes without throwing
- **AND** the event's `DTSTART` denotes the instant `2025-05-09T00:00:00Z`
- **AND** the registry's `getTimeZone("UTC")` returns the parsed definition with its two observances
- **AND** the registry's `getZoneRules()` is empty

#### Scenario: Observances without DTSTART under relaxed validation

- **WHEN** the same calendar is built with only `ical4j.validation.relaxed` enabled
- **THEN** the outcome is identical to the relaxed-parsing scenario

#### Scenario: Observances without DTSTART in strict mode

- **WHEN** the same calendar is built with no relaxed hints enabled
- **THEN** `CalendarBuilder.build` throws a `ParserException`
- **AND** its message contains `UTC` and `DTSTART`

#### Scenario: Well-formed definitions are unaffected

- **WHEN** a calendar with a well-formed `VTIMEZONE` for `Europe/Paris` (observances with `DTSTART`, `RRULE`, `TZOFFSETFROM`, `TZOFFSETTO`) and an event at `DTSTART;TZID=Europe/Paris:20260715T100000` is built in strict mode
- **THEN** the registry's `getZoneRules()` contains one entry
- **AND** the event's offset is `+02:00`
