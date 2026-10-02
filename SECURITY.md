# Security Policy

## Reporting a vulnerability

**Please don't report security vulnerabilities through public GitHub issues, pull requests or discussions.**

Report them privately through GitHub's private vulnerability reporting instead:

1. Go to the repository's **Security** tab.
2. Choose **Report a vulnerability**.
3. Fill in the advisory form.

Direct link: <https://github.com/ical4j/ical4j/security/advisories/new>

The report stays visible only to you and the maintainers until a fix is published. If the issue affects another iCal4j repository (`ical4j-vcard`, `ical4j-connector`, `ical4j-extensions`, …), report it in that repository, or mention the affected repositories in your report.

### What to include

- the affected version(s) of iCal4j and, if relevant, the Java version and platform (e.g. Android);
- a description of the issue and its impact: what an attacker can achieve, and under what conditions;
- a minimal reproduction, ideally a small `.ics` input and the API calls that trigger the problem;
- any suggested fix or mitigation you have in mind.

### What to expect

- We aim to acknowledge a report within a week, and to agree on an assessment and a plan with you after that.
- Fixes are developed in a private fork where needed. We'll coordinate a disclosure date with you and credit you in the advisory unless you'd prefer otherwise.
- iCal4j is maintained on a volunteer basis, so please allow reasonable time before any public disclosure.

## Supported versions

Security fixes are made on the current release line.

| Version | Supported |
|---------|-----------|
| 4.3.x (latest) | ✅ |
| 4.0 – 4.2 | ❌ Upgrade to the latest 4.x |
| 3.x and earlier | ❌ No longer maintained |

Snapshots (`*-SNAPSHOT`) are pre-release builds and aren't covered, but reports against `develop` are welcome.

## Scope

In scope: vulnerabilities in the published iCal4j libraries, for example in parsing untrusted calendar data, timezone resolution, or resource and network access triggered by input.

Out of scope:
- vulnerabilities in third-party dependencies with no exploitable path through iCal4j (please report those upstream);
- build-time-only tooling that doesn't ship in a published artifact, such as Gradle plugin dependencies;
- issues that require an attacker to already control the application's classpath or configuration.
