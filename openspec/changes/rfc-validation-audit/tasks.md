## 1. Baseline & investigation

- [x] 1.1 Capture pre-change baseline: run `./gradlew test` and record total/skipped/failures (compare against post-Phase-4a baseline of 2671/51/0/0 if unchanged since the JUnit 5 migration)
- [x] 1.2 Enumerate existing validation tests likely to need updates after F10 lands: `grep -rl "validate(.*Method\|methodValidators\|ITIPValidator" src/test --include="*.java" --include="*.groovy"` and list candidates
- [x] 1.3 Quick check (Open Question Q1 from design.md): GitHub code-search for external subclasses of `validate/component/{VEvent,VToDo,VAvailability,VFreeBusy,VTimeZone,Available}Validator` — informs the breaking-removal decision in step 8

## 2. Build the iTIP rule registry (D5 step 1-2)

- [x] 2.1 Create `src/main/java/net/fortuna/ical4j/validate/ITIPRuleRegistry.java` (final name TBD; the design defaults to ITIPRuleRegistry)
- [x] 2.2 Copy every `methodValidators.put(...)` entry from `model/component/VEvent.java` static initialiser into the registry, keyed by `(Component.VEVENT, Method)`. Preserve rule semantics exactly — no rule changes in this step.
- [x] 2.3 Same for `model/component/VToDo.java`
- [x] 2.4 Same for `model/component/VJournal.java`
- [x] 2.5 Same for `model/component/VFreeBusy.java`
- [x] 2.6 Expose `ITIPRuleRegistry.validate(Component, Method)` returning a `ValidationResult` (handles registry lookup and the not-applicable fallback)
- [x] 2.7 Update `VEvent.validate(Method)` to delegate to the registry (replace the local map lookup with `ITIPRuleRegistry.validate(this, method)`)
- [x] 2.8 Same for `VToDo.validate(Method)`, `VJournal.validate(Method)`, `VFreeBusy.validate(Method)`
- [x] 2.9 Delete the (now-unused) `methodValidators` maps and static initialisers in the four model classes
- [x] 2.10 Run `./gradlew test` — MUST be green with no count change vs 1.1 baseline (semantics unchanged in this step)
- [x] 2.11 Commit "refactor: move iTIP rule definitions into ITIPRuleRegistry"

## 3. F4 — Unsupported (component, method) returns a ValidationResult

- [x] 3.1 Change `CalendarComponent.validate(Method)` (base in `src/main/java/net/fortuna/ical4j/model/component/CalendarComponent.java`) from `throw new ValidationException(...)` to returning a `ValidationResult` with a single ERROR `ValidationEntry` whose message is `String.format("Method %s not applicable to component %s", method.getValue(), getName())`. Keep the `throws ValidationException` on the signature for binary compat.
- [x] 3.2 Update `ITIPRuleRegistry.validate(...)` to use the same not-applicable entry shape when a registry cell is empty (consistency)
- [x] 3.3 Update any tests in `validate/CalendarValidatorImplTest` and per-component validate(Method) tests that previously expected `ValidationException` for unsupported methods
- [x] 3.4 Run `./gradlew test` — green
- [x] 3.5 Commit "fix: report unsupported (component, method) as validation entry instead of throwing"

## 4. F10 — Merge per-component iTIP results

- [x] 4.1 In `CalendarValidatorImpl.validate`, change the `for (var component : target.getComponents()) { component.validate(method.get()); }` loop to `for (var component : target.getComponents()) { result = result.merge(component.validate(method.get())); }`
- [x] 4.2 Run `./gradlew test` — expect new failures in tests that had been passing only because per-component iTIP errors were silently dropped. Investigate each failure; some are expected behaviour changes (correctness fixes), some may indicate over-strict rules in the registry that should be relaxed.
- [x] 4.3 For each failed test that reflects a correctness fix: update the test expectation to match the new (correct) behaviour
- [x] 4.4 For each failed test that reveals an over-strict rule: document the rule in the design doc's Open Questions and decide whether to relax (with `relaxedModeSupported=true`) or accept as a strict rule
- [x] 4.5 Run `./gradlew test` — green
- [x] 4.6 Commit "fix: merge per-component iTIP validation results into calendar-level result"

## 5. F1 — Drop None ATTENDEE rule for VEVENT/PUBLISH

- [x] 5.1 In `ITIPRuleRegistry` (the entry for `(VEVENT, PUBLISH)`), remove the `new ValidationRule<>(None, true, ATTENDEE)` rule
- [x] 5.2 Add a test: PUBLISH VEVENT with one ATTENDEE produces no validation entries naming ATTENDEE
- [x] 5.3 Add a test: PUBLISH VEVENT with multiple ATTENDEEs validates clean
- [x] 5.4 Run `./gradlew test` — green
- [x] 5.5 Commit "fix: allow ATTENDEE on VEVENT/PUBLISH per RFC 5546 §3.2.1.1"

## 6. F3 — Add VFREEBUSY/COUNTER iTIP rule set

- [x] 6.1 In `ITIPRuleRegistry`, add the entry for `(VFREEBUSY, COUNTER)`:
  - `One`: ATTENDEE, DTEND, DTSTAMP, DTSTART, ORGANIZER, UID
  - `OneOrLess`: COMMENT, DURATION, URL
- [x] 6.2 Add a test: COUNTER VFREEBUSY with all required properties validates clean
- [x] 6.3 Add a test: COUNTER VFREEBUSY missing UID produces a ValidationEntry for UID
- [x] 6.4 Add a test: COUNTER VFREEBUSY does not throw `ValidationException`
- [x] 6.5 Run `./gradlew test` — green
- [ ] 6.6 Commit "feat: add VFREEBUSY/COUNTER iTIP validation rule set per RFC 5546 §4.3.3"

## 7. F2 — Enforce PRODID and VERSION presence in CalendarValidatorImpl

- [ ] 7.1 In `CalendarValidatorImpl.validate`, after the constructor-rules-driven check, add unconditional presence checks: if `target.getProperty(PRODID).isEmpty()` add ERROR entry; same for VERSION
- [ ] 7.2 Update tests that constructed `CalendarValidatorImpl` directly with no rules and previously passed validation despite missing PRODID/VERSION
- [ ] 7.3 Verify `DefaultCalendarValidatorFactory.newInstance()` still passes its existing tests (the constructor-rules path already covers PRODID/VERSION; the new checks should be redundant but not contradict)
- [ ] 7.4 Add a test: `new CalendarValidatorImpl()` (no rules) on a Calendar missing PRODID returns a ValidationEntry for PRODID
- [ ] 7.5 Add a test: same for missing VERSION
- [ ] 7.6 Run `./gradlew test` — green
- [ ] 7.7 Commit "fix: enforce PRODID and VERSION presence in CalendarValidatorImpl per RFC 5545 §3.6"

## 8. D5 — Delete deprecated validator subclasses

- [ ] 8.1 Verify no remaining ical4j source references the following classes outside their own files:
  - `validate/component/VEventValidator.java`
  - `validate/component/VToDoValidator.java`
  - `validate/component/VAvailabilityValidator.java`
  - `validate/component/VFreeBusyValidator.java`
  - `validate/component/VTimeZoneValidator.java`
  - `validate/component/AvailableValidator.java`
- [ ] 8.2 If 1.3 surfaced no external subclasses (or the decision is to remove anyway), delete all six files
- [ ] 8.3 Update any documentation, javadoc `@see` references, or release notes
- [ ] 8.4 Run `./gradlew test` and `./gradlew jar` — green
- [ ] 8.5 Commit "refactor: remove deprecated validate/component/*Validator subclasses"

## 9. Cleanup & docs

- [ ] 9.1 Update CHANGELOG.md (or release notes) with the behavioural changes:
  - PUBLISH/ATTENDEE now permitted (looser)
  - VFREEBUSY/COUNTER now validated (new coverage)
  - Unsupported (component, method) no longer throws (looser exception surface)
  - Per-component iTIP results now surface at calendar level (stricter result)
  - PRODID/VERSION presence now enforced unconditionally (stricter)
- [ ] 9.2 Run `./gradlew clean test jacocoTestReport` — confirm coverage threshold (≥0.7) still met
- [ ] 9.3 Open PR; reference RFC sections in PR body

## 10. Validation gates (apply at every commit boundary)

- [ ] 10.1 `./gradlew clean test` passes
- [ ] 10.2 No new compiler warnings introduced
- [ ] 10.3 JaCoCo coverage ≥ 0.7
- [ ] 10.4 Spock/Groovy untouched (`git diff --name-only -- src/test/groovy/` is empty per change boundary; this matches the existing test-framework spec's scope rule)
- [ ] 10.5 Spock test count unchanged
