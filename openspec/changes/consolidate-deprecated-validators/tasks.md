## 1. Baseline

- [x] 1.1 Capture pre-change test counts: `./gradlew test` → record total/skipped/failures (expect 276/2682/51/0/0 unchanged from post-rfc-validation-audit baseline)
- [x] 1.2 Confirm no internal callers remain for the trivial pass-throughs (the audit step 1.3 already established this; re-verify):
  - `grep -rln "VAvailabilityValidator\|VFreeBusyValidator\|AvailableValidator" src/main/java src/test/java --include="*.java" | grep -v "/\\(VAvailabilityValidator\\|VFreeBusyValidator\\|AvailableValidator\\)\\.java"`
- [x] 1.3 Identify the (component, method) entries in `ITIPRuleRegistry` that depend on alarm-validation behaviour. From `VEvent.java` history: PUBLISH/REQUEST/COUNTER/ADD/REPLY allow alarms; CANCEL/REFRESH/DECLINE_COUNTER reject alarms. Same pattern for VTodo. Record the full mapping.

## 2. Delete trivial pass-throughs

- [x] 2.1 `git rm src/main/java/net/fortuna/ical4j/validate/component/VAvailabilityValidator.java`
- [x] 2.2 `git rm src/main/java/net/fortuna/ical4j/validate/component/VFreeBusyValidator.java`
- [x] 2.3 `git rm src/main/java/net/fortuna/ical4j/validate/component/AvailableValidator.java`
- [x] 2.4 Run `./gradlew test` — green
- [x] 2.5 Commit: "refactor: remove trivial pass-through deprecated validator subclasses"

## 3. Add canonical helpers in ComponentValidator (D1 + D3)

- [x] 3.1 Add `ComponentValidator.validateAlarms(Component, boolean alarmsAllowed, ValidationResult)` (or chosen signature per design D1). Body lifted from `VEventValidator` / `VToDoValidator` (they're identical).
- [x] 3.2 Add `ComponentValidator.validateObservances(VTimeZone, ValidationResult)`. Body lifted from `VTimeZoneValidator`, but converted from `throw new ValidationException(...)` to `result.getEntries().add(new ValidationEntry(..., ERROR, ...))` per D3.
- [x] 3.3 Add unit tests in a new `ComponentValidatorHelpersTest.java`:
  - alarm-allowed VEvent with valid VALARM → no entries
  - alarm-allowed VEvent with VALARM missing ACTION → ACTION entry
  - alarm-disallowed VEvent with any VALARM → VALARM entry
  - VTIMEZONE missing both STANDARD/DAYLIGHT → presence entry (no throw)
  - VTIMEZONE with STANDARD → no presence entry
- [x] 3.4 Run `./gradlew test` — green
- [x] 3.5 Commit: "feat: add validateAlarms and validateObservances helpers in ComponentValidator"

## 4. Add VTODO STATUS predicate to ComponentValidator.VTODO (D2-ALT)

- [x] 4.1 In `ComponentValidator.VTODO`, add a `ValidationRule<>((Predicate<VToDo> & Serializable) ...)` mirroring the existing `VJOURNAL` STATUS check pattern, asserting STATUS value is one of NEEDS-ACTION/COMPLETED/IN-PROCESS/CANCELLED.
- [x] 4.2 Add a test in `ComponentValidatorHelpersTest`:
  - VTODO with STATUS:COMPLETED → no STATUS entry
  - VTODO with STATUS:TENTATIVE (a VEVENT value) → STATUS entry
- [x] 4.3 Run `./gradlew test` — green
- [x] 4.4 Commit: "feat: enforce VTODO STATUS allowed values via ValidationRule predicate"

## 5. Update no-method validate() paths in model classes

- [ ] 5.1 `VEvent.validate()` (the no-arg, no-method path) — after the existing `ComponentValidator.VEVENT.validate(this)` call, invoke `ComponentValidator.validateAlarms(this, true, result)`. Closes the asymmetry vs the iTIP path.
- [ ] 5.2 `VToDo.validate()` — same treatment with `validateAlarms`. STATUS check now flows via the VTODO predicate added in §4.
- [ ] 5.3 `VTimeZone.validate()` — replace `new VTimeZoneValidator().validate(this)` with `ComponentValidator.VTIMEZONE.validate(this)` + `ComponentValidator.validateObservances(this, result)`.
- [ ] 5.4 Run `./gradlew test` — green
- [ ] 5.5 Commit: "refactor: move alarm/observance validation into canonical paths in VEvent/VToDo/VTimeZone"

## 6. Update ITIPRuleRegistry to drop deprecated subclasses (D6)

- [ ] 6.1 Choose between sketch options in design.md D6: (a) `validateWithAlarms(...)` wrapper that aggregates `ComponentValidator<>` + alarm-validation; or (b) inline lambda creation per registry entry. Default to (a) — fewer copies of the wrapper code.
- [ ] 6.2 Replace each `new VEventValidator(alarmsAllowed, rules...)` registry entry with `validateWithAlarms(new ComponentValidator<>(VEVENT, rules...), alarmsAllowed)`-shaped construction
- [ ] 6.3 Same for `new VToDoValidator(alarmsAllowed, rules...)` → `validateWithAlarms(new ComponentValidator<>(VTODO, rules...), alarmsAllowed)`
- [ ] 6.4 Replace `new VTimeZoneValidator()` registry entry with a `new ComponentValidator<>(VTIMEZONE, ...)` plus a wrapper that calls `validateObservances` after the base validation
- [ ] 6.5 Drop the now-unused `import net.fortuna.ical4j.validate.component.VEventValidator`, etc. from `ITIPRuleRegistry.java`
- [ ] 6.6 Run `./gradlew test` — green
- [ ] 6.7 Commit: "refactor: drop deprecated validator subclasses from ITIPRuleRegistry"

## 7. Delete the three remaining @Deprecated files

- [ ] 7.1 `grep -rn "VEventValidator\|VToDoValidator\|VTimeZoneValidator" src/main/java src/test/java --include="*.java"` MUST return nothing outside the file definitions themselves
- [ ] 7.2 `git rm src/main/java/net/fortuna/ical4j/validate/component/VEventValidator.java`
- [ ] 7.3 `git rm src/main/java/net/fortuna/ical4j/validate/component/VToDoValidator.java`
- [ ] 7.4 `git rm src/main/java/net/fortuna/ical4j/validate/component/VTimeZoneValidator.java`
- [ ] 7.5 Run `./gradlew test` and `./gradlew jar` — green
- [ ] 7.6 Verify the `validate/component/` directory is empty (or remove the empty directory if appropriate)
- [ ] 7.7 Commit: "refactor: delete remaining @Deprecated validate/component/*Validator subclasses"

## 8. Cleanup

- [ ] 8.1 Final `./gradlew clean test jacocoTestReport` — coverage ≥ 0.7
- [ ] 8.2 Verify test count: at least 282 tests (276 pre-change + the new helper tests from §3.3 and §4.2)
- [ ] 8.3 Open PR; reference: parent change `rfc-validation-audit`, RFC 5545 §3.8.1.11 (VTODO STATUS), RFC 5545 §3.6.5 (VTIMEZONE observance requirement)

## 9. Validation gates (at every commit boundary)

- [ ] 9.1 `./gradlew clean test` passes
- [ ] 9.2 No new compiler warnings
- [ ] 9.3 JaCoCo ≥ 0.7
- [ ] 9.4 Spock/Groovy untouched (`git diff --name-only -- src/test/groovy/` empty)
- [ ] 9.5 Spock test count unchanged
