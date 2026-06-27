## Context

`TemporalComparator` is the single comparator used throughout ical4j to order `java.time.Temporal` values regardless of their concrete type. It backs `DateProperty.compareTo`, `DtStamp.compareTo`, `Period.intersects`, `Recur.CANDIDATE_SORTER`, `ZoneRulesBuilder` observance ordering, `TemporalAdapter.isBefore`/`isAfter`, and `BySetPosRule`.

The current `compare(Temporal, Temporal)` entry point uses a hand-written `instanceof` ladder to dispatch to typed `compare(...)` overloads. The ladder has gaps. Concretely:

```
                              o2:
              ┌─────────┬──────────┬──────────┬──────────┬──────────┐
              │ Instant │ OffsetDT │ ZonedDT  │ LocalDT  │ LocalDate│
       ┌──────┼─────────┼──────────┼──────────┼──────────┼──────────┤
       │Instant│ typed  │  typed   │   FALL   │  typed   │  typed   │
       ├──────┼─────────┼──────────┼──────────┼──────────┼──────────┤
   o1: │OffsDT │ typed  │  typed   │   FALL   │  typed   │  typed   │
       ├──────┼─────────┼──────────┼──────────┼──────────┼──────────┤
       │ZonedDT│ FALL   │   FALL   │   FALL!  │   FALL   │  typed   │
       ├──────┼─────────┼──────────┼──────────┼──────────┼──────────┤
       │LocalDT│ typed  │  typed   │   FALL   │  typed   │  typed   │
       ├──────┼─────────┼──────────┼──────────┼──────────┼──────────┤
       │LocDate│ typed  │  typed   │   typed  │  typed   │  typed   │
       └──────┴─────────┴──────────┴──────────┴──────────┴──────────┘

   FALL  = falls through to default fallback (ChronoUnit.SECONDS.between)
   FALL! = ZonedDT vs ZonedDT — typed method `compare(ZonedDateTime, ZonedDateTime)`
           exists at TemporalComparator:140 but the dispatch never reaches it.
```

### Why the fallback is asymmetric

```java
long diff = defaultComparisonUnit.between(o2, o1);
return diff > 0 ? Integer.MAX_VALUE : diff < 0 ? Integer.MIN_VALUE : 0;
```

`ChronoUnit.between(start, end)` (per `java.time.temporal.ChronoUnit` Javadoc) converts `end` to the type of `start` before subtracting. So the conversion direction is determined by argument order.

For the four ZonedDateTime pairs that currently fall through:

| Call | Conversion (effective) | Outcome |
|------|------------------------|---------|
| `compare(zdt, instant)` → `between(instant, zdt)` | `ZonedDateTime.from(instant)` | **throws** `DateTimeException` — Instant has no zone |
| `compare(instant, zdt)` → `between(zdt, instant)` | `Instant.from(zdt)` | succeeds; returns signed seconds |
| `compare(zdt, ldt)`     → `between(ldt, zdt)`     | `LocalDateTime.from(zdt)` | succeeds but **silently drops zone** — compares wall-clock times |
| `compare(ldt, zdt)`     → `between(zdt, ldt)`     | `ZonedDateTime.from(ldt)` | **throws** — LocalDateTime has no zone/instant |
| `compare(zdt, odt)`     → `between(odt, zdt)`     | `ZonedDateTime.from(odt)` | succeeds (offset → zone) |
| `compare(odt, zdt)`     → `between(zdt, odt)`     | `OffsetDateTime.from(zdt)` | succeeds (zone instant → offset) |
| `compare(zdt, zdt')`    → `between(zdt', zdt)`    | both already match           | succeeds; result clipped to MAX/MIN |

Two of these cases throw in exactly one direction, violating `Comparator`'s symmetry contract. One silently drops zone information without throwing — which is worse, because callers see a result but it's wrong.

### Why the typed methods are precise but the fallback is clipped

The existing typed `compare(...)` overloads return `o1.compareTo(o2)` (a signed `int` derived from milliseconds/nanoseconds difference). The fallback returns `Integer.MAX_VALUE`/`MIN_VALUE`/`0`. This means sort routines that depend on stable, comparable magnitudes (e.g. anything that does `Math.min`, `Math.signum`, or summation of comparator results) see inconsistent behaviour across type pairs.

### Defect inventory (this change addresses)

| ID | Description | Current location |
|----|-------------|------------------|
| D-1 | `compare(ZonedDateTime, Instant)` and `compare(Instant, ZonedDateTime)` throw vs succeed (asymmetric exception path) | `TemporalComparator.java` default fallback |
| D-2 | `compare(ZonedDateTime, LocalDateTime)` silently drops zone; reverse direction throws | `TemporalComparator.java` default fallback |
| D-3 | `compare(ZonedDateTime, ZonedDateTime)` falls through to clipped fallback despite the typed method existing | `TemporalComparator.java` dispatch ladder vs line 140 |
| D-4 | `compare(ZonedDateTime, OffsetDateTime)` and reverse work but go through magnitude-clipping fallback | `TemporalComparator.java` default fallback |
| D-5 | `TemporalComparatorTest.groovy` has zero ZonedDateTime cases — the gaps are not test-covered | `src/test/groovy/.../TemporalComparatorTest.groovy` |

## Goals / Non-Goals

**Goals:**
- For every pair of types from `{Instant, OffsetDateTime, ZonedDateTime, LocalDateTime, LocalDate}`, `compare(a, b)` and `compare(b, a)` return values with opposite (or both zero) signs.
- No `compare(...)` call between values of those five types throws `DateTimeException` from internal conversion. (Genuinely ambiguous floating-time comparisons against zoned values still resolve via `defaultZoneId`, not by throwing.)
- For instant-resolvable types (Instant, OffsetDateTime, ZonedDateTime, and LocalDateTime via `defaultZoneId`), the comparison reflects instant ordering, not wall-clock.
- Add a parameterised test that asserts the symmetry property structurally, so future additions to the dispatch are guarded.

**Non-Goals:**
- Rewriting the comparator around a canonical-form (`Instant`-where-possible) pipeline. Surgical fix preferred.
- Changing behaviour for like-type pairs that already work (`Instant`/`Instant`, `LocalDate`/`LocalDate`, etc.).
- Touching the default fallback's behaviour for non-enumerated Temporal types (`Year`, `YearMonth`, custom Temporal). No production caller passes those.
- Touching any `.groovy` test file beyond the existing `TemporalComparatorTest.groovy`, except by extending it (out of scope per `test-framework` spec is *introduction* of new groovy tests; extending an existing one is acceptable).

## Decisions

### D1. Floating-vs-zoned semantics: resolve LocalDateTime in `defaultZoneId`

When comparing a `LocalDateTime` (floating) against a `ZonedDateTime`, the floating value is treated as wall-clock time in `defaultZoneId`:

```java
public int compare(LocalDateTime o1, ZonedDateTime o2) {
    return ZonedDateTime.of(o1, defaultZoneId).toInstant().compareTo(o2.toInstant());
}
public int compare(ZonedDateTime o1, LocalDateTime o2) {
    return o1.toInstant().compareTo(ZonedDateTime.of(o2, defaultZoneId).toInstant());
}
```

**Why `defaultZoneId` and not `zdt.getZone()`?** Two reasons:
- The existing `compare(Instant, LocalDateTime)` overload (line 158) already uses `defaultZoneId` to resolve floating values. Using `zdt.getZone()` for the new pair would introduce an asymmetric convention within the same class: "if you compare floating-vs-Instant, we use defaultZoneId; if you compare floating-vs-ZonedDateTime, we use the ZDT's zone." Confusing.
- Using `zdt.getZone()` would make the comparator non-deterministic for the same LocalDateTime: it would compare as different instants depending on which ZonedDateTime it was paired with. A symmetric pair `(ldt, zdt1)` and `(ldt, zdt2)` could disagree on whether `ldt` is "before" anything, depending solely on whether `zdt1.getZone() == zdt2.getZone()`.

**Alternative considered:** Treat the comparison as wall-clock — i.e. `ldt.compareTo(zdt.toLocalDateTime())`. Rejected: this means a ZonedDateTime in Asia/Tokyo compares "earlier" than a ZonedDateTime in America/Los_Angeles for the same instant, which contradicts what every other path in the class does (instant-based comparison).

### D2. Instant-based comparison for all date-time pairs

Every new overload resolves both operands to `Instant` and uses `Instant.compareTo`:

```java
public int compare(Instant o1, ZonedDateTime o2)        { return o1.compareTo(o2.toInstant()); }
public int compare(ZonedDateTime o1, Instant o2)        { return o1.toInstant().compareTo(o2); }
public int compare(OffsetDateTime o1, ZonedDateTime o2) { return o1.toInstant().compareTo(o2.toInstant()); }
public int compare(ZonedDateTime o1, OffsetDateTime o2) { return o1.toInstant().compareTo(o2.toInstant()); }
public int compare(LocalDateTime o1, ZonedDateTime o2)  { return ZonedDateTime.of(o1, defaultZoneId).toInstant().compareTo(o2.toInstant()); }
public int compare(ZonedDateTime o1, LocalDateTime o2)  { return o1.toInstant().compareTo(ZonedDateTime.of(o2, defaultZoneId).toInstant()); }
```

These mirror the existing pattern for `Instant`/`OffsetDateTime` interactions (lines 165-178 in the current file).

**Trade-off:** `Instant.compareTo` returns a signed `int` derived from nano-of-second precision. This is more precise than the `ChronoUnit.SECONDS.between` fallback. For pairs that previously fell into the fallback and returned MAX/MIN, the new behaviour returns a typical small int — any caller doing `Math.signum`-style use is unaffected; any caller doing magnitude arithmetic on the result was already wrong.

### D3. Dispatch ladder additions

Add the missing branches in `compare(Temporal o1, Temporal o2)`:

```java
} else if (o1 instanceof Instant) {
    ...
    } else if (o2 instanceof ZonedDateTime) {
        return compare(i1, (ZonedDateTime) o2);    // NEW
    }
} else if (o1 instanceof OffsetDateTime) {
    ...
    } else if (o2 instanceof ZonedDateTime) {
        return compare(l1, (ZonedDateTime) o2);    // NEW
    }
} else if (o1 instanceof ZonedDateTime) {
    var l1 = (ZonedDateTime) o1;
    if (o2 instanceof Instant) {
        return compare(l1, (Instant) o2);          // NEW
    } else if (o2 instanceof OffsetDateTime) {
        return compare(l1, (OffsetDateTime) o2);   // NEW
    } else if (o2 instanceof ZonedDateTime) {
        return compare(l1, (ZonedDateTime) o2);    // NEW (wires existing method)
    } else if (o2 instanceof LocalDateTime) {
        return compare(l1, (LocalDateTime) o2);    // NEW
    } else if (o2 instanceof LocalDate) {
        return compare(l1, (LocalDate) o2);        // already present
    }
} else if (o1 instanceof LocalDateTime) {
    ...
    } else if (o2 instanceof ZonedDateTime) {
        return compare(l1, (ZonedDateTime) o2);    // NEW
    }
}
```

The `LocalDate` branch already has `o2 instanceof ZonedDateTime` (line 91-92), so it stays untouched.

### D4. Leave the default fallback alone

After D3 the fallback is no longer reachable for any pair drawn from the five enumerated Temporal types. Touching it would expand the scope without a clear benefit — non-enumerated Temporal types (e.g. `Year`, `YearMonth`) are not used by any production caller in the codebase, so we have no test cases to validate a change. The clip-to-MAX/MIN behaviour also has the right symmetry property mathematically (`between(a,b) = -between(b,a)`), even if it loses magnitude.

**Alternative considered:** Replace the clip with `Long.signum(diff)`. Cleaner output, no test-able callers, deferred.

### D5. Symmetry property test

Add to `TemporalComparatorTest.groovy`:

```groovy
def 'compare is sign-antisymmetric for all supported temporal type pairs'() {
    given: 'a comparator instance'
    TemporalComparator comparator = []

    expect: 'sgn(compare(a, b)) == -sgn(compare(b, a))'
    Math.signum(comparator.compare(a, b)) == -Math.signum(comparator.compare(b, a))

    where:
    a << supportedTemporals()
    b << supportedTemporals().reverse()  // or any non-identity pairing
}
```

Where `supportedTemporals()` returns a fixed set of `Instant`, `OffsetDateTime`, `ZonedDateTime`, `LocalDateTime`, `LocalDate` instances at varied moments. The structural assertion `sgn(compare(a,b)) == -sgn(compare(b,a))` is the comparator contract restated. If a future addition to the dispatch ladder breaks symmetry for any pair in the cartesian product, this test fails.

Also add point cases for the four previously-broken pairs (per defect inventory D-1..D-4), with concrete expected sign:

- `Instant.parse("2024-06-01T12:00:00Z")` vs `ZonedDateTime.parse("2024-06-01T22:00+10:00[Australia/Sydney]")` — equal instants, expect 0
- `LocalDateTime.parse("2024-06-01T12:00")` vs `ZonedDateTime.parse("2024-06-01T12:00+10:00[Australia/Sydney]")` — depends on `defaultZoneId`; assert against a fixed UTC `defaultZoneId` to make the expectation deterministic.

### D6. Use Australia/Sydney plus UTC as the test zones

The existing test suite uses `LocalDate.now()`/`Instant.now()` style "current time" anchors. The new ZonedDateTime cases need fixed anchor points to make assertions deterministic across timezone-equipped CI runners. Use `Australia/Sydney` and `UTC` as the two reference zones in symmetry tests (matches the project's own timezone affiliation; `UTC` is the unambiguous fallback).

## Risks / Trade-offs

- **[R1] Behavioural change for callers who were silently getting wrong answers.** The current `compare(zdt, ldt)` returns a wall-clock comparison (drops zone). After the fix it returns an instant comparison. Any caller depending on the wall-clock semantics was already broken — `compare(ldt, zdt)` threw on the same input. Direction matches what `compare(Instant, LocalDateTime)` already does. **Mitigation:** call out in the release notes that mixed LDT/ZDT comparisons now use `defaultZoneId` to resolve LDT into an instant.
- **[R2] Default fallback remains MAX/MIN-clipped.** For Temporal types beyond the five enumerated, behaviour is unchanged. **Mitigation:** none needed; no production caller passes those.
- **[R3] `defaultZoneId` choice changes results vs `zdt.getZone()` choice.** D1 documents the rationale. Tests pin specific zones to make this verifiable.
- **[R4] Test suite touches `.groovy`.** Standing scope rule from `test-framework` spec restricts *new* groovy tests; extending an existing test is reading-the-letter-vs-spirit. **Mitigation:** extend the existing `TemporalComparatorTest.groovy` rather than creating a new test file. If the project would prefer the new property tests in Java JUnit 5, add them there instead — both are acceptable; the structural assertion is what matters.

## Migration Plan

```
1. Add the six new typed compare(...) overloads with instant-based semantics
2. Add the missing dispatch branches in compare(Temporal, Temporal)
3. Add ZonedDateTime point cases + symmetry property test to TemporalComparatorTest
4. Run ./gradlew test — verify no regressions across all callers
5. Spot-check Period.intersects, DateProperty.compareTo, Recur ordering with mixed-type inputs
6. Commit
```

Each step is a single commit. Steps 1-3 can be combined into one commit since they are atomic (the new overloads aren't reachable without the dispatch changes, and the tests would fail without the overloads). Rollback: revert the commit.

## Open Questions

- **Q1**: Should the new symmetry test live in the existing `TemporalComparatorTest.groovy` or a new Java JUnit 5 file? Default: extend the groovy test for proximity to existing cases. If the project would rather not extend `.groovy` files, add a Java test under `src/test/java/net/fortuna/ical4j/model/`.
- **Q2**: Should the fallback path's MAX/MIN clipping be changed to `Integer.signum(...)` in this change? Default: **no** — out of scope, no test-able caller exercises it.
- **Q3**: Should we add a deprecation note to the public typed `compare(...)` overloads suggesting callers use `compare(Temporal, Temporal)` instead? Default: **no** — the typed overloads are useful and documented. The fix is to wire them into the dispatch, not to discourage them.
