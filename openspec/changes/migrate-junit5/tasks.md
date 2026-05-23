## 1. Phase 0 — Bootstrap base classes (FOLDED INTO PHASE 4)

The original "migrate base classes first" plan was rejected because the `junit-vintage-engine` walks the class hierarchy looking for `TestCase` descendants. Removing `TestCase` from any base silently un-discovers every subclass — those subclasses still have plain `public void testX()` methods without `@Test` annotations, so Jupiter never picks them up either. See design.md "Phase 0 (base classes) is intentionally folded into Phase 4" for the full rationale.

Each base class is now migrated *together with* every subclass that depends on it, in one atomic commit per tree (see Phase 4).

- [x] 1.1 Decision recorded: bundle base + all subclasses per commit (option (b) from original plan, expanded to "all subclasses" rather than "at least one") — captured in design.md

## 2. Phase 1 — JUnit 4 files → Jupiter (17 files)

- [x] 2.1 `model/TimeZoneTest2.java`
- [x] 2.2 `model/CalendarTest.java`
- [x] 2.3 `model/property/ExDateTest.java`
- [x] 2.4 `model/property/RDateTest.java`
- [x] 2.5 `data/CalendarBuilderTimezoneTest.java`
- [x] 2.6 `data/HCalendarParserTest.java`
- [x] 2.7 `data/CalendarBuilderTest.java`
- [x] 2.8 `data/CalendarParserImplTest.java`
- [x] 2.9 `data/CalendarBuilderConcurrencyTest.java`
- [x] 2.10 `data/CalendarParserFactoryTest.java`
- [x] 2.11 `data/UTCCalendarEqualsTest.java`
- [x] 2.12 `data/CalendarBuilderCustomRegistryTest.java`
- [x] 2.13 `data/CalendarOutputterTest.java`
- [x] 2.14 `data/ConcurrencyTest.java`
- [x] 2.15 `data/CalendarEqualsTest.java`
- [x] 2.16 `transform/compliance/Rfc5545TransformerTest.java`
- [x] 2.17 `transform/compliance/AttendeePropertyRuleTest.java`
- [x] 2.18 Run `./gradlew test` — all green
- [x] 2.19 Commit Phase 1

## 3. Phase 2 — Simple JUnit 3 files (no setUp, no suite(), no base-class consumers)

Targets: files that `extends TestCase` directly, do not define `setUp`/`tearDown`, do not define `suite()`, and are not themselves used as a base class by other tests. The first three (`PropertyTest`, `ComponentTest`, `ParameterTest`, `FilterTest`, `CalendarComponentTest`) are EXCLUDED from this phase — they're handled in Phase 4 with their subclasses.

- [x] 3.1 Enumerate this set with: `comm -23 <(grep -rl "extends TestCase" src/test --include="*.java" | sort) <(grep -rlE "suite\(\)|protected void (setUp|tearDown)" src/test --include="*.java" | sort)`
- [x] 3.2 Subtract base classes (`PropertyTest`, `ComponentTest`, `ParameterTest`, `FilterTest`, `CalendarComponentTest`, `AbstractPropertyTest`)
- [x] 3.3 For each file: remove `extends TestCase`, drop `junit.framework.*` imports, add `@Test`, change `public` test methods to package-private, flip any message-first assertions
- [x] 3.4 Run `./gradlew test` — all green
- [ ] 3.5 Commit Phase 2

## 4. Phase 3 — setUp/tearDown JUnit 3 files (no base-class consumers, no suite())

- [ ] 4.1 Enumerate: `grep -rl "extends TestCase" src/test --include="*.java" | xargs grep -lE "protected void (setUp|tearDown)"`
- [ ] 4.2 Subtract files with `suite()` (they belong to Phase 4) and base classes
- [ ] 4.3 For each remaining file: PATTERN 2 transform — `@BeforeEach`/`@AfterEach`, plus PATTERN 1 changes
- [ ] 4.4 Run `./gradlew test` — all green
- [ ] 4.5 Commit Phase 3

## 5. Phase 4 — Parameterised suite() files (~105 files) + base classes (bundled)

For each file with `suite()`:
- Map every `suite.addTest(new X(...))` row to one `Arguments.of(...)` row
- Group rows by the `super("testX")` of the constructor that built them
- Emit one `@ParameterizedTest` + `@MethodSource` per target method
- Verify the post-migration row count equals the pre-migration `suite.addTest(...)` count
- Delete `suite()`, custom constructors, and `getName()` overrides

Each sub-phase below is ONE atomic commit — the base class and ALL its subclasses move together so test discovery is preserved at every commit boundary.

- [ ] 5.1 **Sub-phase 4a (atomic commit)**: `AbstractPropertyTest` + `model/PropertyTest` + all ~54 `model/property/*` subclasses
   - [ ] 5.1.1 Migrate `AbstractPropertyTest` off `TestCase` (drop `String name` constructor; keep `assertValidationError` using Jupiter `assertTrue`)
   - [ ] 5.1.2 Migrate `model/PropertyTest` — convert from per-row constructors + `suite()` to either: (i) Jupiter-friendly base exposing protected static helpers, or (ii) a real test class with its own @ParameterizedTest methods that subclasses inherit. Choose (i) — see design.md.
   - [ ] 5.1.3 Migrate each `model/property/*` subclass to one or more `@ParameterizedTest` methods feeding from `static Stream<Arguments> data()` sources
   - [ ] 5.1.4 Run `./gradlew test` — all green
   - [ ] 5.1.5 Commit Sub-phase 4a
- [ ] 5.2 **Sub-phase 4b (atomic commit)**: `model/ParameterTest` + all ~20 `model/parameter/*` subclasses (some already migrated — preserve those)
   - [ ] 5.2.1 Migrate `model/ParameterTest` off `TestCase`
   - [ ] 5.2.2 Migrate each `model/parameter/*` subclass that still extends `ParameterTest` or `TestCase`
   - [ ] 5.2.3 Run `./gradlew test` — all green
   - [ ] 5.2.4 Commit Sub-phase 4b
- [ ] 5.3 **Sub-phase 4c (atomic commit)**: `model/ComponentTest` + `model/CalendarComponentTest` + all ~12 `model/component/*` subclasses
   - [ ] 5.3.1 Migrate `ComponentTest` off `TestCase`
   - [ ] 5.3.2 Migrate `CalendarComponentTest` (extends `ComponentTest`) off `TestCase`
   - [ ] 5.3.3 Migrate each `model/component/*` subclass
   - [ ] 5.3.4 Run `./gradlew test` — all green
   - [ ] 5.3.5 Commit Sub-phase 4c
- [ ] 5.4 **Sub-phase 4d (atomic commit)**: `filter/FilterTest` + filter subclasses (`filter/predicate/*`)
   - [ ] 5.4.1 Migrate `FilterTest` off `TestCase`
   - [ ] 5.4.2 Migrate `filter/predicate/*` subclasses
   - [ ] 5.4.3 Run `./gradlew test` — all green
   - [ ] 5.4.4 Commit Sub-phase 4d
- [ ] 5.5 **Sub-phase 4e (commit)**: standalone `model/*` parameterised tests — no base-class entanglement
   - Files: `DateTest`, `DateTimeTest`, `DateListTest`, `DurTest`, `NumberListTest`, `PeriodTest`, `PeriodListTest`, `RecurTest`, `TextListTest`, `TimeZoneTest`, `AddressListTest`, `CalendarDateFormatFactoryTest`
   - [ ] 5.5.1 Migrate each file
   - [ ] 5.5.2 Run `./gradlew test` — all green
   - [ ] 5.5.3 Commit Sub-phase 4e
- [ ] 5.6 **Sub-phase 4f (commit)**: `util/*` parameterised — `UrisTest`, `CalendarsTest`
   - [ ] 5.6.1 Migrate each file
   - [ ] 5.6.2 Run `./gradlew test` — all green
   - [ ] 5.6.3 Commit Sub-phase 4f
- [ ] 5.7 Final sweep: `grep -rlE "extends TestCase|^import junit\.framework" src/test/java --include="*.java"` MUST return zero results

## 6. Phase 5 — Cleanup

- [ ] 6.1 Remove `testRuntimeOnly libs.junit.vintage` from `build.gradle`
- [ ] 6.2 Remove `testCompileOnly libs.junit.junit4` from `build.gradle`
- [ ] 6.3 Run `./gradlew clean test` — verify Spock + testcontainers tests still pass without vintage engine
- [ ] 6.4 If 6.3 fails, restore the missing dependency with an inline comment in `build.gradle` citing the failure
- [ ] 6.5 Verify `./gradlew jacocoTestReport` — coverage at or above pre-migration baseline (target ≥ 0.7 per existing rule)
- [ ] 6.6 Update or delete `docs/JUNIT5_MIGRATION_GUIDE.md` (default: delete — this OpenSpec change is the durable record)
- [ ] 6.7 Final commit

## 7. Validation gates (apply at every phase boundary)

- [ ] 7.1 `./gradlew clean test` passes
- [ ] 7.2 No new compiler warnings introduced
- [ ] 7.3 JaCoCo coverage report shows no regression > 1% from previous phase
- [ ] 7.4 Test invocation count is monotonic: each phase's total ≥ previous phase's total (no silent test loss)
- [ ] 7.5 **Spock/Groovy untouched check**: `git diff --name-only <phase-base>..HEAD -- src/test/groovy/` returns zero files; any non-empty diff is a scope violation and must be reverted
- [ ] 7.6 **Spock test count unchanged**: count of Spock specifications reported by Gradle is the same as the pre-migration baseline (run once at start, compare after each phase)
