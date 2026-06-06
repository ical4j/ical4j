## Why

A `DTSTART;TZID=Unknown:20260605T120000` parses without error but throws `java.time.zone.ZoneRulesException: Unknown time zone ID: Unknown` the first time the value is touched — `getValue()`, `getDate()`, `getTemporal()`, `toString()`, or any comparison. The build succeeds because `TemporalAdapter` defers zone resolution; the failure surfaces later, far from the malformed input, as an uncaught runtime exception.

Two defects make this sharp:

1. **`TemporalAdapter.getTemporal()` catches the wrong exception type.** It has a relaxed-validation fallback (parse as a floating `LocalDateTime` and ignore the TZID) at `TemporalAdapter.java:119-126`, but the `catch` clause catches `DateTimeParseException`. An unresolvable zone throws `ZoneRulesException`, which extends `DateTimeException` and is a **sibling** of `DateTimeParseException` — so the intended escape hatch never fires. Even with `KEY_RELAXED_VALIDATION` enabled, the value still throws.

   ```
           DateTimeException
           ├── DateTimeParseException   ← what the catch catches
           └── ZoneRulesException       ← what TzId.toZoneId() actually throws
   ```

2. **`DateProperty.getValue()` re-resolves the zone eagerly, with no guard** (`DateProperty.java:253`): `return date.toString(tzId.get().toZoneId(timeZoneRegistry));`. The argument `tzId.get().toZoneId(...)` throws before `toString` is ever called, and there is no try/catch here at all — so this path throws unconditionally, independent of any compatibility hint.

The net effect: the relaxed-validation tolerance for unresolvable TZIDs that the code clearly *intends* to provide does not actually work, on either call site.

## What Changes

Behaviour is gated on `CompatibilityHints.KEY_RELAXED_VALIDATION` (the hint the existing fallback block already keys off — reused for consistency):

- **Relaxed validation ON + unresolvable TZID** → the value is interpreted as a **floating** `LocalDateTime`; the TZID is ignored. `getValue()` returns the floating representation (e.g. `"20260605T120000"`). No exception.
- **Relaxed validation OFF (default)** → a `DateTimeException` (the existing `ZoneRulesException`) is propagated, consistently, from every access path. Strict mode stays strict; the contract is now defined rather than incidental.

Concretely:

- **`TemporalAdapter.getTemporal()`**: broaden the relaxed-fallback `catch` from `DateTimeParseException` to `DateTimeException` so an unresolvable zone (`ZoneRulesException`) is caught and falls back to `CalendarDateFormat.DEFAULT_PARSE_FORMAT.parse(valueString)` (a floating `LocalDateTime`). In strict mode it rethrows, unchanged.
- **`DateProperty.getValue()` and `DateProperty.getDate()`**: each wraps its eager `tzId.toZoneId(...)` resolution in a try/catch. On `DateTimeException`, if relaxed validation is enabled, `getValue()` returns `Strings.valueOf(date)` and `getDate()` returns `date.getTemporal()` (both the already-parsed floating `LocalDateTime`); otherwise rethrow. `getDate()` was found during implementation to be a *third* eager-resolution site (it does not delegate to `getValue()`), so it is guarded independently.

The three call sites converge on the same rule, so `getValue()`, `getDate()`, `getTemporal()`, and `toString()` agree.

Tolerance applies to *value access*, not to *validation*:

- **`DateProperty.validate()`**: reports an ERROR-severity `ValidationEntry` when the property carries a `TZID` (and a timezone applies) that resolves to no known zone — **regardless** of the relaxed-validation hint. Reading the value is lenient; the calendar is still flagged structurally invalid. This was surfaced during implementation: an existing fixture (`samples/invalid/talios.ics`, whose only defect is an unresolvable TZID) was classified "invalid" solely because value access *threw* during validation. With value access no longer throwing under relaxed validation, the explicit validation error preserves its invalid classification.

## Capabilities

### Modified Capabilities

- `date-property-parsing`: Add requirements defining how a TZID that resolves to no known zone is handled — floating fallback under relaxed validation, defined exception under strict. Existing requirements (UtcProperty coercion, strict UTC rejection, non-UtcProperty preservation) are unchanged.

### New Capabilities

<!-- None — this extends the existing date-property-parsing capability. -->

## Impact

- **Affected code**:
  - `src/main/java/net/fortuna/ical4j/model/TemporalAdapter.java` — broaden the relaxed-fallback catch in `getTemporal()` from `DateTimeParseException` to `DateTimeException`.
  - `src/main/java/net/fortuna/ical4j/model/property/DateProperty.java` — guard the eager `toZoneId(...)` resolution in `getValue()` and `getDate()` with a relaxed-aware try/catch; and add an unresolvable-TZID error to `validate()`.
- **Behavioural note (not a default-behaviour change)**: with no hints set (the default), an unresolvable TZID still throws on value access — that is the chosen strict contract. The reporter's scenario (`getValue()` returning `"20260605T120000"`) requires `KEY_RELAXED_VALIDATION` to be enabled. The fix makes that relaxed path actually function (today it throws even when the hint is on) and makes the strict path consistent across all access methods.
- **Validation classification preserved**: `DateProperty.validate()` now emits an unresolvable-TZID error regardless of the hint, so `samples/invalid/talios.ics` stays in the `invalid/` suite (`CalendarBuilderTest.testBuildInvalid` keeps passing) even though its value is now readable. Previously that file was classified invalid only because value access threw during validation.
- **Test coverage**:
  - New test: relaxed validation ON, `DTSTART;TZID=Unknown:...` → `getValue()` returns the floating value, `getDate()` returns a `LocalDateTime`, no exception.
  - New test: relaxed validation OFF (default), same input → `getValue()`/`getDate()` propagate a `DateTimeException`.
  - New test: relaxed validation ON, same input → `calendar.validate().hasErrors()` is true (validation flags the TZID though the value is readable).
  - Regression: a *resolvable* TZID (e.g. `Australia/Melbourne` with a VTIMEZONE) is unaffected — zoned value, no validation error — in both modes.
- **Scope guard**: hints are global mutable state — tests MUST enable/clear `KEY_RELAXED_VALIDATION` in try/finally (or @BeforeEach/@AfterEach) to avoid leaking into other tests.
- **No change to**: `TzId.toZoneId()` (it cannot express "floating" — it must return a `ZoneId` — so the floating decision stays at the call sites), the registry lookup order, or `UtcProperty` handling (those never apply a TZID).
- **Coverage**: must remain ≥ 0.7 (existing `jacocoTestCoverageVerification` rule).
- **Spock/Groovy tests**: untouched (standing scope rule from `test-framework` spec).

## Non-goals

- Changing the *default* (strict) behaviour to tolerate unknown TZIDs. The decision is explicit: tolerance is opt-in via `KEY_RELAXED_VALIDATION`.
- Introducing a new compatibility hint or a new exception type. The existing `KEY_RELAXED_VALIDATION` and the existing `ZoneRulesException`/`DateTimeException` are reused.
- De-duplicating the independent zone-resolution call sites (`getTemporal()`, `getValue()`, `getDate()`) into one. That is a larger refactor; this change keeps them and makes them consistent (the "both call sites, minimal" scope — implementation found a third site, `getDate()`).
- Gating the validation error on the relaxed-validation hint. The unresolvable-TZID error is reported in both modes: value access is lenient, but a calendar referencing an undefined timezone is structurally invalid either way.
