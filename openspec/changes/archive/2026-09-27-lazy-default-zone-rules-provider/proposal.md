## Why

With iCal4j on the classpath, the JVM **fails to start** if Java Flight Recorder is given a custom repository (issue #808):

```
$ java -XX:FlightRecorderOptions=repository=/tmp/jfr -cp ical4j-4.4.0-develop-SNAPSHOT.jar -version
[error][jfr,startup] java.time.zone.ZoneRulesProvider: Provider net.fortuna.ical4j.model.DefaultZoneRulesProvider not found
java.util.ServiceConfigurationError: ...
Error occurred during initialization of VM
```

Reproduced on JDK 17.0.18 with current `develop`.

**Why it happens:** JFR initialises `java.time` zone rules during VM startup. The JDK's `ServiceLoader` finds iCal4j's `META-INF/services/java.time.zone.ZoneRulesProvider` entry, can't load the class that early, and aborts the VM.

**It's a JDK bug,** JDK-8359441, open since June 2025 with no fix in sight. But it only affects libraries that register a `ZoneRulesProvider` through `ServiceLoader`. Removing that one services entry from the jar makes the JVM start normally.

**The ServiceLoader registration also has two costs unrelated to JFR:**
- **A JVM-wide side effect.** Merely having iCal4j on the classpath adds about 339 `ical4j~<uuid>` zone IDs to `ZoneId.getAvailableZoneIds()` for the whole JVM, whether or not the application uses iCal4j.
- **Behaviour that depends on deployment.** The JDK looks up `ZoneRulesProvider` services on the **system** class loader only. With iCal4j in a webapp's `WEB-INF/lib`, `DefaultZoneRulesProvider` is never loaded, and global zone resolution silently falls back to plain `ZoneId.of`. That's a different result from the same code running standalone.

## What Changes

- **Stop registering `DefaultZoneRulesProvider` through `ServiceLoader`.** Remove `META-INF/services/java.time.zone.ZoneRulesProvider`, and the `provides ZoneRulesProvider with DefaultZoneRulesProvider` clause in `module-info.java`.
- **Register it programmatically, lazily and once,** on first use of global zone resolution (`TimeZoneRegistry.getGlobalZoneId`). This replaces the current `ZoneId.getAvailableZoneIds()` call that exists only to trigger the ServiceLoader.
- **Guard registration** against `LinkageError` and `SecurityException`, the same way `ZoneRulesProviderImpl` is guarded (#916). If it fails, global resolution falls back to `ZoneId.of`, as it already does wherever the provider isn't loaded, e.g. on Android.
- **Publish the `ical4j~` ID mappings only after registration succeeds,** so a failed registration never leaves `ZONE_IDS` pointing at IDs no provider can resolve.
- **Make `DefaultZoneRulesProvider` answer from the map it was constructed with.** Today `provideZoneIds()` and `provideRules()` read the global `TimeZoneRegistry.ZONE_IDS` even when the two-argument constructor was given a different map.

- **Give `ZoneRulesProviderImpl`'s synthetic zone IDs a per-copy prefix** (`ical4j-local-<token>-N` instead of `ical4j-local-N`). Found while implementing this change: the IDs are currently fixed, so a second copy of iCal4j in the same JVM can't register its provider (`ZoneRulesException: … already registered with that ID: ical4j-local-406`). That copy then fails with `ExceptionInInitializerError` and can't handle VTIMEZONEs. It affects two webapps that each bundle iCal4j, and a webapp redeploy, where the old copy's provider stays pinned. A per-copy prefix lets every copy register independently.

**Behaviour changes:**
- `ical4j~` zone IDs appear in `ZoneId.getAvailableZoneIds()` only after iCal4j's global zone resolution is first used, not as soon as `java.time` initialises.
- In deployments where iCal4j isn't on the system class loader (webapps, plugin containers), global resolution now returns iCal4j's bundled definitions like everywhere else, instead of the JDK's.
- Synthetic zone IDs for registered VTIMEZONEs change format from `ical4j-local-N` to `ical4j-local-<token>-N`. The IDs were never stable or meaningful, but code that pattern-matched them would notice. A second copy of iCal4j in the same JVM now works instead of failing.

## Non-goals

- Fixing JDK-8359441 itself.
- Changing how `ZoneRulesProviderImpl` is registered (it already registers programmatically). Only its ID format changes.
- Changing *what* global resolution returns on a standard JVM. It's still the bundled definition under an `ical4j~<uuid>` ID.
- Removing the `ical4j~` mechanism, or changing the public `TimeZoneRegistry.ZONE_IDS` map.

## Capabilities

### New Capabilities
- `global-zone-resolution`: how iCal4j makes its bundled timezone definitions available as `java.time` zones for global TZID resolution. Covers when and how the provider is registered, what happens when it can't be, and the guarantee that iCal4j doesn't register providers as a side effect of being on the classpath.

### Modified Capabilities
<!-- None. The related timezone-registry-fallback spec is still an active change (android-zone-rules-fallback), not a main spec; its "global resolution" behaviour on restricted platforms is consistent with this change. -->

## Impact

- **Code:**
  - remove `src/main/resources/META-INF/services/java.time.zone.ZoneRulesProvider`;
  - `src/main/java/module-info.java` (drop the `provides` clause);
  - `TimeZoneRegistry.getGlobalZoneId`;
  - `DefaultZoneRulesProvider` (use the instance map);
  - a small package-private holder that performs the one-time guarded registration;
  - `ZoneRulesProviderImpl` (per-copy ID prefix).
- **Public API:** no signature changes. `DefaultZoneRulesProvider` and its constructors stay public.
- **JPMS:** the module no longer `provides ZoneRulesProvider`. Code that discovered iCal4j's provider through `ServiceLoader.load(ZoneRulesProvider.class)` won't find it, which is unlikely outside the JDK itself.
- **Risk:** registering from a non-system class loader pins that loader in the JDK's global provider registry until the JVM exits, so a redeployed webapp leaks one class loader per deploy. `ZoneRulesProviderImpl` already does this (see #834), so it adds a second pinned provider rather than a new kind of leak.
- **Workaround until released:** set the JFR repository at runtime with `jcmd <pid> JFR.configure repositorypath=…` instead of `-XX:FlightRecorderOptions=repository=…`.
