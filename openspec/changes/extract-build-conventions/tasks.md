## 0. Preconditions

- [ ] 0.1 `standardise-build-publishing` complete — all nine repos on `com.vanniktech.maven.publish` targeting the Central Portal
- [ ] 0.2 Resolve design.md open questions: licence URL, target Gradle wrapper version, vcard 1.x-maintenance config, multi-module application point
- [ ] 0.3 Record the converged publishing configuration produced by `standardise-build-publishing` task 4.5 — it is the source text for `ical4j.publishing-conventions`

## 1. Phase 1 — Create `ical4j-build-logic`

- [ ] 1.1 Create the repository with a minimal hand-written publishing setup (bootstrap; it cannot consume its own conventions)
- [ ] 1.2 `ical4j.java-library-conventions` — toolchain, `java-library` + `groovy`, javadoc options, JaCoCo, JUnit Platform + Spock wiring, bnd manifest attributes
- [ ] 1.3 `ical4j.publishing-conventions` — vanniktech, POM DSL with `url`/`scm` derived from `project.name`, signing toggles, `group` parameterised
- [ ] 1.4 `ical4j.release-conventions` — axion `scmVersion` with `prefix = "${project.name}-"`
- [ ] 1.5 `ical4j.revapi-conventions` — revapi plugin and baseline wiring
- [ ] 1.6 Shared version catalog: Groovy, Spock, JUnit, Log4j, Hamcrest, Testcontainers at single converged versions
- [ ] 1.7 Publish `build-logic` and the catalog as a snapshot; confirm both resolve from an external build
- [ ] 1.8 Confirm plugins compose additively — a consumer can apply conventions and still add its own tasks and source sets

## 2. Phase 2 — Pilot on `ical4j-zoneinfo-outlook`

- [ ] 2.1 Converge Gradle wrapper to the agreed version; `./gradlew build` green
- [ ] 2.2 Converge bnd/axion versions; **diff the generated jar manifest before and after** — bnd version changes alter manifest content
- [ ] 2.3 Consume the shared catalog from `settings.gradle`; delete the local `gradle/libs.versions.toml`
- [ ] 2.4 Apply `java-library-conventions`, `publishing-conventions`, `release-conventions`
- [ ] 2.5 Delete the superseded local configuration
- [ ] 2.6 Diff the generated POM against pre-adoption; reconcile every difference
- [ ] 2.7 Confirm `./gradlew build` green and a snapshot publishes from CI
- [ ] 2.8 Confirm the build file is ~10 lines. If it is not, the plugins are wrong — fix them before proceeding
- [ ] 2.9 Write up the adoption procedure; it is the template for every repo below

## 3. Phase 3 — Roll out to satellites

One PR per repo, each following the Phase 2 procedure. Each repo is independently revertible.

- [ ] 3.1 `ical4j-extensions` (no live branch; uses revapi)
- [ ] 3.2 `ical4j-serializer` (group `org.ical4j`; extra deps Jackson, j2html stay local)
- [ ] 3.3 `ical4j-template` (group `org.ical4j`; Java 17; jte stays local; **largest convergence risk** — bnd 6.1.0 → 7.x and Gradle 9.2.1)
- [ ] 3.4 `ical4j-integration` (multi-module; apply at the point agreed in 0.2)
- [ ] 3.5 `ical4j-command` (multi-module; Java 17)
- [ ] 3.6 `ical4j-vcard` (uses revapi; reconcile the 1.x-maintenance branch config per 0.2; coordinate with the live `fix-gender-fn-validation` branch)
- [ ] 3.7 `ical4j-connector` (multi-module × 4; uses revapi; coordinate with the live `feat/complete-msgraph-connector` branch)

## 4. Phase 4 — Reusable workflows

- [ ] 4.1 Consolidate `build.yml`, `create-release.yml`, `publish-snapshots.yml` in `ical4j/.github/workflows/` as `workflow_call` entry points
- [ ] 4.2 SHA-pin every third-party action, matching the existing `gradle/actions/setup-gradle@af1da67…` practice
- [ ] 4.3 Retire the deprecated `gradle/gradle-build-action@v2` wherever it remains
- [ ] 4.4 Agree and apply a deliberate CI JDK matrix (currently 11, 17 and 21 across repos with no stated rationale)
- [ ] 4.5 Point all eight satellites at the shared workflows
- [ ] 4.6 Confirm every repo's CI is green and still publishes snapshots

## 5. Phase 5 — `ical4j` core

- [ ] 5.1 Apply the conventions to core, keeping locally: the four feature variants, Podman/testcontainers handling, `timezoneIndependenceTest`, and the BouncyCastle force
- [ ] 5.2 Diff the generated POM and jar manifest against pre-adoption; feature-variant capability metadata MUST be unchanged
- [ ] 5.3 Confirm `./gradlew check` green, including `timezoneIndependenceTest` and the JaCoCo 0.7 threshold
- [ ] 5.4 If core fits badly, revert and record why — the change stands without it

## 6. Phase 6 — Verification

- [ ] 6.1 Every migrated repo's `build.gradle` is ≤ ~20 lines of non-dependency configuration
- [ ] 6.2 Exactly one Gradle wrapper version, one bnd, one axion, one revapi across all repos
- [ ] 6.3 No repo retains a local `gradle/libs.versions.toml` except for genuinely repo-specific libraries
- [ ] 6.4 Every repo publishes a snapshot from CI after migration
- [ ] 6.5 Prove the mechanism end-to-end: make one shared-config change in `build-logic`, publish, and confirm it reaches every repo
- [ ] 6.6 Validate this change: `openspec validate extract-build-conventions --strict`
