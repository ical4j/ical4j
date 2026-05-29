## Context

`DateProperty.setValue(String)` (in `src/main/java/net/fortuna/ical4j/model/property/DateProperty.java`) has a try/catch around the strict-format parse, with a relaxed-parsing fallback:

```java
try {
    if (tzId.isPresent()) {
        this.date = (TemporalAdapter<T>) TemporalAdapter.parse(value, tzId.get(), timeZoneRegistry);
    } else if (defaultTimeZone != null && shouldApplyTimezone()) {
        this.date = (TemporalAdapter<T>) TemporalAdapter.parse(value, defaultTimeZone);
    } else {
        this.date = TemporalAdapter.parse(value, parseFormat);
    }
} catch (DateTimeParseException dtpe) {
    if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
        // parse with relaxed format..
        this.date = tzId.map(id -> (TemporalAdapter<T>) TemporalAdapter.parse(value, id, timeZoneRegistry))
                .orElseGet(() -> TemporalAdapter.parse(value, CalendarDateFormat.DEFAULT_PARSE_FORMAT));
    } else {
        throw dtpe;
    }
}
```

`CalendarDateFormat.DEFAULT_PARSE_FORMAT` is built with three parsers in order:

```java
public static final CalendarDateFormat DEFAULT_PARSE_FORMAT = new CalendarDateFormat(
        "yyyyMMdd['T'HHmmss[X]]", new OffsetDateTimeTemporalQuery(),
        new LocalDateTimeTemporalQuery(), new LocalDateTemporalQuery());
```

The `'T'HHmmss[X]` portion is wrapped in outer brackets — i.e. optional. So `parseBest` will try OffsetDateTime → LocalDateTime → LocalDate and return the most precise match. For `"20240601"` (date-only) all three parsers run; only LocalDate succeeds.

The fallback path is reached identically by every DateProperty subclass — including `DtStamp`, `Created`, `LastModified`, `Completed`, `Acknowledged`, `TzUntil`, and `Trigger`, all of which implement `UtcProperty` and declare their generic type as `Instant`. The `(TemporalAdapter<T>)` cast is unchecked; Java erasure means a `LocalDate` happily slots into a field typed for `Instant`.

### The downstream symptom

`DateProperty.getValue()`:

```java
} else if (this instanceof UtcProperty) {
    return date.toString(ZoneOffset.UTC);
}
```

`TemporalAdapter.toString(zoneId)` for `ZoneOffset.UTC`:

```java
private String toString(T temporal, ZoneId zoneId) {
    if (ZoneOffset.UTC.equals(zoneId)) {
        return toInstantString(temporal);   // UTC_DATE_TIME_FORMAT.format(temporal)
    }
    ...
}
```

`UTC_DATE_TIME_FORMAT` pattern is `"yyyyMMdd'T'HHmmss'Z'"`. Formatting a `LocalDate` against that pattern throws `UnsupportedTemporalTypeException` because `LocalDate` doesn't support `HOUR_OF_DAY`.

### What other non-Instant types do

| Parsed type | `getValue()` for UtcProperty | Symptom |
|-------------|------------------------------|---------|
| `Instant` | Correct UTC string | OK |
| `LocalDate` | **Throws** `UnsupportedTemporalTypeException` | reported bug |
| `LocalDateTime` | Formats wall-clock fields with literal `Z` | silent wrong data (LDT is floating; result claims UTC) |
| `OffsetDateTime` | Formats local fields with literal `Z` | silent wrong data (no offset conversion) |

All three are off-spec for a `UtcProperty`; only the first throws audibly.

### Defect inventory (this change addresses)

| ID | Description | Current location |
|----|-------------|------------------|
| D-1 | `setValue("20240601")` on a UtcProperty succeeds; `getValue()` throws `UnsupportedTemporalTypeException` | `DateProperty.java` catch block + `TemporalAdapter.toInstantString` |
| D-2 | `setValue("20240601T120000")` on a UtcProperty succeeds; `getValue()` returns `"20240601T120000Z"` — claims UTC for a floating value | `DateProperty.java` catch block + `TemporalAdapter` formatter behaviour |
| D-3 | `setValue("20240601T120000+0500")` on a UtcProperty succeeds; `getValue()` returns `"20240601T120000Z"` instead of `"20240601T070000Z"` — drops the offset conversion | `DateProperty.java` catch block + `TemporalAdapter` formatter behaviour |
| D-4 | Type contract `DateProperty<Instant>` is violated by unchecked cast — `TemporalAdapter<LocalDate>` ends up in the field | `DateProperty.java:208` |

## Goals / Non-Goals

**Goals:**
- After `setValue(String)` returns successfully on any `UtcProperty` instance, the internal `date` holds an `Instant` (no `LocalDate`/`LocalDateTime`/`OffsetDateTime`).
- `getValue()` on the resulting property never throws as a consequence of the input shape; it always returns a well-formed UTC date-time string ending in `Z`.
- The semantic coercion is well-defined and documented (which floating values map to which Instants).
- Strict mode (relaxed parsing OFF) is unchanged — strict-format parse errors still propagate.
- The coercion applies uniformly to all seven UtcProperty subclasses without per-subclass override.

**Non-Goals:**
- Replacing `UtcProperty` (interface) with an abstract class hierarchy that enforces the constraint at the type level. Bigger refactor, no clear benefit over the surgical fix.
- Changing the relaxed-parse fallback for non-UtcProperty subclasses. `DtStart`, `DtEnd`, `Due`, `RecurrenceId`, etc. legitimately accept LocalDate/LocalDateTime/OffsetDateTime values.
- Changing the strict format (`UTC_DATE_TIME_FORMAT`) or its parsers.
- Touching `Trigger`'s DURATION code path — the bug is reachable only via the DATE-TIME branch (`super.setValue(aValue)`), which is what gets fixed.
- Spock/Groovy test changes (standing scope rule from `test-framework`).

## Decisions

### D1. Coerce, don't reject

When the catch block produces a non-Instant value for a UtcProperty, the change coerces it to an Instant rather than throwing.

**Rationale:** The whole point of `KEY_RELAXED_PARSING` is to accept off-spec input that strict mode rejects. The project's recent history confirms this stance:
- `feat: tolerate escaped semicolon in parameter values under relaxed parsing` (`954c354b1`)

Rejecting under relaxed mode would defeat the purpose — callers who couldn't change the upstream data source would have no escape. Coercion gives them a well-defined fallback.

**Alternative considered:** Throw `DateTimeParseException` from the catch block when the result type doesn't match the property's declared generic type. Rejected — see above.

### D2. Coercion table

For UtcProperty, the catch block coerces by parsed type:

| Parsed type | Coerced to | How |
|-------------|------------|-----|
| `Instant` | `Instant` | unchanged |
| `OffsetDateTime` | `Instant` | `offsetDateTime.toInstant()` (proper UTC translation) |
| `LocalDateTime` | `Instant` | `localDateTime.atOffset(ZoneOffset.UTC).toInstant()` (treat wall-clock as UTC) |
| `LocalDate` | `Instant` | `localDate.atStartOfDay(ZoneOffset.UTC).toInstant()` (midnight UTC) |

**Rationale for LocalDateTime treatment as UTC wall-clock:** RFC 5545 mandates UtcProperty values "MUST be specified in the UTC time format". When relaxed parsing receives a floating value, the most charitable interpretation is that the caller *meant* UTC but forgot the `Z` suffix. Using the system default zone or some other zone would introduce non-determinism (the same input would coerce to different instants on different machines/JVMs).

**Alternative considered:** Resolve LocalDateTime via the system default zone, matching `DateProperty.setValue`'s strict path for `DtStart`/`DtEnd` (which uses `defaultZoneId`). Rejected — UtcProperty doesn't have a `defaultZoneId` (`setDefaultTimeZone` throws `UnsupportedOperationException`), so there's no natural zone to consult.

**Rationale for LocalDate → midnight UTC:** The only candidate "time of day" for a date-only string is midnight. The only candidate zone is UTC (per the property's UTC contract). This is the unique deterministic coercion.

### D3. Fix site: `DateProperty.setValue`, applied uniformly after parse (whether strict or relaxed fallback)

The coercion is added in `DateProperty.java`, *after* the try/catch block, gated by `this instanceof UtcProperty`. It applies to whatever the parse produced, regardless of which branch ran:

```java
TemporalAdapter<?> parsed;
try {
    if (tzId.isPresent()) {
        parsed = TemporalAdapter.parse(value, tzId.get(), timeZoneRegistry);
    } else if (defaultTimeZone != null && shouldApplyTimezone()) {
        parsed = TemporalAdapter.parse(value, defaultTimeZone);
    } else {
        parsed = TemporalAdapter.parse(value, parseFormat);
    }
} catch (DateTimeParseException dtpe) {
    if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
        parsed = tzId.isPresent()
                ? TemporalAdapter.parse(value, tzId.get(), timeZoneRegistry)
                : TemporalAdapter.parse(value, CalendarDateFormat.DEFAULT_PARSE_FORMAT);
    } else {
        throw dtpe;
    }
}
if (this instanceof UtcProperty) {
    parsed = coerceToInstant(parsed);
}
this.date = (TemporalAdapter<T>) parsed;
```

**Why outside the catch block, not just inside it?** During implementation it became clear that the strict-parse try block also produces non-Instant values for UtcProperty:

- `DtStamp` (under relaxed parsing) uses `parseFormat = RELAXED_DATE_TIME_FORMAT`, whose parsers are `[OffsetDateTime, LocalDateTime]` — neither is `Instant`. `"20240601T120000"` succeeds as a `LocalDateTime`; `"20240601T120000+0500"` succeeds as `OffsetDateTime`; `"20240601T120000Z"` succeeds as `OffsetDateTime` (the `X` pattern matches `Z`).
- For the strict-parse-successful cases, the catch block never runs, so a catch-block-only fix would leave the LocalDateTime/OffsetDateTime in place, with the downstream `getValue()` symptoms documented in D-2/D-3.

Moving the coercion outside the try/catch closes both windows in one place.

Plus a private static helper:

```java
private static TemporalAdapter<Instant> coerceToInstant(TemporalAdapter<?> adapter) {
    Temporal t = adapter.getTemporal();
    if (t instanceof Instant) {
        return (TemporalAdapter<Instant>) adapter;
    } else if (t instanceof OffsetDateTime) {
        return new TemporalAdapter<>(((OffsetDateTime) t).toInstant());
    } else if (t instanceof LocalDateTime) {
        return new TemporalAdapter<>(((LocalDateTime) t).atOffset(ZoneOffset.UTC).toInstant());
    } else if (t instanceof LocalDate) {
        return new TemporalAdapter<>(((LocalDate) t).atStartOfDay(ZoneOffset.UTC).toInstant());
    }
    return new TemporalAdapter<>(Instant.from(t));   // last-resort: any other instant-bearing Temporal
}
```

**Rationale:** Single touch point, applies to every UtcProperty subclass uniformly, no per-class override duplication.

**Alternative considered:** Override `setValue` in each UtcProperty subclass. Rejected on duplication grounds — seven near-identical overrides for behaviour that's inherently shared via the interface marker.

**Alternative considered:** New `CalendarDateFormat.UTC_RELAXED_PARSE_FORMAT` constant (no LocalDate parser) used as the fallback when `this instanceof UtcProperty`. Rejected — would still need post-parse handling for LocalDateTime (the LocalDateTime parser would still match and produce a floating value), so it doesn't fully solve the problem. The coercion approach handles every case.

### D4. The tzId-present branch

The catch block has two sub-paths: one with TZID present, one without. The fix is added to *both* paths (after the parse) so the coercion is uniform. The TZID-present branch is rarely exercised for UtcProperty (the spec doesn't allow TZID on UTC values), but the safety belt is cheap.

### D5. Strict mode is untouched

The catch block only runs after a strict-format parse fails *and* relaxed parsing is enabled. Strict mode (`KEY_RELAXED_PARSING` off) continues to throw `DateTimeParseException` for off-spec input, exactly as before.

### D6. Tests use JUnit 5

Per the `test-framework` capability spec, new tests use JUnit 5 in `src/test/java/`. No new Spock/Groovy tests. A parameterised test covers the seven UtcProperty subclasses.

## Risks / Trade-offs

- **[R1] Coercion fabricates time-of-day data.** A user passing `"20240601"` to DTSTAMP now silently gets `2024-06-01T00:00:00Z`. The original input did not specify "midnight UTC" — the library is inventing that. **Mitigation:** behaviour is gated on `KEY_RELAXED_PARSING`, which is opt-in. Document the coercion table in release notes. Strict mode preserves the original "reject" behaviour for callers who want strictness.
- **[R2] LocalDateTime "treated as UTC" may surprise.** A caller passing `"20240601T120000"` (intended as wall-clock floating) now gets `2024-06-01T12:00:00Z`. For DTSTAMP/CREATED/etc., this matches the RFC's UTC requirement. **Mitigation:** UtcProperty subclasses *should* be in UTC per RFC 5545; the coercion enforces the contract instead of letting wrong data through silently.
- **[R3] OffsetDateTime offset conversion changes the visible string.** `"20240601T120000+0500"` → `"20240601T070000Z"`. Previously, the broken `getValue()` returned `"20240601T120000Z"`. Any caller depending on the broken round-trip would see a different (and now correct) value. **Mitigation:** the old behaviour was off-spec; the new behaviour matches RFC 5545. Document in release notes.
- **[R4] Defect D-2 and D-3 are not the originally-reported bug.** The user reported the LocalDate-throws case (D-1). D-2/D-3 are related defects in the same fallback path. **Mitigation:** fixing only D-1 would leave the other two latent — bad ROI vs. doing the full coercion table now. The scope is contained (single helper method).
- **[R5] Unchecked cast still present.** The fix uses `(TemporalAdapter<T>) parsed` after coercion. For UtcProperty, T=Instant and the cast is safe; for non-UtcProperty the cast is no different from before. The deeper type-safety hole (raw access to `T`) is not in scope to fully close. **Mitigation:** the fix removes the *observable* manifestation of the hole for UtcProperty; the residual issue is a Java generics limitation, not a runtime bug.

## Migration Plan

```
1. Add the private static coerceToInstant(...) helper in DateProperty.java
2. Wire it into the relaxed-parsing catch block, gated by `this instanceof UtcProperty`
3. Add a JUnit 5 test class covering:
   - DTSTAMP with date-only, floating, and offset inputs under relaxed parsing
   - Same coverage for one other UtcProperty subclass (CREATED) for parity
   - Parameterised test over all seven UtcProperty subclasses with the date-only input
   - Strict-mode assertions: same inputs still throw with relaxed OFF
4. Run ./gradlew test — verify no regressions
5. Commit
```

Single commit. Rollback: `git revert`.

## Open Questions

- **Q1**: Should the coercion log a WARN (via the existing `LoggerFactory`) when it kicks in, so callers see something during development even without enabling DEBUG? Default: **no** — the existing catch block already DEBUG-logs `dtpe`; adding WARN risks log spam in production environments where relaxed parsing is the steady-state. Document the coercion table in release notes instead.
- **Q2**: Is there a `Trigger` quirk where the DURATION fallback (`TemporalAmountAdapter.parse(aValue)`) could ALSO accept date-only strings and produce weird results? Default: **no** — `TemporalAmountAdapter.parse` is for ISO duration syntax (`P1D`, `PT1H`), which a date-like string doesn't match. Confirm during implementation by running the parameterised test with Trigger included.
- **Q3**: Should we surface a public utility like `TemporalAdapter.toUtcInstant(Temporal)` for callers who want the same coercion outside of parsing? Default: **no** — speculative API; add only when there's a concrete second caller.
