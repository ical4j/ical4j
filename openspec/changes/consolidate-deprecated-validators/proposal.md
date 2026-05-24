## Why

Six `@Deprecated` classes under `src/main/java/net/fortuna/ical4j/validate/component/` are still load-bearing despite their annotation:

- `VEventValidator`, `VToDoValidator`, `VTimeZoneValidator` carry real per-component validation logic (alarm handling, STATUS allowed-values, observance presence)
- `VAvailabilityValidator`, `VFreeBusyValidator`, `AvailableValidator` are trivial pass-throughs that add nothing

The previous OpenSpec change `rfc-validation-audit` planned to delete all six but discovered during implementation that the first three carry ~120 lines of non-trivial logic that needs to land somewhere before the files can go. That change deferred the deletion explicitly and left this as the natural follow-up.

Keeping `@Deprecated` classes around — actively used, with no migration path documented — sends the wrong signal to consumers reading the code. Either they should be undeprecated (load-bearing classes shouldn't be marked deprecated) or they should be deleted (with the logic ported).

## What Changes

- **Delete trivial pass-throughs** with no logic to port: `VAvailabilityValidator`, `VFreeBusyValidator`, `AvailableValidator`. Their only call sites currently are pass-through wrappers around `ComponentValidator.<COMPONENT>` or `target.validate()`. None of those call sites exist in production code — they were references-only-from-themselves at the rfc-validation-audit audit, and remain so today.
- **Port alarm-validation helper** from `VEventValidator` / `VToDoValidator` into a new public static helper, e.g. `ComponentValidator.validateAlarms(ComponentContainer<?>, boolean alarmsAllowed)`. This replaces the duplicated block currently in both deprecated subclasses.
- **Port VTODO STATUS allowed-values check** from `VToDoValidator` into the registry-side VTODO validators (so each iTIP method's rule set carries it where appropriate). Alternative: a separate `ComponentValidator.validateVToDoStatus(VToDo)` helper that any caller can invoke.
- **Port VTIMEZONE observance handling** from `VTimeZoneValidator` into the registry's VTIMEZONE validator (currently a method-agnostic single entry under the null-key). The "at least one STANDARD or DAYLIGHT" check and per-observance `OBSERVANCE_ITIP` validation move into that validator's body.
- **Update `ITIPRuleRegistry`** to construct rule sets using `new ComponentValidator<>(...)` directly with the ported helpers wired in, rather than the deprecated subclasses.
- **Update `VEvent.validate()`, `VToDo.validate()`, `VTimeZone.validate()`** (the no-method paths) to call the new helpers instead of constructing the deprecated subclasses inline. `VTimeZone.java` line ~165 still does `new VTimeZoneValidator().validate(this)`.
- **Delete the six `@Deprecated validate/component/*Validator.java` files** once the registry and model classes no longer reference them.
- **BREAKING (deprecated API removal)**: external code that referenced any of the six `@Deprecated` validator subclasses by name will fail to compile after this change. They have been `@Deprecated` for ≥2 minor releases; downstream code should already be on the canonical `ComponentValidator.<COMPONENT>` static instances. Documented in release notes.

## Capabilities

### Modified Capabilities

- `calendar-validation`: Add scenarios covering the helper extractions and the canonical home for alarm/STATUS/observance checks. The behavioural contract is unchanged (same checks run, same errors produced); the change is structural.

### New Capabilities

<!-- None — this is structural cleanup over the existing capability. -->

## Impact

- **Affected code**:
  - `src/main/java/net/fortuna/ical4j/validate/component/` — all six files deleted
  - `src/main/java/net/fortuna/ical4j/validate/ComponentValidator.java` — new `validateAlarms(...)`, `validateVToDoStatus(...)`, and `validateObservances(...)` helpers (or equivalent grouping)
  - `src/main/java/net/fortuna/ical4j/validate/ITIPRuleRegistry.java` — replace `new VEventValidator(...)` / `new VToDoValidator(...)` / `new VTimeZoneValidator()` with `new ComponentValidator<>(...)` plus helper invocations
  - `src/main/java/net/fortuna/ical4j/model/component/VEvent.java`, `VToDo.java`, `VTimeZone.java` — the no-method `validate()` path no longer references the deprecated subclasses
- **Test coverage**: existing `validate` tests cover the underlying assertions already. Add a small test that all six deprecated files are absent from `src/main/java/net/fortuna/ical4j/validate/component/` and that the helpers produce equivalent results to the deleted validators.
- **Public API removal (breaking)**: the six classes named above. They have been `@Deprecated` since at least 2021 (per file headers); this is the canonical removal window. No internal callers remain after the rfc-validation-audit refactor, so the only impact is external consumers.
- **Spock/Groovy tests**: untouched. Standing scope rule from `test-framework` spec.
- **No behavioural change at runtime**: the same checks run, in the same order, producing the same `ValidationResult` entries. This is a refactor with deletion, not a feature change.
- **Coverage**: must remain ≥0.7 (existing jacocoTestCoverageVerification rule).
- **Out of scope (continuations of the broader audit, still not in this change)**:
  - F6 cross-property constraint vocabulary
  - F7 systematic row-by-row RFC 5546 §3.x/§4.x table audit
  - F8 property value-set enum constraints (CLASS, TRANSP, STATUS per-component beyond VTODO, PARTSTAT, ROLE, CUTYPE)
