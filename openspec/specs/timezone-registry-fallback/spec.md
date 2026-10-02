# Timezone registry fallback

## Purpose

Defines how `net.fortuna.ical4j.model.TimeZoneRegistryImpl` and `ZoneRulesProviderImpl` behave when the custom `java.time.zone.ZoneRulesProvider` cannot be installed (e.g. Android hidden-API enforcement throws `LinkageError`). Covers one-shot detection of provider unavailability, retention of parsed `VTIMEZONE` definitions, resolution of `TZID`s to platform zones via `TimeZoneRegistry.getGlobalZoneId`, the limitation that custom `VTIMEZONE` rules are not applied in fallback mode, handling of unresolvable `TZID`s, and the guarantee that behaviour is unchanged where the provider is available.

## Requirements

### Requirement: Zone-rules provider unavailability is detected, not fatal

The system SHALL attempt to construct and register its custom `java.time.zone.ZoneRulesProvider` at most once. If construction or registration throws a `LinkageError` or `SecurityException`, the system SHALL record the provider as unavailable instead of propagating the error. It SHALL log this once at `INFO`, and SHALL NOT fail class initialisation or calendar parsing because of it.

`ZoneRulesProviderImpl.isAvailable()` SHALL report whether the provider was installed. `ZoneRulesProviderImpl.getInstance()` SHALL return the installed provider, or an empty `Optional` when it is unavailable. `ZoneRulesProviderImpl.INSTANCE` SHALL be `null` when the provider is unavailable, including when construction succeeded but registration failed.

#### Scenario: Provider cannot be installed

- **WHEN** constructing or registering the provider throws a `LinkageError` (as Android's hidden-API enforcement does)
- **THEN** no `ExceptionInInitializerError` or `NoClassDefFoundError` reaches the caller
- **AND** `ZoneRulesProviderImpl.isAvailable()` returns `false`
- **AND** `ZoneRulesProviderImpl.getInstance()` is empty and `ZoneRulesProviderImpl.INSTANCE` is `null`

#### Scenario: Provider installs normally

- **WHEN** the provider is constructed and registered without error (any standard JVM)
- **THEN** `ZoneRulesProviderImpl.isAvailable()` returns `true`
- **AND** `ZoneRulesProviderImpl.getInstance()` contains `ZoneRulesProviderImpl.INSTANCE`

### Requirement: VTIMEZONE definitions are kept when the provider is unavailable

When the provider is unavailable, `TimeZoneRegistryImpl.register(TimeZone)` SHALL store the definition, so that `getTimeZone(id)` returns it. It SHALL NOT allocate a synthetic zone ID, and SHALL NOT add any entry to `getZoneRules()`.

#### Scenario: Calendar with a VTIMEZONE parses in fallback mode

- **WHEN** a calendar containing `VTIMEZONE` `TZID:Europe/Paris` and a `VEVENT` with `DTSTART;TZID=Europe/Paris:20260315T100000` is built with a `TimeZoneRegistryImpl` whose provider is unavailable
- **THEN** `CalendarBuilder.build` completes without throwing
- **AND** the registry's `getTimeZone("Europe/Paris")` returns the parsed definition
- **AND** the registry's `getZoneRules()` is empty

#### Scenario: Calendar output is unchanged in fallback mode

- **WHEN** a calendar parsed in fallback mode is serialised again
- **THEN** its `VTIMEZONE` component and `TZID` parameters are emitted as parsed

### Requirement: TZIDs resolve to platform zones when the provider is unavailable

When the provider is unavailable, the system SHALL resolve a `TZID` exactly as it resolves a `TZID` with no registered definition, via `TimeZoneRegistry.getGlobalZoneId`: an Olson ID directly, or a name mapped by the timezone alias tables, including Microsoft/Windows zone names. `TimeZoneRegistryImpl.getZoneId(tzId)` SHALL return that same `ZoneId`, rather than failing because no synthetic ID was allocated.

The resulting `ZoneId` depends on the platform. Where ical4j's bundled `DefaultZoneRulesProvider` is not loaded (e.g. Android), it is the platform zone, such as `ZoneId.of("Europe/Paris")`. On a standard JVM, that provider is loaded through `META-INF/services` and global resolution returns its `ical4j~<uuid>` zone for the same Olson ID. Scenarios therefore compare against `getGlobalZoneId` and against offsets, not against a literal zone ID.

#### Scenario: Olson TZID resolves to the platform zone

- **WHEN** the event above is read in fallback mode
- **THEN** its `DTSTART` is a `ZonedDateTime` at local time `2026-03-15T10:00` in the zone `TimeZoneRegistry.getGlobalZoneId("Europe/Paris")`
- **AND** its offset is `+01:00`, Paris standard time

#### Scenario: Windows zone name resolves through the alias tables

- **WHEN** an event has `DTSTART;TZID=Romance Standard Time:20260315T100000` and is read in fallback mode
- **THEN** its `DTSTART` resolves to the same zone as `TimeZoneRegistry.getGlobalZoneId("Europe/Paris")`

#### Scenario: Registry lookup agrees with property resolution

- **WHEN** `getZoneId("Europe/Paris")` is called on a fallback-mode registry that has registered a `Europe/Paris` definition
- **THEN** it returns `TimeZoneRegistry.getGlobalZoneId("Europe/Paris")`, which is `ZoneId.of("Europe/Paris")` where the bundled provider is not loaded

### Requirement: Custom VTIMEZONE rules are not applied in fallback mode

When the provider is unavailable, the system SHALL use the platform zone's rules for a `TZID` even if the calendar's `VTIMEZONE` for that `TZID` defines different offsets or transitions. This limitation SHALL be documented in the `TimeZoneRegistryImpl` javadoc.

#### Scenario: Platform rules win over a divergent definition

- **WHEN** a calendar defines `VTIMEZONE` `TZID:Europe/Paris` with a fixed `+05:00` offset and no daylight observance, and an event at `DTSTART;TZID=Europe/Paris:20260715T100000` is read in fallback mode
- **THEN** the event's offset is the platform's `Europe/Paris` summer offset, `+02:00`, not `+05:00`

### Requirement: Unresolvable TZIDs in fallback mode follow the existing policy

When the provider is unavailable and a `TZID` matches no platform zone and no alias, the system SHALL apply the same handling as any unresolvable `TZID`: propagate the `DateTimeException` in strict mode, or ignore the `TZID` when relaxed validation is enabled. It SHALL NOT substitute UTC or the JVM default zone.

#### Scenario: Custom-named zone in strict mode

- **WHEN** a calendar defines `VTIMEZONE` `TZID:My Office Time` and an event uses `DTSTART;TZID=My Office Time:20260315T100000`, and it is read in fallback mode with relaxed validation disabled
- **THEN** reading the event's `DTSTART` value throws a `DateTimeException`

#### Scenario: Custom-named zone with relaxed validation

- **WHEN** the same calendar is read in fallback mode with `ical4j.validation.relaxed=true`
- **THEN** the event's `DTSTART` is parsed without the `TZID`, as for any unresolvable `TZID` under relaxed validation

### Requirement: Behaviour is unchanged where the provider is available

When the provider is available, `TimeZoneRegistryImpl` SHALL behave exactly as it did before this capability was introduced. It SHALL allocate a synthetic zone ID and build `ZoneRules` for each registered definition, and `TZID` resolution SHALL use those rules.

#### Scenario: Custom rules apply on a standard JVM

- **WHEN** the divergent `Europe/Paris` calendar above (fixed `+05:00`) is read with the default registry on a standard JVM
- **THEN** the event's offset is `+05:00`, taken from the calendar's own `VTIMEZONE`
- **AND** the registry's `getZoneRules()` contains one entry for that definition
