# Recurrence set bounds

## Purpose

Defines how the query period passed to `Component.calculateRecurrenceSet` (and `RecurrenceSet.Builder.build()`) bounds the returned occurrences, consistently across every occurrence source (RRULE instances, RDATE dates and periods, and the single instance of a non-recurring component). The period is half-open: start inclusive, end exclusive, with an instance that starts before the period but overlaps it retained. Also covers zero-length occurrences and zero-length (point) queries, zero-length `Period.intersects` semantics, and DATE-valued bounds applied to date-time components.
## Requirements
### Requirement: Recurrence calculation SHALL accept DATE-valued query bounds for date-time events

`Component.calculateRecurrenceSet(period)` (via `RecurrenceSet.Builder.build()`) SHALL accept a query `Period` whose bounds are DATE values (`LocalDate`) when the component's effective duration is a time-based `java.time.Duration`, and SHALL NOT throw `UnsupportedTemporalTypeException`. The look-back window used to catch instances that start before the period but overlap it SHALL be widened by whole days (at least the duration rounded up to the next day) instead of subtracting the time-based amount from the DATE bound.

#### Scenario: Monthly date-time event queried over a DATE period

- **WHEN** a `VEVENT` has `DTSTART:20140629T100000Z`, `DTEND:20140629T110000Z`, `RRULE:FREQ=MONTHLY;COUNT=3`
- **AND** `calculateRecurrenceSet` is called with `new Period<>(LocalDate.parse("2014-06-29"), LocalDate.parse("2015-06-30"))`
- **THEN** no exception is thrown
- **AND** the result contains exactly the three instances starting at `2014-06-29T10:00Z`, `2014-07-29T10:00Z` and `2014-08-29T10:00Z`

### Requirement: Zero-length periods SHALL intersect by half-open containment

`Period.intersects(other)` SHALL return `true` for a zero-length `other` (start equal to end by `TemporalComparator`) if and only if `this.start <= other.start < this.end`; symmetrically, a zero-length `this` intersects `other` if and only if `other.start <= this.start < other.end`. Two equal periods SHALL always intersect. Non-empty periods keep the existing half-open rule (`this.start < other.end && other.start < this.end`).

#### Scenario: Zero-length period at the start of a period

- **WHEN** `[20:00, 21:00)` is tested against the zero-length period at `20:00`
- **THEN** `intersects` returns `true` in both argument orders

#### Scenario: Zero-length period strictly inside a period

- **WHEN** `[19:00, 21:00)` is tested against the zero-length period at `20:00`
- **THEN** `intersects` returns `true`

#### Scenario: Zero-length period at the end of a period

- **WHEN** `[19:00, 20:00)` is tested against the zero-length period at `20:00`
- **THEN** `intersects` returns `false` in both argument orders

#### Scenario: Two zero-length periods

- **WHEN** two zero-length periods are tested
- **THEN** `intersects` returns `true` if they are at the same instant and `false` otherwise

### Requirement: A zero-duration event SHALL be returned when the query period starts at its start

`Component.calculateRecurrenceSet(period)` for a non-recurring component whose `DTSTART` equals its `DTEND` SHALL include the component's single zero-length period when `period.start <= DTSTART < period.end`, and SHALL exclude it when `DTSTART == period.end`.

#### Scenario: Query period starts exactly at the event start

- **WHEN** a `VEVENT` has `DTSTART:20160520T200000Z` and `DTEND:20160520T200000Z`
- **AND** `calculateRecurrenceSet` is called for `20160520T200000Z/20160520T210000Z`
- **THEN** the result contains one period

#### Scenario: Query period ends exactly at the event start

- **WHEN** the same event is queried for `20160520T190000Z/20160520T200000Z`
- **THEN** the result is empty

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

