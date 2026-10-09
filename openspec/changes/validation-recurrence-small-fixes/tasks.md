## 1. Regression tests first (red)

- [x] 1.1 `CalendarValidatorImplTest`: METHOD:REQUEST calendar whose VEVENT lacks ORGANIZER — `validate(false)` and `new CalendarValidatorImpl().validate(cal)` have no errors; `validate(true)` reports ORGANIZER exactly once (#363).
- [x] 1.2 New `ComponentValidatorTest`: VEVENT with two DTSTAMP yields one DTSTAMP entry; two UID on VEVENT/VFREEBUSY/VAVAILABILITY yields one UID entry (#692).
- [x] 1.3 `ComponentBuilderTest`: `name("standard")` then `hasName` in upper/lower/mixed case (#691).
- [x] 1.4 `TzHelperTest`: `Central America Standard Time` → `America/Guatemala`, July offset -06:00 (#709).
- [x] 1.5 `PeriodSpec 'test intersects'`: zero-length rows at start (true, both orders), inside (true), at end (false, both orders), equal empties (true), distinct empties (false) (#82).
- [x] 1.6 `VEventSpec`: date-time monthly event over a `LocalDate` period returns 3 instances without exception (#736); zero-duration event included when the period starts at its start, excluded when it ends there (#82).

## 2. Fixes

- [x] 2.1 `CalendarValidatorImpl.validate`: remove the per-component `component.validate(method)` loop; keep `ITIPValidator`; comment the two-level contract (D1).
- [x] 2.2 `ComponentValidator`: drop UID/DTSTAMP from the `OneOrLess` rules of VEVENT, VFREEBUSY, VAVAILABILITY (D2).
- [x] 2.3 `ComponentBuilder.hasName`: `equalsIgnoreCase` (D3).
- [x] 2.4 `msTimezones` and `msTimezoneNames`: `Central America Standard Time=America/Guatemala` (D4).
- [x] 2.5 `RecurrenceSet.Builder.build`: for `Duration` amounts and bounds without `ChronoUnit.SECONDS`, widen by `toDays() + 1` days (D5).
- [x] 2.6 `Period.intersects`: zero-length guards with half-open containment (D6).

## 3. Verify

- [x] 3.1 Focused tests green: CalendarValidatorImplTest, ComponentValidatorTest, ComponentBuilderTest, TzHelperTest, PeriodSpec, VEventSpec.
- [x] 3.2 `./gradlew check` green (full suite, RevAPI, checkstyle).
- [x] 3.3 `openspec validate validation-recurrence-small-fixes --strict`.

## 4. Land

- [ ] 4.1 Push `fix/validation-recurrence-small-fixes`; open PR against `develop` with "Fixes #363, fixes #736, fixes #691, fixes #692, fixes #709, fixes #82" and release-note wording.
