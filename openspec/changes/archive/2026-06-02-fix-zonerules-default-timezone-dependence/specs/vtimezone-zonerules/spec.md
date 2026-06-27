## ADDED Requirements

### Requirement: ZoneRules derivation is independent of the JVM default time zone

The system SHALL derive identical `java.time.zone.ZoneRules` from a given `VTimeZone` regardless of the JVM default time zone (`java.util.TimeZone.getDefault()` / `TimeZones.getDefault()`). In particular, when building historical DST transitions from an observance whose `RRULE` is bounded by a UTC `UNTIL`, the recurrence set SHALL be computed using the observance's own `TZOFFSETFROM` offset rather than the JVM default zone, so that the `UNTIL` comparison is deterministic.

#### Scenario: Asia/Tokyo resolves to +09:00 in any default zone

- **WHEN** `ZoneRules` are built from the registry `Asia/Tokyo` `VTimeZone` (which encodes expired 1948–1951 DST observances)
- **THEN** the offset for any present-day date is `+09:00`
- **AND** the result is the same whether the JVM default zone is `UTC`, `Australia/Sydney`, or any other zone

#### Scenario: Expired historical DST does not leave a zone permanently in daylight time

- **WHEN** an observance defines a DST start via `RRULE ... UNTIL=<UTC instant>` whose final occurrence lies on the `UNTIL` boundary
- **THEN** the built `ZoneRules` either include both the matching DST start and its closing standard-time transition, or neither
- **AND** the zone is not left in a daylight (offset-advanced) state after the last historical transition
