## Context

`DateProperty` parses lazily. `PropertyBuilder` calls `setValue(String)`, which (when a TZID is present) stores a `TemporalAdapter` holding only `(valueString, tzId, timeZoneRegistry)` — the zone is **not** resolved yet (`DateProperty.java:201-202`, `TemporalAdapter.java:104-108`). Resolution happens lazily inside `TemporalAdapter.getTemporal()`:

```java
// TemporalAdapter.getTemporal()  (lines 115-127)
if (tzId != null) {
    try {
        temporal = (T) CalendarDateFormat.FLOATING_DATE_TIME_FORMAT.parse(valueString,
                tzId.toZoneId(timeZoneRegistry));        // <-- toZoneId throws here
    } catch (DateTimeParseException e) {                  // <-- sibling of the thrown type
        if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            temporal = (T) CalendarDateFormat.DEFAULT_PARSE_FORMAT.parse(valueString);  // floating
        } else {
            throw e;
        }
    }
}
```

`TzId.toZoneId()` (lines 82-92) tries the registry first (throws `DateTimeException` "Unknown timezone identifier", caught internally), then falls through to `TimeZoneRegistry.getGlobalZoneId()`, which calls `ZoneId.of("Unknown", ZONE_ALIASES)` — that throws **`java.time.zone.ZoneRulesException`** (a `DateTimeException`, **not** a `DateTimeParseException`).

So the `catch (DateTimeParseException)` above never matches an unresolvable-zone failure. The relaxed fallback is dead code for this case.

Separately, `DateProperty` resolves the zone again — eagerly, with no guard — in **two** more places: `getValue()` and `getDate()`:

```java
// DateProperty.getValue()  (line 255)
if (tzId.isPresent() && shouldApplyTimezone()) {
    return date.toString(tzId.get().toZoneId(timeZoneRegistry));  // arg throws before toString
}

// DateProperty.getDate()  (line 161)
if (tzId.isPresent() && shouldApplyTimezone()) {
    return (T) date.toLocalTime(tzId.get().toZoneId(timeZoneRegistry));  // arg throws before toLocalTime
}
```

> **Implementation note:** the initial analysis counted only `getTemporal()` + `getValue()`. Implementation surfaced `getDate()` (line 161) as a *third* eager-resolution site with the identical pattern — `getDate()` does not delegate to `getValue()`, so it must be guarded independently. The spec scenarios already required `getDate()` to behave correctly in both modes, so the contract was right; only this design's call-site count needed correcting.

## Goals / Non-Goals

**Goals:**
- Make the intended relaxed-validation tolerance for unresolvable TZIDs actually work.
- Make every access path (`getValue`, `getDate`, `getTemporal`, `toString`) agree: floating under relaxed, defined exception under strict.
- Keep strict mode (the default) strict.

**Non-Goals:**
- No new compatibility hint, no new exception type.
- No change to the default (strict) outcome for an unknown TZID — it still throws.
- No unification of the (three) zone-resolution call sites; keep them, make them consistent (minimal scope).
- No change to `TzId.toZoneId()`, registry lookup order, or `UtcProperty` handling.

## Decisions

**Decision: Gate tolerance on `KEY_RELAXED_VALIDATION` (not `KEY_RELAXED_PARSING`).**

The existing dead fallback block already keys off `KEY_RELAXED_VALIDATION`. Reusing it keeps both call sites consistent and avoids a second knob. An unresolvable TZID is a well-formed value with an unknown *reference* — closer to a validation concern than a lexical parse concern — which also fits `KEY_RELAXED_VALIDATION`.

**Decision: Broaden the `getTemporal()` catch to `DateTimeException`.**

`ZoneRulesException extends DateTimeException`. Catching `DateTimeException` covers both the unresolvable-zone case and the original `DateTimeParseException` case (a `DateTimeParseException` is still a `DateTimeException`), so no existing relaxed behaviour regresses. Under relaxed validation the fallback parses `valueString` with `DEFAULT_PARSE_FORMAT`, yielding a floating `LocalDateTime` with the TZID dropped. Under strict mode it rethrows — unchanged outcome.

**Decision: Guard `getValue()` and `getDate()` symmetrically rather than reusing the temporal's zone.**

The smallest consistent fix wraps the `toZoneId(...)` resolution in each method. `getValue()`:

```java
if (tzId.isPresent() && shouldApplyTimezone()) {
    try {
        return date.toString(tzId.get().toZoneId(timeZoneRegistry));
    } catch (DateTimeException e) {
        if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            return Strings.valueOf(date);   // date is the floating LocalDateTime from getTemporal()
        }
        throw e;
    }
}
```

`getDate()` is guarded the same way, returning `date.getTemporal()` (the already-parsed floating `LocalDateTime`) in the relaxed branch:

```java
if (tzId.isPresent() && shouldApplyTimezone()) {
    try {
        return (T) date.toLocalTime(tzId.get().toZoneId(timeZoneRegistry));
    } catch (DateTimeException e) {
        if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            return date.getTemporal();
        }
        throw e;
    }
}
```

Under relaxed validation, `date.getTemporal()` has already fallen back to a floating `LocalDateTime` (same hint, same path), so `Strings.valueOf(date)` emits the floating form `"20260605T120000"` and `getDate()` returns the `LocalDateTime` — matching the reporter's expectation. Deriving the zone *from* the already-parsed temporal instead would also work and would remove the duplicate resolution, but that is the larger "de-duplicate" scope explicitly deferred; this keeps the blast radius to these methods.

**Decision: Strict-mode contract is "propagates a `DateTimeException`."**

The spec asserts the *type hierarchy* (`DateTimeException`), not the concrete `ZoneRulesException` subclass, so the registry/global-zone implementation can evolve without breaking the contract.

## Risks / Trade-offs

- **Silent zone loss under relaxed validation**: the user's stated (unknown) timezone intent is dropped and the value round-trips out as floating. This is the accepted cost of relaxed tolerance and is opt-in.
- **Broadened catch swallows more**: catching `DateTimeException` in `getTemporal()` is wider than before, but only inside the relaxed branch, where tolerance is the explicit intent; strict mode still rethrows everything.
