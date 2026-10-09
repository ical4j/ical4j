## 1. Regression tests first (red)

- [x] 1.1 Add `RecurDstOverlapSpec` with the #716 weekly BYDAY case (no `WKST`, `WKST=SU`, `WKST=MO`), the daily case seeded in PST and in PDT, the spring-forward gap case, and an `OffsetDateTime` seed case. Confirm the overlap cases fail on `develop`.

## 2. Fix `Recur`

- [x] 2.1 Add a private static `withEarlierOffsetAtOverlap(T)` helper that applies `ZonedDateTime.withEarlierOffsetAtOverlap()` and is the identity for other temporal types (D1, D2).
- [x] 2.2 Apply it to the result of `getCandidates` when the root seed is a `ZonedDateTime`.
- [x] 2.3 Document the RFC 5545 §3.3.5 rule in the `getCandidates` javadoc and on the helper.

## 3. Verify

- [x] 3.1 `./gradlew test --tests 'net.fortuna.ical4j.model.RecurDstOverlapSpec' --tests 'net.fortuna.ical4j.model.RecurSpec' --tests 'net.fortuna.ical4j.model.RecurTest'` green.
- [x] 3.2 `./gradlew check` green (full suite, RevAPI, checkstyle).
- [x] 3.3 `openspec validate recurrence-dst-overlap-earlier-offset --strict`.

## 4. Land

- [ ] 4.1 PR against `develop` referencing #716 with a "Behaviour change" release note.
