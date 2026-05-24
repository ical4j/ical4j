## ADDED Requirements

### Requirement: Migration scope is limited to Java JUnit tests

This migration SHALL apply only to Java source files under `src/test/java/`. Groovy/Spock tests under `src/test/groovy/` SHALL NOT be modified, renamed, moved, or have their behaviour altered in any way by this change. The migration MUST NOT touch Spock specifications, Groovy test utilities, or anything else outside `src/test/java/`.

#### Scenario: Groovy/Spock source tree is untouched

- **WHEN** the change is complete
- **THEN** `git diff --name-only <pre-migration>..HEAD -- src/test/groovy/` returns no files
- **AND** the count of files under `src/test/groovy/` is unchanged
- **AND** no `*.groovy` file has been edited, added, or deleted by this change

#### Scenario: Spock tests continue to pass unchanged

- **WHEN** `./gradlew test` is run at any point during or after the migration
- **THEN** every Spock specification that passed before the migration continues to pass
- **AND** every Spock specification reports the same test invocation count as before
- **AND** no Spock test is skipped, ignored, or rerouted to a different runner as a side effect of the migration

#### Scenario: Test-classpath changes preserve Spock execution

- **WHEN** `junit-vintage-engine` is removed from `build.gradle` in the cleanup phase
- **THEN** Spock tests continue to run under the Spock engine on the Jupiter platform without modification
- **AND** if removing vintage breaks any Spock test, vintage is restored with an inline comment in `build.gradle` citing the broken consumer

### Requirement: Java tests SHALL use JUnit Jupiter

All Java unit tests under `src/test/java/` SHALL be authored against the JUnit Jupiter API (`org.junit.jupiter.api.*`, `org.junit.jupiter.params.*`). The Gradle test runner SHALL invoke them through `useJUnitPlatform()` (already configured).

#### Scenario: No JUnit 3 TestCase usage

- **WHEN** any Java source file under `src/test/java/` is inspected
- **THEN** it MUST NOT contain the string `extends TestCase`
- **AND** it MUST NOT contain any `import junit.framework.*` statement

#### Scenario: No JUnit 4 annotations or runner

- **WHEN** any Java source file under `src/test/java/` is inspected
- **THEN** it MUST NOT contain `import org.junit.Test` (the JUnit 4 `Test` annotation)
- **AND** it MUST NOT contain `@RunWith` or `import org.junit.runner.*`

#### Scenario: Test execution discovers all tests via Jupiter

- **WHEN** `./gradlew test` is run from a clean state
- **THEN** every test method that previously ran (under JUnit 3/4 + vintage engine) continues to run under the Jupiter engine
- **AND** the total test invocation count is not less than the pre-migration count

### Requirement: Parameterised tests SHALL use @ParameterizedTest with @MethodSource

Tests that previously provided parameters via `public static TestSuite suite()` SHALL be expressed as one or more `@ParameterizedTest` methods, each fed by a `static Stream<Arguments> <name>()` source method via `@MethodSource`.

#### Scenario: One @ParameterizedTest per original target method

- **WHEN** a former `suite()` test had constructors calling `super("testA")` and `super("testB")`
- **THEN** the migrated file has both `testA(...)` and `testB(...)` as `@ParameterizedTest` methods, each with its own `@MethodSource`-supplied data
- **AND** each `suite.addTest(new X(...))` row from the original `suite()` is represented as exactly one `Arguments.of(...)` row in the matching source method

#### Scenario: Parameter data preserved

- **WHEN** the original `suite()` produced N test invocations
- **THEN** the migrated `@MethodSource` methods produce N rows in aggregate (per-source-method counts summing to N)

### Requirement: Lifecycle hooks SHALL use Jupiter annotations

Test lifecycle methods that were `protected void setUp()` / `protected void tearDown()` SHALL be replaced with `@BeforeEach void setUp()` / `@AfterEach void tearDown()`.

#### Scenario: setUp converted

- **WHEN** an original class had `protected void setUp() throws Exception`
- **THEN** the migrated class has `@BeforeEach void setUp() throws Exception` (no `@Override`)

#### Scenario: tearDown converted

- **WHEN** an original class had `protected void tearDown() throws Exception`
- **THEN** the migrated class has `@AfterEach void tearDown() throws Exception` (no `@Override`)

### Requirement: Assertions SHALL use Jupiter Assertions with message-last argument order

Assertion call sites SHALL use `org.junit.jupiter.api.Assertions.*` (imported statically). Where the legacy JUnit 3/4 form put a `String message` as the first argument, the migrated form SHALL place it as the last argument.

#### Scenario: Message-first calls swapped

- **WHEN** a legacy call was `assertEquals("msg", expected, actual)`
- **THEN** the migrated call is `assertEquals(expected, actual, "msg")`

#### Scenario: Hamcrest assertions left intact

- **WHEN** a test uses `assertThat(...)` from Hamcrest
- **THEN** the migrated test SHALL keep the Hamcrest call unchanged (Hamcrest is not affected by the Jupiter migration)

### Requirement: junit-vintage-engine and JUnit 4 SHALL be removed from build dependencies

Once no Java test depends on JUnit 3 or JUnit 4 APIs, `build.gradle` SHALL be updated to remove `testRuntimeOnly libs.junit.vintage` and `testCompileOnly libs.junit.junit4`.

#### Scenario: Vintage engine removed

- **WHEN** all Java tests have been migrated to Jupiter
- **THEN** `build.gradle` does not reference `libs.junit.vintage`
- **AND** `build.gradle` does not reference `libs.junit.junit4`
- **AND** `./gradlew test` continues to pass

#### Scenario: Vintage kept only if a non-Java consumer needs it

- **WHEN** removing the vintage engine breaks Spock or another non-Java test consumer
- **THEN** vintage MAY be retained with an inline comment in `build.gradle` recording the consumer that requires it
- **AND** the comment cites the specific failure mode observed

### Requirement: AbstractPropertyTest SHALL be a Jupiter-compatible base class

The custom abstract base `net.fortuna.ical4j.model.AbstractPropertyTest` SHALL not extend `TestCase`. Its `assertValidationError` helper SHALL continue to work for all subclasses using Jupiter `Assertions.assertTrue`.

#### Scenario: Base class no longer extends TestCase

- **WHEN** `AbstractPropertyTest` is inspected
- **THEN** it does not extend `junit.framework.TestCase`
- **AND** it does not declare a `String name`-taking constructor required by `TestCase`

#### Scenario: Subclasses still compile and run

- **WHEN** any subclass of `AbstractPropertyTest` is executed via `./gradlew test`
- **THEN** the test runs under the Jupiter engine and `assertValidationError` continues to behave as before
