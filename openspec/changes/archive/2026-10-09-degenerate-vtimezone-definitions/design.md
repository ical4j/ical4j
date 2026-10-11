## Context

`DefaultContentHandler.endComponent()` registers every top-level `VTIMEZONE` it parses via `tzRegistry.register(new TimeZone(vtz))`. `TimeZoneRegistryImpl.register(TimeZone, boolean)` stores the definition in `timezones`, then (when the custom `ZoneRulesProvider` is available) calls `new ZoneRulesBuilder().vTimeZone(...).build()` and allocates a synthetic global zone id for the resulting rules. Any `RuntimeException` from the builder propagates out of the content handler and `CalendarParserImpl` wraps it as a `ParserException`, aborting the parse.

Two degenerate inputs reach the builder:

1. **No observances** (#531): `BEGIN:VTIMEZONE / TZID:UTC / END:VTIMEZONE`. `ZoneRulesBuilder.build()` finds neither a current standard nor daylight observance and throws `NullPointerException("VTIMEZONE has no observance applicable to the current time")`.
2. **Observances without `DTSTART`** (#847, #750): `TZID:UTC` with `STANDARD`/`DAYLIGHT` that carry only `TZOFFSETFROM`/`TZOFFSETTO`. `Observance.getLatestOnset` tolerates the missing `DTSTART`, but `ZoneRulesBuilder` calls `getRequiredProperty("DTSTART")` and throws `ConstraintViolationException("Missing required DTSTART")`. Relaxed hints do not help because nothing in this path consults them.

Existing precedent: in fallback mode (provider unavailable, see `openspec/specs/timezone-registry-fallback`) the registry already keeps a definition without zone rules, and `TzId.toZoneId(registry)` already falls through to `TimeZoneRegistry.getGlobalZoneId` when the registry cannot resolve a `TZID`. `VTimeZone.validate()` already reports both degenerate shapes (`ComponentValidator.validateObservances`).

Constraints: `register` is public API (no signature change); the parser must not start validating RFC cardinality in general; strict mode should remain fail-fast for malformed data.

## Goals / Non-Goals

**Goals:**
- An observance-less `VTIMEZONE` never aborts a parse.
- A `VTIMEZONE` whose observances cannot produce zone rules does not abort a parse when either relaxed hint is set.
- In every tolerated case the definition is retained (visible via `getTimeZone`, round-trips in output) and references resolve via the global zone id.
- Strict-mode failures name the `TZID` and the cause.
- Well-formed definitions behave exactly as before.

**Non-Goals:**
- Repairing or synthesising observance data.
- Changing `ZoneRulesBuilder` or `Observance`.
- Deciding whether `VTIMEZONE`s for IANA ids should be ignored altogether (RFC 7809); a separate discussion.

## Decisions

### D1. Guard in `TimeZoneRegistryImpl.register`, not in `DefaultContentHandler`

The guard lives in the registry so that direct callers of `register` (not only the parser) get the same behaviour, and so the "retained without zone rules" state is defined in one place alongside the existing fallback-mode branch.

*Alternative — validate in `DefaultContentHandler.endComponent()` and skip `register` for invalid definitions (cketti's suggestion):* the definition would then be absent from `getTimeZone(id)` and from any registry-driven output, and a second code path (direct `register`) would still throw. Rejected.

### D2. Observance-less definitions are tolerated unconditionally

There is nothing to build rules from, so skipping the build loses no information: the definition is inert whether or not we throw. Keeping it and resolving the `TZID` globally gives the 3.x behaviour DAVx5 relied on, and `VTimeZone.validate()` still reports the missing observance for callers who validate.

*Alternative — gate on relaxed hints too:* would leave #531 failing for strict-mode consumers for no gain, since strict mode cannot produce rules either.

### D3. Unbuildable observances are tolerated only under relaxed hints

Observances that exist but are unusable are genuinely malformed data (RFC 5545 §3.6.5 requires `DTSTART`). Strict mode keeps failing fast, matching the maintainer's stance on #847 and the "we might explore a way to relax" note on #750. Either `KEY_RELAXED_PARSING` or `KEY_RELAXED_VALIDATION` enables tolerance: the reporter in #750 had both set, and the two hints are what consumers of sloppy feeds reach for.

The guard catches `RuntimeException` rather than enumerating `ConstraintViolationException`/`NullPointerException`: the contract is "a definition that cannot produce zone rules does not abort relaxed parsing", and the builder's failure modes are an implementation detail. In strict mode the exception is rethrown wrapped, so no failure is swallowed there.

### D4. Strict-mode failure is an `IllegalArgumentException` naming the `TZID`

The parser wraps any `RuntimeException` as `ParserException` with the line number, so the type is not load-bearing; the message is. It now reads `Unable to build zone rules for VTIMEZONE [UTC]: Missing required DTSTART`, with the original exception as cause.

### D5. Log at WARN

A retained-but-ruleless definition changes how its `TZID` resolves (platform rules instead of the definition's own). That is worth a warning, not debug, so operators can find the feed that produced it.

## Risks / Trade-offs

- [A strict-mode consumer previously relied on an observance-less `VTIMEZONE` failing the parse] → Unlikely: the failure was an unintentional NPE, and `VTimeZone.validate()` still reports the problem.
- [Relaxed-mode consumers now get platform rules for a `TZID` whose definition was discarded] → Exactly what fallback mode already does; documented in the class javadoc and logged at WARN. For `TZID:UTC` (all three reports) the result is identical.
- [Catching `RuntimeException` could hide a builder bug in relaxed mode] → The message and cause are logged; strict mode still surfaces it.

## Migration Plan

Straight fix on a branch off `develop`; no configuration changes. Rollback is a revert of the single commit.

## Open Questions

- None blocking. Whether relaxed mode should also skip zone-rules building for IANA ids entirely (RFC 7809) can be raised separately.
