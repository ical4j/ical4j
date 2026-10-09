## ADDED Requirements

### Requirement: The query period end is exclusive for every occurrence source

`RecurrenceSet.Builder.build()` (and therefore `Component.calculateRecurrenceSet(period)`) SHALL NOT return an occurrence whose start equals `period.getEnd()`, whether that occurrence originates from an `RRULE`, an `RDATE` date, an `RDATE;VALUE=PERIOD` value or the component's own `DTSTART`. Starts SHALL be compared to the period end with `TemporalComparator`.

#### Scenario: Daily RRULE instance at the period end is excluded

- **WHEN** a `VEVENT` has `DTSTART:20240101T000000Z`, `DTEND:20240101T010000Z`, `RRULE:FREQ=DAILY`
- **AND** `calculateRecurrenceSet` is called for `2024-01-01T00:00Z/2024-01-03T00:00Z`
- **THEN** the result contains exactly two periods, starting at `2024-01-01T00:00Z` and `2024-01-02T00:00Z`

#### Scenario: RDATE date at the period end is excluded

- **WHEN** a `VEVENT` has `DTSTART:20240101T000000Z`, `DTEND:20240101T010000Z` and `RDATE:20240102T000000Z,20240103T000000Z`
- **AND** `calculateRecurrenceSet` is called for `2024-01-01T00:00Z/2024-01-03T00:00Z`
- **THEN** the result contains exactly two periods, starting at `2024-01-01T00:00Z` and `2024-01-02T00:00Z`

#### Scenario: Date-valued daily event queried day by day

- **WHEN** a `VEVENT` has `DTSTART;VALUE=DATE:20240101` and `RRULE:FREQ=DAILY`
- **AND** `calculateRecurrenceSet` is called for `2024-01-02/2024-01-03`
- **THEN** the result contains exactly one period, starting on `2024-01-02`

### Requirement: The query period start is inclusive and overlapping instances are retained

An occurrence whose start equals `period.getStart()` SHALL be returned. An occurrence that starts before `period.getStart()` but whose effective duration extends past it SHALL also be returned. An occurrence that ends exactly at `period.getStart()` SHALL NOT be returned.

#### Scenario: Instance at the period start is included

- **WHEN** a daily `VEVENT` starting `20240101T000000Z` with a one-hour duration is queried for `2024-01-02T00:00Z/2024-01-03T00:00Z`
- **THEN** the result contains exactly one period, starting at `2024-01-02T00:00Z`

#### Scenario: Instance ending exactly at the period start is excluded

- **WHEN** the same event (instances `00:00`–`01:00`) is queried for `2024-01-02T01:00Z/2024-01-02T12:00Z`
- **THEN** the result is empty

#### Scenario: Instance overlapping the period start is included

- **WHEN** the same event is queried for `2024-01-02T00:30Z/2024-01-03T00:00Z`
- **THEN** the result contains exactly one period, starting at `2024-01-02T00:00Z`

### Requirement: A zero-length occurrence is included when its instant lies in the window

An occurrence with no extent (date-time `DTSTART` without `DTEND`/`DURATION`, or `DTEND` equal to `DTSTART`) SHALL be returned when its start is at or after `period.getStart()` and strictly before `period.getEnd()`, consistent with `Period.intersects` for zero-length periods.

#### Scenario: Zero-length occurrence at exactly the period start

- **WHEN** a `VEVENT` with `DTSTART:20240101T000000Z`, no `DTEND`, and `RRULE:FREQ=DAILY` is queried for `2024-01-02T00:00Z/2024-01-03T00:00Z`
- **THEN** the result contains exactly one period, starting at `2024-01-02T00:00Z`

#### Scenario: Zero-length occurrence strictly inside the window

- **WHEN** the same event is queried for `2024-01-01T12:00Z/2024-01-02T12:00Z`
- **THEN** the result contains exactly one period, starting at `2024-01-02T00:00Z`

#### Scenario: Zero-length occurrence at exactly the period end

- **WHEN** the same event is queried for `2024-01-01T12:00Z/2024-01-02T00:00Z`
- **THEN** the result is empty

### Requirement: A zero-length query period is a point query

When `period.getStart()` equals `period.getEnd()`, the system SHALL return the occurrences (if any) that start exactly at that instant or are in progress at that instant.

#### Scenario: VEvent.getOccurrence on a recurring instance

- **WHEN** a daily `VEVENT` starting `20240101T000000Z` is asked for `getOccurrence(2024-01-05T00:00Z)`
- **THEN** a non-null occurrence with `RECURRENCE-ID:20240105T000000Z` is returned

#### Scenario: Point query misses between instances

- **WHEN** the same event is queried with `calculateRecurrenceSet` for `2024-01-05T02:00Z/2024-01-05T02:00Z` (after the one-hour instance has ended)
- **THEN** the result is empty
