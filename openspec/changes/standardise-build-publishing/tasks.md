## 1. Phase 0 — Unblock and decide

- [ ] 1.1 Check whether the current `pl.allegro.tech.build.axion-release` release ships BouncyCastle ≥ 1.84. Record the answer in design.md under "Open Questions". If yes, Phase 2 becomes "upgrade axion-release" instead of "copy the force block".
- [ ] 1.2 Fix `ical4j-extensions/build.gradle:146` — release URL `https://central.sonatype.com` → `https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/`. Single-commit PR, shipped independently of everything below.
- [ ] 1.3 Verify 1.2 by publishing a snapshot from `ical4j-extensions` CI.
- [ ] 1.4 Inspect `ical4j-command`'s most recent successful release run to determine which publishing path actually executed. Record in design.md.

## 2. Phase 1 — BouncyCastle posture

Skip this entire phase if 1.1 found a patched axion-release; do task 2.9 instead.

- [ ] 2.1 `ical4j-vcard` — add force block to root `buildscript`, comment included
- [ ] 2.2 `ical4j-extensions`
- [ ] 2.3 `ical4j-serializer`
- [ ] 2.4 `ical4j-template`
- [ ] 2.5 `ical4j-zoneinfo-outlook`
- [ ] 2.6 `ical4j-connector`
- [ ] 2.7 `ical4j-integration`
- [ ] 2.8 `ical4j-command`
- [ ] 2.9 *(alternative to 2.1–2.8)* Upgrade axion-release to the patched version in all nine repos and remove the force block from `ical4j/build.gradle:1-15`
- [ ] 2.10 Confirm each repo still builds: `./gradlew build` green in all nine

## 3. Phase 2 — Publishing migration

Each repo below is one PR. Every repo follows the D4 procedure: capture pre-migration POM → migrate → diff → reconcile → delete legacy config. A repo is not done until its snapshot publishes from CI.

- [ ] 3.1 `ical4j-zoneinfo-outlook` (pilot — smallest build, single dependency, no revapi)
  - [ ] 3.1.1 Capture pre-migration POM via `publishToMavenLocal`
  - [ ] 3.1.2 Replace `maven-publish` + `pom.withXml` with `com.vanniktech.maven.publish` 0.34.0 + `mavenPublishing { pom { … } }`
  - [ ] 3.1.3 Add `SONATYPE_HOST`, `SONATYPE_AUTOMATIC_RELEASE`, `RELEASE_SIGNING_ENABLED` to `gradle.properties`
  - [ ] 3.1.4 Rename CI secrets to `ORG_GRADLE_PROJECT_*`
  - [ ] 3.1.5 Diff generated POM against 3.1.1; reconcile every difference
  - [ ] 3.1.6 Delete legacy `publishing {}` and `signing` blocks
  - [ ] 3.1.7 Publish snapshot from CI; confirm it lands on the Central Portal
  - [ ] 3.1.8 Write up anything surprising — the remaining repos follow this template
- [ ] 3.2 `ical4j-vcard` (same sub-steps; note the licence-URL discrepancy in D4 — preserve it as-is)
- [ ] 3.3 `ical4j-extensions` (same sub-steps)
- [ ] 3.4 `ical4j-serializer` (same sub-steps; group is `org.ical4j`)
- [ ] 3.5 `ical4j-template` (same sub-steps; group is `org.ical4j`; also has a snapshot repo declaration at `:22`)
- [ ] 3.6 `ical4j-command` — delete legacy `publishing {}` block at `:81` and URL at `:110`; add missing `gradle.properties` keys; POM diff per module
- [ ] 3.7 `ical4j-connector` — four modules (`api`, `dav`, `google`, `msgraph`), four POM diffs; coordinate with the live `feat/complete-msgraph-connector` branch

## 4. Phase 3 — Verification

- [ ] 4.1 All nine repos: `./gradlew build` green
- [ ] 4.2 All nine repos: snapshot published from CI to the Central Portal within this change's window
- [ ] 4.3 Confirm no repo still references `https://central.sonatype.com` as a *deploy* target (the snapshot repo `…/repository/maven-snapshots/` is legitimate; the bare portal URL is not)
- [ ] 4.4 Confirm no repo still applies the raw `maven-publish` plugin
- [ ] 4.5 Record the resulting uniform configuration — it is the direct input to `extract-build-conventions`
- [ ] 4.6 Validate this change: `openspec validate standardise-build-publishing --strict`
