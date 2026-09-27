## Context

**How a TZID-bearing value is stored and read:**
- `DateProperty.setValue()` and `DateListProperty.setValue()` store values as `TemporalAdapter`s. `DateListProperty` wraps them in a `DateList`.
- A `TemporalAdapter` keeps `valueString`: the original text when parsed, or the formatted temporal when built from one. It resolves the zone lazily in `getTemporal()`.
- `equals()`/`hashCode()` of `TemporalAdapter` are already defined on `valueString`, so the text is the adapter's identity.

**After #890 (`date-property-parsing` spec):**
- `TemporalAdapter.getTemporal()` catches `DateTimeException`. It falls back to a floating parse under `KEY_RELAXED_VALIDATION` and rethrows otherwise.
- `DateProperty.getDate()` and `getValue()` each guard their `tzId.toZoneId(...)` call the same way. `getValue()` returns `Strings.valueOf(date)` under relaxed validation and rethrows in strict mode.
- `DateProperty.validate()` adds an ERROR for an unresolvable TZID in every mode.

**Gaps:**
- `DateListProperty.getValue()` (`dates.toString(tzId.toZoneId(...))`) and `getDates()` (`TemporalAdapter.toLocalTime(date, tzId.toZoneId(...))`) have no guard at all.
- `ExDate`/`RDate` validate through `RecurrencePropertyValidators`, which don't check TZID resolvability.
- `Property.toString()` is `name + params + ":" + getValue()`, so any `getValue()` failure breaks `Calendar.toString()` and `CalendarOutputter`.

## Goals / Non-Goals

**Goals:**
- `getValue()`/`toString()` never fail for a date or date-list value that parsed, whatever the TZID and whatever the mode.
- Date lists get the same typed-access contract as single dates (#890).
- Unresolvable TZIDs are still reported by `validate()` for both property kinds.

**Non-Goals:**
- Changing typed-access semantics: strict `getDate()`/`getDates()` still throw.
- Guessing a zone for an unknown TZID, or rewriting the TZID.
- `UtcProperty` and `VALUE=DATE` values, which never apply a TZID. They're unaffected.

## Decisions

### D1. Split the contract into textual access and typed access

- **Textual access** (`getValue()`, and `toString()` through it) is how a calendar gets serialised. For an unresolvable TZID it SHALL return the stored value text, in every mode.
- **Typed access** (`getDate()`, `getDates()`, `getTemporal()`) keeps #890's rule: throw in strict mode, floating fallback under relaxed validation.

**Why:** the only thing strict-mode `getValue()` protected was a guarantee that "every access path throws". But the text it refuses to return is the exact input, and it's already stored. Refusing it turns one bad parameter into an unwritable calendar. Strictness still matters where the zone actually changes the meaning, i.e. converting to an instant, so typed access and `validate()` keep it.

**Rejected:**
- **Keep strict textual access, and only do A.** That fixes relaxed-mode serialisation but not #889's actual report, and strict-mode clients (the default) still can't write back received events.
- **Make everything lenient in strict mode.** That would silently float date-times in strict mode, hiding a data problem wherever an instant is computed.

### D2. Return the stored text, not a re-formatted temporal

Add a public accessor on `TemporalAdapter`, e.g. `getValueString()`, that returns `valueString` without resolving anything. Add `DateList.toValueString()`, which joins each adapter's value text with `,`. On a caught `DateTimeException`:
- `DateProperty.getValue()` returns `date.getValueString()`;
- `DateListProperty.getValue()` returns `dates.toValueString()`.

**Why not keep `Strings.valueOf(date)`,** as #890 does under relaxed validation? That calls `TemporalAdapter.toString()` → `getTemporal()`, which only succeeds because the relaxed floating fallback kicked in. In strict mode it would throw again. The stored text needs no parsing and no zone, and for parsed input it's byte-identical to the input. For relaxed mode the output doesn't change: the floating form of a parsed value is its text.

**Values built in code:** a `TemporalAdapter` built from a `ZonedDateTime` stores that value formatted in its own zone. If the property's TZID parameter is later changed to something unresolvable, `getValue()` returns that formatted local text. That's the best available representation and matches what serialisation would have produced before the TZID changed.

### D3. `DateListProperty.getDates()` mirrors `DateProperty.getDate()`

Wrap the `tzId.toZoneId(...)` call: on `DateTimeException`, return the floating temporals (`dates.getDates()`, whose adapters already fall back under relaxed validation) if `KEY_RELAXED_VALIDATION` is enabled, otherwise rethrow. Resolve the zone once per call, not per element.

### D4. Report unresolvable TZIDs in `DateListProperty.validate()`

Add the same check `DateProperty.validate()` has: if a `TZID` is present, applies to the values (not `VALUE=DATE`/`PERIOD`-only, not UTC), and resolves to no known zone, add an ERROR `ValidationEntry`. Put it in `DateListProperty.validate()` or its validator so both `ExDate` and `RDate` get it, and make sure the validator doesn't itself touch typed access in a way that throws.

### D5. Where resolution happens

To tell "unresolvable" apart from other failures, keep catching `DateTimeException` around the `toZoneId(...)` call only, as #890 does. Don't broaden the `try` to include the formatting.

**Found during implementation:** `DateProperty.getValue()` calls `shouldApplyTimezone()` before resolving the zone, and that calls `isUtc()` → `date.getTemporal()`, which resolves the TZID and throws in strict mode. The guarded region therefore covers `shouldApplyTimezone()` plus `toZoneId(...)`, both of which only resolve. Formatting stays outside it. `isUtc()` itself is public typed access and keeps its strict behaviour.

## Risks / Trade-offs

- **[Strict-mode callers relying on the exception]** Code that used `getValue()` throwing to detect bad TZIDs now gets a string. → Called out as a behaviour change in the PR description and release notes. `getDate()`, `getDates()` and `validate()` still detect it.
- **[Serialised output keeps a bad TZID]** Writing back `DTSTART;TZID=Unknown:...` preserves the defect instead of fixing it. → That's intended: it's what was received. Validation still flags it, and fixing TZIDs belongs to the compliance transforms, not serialisation.
- **[`RDATE;VALUE=PERIOD` with a TZID]** Period values go through a different path. → Cover them in tests. If they have the same unguarded resolution, apply the same text fallback. If not, record it as out of scope.

## Migration Plan

No migration. Additive API. Roll back by reverting.

## Open Questions

- **`DateProperty.hashCode()`** calls `getDate()`, so in strict mode it also throws for an unresolvable TZID. Putting such a property in a hash-based collection, or hashing a component that contains one, fails. It's outside this change (typed access stays strict), but it's a likely next report. Options: hash on the stored value text plus parameters, mirroring `TemporalAdapter`'s `equals`/`hashCode`.

- **Accessor naming:** should the new `TemporalAdapter` accessor be `getValueString()` or `getValue()`? The latter reads like the `Property` API, but `TemporalAdapter` isn't a property. Proposed: `getValueString()`.
- **Re-checking #452:** after this lands, re-test #452's scenario (the reporter believes it's the same bug) and close it with a reference if so.
