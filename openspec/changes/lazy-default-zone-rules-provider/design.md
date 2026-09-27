## Context

**How global zone resolution works today:**
- `TimeZoneRegistry.getGlobalZoneId(tzId)` resolves a TZID that has no registry-local definition.
- It calls `ZoneId.getAvailableZoneIds()` purely for its side effect. That forces `ZoneRulesProvider`'s static initialiser to run, which loads `META-INF/services` providers via `ServiceLoader`. iCal4j ships an entry for `DefaultZoneRulesProvider`, so the JDK constructs one.
- That constructor puts `ical4j~<uuid> → <Olson id>` into the public static `TimeZoneRegistry.ZONE_IDS`, for every bundled definition (about 339).
- `getGlobalZoneId` then resolves `ZoneId.of(tzId, ZONE_ALIASES)` and, if `ZONE_IDS` has a mapping for that Olson ID, returns `ZoneId.of("ical4j~<uuid>")` instead. Its rules come from iCal4j's bundled `zoneinfo/` definitions via `provideRules`.
- If `ZONE_IDS` is empty (the provider was never loaded), it returns the plain JDK zone.

**Why the JVM dies under JFR:** `-XX:FlightRecorderOptions=repository=…` makes JFR initialise `java.time` zone rules during VM creation. The ServiceLoader sees the services entry but can't load the class yet. The `ServiceConfigurationError` aborts VM creation (JDK-8359441, open).

**Related code:**
- `ZoneRulesProviderImpl` (synthetic IDs for registry-local VTIMEZONEs) is already registered programmatically, inside a `LinkageError`/`SecurityException` guard added by #916.
- The active change `android-zone-rules-fallback` specifies that where providers can't be installed, global resolution yields platform zones.

**A latent inconsistency:** `DefaultZoneRulesProvider(TimeZoneLoader, Map)` writes IDs into the map it's given. But `provideZoneIds()` and `provideRules()` read the global `TimeZoneRegistry.ZONE_IDS`, so an instance constructed with any other map doesn't answer for its own IDs.

## Goals / Non-Goals

**Goals:**
- The JVM starts with iCal4j on the classpath under any JFR options.
- iCal4j registers no `java.time` provider until its global zone resolution is actually used.
- Global resolution behaves the same regardless of which class loader loaded iCal4j.
- On a standard JVM, global resolution returns the same thing as today: the bundled definition under an `ical4j~` ID.

**Non-Goals:**
- Fixing JDK-8359441, changing `ZoneRulesProviderImpl`, or removing the `ical4j~` mechanism.

## Decisions

### D1. Remove the ServiceLoader registration entirely

Delete `META-INF/services/java.time.zone.ZoneRulesProvider`, and the `provides ZoneRulesProvider with DefaultZoneRulesProvider` clause in `module-info.java`. Leaving either in keeps the JFR crash on its classpath or module-path variant.

**Rejected:** keeping the services file and making `DefaultZoneRulesProvider` "safe to load early". The failure is the JDK's inability to *load the class* at that point, so nothing inside the class can fix it.

### D2. Register lazily through an on-demand holder

Add a package-private holder class, e.g. `GlobalZoneRules`, whose static initialiser performs the one-time registration. `getGlobalZoneId` touches it in place of `ZoneId.getAvailableZoneIds()`. The JVM's class-initialisation rules give once-only, thread-safe initialisation without explicit locking. The existing `Class.forName(TimeZoneRegistryImpl…)` call, which loads `ZONE_ALIASES`, stays as it is.

**Rejected:** registering in `TimeZoneRegistryImpl`'s static initialiser. That's earlier and broader than needed: building any registry would register the global provider even if global resolution is never used.

### D3. Build privately, register, then publish

The holder:
1. creates a fresh `ConcurrentHashMap` and constructs `new DefaultZoneRulesProvider(loader, privateMap)`;
2. calls `ZoneRulesProvider.registerProvider(provider)`;
3. **only on success**, runs `TimeZoneRegistry.ZONE_IDS.putAll(privateMap)`.

This depends on D4: during `registerProvider` the JDK asks the provider for its IDs, so the provider has to answer from `privateMap`.

**Why:** if `ZONE_IDS` were filled before registration succeeded, a failure would leave `getGlobalZoneId` returning `ZoneId.of("ical4j~…")` IDs that no provider serves. Every global lookup would then throw `ZoneRulesException`, which is worse than having no provider at all.

### D4. `DefaultZoneRulesProvider` answers from its own map

Keep the map passed to the constructor as a field. `provideZoneIds()` returns its key set, and `provideRules()` looks IDs up in it. The public no-argument constructor still passes `TimeZoneRegistry.ZONE_IDS`, so an instance created that way behaves exactly as before.

### D5. Guarded, with fallback to platform zones

Wrap construction and registration in a `try` that catches `LinkageError` and `SecurityException`, as `ZoneRulesProviderImpl` does. On failure: log once at `INFO`, publish nothing, and leave `ZONE_IDS` empty. `getGlobalZoneId` then returns `ZoneId.of(tzId, ZONE_ALIASES)`, the platform zone. That's the behaviour Android already gets, and what the `android-zone-rules-fallback` change specifies.

`ZoneRulesException` from `registerProvider` (duplicate IDs) isn't expected, since the IDs are random UUIDs. It isn't caught, so a genuine problem stays visible.

### D6. Verify the JFR fix with a forked JVM

The crash only happens during VM creation, so the test has to start a new JVM:
- run `${java.home}/bin/java -XX:FlightRecorderOptions=repository=<temp dir> -cp <test runtime classpath> <tiny main or -version>`;
- assert exit code 0 and that the output has no `ServiceConfigurationError`.

Use the same forked approach to assert that `ZoneId.getAvailableZoneIds()` contains no `ical4j~` IDs in a fresh JVM until `getGlobalZoneId` is called. In the shared test JVM, other tests will already have triggered registration.

### D7. Per-copy prefix for `ZoneRulesProviderImpl`'s synthetic IDs

**Found during implementation (task 5.3):** loading a second copy of iCal4j in a child class loader failed in `ZoneRulesProviderImpl.<clinit>` with `ZoneRulesException: Unable to register zone as one already registered with that ID: ical4j-local-406`.
- **Cause:** its pool IDs are fixed strings (`ical4j-local-0` … `ical4j-local-<pool size - 1>`), while the JDK's provider registry is global to the JVM. The first copy claims them, and every later copy's registration throws.
- **Why it isn't caught:** the #916 guard only catches `LinkageError`/`SecurityException`, so the failure escapes as `ExceptionInInitializerError`, and that copy can't register VTIMEZONEs.
- **Who is affected:** two webapps each bundling iCal4j, and a webapp redeploy (the old copy's provider stays pinned).

**Decision:** generate the IDs as `ical4j-local-<token>-<n>`, where `<token>` is a short random value chosen once per `ZoneRulesProviderImpl` instance, e.g. 8 hex characters from a `UUID`. Each copy then has a disjoint ID set and registers independently, keeping full VTIMEZONE support. Pool size, allocation and release are unchanged.

**Rejected:**
- **Also catching `ZoneRulesException`,** so a second copy runs in fallback mode. It avoids the crash, but that copy silently loses VTIMEZONE fidelity for a problem that's easy to prevent.
- **A deterministic per-class-loader token** (e.g. an identity hash). Collisions stay possible, and there's no need for determinism: the IDs are internal and short-lived.

## Risks / Trade-offs

- **[Class-loader pinning]** Registering from a webapp class loader keeps it reachable from the JDK's static provider registry, so redeploys leak it. → `ZoneRulesProviderImpl` already has the same property (#834), so this is a second pinned provider, not a new kind of leak. Document it next to the existing note.
- **[Webapp behaviour change]** Where iCal4j wasn't on the system class loader, global resolution used JDK rules and will now use iCal4j's bundled rules, as standalone apps always have. → It's a consistency fix, but visible if bundled and JDK tzdata versions differ. Call it out in the release notes.
- **[Timing]** `ical4j~` IDs appear only after first use. Only code that enumerates `ZoneId.getAvailableZoneIds()` expecting them before touching iCal4j would notice. They're random per JVM, so nothing can depend on specific values.
- **[Synthetic ID format change]** `ical4j-local-N` becomes `ical4j-local-<token>-N`. → The IDs are allocated per registration, differ between runs, and aren't part of any public contract. Only code that pattern-matched them would notice.
- **[Forked-JVM tests]** Slower and more environment-sensitive. → Keep it to two small forks. Use `java.home` from the running JVM, and the test runtime classpath from `java.class.path`.

## Migration Plan

No migration. Roll back by reverting the change, which restores the services file.

## Open Questions

- Should `DefaultZoneRulesProvider`'s public no-argument constructor be deprecated, since the library no longer needs it? Keeping it costs nothing, so this proposal leaves it.
