## Why

The nine Gradle repos in the iCal4j ecosystem maintain nine near-identical copies of the same build configuration. Diffing `ical4j-vcard/build.gradle` against `ical4j-extensions/build.gradle` shows the substantive differences are only: description, dependencies, SCM URL, axion tag prefix, and publication name. Everything else — the Java toolchain block, javadoc options, JaCoCo setup, JUnit/Spock test wiring, bnd manifest attributes, the `scmVersion` block, the publishing block — is duplicated prose.

Volume is not the real cost; **drift** is. Because the copies are independent, they have diverged:

| | axion | revapi | bnd | Gradle wrapper |
|---|---|---|---|---|
| `ical4j` | 1.21.1 | 2.0.0 | 7.2.3 | 8.5 |
| `ical4j-vcard` | 1.20.1 | 1.7.0 | 7.1.0 | 8.4 |
| `ical4j-extensions` | 1.13.6 | 1.7.0 | 7.1.0 | 8.4 |
| `ical4j-serializer` | 1.13.6 | – | 7.1.0 | 8.4 |
| `ical4j-template` | 1.20.1 | – | 6.1.0 | 9.2.1 |
| `ical4j-zoneinfo-outlook` | 1.20.1 | – | 7.1.0 | 9.1.0 |
| `ical4j-connector` | 1.20.1 | – | 7.1.0 | 8.4 |
| `ical4j-command` | 1.13.6 | – | 7.1.0 | 9.2.1 |
| `ical4j-integration` | 1.15.1 | – | 7.1.0 | 8.5 |

> **Survey basis (corrected 2026-07-22).** These figures are committed `HEAD` state. An earlier revision reported `ical4j-command` at axion 1.15.1 and `ical4j-connector` with revapi 1.7.0; both came from uncommitted working-tree changes rather than committed configuration. The drift conclusion is unaffected — if anything the committed spread is wider, since `ical4j-command` sits on axion 1.13.6.

Four Gradle wrapper versions, three bnd versions, an eight-minor-version spread on axion-release. The nine version catalogs have drifted the same way (Groovy 3.0.22/3.0.25, Spock 2.4-M4/2.4-M7, Log4j 2.23.1 through 2.26.0), and CI runs JDK 11, 17 and 21 across repos with no stated rationale. The demonstrated consequence is `standardise-build-publishing`: a security fix and a publishing migration each reached a subset of repos and stalled there.

The CI half of the solution already exists and was abandoned mid-rollout. `ical4j-vcard/.github/workflows/publish-snapshots.yml:15` calls `uses: ical4j/ical4j/.github/workflows/test.yml@develop` — a reusable workflow hosted here. Meanwhile `ical4j`'s own copy has moved on to SHA-pinned `gradle/actions/setup-gradle@af1da67…` while the satellites still call the deprecated `gradle/gradle-build-action@v2`. The pattern works; it was never finished.

## What Changes

- Create an `ical4j-build-logic` repository publishing four Gradle convention plugins: `ical4j.java-library-conventions`, `ical4j.publishing-conventions`, `ical4j.release-conventions`, `ical4j.revapi-conventions`.
- Derive per-repo variation from `project.name` rather than parameterising it: axion tag prefix becomes `"${project.name}-"`, SCM and project URLs become `"https://github.com/ical4j/${project.name}"`. This is already the de-facto convention in all nine repos.
- Parameterise the genuinely varying inputs: `group` (the `org.mnode.ical4j` / `org.ical4j` split), Java toolchain version (11 vs 17), description, dependencies, and revapi baseline.
- Publish a shared version catalog as a TOML artifact, consumed via `settings.gradle`'s `versionCatalogs { from(…) }`, replacing nine independent catalogs.
- Converge the drifted versions as part of adoption: one Gradle wrapper version, one bnd, one axion, one revapi, one Groovy/Spock/Log4j/JUnit line.
- Finish the reusable-workflow rollout so all repos consume `build.yml`, `create-release.yml` and `publish-snapshots.yml` from a single source, with SHA-pinned actions.
- Migrate repos one at a time, piloting on `ical4j-zoneinfo-outlook`.

## Non-goals

- **Not a monorepo.** This change works entirely across separate repositories. It neither requires nor precludes a later merge; see design.md D6 for how the two relate.
- **No coordinate changes.** The `org.mnode.ical4j` / `org.ical4j` group split is preserved, not resolved.
- **No source changes.** Nothing under any repo's `src/` is touched.
- **No version-alignment of the published libraries themselves.** Each repo keeps its own release line and its own axion tag prefix.
- **The BouncyCastle buildscript force is explicitly out of scope for extraction** — it cannot be moved into a convention plugin (see `standardise-build-publishing` design.md D2) and stays duplicated by necessity.

## Dependencies

This change **depends on `standardise-build-publishing` completing first.** That change converges nine divergent publishing configurations into one target state; this change encodes that state into a plugin. Attempting the extraction first would mean writing a convention plugin that supports three publishing mechanisms, one of which is broken.

## Cross-repository scope

As with `standardise-build-publishing`, this change is hosted in `ical4j` because it is the ecosystem coordination point, but the bulk of the work lands in eight sibling repos plus one new repository. Sibling work SHALL be tracked as tasks here and delivered as one PR per repo.

## Capabilities

### New Capabilities
- `build-conventions`: Requirements governing how shared build configuration is defined, distributed, versioned and consumed across ecosystem repositories.

### Modified Capabilities
<!-- None. -->

## Impact

- **Affected files**: `build.gradle`, `settings.gradle`, `gradle/libs.versions.toml`, `gradle/wrapper/*` and `.github/workflows/*` across nine repos; one new repository.
- **Expected reduction**: a satellite `build.gradle` goes from ~110–180 lines to roughly 10 — a `plugins {}` block, a description, and a `dependencies {}` block.
- **No public API impact**: no source changes, no coordinate changes, no dependency changes visible to consumers.
- **New coupling — this is the central trade-off.** Nine repos gain a shared dependency. A defective `build-logic` release can break every build at once, where today a mistake is contained to one repo. Mitigated by pinned versions (never dynamic), staged adoption, and the pilot repo absorbing each new release first (design.md D5).
- **Rollout tax**: with separate repos, changing shared config means publishing `build-logic` and opening nine PRs. Automatable via Renovate. This tax is the honest price of not merging repos, and is the clearest concrete argument a future monorepo decision would weigh.
- **Risk — behaviour change during convergence**: moving `ical4j-template` from bnd 6.1.0 to 7.x, or any repo across a Gradle major (8.x → 9.x), can change manifest content or fail the build. Each repo's convergence is a separate reviewable step, not bundled into its convention-plugin adoption.
- **Risk — `ical4j` core is not a typical consumer**: it has feature variants, a Podman/testcontainers environment dance, and a bespoke `timezoneIndependenceTest` task. It adopts the conventions and keeps its extras locally (design.md D4); if that proves awkward, core stays unmigrated and the change still delivers most of its value.
