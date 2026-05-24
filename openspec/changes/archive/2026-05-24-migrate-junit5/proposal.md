## Why

The Java test suite still runs on JUnit 3 (`junit.framework.TestCase` + `suite()`) for 128 files and on JUnit 4 (`@RunWith`, `org.junit.Test`) for 17 more. JUnit 5's Jupiter engine is the long-term direction (it's already on the classpath, and 14 files plus all Spock tests use it). The legacy styles only run today because `junit-vintage-engine` is on the test classpath. Migrating finishes a transition that's already partially underway, lets us drop the vintage engine, removes obsolete `TestCase` boilerplate, and unblocks modern Jupiter features (parameterized providers, nested tests, lifecycle, dynamic tests) for future work.

## What Changes

- Migrate all 128 `junit.framework.TestCase`-extending Java test classes to JUnit Jupiter (`@Test`, `@BeforeEach`, `@AfterEach`, `@ParameterizedTest`).
- Migrate the 17 JUnit 4-style classes (`org.junit.Test`, `@RunWith`) to Jupiter.
- Migrate the lone custom base class `AbstractPropertyTest` off `TestCase` before its subclasses.
- For `suite()`-based parameterized tests, split each `super("testX")` constructor branch into its own `@ParameterizedTest` + `@MethodSource` (one per target method), preserving the original test data and intent.
- Flip assertion argument order from JUnit 3/4 (message-first) to Jupiter (message-last) wherever message-first signatures are used.
- Delete the `junit.framework.*` and `org.junit.*` imports across the test tree.
- **BREAKING** (test-time only, no runtime API change): Remove `testRuntimeOnly libs.junit.vintage` and `testCompileOnly libs.junit.junit4` from `build.gradle` after the last JUnit 3/4 file is migrated.
- **Spock/Groovy tests are strictly out of scope.** All 130 `*.groovy` files under `src/test/groovy/` MUST remain untouched. The migration is confined to `src/test/java/`. Spock already runs on the Jupiter platform and requires no migration; any change to Spock sources by this work would be a scope violation.

## Capabilities

### New Capabilities
- `test-framework`: Defines the requirements for the project's Java test framework — what engine tests use, what styles are permitted, and how parameterized tests are expressed.

### Modified Capabilities
<!-- None — no existing specs to modify. -->

## Impact

- **Affected code**: ~145 Java test files under `src/test/java/`, plus `build.gradle` (drop vintage/junit4 dependencies at the end).
- **No production code changes**: `src/main/` is untouched.
- **No public API impact**: change is confined to test sources and test-classpath dependencies.
- **CI**: `./gradlew test` continues to be the test command; coverage threshold (jacoco 0.7) must hold.
- **Dependencies removed at end of migration**: `junit-vintage-engine`, `junit:junit` (JUnit 4).
- **Risk**: silent test loss if a parameterized `suite()` row is dropped during translation — mitigated by per-pattern phasing, file-by-file review, and running the full suite at each phase boundary.
