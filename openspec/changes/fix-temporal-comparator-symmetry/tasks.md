## 1. Baseline

- [x] 1.1 Capture pre-change test counts: `./gradlew test` → record total/skipped/failures (baseline: 2694 tests, 51 skipped, 0 failures/errors)
- [x] 1.2 Confirm `TemporalComparatorTest.groovy` runs green pre-change (sanity) (14/14 pass)
- [x] 1.3 Document the four broken type pairs in a scratch `repro.md` (or in this branch's commit body) — Instant↔ZonedDateTime, LocalDateTime↔ZonedDateTime, and the unreached ZonedDateTime↔ZonedDateTime typed path — with concrete inputs that throw or return clipped values today. This is for the PR description / release notes, not committed. (captured in design.md D-1..D-5)

## 2. Add typed compare overloads (D2)

- [x] 2.1 In `src/main/java/net/fortuna/ical4j/model/TemporalComparator.java`, add `compare(Instant, ZonedDateTime)` and `compare(ZonedDateTime, Instant)` following the pattern of existing `compare(Instant, OffsetDateTime)` / `compare(OffsetDateTime, Instant)` (lines 165-171). Bodies: `o1.compareTo(o2.toInstant())` and `o1.toInstant().compareTo(o2)`.
- [x] 2.2 Add `compare(OffsetDateTime, ZonedDateTime)` and `compare(ZonedDateTime, OffsetDateTime)`. Bodies: `o1.toInstant().compareTo(o2.toInstant())` (symmetric, both pre-instant).
- [x] 2.3 Add `compare(LocalDateTime, ZonedDateTime)` and `compare(ZonedDateTime, LocalDateTime)`. Per D1, resolve LDT in `defaultZoneId`: `ZonedDateTime.of(ldt, defaultZoneId).toInstant().compareTo(zdt.toInstant())` (and symmetric reverse).
- [x] 2.4 Verify the new methods compile cleanly. No new imports required (all types are already in `java.time.*`).

## 3. Wire dispatch ladder (D3)

- [x] 3.1 In the `o1 instanceof Instant` branch (lines 49-59), add `else if (o2 instanceof ZonedDateTime) return compare(i1, (ZonedDateTime) o2);` after the `LocalDate` case.
- [x] 3.2 In the `o1 instanceof OffsetDateTime` branch (lines 60-68), add `else if (o2 instanceof ZonedDateTime) return compare(l1, (ZonedDateTime) o2);`.
- [x] 3.3 In the `o1 instanceof ZonedDateTime` branch (lines 69-73), add four new cases: `Instant`, `OffsetDateTime`, `ZonedDateTime`, `LocalDateTime`. Order them to match the o2-side conventions in other branches (Instant first, then OffsetDateTime, then ZonedDateTime, then LocalDateTime, then the existing LocalDate).
- [x] 3.4 In the `o1 instanceof LocalDateTime` branch (lines 74-84), add `else if (o2 instanceof ZonedDateTime) return compare(l1, (ZonedDateTime) o2);`.
- [x] 3.5 Leave the `o1 instanceof LocalDate` branch (lines 85-98) untouched — it already covers ZonedDateTime at line 91-92.
- [x] 3.6 Run `./gradlew compileJava` — verify the file compiles.

## 4. Symmetry property test (D5)

- [x] 4.1 In `src/test/groovy/net/fortuna/ical4j/model/TemporalComparatorTest.groovy`, add a `def 'compare is sign-antisymmetric for all supported temporal type pairs'()` spec. Use a pairwise data table over `{Instant, OffsetDateTime, ZonedDateTime, LocalDateTime, LocalDate}` with at least one instance per type, anchored at a fixed moment (e.g. `Instant.parse("2024-06-01T12:00:00Z")` and its zone-shifted equivalents) plus one moment offset by a few hours to exercise non-zero comparisons.
- [x] 4.2 The body asserts `Math.signum((double) comparator.compare(a, b)) == -Math.signum((double) comparator.compare(b, a))` for every pair.
- [x] 4.3 Add point cases for the four previously-broken pairs (D-1..D-4 in design.md):
    - `compare(instant_2024_06_01_12Z, zdt_2024_06_01_22_Sydney)` → 0 (equal instants)
    - `compare(zdt_2024_06_01_22_Sydney, instant_2024_06_01_12Z)` → 0
    - `compare(ldt_2024_06_01_12, zdt_2024_06_01_12_UTC)` with `defaultZoneId == UTC` → 0
    - `compare(zdt_2024_06_01_22_Sydney, zdt_2024_06_01_12_UTC)` → 0 (same instant, different zones)
    - One asymmetric pair (e.g. instant one hour later than zdt) returning > 0 / < 0 respectively.
- [x] 4.4 For the LDT/ZDT cases, construct the comparator with an explicit `defaultZoneId` (UTC) so the assertion is reproducible across CI timezones: `TemporalComparator comparator = new TemporalComparator(ZoneOffset.UTC)`.
- [x] 4.5 Run `./gradlew test --tests "net.fortuna.ical4j.model.TemporalComparatorTest"` — verify the new tests pass and the existing tests still pass.

## 5. Full test sweep

- [x] 5.1 Run `./gradlew clean test` — every test green, no regressions
- [x] 5.2 Run `./gradlew jacocoTestReport jacocoTestCoverageVerification` — coverage ≥ 0.7
- [x] 5.3 Spot-check downstream callers by reading the diff against git blame: confirm no behavioural change for like-type comparisons (Instant/Instant, ZDT/ZDT result sign matches `Instant.compareTo`).

## 6. Commit + PR

- [ ] 6.1 Single commit: `fix: make TemporalComparator symmetric for ZonedDateTime pairs`
    - Subject line ≤ 70 chars.
    - Body lists the four broken type pairs and links to the OpenSpec change folder.
    - `Co-Authored-By:` trailer as project standard.
- [ ] 6.2 Open PR. PR description: bullet list of the four broken pairs, link to `openspec/changes/fix-temporal-comparator-symmetry/`, callout that no public API is removed.
- [ ] 6.3 Once merged, archive the change folder via the openspec archive workflow.

## 7. Validation gates (at every commit boundary)

- [x] 7.1 `./gradlew clean test` passes
- [x] 7.2 No new compiler warnings
- [x] 7.3 JaCoCo ≥ 0.7
- [x] 7.4 No new `.groovy` files created (extending existing `TemporalComparatorTest.groovy` is acceptable per design.md R4)
