## ADDED Requirements

### Requirement: Shared build configuration SHALL be defined once and consumed as plugins

Build configuration common to more than one ecosystem repository SHALL be defined in Gradle convention plugins published from a single source, and consumed by reference. It SHALL NOT be maintained as per-repository copies.

#### Scenario: Repos apply conventions rather than restating them

- **WHEN** a migrated repo's `build.gradle` is inspected
- **THEN** it MUST apply the relevant `ical4j.*-conventions` plugins
- **AND** it MUST NOT restate configuration those plugins own — javadoc options, JaCoCo setup, JUnit/Spock wiring, bnd manifest attributes, the `scmVersion` block, or the POM block
- **AND** its remaining configuration MUST be limited to description, dependencies, and genuinely repo-specific tasks or source sets

#### Scenario: Conventions compose additively

- **WHEN** a repo applies convention plugins and also defines its own tasks, source sets or feature variants
- **THEN** the repo-specific configuration MUST continue to work
- **AND** applying a convention plugin MUST NOT require the repo to abandon configuration unique to it

#### Scenario: Single-repo configuration stays in that repo

- **WHEN** configuration is required by exactly one repository
- **THEN** it MUST remain in that repository's own build script
- **AND** it MUST NOT be added to a convention plugin behind an enable/disable flag

### Requirement: Convention plugins SHALL derive per-repo values from the project name

Values that follow a mechanical naming convention SHALL be derived rather than configured. Values that genuinely vary SHALL be parameterised.

#### Scenario: Tag prefix and URLs are derived

- **WHEN** a repo applies `ical4j.release-conventions` and `ical4j.publishing-conventions`
- **THEN** the axion tag prefix MUST resolve to `"${project.name}-"`
- **AND** the project and SCM URLs MUST resolve to `"https://github.com/ical4j/${project.name}"`
- **AND** the repo MUST NOT need to state any of these explicitly

#### Scenario: Group is parameterised, not hardcoded

- **WHEN** a repo publishing under `org.ical4j` applies `ical4j.publishing-conventions`
- **THEN** its published `groupId` MUST be `org.ical4j`
- **AND** a repo publishing under `org.mnode.ical4j` MUST publish under `org.mnode.ical4j`
- **AND** the plugin MUST NOT hardcode either group

#### Scenario: Java toolchain is parameterised

- **WHEN** a repo requiring Java 17 applies `ical4j.java-library-conventions`
- **THEN** it MUST compile against Java 17
- **AND** a repo requiring Java 11 MUST compile against Java 11

### Requirement: Adoption SHALL NOT change published output

Applying convention plugins to an existing repository SHALL be output-neutral. Any change to the published POM or jar manifest MUST be intentional and recorded.

#### Scenario: POM is diffed before legacy configuration is deleted

- **WHEN** a repo adopts the convention plugins
- **THEN** a POM generated before adoption MUST be diffed against one generated after
- **AND** every difference MUST be either eliminated or documented in that repo's task
- **AND** the superseded local configuration MUST NOT be deleted until the diff is reconciled

#### Scenario: Manifest is diffed when the bnd version changes

- **WHEN** a repo's bnd version changes during convergence
- **THEN** the generated jar manifest MUST be diffed before and after
- **AND** any OSGi metadata change MUST be reviewed

#### Scenario: Feature-variant metadata survives

- **WHEN** `ical4j` core adopts the conventions
- **THEN** the published capability metadata for `caffeineTimezoneCache`, `filterExpressions`, `groovyDsl` and `schemaValidation` MUST be unchanged
- **AND** consumers requiring those capabilities MUST continue to resolve them

### Requirement: Shared tooling versions SHALL be declared once

Versions of build tooling and the common test stack SHALL come from a single shared source. Repositories SHALL NOT independently pin them.

#### Scenario: One version of each build tool across the ecosystem

- **WHEN** all migrated repos are surveyed
- **THEN** there MUST be exactly one Gradle wrapper version in use
- **AND** exactly one bnd version, one axion-release version, and one revapi version

#### Scenario: Common libraries come from the shared catalog

- **WHEN** a migrated repo's `settings.gradle` is inspected
- **THEN** it MUST consume the shared published version catalog
- **AND** it MUST NOT declare local versions for Groovy, Spock, JUnit, Log4j, Hamcrest or Testcontainers
- **AND** it MAY declare libraries used only by that repo

#### Scenario: Convention versions are pinned, never dynamic

- **WHEN** a repo declares its dependency on the convention plugins or shared catalog
- **THEN** the version MUST be an explicit fixed version
- **AND** it MUST NOT use a dynamic version range, `latest.release`, or a snapshot for a released build

### Requirement: CI workflows SHALL be shared rather than copied

Repositories SHALL consume common CI workflows via `workflow_call` from a single hosting repository rather than maintaining copies.

#### Scenario: Satellites call shared workflows

- **WHEN** a migrated repo's `.github/workflows/` is inspected
- **THEN** its build, release and snapshot-publish workflows MUST invoke shared reusable workflows
- **AND** they MUST NOT duplicate the job bodies of those workflows

#### Scenario: Third-party actions are SHA-pinned

- **WHEN** any shared workflow references a third-party action
- **THEN** it MUST be pinned to a full commit SHA with the version in a trailing comment
- **AND** the deprecated `gradle/gradle-build-action@v2` MUST NOT be referenced by any repo

#### Scenario: The JDK matrix is deliberate

- **WHEN** the CI JDK versions across all repos are surveyed
- **THEN** each version in use MUST correspond to a stated reason
- **AND** a repo's CI JDK MUST be consistent with the Java toolchain its build declares
