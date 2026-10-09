## 1. Regression tests first (red)

- [x] 1.1 In `TemporalAmountAdapterTest."verify string representation"`, change `Period.ofYears(1)` → `P365D`, `Period.ofMonths(6)` → `P183D`, `Period.ofMonths(-6)` stays `-P26W` (182 days back is an exact 26 weeks); add rows for `Period.ofMonths(1)` from 2020-06-01 (`P30D`), from 2021-02-01 (`P4W`), and `Period.ofYears(1)` from 2020-01-01 (`P366D`).
- [x] 1.2 Remove `@Ignore` from the two #419 tests and give them explicit seeds (D2). Confirm the changed rows fail on `develop`.

## 2. Fix `TemporalAmountAdapter.periodToString`

- [x] 2.1 Replace the years/months/days branches with a single calendar-day measurement; emit weeks only when the count is divisible by seven (D1). Update the method javadoc.

## 3. Verify

- [x] 3.1 `./gradlew test --tests 'net.fortuna.ical4j.model.TemporalAmountAdapterTest' --tests 'net.fortuna.ical4j.model.property.DurationSpec' --tests 'net.fortuna.ical4j.model.DurTest'` green.
- [x] 3.2 `./gradlew check` green (full suite, RevAPI, checkstyle).
- [x] 3.3 `openspec validate temporal-amount-days-not-weeks --strict`.

## 4. Land

- [ ] 4.1 PR against `develop` referencing #419 with a "Behaviour change" release note.
