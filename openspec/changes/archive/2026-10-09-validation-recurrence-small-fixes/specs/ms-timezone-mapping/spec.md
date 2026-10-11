## ADDED Requirements

### Requirement: Microsoft timezone aliases SHALL map to IANA zones with matching DST behaviour

Each entry in `msTimezones` and `msTimezoneNames` SHALL map a Windows timezone identifier to an IANA zone whose standard offset and daylight-saving behaviour match the Windows zone (the CLDR `windowsZones` territory `001` mapping is the reference). The two resource files SHALL agree for every identifier they both contain.

#### Scenario: Central America Standard Time has no daylight saving

- **WHEN** `TzHelper.getCorrectedTimeZoneIdFrom("Central America Standard Time")` is invoked
- **THEN** it returns `America/Guatemala`
- **AND** a July date-time in that zone has offset `-06:00`
