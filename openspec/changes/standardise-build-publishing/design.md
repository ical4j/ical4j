## Context

Nine Gradle repos, surveyed 2026-07-21:

| Repo | Publishing mechanism | `SONATYPE_HOST` | Effective release target |
|---|---|---|---|
| `ical4j` | vanniktech 0.34.0 | `CENTRAL_PORTAL` | Central Portal ✅ |
| `ical4j-integration` | vanniktech 0.34.0 | `CENTRAL_PORTAL` | Central Portal ✅ |
| `ical4j-command` | vanniktech applied **+** legacy `publishing {}` at `:81`, URL at `:110` | *unset* | Ambiguous ⚠️ |
| `ical4j-vcard` | `maven-publish` | – | OSSRH bridge |
| `ical4j-serializer` | `maven-publish` | – | OSSRH bridge |
| `ical4j-template` | `maven-publish` | – | OSSRH bridge |
| `ical4j-zoneinfo-outlook` | `maven-publish` | – | OSSRH bridge |
| `ical4j-connector` | `maven-publish` (per-subproject) | – | OSSRH bridge |
| `ical4j-extensions` | `maven-publish` | – | `https://central.sonatype.com` ❌ |

"OSSRH bridge" is `https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/`, Sonatype's compatibility endpoint for publishers who have not yet moved to the Portal API. It functions today.

BouncyCastle force present in: `ical4j` only. axion-release applied in: all nine, at 1.13.6 / 1.15.1 / 1.20.1 / 1.21.1.

## Goals / Non-Goals

**Goals**
- One publishing mechanism across all nine repos.
- Build-classpath CVE posture managed uniformly rather than in one repo.
- Configuration made *identical* so it can later be extracted mechanically.

**Non-Goals**
- Extracting the configuration (that is `extract-build-conventions`).
- Converging plugin/wrapper versions.
- Changing what is published, or its coordinates.

## Decisions

### D1. Converge on `com.vanniktech.maven.publish`, not on hand-rolled `maven-publish`

Three of nine repos already use it, including the two most-consumed (`ical4j`, `ical4j-integration`). It handles Central Portal upload, signing, javadoc/sources jars and POM completeness validation, replacing ~50 lines of `pom.withXml` node-appending per repo with a declarative block. Converging on the legacy mechanism would mean migrating the two working repos backwards onto an endpoint Sonatype intends to retire.

**Rejected:** keeping both mechanisms and only fixing `ical4j-extensions`. It fixes the breakage but leaves the divergence, and `extract-build-conventions` would then have to encode two publishing paths in one convention plugin.

### D2. The BouncyCastle force must stay duplicated — it cannot be solved by a convention plugin

This is a load-bearing constraint on the follow-up change and is recorded here so it is not rediscovered later.

The force lives in the root `buildscript { configurations.classpath { … } }` block. That block configures *the classpath that loads plugins for this build script*. A convention plugin is itself resolved from that classpath, so it cannot retroactively alter it — by the time convention-plugin code runs, axion-release and its BouncyCastle transitive have already been resolved.

Consequences:
- The ~10-line force block stays copied into every repo's root `build.gradle`, even after `extract-build-conventions` lands.
- The only real elimination is upstream: an axion-release version whose own dependencies are patched. **Task 1.1 re-checks this.** If a patched release exists, upgrading everywhere is strictly better than forcing, and the force blocks are never written.

### D3. Sequence the `ical4j-extensions` URL fix first, alone

`ical4j-extensions` cannot publish a release at all. That is a one-line fix with a different urgency and a different risk profile from a nine-repo mechanism migration. It ships as its own commit and is not gated on anything else in this change.

### D4. Prove each migration by POM diff before deleting legacy configuration

The migration's real hazard is silent metadata loss: the hand-built POMs append `licenses`, `scm` and `developers` as raw XML, and they are not consistent with each other — `ical4j-vcard/build.gradle:145` points its licence URL at `ical4j/ical4j/master/LICENSE` (no extension) while `ical4j-extensions/build.gradle:133` points at its own repo's `LICENSE.txt`. A DSL migration that quietly drops or rewrites one of these produces a POM that still publishes and still resolves, so no test fails.

Procedure, per repo:
1. On the pre-migration commit, `./gradlew publishToMavenLocal`; keep the generated `.pom`.
2. Apply the migration.
3. Re-run; diff the two POMs.
4. Every difference must be either intended (documented in the task) or eliminated.
5. Only then delete the legacy `publishing {}` / `signing` blocks.

This is why the change deliberately does not also converge licence URLs: doing so would make every POM diff non-empty and destroy the signal. Licence-URL convergence belongs to `extract-build-conventions`, where a single convention plugin sets one value for everyone and the diff is expected.

### D5. `ical4j-connector` is per-subproject and is migrated last

Connector applies `maven-publish` inside a `subprojects {}` block across four modules (`api`, `dav`, `google`, `msgraph`), so it is four POM diffs, not one. It is also the only affected repo currently on a feature branch (`feat/complete-msgraph-connector`), so migrating it early risks a painful merge. It goes last.

## Risks / Trade-offs

| Risk | Mitigation |
|---|---|
| POM metadata silently lost | D4 diff gate; legacy config deleted only after a clean diff |
| CI secrets not renamed to `ORG_GRADLE_PROJECT_*` | Snapshot publish from CI is a required task per repo, before the repo is considered done |
| Signing misconfigured | Fails the publish loudly; snapshot verification catches it pre-release |
| `ical4j-command`'s ambiguous state hides an in-use path | Inspect its last successful release run before deleting the legacy block |
| Nine PRs is a lot of review surface | Per-repo PRs are independent and individually revertible; no repo depends on another's migration |
| Feature branches diverge during the work | Only `ical4j-connector` and `ical4j-vcard` have live branches; both are sequenced late |

## Open Questions

- Does the current axion-release release ship patched BouncyCastle (≥1.84)? Determines whether D2's force blocks are written at all. **Blocks task 1.1 only.**
- Is `ical4j-command`'s release path actually exercised, or has it not been released since the vanniktech plugin was added? Changes whether its migration is a fix or a cleanup.
- `ical4j-template` and `ical4j-serializer` publish under `org.ical4j` rather than `org.mnode.ical4j`. Out of scope here, but the convention plugin in the follow-up change will need to parameterise group rather than hardcode it.
