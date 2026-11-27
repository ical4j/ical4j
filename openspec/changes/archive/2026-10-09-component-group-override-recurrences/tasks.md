## 1. Regression tests first (red)

- [x] 1.1 In `ComponentGroupTest`, change `"CalculateRecurrenceSetWithException"` to expect `master.size()` periods, no period on 2010-11-29, and a one-day period on 2010-11-30 whose `getComponent()` is `rev3`. Confirm it fails on current `develop`.
- [x] 1.2 Add the #510 case: daily 30-minute UTC master, bare `RECURRENCE-ID:20210715T000000Z` override rescheduled to `DTSTART;VALUE=DATE:20210714`; query 14–16 July; assert no `2021-07-15T00:00Z` period and a one-day `2021-07-14` period. Build it from the raw `.ics` text via `CalendarBuilder` (not `ContentBuilder`) so the parser's property typing is exercised.
- [x] 1.3 Add the parameterised variant of 1.2 (`RECURRENCE-ID;TZID=UTC:20210715T000000`) asserting the same result.
- [x] 1.4 Add "override keeps start, changes duration": `RECURRENCE-ID` = `DTSTART` = `20210715T000000Z`, `DTEND:20210715T020000Z`; assert exactly one period at that start with `PT2H`.
- [x] 1.5 Add "override moved outside the query period": `RECURRENCE-ID:20210715T000000Z`, `DTSTART:20210801T000000Z`; query 14–16 July; assert the 15th is absent and nothing was added for the override.
- [x] 1.6 Add "override carrying the master's RRULE" (#472 guard): override with `RRULE:FREQ=DAILY`, `DTSTART:20210715T010000Z`; query 14–18 July; assert exactly one override period and the master instances on 14, 16, 17 July present once each.
- [x] 1.7 Add "zoned master, UTC RECURRENCE-ID": `DTSTART;TZID=Europe/Paris:20210715T020000` daily master, override `RECURRENCE-ID:20210716T000000Z` with `DTSTART;TZID=Europe/Paris:20210716T090000`; assert the 00:00Z instant is absent and the 09:00 Paris period is present.
- [x] 1.8 Keep `"CalculateRecurrenceSet"` (master + non-override revision) unchanged as the no-override regression guard.

## 2. Fix `ComponentGroup.calculateRecurrenceSet`

- [x] 2.1 Add a private final `Uid uid` field, assigned in every constructor (D1).
- [x] 2.2 In `calculateRecurrenceSet`, replace `getRevisions()` with `getComponents()` filtered by `new PropertyEqualToRule<C>(uid)` so overrides are found regardless of `RECURRENCE-ID` parameters. Leave `getRevisions()` and the constructor predicates untouched.
- [x] 2.3 Replace the per-override `component.calculateRecurrenceSet(period).filter(start == recurrenceId)` with a `RecurrenceSet.Builder` seeded only from the override's `DTSTART`, `DTEND`|`DUE`, `DURATION` and the query `period` (no rules/dates), and call `setComponent(override)` on the resulting period(s) before adding them (D2). Extract this into a private helper (e.g. `overrideOccurrence(Component, Period)`) so the intent is readable.
- [x] 2.4 Change the master-instance removal to `TemporalComparator.INSTANCE.compare(p.getStart(), recurrenceId.getDate()) == 0` (D3).
- [x] 2.5 Update the `calculateRecurrenceSet` javadoc to state that overrides are matched by `UID` only, contribute their own single occurrence, and that `RRULE`/`RDATE` on an override are ignored; reference RFC 5545 §3.8.4.4 and #510/#472.

## 3. Verify

- [x] 3.1 `./gradlew test --tests 'net.fortuna.ical4j.model.ComponentGroupTest'` — all green, including the tests that were red in step 1.
- [x] 3.2 `./gradlew check` passes (full suite + RevAPI + checkstyle). If RevAPI reports anything for `ComponentGroup`, record the justification in `.palantir/revapi.yml`.
- [x] 3.3 `openspec validate component-group-override-recurrences --strict`.
- [x] 3.4 Add a "Fixed" entry to the changelog/release notes noting: rescheduled overrides now appear; bare `RECURRENCE-ID` overrides are now applied; `Period.getComponent()` on an override instance returns the override. **Repo CHANGELOG.md is unmaintained (last entry 1.0-beta5); wording captured in the PR description for the website release notes.**

## 4. Land

- [x] 4.1 Open a PR against `develop` referencing #510 (and #472 for the preserved guard); include the before/after period lists from the #510 reproduction in the description. **PR #931.**
- [x] 4.2 After merge, comment on #510 with the `develop` snapshot coordinates and ask roubert/vkrupach to confirm; close on confirmation or at the next release. **Commented 2026-10-09 asking for confirmation; issue left open pending reply or next release.**
