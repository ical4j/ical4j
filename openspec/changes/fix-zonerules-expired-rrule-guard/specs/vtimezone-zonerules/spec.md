## ADDED Requirements

### Requirement: Expired observance RRULEs are not projected as future transition rules

When building future transition rules, the system SHALL consider only the observances that would produce a transition rule: those with an `RRULE` that has a `BYMONTH` part and whose `TZOFFSETFROM` offset differs from its `TZOFFSETTO` offset. If every such observance's `RRULE` has an `UNTIL` before the current instant, the system SHALL produce no future transition rules.

An observance that would not produce a transition rule, such as one with no `RRULE` (only an `RDATE` or a single `DTSTART`), one whose `RRULE` has no `BYMONTH`, or one with equal `TZOFFSETFROM` and `TZOFFSETTO`, SHALL NOT cause an expired `RRULE` on another observance to be projected into the future.

#### Scenario: Expired DAYLIGHT rule beside an RDATE-only STANDARD observance

- **WHEN** `ZoneRules` are built from a `VTimeZone` whose latest DAYLIGHT observance has `RRULE:FREQ=YEARLY;BYMONTH=1;BYDAY=3SA;UNTIL=19920118T060000Z` and whose latest STANDARD observance has no `RRULE`, only `RDATE:19920315T000000` (the shape of `America/Costa_Rica`)
- **THEN** no future transition rule is generated from the expired DAYLIGHT `RRULE`
- **AND** the offset at `2026-07-01T12:00:00Z` is the standard offset, `-06:00`

#### Scenario: Zones that abolished DST keep their standard offset all year

- **WHEN** `ZoneRules` are built from the bundled definitions of `Europe/Moscow`, `America/Costa_Rica`, `America/Santo_Domingo`, `Australia/Darwin`, `America/Sao_Paulo` and `Europe/Istanbul`
- **THEN** the offset on `2026-01-15T12:00:00Z` and on `2026-07-01T12:00:00Z` is, respectively, `+03:00`, `-06:00`, `-04:00`, `+09:30`, `-03:00` and `+03:00`

#### Scenario: Zoned date-time output in a zone that abolished DST

- **WHEN** a `DtStart` is created from `2026-10-25T12:00:00Z` at zone `Europe/Moscow`
- **THEN** it is written as `DTSTART;TZID=Europe/Moscow:20261025T150000`

#### Scenario: Active DST rules are unaffected

- **WHEN** `ZoneRules` are built from a `VTimeZone` whose latest STANDARD and DAYLIGHT observances have `RRULE`s with no `UNTIL` (for example `America/Los_Angeles` or `Australia/Melbourne`)
- **THEN** future transition rules are generated for both observances as before
