## ADDED Requirements

### Requirement: ComponentBuilder SHALL match names case-insensitively

`ComponentBuilder.hasName(String)` SHALL return `true` when the argument equals the builder's name ignoring case (RFC 5545 §3.1: component names are case-insensitive), matching the existing behaviour of `PropertyBuilder.hasName`.

#### Scenario: Lower-case name set, any-case query

- **WHEN** a `ComponentBuilder` is initialised with `name("standard")`
- **THEN** `hasName("STANDARD")`, `hasName("standard")` and `hasName("Standard")` all return `true`
- **AND** `hasName("DAYLIGHT")` returns `false`
