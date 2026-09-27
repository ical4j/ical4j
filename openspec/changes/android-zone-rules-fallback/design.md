## Context

ical4j turns every parsed `VTIMEZONE` into a `java.time.ZoneId`, so date-times can be real `ZonedDateTime`s with that definition's rules.

**How it works today:**
- `TimeZoneRegistryImpl.register()` builds `ZoneRules` from the `VTIMEZONE` (`ZoneRulesBuilder`).
- It then allocates a synthetic ID such as `ical4j-local-17` from `ZoneRulesProviderImpl.INSTANCE.getZoneIdPool()` and stores `id → rules`.
- `ZoneRulesProviderImpl` extends `java.time.zone.ZoneRulesProvider`, registers itself in its static initializer, and answers `provideRules(id)` from the registry that owns the ID. That's why `ZoneId.of("ical4j-local-17")` works.
- `TzId.toZoneId(registry)` asks the registry first when `registry.getZoneRules()` is non-empty. Otherwise, or on `DateTimeException`, it uses `TimeZoneRegistry.getGlobalZoneId`: `ZoneId.of(tzId, ZONE_ALIASES)`, preferring an `ical4j~<uuid>` ID from `ZONE_IDS` if the ServiceLoader-registered `DefaultZoneRulesProvider` populated one.
- `ZONE_ALIASES` is filled in `TimeZoneRegistryImpl`'s static initializer from `tz.alias`, `msTimezoneNames` and `msTimezoneIds`, so Windows zone names resolve to Olson IDs on the global path.
- `TemporalAdapter.getTemporal()` already defines what happens when a TZID resolves to no zone: rethrow the `DateTimeException`, or, under `KEY_RELAXED_VALIDATION`, parse without the TZID.

**The Android constraint:** Android's `java.time` blocks `ZoneRulesProvider.<init>()` and `ZoneRulesProvider.registerProvider(...)` as hidden APIs. The reporter's logcat (#900) shows both denied. So on Android the failure happens when `ZoneRulesProviderImpl` is constructed (the `super()` call), before `registerProvider` is even reached. It surfaces the first time `register()` touches `ZoneRulesProviderImpl.INSTANCE`, as an `ExceptionInInitializerError` (later calls get `NoClassDefFoundError`).

## Goals / Non-Goals

**Goals:**
- The default registry parses and resolves calendars on platforms where the provider can't be installed.
- Behaviour on standard JVMs is identical to today.
- The fallback is testable on the JVM.

**Non-Goals:**
- A separate opt-in registry (option 1 in #900). This is a possible follow-up.
- Honouring custom `VTIMEZONE` rules without a provider. `java.time` has no public way to build a region `ZoneId` with custom rules, so that's impossible.
- Best-match mapping of custom `VTIMEZONE`s to platform zones by comparing rules.

## Decisions

### D1. Detect unavailability by trying, not by platform sniffing

Construct and register the provider inside a guard that catches `LinkageError` (which covers `NoSuchMethodError` and `IllegalAccessError`, what Android's hidden-API enforcement throws) and `SecurityException`. If either is thrown, the provider is unavailable. Log it once at `INFO`, naming the cause and noting that platform zones will be used.

**Rejected:** checking `java.vm.name` / `java.vendor` for Dalvik/Android. It's fragile across Android versions and misses other restricted environments. Trying the operation is the only reliable test.

### D2. Keep `ZoneRulesProviderImpl.INSTANCE`, but allow it to be null; add accessors

`INSTANCE` is `public static final` and public API, so removing it or changing its type would be a RevAPI break. Instead, initialise it in a guarded static block:

- construct into a local variable, then call `registerProvider`;
- if either step throws a caught error, `INSTANCE = null`;
- if construction succeeds but registration fails, still set it to `null`: an unregistered provider is useless.

Add `static boolean isAvailable()` and `static Optional<ZoneRulesProviderImpl> getInstance()`, and deprecate direct use of `INSTANCE` in its javadoc.

Loading a subclass of `ZoneRulesProvider` isn't what Android blocks; calling its blocked members is. So guarded static initialisation of this class is safe on Android.

**Rejected:** a separate availability class that never loads `ZoneRulesProviderImpl`. That's slightly more isolated, but it adds a type without a demonstrated need, given class loading itself isn't blocked. Revisit if Android testing (see Risks) shows linking of the subclass fails.

**Compatibility:** binary-compatible, but `INSTANCE` can now be `null` on restricted platforms. External code that dereferences it directly would get an NPE there instead of an `ExceptionInInitializerError`. Either way it fails, and the javadoc and release notes say so. RevAPI shouldn't flag a nullability-only change. If it flags anything, justify it in `.palantir/revapi.yml`.

### D3. In fallback mode the registry stores definitions but registers no rules

`TimeZoneRegistryImpl` gets a `zoneRulesProviderAvailable` flag, read from `ZoneRulesProviderImpl.isAvailable()` by the public constructors (D5 covers tests). When the flag is false:

- `register()` still puts the `TimeZone` in `timezones`, so `getTimeZone(id)` returns it and a calendar round-trips unchanged. It doesn't build `ZoneRules` and doesn't allocate an ID.
- `getZoneRules()` therefore stays empty, and `TzId.toZoneId(registry)` falls through to `getGlobalZoneId`. No change to `TzId` is needed.
- `getZoneId(tzId)` delegates to `TimeZoneRegistry.getGlobalZoneId(tzId)` rather than throwing "Unknown timezone identifier". Callers that ask the registry directly then get the same answer as the `TzId` path.
- `getTzId(zoneId)` returns `null`, since no synthetic IDs exist.

**Rejected:** building the `ZoneRules` anyway and keeping them in a private map. With no provider nothing can consume them through `ZoneId`, so it would be work and memory for no effect.

### D4. TZIDs with no platform equivalent use the existing policy

In fallback mode, a TZID that isn't an Olson ID and isn't in `ZONE_ALIASES` (e.g. a custom name like `My Office Time`) goes through the path `TemporalAdapter` already has:
- strict mode: the `DateTimeException` (`ZoneRulesException`) propagates;
- relaxed validation: the TZID is ignored and the value is parsed without it.

**Rejected:** silently mapping to UTC or the JVM default zone. It hides data errors in strict mode, and the relaxed-mode behaviour already exists and is specified.

### D5. Test through a package-private constructor, not global state

Add a package-private `TimeZoneRegistryImpl(String resourcePrefix, boolean lenientTzResolution, boolean zoneRulesProviderAvailable)`. The public constructors pass `ZoneRulesProviderImpl.isAvailable()`. Tests build a registry with `false` and hand it to the existing `new CalendarBuilder(TimeZoneRegistry)` constructor, so the whole parse → resolve path runs in fallback mode on a normal JVM.

**Rejected:** a static "force unavailable" switch. It leaks between tests running in the same JVM, and `INSTANCE`, which is `final`, can't be reset.

The guard in D1 itself (the static block catching `LinkageError`) can't be triggered on a JVM without bytecode tricks. It's small enough to review by eye, and it'll be confirmed on a real device (see Risks).

## Risks / Trade-offs

- **[Silent rule substitution]** A `VTIMEZONE` named after an Olson zone but with different rules resolves to the platform's rules in fallback mode. → Log once at `INFO` when fallback is active. Document it in the `TimeZoneRegistryImpl` javadoc and the release notes. This matches what the Nextcloud and DAVx5 workarounds already accept.
- **[Can't run Android in CI]** The actual failure mode (hidden-API linkage errors) isn't reproducible on the JVM. → The D5 tests cover everything after detection. Ask the #900 reporter to verify with the published snapshot (Central Portal snapshot publishing is live) before closing the issue.
- **[`DefaultZoneRulesProvider` via ServiceLoader]** If Android's `java.time` did try to load providers from `META-INF/services`, construction would fail inside platform code, beyond this change's guard. → Evidence suggests it doesn't: the reporter's log only shows ical4j's own call sites. If Android testing shows otherwise, that's a follow-up.
- **[Nullable `INSTANCE`]** Third-party code using `ZoneRulesProviderImpl.INSTANCE` directly could NPE on restricted platforms. → Deprecate it in favour of `getInstance()`/`isAvailable()`. It already failed on those platforms, just differently.

## Migration Plan

No migration needed. No configuration changes, and JVM behaviour is unchanged. To roll back, revert the change.

## Open Questions

- **Duplicate registration:** `registerProvider` throws `ZoneRulesException` if the zone IDs are already registered, e.g. ical4j loaded twice by separate class loaders in a servlet container. Should that also fall back rather than fail? It's plausible, but it's a different problem from #900 and needs its own evidence. It's deliberately **not** caught here.
- **Forcing fallback by configuration:** should a system property (e.g. `net.fortuna.ical4j.timezone.provider.enabled=false`) force fallback on a JVM, for apps that want platform zones everywhere? It's cheap to add, but it overlaps with option 1. Decide alongside that follow-up.
