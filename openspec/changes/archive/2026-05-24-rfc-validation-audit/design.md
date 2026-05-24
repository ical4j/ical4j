## Context

ical4j's validation surface has two layers:

1. **Cardinality rules** (`ValidationRule` with `One`/`OneOrLess`/`None`/`OneOrMore`/`OneExclusive`/`AllOrNone`/`ValueMatch`) wired through `ComponentValidator`/`PropertyValidator`/`CalendarValidatorImpl` in `net.fortuna.ical4j.validate`.
2. **Method-aware rules** for RFC 5546 iTIP, currently embedded as static initialiser blocks inside `net.fortuna.ical4j.model.component.{VEvent,VToDo,VJournal,VFreeBusy}`. Each class holds a `Map<Method, Validator<T>> methodValidators`. The validators in those maps are instances of `validate/component/{VEvent,VToDo,VAvailability,VFreeBusy}Validator.java`, all marked `@Deprecated` but used.

The split between layers 1 and 2 was historical — the per-method rules predate the ComponentValidator static instances refactor. The audit (see proposal.md) surfaced five behavioural defects and one architectural split that this change addresses.

### Defects identified

| ID  | RFC ref           | Description                                                                                       |
|-----|-------------------|---------------------------------------------------------------------------------------------------|
| F1  | RFC 5546 §3.2.1.1 | VEVENT/PUBLISH wrongly forbids ATTENDEE                                                           |
| F2  | RFC 5545 §3.6     | CalendarValidatorImpl doesn't enforce PRODID/VERSION presence by default                          |
| F3  | RFC 5546 §4.3.3   | VFREEBUSY/COUNTER validator missing                                                               |
| F4  | RFC 5545 §3.4     | `CalendarComponent.validate(Method)` throws instead of returning a ValidationResult on unknown    |
| F5  | (architecture)    | iTIP rules in `model/component/*` rather than `validate/`                                         |
| F10 | (correctness)     | `CalendarValidatorImpl` discards per-component iTIP results instead of merging                    |

F6/F7/F8/F9 from the audit are explicitly deferred (see proposal Impact / Out of scope).

## Goals / Non-Goals

**Goals:**
- Validation results are consistent: every cell in the (component, method) matrix either has a defined rule set or returns a `ValidationResult` with a clear "method not applicable" entry — never throws.
- The per-(component, method) iTIP rule definitions live in a single discoverable location under `validate/`.
- F1, F2, F3 produce RFC-conformant outcomes for the most common interop cases (Outlook/Google ATTENDEE on PUBLISH; PRODID/VERSION presence; VFREEBUSY counter-proposals).
- F10 changes calendar-level validation from "silently OK if components fail iTIP rules" to "report the failures". No new throws.

**Non-Goals:**
- A full row-by-row audit of every RFC 5546 §3.2.x/§4.3.x property table against the existing rules. Spot-check level only here; systematic walk is a follow-up change.
- New constraint vocabulary beyond what `ValidationRule` already supports. Cross-property invariants (RECURRENCE-ID type, UNTIL type, SEQUENCE>0 implies present) stay on the existing `Predicate` escape hatch or remain unenforced for this change.
- Property value-set enum enforcement (CLASS, TRANSP, STATUS per-component, PARTSTAT, ROLE, CUTYPE) — deferred.
- Changes to the parser, the property model, or anything outside `validate/` and the iTIP map static blocks in `model/component/`.
- Touching Spock/Groovy tests under `src/test/groovy/` (carries over the strict-scope boundary from the JUnit 5 migration).

## Decisions

### D1. F1 fix: drop the `None ATTENDEE` rule from VEVENT/PUBLISH

Current (`model/component/VEvent.java:228`):
```java
new ValidationRule<>(None, true, ATTENDEE),   // "MUST NOT be present" with relaxed-mode escape
```

Replace with: removed entirely (or `OneOrMore` with no minimum; `ValidationRule` doesn't have an explicit "0+" type — absence of any rule mentioning ATTENDEE is the "0+" encoding the rest of the codebase uses).

The change preserves the rest of the rule set unchanged. The rationale comment in PUBLISH already implies the historic interpretation was "PUBLISH messages aren't directed at attendees, so listing them is meaningless" — but RFC 5546 §3.2.1.1 explicitly allows them, and Outlook/Google exports rely on this.

**Alternative considered:** keep the rule, demote permanently to WARN. Rejected — even a WARN is friction for valid input, and the warning has no actionable message.

### D2. F2 fix: lift PRODID/VERSION enforcement into CalendarValidatorImpl itself

Current `CalendarValidatorImpl.validate` only verifies VERSION's *value* when present. PRODID isn't checked at all unless the caller passed a `ValidationRule` requiring it.

Change: add unconditional checks in `validate`:
```java
if (target.getProperty(PRODID).isEmpty()) result.getEntries().add(new ValidationEntry("PRODID is required", ERROR, VCALENDAR));
if (target.getProperty(VERSION).isEmpty()) result.getEntries().add(new ValidationEntry("VERSION is required", ERROR, VCALENDAR));
```

These complement (not replace) the constructor-passed `rules`. Direct construction of `CalendarValidatorImpl` (e.g., from tests or third-party code) now matches `DefaultCalendarValidatorFactory.newInstance()` behaviour.

**Alternative considered:** make the constructor private and force factory use. Rejected — breaking change with no upside the in-validator check doesn't already give.

### D3. F3 fix: add VFREEBUSY/COUNTER method validator

RFC 5546 §4.3.3 defines `METHOD:COUNTER` for VFREEBUSY:

| Property            | Presence | Comment |
|---------------------|----------|---------|
| ATTENDEE            | 1+       |         |
| DTEND               | 1        |         |
| DTSTAMP             | 1        |         |
| DTSTART             | 1        |         |
| FREEBUSY            | 0+       |         |
| ORGANIZER           | 1        |         |
| UID                 | 1        |         |
| COMMENT             | 0 or 1   |         |
| CONTACT             | 0+       |         |
| DURATION            | 0 or 1   | (mutually exclusive with DTEND/DTSTART span — handled by existing rule) |
| REQUEST-STATUS      | 0+       |         |
| URL                 | 0 or 1   |         |
| X-PROPERTY          | 0+       |         |

Add to `VFreeBusy.methodValidators` (or to the new registry per D5):
```java
new ComponentValidator<>(VFREEBUSY,
    new ValidationRule<>(One, ATTENDEE, DTEND, DTSTAMP, DTSTART, ORGANIZER, UID),
    new ValidationRule<>(OneOrLess, COMMENT, DURATION, URL));
```

The exact rule encoding (`OneOrMore` ATTENDEE vs `One`) follows the pattern of VFREEBUSY/REQUEST already in the codebase.

### D4. F4 fix: unsupported-method returns a result rather than throwing

Current `CalendarComponent.validate(Method)` (the fallback in the base class):
```java
public ValidationResult validate(Method method) throws ValidationException {
    throw new ValidationException("Unsupported method: " + method);
}
```

Change to:
```java
public ValidationResult validate(Method method) {
    ValidationResult result = new ValidationResult();
    result.getEntries().add(new ValidationEntry(
        String.format("Method %s not applicable to component %s", method.getValue(), getName()),
        ValidationEntry.Severity.ERROR,
        getName()));
    return result;
}
```

Subclasses (VEvent/VToDo/etc.) keep their override that consults `methodValidators` and falls back to `super.validate(method)` for unknown methods — that path now returns a result rather than throwing.

**Subtlety:** The signature `throws ValidationException` stays on the abstract method to preserve binary compatibility, even though the default no longer throws. Concrete subclasses may still throw from their own rules (e.g., via `Validator.assertFalse`). No public API change.

**Alternative considered:** Catch the throw in `CalendarValidatorImpl`. Rejected — pushes the responsibility outward and masks the semantic fix; better to fix at the source.

### D5. F5 refactor: `ITIPRuleRegistry` co-locates iTIP rules

Create `src/main/java/net/fortuna/ical4j/validate/ITIPRuleRegistry.java` (final name TBD; "ITIPRuleRegistry" works) that holds a `Map<String, Map<Method, Validator<? extends Component>>>` keyed by component-name + method. Populated via a single static block that defines every (component, method) cell currently in `model/component/{VEvent,VToDo,VJournal,VFreeBusy}.java`.

Each model class's `validate(Method)` becomes:
```java
public ValidationResult validate(Method method) {
    return ITIPRuleRegistry.validate(this, method);
}
```

where `ITIPRuleRegistry.validate(component, method)` looks up the validator, runs it, and returns the result (or returns a "not applicable" entry per D4 if the cell is empty).

**Why a registry not a strategy-per-component?** A single registry keeps every iTIP rule in one file readable end-to-end. Strategy-per-component (one ITIPVEventValidator, one ITIPVToDoValidator, etc.) preserves package boundaries cleaner but spreads rules across four-plus files, undoing the colocation benefit. Default: registry. Open to revisit if it grows beyond ~400 lines.

**Migration:**
1. Add `ITIPRuleRegistry` with all current rules copied from the four model files.
2. Wire each model class's `validate(Method)` through the registry.
3. Verify behaviour parity with existing tests (no result-set changes from this step alone).
4. Apply F1/F3 fixes inside the registry.
5. Delete the now-unused static `methodValidators` blocks.

**Deprecated subclasses:** `validate/component/{VEventValidator,VToDoValidator,VAvailabilityValidator,VFreeBusyValidator,VTimeZoneValidator,AvailableValidator}.java` are all `@Deprecated`. After F5, none of them is instantiated by ical4j itself. Decision: **delete them as part of this change**. They have been `@Deprecated` for several releases and external callers can switch to `ComponentValidator.<COMPONENT>` static instances or the registry. Documented as a breaking removal in the release notes for the change.

**Alternative considered:** Keep deprecated classes one more cycle. Rejected — they're not in a public stable API surface and the deprecation predates the audit; cleanup avoids carrying dead code through Phase 4.

### D6. F10 fix: merge per-component iTIP results

Current `CalendarValidatorImpl.validate` (lines 95-99):
```java
// perform ITIP validation on components..
for (var component : target.getComponents()) {
    component.validate(method.get());
}
```

The return value of `component.validate(method)` is discarded. Change to:
```java
for (var component : target.getComponents()) {
    result = result.merge(component.validate(method.get()));
}
```

After D4, the call no longer throws — so no try/catch is needed.

**Test cost:** several tests have been passing because calendar-level validation didn't report per-component iTIP failures. After this fix, they may start producing entries. The change includes updating those tests; the audit's spot-check found ≤10 likely-affected tests but a full run will surface the precise list.

## Risks / Trade-offs

- **[R1] F10 surfaces previously-hidden errors** — Consumer code that treats any non-empty `ValidationResult` as a hard failure will start seeing new errors for calendars that pass today. **Mitigation:** describe in release notes; offer relaxed-mode escape for the most common new errors by setting `relaxedModeSupported=true` on rules where appropriate; provide a migration table.
- **[R2] F1 is a behaviour change in the strict direction → looser** — Calendars that used to fail PUBLISH validation because they had ATTENDEEs now pass. No consumer should object to a removal of a false-positive error, but document anyway.
- **[R3] D5 (registry) is a non-trivial refactor with no behavioural change of its own** — Bugs are easy to introduce when moving large static blocks. **Mitigation:** sequence migration as D5 step 1 (copy), step 2 (wire), step 3 (verify tests green BEFORE making F1/F3 changes), step 4 (apply fixes), step 5 (delete). Each step is its own commit so bisect is cheap if a regression appears.
- **[R4] Deleting `@Deprecated validate/component/*Validator.java` classes is a breaking removal** — anyone subclassing them externally breaks. **Mitigation:** they were `@Deprecated` for ≥1 minor version; this is the canonical migration window. Document in release notes; provide a one-paragraph migration guide ("replace `new VEventValidator(...)` with `ComponentValidator.VEVENT` for default rules, or use `ITIPRuleRegistry` for method-aware validation"). If pushback emerges in review, can be reverted as a separate task while keeping D1-D4 + D6.
- **[R5] PRODID/VERSION required check (D2) might break calendars where these were intentionally omitted** — Some consumers may rely on the lenient default. RFC 5545 is unambiguous that they are required; documentation update covers the change. Relaxed-mode escape via the existing `KEY_RELAXED_VALIDATION` hint is preserved for the version-value check but **not** added for the presence check — RFC 5545 is too clear to make this hint-dependent.

## Migration Plan

The change is implemented as a sequence of commits, each individually green:

```
1. D5 step 1-2  Add ITIPRuleRegistry + wire model classes through it (no rule changes)
2. D5 step 3    Verify all existing validation tests still pass (no commit, just confirmation)
3. D1           Drop None ATTENDEE rule for VEVENT/PUBLISH
4. D3           Add VFREEBUSY/COUNTER rule
5. D4           Change CalendarComponent.validate(Method) base behaviour
6. D6           Merge per-component iTIP results in CalendarValidatorImpl
7. D2           Add PRODID/VERSION presence checks in CalendarValidatorImpl
8. D5 step 5    Delete the deprecated validate/component/*Validator.java subclasses
9. Tests        Update tests that observe the new (correct) behaviour
```

Rollback: each step is a separate commit, `git revert <step>` works. Step 6 (D6) is the only step that can surface unexpected new errors; if a deployment-blocker appears, revert just D6.

## Open Questions

- **Q1**: Are there any downstream open-source consumers that subclass `validate/component/VEventValidator` etc.? A quick GitHub code-search would clarify the actual exposure for the breaking removal in D5.
- **Q2**: Should `ITIPRuleRegistry` be open for extension (register additional methods, e.g., for X-METHOD extensions used by some vendors)? Default: no, keep it private/sealed for this change; revisit if a use case emerges.
- **Q3**: For the F4 entry message — should it use the human-readable method value ("PUBLISH") or the iCal token form? They're the same for standard methods. Stick with `method.getValue()`.
- **Q4**: After F10, what tests need updating? Cannot know without running the suite; capture the list in `tasks.md` step 9 once the fixes land.
