# Contributing to iCal4j

Thanks for helping improve iCal4j. Bug reports, fixes, tests, documentation and answers in
[Discussions](https://github.com/orgs/ical4j/discussions) are all welcome.

This guide covers how to get a change merged. If anything here is unclear, ask in Discussions.

## Before you start

- **Questions and usage help** belong in [Discussions](https://github.com/orgs/ical4j/discussions), not issues.
- **Security vulnerabilities** must be reported privately, never as a public issue or pull request. See
  [SECURITY.md](SECURITY.md). This includes fixes: if your pull request fixes something an attacker could exploit
  (for example a crash, hang or injection triggered by untrusted calendar data), report it privately first so a fix
  can be released before details are public.
- **Bugs** should be reproducible on the current 4.x release or on `develop`. iCal4j 3.x and earlier are no longer
  maintained.
- **Larger changes**, such as new public API or a change in behaviour, are best discussed in an issue first, so
  you don't spend time on something that can't be merged.

Issues labelled [`good first issue`](https://github.com/ical4j/ical4j/labels/good%20first%20issue) or
[`help wanted`](https://github.com/ical4j/ical4j/labels/help%20wanted) are a good place to start.

## Development setup

You need **Java 17 or newer** to build. The build uses Error Prone and NullAway, which need 17+, but the published
library targets Java 11. The Gradle wrapper is included, so you don't need Gradle installed.

```bash
git clone https://github.com/ical4j/ical4j.git
cd ical4j
git checkout develop
./gradlew check
```

`./gradlew check` runs the full test suite plus the API compatibility (RevAPI) and style checks that CI runs. The
`Makefile` wraps the common tasks, so `make check` does the same thing.

Other useful commands:

| Command | What it does |
|---|---|
| `./gradlew test --tests 'net.fortuna.ical4j.model.RecurSpec'` | Run one test class |
| `./gradlew publishToMavenLocal` | Install a snapshot into your local Maven repository |
| `make listApiChanges` | Show public API changes detected by RevAPI |

### Tests that need Docker

A few integration tests start a local timezone-update server with
[Testcontainers](https://testcontainers.com/). They are skipped automatically when no Docker (or Podman) daemon is
available, so a local `check` still passes without Docker. CI runs them.

## Making a change

1. **Fork** the repository and create a branch from **`develop`**. `develop` is the default branch and the target for
   all pull requests. `master` tracks releases.
2. **Add a test** that fails without your change and passes with it. Java tests use JUnit 5
   (`SomeClassTest.java`); Groovy tests use Spock (`SomeClassSpec.groovy` or `SomeClassTest.groovy`). Sample `.ics`
   files go under `src/test/resources/samples/`.
3. **Follow RFC 5545** and the related RFCs listed in the [README](README.md#specifications). When behaviour is
   specified by an RFC, cite the section in your pull request.
4. **Keep the change focused.** Separate unrelated fixes into separate pull requests.
5. **Run `./gradlew check`** before pushing.
6. **Open a pull request** against `develop` and fill in the template.

### Code style

- Follow the conventions of the surrounding code. Checkstyle runs as part of `check`.
- New Java files start with the BSD-3-Clause licence header used by existing files.
- Avoid locale- or timezone-dependent behaviour: pass `Locale.ROOT` or `Locale.US` when changing case or formatting
  numbers, and don't rely on the JVM default timezone in library code.
- Don't change public API signatures unless the issue calls for it. RevAPI will flag breaking changes.

### OpenSpec changes

Maintainers record design decisions for larger changes as [OpenSpec](https://github.com/Fission-AI/OpenSpec) changes
under `openspec/`. You don't need to write one for your pull request; a maintainer may add one when merging if the
change warrants it.

## Pull request review

- CI must pass. For first-time contributors, a maintainer has to approve the workflow run before CI starts, so a
  short delay is normal.
- `develop` requires pull requests to be up to date before merging. If your branch falls behind, a maintainer can
  update it for you as long as **Allow edits by maintainers** is ticked.
- iCal4j is maintained by volunteers. If you haven't had a response in a couple of weeks, a polite comment on the pull
  request is welcome.

## AI-assisted and automated contributions

You're welcome to use AI tools or automated analysis to help you contribute, on the same terms as any other
contribution:

- You must understand the change, have run the tests yourself, and be able to answer review questions about it.
- Say in the pull request if the change came from an automated tool or scanner.
- Don't include promotional links or tracking references in issues or pull requests.

Pull requests that don't meet these may be closed.

## Licence

By contributing, you agree that your contributions are licensed under the project's
[BSD-3-Clause licence](LICENSE).

## Code of conduct

This project follows the [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you agree to uphold it.
