## 1. Capture the problem in tests first

- [x] 1.1 Add a forked-JVM test: run `${java.home}/bin/java -XX:FlightRecorderOptions=repository=<temp dir> -cp <java.class.path> -version` and assert exit code 0 with no `ServiceConfigurationError` in the output. Confirm it **fails** on current `develop` (the crash reproduced on JDK 17.0.18).
- [x] 1.2 Add a forked-JVM test: a tiny main that prints whether `ZoneId.getAvailableZoneIds()` contains any `ical4j~` ID, then calls `TimeZoneRegistry.getGlobalZoneId("Australia/Melbourne")` and prints the result and the check again. Assert "none before, `ical4j~` after". Record what `develop` does today (expected: IDs present before first use). **Confirmed on develop: `before=true`. 1.1 reproduces the JFR `ServiceConfigurationError` in the fork; 1.3 fails as expected.**
- [x] 1.3 Add a test for the "Instance with a private map" scenario: `provideZoneIds()` equals the supplied map's keys, and constructing doesn't touch `TimeZoneRegistry.ZONE_IDS`. Expected to fail today, because `provideZoneIds()` returns the global map's keys.

## 2. Provider answers from its own map (D4)

- [x] 2.1 `DefaultZoneRulesProvider`: keep the constructor's map in a field, and use it in `provideZoneIds()` and `provideRules()`. The no-argument constructor still passes `TimeZoneRegistry.ZONE_IDS`.

## 3. Lazy, guarded registration (D2, D3, D5)

- [x] 3.1 Add a package-private holder (e.g. `GlobalZoneRules`) with a static initialiser that runs the registration once, via a package-private method `static boolean register(Supplier<? extends ZoneRulesProvider> factory, Map<String,String> privateIds, Map<String,String> publishTo)`:
  - construct the provider through the factory (writing IDs into `privateIds`);
  - register it;
  - on success, `publishTo.putAll(privateIds)` and return `true`;
  - catch `LinkageError`/`SecurityException`, log once at `INFO`, publish nothing, return `false`.

  **Implemented as `GlobalZoneRules.register(Function<Map<String,String>, ? extends ZoneRulesProvider>, Map<String,String> publishTo)`: the method creates the private map itself and passes it to the factory, so "private until registered" is enforced inside it.**
- [x] 3.2 `TimeZoneRegistry.getGlobalZoneId`: replace the `ZoneId.getAvailableZoneIds()` side-effect call with a touch of the holder. Keep the `TimeZoneRegistryImpl` class load that populates `ZONE_ALIASES`.
- [x] 3.3 Test `register(...)` with a factory that throws `LinkageError`: returns `false`, and the publish map stays empty. Test it with a real provider: returns `true`, and the IDs are published and resolvable via `ZoneId.of`.

## 4. Remove ServiceLoader registration (D1)

- [x] 4.1 Delete `src/main/resources/META-INF/services/java.time.zone.ZoneRulesProvider`.
- [x] 4.2 Remove `provides ZoneRulesProvider with DefaultZoneRulesProvider;` from `module-info.java` (and any import only it used).
- [x] 4.3 Confirm tests 1.1 and 1.2 now pass, and that the built jar has no `META-INF/services/java.time.zone.ZoneRulesProvider`.

## 5. Behaviour unchanged on a standard JVM

- [x] 5.1 Existing `getGlobalZoneId` users pass unchanged (`DefaultZoneRulesProviderTest`, `CalendarTest`, `RecurTest`, `DurTest`, `VEventTest`, `VFreeBusyTest`, `RDateTest`, `DatePropertyTest`, `TimeZoneRegistryFallbackTest`).
- [x] 5.2 Add tests: `getGlobalZoneId("America/Los_Angeles")` offsets `-07:00` (July 2026) and `-08:00` (January 2026); `getGlobalZoneId("Romance Standard Time")` equals `getGlobalZoneId("Europe/Paris")`.
- [x] 5.3 Non-system class loader: load iCal4j's classes and resources through a child `URLClassLoader` over the main output directories, call `getGlobalZoneId("Australia/Melbourne")` reflectively, and assert an `ical4j~` result. If this proves impractical in the test harness, verify it manually and record the result here. **BLOCKED (pre-existing issue, not this change): the child copy fails in `ZoneRulesProviderImpl.<clinit>` with `ZoneRulesException: Unable to register zone as one already registered with that ID: ical4j-local-406`. Its synthetic ids are fixed (`ical4j-local-0..N`), so a second copy of iCal4j in the same JVM (two webapps, or a redeploy while the old provider stays pinned) cannot register it. The #916 guard doesn't catch `ZoneRulesException`. `GlobalZoneRules` (random `ical4j~uuid` ids) is unaffected. Resolved by D7 (option 1, per-copy ids): now passes, and fails again if ids are reverted to fixed.**

## 5b. Per-copy synthetic IDs (D7)

- [x] 5b.1 Test: two `ZoneRulesProviderImpl` instances provide disjoint IDs matching `ical4j-local-<token>-<n>`. Expected to fail today (both are `ical4j-local-0..N`).
- [x] 5b.2 `ZoneRulesProviderImpl`: generate pool IDs as `ical4j-local-<token>-<n>`, with `<token>` chosen randomly once per instance. Pool size, allocation and release are unchanged.
- [x] 5b.3 Re-run 5.3 (second copy via child class loader). Extend it so the second copy also parses a calendar with a VTIMEZONE, and assert its `ZoneRulesProviderImpl.isAvailable()` is `true`.

## 6. Documentation

- [x] 6.1 `DefaultZoneRulesProvider` / `TimeZoneRegistry.getGlobalZoneId` javadoc: registration is lazy and guarded, and happens on first global resolution. Note the class-loader pinning alongside the existing note for `ZoneRulesProviderImpl`.
- [x] 6.2 The PR description carries the release-note items: the #808 fix, IDs appearing only after first use, and webapp deployments now using bundled definitions.

## 7. Verify

- [x] 7.1 `./gradlew check` passes (tests + RevAPI). Removing the `provides` clause and services file isn't a Java API change; record any RevAPI finding. **Passed against both the configured baseline (4.1.1) and 4.3.0; no findings.**
- [x] 7.2 Manual: `java -XX:FlightRecorderOptions=repository=<dir> -cp build/libs/ical4j-*.jar -version` starts normally.
- [x] 7.3 `openspec validate lazy-default-zone-rules-provider --strict`.
- [x] 7.4 After merge: comment on #808 with the fix and the snapshot version, mention the `jcmd JFR.configure` workaround for older versions, then close it. **Done 2026-09-27: #921 merged (`ea5c2a8cc`). GitHub auto-closed #808 on merge; the comment (fix, published-snapshot verification under the JFR option, `jcmd JFR.configure` workaround) was posted afterwards.**
