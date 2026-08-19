## 1. Phase 0 — Unblock and decide

- [x] 1.1 Check whether the current `pl.allegro.tech.build.axion-release` release ships BouncyCastle ≥ 1.84. Record the answer in design.md under "Open Questions". If yes, Phase 2 becomes "upgrade axion-release" instead of "copy the force block". **Result: No — 1.21.2 (latest) still pulls BC 1.82/1.81. Force blocks required; task 2.9 struck.**
- [x] 1.2 Fix `ical4j-extensions/build.gradle:146` — release URL `https://central.sonatype.com` → `https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/`. Single-commit PR, shipped independently of everything below. **Edit applied 2026-07-22, left uncommitted (repo has unrelated WIP).**
- [ ] 1.3 Verify 1.2 by publishing a snapshot from `ical4j-extensions` CI. **BLOCKED — requires CI credentials.**
- [x] 1.4 ~~Inspect `ical4j-command`'s most recent successful release run to determine which publishing path actually executed.~~ **Superseded 2026-07-22.** The premise was a survey error: `ical4j-command` at `HEAD` is a plain `maven-publish` repo on axion 1.13.6, with no ambiguity to investigate. The vanniktech configuration is uncommitted WIP. Replaced by 1.5.
- [ ] 1.5 Review the uncommitted vanniktech migration in `ical4j-command`'s working tree against this change's target state; adopt and complete it rather than re-doing it. Blocked until that work is committed.

## 2. Phase 1 — BouncyCastle posture

Skip this entire phase if 1.1 found a patched axion-release; do task 2.9 instead.

- [x] 2.1 `ical4j-vcard` — add force block to root `buildscript`, comment included. **Verified: `1.80/1.81 -> 1.84`.**
- [x] 2.2 `ical4j-extensions` — force block **plus axion 1.13.6 → 1.20.1** (required; see 2.11). **Verified: now on `-jdk18on`, `1.80/1.81 -> 1.84`; `currentVersion` unchanged at `2.0.1-develop-SNAPSHOT`; `compileJava` green.**
- [x] 2.3 `ical4j-serializer` — force block **plus axion 1.13.6 → 1.20.1**. **Verified: now on `-jdk18on`, `1.80/1.81 -> 1.84`; `currentVersion` unchanged at `0.4.0-develop-SNAPSHOT`; `compileJava` green.**
- [x] 2.4 `ical4j-template` — **Verified: `1.80/1.81 -> 1.84`.**
- [x] 2.5 `ical4j-zoneinfo-outlook` — **Verified: `1.80/1.81 -> 1.84`.**
- [ ] 2.6 `ical4j-connector` — **DEFERRED**: `build.gradle`, `gradle.properties`, `settings.gradle`, `libs.versions.toml` all uncommitted (design.md D5/D6)
- [x] 2.7 `ical4j-integration` — **Verified: `1.72 -> 1.84`** (was on BC 1.72, the oldest jdk18on in the set)
- [ ] 2.8 `ical4j-command` — force block **plus axion 1.15.1 → 1.20.1** applied. **Partially blocked:** the upgrade repaired the plugin-application failure (see 2.12), but the build now fails at `build.gradle:137` on a leftover `signing {}` block whose `signing` plugin was removed by uncommitted WIP. Completing that is Phase 2 task 3.6 and is gated on a POM diff, so it is not done here.
- [x] 2.11 **Defect found during implementation, resolved.** `ical4j-extensions`, `ical4j-serializer` and `ical4j-command` ran axion **1.13.6/1.15.1**, which pull the legacy `-jdk15on` BouncyCastle family (`bcprov-jdk15on:1.65`) rather than `-jdk18on`. The force targets `*-jdk18on:1.84` and matched nothing, leaving those repos on BC 1.65 — older than the versions this change exists to fix — while the spec's `-jdk18on ≥ 1.84` scenario passed vacuously. **Resolution: upgrade axion to 1.20.1 in all three** (option (a) in design.md), and add a non-vacuity scenario to the spec forbidding `-jdk15on` on the classpath and requiring axion ≥ 1.20.1.
- [x] 2.12 **Pre-existing breakage found, unrelated to this change.** `ical4j-command` could not build **at `HEAD`**: committed axion 1.13.6 calls `Provider.forUseAtConfigurationTime()`, removed in Gradle 9, against the repo's committed Gradle 9.2.1 wrapper. Configuration failed before any task ran. The 1.20.1 upgrade in 2.8 repairs this; `ical4j-template` already demonstrates 1.20.1 working on Gradle 9.2.1. Worth an issue in its own right — the repo's CI cannot have been green.
- [x] 2.9 ~~*(alternative to 2.1–2.8)* Upgrade axion-release to the patched version~~ **STRUCK by task 1.1: no patched axion-release exists (1.21.2 latest still pulls BC 1.82/1.81). Force blocks are the only option.**
- [ ] 2.10 Confirm each repo still builds: `./gradlew build` green in all nine. **Seven of nine verified 2026-08-19: `ical4j-zoneinfo-outlook`, `ical4j-vcard`, `ical4j-extensions`, `ical4j-serializer` full `build` green; `ical4j-template` and `ical4j-integration` `build -x test` green (template's `:test` failure is pre-existing at `HEAD`). Outstanding: `ical4j-command` (blocked on WIP, see 2.8) and `ical4j-connector` (deferred).**

## 3. Phase 2 — Publishing migration

Each repo below is one PR. Every repo follows the D4 procedure: capture pre-migration POM → migrate → diff → reconcile → delete legacy config. A repo is not done until its snapshot publishes from CI.

- [ ] 3.1 `ical4j-zoneinfo-outlook` (pilot — smallest build, single dependency, no revapi) **Migrated 2026-08-19; PR ical4j/ical4j-zoneinfo-outlook#35. Only CI snapshot (3.1.7) outstanding.**
  - [x] 3.1.1 Capture pre-migration POM via `publishToMavenLocal` **(scratchpad `zoneinfo-outlook-pre.pom`, version `2.2.0-develop-SNAPSHOT`)**
  - [x] 3.1.2 Replace `maven-publish` + `pom.withXml` with `com.vanniktech.maven.publish` 0.34.0 + `mavenPublishing { pom { … } }`
  - [x] 3.1.3 Add `SONATYPE_HOST`, `SONATYPE_AUTOMATIC_RELEASE`, `RELEASE_SIGNING_ENABLED` to `gradle.properties`
  - [x] 3.1.4 Rename CI secrets to `ORG_GRADLE_PROJECT_*` **Workflow rewritten to the `ical4j-integration` pattern (`publishToMavenCentral --no-configuration-cache`, `ORG_GRADLE_PROJECT_mavenCentral*`/`signingInMemoryKey*` env). Secret provisioning is a maintainer step: repo has NO repo-level secrets — `CENTRAL_PORTAL_USERNAME/PASSWORD` + `GPG_SIGNING_KEY/GPG_SIGNING_PASSWORD` must be added (they exist on `ical4j-integration` only; org level has just `MAVEN_*`).**
  - [x] 3.1.5 Diff generated POM against 3.1.1; reconcile every difference **Clean: only element reordering + XML attribute formatting; canonical XML comparison confirms semantic identity. No intended differences.**
  - [x] 3.1.6 Delete legacy `publishing {}` and `signing` blocks **Also removed now-unused `ext.isReleaseVersion` and `withJavadocJar()/withSourcesJar()` (vanniktech provides both jars). `./gradlew build` green; local publish produces signed jar/sources/javadoc/module.**
  - [ ] 3.1.7 Publish snapshot from CI; confirm it lands on the Central Portal **BLOCKED — requires the four repo secrets above (same credentials blocker as 1.3).**
  - [x] 3.1.8 Write up anything surprising — the remaining repos follow this template
    - POM diff is textually noisy (vanniktech orders `scm`/`dependencies` differently) but semantically empty — diff canonically, not line-by-line.
    - Drop `withJavadocJar()/withSourcesJar()` and `ext.isReleaseVersion` with the legacy blocks; vanniktech supplies sources/javadoc jars itself.
    - **Found: `ical4j`'s own `publish-snapshots.yml` was broken** — the publish job's guard read `needs.gradle.result` but the job is named `test`, so publish was always skipped; and its env passed `MAVEN_USERNAME/MAVEN_PASSWORD`, which vanniktech does not read. **Fixed 2026-08-19** to the `ical4j-integration` pattern (committed on `docs/build-conventions-proposals` as "ci: repair snapshot publish workflow"); `ical4j` also has no repo-level secrets, so 4.2 still needs the four secrets provisioned there too.
- [ ] 3.2 `ical4j-vcard` — **Migrated 2026-08-19; PR ical4j/ical4j-vcard#37, stacked on `fix-gender-fn-validation` (retarget to `develop` when it merges). POM diff canonically identical (licence URL `ical4j/master/LICENSE` preserved as-is per D4); `./gradlew build` green; workflow moved to the `ical4j-integration` pattern. Required a wrapper bump 8.4 → 8.5 (vanniktech 0.34.0 needs ≥ 8.5; matches `ical4j`/`ical4j-integration`). Outstanding: CI snapshot (same secrets blocker as 3.1.7).**
- [ ] 3.3 `ical4j-extensions` — **Migrated 2026-08-19; PR ical4j/ical4j-extensions#41. POM diff canonically identical; `./gradlew build` green; wrapper bumped 8.4 → 8.5; workflow moved to the `ical4j-integration` pattern. The migration deletes the legacy repositories block outright, superseding the 1.2 URL fix. Outstanding: CI snapshot (secrets blocker).**
- [ ] 3.4 `ical4j-serializer` — **Migrated 2026-08-19; PR ical4j/ical4j-serializer#23. POM diff canonically identical (group `org.ical4j` unchanged); `./gradlew build` green; wrapper bumped 8.4 → 8.5; workflow moved to the `ical4j-integration` pattern. Repo had NO `gradle.properties` — created with the standard toggles. Outstanding: CI snapshot (secrets blocker).**
- [ ] 3.5 `ical4j-template` — **Migrated 2026-08-19; PR ical4j/ical4j-template#18. POM diff canonically identical (group `org.ical4j`; multiline description preserved); snapshot *resolution* repo declaration kept as-is (permitted by spec); workflow moved to the `ical4j-integration` pattern; already on Gradle 9.2.1, no wrapper bump. `./gradlew build -x test` green; `:test` has a pre-existing failure (`VAvailabilityViewTest` locale/emoji rendering) that also fails at clean `HEAD` — unrelated to this change. Outstanding: CI snapshot (secrets blocker).**
- [ ] 3.6 `ical4j-command` — delete legacy `publishing {}` block at `:81` and URL at `:110`; add missing `gradle.properties` keys; POM diff per module
- [ ] 3.7 `ical4j-connector` — four modules (`api`, `dav`, `google`, `msgraph`), four POM diffs; coordinate with the live `feat/complete-msgraph-connector` branch

## 4. Phase 3 — Verification

- [ ] 4.1 All nine repos: `./gradlew build` green
- [ ] 4.2 All nine repos: snapshot published from CI to the Central Portal within this change's window
- [ ] 4.3 Confirm no repo still references `https://central.sonatype.com` as a *deploy* target (the snapshot repo `…/repository/maven-snapshots/` is legitimate; the bare portal URL is not). **Scan 2026-08-19 across all `*.gradle` in all nine working trees: zero deploy references to the bare portal URL or the OSSRH bridge remain. Re-run once command/connector land.**
- [ ] 4.4 Confirm no repo still applies the raw `maven-publish` plugin. **Scan 2026-08-19: only `ical4j-connector/build.gradle:25` (deferred repo) still applies it. Re-run once connector lands.**
- [x] 4.5 Record the resulting uniform configuration — it is the direct input to `extract-build-conventions`. **Recorded in design.md, "Resulting uniform configuration".**
- [ ] 4.6 Validate this change: `openspec validate standardise-build-publishing --strict`
