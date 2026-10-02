# Global zone resolution

## Purpose

Defines how iCal4j makes its bundled timezone definitions available as `java.time` zones for global TZID resolution (`TimeZoneRegistry.getGlobalZoneId`), and how the synthetic zones for registered VTIMEZONE definitions coexist across copies of the library. Covers:
- when and how the bundled-definition `ZoneRulesProvider` is registered: lazily and programmatically, never through `ServiceLoader`;
- fallback to platform zones when registration isn't allowed;
- behaviour that stays the same regardless of class loader;
- synthetic zone IDs that stay unique per copy of iCal4j.

## Requirements

### Requirement: iCal4j SHALL NOT register a zone rules provider through ServiceLoader

The published iCal4j artifact SHALL NOT contain a `META-INF/services/java.time.zone.ZoneRulesProvider` entry, and its module descriptor SHALL NOT declare `provides java.time.zone.ZoneRulesProvider`. Having iCal4j on the classpath or module path SHALL NOT cause the JDK to load any iCal4j class while `java.time` initialises.

#### Scenario: JVM starts with a custom JFR repository

- **WHEN** a JVM is started with iCal4j on the classpath and `-XX:FlightRecorderOptions=repository=<directory>`
- **THEN** the JVM starts and exits normally (exit code 0)
- **AND** no `ServiceConfigurationError` mentioning `ZoneRulesProvider` is reported

#### Scenario: No service entry in the artifact

- **WHEN** the built jar is inspected
- **THEN** it contains no `META-INF/services/java.time.zone.ZoneRulesProvider` resource

### Requirement: The bundled-definition provider SHALL be registered lazily, once, on first global resolution

The system SHALL register its bundled-definition `ZoneRulesProvider` programmatically, at most once per class loader, the first time `TimeZoneRegistry.getGlobalZoneId` is invoked, and not before. Until then, iCal4j SHALL add no zone IDs to `java.time.ZoneId.getAvailableZoneIds()`.

#### Scenario: No ical4j zone IDs before first use

- **WHEN** a fresh JVM with iCal4j on the classpath calls `ZoneId.getAvailableZoneIds()` before using any iCal4j API
- **THEN** the result contains no ID starting with `ical4j~`

#### Scenario: Registration on first global resolution

- **WHEN** `TimeZoneRegistry.getGlobalZoneId("Australia/Melbourne")` is invoked for the first time in a JVM where registration succeeds
- **THEN** it returns a `ZoneId` whose ID starts with `ical4j~`
- **AND** `TimeZoneRegistry.ZONE_IDS` maps that ID to `"Australia/Melbourne"`
- **AND** `ZoneId.getAvailableZoneIds()` now contains that ID

#### Scenario: Registration happens only once

- **WHEN** `getGlobalZoneId` is invoked repeatedly, including concurrently from several threads
- **THEN** the provider is registered exactly once
- **AND** every call for the same TZID returns the same `ZoneId`

### Requirement: Global resolution SHALL be unchanged on a standard JVM

Where registration succeeds, `TimeZoneRegistry.getGlobalZoneId(tzId)` SHALL return the same kind of result as before: an `ical4j~<id>` zone whose rules come from iCal4j's bundled definition for the resolved Olson ID, including resolution through `ZONE_ALIASES`. It SHALL do so regardless of which class loader loaded iCal4j.

#### Scenario: Bundled rules back the resolved zone

- **WHEN** `getGlobalZoneId("America/Los_Angeles")` is invoked where registration succeeded
- **THEN** the returned zone's offset at `2026-07-01T12:00Z` is `-07:00` and at `2026-01-01T12:00Z` is `-08:00`

#### Scenario: Aliases still resolve

- **WHEN** `getGlobalZoneId("Romance Standard Time")` is invoked where registration succeeded
- **THEN** it returns the same zone as `getGlobalZoneId("Europe/Paris")`

#### Scenario: iCal4j loaded by a non-system class loader

- **WHEN** iCal4j's classes are loaded by a class loader other than the system class loader (e.g. a webapp or plugin class loader), and `getGlobalZoneId("Australia/Melbourne")` is invoked through that loader
- **THEN** it returns an `ical4j~` zone backed by the bundled definition, as it does on the system class loader

### Requirement: Failed registration SHALL fall back to platform zones without partial state

If constructing or registering the provider throws a `LinkageError` or `SecurityException`, the system SHALL NOT propagate the error. It SHALL log the failure once at `INFO`, and SHALL NOT publish any `ical4j~` mapping into `TimeZoneRegistry.ZONE_IDS`. `getGlobalZoneId` SHALL then return `ZoneId.of(tzId, ZONE_ALIASES)`, the platform zone.

#### Scenario: Registration is blocked

- **WHEN** provider construction or registration fails with a `LinkageError` (as on Android, which blocks `ZoneRulesProvider` as a hidden API)
- **THEN** `getGlobalZoneId("Europe/Paris")` returns `ZoneId.of("Europe/Paris")` without throwing
- **AND** `TimeZoneRegistry.ZONE_IDS` contains no entries added by the failed attempt

### Requirement: A provider instance SHALL answer from the ID map it was constructed with

`DefaultZoneRulesProvider(TimeZoneLoader, Map<String,String>)` SHALL report, as its zone IDs, exactly the keys it added to the supplied map, and SHALL resolve rules for those IDs using that map. The public no-argument constructor SHALL continue to use `TimeZoneRegistry.ZONE_IDS`.

#### Scenario: Instance with a private map

- **WHEN** a `DefaultZoneRulesProvider` is constructed with a new empty map
- **THEN** its provided zone IDs equal that map's keys and are non-empty
- **AND** provided rules for one of those IDs match the bundled definition of the Olson ID it maps to
- **AND** `TimeZoneRegistry.ZONE_IDS` is not modified by the construction

### Requirement: Synthetic zone IDs SHALL be unique per copy of iCal4j

The synthetic zone IDs that `ZoneRulesProviderImpl` allocates to registered VTIMEZONE definitions SHALL be unique to each provider instance, so that several copies of iCal4j loaded by different class loaders in one JVM can each register their provider. IDs SHALL have the form `ical4j-local-<token>-<n>`, where `<token>` is chosen randomly once per provider instance.

#### Scenario: A second copy of iCal4j registers its own provider

- **WHEN** a second copy of iCal4j is loaded by a separate class loader in a JVM where the first copy has already registered its providers
- **AND** that copy parses a calendar containing a VTIMEZONE and resolves global zones
- **THEN** no `ZoneRulesException` or `ExceptionInInitializerError` occurs
- **AND** the second copy's `ZoneRulesProviderImpl.isAvailable()` is `true`

#### Scenario: Two provider instances allocate disjoint IDs

- **WHEN** two `ZoneRulesProviderImpl` instances are created
- **THEN** no zone ID provided by one is provided by the other
- **AND** each ID matches `ical4j-local-<token>-<n>`
