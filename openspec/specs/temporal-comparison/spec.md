# Temporal comparison

## Purpose

Defines the contract for `net.fortuna.ical4j.model.TemporalComparator` — the single `Comparator<Temporal>` used throughout ical4j to order `java.time.Temporal` values regardless of their concrete type. Covers which types are supported, how mixed-type pairs are resolved to a comparable form, and the symmetry guarantee callers can rely on.

## Requirements

### Requirement: TemporalComparator SHALL be sign-antisymmetric for every supported temporal type pair

`TemporalComparator.compare(a, b)` and `TemporalComparator.compare(b, a)` SHALL return values with opposite signs (or both zero) for every pair `(a, b)` drawn from `{Instant, OffsetDateTime, ZonedDateTime, LocalDateTime, LocalDate}`. This is the structural restatement of the `java.util.Comparator` contract.

#### Scenario: Pairwise symmetry for all supported temporal types

- **GIVEN** a `TemporalComparator` instance constructed with any `defaultZoneId`
- **AND** any pair `(a, b)` where both `a` and `b` are instances of `Instant`, `OffsetDateTime`, `ZonedDateTime`, `LocalDateTime`, or `LocalDate` (with any combination of types)
- **WHEN** `compare(a, b)` returns `r1` and `compare(b, a)` returns `r2`
- **THEN** `Math.signum(r1) == -Math.signum(r2)`

### Requirement: TemporalComparator SHALL compare zoned and instant-bearing temporals by their absolute instant

When comparing values that resolve unambiguously to a `java.time.Instant` — `Instant`, `OffsetDateTime`, and `ZonedDateTime` — `TemporalComparator` SHALL order them by their absolute instant, not by their wall-clock fields.

#### Scenario: Same instant across different zones compares equal

- **GIVEN** `instantValue = Instant.parse("2024-06-01T12:00:00Z")`
- **AND** `zonedValue = ZonedDateTime.parse("2024-06-01T22:00:00+10:00[Australia/Sydney]")` (same absolute instant)
- **WHEN** `TemporalComparator.INSTANCE.compare(instantValue, zonedValue)` is invoked
- **THEN** the result is 0

#### Scenario: Same instant compared in reverse order also compares equal

- **GIVEN** the same `instantValue` and `zonedValue`
- **WHEN** `TemporalComparator.INSTANCE.compare(zonedValue, instantValue)` is invoked
- **THEN** the result is 0

#### Scenario: Earlier instant compares before later instant regardless of type

- **GIVEN** `instantValue = Instant.parse("2024-06-01T11:00:00Z")`
- **AND** `zonedValue = ZonedDateTime.parse("2024-06-01T22:00:00+10:00[Australia/Sydney]")` (12:00:00Z; one hour after the instant)
- **WHEN** `TemporalComparator.INSTANCE.compare(instantValue, zonedValue)` is invoked
- **THEN** the result is negative
- **AND** `TemporalComparator.INSTANCE.compare(zonedValue, instantValue)` is positive

#### Scenario: Two ZonedDateTimes at the same instant compare equal regardless of zone

- **GIVEN** `zdtSydney = ZonedDateTime.parse("2024-06-01T22:00:00+10:00[Australia/Sydney]")`
- **AND** `zdtUtc = ZonedDateTime.parse("2024-06-01T12:00:00+00:00[UTC]")`
- **WHEN** `TemporalComparator.INSTANCE.compare(zdtSydney, zdtUtc)` is invoked
- **THEN** the result is 0

### Requirement: TemporalComparator SHALL resolve floating temporals using the configured default zone

When comparing a floating temporal (`LocalDateTime` or `LocalDate`) against a zoned or instant-bearing temporal (`Instant`, `OffsetDateTime`, `ZonedDateTime`), the floating value SHALL be resolved into an instant by interpreting it as a wall-clock time in the comparator's `defaultZoneId`. The resolution SHALL NOT use the zone of the other operand, so that the same floating value resolves consistently regardless of what it is compared against.

#### Scenario: LocalDateTime resolved using defaultZoneId when compared against ZonedDateTime

- **GIVEN** `comparator = new TemporalComparator(ZoneOffset.UTC)`
- **AND** `ldtValue = LocalDateTime.parse("2024-06-01T12:00:00")`
- **AND** `zdtValue = ZonedDateTime.parse("2024-06-01T12:00:00+00:00[UTC]")` (same instant once LDT is resolved in UTC)
- **WHEN** `comparator.compare(ldtValue, zdtValue)` is invoked
- **THEN** the result is 0

#### Scenario: LocalDateTime resolution does not consult the ZonedDateTime's zone

- **GIVEN** `comparator = new TemporalComparator(ZoneOffset.UTC)`
- **AND** `ldtValue = LocalDateTime.parse("2024-06-01T12:00:00")`
- **AND** `zdtSydney = ZonedDateTime.parse("2024-06-01T12:00:00+10:00[Australia/Sydney]")` (02:00:00Z)
- **WHEN** `comparator.compare(ldtValue, zdtSydney)` is invoked
- **THEN** the result is positive (12:00 UTC is after 02:00 UTC)
- **AND** `comparator.compare(zdtSydney, ldtValue)` is negative

### Requirement: TemporalComparator SHALL NOT throw DateTimeException for any pair of supported temporal types

For every pair `(a, b)` drawn from `{Instant, OffsetDateTime, ZonedDateTime, LocalDateTime, LocalDate}`, `TemporalComparator.compare(a, b)` SHALL complete normally and return an `int`. It SHALL NOT propagate a `java.time.DateTimeException` originating from internal conversion of the operands.

#### Scenario: Instant vs ZonedDateTime completes without exception

- **GIVEN** any `Instant` value and any `ZonedDateTime` value
- **WHEN** `TemporalComparator.INSTANCE.compare(instant, zonedDateTime)` is invoked
- **THEN** the call returns a value of type `int` without throwing

#### Scenario: ZonedDateTime vs Instant completes without exception

- **GIVEN** any `ZonedDateTime` value and any `Instant` value
- **WHEN** `TemporalComparator.INSTANCE.compare(zonedDateTime, instant)` is invoked
- **THEN** the call returns a value of type `int` without throwing

#### Scenario: LocalDateTime vs ZonedDateTime completes without exception

- **GIVEN** any `LocalDateTime` value and any `ZonedDateTime` value
- **WHEN** `TemporalComparator.INSTANCE.compare(localDateTime, zonedDateTime)` is invoked
- **THEN** the call returns a value of type `int` without throwing

#### Scenario: ZonedDateTime vs LocalDateTime completes without exception

- **GIVEN** any `ZonedDateTime` value and any `LocalDateTime` value
- **WHEN** `TemporalComparator.INSTANCE.compare(zonedDateTime, localDateTime)` is invoked
- **THEN** the call returns a value of type `int` without throwing

### Requirement: TemporalComparator SHALL expose typed compare(...) overloads for every supported pair

`TemporalComparator` SHALL expose a public typed `compare(...)` method for every ordered pair drawn from `{Instant, OffsetDateTime, ZonedDateTime, LocalDateTime, LocalDate}` so that callers with concrete types can avoid the generic dispatch path and so that future modifications to the `compare(Temporal, Temporal)` dispatch retain a complete set of typed implementations to forward to.

#### Scenario: Typed compare methods are present for every supported pair

- **GIVEN** the `net.fortuna.ical4j.model.TemporalComparator` class
- **THEN** the class declares `compare(T1, T2)` for every pair `(T1, T2)` where both are drawn from `{Instant, OffsetDateTime, ZonedDateTime, LocalDateTime, LocalDate}` (i.e. all 25 ordered pairs)
- **AND** each method returns `int` and is publicly accessible

### Requirement: TemporalComparator.compare(Temporal, Temporal) SHALL dispatch every supported pair to its typed overload

For every ordered pair `(a, b)` drawn from `{Instant, OffsetDateTime, ZonedDateTime, LocalDateTime, LocalDate}`, the generic `compare(Temporal, Temporal)` entry point SHALL invoke the typed `compare(...)` overload for that pair, rather than falling through to the default `ChronoUnit.between` fallback.

#### Scenario: Dispatch reaches the typed overload for ZonedDateTime/ZonedDateTime

- **GIVEN** two `ZonedDateTime` instances `a` and `b`
- **WHEN** `compare((Temporal) a, (Temporal) b)` is invoked
- **THEN** the result equals `compare(a, b)` (the typed overload), where the typed overload returns the sign of `a.toInstant().compareTo(b.toInstant())`

#### Scenario: Dispatch reaches the typed overload for ZonedDateTime/Instant

- **GIVEN** a `ZonedDateTime` `a` and an `Instant` `b`
- **WHEN** `compare((Temporal) a, (Temporal) b)` is invoked
- **THEN** the result equals `compare(a, b)` (the typed overload)
- **AND** the result equals `a.toInstant().compareTo(b)`

#### Scenario: Default fallback is unreachable for supported pairs

- **GIVEN** any ordered pair `(a, b)` where both are drawn from the five supported types
- **WHEN** `compare((Temporal) a, (Temporal) b)` is invoked
- **THEN** the result is computed by a typed overload, not by `defaultComparisonUnit.between(b, a)`
- **AND** in particular, the result is NOT `Integer.MAX_VALUE` or `Integer.MIN_VALUE` solely as an artefact of the fallback clip
