## Why

A degenerate `VTIMEZONE` aborts parsing of the entire calendar. `TimeZoneRegistryImpl.register` builds `java.time.zone.ZoneRules` from every parsed definition, and `ZoneRulesBuilder.build()` throws when the definition has no `STANDARD`/`DAYLIGHT` observance (issue #531, hit by DAVx5 in 2026) or when an observance lacks `DTSTART` (issues #847 and #750, the natuurhuisje.nl feed, which also failed with every relaxed hint enabled). The exception surfaces as a `ParserException`, so a calendar whose events don't even reference the broken `TZID` cannot be read at all. ical4j 3.x tolerated both inputs.

## What Changes

- A `VTIMEZONE` with no `STANDARD` or `DAYLIGHT` observance is registered without zone rules in every mode: the definition stays available from `getTimeZone(id)` and in calendar output, no synthetic zone id is allocated, and a `TZID` referencing it resolves via `TimeZoneRegistry.getGlobalZoneId` (the same path used in fallback mode and for TZIDs with no definition). A warning is logged.
- A `VTIMEZONE` whose observances cannot be turned into zone rules (e.g. observance without `DTSTART`, or no observance applicable to the current time) is handled the same way when `ical4j.parsing.relaxed` or `ical4j.validation.relaxed` is enabled. In strict mode registration still fails, but with an `IllegalArgumentException` naming the `TZID` and the underlying cause instead of a bare `NullPointerException`/`ConstraintViolationException`.
- `TimeZoneRegistryImpl` javadoc documents the behaviour; `VTimeZone.validate()` remains the place where the RFC 5545 §3.6.5 cardinality violations are reported (it already flags both cases).
- No public signature changes. Strict-mode output for well-formed definitions is unchanged.

Out of scope: auto-repairing observances (e.g. inventing a `DTSTART`); applying RFC 7809-style "ignore VTIMEZONE for IANA ids" wholesale; changing `ZoneRulesBuilder` itself.

## Capabilities

### New Capabilities
<!-- none -->

### Modified Capabilities
- `timezone-registry-fallback`: adds requirements for retaining degenerate definitions without zone rules (observance-less definitions unconditionally; unbuildable definitions under relaxed hints) and for the strict-mode failure message.

## Impact

- `src/main/java/net/fortuna/ical4j/model/TimeZoneRegistryImpl.java` — `register(TimeZone, boolean)` only, plus class javadoc.
- `src/test/groovy/net/fortuna/ical4j/model/DegenerateVTimeZoneTest.groovy` — new regression cases for #531, #847/#750 (relaxed and strict), direct registration, and a well-formed control.
- Consumers parsing feeds with empty `VTIMEZONE`s (DAVx5) now succeed in any mode; consumers with relaxed hints now succeed on observances without `DTSTART`. Strict-mode consumers see a clearer error for the latter.
- No dependency or build changes. RevAPI: no API-shape change expected.
- Fixes #531, #847, #750.
