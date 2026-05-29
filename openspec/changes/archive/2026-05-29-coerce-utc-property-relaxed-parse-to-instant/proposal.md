## Why

When `CompatibilityHints.KEY_RELAXED_PARSING` is enabled, `DateProperty.setValue(String)` falls back to `CalendarDateFormat.DEFAULT_PARSE_FORMAT` after the strict format fails. That fallback format is built with parsers for `OffsetDateTime`, `LocalDateTime`, **and `LocalDate`** — so a date-only string like `"20240601"` is silently accepted, even when the receiving property is a `UtcProperty` (DTSTAMP, CREATED, LAST-MODIFIED, COMPLETED, ACKNOWLEDGED, TZUNTIL, TRIGGER).

The result violates the generic type contract: `DateProperty<Instant>` ends up holding a `TemporalAdapter<LocalDate>` via an unchecked cast (`DateProperty.java:208`). Java erasure hides the type mismatch at runtime — until the property is read back:

```
DtStamp dtStamp = new DtStamp("20240601");   // relaxed parsing on; silent LocalDate
dtStamp.getValue();                          // → toInstantString(localDate)
                                             // → UTC_DATE_TIME_FORMAT.format(localDate)
                                             // → UnsupportedTemporalTypeException
```

The same path is reachable for `LocalDateTime` (`"20240601T120000"` — no `Z`) and `OffsetDateTime` (`"20240601T120000+0500"`), with subtler symptoms: `getValue()` doesn't throw, but the rendered UTC string is wrong (a LocalDateTime's wall-clock fields are formatted as if they were UTC; an OffsetDateTime's local fields are formatted without offset conversion). The result is data that *looks* valid but isn't.

The fix needs to ensure that whatever the relaxed-parse fallback accepts, a `UtcProperty`'s internal `TemporalAdapter` always holds an `Instant` — matching the property's declared generic type and the RFC 5545 requirement that DTSTAMP/CREATED/etc. values "MUST be specified in the UTC time format".

## What Changes

- **Coerce non-Instant results to Instant for `UtcProperty` in the relaxed fallback path**. In `DateProperty.setValue(String)`'s catch block, when relaxed parsing is enabled and the receiving instance is a `UtcProperty`, the parsed temporal is normalised to an `Instant` before being stored:
  - `LocalDate` → `atStartOfDay(ZoneOffset.UTC).toInstant()`
  - `LocalDateTime` → `atOffset(ZoneOffset.UTC).toInstant()` (treats the wall-clock as UTC, matching RFC 5545's UTC contract for these properties)
  - `OffsetDateTime` → `toInstant()` (proper offset conversion)
  - `Instant` → returned unchanged
- **Strict mode (relaxed parsing OFF) is untouched.** The strict format already rejects these inputs; behaviour is unchanged for callers who don't opt in to relaxed parsing.
- **New tests** asserting:
  - DTSTAMP constructed with a date-only string under relaxed parsing returns a non-null Instant and a `getValue()` ending in `Z`.
  - The same for one representative property per UtcProperty subclass (DTSTAMP, CREATED, LAST-MODIFIED, COMPLETED, ACKNOWLEDGED, TZUNTIL, TRIGGER).
  - LocalDateTime input (`"20240601T120000"`) and OffsetDateTime input (`"20240601T120000+0500"`) under relaxed parsing produce the expected UTC instant (with the offset value converting to `20240601T070000Z`).
  - Strict parsing of the same inputs still throws (no behaviour change when relaxed is off).
- **No public API removal or signature change.** `UtcProperty` remains an interface marker. `DateProperty.setValue` keeps its signature. No new public classes or methods.

## Capabilities

### New Capabilities

- `date-property-parsing`: Defines how `DateProperty` and its subclasses parse string values into temporal types, including the relaxed-parsing fallback behaviour for `UtcProperty` subclasses.

### Modified Capabilities

<!-- None — this is the first capability for date property string parsing. -->

## Impact

- **Affected code**:
  - `src/main/java/net/fortuna/ical4j/model/property/DateProperty.java` — catch block in `setValue(String)` gains a `UtcProperty` check + coercion helper (private static method, ~10 lines).
- **Affected behaviour**:
  - Under relaxed parsing, `UtcProperty` subclasses now accept date-only, floating, and offset date-time strings without throwing — coercing each to a UTC `Instant`.
  - Previously, date-only strings parsed successfully but then threw on `getValue()`; LocalDateTime strings parsed and `getValue()` returned semantically-wrong "UTC" strings; OffsetDateTime strings parsed and `getValue()` lost the offset conversion. All three now produce a well-defined Instant.
  - No change for non-UtcProperty subclasses (DTSTART, DTEND, DUE, RECURRENCE-ID, etc. retain existing relaxed-parse semantics).
  - No change in strict mode.
- **Tests**:
  - New test file at `src/test/java/net/fortuna/ical4j/model/property/UtcPropertyRelaxedParseTest.java` (or extend an existing relaxed-parsing test file if one fits).
  - Tests use JUnit 5 (project convention from the `test-framework` capability).
  - Parameterised test over the seven UtcProperty subclasses to verify uniform behaviour.
- **Coverage**: must remain ≥0.7. The change adds tests; coverage should not regress.
- **No public API removal.** No deprecations.
- **Bug-fix nature**: this is a correctness fix. Callers who happened to avoid the broken inputs see no change; callers who hit them previously got an exception or wrong UTC string and now get a correct, RFC-conformant Instant value.
- **Out of scope**:
  - Rewriting `UtcProperty` as an abstract class with a constrained `setValue` (bigger refactor; surgical fix preferred).
  - Touching strict-mode behaviour (no behavioural shift desired there).
  - Changing how non-UtcProperty subclasses handle the relaxed fallback (they have legitimate use cases for LocalDate/LocalDateTime results).
  - Spock/Groovy test framework changes (standing scope rule from `test-framework` spec).
