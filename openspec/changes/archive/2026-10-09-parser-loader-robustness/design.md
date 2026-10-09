## Context

Four unrelated code paths, one theme: iCal4j fails hard where it could degrade safely.

1. **Parser tokenisation.** `CalendarParserImpl` drives a `java.io.StreamTokenizer` with `wordChars(32, 255)` and `whitespaceChars(0, 20)`. Space is therefore a *word* character, which is deliberate (property values contain spaces and the tokeniser must not split them) but means a `BEGIN:`/`END:` name token includes any trailing spaces. `UnfoldingReader` additionally removes `CRLF + SPACE` sequences, so a whitespace-only line after `END:VCALENDAR` is folded into the `END` line. Tabs are `< 21` and so already whitespace to the tokeniser, which is why only spaces trigger #175.
2. **Parameter construction.** `DefaultContentHandler.parameter(name, value)` builds the parameter via `ParameterBuilder` and only then attaches it to the pending `PropertyBuilder`. Parameter factories such as `Range.Factory` validate the value in the constructor and throw `IllegalArgumentException`. The handler has no property context at that point, so a vendor property that reuses a standard parameter name with a vendor value (`X-LOTUS-RECURID;RANGE=ALL`) cannot be distinguished from a genuinely invalid standard property. The existing escape hatch is the global Notes compatibility hint, which relaxes `Range` everywhere.
3. **Validator factory lookup.** `AbstractCalendarValidatorFactory` resolves its singleton in a static initialiser with `ServiceLoader.load(...).iterator().next()`. The service file `META-INF/services/net.fortuna.ical4j.validate.CalendarValidatorFactory` is the only registration. `Calendar`'s constructor touches this class, so a missing service file surfaces as `ExceptionInInitializerError` on the first calendar constructed. Parsing no longer relies on `ServiceLoader` (factory suppliers are hard-coded since the 3.x rewrite); this is the last `ServiceLoader` call on the hot path.
4. **Locale-sensitive formatting.** #458 was fixed for `ZoneOffsetAdapter` and `TemporalAmountAdapter` by passing `Locale.US`. `TimeZoneLoader.addTransitionRules` still formats `FREQ=YEARLY;BYMONTH=%d;BYDAY=%d%s` with the default locale. The output is immediately parsed by `new RRule<>(text)`, and `Integer.parseInt` accepts any Unicode decimal digit, so the defect is masked today; it would surface if the text were ever emitted or compared directly.

Constraints: no public signature changes (RevAPI runs in `check`); strict-mode semantics for *valid-looking* standard content must not loosen; NullAway is enforced on `src/main`.

## Goals / Non-Goals

**Goals:**
- Accept trailing whitespace on `BEGIN`/`END` lines and whitespace-only trailing lines in strict mode, with the component name reported without the whitespace.
- Keep vendor parameters on vendor properties, with their raw value, without requiring a global compatibility hint.
- Guarantee `AbstractCalendarValidatorFactory.getInstance()` never throws because of a missing service registration.
- Make every numeric `String.format` in `src/main` locale-pinned.

**Non-Goals:**
- Changing how property *values* or property *names* with embedded/trailing whitespace are handled (`SUMMARY :x` remains invalid).
- Relaxing standard-parameter validation on standard properties, or changing what the Notes compatibility hint does.
- Replacing `ServiceLoader` with another discovery mechanism, or removing the service file.
- Reviving the dead `TimeZoneLoader.generateTimezoneForId` path (`TIMEZONE_DEFINITIONS` is never populated); noted for a separate cleanup.

## Decisions

### D1. Strip trailing whitespace from name tokens at the two consumer sites, not in the tokeniser

`ComponentParser.parse` (the name after `BEGIN:`) and `assertToken(String, …)` (the name after `END:` and the `VCALENDAR` token) call `String.stripTrailing()` on `tokeniser.sval`.

*Alternative — make space a whitespace character in the tokeniser:* breaks every value containing a space. *Alternative — strip in `nextToken`:* would also strip property values, which must be preserved verbatim (`FoldingWriter` round-trip tests rely on it). Stripping only where a token is used as a *name* is the minimal change and leaves `PropertyParser` untouched.

`stripTrailing()` is Java 11 API, matching the bytecode target.

### D2. Fallback to `XParameter` decided by the enclosing property, inside the handler

`DefaultContentHandler.parameter()` wraps the `ParameterBuilder.build()` call: on `IllegalArgumentException`, if `propertyBuilder.hasExperimentalName()` the parameter becomes `new XParameter(name, value)`; otherwise the exception propagates as before. `PropertyBuilder.hasExperimentalName()` reuses the existing `AbstractContentBuilder.isExperimentalName` test.

*Alternative — relax `Range` itself (accept any value):* loses validation for `RECURRENCE-ID;RANGE=…`, where RFC 5545 defines exactly one legal value. *Alternative — pass the property name into `ParameterBuilder`:* larger API change for the same effect; the handler already owns both builders. *Alternative — tell users to set the Notes hint:* that is the status quo and is global.

The raw (undecoded) value is retained for the fallback. `ParameterBuilder` RFC 6868-decodes values before handing them to factories, and `XParameter` is `Encodable`, so it is re-encoded on output; for the values this path sees (plain tokens such as `ALL`) decoding is the identity, and the round-trip test asserts the line is emitted unchanged.

### D3. `hasNext()` guard with an in-code default

```java
var factories = ServiceLoader.load(CalendarValidatorFactory.class, DefaultCalendarValidatorFactory.class.getClassLoader()).iterator();
instance = factories.hasNext() ? factories.next() : new DefaultCalendarValidatorFactory();
```

A registered provider still wins, so custom factories keep working. Only the "no registration at all" case changes, from a crash to the documented default. The class loader argument (fixed for OSGi in `4402bc83a`) is unchanged.

*Alternative — catch `ServiceConfigurationError`/`NoSuchElementException`:* `hasNext()` is the idiomatic check and avoids catching errors that indicate a genuinely broken registration (malformed service file, provider class missing), which should still surface.

Test approach: a `ClassLoader` subclass re-defines `AbstractCalendarValidatorFactory` and `DefaultCalendarValidatorFactory` from the parent's bytes (so the static initialiser runs again) and returns an empty enumeration for the service resource. `ServiceLoader.load(Class, ClassLoader)` consults `loader.getResources(...)`, so the override is honoured. The test asserts the instance is a `DefaultCalendarValidatorFactory` defined by the isolating loader. Before the fix the same test fails with `ExceptionInInitializerError` (verified).

### D4. Extract `transitionRuleText(month, index, dayOfWeek)` and pin it to `Locale.US`

A package-private static helper makes the format directly testable; the behavioural test on `addTransitionRules` (whole observance serialises as ASCII) is retained as a guard but cannot fail on the old code because `Recur` normalises the digits. The helper test does fail on the old code.

## Risks / Trade-offs

- [A producer relying on trailing spaces being part of a component name] → Not a realistic contract; RFC 5545 names are `iana-token`/`x-name` with no whitespace, and such input previously failed to parse at all.
- [X- property with a *standard* parameter whose value is merely mistyped now parses instead of failing] → Accepted: on an X- property the parameter's semantics are the producer's, and strict mode still rejects the same parameter on standard properties. The fallback only triggers on `IllegalArgumentException` from the factory, so unknown parameter *names* follow the existing `allowIllegalNames` path unchanged.
- [`XParameter` instead of `Range` for `RANGE` on X- properties when the Notes hint is **off**] → With the hint on, `Range.Factory` accepts the value and a `Range` instance is produced, as today; the new test covers both.
- [Fallback hides a misconfigured custom factory] → Only when the registration is entirely absent; a present-but-broken registration still throws `ServiceConfigurationError`.
- [RevAPI] → `PropertyBuilder.hasExperimentalName()` is an addition; no removals or signature changes expected.

## Migration Plan

Straight fix on a branch off `develop`; no configuration or data migration. Rollback is a revert of the single commit.

## Open Questions

- None blocking. Whether `TimeZoneLoader.generateTimezoneForId` (dead since `TIMEZONE_DEFINITIONS` is never populated) should be removed is a separate cleanup.
