# Temporal amount serialisation

## Purpose

Defines how `TemporalAmountAdapter` renders a `java.time.TemporalAmount` as an RFC 5545 §3.3.6 `dur-value`. A `java.time.Period` of years, months or days is anchored to a seed and rendered as the exact number of calendar days between the seed and the seed plus the period, using the week form (`PnW`) only when that count is a multiple of seven; a `java.time.Duration` keeps its day/time rendering. No precision is lost by truncating to whole weeks.
## Requirements
### Requirement: A Period renders to an exact day-based or week-based dur-value

`TemporalAmountAdapter.toString(seed)` for a `java.time.Period` SHALL measure the number of calendar days between `seed` and `seed.plus(period)` and SHALL render `PnW` with `n = days / 7` when that count is an exact multiple of seven, and `PnD` otherwise. Years and months SHALL NOT be approximated as a fixed number of weeks. A negative period SHALL be rendered with a leading `-` and the absolute day or week count.

#### Scenario: One month that is not a whole number of weeks

- **WHEN** `new TemporalAmountAdapter(Period.ofMonths(1)).toString(LocalDateTime.of(2020, 6, 1, 0, 0))` is called
- **THEN** the result is `P30D`

#### Scenario: One month that is exactly four weeks

- **WHEN** `new TemporalAmountAdapter(Period.ofMonths(1)).toString(LocalDateTime.of(2021, 2, 1, 0, 0))` is called
- **THEN** the result is `P4W`

#### Scenario: One year in a common year and in a leap year

- **WHEN** `Period.ofYears(1)` is rendered from seed `2021-04-01T00:00`
- **THEN** the result is `P365D`
- **WHEN** `Period.ofYears(1)` is rendered from seed `2020-01-01T00:00`
- **THEN** the result is `P366D`

#### Scenario: Six months, positive and negative

- **WHEN** `Period.ofMonths(6)` and `Period.ofMonths(-6)` are rendered from seed `2021-04-01T00:00`
- **THEN** the results are `P183D` (183 days forward) and `-P26W` (182 days back to 1 October 2020, an exact 26 weeks)

#### Scenario: Whole-week day periods keep the week form

- **WHEN** `Period.ofDays(364)` and `Period.ofWeeks(7)` are rendered from any seed
- **THEN** the results are `P52W` and `P7W`

### Requirement: Duration rendering is unchanged

`TemporalAmountAdapter.toString(seed)` for a `java.time.Duration` SHALL continue to render whole calendar days followed by the exact remaining hours, minutes and seconds.

#### Scenario: Twelve days and thirty minutes

- **WHEN** `Duration.ofDays(12).plusMinutes(30)` is rendered from seed `2021-04-01T00:00`
- **THEN** the result is `P12DT30M`

