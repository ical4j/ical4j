# Microsoft timezone mapping

## Purpose

Defines the mapping of Microsoft Windows timezone names (the `msTimezones` and `msTimezoneNames` resources used by `TzHelper` and `TimeZoneRegistryImpl`) to IANA zone identifiers. Each alias must resolve to a zone whose daylight-saving behaviour matches the Windows zone it names, following the CLDR territory-001 mapping, so that converting a Windows-named TZID does not introduce or remove a DST shift.
## Requirements
### Requirement: Microsoft timezone aliases SHALL map to IANA zones with matching DST behaviour

Each entry in `msTimezones` and `msTimezoneNames` SHALL map a Windows timezone identifier to an IANA zone whose standard offset and daylight-saving behaviour match the Windows zone (the CLDR `windowsZones` territory `001` mapping is the reference). The two resource files SHALL agree for every identifier they both contain.

#### Scenario: Central America Standard Time has no daylight saving

- **WHEN** `TzHelper.getCorrectedTimeZoneIdFrom("Central America Standard Time")` is invoked
- **THEN** it returns `America/Guatemala`
- **AND** a July date-time in that zone has offset `-06:00`

