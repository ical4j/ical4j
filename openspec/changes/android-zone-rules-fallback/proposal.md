## Why

On Android, the default `TimeZoneRegistryImpl` cannot handle a calendar that contains a `VTIMEZONE` (issue #900). For each `VTIMEZONE`, `register()` allocates a synthetic zone ID from `ZoneRulesProviderImpl`. That class subclasses `java.time.zone.ZoneRulesProvider` and calls `ZoneRulesProvider.registerProvider()` in its static initializer. Android treats both the `ZoneRulesProvider` constructor and `registerProvider` as blocked hidden APIs (https://issuetracker.google.com/issues/159421054). The reporter's logcat shows both being denied.

Today, Android apps have to write their own `TimeZoneRegistry`, as Nextcloud and DAVx5 have done. The library should work out of the box on a platform where people already use it; #871 was an earlier Android-compatibility fix from an outside contributor.

## What Changes

- Initialise the custom `ZoneRulesProvider` defensively. If constructing or registering it fails with a `LinkageError` or `SecurityException`, record that the provider is **unavailable** instead of propagating an `ExceptionInInitializerError`. Log it once, at `INFO`.
- Expose that availability so the registry can check it without touching the provider class when it is unusable.
- When the provider is unavailable, `TimeZoneRegistryImpl.register()` still stores the `VTIMEZONE`, so `getTimeZone()` and calendar output are unaffected. It skips synthetic `ical4j-local-N` ID allocation and builds no `ZoneRules`.
- With no registry-local rules, TZID resolution falls through to the existing global path, `TimeZoneRegistry.getGlobalZoneId`. That resolves against platform (system) zones and already maps Olson aliases and Microsoft/Windows zone names via `ZONE_ALIASES`.
- A TZID with no platform equivalent follows the existing unresolvable-TZID policy: `DateTimeException`, or ignoring the TZID under relaxed validation. No new policy is introduced.
- Add a package-private test hook that forces "provider unavailable", so the fallback path is tested on the JVM.
- Document the fallback: custom `VTIMEZONE` rules are **not** applied, and the platform zone of the same (or aliased) name is used instead.

**Behaviour change (fallback only):** where the provider is unavailable, a `VTIMEZONE` whose rules differ from the platform zone of the same name is ignored. On platforms where the provider works (every standard JVM), behaviour is unchanged.

## Non-goals

- **An explicit opt-in registry** (option 1 in the #900 review: a separate `TimeZoneRegistry` selected via `net.fortuna.ical4j.timezone.registry`). It could follow later, e.g. to force system zones on the JVM, but it isn't needed to make Android work.
- **Matching a custom `VTIMEZONE` to the closest platform zone by comparing rules.** That's possible, but costly and heuristic. Deferred until there's evidence it's needed.
- **Changing `DefaultZoneRulesProvider`**, which is discovered by the JDK through `META-INF/services`. Whether Android's `java.time` performs that ServiceLoader lookup is outside ical4j's control. If it never loads, `ZONE_IDS` stays empty and global resolution already falls back to `ZoneId.of`.

## Capabilities

### New Capabilities
- `timezone-registry-fallback`: how `TimeZoneRegistryImpl` registers `VTIMEZONE` definitions and resolves TZIDs when the `java.time` zone-rules provider cannot be installed, including the preserved behaviour where it can.

### Modified Capabilities
<!-- None. vtimezone-zonerules covers deriving ZoneRules from a VTIMEZONE, which is unchanged; this change only decides whether those rules get registered. -->

## Impact

- **Code:** `net.fortuna.ical4j.model.ZoneRulesProviderImpl` (guarded initialisation, availability), `TimeZoneRegistryImpl.register()` and `getZoneId()`, plus a new small holder or accessor so availability can be checked without initialising a `ZoneRulesProvider` subclass.
- **Public API:** `ZoneRulesProviderImpl.INSTANCE` is a `public static final` field. Construction must be guarded, so it can't stay an eagerly constructed non-null field. The options and their compatibility cost are in design.md. RevAPI will flag any change, and it must be justified.
- **Dependencies:** none.
- **Platforms:** Android (the target) and any environment that restricts `ZoneRulesProvider` registration. Standard JVMs should see no difference.
