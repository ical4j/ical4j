## Context

The Java test tree has three coexisting test styles plus Groovy/Spock:

- **JUnit 3 (`extends TestCase`)** — 128 files. Of those, 105 use `public static TestSuite suite()` to provide parameterised inputs by constructing the test class multiple times with different `super("testMethodName")` targets. Two of the 128 are setUp/tearDown without `suite()`.
- **JUnit 4 (`@RunWith`, `org.junit.Test`)** — 17 files.
- **JUnit 5 Jupiter** — 14 files already migrated (`UidGeneratorTest`, `StringsTest`, `ConstantsTest`, `CnTest`, `CuTypeTest`, and earlier work).
- **Spock (Groovy)** — multiple files, already runs on the Jupiter platform via the Spock engine. **Out of scope.**

`build.gradle` keeps `junit-vintage-engine` + `junit:junit` on the test classpath so the legacy classes still execute. `useJUnitPlatform()` is already in place. JaCoCo enforces a 70% coverage minimum.

There is a multi-level custom test class hierarchy, not a single base class. All five base classes themselves extend `junit.framework.TestCase`, so the migration ordering matters:

```
TestCase
  ├─ AbstractPropertyTest                    (abstract; helper: assertValidationError)
  │     └─ PropertyTest                      (~50+ subclasses in model/property/)
  │            ├─ PriorityTest, SummaryTest, RepeatTest, RegionTest, …
  │            └─ (etc.)
  ├─ ParameterTest                           (parameter base — used by some subclasses;
  │                                           note: CnTest/CuTypeTest already migrated
  │                                           but never inherited from this base)
  ├─ ComponentTest                           (component base)
  │     └─ CalendarComponentTest<T>          (intermediate generic base)
  └─ FilterTest                              (filter base)
```

Subclasses typically call `super("testX", arg1, arg2, ...)` chaining the per-row constructor up to the base class. Migrating a base class is a load-bearing step: every subclass compiles against it. Subclasses cannot move ahead of their base.

The pre-existing `docs/JUNIT5_MIGRATION_GUIDE.md` undercounts the work: it claims ~14 parameterised cases but there are actually 105. The dominant pattern is the hardest one. The guide's "Phase 1: IntelliJ inspections auto-fix the easy 100" does not match the codebase and we should not rely on it. Corrected counts:

- 32 files directly `extends TestCase` (most of which ARE the base classes themselves or `*Test` files that play the role of base classes).
- 89 additional files import `junit.framework.*` (typically `TestSuite`) but extend a custom intermediate base instead of `TestCase` directly — making 121 total JUnit 3-flavoured files.
- 105 of the 121 have a `suite()` method.
- 17 files use JUnit 4 (`org.junit.Test`, `@RunWith`).
- 14 Java files are already on Jupiter.
- ~150 Java test files total.

## Goals / Non-Goals

**Goals:**
- All Java tests under `src/test/java/` run on JUnit Jupiter.
- `junit-vintage-engine` and `junit:junit` are removed from `build.gradle` once no Java test depends on them.
- Test count, intent, and coverage are preserved — no silent dropping of `suite()` rows.
- Migration is reviewable in phases (one commit per phase) and the build stays green at every phase boundary.
- `docs/JUNIT5_MIGRATION_GUIDE.md` is updated to reflect actual progress and the correct counts (or deleted, since the spec now anchors the work).

**Non-Goals:**
- **Touching Spock/Groovy tests in any way.** All 130 `*.groovy` files under `src/test/groovy/` are out of scope. They are not edited, moved, renamed, or otherwise affected by this change. They already run on the Jupiter platform via the Spock engine and require no migration. Scope is strictly the Java sources under `src/test/java/`.
- Rewriting test logic beyond what the framework switch requires.
- Renaming test methods, restructuring packages, or changing assertion libraries (Hamcrest matchers stay).
- Replacing custom `getName()` overrides with display-name machinery beyond what `@ParameterizedTest(name = ...)` provides.
- Introducing new test utilities or base classes beyond what's needed to migrate `AbstractPropertyTest`.

## Decisions

### D1. Phased commit strategy (single PR, multiple commits)

```
Phase 1 ─ JUnit 4 → Jupiter (17 files; lightest transformation, no class hierarchy)
Phase 2 ─ JUnit 3 simple cases (extends TestCase directly, no suite(), no base
            class consumers)
Phase 3 ─ JUnit 3 setUp/tearDown cases (no base class consumers)
Phase 4 ─ JUnit 3 base-class + subclass bundles (atomic per tree):
              4a. AbstractPropertyTest + PropertyTest + ~54 property subclasses
              4b. ParameterTest + ~20 parameter subclasses
              4c. ComponentTest + CalendarComponentTest + ~12 component subclasses
              4d. FilterTest + filter subclasses (filter/*, filter/predicate/*)
              4e. Standalone parameterised tests in model/*
                    (DateTest, DateTimeTest, RecurTest, TimeZoneTest, …)
              4f. util/* parameterised (CalendarsTest, UrisTest)
Phase 5 ─ Drop junit-vintage-engine and junit:junit from build.gradle;
          update or delete docs/JUNIT5_MIGRATION_GUIDE.md
```

**Phase 0 (base classes) is intentionally folded into Phase 4.** The original plan listed a standalone "Phase 0 bootstrap" for base classes, but that approach silently loses tests:

- `junit-vintage-engine` discovers JUnit 3 tests by walking the class hierarchy for `TestCase` descendants.
- The moment `AbstractPropertyTest` stops extending `TestCase`, every subclass that transitively extended it (~54 property tests) becomes invisible to vintage.
- Those subclasses' `public void testX()` methods don't yet have `@Test` annotations, so Jupiter doesn't pick them up either.
- Net effect: a standalone Phase 0 commit would land green only because the tests aren't running at all — silent loss violates spec scenario "Test invocation count is monotonic".

Keeping a no-op `String name` constructor on the migrated base (option (a) from the original task list) doesn't help — it keeps the code compiling but doesn't restore test discovery. The only safe approach is to migrate each base together with every subclass that depends on it in a single atomic commit (option (b), bundled).

The 4a commit is large (~55 files), but atomicity is non-negotiable for the spec's no-silent-loss guarantee. 4a can be staged as one logical commit with multiple files; review burden is high but bounded — every file change is mechanical translation of the same pattern.

Each phase ends with `./gradlew test` green and a commit. Phase 4 may be split internally by package (model/property/, model/parameter/, util/, data/, …) to keep commits reviewable.

**Why this order:** AbstractPropertyTest is a dependency of many phase-2/3/4 files, so it must move first. JUnit 4 is the simplest transformation and provides confidence in tooling. Simple TestCase comes next (no parameters), then setUp/tearDown (one extra annotation swap), then the gnarly parameterised pattern last. Build dependencies (`junit-vintage`, `junit:junit`) are dropped only after the last consumer is gone.

**Alternative considered:** Migrate file-by-file alphabetically. Rejected: mixes transformation patterns in every commit and makes review noisy.

### D2. PATTERN 3 translation: one @ParameterizedTest per target method

For a file like `DateTest` that calls `super("testToString")` and `super("testEquals")` from two different constructors:

```java
// BEFORE (sketch)
public DateTest(Date d, String s)        { super("testToString"); ... }
public DateTest(Date d, java.util.Date e){ super("testEquals");   ... }
public void testToString() { ... }
public void testEquals()   { ... }
public static TestSuite suite() {
    suite.addTest(new DateTest(new Date(0L), "19700101"));    // → testToString
    suite.addTest(new DateTest(d, e));                         // → testEquals
}

// AFTER
@ParameterizedTest(name = "toString [{0}]")
@MethodSource("toStringData")
void testToString(Date d, String s) { ... }

@ParameterizedTest(name = "equals [{0}]")
@MethodSource("equalsData")
void testEquals(Date d, java.util.Date e) { ... }

static Stream<Arguments> toStringData() throws ParseException { return Stream.of(...); }
static Stream<Arguments> equalsData()   throws ParseException { return Stream.of(...); }
```

**Rules:**
- One `@ParameterizedTest` per original test method, with its own `@MethodSource`.
- Each `suite.addTest(new X(...))` row maps to one `Arguments.of(...)` in the matching source method, **based on the `super("testX")` of the constructor that built it**.
- The number of test invocations after migration MUST equal the number of `suite.addTest(...)` calls before migration. If a `for` loop or helper builds rows, the source method MUST produce the same number of rows.
- `getName()` overrides become `@ParameterizedTest(name = "...")` patterns using positional refs (`{0}`, `{1}`). Where dynamic naming used non-arg state, we accept the simpler `@ParameterizedTest(name = "<method> [{0}]")` form.
- Static initialiser logic (e.g., `Calendar` setup in `DateTest.suite()`) moves into the `@MethodSource` method, which may declare `throws ParseException` etc.

**Alternative considered:** A single `@ParameterizedTest` with an enum/discriminator arg dispatching to the right assertions. Rejected: works against Jupiter idioms, makes each row harder to read, and the per-method approach matches the original test structure 1:1.

### D3. Assertion call sites: message position

JUnit 3/4: `assertEquals("msg", expected, actual)` — message first.
Jupiter: `assertEquals(expected, actual, "msg")` — message last.

For every migrated file, audit each assertion: if it has a String literal as the first arg with one or more values after it (heuristic: first arg is `String`, more than 2 args total, return-type of expected matches return-type of actual), swap to message-last. When in doubt, prefer Jupiter's overload that matches the obvious "two-things-being-compared" case and put the message at the end.

**Alternative considered:** Leaving Hamcrest-style `assertThat(x, is(y))` wherever the test already uses it. **Kept.** No reason to touch Hamcrest call sites.

### D4. `AbstractPropertyTest` migration

Remove `extends TestCase`, drop the two `super()` constructors (no longer needed). Keep `assertValidationError` as a protected static-style helper. It already uses Jupiter-compatible `assertTrue(result.hasErrors())` — add a static import `import static org.junit.jupiter.api.Assertions.assertTrue;`.

Subclasses keep extending `AbstractPropertyTest` but no longer inherit `TestCase`. This is the load-bearing change for Phase 0.

### D5. Documentation

- Keep `docs/JUNIT5_MIGRATION_GUIDE.md` updated in the same PR with corrected counts and the actual pattern distribution, **or** delete it and let this OpenSpec change anchor the work. Default: **delete** when the migration is archived — the spec is the durable record. Until archive, keep the doc but correct the counts.

## Risks / Trade-offs

- **[R1] Silent test loss** — `suite()` rows quietly dropped during translation. **Mitigation:** before/after row count comparison per file; spot-check `./gradlew test` output for the same invocation count.
- **[R2] PATTERN 3 with hidden state** — constructors that set instance fields used by *multiple* test methods (e.g., test A depends on field set by constructor variant 1, test B from variant 2). **Mitigation:** when a `super("testX")` constructor sets fields used only by `testX`, the per-method split is mechanical. When a single constructor is reused across multiple test methods, the file needs case-by-case design — annotate in the design as we go, do not force the rule.
- **[R3] Coverage regression** — JaCoCo's 70% threshold could trip if a migration accidentally removes assertions. **Mitigation:** run `./gradlew jacocoTestReport` at each phase boundary; the build's existing `failOnViolation = false` means it won't break CI, but we check the number manually.
- **[R4] Dependency removal breaks Spock or testcontainers** — vintage engine may be transitively expected. **Mitigation:** Phase 5 runs the full suite after removing dependencies; if Spock or testcontainers needs vintage, add it back with a `// keep for Spock` comment and document why. Spock tests themselves are out of scope and MUST NOT be edited — the only acceptable response to a Spock failure caused by Phase 5 is restoring the missing test-classpath dependency, never editing a `.groovy` file.
- **[R5] Custom `getName()` overrides** — used in JUnit 3 to make failure output identify which row failed (`DateTest` does this). **Mitigation:** translate via `@ParameterizedTest(name = "...")`. Some loss of richness is acceptable.
- **[R6] Repetitive boredom errors** — 105 mechanical translations invite copy-paste mistakes. **Mitigation:** review per file; do not batch more than ~10 files per commit in Phase 4.

## Migration Plan

The migration plan IS the phased commit strategy in D1. Rollback strategy: each phase is its own commit, so `git revert <phase-commit>` cleanly reverses one phase. Phase 5 (dependency removal) is the only non-revertible-without-thought step — and even there, reverting just restores two `gradle` lines.

## Open Questions

- **Q1**: Are there any tests where `super("testX")` chooses a method dynamically (e.g., based on the data passed)? Initial grep doesn't show any but Phase 4 will confirm.
- **Q2**: Should `@DisplayName` annotations be added at file/method level for readability? Default: **no** — out of scope, can be a follow-up.
- **Q3**: Are there any tests that depend on JUnit 3's `TestSuite` ordering (e.g., test A runs before test B)? Jupiter doesn't guarantee order. If found, address per-file with `@TestMethodOrder` rather than blocking the migration.
