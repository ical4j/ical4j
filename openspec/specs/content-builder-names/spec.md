# Content builder names

## Purpose

Defines name matching for the content builders used by the parser (`ComponentBuilder`, `PropertyBuilder`): component, property and parameter names are case-insensitive per RFC 5545 §3.1, so `hasName` comparisons must succeed regardless of the case of either side. Matters for parser guards such as the TZID exclusion inside `STANDARD`/`DAYLIGHT` observances.
## Requirements
### Requirement: ComponentBuilder SHALL match names case-insensitively

`ComponentBuilder.hasName(String)` SHALL return `true` when the argument equals the builder's name ignoring case (RFC 5545 §3.1: component names are case-insensitive), matching the existing behaviour of `PropertyBuilder.hasName`.

#### Scenario: Lower-case name set, any-case query

- **WHEN** a `ComponentBuilder` is initialised with `name("standard")`
- **THEN** `hasName("STANDARD")`, `hasName("standard")` and `hasName("Standard")` all return `true`
- **AND** `hasName("DAYLIGHT")` returns `false`

