## Context

Nine Gradle repos. Surveyed 2026-07-21 against working trees; **re-derived 2026-07-22 against committed `HEAD`** after the first survey was found to have captured uncommitted work in progress as if it were committed state. The table below is committed state.

| Repo | Publishing mechanism | `SONATYPE_HOST` | Effective release target |
|---|---|---|---|
| `ical4j` | vanniktech 0.34.0 | `CENTRAL_PORTAL` | Central Portal ✅ |
| `ical4j-integration` | vanniktech 0.34.0 | `CENTRAL_PORTAL` | Central Portal ✅ |
| `ical4j-command` | `maven-publish` (axion 1.13.6) | – | OSSRH bridge — *vanniktech migration uncommitted in working tree* |
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

Connector applies `maven-publish` inside a `subprojects {}` block across four modules (`api`, `dav`, `google`, `msgraph`), so it is four POM diffs, not one. It is on a feature branch (`feat/complete-msgraph-connector`) with `build.gradle`, `gradle.properties`, `settings.gradle` and `libs.versions.toml` all uncommitted, plus an unreferenced new `ical4j-connector-jpa` module. Migrating it early guarantees a conflict. It goes last, after that work lands.

### D6. Repos with in-flight build-file edits are deferred, not forced

`ical4j-command` and `ical4j-connector` both have uncommitted `build.gradle` changes. `ical4j-command`'s are a vanniktech migration already in progress — the same destination this change specifies, arrived at independently.

Editing those files now would either conflict with that work or silently absorb it into this change, making the POM-diff gate (D4) meaningless: the "before" POM would already contain half the migration.

Therefore: this change does not touch either repo's build files until their in-flight work is committed. For `ical4j-command`, the migration task becomes *review and complete the existing work* rather than *perform the migration*. This is why task 1.4 is a reconciliation step, not an archaeology step.

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

- ~~Does the current axion-release release ship patched BouncyCastle (≥1.84)?~~ **Resolved 2026-07-22 (task 1.1): No.** The latest release, axion-release 1.21.2, still resolves `bcprov-jdk18on:1.82`, `bcpg/bcpkix/bcutil-jdk18on:1.81` on the buildscript classpath — all below the 1.84 that fixes the four advisories. Verified in an isolated scratch build applying only the plugin marker. Consequence: the force blocks in Phase 1 (tasks 2.1–2.8) are required; the "upgrade instead of force" alternative (task 2.9) is **not viable** and is struck. `ical4j` itself is on 1.21.1 and its `-> 1.84` overrides confirm the same native versions.
- **How do `ical4j-extensions` and `ical4j-serializer` get a patched BouncyCastle?** Discovered during implementation (task 2.11): both run axion-release **1.13.6**, which depends on JGit 5.12 and pulls the legacy `-jdk15on` BouncyCastle family at 1.65 — a different artifact coordinate from the `-jdk18on` family the force targets. The force is inert there. Three options, none free:
  - **(a) Upgrade axion to ≥1.20.1 in those two repos**, moving them onto `-jdk18on` so the force applies. Correct, but imports version convergence into a change whose Non-goals explicitly defer it to `extract-build-conventions`.
  - **(b) Additionally force `*-jdk15on`.** The jdk15on line is end-of-life at 1.70 and 1.70 does not clear the advisories this change targets, so this buys little.
  - **(c) Document and defer** to `extract-build-conventions`, accepting that two repos keep BC 1.65 in the meantime.
- ~~Is `ical4j-command`'s release path actually exercised?~~ **Resolved 2026-07-22: the premise was a survey error.** At `HEAD` it is a plain `maven-publish` repo on axion 1.13.6; the vanniktech configuration is uncommitted WIP. Note this means `ical4j-command` is a third repo on axion 1.13.6 and so will hit the same `-jdk15on` problem when it is un-deferred.
- `ical4j-template` and `ical4j-serializer` publish under `org.ical4j` rather than `org.mnode.ical4j`. Out of scope here, but the convention plugin in the follow-up change will need to parameterise group rather than hardcode it.

## Resulting uniform configuration (task 4.5 — input to `extract-build-conventions`)

As of 2026-08-19, seven of nine repos (`ical4j`, `ical4j-integration`, `ical4j-zoneinfo-outlook`, `ical4j-vcard`, `ical4j-extensions`, `ical4j-serializer`, `ical4j-template`) carry this identical configuration; `ical4j-command` and `ical4j-connector` are still deferred on their in-flight work.

**`build.gradle`** — every repo has, verbatim apart from the repo-specific values called out below:

1. The BouncyCastle force block at the top of `buildscript {}` (stays per-repo forever — D2).
2. `id 'com.vanniktech.maven.publish' version '0.34.0'` in `plugins {}`; no `maven-publish`, no `signing` plugin, no `withJavadocJar()/withSourcesJar()` (vanniktech supplies both jars), no `ext.isReleaseVersion`.
3. `pl.allegro.tech.build.axion-release` at 1.20.1+ (1.21.1 in `ical4j`).
4. One `mavenPublishing { coordinates(...); pom { ... } }` block: `name`/`description` from project, `url = 'http://ical4j.github.io'`, licence name `iCal4j - License` + `distribution = 'repo'`, developer `fortuna`/`Ben Fortuna`, scm pointing at the repo's own GitHub coordinates.

**Repo-specific values the convention plugin must parameterise:** group (`org.mnode.ical4j` vs `org.ical4j`), licence URL (`ical4j/master/LICENSE` in ical4j/vcard/serializer/template; own-repo `LICENSE.txt` in extensions/zoneinfo-outlook — deliberately not converged, D4), scm URLs, description, javadoc links, jar manifest title.

**`gradle.properties`** — identical four-line block everywhere: comment naming the `ORG_GRADLE_PROJECT_*` env vars, then `SONATYPE_HOST=CENTRAL_PORTAL`, `SONATYPE_AUTOMATIC_RELEASE=true`, `RELEASE_SIGNING_ENABLED=true`.

**`publish-snapshots.yml`** — identical publish job: checkout with `fetch-depth: 0` (axion needs full history + tags), `./gradlew publishToMavenCentral --no-configuration-cache`, env mapping `ORG_GRADLE_PROJECT_mavenCentralUsername/Password` ← `CENTRAL_PORTAL_USERNAME/PASSWORD` and `ORG_GRADLE_PROJECT_signingInMemoryKey(Password)` ← `GPG_SIGNING_KEY`/`GPG_SIGNING_PASSWORD`.

**Wrapper baseline:** Gradle ≥ 8.5 (vanniktech 0.34.0 hard-requires it; vcard/extensions/serializer were bumped 8.4 → 8.5 for exactly this).

**Operational gap (closed 2026-08-19):** the four CI secrets are now org-level with visibility to all repos; `ical4j-integration` additionally keeps its original repo-level copies, which shadow them.
