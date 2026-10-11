# Calendar parsing robustness

## Purpose

Defines how `CalendarParserImpl` and `DefaultContentHandler` tolerate common deviations in real-world iCalendar input without relaxed hints: trailing whitespace on `BEGIN`/`END` lines (including a folded whitespace-only line after `END:VCALENDAR`), and parameter values that a standard parameter type rejects but that appear on an experimental (`X-`) property, which are retained as `XParameter`. Standard properties keep their strict parameter validation.
## Requirements
### Requirement: Trailing whitespace on BEGIN and END lines is ignored

`CalendarParserImpl` SHALL ignore trailing whitespace (space, tab) after the component name on a `BEGIN:` or `END:` line, and SHALL ignore whitespace-only lines following `END:VCALENDAR`, in both strict and relaxed parsing modes. The component name passed to the `ContentHandler` SHALL NOT include the trailing whitespace.

#### Scenario: END:VCALENDAR with trailing spaces

- **WHEN** a calendar ends with `END:VCALENDAR   ` (three trailing spaces, with or without a final CRLF)
- **THEN** the calendar is parsed without a `ParserException`

#### Scenario: Whitespace-only line after END:VCALENDAR

- **WHEN** a calendar ends with `END:VCALENDAR\r\n   \r\n` or `END:VCALENDAR\r\n\t\r\n\r\n`
- **THEN** the calendar is parsed without a `ParserException`

#### Scenario: BEGIN:VEVENT with trailing spaces

- **WHEN** a component is delimited by `BEGIN:VEVENT  ` and `END:VEVENT  `
- **THEN** the calendar is parsed without a `ParserException`
- **AND** the resulting component's name is `VEVENT` (no trailing whitespace)

### Requirement: Rejected parameter values on experimental properties are retained as X parameters

When the `ContentHandler` is building a property whose name is experimental (`X-` prefix) and a parameter factory rejects the parameter value with `IllegalArgumentException`, the handler SHALL attach an `XParameter` carrying the parameter name and the raw value instead of propagating the exception. This fallback SHALL NOT apply to properties with standard names, and SHALL NOT change the behaviour of any compatibility hint.

#### Scenario: X-LOTUS-RECURID with RANGE=ALL parses without hints

- **WHEN** a `VEVENT` contains `X-LOTUS-RECURID;RANGE=ALL:20220309T173000Z` and no compatibility hints are enabled
- **THEN** the calendar is parsed without a `ParserException`
- **AND** the `X-LOTUS-RECURID` property has a `RANGE` parameter whose value is `ALL`
- **AND** the property is written back as `X-LOTUS-RECURID;RANGE=ALL:20220309T173000Z`

#### Scenario: RECURRENCE-ID with RANGE=ALL still fails in strict mode

- **WHEN** a `VEVENT` contains `RECURRENCE-ID;RANGE=ALL:20220309T173000Z` and no compatibility hints are enabled
- **THEN** parsing fails with a `ParserException` whose message contains `Invalid value [ALL]`

#### Scenario: Notes compatibility hint still produces a standard Range parameter

- **WHEN** the `ical4j.compatibility.notes` hint is enabled
- **AND** a `VEVENT` contains `RECURRENCE-ID;RANGE=ALL:20220309T173000Z`
- **THEN** the calendar is parsed and the `RANGE` parameter is a `Range` instance with value `ALL`

