## Why

An audit of ical4j's validation implementation against RFC 5545 (iCalendar) and RFC 5546 (iTIP) surfaced six concrete defects and one architectural split that make method-aware validation harder to maintain and harder to trust:

- VEVENT/PUBLISH validation forbids ATTENDEE properties, contradicting RFC 5546 §3.2.1.1 which explicitly allows 0+. Real-world publishers (Outlook, Google) routinely include ATTENDEE on PUBLISH messages, so strict validation rejects valid calendars.
- VFREEBUSY/COUNTER has no method validator registered; RFC 5546 §4.3.3 defines that combination, and incoming COUNTER messages with VFREEBUSY currently raise an `Unsupported method: COUNTER` exception.
- `CalendarComponent.validate(Method)` throws `ValidationException` for any (component, method) pair not in the per-component map. The calendar-level pipeline does not catch this, so a calendar with a stray VJOURNAL inside a METHOD:REQUEST envelope crashes validation instead of reporting a clean validation entry.
- Per-component iTIP results are silently discarded: `CalendarValidatorImpl` calls `component.validate(method)` and throws away the returned `ValidationResult`. Any rule violations in per-method validators do not surface at the calendar level.
- `CalendarValidatorImpl` does not enforce PRODID and VERSION presence by default. RFC 5545 §3.6 says both are REQUIRED. Enforcement currently depends on whether the caller constructed the validator via `DefaultCalendarValidatorFactory` or directly.
- Per-method iTIP rules live in `model/component/VEvent.java`, `VToDo.java`, `VJournal.java`, `VFreeBusy.java` static initialisers, alongside the model classes. The `validate/component/*Validator.java` classes that the rules instantiate are `@Deprecated` but actively load-bearing. Validation logic split across two packages makes auditing, extension, and discoverability harder.

## What Changes

- **Fix F1 (PUBLISH/ATTENDEE)**: Remove the `None ATTENDEE` rule from VEVENT/PUBLISH. RFC 5546 §3.2.1.1 permits 0+ ATTENDEEs.
- **Fix F2 (PRODID/VERSION required)**: Make `CalendarValidatorImpl` enforce PRODID and VERSION presence (One each) independent of constructor-passed rules. The `DefaultCalendarValidatorFactory` already does; this lifts the guarantee to the validator itself.
- **Fix F3 (VFREEBUSY/COUNTER)**: Add a COUNTER method validator to `VFreeBusy.methodValidators` following RFC 5546 §4.3.3.
- **Fix F4 (Unsupported method as validation entry)**: Change `CalendarComponent.validate(Method)` (the fallback path) so that an unsupported (component, method) combination produces a `ValidationResult` containing a `ValidationEntry` rather than throwing. Calendar-level pipeline gains a single clear error instead of an unhandled exception.
- **Fix F10 (Merge per-component iTIP results)**: Change `CalendarValidatorImpl.validate` to merge the `ValidationResult` returned by each `component.validate(method)` call into the calendar-level result, instead of discarding it.
- **Refactor F5 (Co-locate iTIP rules in `validate/`)**: Move the `methodValidators` maps out of `model/component/VEvent.java`, `VToDo.java`, `VJournal.java`, `VFreeBusy.java` into a single `validate/ITIPRuleRegistry` (name TBD in design.md) that holds the per-(component, method) rule sets. The model classes' `validate(Method)` methods become thin dispatchers that consult the registry. The deprecated `validate/component/*Validator.java` subclasses can then be deleted; their construction sites move to the registry.
- **BREAKING (validation-result only, no API removal)**: After F10, calendars that previously appeared to validate cleanly because per-component iTIP failures were silently dropped will now correctly report those failures. This is the fix's whole point, but downstream consumers that consider any non-empty result a hard failure may see new entries. No public Java method signature changes.

## Capabilities

### New Capabilities
- `calendar-validation`: Defines requirements for how ical4j validates `Calendar`, `Component`, and `Property` instances against RFC 5545 (iCalendar) and RFC 5546 (iTIP). Captures the fixes and the consolidated iTIP rule registry shape.

### Modified Capabilities
<!-- None — `test-framework` (the only existing spec) is unrelated. -->

## Impact

- **Affected code**: `src/main/java/net/fortuna/ical4j/validate/CalendarValidatorImpl.java`, `validate/ITIPValidator.java`, `validate/CalendarComponent` fallback in `model/component/CalendarComponent.java`, and the static iTIP rule blocks in `model/component/{VEvent,VToDo,VJournal,VFreeBusy}.java`. New file under `validate/` for the rule registry.
- **Test coverage**: Existing tests under `src/test/java/net/fortuna/ical4j/validate/` and the per-component test classes need updates for the changed PUBLISH/ATTENDEE behaviour and the new VFREEBUSY/COUNTER path. New tests added for F4 (unsupported method) and F10 (result merging).
- **Public API**: No method removals. The `@Deprecated validate/component/*Validator.java` classes are deletable after F5 — they have no non-internal callers in ical4j itself, but downstream users who reference them directly will need to migrate. Mark as removable with one minor-version cycle, or remove now (a deliberate cleanup of long-deprecated API).
- **Spock/Groovy tests**: untouched — out of scope, same boundary as the JUnit 5 migration.
- **Risk**: F10 may surface real validation failures in calendars that previously passed silently. This is correctness, not regression, but consumers using ical4j with strict validation may see new errors. Document in the changelog.
- **Out of scope (deferred to follow-up changes)**: Cross-property constraint vocabulary (e.g., RECURRENCE-ID type ≡ DTSTART type), systematic row-by-row audit of every RFC 5546 §3.x/§4.x table, property value-set enum constraints (CLASS, TRANSP, STATUS per-component, PARTSTAT, ROLE, CUTYPE). These are larger pieces of work and warrant their own change proposals.
