## Context

After the `rfc-validation-audit` change landed, the iTIP rule registry `src/main/java/net/fortuna/ical4j/validate/ITIPRuleRegistry.java` consolidates per-(component, method) rule definitions in one place. But three of the six `@Deprecated` `validate/component/*Validator.java` classes still carry real validation logic that's instantiated by the registry:

```
ITIPRuleRegistry.java
  └─ vevent.put(PUBLISH/REQUEST/CANCEL/…,
       new VEventValidator(…rules))      ← carries alarm-validation
  └─ vtodo.put(PUBLISH/REQUEST/CANCEL/…,
       new VToDoValidator(…rules))       ← carries alarm-validation + STATUS check
  └─ vtimezone.put(null,
       new VTimeZoneValidator())         ← carries observance-validation
```

Plus the no-method path:

```
VEvent.validate()  ← currently delegates to ComponentValidator.VEVENT
                     (does NOT include alarm-validation — gap?)
VToDo.validate()   ← same shape, no STATUS check
VTimeZone.validate()
  → new VTimeZoneValidator().validate(this)  ← does include observance validation
```

The asymmetry: the iTIP path picks up alarm-validation via the deprecated subclasses, but the no-method path doesn't. (This may itself be a latent bug — to verify in the audit step.)

### Defect inventory (this change addresses)

| ID | Description | Current location |
|----|-------------|------------------|
| D-1 | Alarm-validation logic duplicated between `VEventValidator` and `VToDoValidator` and inaccessible elsewhere | `validate/component/{VEvent,VToDo}Validator.java` |
| D-2 | VTODO STATUS allowed-value check (only DRAFT/NEEDS-ACTION/etc.) only fires through the deprecated `VToDoValidator` | `validate/component/VToDoValidator.java` |
| D-3 | VTIMEZONE observance presence (STANDARD/DAYLIGHT) and per-observance `OBSERVANCE_ITIP` only fires through the deprecated `VTimeZoneValidator` | `validate/component/VTimeZoneValidator.java` |
| D-4 | Three `@Deprecated` files (`VAvailability`, `VFreeBusy`, `Available`) are pure pass-throughs — pure code rot | `validate/component/` |
| D-5 | The `@Deprecated` annotation is misleading: these classes are load-bearing | `validate/component/` (all six) |

### Trivial cases (no logic to port)

- `VAvailabilityValidator` — body is `return ComponentValidator.VAVAILABILITY.validate(target);` (one line)
- `VFreeBusyValidator` — body is `return target.validate();` (one line)
- `AvailableValidator` — body is `return target.validate();` (one line)

All three can be deleted in one commit with zero callers (verified — no production references after `rfc-validation-audit`).

### Non-trivial cases (port logic, then delete)

- `VEventValidator` — alarm block:
  ```java
  if (alarmsAllowed) {
      result.getEntries().addAll(target.getAlarms().stream()
          .map(ComponentValidator.VALARM_ITIP::validate)
          .flatMap(r -> r.getEntries().stream())
          .collect(Collectors.toList()));
  } else {
      result.getEntries().addAll(NO_ALARMS_RULE_SET.apply(target.getName(), target));
  }
  ```
- `VToDoValidator` — alarm block (identical pattern) + STATUS allowed-value check:
  ```java
  if (status.isPresent() && !VTODO_NEEDS_ACTION.equals(status.get())
          && !VTODO_COMPLETED.equals(status.get())
          && !VTODO_IN_PROCESS.equals(status.get())
          && !VTODO_CANCELLED.equals(status.get())) {
      result.getEntries().add(new ValidationEntry(...));
  }
  ```
- `VTimeZoneValidator` — observance block:
  ```java
  if (!target.getComponent(Observance.STANDARD).isPresent()
          && !target.getComponent(Observance.DAYLIGHT).isPresent()) {
      throw new ValidationException(...);   // ← uses throw, not entry!
  }
  target.getObservances().forEach(ComponentValidator.OBSERVANCE_ITIP::validate);
  ```

  Note: `VTimeZoneValidator` THROWS when observances are missing, rather than producing a `ValidationEntry`. Inconsistent with the rest of the validation pipeline. Worth fixing as part of the port — see D2-ALT decision below.

## Goals / Non-Goals

**Goals:**
- All six `@Deprecated validate/component/*Validator.java` files are deleted.
- Alarm-validation, VTODO STATUS check, and VTIMEZONE observance handling live in canonical, non-deprecated locations.
- No behavioural regression: the same checks fire in the same scenarios, producing the same set of `ValidationEntry` instances (with the exception of the VTIMEZONE throw → entry conversion, which is in the same direction as `rfc-validation-audit`'s F4 fix).
- The "iTIP path includes alarm-validation, no-method path does not" asymmetry is resolved by making the helper invocations explicit in both paths.

**Non-Goals:**
- Adding new validation rules. This is structural cleanup.
- Refactoring the iTIP registry's shape. The registry's API is unchanged.
- Touching any `.groovy` test files (out of scope per `test-framework` spec).
- F6/F7/F8 audit follow-ups from the audit (still deferred).

## Decisions

### D1. Alarm-validation helper: static method on `ComponentValidator`

Add to `ComponentValidator`:

```java
/**
 * Validates VALARM sub-components of a calendar component:
 * - When {@code alarmsAllowed} is true: validates each contained VALARM against
 *   the {@link #VALARM_ITIP} rule set.
 * - When {@code alarmsAllowed} is false: produces a {@link ValidationEntry} for
 *   each VALARM present (matches the NO_ALARMS rule).
 * Aggregated into the supplied {@link ValidationResult}.
 */
public static <T extends Component & ComponentContainer<?> & AlarmsAccessor>
        void validateAlarms(T target, boolean alarmsAllowed, ValidationResult result);
```

The generic bound requires the component to expose `getAlarms()`. `VEvent` and `VToDo` both implement `AlarmsAccessor`. The signature accepts the result as a parameter (mutating-style) rather than returning a new one, matching the existing internal idiom in ComponentValidator.

**Alternative considered:** A separate `AlarmValidationHelper` class. Rejected — adds a file for one method, and `ComponentValidator` already groups all related validation utilities.

### D2. VTODO STATUS check: helper on `ComponentValidator`

Add to `ComponentValidator`:

```java
/**
 * Validates that a VTODO's STATUS (if present) is one of the RFC 5545 §3.8.1.11
 * allowed values for VTODO: NEEDS-ACTION, COMPLETED, IN-PROCESS, CANCELLED.
 * Produces a {@link ValidationEntry} when an invalid value is present.
 */
public static void validateVToDoStatus(VToDo target, ValidationResult result);
```

**Alternative considered:** Express the STATUS check via a new `ValidationRule` predicate. This would mirror the existing `VJournal` STATUS check pattern in `ComponentValidator.VJOURNAL` and let the rule appear in `RULES` lists declaratively. Preferred for consistency. **Use this alternative**: add the predicate to every VTODO entry in `ITIPRuleRegistry` and to `ComponentValidator.VTODO`, removing the need for a separate helper. (Document the rationale here, then go with the rule-predicate approach.)

### D2-ALT. Use a predicate rule instead of a helper

For VJOURNAL, `ComponentValidator.VJOURNAL` already includes:

```java
new ValidationRule<>((Predicate<VJournal> & Serializable) a -> a.getProperties(STATUS).stream()
        .anyMatch(p -> !(VJOURNAL_DRAFT.equals(p) || VJOURNAL_FINAL.equals(p)
                || VJOURNAL_CANCELLED.equals(p))),
        "STATUS value not applicable for VJOURNAL", STATUS);
```

Mirror this exactly for VTODO. Add to `ComponentValidator.VTODO`:

```java
new ValidationRule<>((Predicate<VToDo> & Serializable) t -> t.getProperties(STATUS).stream()
        .anyMatch(p -> !(VTODO_NEEDS_ACTION.equals(p) || VTODO_COMPLETED.equals(p)
                || VTODO_IN_PROCESS.equals(p) || VTODO_CANCELLED.equals(p))),
        "STATUS value not applicable for VTODO", STATUS);
```

This eliminates the need for D2's helper method entirely. Cleaner. **Use this.**

### D3. VTIMEZONE observance handling: helper on `ComponentValidator`

Add to `ComponentValidator`:

```java
/**
 * Validates VTIMEZONE observance structure:
 * - Adds a {@link ValidationEntry} if neither STANDARD nor DAYLIGHT is present.
 * - Validates each observance against {@link #OBSERVANCE_ITIP}.
 */
public static void validateObservances(VTimeZone target, ValidationResult result);
```

**Behavioural change**: the deprecated `VTimeZoneValidator.validate(...)` threw `ValidationException` when STANDARD/DAYLIGHT were both missing. The new helper produces an `ERROR` entry instead. This is consistent with `rfc-validation-audit`'s F4 (no throwing during validation pipelines) and is the right direction.

Document this in release notes as a small behavioural shift: callers who previously caught `ValidationException` for the missing-observance case will instead see an entry on the `ValidationResult`. No `try { validate() } catch (ValidationException)` is documented in the public API as the right handling of validation failure, so impact is minimal.

### D4. Trivial-deletion order

Three files (`VAvailabilityValidator`, `VFreeBusyValidator`, `AvailableValidator`) have zero call sites after the audit. They can be deleted in step 1 without any code changes elsewhere. This is the safest opener for the change.

### D5. Migration of `VTimeZone.validate()` no-method path

`VTimeZone.java` line ~165 currently does:

```java
var result = new VTimeZoneValidator().validate(this);
```

Change to:

```java
var result = ComponentValidator.VTIMEZONE.validate(this);
ComponentValidator.validateObservances(this, result);
```

This makes the observance validation explicit in the no-method path, matching what the iTIP path will do via the registry.

### D6. Migration of `ITIPRuleRegistry` to drop deprecated subclasses

Replace all uses of `new VEventValidator(...)` / `new VToDoValidator(...)` / `new VTimeZoneValidator()` in the registry with `new ComponentValidator<>(...)`. The alarm validation that VEventValidator and VToDoValidator performed gets reintroduced by:

```java
private static ValidationResult validateWithAlarms(CalendarComponent c,
        Validator<? extends CalendarComponent> base, boolean alarmsAllowed) {
    @SuppressWarnings("unchecked")
    Validator<CalendarComponent> v = (Validator<CalendarComponent>) base;
    ValidationResult result = v.validate(c);
    if (c instanceof AlarmsAccessor) {
        ComponentValidator.validateAlarms((Component) c, alarmsAllowed, result);
    }
    return result;
}
```

Then registry entries become:

```java
vevent.put(REQUEST, c -> validateWithAlarms(c, new ComponentValidator<>(VEVENT, ...rules...), true));
vevent.put(CANCEL,  c -> validateWithAlarms(c, new ComponentValidator<>(VEVENT, ...rules...), false));
```

(Sketch — exact wiring decided during implementation.)

**Alternative considered:** Push the alarm-allowed flag into `ValidationRule` itself. Rejected — alarms aren't a property-cardinality rule, they're a sub-component validation that the existing rule vocabulary doesn't express.

## Risks / Trade-offs

- **[R1] Downstream consumers using the @Deprecated classes will break at compile-time.** They have been `@Deprecated` for ≥2 years. **Mitigation:** call out in release notes; provide migration snippets for each (`new VEventValidator(rules)` → `ComponentValidator.VEVENT.validate(...)`; subclasses can layer their own alarm logic on top with `ComponentValidator.validateAlarms(...)`).
- **[R2] VTIMEZONE throw → entry behavioural shift** — documented above. Direction matches F4 from the prior change. Low impact in practice.
- **[R3] Generic bound on `validateAlarms`** is fiddly (`T extends Component & ComponentContainer<?> & AlarmsAccessor`). Verify it accepts VEvent/VToDo cleanly without forcing every callsite to cast. **Mitigation:** if the bound is too restrictive in Java's type system, fall back to a wider `Component` param plus internal `instanceof AlarmsAccessor` check (the registry helper sketch in D6 already does this).
- **[R4] VTODO STATUS predicate added via D2-ALT introduces a *new* validation entry path** — calendars that previously had an invalid VTODO STATUS but never hit the deprecated `VToDoValidator` path will now flag the error. Strictly a correctness gain. **Mitigation:** none needed; document in release notes that VTODO STATUS values are now strictly enforced regardless of code path (matching VJOURNAL's existing behaviour).

## Migration Plan

```
1. Delete the 3 trivial pass-throughs (VAvailability, VFreeBusy, Available)
2. Add ComponentValidator.validateAlarms(...) helper; add VTODO STATUS predicate to ComponentValidator.VTODO; add ComponentValidator.validateObservances(...) helper
3. Update VEvent.validate(), VToDo.validate(), VTimeZone.validate() to use the new helpers
4. Update ITIPRuleRegistry to drop new VEventValidator(...) / new VToDoValidator(...) / new VTimeZoneValidator()
5. Verify ./gradlew test green
6. Delete the 3 remaining @Deprecated files (VEventValidator, VToDoValidator, VTimeZoneValidator)
7. Final test + cleanup commit
```

Each step is a separate commit. Rollback: `git revert <step>`. Steps 1, 6, 7 are simple deletions. Steps 2–4 are the substantive work.

## Open Questions

- **Q1**: Should the alarm-validation gap in the no-method path (`VEvent.validate()` doesn't currently fire alarm validation) be fixed in this change or noted as a follow-up? Default: **fix here** — D5 already wires `validateObservances` into the no-method path for VTimeZone; doing the same for VEvent/VToDo alarms is symmetric. Adds maybe 4 lines.
- **Q2**: Should the VTIMEZONE observance-presence check produce ERROR or WARNING severity? Currently throws (effectively ERROR). Default: **ERROR** to match.
- **Q3**: Is there value in keeping the trivial pass-throughs as `@Deprecated` for one more release cycle? Default: **no** — they were already on the chopping block for `rfc-validation-audit`; another cycle is just procrastination. External callers can migrate to `ComponentValidator.<COMPONENT>` directly.
