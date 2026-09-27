## 1. Pin down current behaviour

- [x] 1.1 Add a test for the "Behaviour is unchanged where the provider is available" requirement, run against today's code: the divergent `Europe/Paris` calendar (fixed `+05:00`) read with the default registry yields `+05:00`, and `getZoneRules()` has one entry. If today's behaviour differs from the spec, stop and update the spec before continuing. **Done: `TimeZoneRegistryFallbackTest` confirms `+05:00` and one rules entry on today's code. Also found that on a JVM `getGlobalZoneId("Europe/Paris")` returns a bundled `ical4j~<uuid>` zone (339 registered by `DefaultZoneRulesProvider`), so the fallback-resolution scenarios were reworded to compare against `getGlobalZoneId` and offsets instead of a literal `Europe/Paris`.**

## 2. Guarded provider initialisation

- [x] 2.1 In `ZoneRulesProviderImpl`, replace the eager `INSTANCE = new ZoneRulesProviderImpl()` + static `registerProvider` with a guarded static block. Construct into a local, register it, catch `LinkageError` and `SecurityException`, and assign `INSTANCE` the provider on success or `null` on any caught failure (including register-after-construct failure).
- [x] 2.2 Log provider unavailability once at `INFO`, including the exception class and message and a note that platform zones will be used.
- [x] 2.3 Add `static boolean isAvailable()` and `static Optional<ZoneRulesProviderImpl> getInstance()`. Deprecate direct `INSTANCE` use in its javadoc, stating that it is `null` when unavailable.

## 3. Registry fallback

- [x] 3.1 Add a package-private `TimeZoneRegistryImpl(String resourcePrefix, boolean lenientTzResolution, boolean zoneRulesProviderAvailable)`. The existing constructors delegate with `ZoneRulesProviderImpl.isAvailable()`.
- [x] 3.2 In `register(TimeZone, boolean)`, when unavailable, keep storing the definition in `timezones` but skip `ZoneRulesBuilder`, ID allocation and the `zoneRules` entry. When available, keep the current code path and reach the provider via `getInstance()` rather than `INSTANCE`.
- [x] 3.3 In `getZoneId(tzId)`, when unavailable, delegate to `TimeZoneRegistry.getGlobalZoneId(tzId)`. `getTzId` returns `null` naturally, since no IDs are allocated.
- [x] 3.4 Confirm `TzId.toZoneId(registry)` needs no change: `getZoneRules().isEmpty()` routes to the global path.

## 4. Fallback tests (JVM, via the package-private constructor)

- [x] 4.1 Calendar with `VTIMEZONE` `Europe/Paris` + event builds without throwing; `getTimeZone("Europe/Paris")` returns the definition; `getZoneRules()` is empty.
- [x] 4.2 Event `DTSTART;TZID=Europe/Paris` resolves to `TimeZoneRegistry.getGlobalZoneId("Europe/Paris")` with offset `+01:00`; `getZoneId("Europe/Paris")` returns the same zone. (On a JVM that is the bundled `ical4j~<uuid>` zone, not literally `Europe/Paris`; see spec.)
- [x] 4.3 `TZID=Romance Standard Time` resolves to the same zone as `getGlobalZoneId("Europe/Paris")`, through the alias tables.
- [x] 4.4 Divergent `Europe/Paris` definition (fixed `+05:00`): the July event reads `+02:00` (platform rules).
- [x] 4.5 `TZID=My Office Time`: strict mode throws `DateTimeException` on reading `DTSTART`; with `ical4j.validation.relaxed=true`, `DTSTART` parses without the TZID (reset the hint afterwards).
- [x] 4.6 Round trip: a calendar parsed in fallback mode serialises its `VTIMEZONE` and `TZID` parameters unchanged.

## 5. Documentation

- [x] 5.1 `TimeZoneRegistryImpl` class javadoc: describe fallback mode, when it applies, and that custom `VTIMEZONE` rules are not applied in it.
- [x] 5.2 Release notes reference #900, including the nullable `ZoneRulesProviderImpl.INSTANCE` note. **Release notes are generated from PR titles (`release.yml`: `generate_release_notes: true`), and `CHANGELOG.md` hasn't been maintained since 2016. So this is carried by the implementation PR's title ("fix: fall back to platform zones when the zone rules provider can't be installed (Android, #900)") and its description's compatibility note, not a CHANGELOG entry.**

## 6. Verify

- [x] 6.1 `./gradlew check` passes (tests + revapi). If RevAPI flags `ZoneRulesProviderImpl`, record the justification in `.palantir/revapi.yml`. **Passed; RevAPI flagged nothing.**
- [x] 6.2 `openspec validate android-zone-rules-fallback --strict`.
- [ ] 6.3 After merge, comment on #900 asking the reporter to verify the published `develop` snapshot on Android. Close the issue only after that confirmation, or with a note if none arrives.
