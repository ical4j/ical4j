# VTIMEZONE zone rules

## Purpose

Defines the contract for `net.fortuna.ical4j.model.ZoneRulesBuilder` — deriving a `java.time.zone.ZoneRules` instance from a `net.fortuna.ical4j.model.component.VTimeZone`. Covers how observance `RRULE`s are translated into future `ZoneOffsetTransitionRule`s, including degenerate rules (e.g. non-DST zones emitted by Apple Calendar) that omit a weekday, which observances are ignored, and the guarantee that derivation does not depend on the JVM default time zone.

## Requirements

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



### Requirement: Future transition rules from BYMONTH-only observance RRULEs

When building `java.time.zone.ZoneRules` from a `VTimeZone`, the system SHALL build a future transition rule for an observance whose `RRULE` specifies a `BYMONTH` part even when the `RRULE` specifies neither a `BYDAY` part nor a `BYMONTHDAY` part. In that case the system SHALL derive the day-of-month from the observance `DTSTART` and produce a fixed-date transition rule (no day-of-week constraint).

The system SHALL NOT throw `IndexOutOfBoundsException` (or otherwise fail registration) when an observance `RRULE` has a `BYMONTH` part but an empty day list.

#### Scenario: Non-DST VTIMEZONE with yearly BYMONTH-only rule registers successfully

- **WHEN** a calendar contains a `VTIMEZONE` with a single `STANDARD` observance whose `DTSTART` is `20000101T000000`, `TZOFFSETFROM:+0900`, `TZOFFSETTO:+0900`, and `RRULE:FREQ=YEARLY;BYMONTH=1` (e.g. Apple Calendar's `Asia/Tokyo`)
- **THEN** `CalendarBuilder.build` parses the calendar without throwing
- **AND** the timezone registers and produces a usable `ZoneRules` instance

#### Scenario: BYMONTH-only rule derives day-of-month from DTSTART

- **WHEN** an observance has an effective `RRULE` with `BYMONTH` set but no `BYDAY` and no `BYMONTHDAY`, and a `DTSTART` whose day-of-month is D
- **THEN** the generated transition rule uses day-of-month D with no day-of-week constraint
- **AND** the rule's month matches the `RRULE` `BYMONTH` value

### Requirement: Skip no-effect transition rules

When building future transition rules, the system SHALL ignore observances whose `TZOFFSETFROM` offset equals its `TZOFFSETTO` offset, since such an observance describes no actual offset change. This mirrors the existing behavior when building historical DST transitions.

#### Scenario: Observance with equal from/to offsets produces no transition rule

- **WHEN** an observance has `TZOFFSETFROM` equal to `TZOFFSETTO`
- **THEN** no `ZoneOffsetTransitionRule` is generated for that observance
- **AND** existing observances with differing offsets continue to generate transition rules unchanged

### Requirement: Existing BYDAY-based transition rules unchanged

The system SHALL continue to build transition rules for observances whose `RRULE` specifies a `BYDAY` part (e.g. `RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU`) exactly as before this change, including handling of ordinal weekday offsets and `BYMONTHDAY` fallback.

#### Scenario: Standard DST rule with BYDAY still produces a weekday transition rule

- **WHEN** an observance has `RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU` and differing from/to offsets
- **THEN** the generated transition rule uses the last Sunday of March with the correct day-of-week constraint
- **AND** the resulting `ZoneRules` matches the behavior prior to this change
