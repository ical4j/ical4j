## Why

`TemporalComparator` (`src/main/java/net/fortuna/ical4j/model/TemporalComparator.java`) dispatches its `compare(Temporal, Temporal)` entry point through a hand-written `instanceof` ladder. The ladder is asymmetric: `ZonedDateTime` is wired to typed comparison only when paired with `LocalDate`. All other ZonedDateTime pairings — and even `ZonedDateTime` vs `ZonedDateTime` — fall through to a generic `ChronoUnit.SECONDS.between(o2, o1)` fallback.

That fallback is not symmetric for every type pair. `ChronoUnit.between(start, end)` converts `end` to `start`'s type:

- `compare(zdt, instant)` → `between(instant, zdt)` → `ZonedDateTime.from(instant)` throws `DateTimeException` (no zone).
- `compare(instant, zdt)` → `between(zdt, instant)` → `Instant.from(zdt)` succeeds.
- `compare(zdt, ldt)` → `between(ldt, zdt)` → `LocalDateTime.from(zdt)` succeeds but silently drops zone.
- `compare(ldt, zdt)` → `between(zdt, ldt)` → `ZonedDateTime.from(ldt)` throws.

So one comparison direction throws while the other returns a value — a clear violation of `Comparator`'s `sgn(compare(a,b)) == -sgn(compare(b,a))` contract, and a latent exception path in code that callers (`Period.intersects`, recurrence sorting in `Recur.CANDIDATE_SORTER`, `DateProperty.compareTo`, `DtStamp.compareTo`, `ZoneRulesBuilder`'s observance sort) treat as total.

The comparator also returns `Integer.MAX_VALUE` / `Integer.MIN_VALUE` from the fallback path, losing the per-second precision that the typed branches preserve. Downstream code that consumes the comparator as a sort key sees inconsistent magnitudes depending on which type pair it gets handed.

## What Changes

- **Wire ZonedDateTime through the dispatch ladder**: every `o1` branch (Instant, OffsetDateTime, ZonedDateTime, LocalDateTime, LocalDate) gains an `o2 instanceof ZonedDateTime` case that forwards to a typed `compare` method. The `ZonedDateTime` `o1` branch additionally covers Instant, OffsetDateTime, ZonedDateTime, and LocalDateTime.
- **Add six new typed `compare` overloads** for the previously unhandled pairs:
  - `compare(Instant, ZonedDateTime)` and `compare(ZonedDateTime, Instant)`
  - `compare(OffsetDateTime, ZonedDateTime)` and `compare(ZonedDateTime, OffsetDateTime)`
  - `compare(LocalDateTime, ZonedDateTime)` and `compare(ZonedDateTime, LocalDateTime)`

  These resolve both operands to `Instant` and compare. Floating values (`LocalDateTime`) are resolved using `defaultZoneId`, matching the convention already established by the existing `compare(Instant, LocalDateTime)` overload (line 158-160 of the current file).
- **Dispatch ZonedDateTime vs ZonedDateTime** to the existing typed `compare(ZonedDateTime, ZonedDateTime)` method (the method exists at line 140 but is currently unreachable from the dispatch).
- **Preserve compareTo precision** in the typed paths. The default fallback at the bottom of `compare(Temporal, Temporal)` becomes a last-resort path for type combinations the class does not enumerate; it is left as-is (the dispatch additions make it unreachable for the five enumerated Temporal types).
- **Symmetry property test**: add a parameterised test that asserts, for every pair `(a, b)` drawn from `{Instant, OffsetDateTime, ZonedDateTime, LocalDateTime, LocalDate}`, that `sgn(compare(a, b)) == -sgn(compare(b, a))`. This is the structural guarantee the change establishes.
- **No public API removal.** The new typed `compare` methods are additive. The `Comparator<Temporal>` contract is unchanged.

## Capabilities

### New Capabilities

- `temporal-comparison`: Defines the contract for `TemporalComparator` — what types it accepts, how mixed-type pairs are resolved, and the symmetry guarantee.

### Modified Capabilities

<!-- None — this is the first capability for the model.temporal comparator. -->

## Impact

- **Affected code**:
  - `src/main/java/net/fortuna/ical4j/model/TemporalComparator.java` — dispatch ladder extended, six new typed `compare` methods added.
- **Affected callers (behaviourally)**: every site listed in `grep -rn "TemporalComparator"` — most notably `Period.intersects`, `DateProperty.compareTo`, `DtStamp.compareTo`, `Recur.CANDIDATE_SORTER`, `ZoneRulesBuilder` observance ordering, `TemporalAdapter.isBefore`/`isAfter`, `BySetPosRule`. Previously these could throw `DateTimeException` when given mixed Instant/ZonedDateTime or LocalDateTime/ZonedDateTime pairs; after the change they return signed `int` consistent with the operands' instant ordering.
- **Test coverage additions**:
  - Extend `src/test/groovy/net/fortuna/ical4j/model/TemporalComparatorTest.groovy` (or add a Java test) with pairwise symmetry assertions and ZonedDateTime-vs-others cases.
- **No public API removal.** No deprecations. No behaviour change for the all-LocalDate, all-Instant, all-LocalDateTime, all-OffsetDateTime, or all-ZonedDateTime cases (the last is now wired to its typed method, but the typed method already returned `o1.compareTo(o2)`, which is what `ChronoUnit.SECONDS.between` was approximating with clipped magnitudes).
- **Bug-fix nature**: this is a correctness fix. Callers who happened to avoid the broken type combinations see no change; callers who hit them previously got an exception and now get a correct comparison. There is no scenario in which a previously-working call returns a different sign.
- **Coverage**: must remain ≥0.7 (existing jacocoTestCoverageVerification rule). The change adds tests, so coverage should not regress.
- **Out of scope**:
  - Rewriting the comparator around a canonical-form conversion (would be a larger refactor; surgical fix preferred for the current release).
  - Touching the default fallback's MAX/MIN clipping for unenumerated Temporal types (e.g. `Year`, `YearMonth`). No callers in the codebase exercise those types.
  - Spock/Groovy test framework changes (standing scope rule from `test-framework` spec).
