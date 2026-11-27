## ADDED Requirements

### Requirement: All overrides sharing the UID participate in recurrence calculation

`ComponentGroup.calculateRecurrenceSet(period)` SHALL consider every component in the group's component list whose `UID` equals the group's `UID`, regardless of whether the component carries a `RECURRENCE-ID` and regardless of which parameters (if any) that `RECURRENCE-ID` carries. Components without a `RECURRENCE-ID` are master revisions; components with one are overrides.

#### Scenario: Bare RECURRENCE-ID override is applied

- **WHEN** a group built with `new ComponentGroup(components, uid)` contains a master `VEVENT` with `DTSTART:20210701T000000Z`, `DTEND:20210701T003000Z`, `RRULE:FREQ=DAILY` and an override with `RECURRENCE-ID:20210715T000000Z` (no parameters) and `DTSTART;VALUE=DATE:20210714`, `DTEND;VALUE=DATE:20210715`
- **AND** `calculateRecurrenceSet` is called for `2021-07-14T00:00Z/2021-07-16T00:00Z`
- **THEN** the result does not contain a period starting at `2021-07-15T00:00Z`
- **AND** the result contains a period starting at `2021-07-14` with a one-day duration

#### Scenario: Parameterised RECURRENCE-ID override is applied identically

- **WHEN** the same group is built but the override is written `RECURRENCE-ID;TZID=UTC:20210715T000000`
- **THEN** `calculateRecurrenceSet` returns the same periods as in the bare case

### Requirement: An override replaces the master instance it names with its own occurrence

For each override, the system SHALL remove from the result every master instance whose start matches the override's `RECURRENCE-ID` date, and SHALL add the override's own occurrence — a single period starting at the override's `DTSTART` whose duration is derived from its `DURATION`, else its `DTEND` or `DUE`, else the default for the start value type — if that period intersects the query period. The override's occurrence SHALL be added whether or not its `DTSTART` equals its `RECURRENCE-ID`. The added period's `getComponent()` SHALL return the override component.

#### Scenario: Rescheduled override is present in the result

- **WHEN** a weekly master (`DTSTART;VALUE=DATE:20101113`, `RRULE:FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA`) has an override `RECURRENCE-ID;VALUE=DATE:20101129` with `DTSTART;VALUE=DATE:20101130`, `DTEND;VALUE=DATE:20101201`
- **AND** `calculateRecurrenceSet` is called for `20101113/P3W`
- **THEN** the result has the same number of periods as the master alone
- **AND** it contains no period starting on `2010-11-29`
- **AND** it contains a period starting on `2010-11-30` with a one-day duration whose `getComponent()` is the override

#### Scenario: Override that keeps its start but changes its duration

- **WHEN** a daily 30-minute master has an override with `RECURRENCE-ID` equal to its `DTSTART` of `20210715T000000Z` and `DTEND:20210715T020000Z`
- **THEN** the result contains exactly one period starting at `2021-07-15T00:00Z`, with a two-hour duration

#### Scenario: Override moved outside the query period

- **WHEN** an override names `RECURRENCE-ID:20210715T000000Z` but its `DTSTART` is `20210801T000000Z`
- **AND** `calculateRecurrenceSet` is called for `2021-07-14T00:00Z/2021-07-16T00:00Z`
- **THEN** the result contains no period starting at `2021-07-15T00:00Z`
- **AND** the result contains no period for the override

### Requirement: Recurrence properties on an override do not generate occurrences

The system SHALL ignore `RRULE`, `RDATE`, `EXDATE` and `EXRULE` properties on an override when computing its contribution; an override SHALL contribute at most one occurrence.

#### Scenario: Override carrying the master's RRULE

- **WHEN** a daily master's override has `RECURRENCE-ID:20210715T000000Z`, `DTSTART:20210715T010000Z`, `DTEND:20210715T013000Z` and `RRULE:FREQ=DAILY`
- **AND** `calculateRecurrenceSet` is called for `2021-07-14T00:00Z/2021-07-18T00:00Z`
- **THEN** the result contains exactly one period attributable to the override, starting at `2021-07-15T01:00Z`
- **AND** the master instances on 14, 16 and 17 July are present exactly once each

### Requirement: RECURRENCE-ID matches master instances by temporal value, not Java type

The system SHALL compare a master instance start with an override's `RECURRENCE-ID` date using `TemporalComparator`, so that instant-bearing temporals (`Instant`, `OffsetDateTime`, `ZonedDateTime`) match when they denote the same absolute instant, and date or local date-time values match when their fields are equal.

#### Scenario: Zoned master, UTC RECURRENCE-ID

- **WHEN** a master has `DTSTART;TZID=Europe/Paris:20210715T020000`, `RRULE:FREQ=DAILY` and an override has `RECURRENCE-ID:20210716T000000Z` (the same instant as `20210716T020000` Paris) with `DTSTART;TZID=Europe/Paris:20210716T090000`
- **AND** `calculateRecurrenceSet` is called for a period covering 15–17 July 2021
- **THEN** the result contains no period starting at the instant `2021-07-16T00:00Z`
- **AND** the result contains a period starting at `2021-07-16T09:00` Paris time

### Requirement: Groups without overrides are unchanged

When no component in the group carries a `RECURRENCE-ID`, `calculateRecurrenceSet` SHALL return the union of the master revisions' recurrence sets, sorted, with duplicates removed, exactly as before this change.

#### Scenario: Master with one non-override revision

- **WHEN** a group contains a master and a second revision with `SEQUENCE:1` and identical `DTSTART`/`RRULE`
- **THEN** `calculateRecurrenceSet` returns the same set of periods as the master's own `calculateRecurrenceSet`
