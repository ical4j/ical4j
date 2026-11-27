# Recurrence across DST transitions

## Purpose

Defines how `Recur` resolves candidate instances whose local date-time falls on a daylight-saving transition for a `ZonedDateTime` seed. Per RFC 5545 §3.3.5 an ambiguous local time in an overlap denotes the first occurrence (the pre-transition offset), independent of the offset carried by the seed or of `WKST`; a local time in a gap is shifted forward by the gap length. Non-zoned seeds (`LocalDateTime`, `LocalDate`, `Instant`, `OffsetDateTime`) are not adjusted.
## Requirements
### Requirement: Instances in a daylight-saving overlap carry the pre-transition offset

For a `ZonedDateTime` seed, every instance returned by `Recur.getDates`, `Recur.getDatesAsStream` and `Recur.getNextDate` whose local date-time occurs twice in the seed's zone (a fall-back overlap) SHALL carry the earlier of the two valid offsets, i.e. the offset in force before the transition, as required by RFC 5545 §3.3.5. This SHALL hold regardless of the seed's own offset, the `WKST` value, or which BY* rule or frequency increment produced the instance.

#### Scenario: Weekly BYDAY instance in the overlap (issue #716)

- **WHEN** `FREQ=WEEKLY;INTERVAL=1;BYDAY=FR,SA,SU,MO` is seeded at `2000-02-01T01:30` America/Los_Angeles and `getDates` is called for `2019-10-20T20:00Z/2019-11-10T00:00Z`
- **THEN** the instance on `2019-11-03` is `2019-11-03T01:30-07:00[America/Los_Angeles]`
- **AND** the result is the same with `WKST=SU` and with `WKST=MO`

#### Scenario: Daily instance in the overlap, seed in standard time

- **WHEN** `FREQ=DAILY` is seeded at `2019-02-01T01:30` America/Los_Angeles (offset `-08:00`) and `getDates` is called for `2019-11-02T00:00/2019-11-05T00:00` in that zone
- **THEN** the instances carry offsets `-07:00`, `-07:00`, `-08:00` for 2, 3 and 4 November respectively

#### Scenario: Daily instance in the overlap, seed in daylight time

- **WHEN** the same rule is seeded at `2019-07-01T01:30` America/Los_Angeles (offset `-07:00`)
- **THEN** the instances for 2, 3 and 4 November carry the same offsets `-07:00`, `-07:00`, `-08:00`

### Requirement: Instances in a daylight-saving gap are shifted forward by the gap length

For a `ZonedDateTime` seed, an instance whose local date-time does not exist in the seed's zone (a spring-forward gap) SHALL be shifted later by the length of the gap, retaining java.time's resolution.

#### Scenario: Daily 02:00 instance on the spring-forward day

- **WHEN** `FREQ=DAILY` is seeded at `2025-03-08T02:00` America/Los_Angeles and `getDates` is called for `2025-03-08T00:00/2025-03-10T12:00` in that zone
- **THEN** the instances are `2025-03-08T02:00-08:00`, `2025-03-09T03:00-07:00` and `2025-03-10T02:00-07:00`

### Requirement: Non-zoned seeds are unaffected

Instances generated for `Instant`, `OffsetDateTime`, `LocalDateTime` and `LocalDate` seeds SHALL be produced exactly as before; no offset normalisation is applied.

#### Scenario: OffsetDateTime seed across the transition

- **WHEN** `FREQ=DAILY` is seeded with an `OffsetDateTime` at `2019-11-01T01:30-07:00` and queried across 3 November
- **THEN** every instance keeps the fixed `-07:00` offset

