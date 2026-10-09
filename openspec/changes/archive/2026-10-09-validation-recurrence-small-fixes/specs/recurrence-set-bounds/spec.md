## ADDED Requirements

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
