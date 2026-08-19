## Context

Survey of the nine ecosystem repos, 2026-07-21.

**What is duplicated.** `ical4j-vcard/build.gradle` (178 lines) vs `ical4j-extensions/build.gradle` (158 lines) differ only in: plugin versions (drift, not intent), description, dependencies, javadoc `links`, axion tag prefix, publication name, SCM URLs, licence URL, and the publish repository name. Structurally they are the same file. The same holds for `ical4j-serializer`, `ical4j-template` and `ical4j-zoneinfo-outlook`.

**What has drifted.** Four Gradle wrapper versions (8.4, 8.5, 9.1.0, 9.2.1), three bnd versions (6.1.0, 7.1.0, 7.2.3), axion-release spanning 1.13.6–1.21.1, revapi 1.7.0 vs 2.0.0 where present at all. Catalog skew is milder than feared: all nine are on Groovy 3.0.x (3.0.22 or 3.0.25) with Spock 2.4-M4 or 2.4-M7 and Log4j 2.23.1–2.26.0. There is no Groovy 3-vs-4 schism to resolve; convergence here is a patch-level exercise.

**What already works.** Reusable workflows. `ical4j-vcard/.github/workflows/publish-snapshots.yml:15` consumes `ical4j/ical4j/.github/workflows/test.yml@develop`. `ical4j-vcard` and `ical4j-extensions` have byte-identical `publish-snapshots.yml` files. The mechanism is proven; the rollout stopped partway, and core's copy has since drifted ahead onto SHA-pinned actions while satellites remain on the deprecated `gradle/gradle-build-action@v2`.

**Precondition.** `standardise-build-publishing` must land first — see that change's proposal. This design assumes all nine repos publish via `com.vanniktech.maven.publish` to the Central Portal.

## Goals / Non-Goals

**Goals**
- One definition of shared build configuration, consumed by all nine repos.
- Drift becomes structurally impossible for anything the plugins own.
- A change to shared config is one edit plus a rollout, not nine independent edits.
- Adoption is incremental and individually revertible.

**Non-Goals**
- Merging repositories.
- Changing coordinates, versions, or release lines of published libraries.
- Extracting configuration that cannot be extracted (the BouncyCastle force).

## Decisions

### D1. Convention plugins in a standalone `ical4j-build-logic` repo

Four plugins, split by concern so a repo takes only what applies:

| Plugin | Owns |
|---|---|
| `ical4j.java-library-conventions` | Java toolchain, `java-library` + `groovy`, javadoc options, JaCoCo, JUnit Platform + Spock wiring, bnd manifest attributes |
| `ical4j.publishing-conventions` | vanniktech plugin, POM (`name`/`description`/`url`/`licences`/`developers`/`scm`), signing toggles |
| `ical4j.release-conventions` | axion `scmVersion`, tag prefix, branch version creators |
| `ical4j.revapi-conventions` | revapi plugin and baseline wiring — applied only by the four repos that use it |

**Why a standalone repo rather than hosting build-logic inside `ical4j`:** hosting it here creates a dependency cycle — `ical4j`'s build would consume a plugin published by `ical4j`. Bootstrapping that is possible but unpleasant, and it makes `ical4j` unbuildable from a clean checkout until the plugin is published. A separate repo has no cycle.

**Rejected — `buildSrc` per repo:** `buildSrc` is build-local. Nine `buildSrc` directories is the same duplication one level down.

**Rejected — a single "kitchen sink" plugin:** `ical4j-serializer`, `ical4j-template`, `ical4j-command`, `ical4j-integration` and `ical4j-zoneinfo-outlook` do not use revapi. Forcing it on them means either applying an unused plugin or adding disable flags. Splitting by concern is cheaper.

### D2. Derive variation from `project.name`; parameterise only what genuinely varies

The apparently per-repo values are mechanical functions of the project name, and already follow that convention everywhere:

```groovy
// ical4j.release-conventions
scmVersion {
    tag { prefix = "${project.name}-" }        // ical4j-vcard-, ical4j-extensions-, …
    versionCreator 'versionWithBranch'
}

// ical4j.publishing-conventions
mavenPublishing {
    pom {
        url = "https://github.com/ical4j/${project.name}"
        scm { url = "https://github.com/ical4j/${project.name}" }
        licenses { … }   // identical across all nine
        developers { … } // identical across all nine
    }
}
```

Genuinely varying, and therefore parameterised via an extension or `gradle.properties`:
- **`group`** — `org.mnode.ical4j` for six repos, `org.ical4j` for `ical4j-serializer` and `ical4j-template`. The plugin MUST NOT hardcode a group.
- **Java toolchain** — 11 for most, 17 for `ical4j-template` and `ical4j-command`.
- **`description`**, **dependencies**, **revapi baseline** — stay in the consuming build.

Two known exceptions to the derivation, both to be reconciled during adoption rather than encoded as special cases: `ical4j-vcard` has branch-specific `branchPrefix`/`branchVersionCreator` entries for its 1.x maintenance branch, and licence URLs currently differ between repos (`standardise-build-publishing` D4 deliberately preserved that inconsistency so its POM diffs stayed clean; this change is where one value wins).

### D3. Version catalog published as a TOML artifact

One catalog, published from `ical4j-build-logic`, consumed in each `settings.gradle`:

```groovy
dependencyResolutionManagement {
    versionCatalogs {
        create('libs') { from('org.mnode.ical4j:ical4j-catalog:<version>') }
    }
}
```

Repos needing an extra library not in the shared catalog (jte for `ical4j-template`, Jackson for `ical4j-serializer`, jparsec/caffeine for `ical4j` feature variants) declare it locally. The shared catalog covers the common test and logging stack — Groovy, Spock, JUnit, Log4j, Hamcrest, Testcontainers — which is where all the observed skew is.

Catalog and plugins are versioned and released together from one repo, so a consumer upgrading one gets the other consistently.

### D4. `ical4j` core is a consumer, not a special case — but keeps its extras locally

Core has configuration nothing else has: four feature variants (`caffeineTimezoneCache`, `filterExpressions`, `groovyDsl`, `schemaValidation`), the Podman socket resolution for testcontainers, the `timezoneIndependenceTest` task, and the BouncyCastle force. None of that generalises.

Core therefore applies the conventions for the shared 80% and keeps its specifics in its own `build.gradle`. Convention plugins compose additively, so this needs no plugin support.

**Core is migrated last.** It is the most-consumed artifact and the most complex build; the pilot must not be the thing that breaks if adoption goes wrong. If core turns out to fit badly, it stays unmigrated — the change still delivers most of its value from the eight satellites.

### D5. Pilot on `ical4j-zoneinfo-outlook`; it absorbs every subsequent build-logic release first

`ical4j-zoneinfo-outlook` has the smallest build (108 lines), one dependency (`libs.ical4j`), no revapi, no feature variants, and no live feature branch. If the convention plugins cannot express it cleanly, they are wrong.

Beyond the pilot, it stays the canary: each new `build-logic` version is adopted there and verified before the other repos bump. This is the main mitigation for the coupling risk in D6.

### D6. Separate repos now; the design does not prejudge a monorepo

Distribution differs between the two worlds, but the consuming code does not:

```
   SEPARATE REPOS (this change)        MONOREPO (possible later)
   build-logic published to Central    build-logic/ as includeBuild
   each repo pins a version            no version at all
   config change = publish + 9 PRs     config change = 1 commit
   ────────────────────────────────────────────────────────────
   plugins { id 'ical4j.java-library-conventions' }   ← identical
```

Because consumers only ever reference plugin IDs, a later merge relocates `build-logic` and deletes version pins without touching a single `plugins {}` block. Doing this change first therefore does not commit to either answer — and it removes boilerplate from the list of reasons to merge, so that decision can be made on its actual merits (atomic cross-library change, single CI verdict).

### D7. Finish the reusable-workflow rollout; keep hosting them in `ical4j`

The precedent is `ical4j`; moving the workflows to `ical4j-build-logic` would mean re-pointing the one consumer that already works, for no gain. Workflows stay here.

Rollout: every repo consumes `build.yml`, `create-release.yml` and `publish-snapshots.yml` via `workflow_call`; all third-party actions SHA-pinned as core already does; the deprecated `gradle/gradle-build-action@v2` retired; CI JDK matrix stated deliberately rather than inherited by accident.

## Risks / Trade-offs

| Risk | Mitigation |
|---|---|
| **Shared failure domain** — one bad release breaks nine builds | Pinned versions only, never dynamic; pilot repo absorbs each release first (D5); a bad version is skipped, not hotfixed |
| Rollout tax: publish + 9 PRs per change | Renovate automates the bumps; accepted cost of not merging repos, and stated plainly so a monorepo decision can weigh it |
| Convergence breaks a build (bnd 6.1.0→7.x, Gradle 8→9) | Convergence is a separate step per repo, reviewed independently of convention adoption |
| Over-abstraction — plugins accrete flags for one-off cases | Anything needed by only one repo stays in that repo (D4). A parameter is added only when two repos need it differently |
| `ical4j` core does not fit | Migrated last; may stay unmigrated without invalidating the change |
| Live feature branches (`ical4j-connector`, `ical4j-vcard`) conflict | Both sequenced late, as in `standardise-build-publishing` |
| Build-logic itself needs the publishing config it defines | Bootstrap with minimal hand-written publishing; it is one small repo, not nine |

## Open Questions

- Which licence URL wins? Repos currently disagree (`ical4j/ical4j/master/LICENSE` vs per-repo `LICENSE.txt`). Needs one answer before `publishing-conventions` can be written.
- Does `ical4j-vcard`'s 1.x-maintenance branch configuration need to survive in the shared `release-conventions`, or can it stay local to that repo?
- Target Gradle wrapper version — 9.2.1 (highest in use, two repos) or 8.5? Determines how much convergence risk lands on the seven repos on 8.x.
- Should `ical4j-connector`, `ical4j-integration` and `ical4j-command` — already multi-module — apply conventions in their `subprojects {}` blocks, or per module? Affects how the plugins handle root projects that publish nothing.
