## Why

A calendar that references an unknown time zone (`DTSTART;TZID=Unknown:...`) parses successfully but then can't be read or written (issue #889; likely also #452). Downstream, DAVx5 crashed on exactly this (bitfireAT/davx5-ose#2339).

#890 made the value readable under relaxed validation, but only partly. Testing `develop` with `DTSTART` and `EXDATE` both carrying `TZID=Unknown`:

| | Strict (default) | Relaxed validation |
|---|---|---|
| `DTSTART` `getValue()` / `getDate()` | throws | works |
| `EXDATE` `getValue()` / `getDates()` / `toString()` | throws | **throws** |
| `calendar.toString()` | **throws** | **throws** |

There are two problems:

1. **#890 never covered `DateListProperty`** (EXDATE, RDATE). It resolves the zone without a guard in `getValue()` and `getDates()`. So a calendar with such a date list can't be serialised **even under relaxed validation**.
2. **In strict mode, textual access throws by design.** #890's spec requires `getValue()` and `toString()` to throw for an unresolvable TZID. So a client can't write back an event it just received, and can't get at the raw value, which is what #889 asks for. The original text is already stored (`TemporalAdapter` keeps it), so throwing protects nothing.

## What Changes

- **A. Finish #890 for date lists.** `DateListProperty.getDates()` gets the same relaxed-validation fallback as `DateProperty.getDate()`: floating values with the TZID ignored under relaxed validation, a `DateTimeException` in strict mode.
- **B. Textual access never fails for a value that parsed.** For an unresolvable TZID, `getValue()` and therefore `toString()` return the **original value text**, in strict and relaxed modes alike, for both `DateProperty` and `DateListProperty`. That makes `calendar.toString()` safe for any calendar that parsed.
- **Typed access keeps #890's contract.** `DateProperty.getDate()`, `DateListProperty.getDates()` and `getTemporal()` still throw `DateTimeException` in strict mode and fall back to floating values under relaxed validation.
- **Validation is unchanged.** `validate()` still reports an ERROR for an unresolvable TZID in every mode. This change extends that check to `DateListProperty` if it isn't already reported there.
- Add a public accessor for an adapter's value text (`TemporalAdapter`), and a joined form on `DateList`, so the properties can reach the original text without resolving the zone.

**Behaviour change:** in strict mode, `getValue()` and `toString()` on a property with an unresolvable TZID now return the value text instead of throwing. Code that relied on that exception to detect bad TZIDs should use `getDate()`/`getDates()` or `validate()` instead.

## Capabilities

### New Capabilities
<!-- None. -->

### Modified Capabilities
- `date-property-parsing`:
  - the strict-validation requirement no longer applies to textual access (`getValue()`/`toString()`);
  - the relaxed-fallback requirement is restated to separate typed from textual access;
  - new requirements cover `DateListProperty`, and serialising a calendar with an unresolvable TZID.

## Impact

- **Code:**
  - `net.fortuna.ical4j.model.property.DateProperty` (`getValue()`);
  - `net.fortuna.ical4j.model.property.DateListProperty` (`getValue()`, `getDates()`, and `validate()` if needed);
  - `net.fortuna.ical4j.model.TemporalAdapter` (new value-text accessor);
  - `net.fortuna.ical4j.model.DateList` (joined value text).
- **Public API:** two additive methods. RevAPI should classify them as non-breaking. There are no signature changes.
- **Behaviour:** strict-mode `getValue()`/`toString()` stop throwing for unresolvable TZIDs (see above). Behaviour for resolvable TZIDs is unchanged.
- **Downstream:** CalDAV/CardDAV clients (DAVx5 and others) can round-trip events that reference unknown zones.
