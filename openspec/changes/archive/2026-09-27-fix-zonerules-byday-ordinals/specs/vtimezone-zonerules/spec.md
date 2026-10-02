## RENAMED Requirements

- FROM: `### Requirement: Existing BYDAY-based transition rules unchanged`
- TO: `### Requirement: BYDAY ordinals map to transition-rule day-of-month indicators`

## MODIFIED Requirements

### Requirement: BYDAY ordinals map to transition-rule day-of-month indicators

When an observance `RRULE` specifies a `BYDAY` part, the system SHALL build a future transition rule whose day-of-week is that `BYDAY` weekday and whose `dayOfMonthIndicator` selects the weekday the ordinal denotes:

- For a positive ordinal *n* from 1 to 4, the indicator SHALL be `(n-1)*7 + 1`, which selects the *n*th such weekday of the month.
- For a negative ordinal −*n* from −1 to −4, the indicator SHALL be `-((n-1)*7 + 1)`, which selects the *n*th-from-last such weekday of the month.
- For an ordinal of 0 (a `BYDAY` weekday with no ordinal), the indicator SHALL be taken from the first `BYMONTHDAY` value if present, and otherwise from the observance `DTSTART` day-of-month, as before.
- For ordinals of magnitude 5, the indicator SHALL be clamped to the range `-28..28`. Building SHALL NOT throw for these ordinals in any month, including February in a non-leap year. The resulting transition is an approximation. It is not guaranteed to fall on the 5th weekday, and in a short month it may fall in the adjacent month.

Ordinals `1` and `-1` map to indicators `1` and `-1`, so rules using only them SHALL produce the same transition rules as before this requirement was introduced.

#### Scenario: Last-Sunday rule is unchanged

- **WHEN** an observance has `RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU` and differing from/to offsets
- **THEN** the generated transition rule has day-of-week `SUNDAY` and indicator `-1`
- **AND** the resulting `ZoneRules` match those built before this requirement was introduced

#### Scenario: Second-Sunday rule selects the second Sunday

- **WHEN** an observance has `RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=2SU` and differing from/to offsets
- **THEN** the generated transition rule has day-of-week `SUNDAY` and indicator `8`
- **AND** for every year the rule covers, the transition falls on the second Sunday of March, including years in which March 1 is not a Sunday (e.g. 2027), where the previous behaviour was a week early

#### Scenario: US Eastern future transitions match tzdb

- **WHEN** `ZoneRules` are built from a `VTimeZone` for `America/New_York` whose observances use `BYMONTH=3;BYDAY=2SU` and `BYMONTH=11;BYDAY=1SU`
- **THEN** for each year from 2027 to 2040 the DST start and end instants equal those of `ZoneId.of("America/New_York").getRules()`

#### Scenario: Ordinals from the end of the month

- **WHEN** an observance has `BYDAY=-2SU`
- **THEN** the generated transition rule has indicator `-8`
- **AND** the transition falls on the second-to-last Sunday of the rule's month in every year

#### Scenario: Plain BYDAY keeps the BYMONTHDAY fallback

- **WHEN** an observance has `RRULE:FREQ=YEARLY;BYMONTH=4;BYDAY=SU;BYMONTHDAY=8,9,10,11,12,13,14`
- **THEN** the generated transition rule has day-of-week `SUNDAY` and indicator `8`

#### Scenario: Fifth-weekday ordinal in February does not throw

- **WHEN** an observance has `RRULE:FREQ=YEARLY;BYMONTH=2;BYDAY=5SU` or `BYDAY=-5SU`
- **THEN** `ZoneRules` are built without throwing
- **AND** requesting transitions for any year, including non-leap years, does not throw
