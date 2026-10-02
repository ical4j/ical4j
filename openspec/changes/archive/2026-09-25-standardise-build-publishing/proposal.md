## Why

The nine Gradle repos in the iCal4j ecosystem (`ical4j`, `ical4j-vcard`, `ical4j-extensions`, `ical4j-serializer`, `ical4j-template`, `ical4j-zoneinfo-outlook`, `ical4j-connector`, `ical4j-integration`, `ical4j-command`) each carry their own hand-maintained copy of the same build and publishing configuration. Because the copies are independent, fixes applied in one repo do not reach the others. Two concrete consequences exist today:

**1. A security fix reached exactly one of nine repos.** `ical4j/build.gradle:1-15` forces `org.bouncycastle:*-jdk18on:1.84` onto the buildscript classpath to displace the 1.81/1.82 pulled in by the axion-release plugin, which is affected by GHSA-p93r-85wp-75v3, GHSA-cj8j-37rh-8475, GHSA-c3fc-8qff-9hwx and GHSA-wg6q-6289-32hp. Every other repo also applies axion-release — versions 1.13.6 through 1.21.1 — and none of them carry the force. This is build-time exposure only (the vulnerable classes never enter a published artifact), but it is unmanaged exposure in eight repos.

**2. Publishing has diverged into two incompatible states.** Only `ical4j` and `ical4j-integration` are migrated to the Sonatype Central Portal via `com.vanniktech.maven.publish` with `SONATYPE_HOST=CENTRAL_PORTAL` in `gradle.properties`. The remaining seven use raw `maven-publish` with POM metadata appended node-by-node as XML. Of those, `ical4j-extensions/build.gradle:146` names `https://central.sonatype.com` as its release repository URL; that is the web portal, not a Maven repository endpoint, so that repo's release publish path is broken.

> **Survey correction (2026-07-22).** An earlier revision of this proposal described `ical4j-command` as "half-migrated, with an ambiguous release target". That was wrong: the survey read working trees, and `ical4j-command`'s vanniktech configuration is **uncommitted work in progress**, not committed state. At `HEAD` it is a plain `maven-publish` repo on axion 1.13.6. The same error affected `ical4j-connector`, whose revapi configuration is likewise uncommitted. All figures in this proposal now reflect committed `HEAD`. See "Interaction with work in progress" below.

Note a correction to an earlier characterisation of this problem: the other five `maven-publish` repos target `https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/`, which is Sonatype's OSSRH compatibility bridge. That bridge is a migration aid, not a dead endpoint — those repos can still publish today. The argument for migrating them is convergence and the eventual retirement of the bridge, not immediate breakage. Only `ical4j-extensions` is broken right now.

This change fixes the two live problems and converges all nine repos on a single publishing mechanism. It deliberately does **not** attempt to remove the duplication itself — that is [`extract-build-conventions`](../extract-build-conventions/proposal.md), which depends on this change having established one target state to encode.

## What Changes

- Add the BouncyCastle `resolutionStrategy.force` block to the `buildscript` block of the eight repos that lack it, matching `ical4j/build.gradle:1-15` including the explanatory comment.
- Re-verify whether the current axion-release release ships patched BouncyCastle; if it does, upgrade instead of forcing, and record that outcome.
- Fix `ical4j-extensions` release URL — the immediate breakage — as an isolated first commit so it can ship ahead of the rest.
- Migrate the seven `maven-publish` repos to `com.vanniktech.maven.publish` 0.34.0, replacing hand-built POM XML nodes with the plugin's `mavenPublishing { pom { … } }` DSL.
- For `ical4j-command`, adopt and finish the migration already in progress in its working tree rather than starting over.
- Standardise `SONATYPE_HOST=CENTRAL_PORTAL`, `SONATYPE_AUTOMATIC_RELEASE=true` and `RELEASE_SIGNING_ENABLED=true` in every repo's `gradle.properties`.
- Verify each migrated repo can produce a signed snapshot to the Central Portal before its legacy configuration is deleted.

## Non-goals

- **No deduplication.** Each repo keeps its own copy of the configuration. The copies become *identical*, which is the precondition for extracting them later; making them identical and extracting them are separate changes so that a publishing regression is not entangled with a build-logic refactor.
- **No version convergence** of axion-release, revapi, bnd or the Gradle wrapper. Deferred to `extract-build-conventions`.
- **No coordinate changes.** Group IDs stay as they are, including the `org.mnode.ical4j` / `org.ical4j` split.
- **No release.** This change lands configuration and proves it with snapshots. Cutting releases is normal downstream work.

## Cross-repository scope

OpenSpec is per-repository, but eight of the nine repos affected here are outside this one. This change is hosted in `ical4j` because it is the ecosystem root and already acts as the coordination point — `ical4j-vcard/.github/workflows/publish-snapshots.yml:15` consumes `ical4j/ical4j/.github/workflows/test.yml@develop`, so cross-repo ownership from here is established practice.

Work in sibling repos SHALL be tracked as tasks here and delivered as one PR per repo. Sibling repos are not required to carry their own copy of this proposal.

## Interaction with work in progress

Eight of the nine repos have uncommitted changes as of 2026-07-22, and two of them are mid-edit in exactly the files this change targets:

| Repo | Uncommitted work | Collides with this change? |
|---|---|---|
| `ical4j-command` | `build.gradle` — a vanniktech migration already underway | **Yes** — same file, same goal |
| `ical4j-connector` | `build.gradle`, `gradle.properties`, `settings.gradle`, `libs.versions.toml`, plus a new `ical4j-connector-jpa` module | **Yes** — build files mid-refactor |
| `ical4j-extensions` | 19 source files (new strategy classes) | No — `build.gradle` is clean |
| `ical4j-serializer` | new `activitystream`/`rdf` packages | No |
| `ical4j-template` | new `.jte` templates | No |
| `ical4j-vcard`, `ical4j-integration` | `.gitignore` only | No |
| `ical4j-zoneinfo-outlook` | none — clean | No |

Consequences for execution:

- `ical4j-command` and `ical4j-connector` MUST NOT have their build files edited by this change while that work is outstanding. They are deferred until their in-flight work lands, and `ical4j-command`'s existing migration is adopted rather than duplicated.
- In the remaining repos, edits are confined to `build.gradle`, which is clean in all of them, so this change's work stays separable from unrelated WIP.
- Because most repos carry unrelated uncommitted work, edits made by this change SHOULD be left uncommitted for the maintainer to fold into their own commits, rather than committed onto a branch that would strand that WIP.

## Capabilities

### New Capabilities
- `build-publishing`: Requirements governing how ecosystem artifacts are published — the publishing mechanism, release target, signing, and the security posture of the build classpath.

### Modified Capabilities
<!-- None. -->

## Impact

- **Affected files**: `build.gradle` and `gradle.properties` in nine repos. No `src/` changes anywhere.
- **No public API impact**: no source, no coordinates, no dependency changes for consumers.
- **Published artifact content**: unchanged. POM output is re-verified byte-for-byte against the previous mechanism (see design.md) so consumers see no metadata drift.
- **Credentials**: the vanniktech plugin reads `ORG_GRADLE_PROJECT_*` env vars. Repos moving off `maven-publish` need their CI secrets renamed to match; this is the main operational risk and is called out per-repo in tasks.
- **Risk — silent POM regression**: hand-built POMs may contain fields the DSL migration drops (notably `ical4j-vcard` and `ical4j-extensions` append `licenses`, `scm` and `developers` manually, and point at different LICENSE URLs). Mitigated by a mandatory generated-POM diff before legacy config is deleted.
- **Risk — signing**: six repos currently sign via the `signing` plugin against `publishing.publications.<name>`. Under vanniktech, signing is driven by `RELEASE_SIGNING_ENABLED`. A misconfiguration here fails the release, loudly rather than silently.
