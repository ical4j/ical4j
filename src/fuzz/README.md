# Fuzz harnesses

[Jazzer](https://github.com/CodeIntelligenceTesting/jazzer) harnesses for iCal4j, built for
[OSS-Fuzz](https://google.github.io/oss-fuzz/) and runnable locally. They are not part of the published artifact.

Each harness is a class in `net.fortuna.ical4j.fuzz` with a `fuzzerTestOneInput` method. A harness that takes a
whole calendar reads one or two selector bytes first: these choose the compatibility hints (strict, or relaxed
parsing, unfolding, validation and Outlook/Notes compatibility) and, for some harnesses, the
`suppressInvalidProperties` builder or the operation to run. The class Javadoc of each harness describes its input
and the behaviour it checks.

| Harness | Target |
|---|---|
| `CalendarBuilderParseFuzzer` | `CalendarBuilder#build` on raw bytes, then `toString()` |
| `VTimeZoneZoneRulesFuzzer` | `ZoneRules` built from a sender's `VTIMEZONE` |
| `RecurrenceSetExpansionFuzzer` | `calculateRecurrenceSet` with RDATE/EXDATE/overrides |
| `RecurDateGenerationFuzzer` | `Recur#getDates` |
| `CalendarOutputRoundTripFuzzer` | parse, write, parse |
| `PropertyOutputInjectionFuzzer` | values built from user text, written and re-parsed |
| `TimeZoneRegistryStateFuzzer` | the shared zone id pool across registries |
| `TextCodecsFuzzer` | text, parameter and URI escaping |
| `LazyValueAccessorsFuzzer` | lazily parsed values (`getDate()`, `getRecur()`, …) |
| `AttachTempFilesFuzzer` | `ATTACH` binary values and temporary files |
| `ItipUserAgentFuzzer` | the iTIP user agents (REPLY, COUNTER, DELEGATE, …) |
| `CalendarValidationFuzzer` | `validate()` |
| `CalendarCopySplitFuzzer` | `copy()` and calendar splitting |
| `RecurRuleRoundTripFuzzer` | `Recur` parse and `toString()` |
| `TzidResolutionFuzzer` | TZID lookups, including path traversal into the classpath |
| `DurationPeriodOffsetFuzzer` | duration, period and UTC offset values |
| `UnfoldingFoldingFuzzer` | `UnfoldingReader` and `FoldingWriter` |
| `ComplianceTransformerFuzzer` | the RFC 5545 compliance transformers |

## Layout

- `java/`: the harnesses and their shared helpers (`Ical`, `Rules`, `Diff`).
- `resources/`: `twake-fuzz-canary.ics`, which `TzidResolutionFuzzer` uses to detect a TZID resolving outside
  `zoneinfo/`.
- `dictionaries/`: libFuzzer dictionaries. `ics.dict` is shared; `<Harness>-extra.dict` is added to it.
- `corpus/<Harness>/`: seed inputs kept as they are, selector bytes included.

The per-harness settings (dictionaries, `max_len`, heap, extra JVM options and the seeds taken from
`src/test/resources`) are in `gradle/fuzz.gradle`.

## Building

```bash
./gradlew fuzzPackage
```

This writes `build/fuzz`:

- `lib/`: the runtime classpath, as jars. Jazzer itself is not included.
- `<Harness>.dict`, `<Harness>.options` and `<Harness>_seed_corpus.zip`, named as OSS-Fuzz expects.
- `fuzzers.tsv`: the harness class and its JVM options, one per line.

`./gradlew check` compiles the harnesses, so API changes that break them fail the build.

## Running locally

With the [Jazzer](https://github.com/CodeIntelligenceTesting/jazzer/releases) jars (`jazzer` and `jazzer-api`,
from Maven Central or a release) in `$JAZZER`:

```bash
./gradlew fuzzPackage
CP="$(ls build/fuzz/lib/*.jar | tr '\n' ':')$JAZZER/jazzer-0.30.0.jar:$JAZZER/jazzer-api-0.30.0.jar"
H=CalendarBuilderParseFuzzer
mkdir -p corpus/$H && unzip -oq build/fuzz/${H}_seed_corpus.zip -d corpus/$H
java $(grep "\.$H	" build/fuzz/fuzzers.tsv | cut -f2) -cp "$CP" com.code_intelligence.jazzer.Jazzer \
  --target_class=net.fortuna.ical4j.fuzz.$H \
  --instrumentation_includes='net.fortuna.ical4j.**' --instrumentation_excludes='net.fortuna.ical4j.fuzz.**' \
  -dict=build/fuzz/$H.dict -max_len=32768 corpus/$H
```

To replay a single input, pass the file instead of the corpus directory.

## Credits

The harnesses were contributed by Benoit Tellier ([@chibenwa](https://github.com/chibenwa)), from the `twake-fuzz`
audit of the iCal4j parsing surface.
