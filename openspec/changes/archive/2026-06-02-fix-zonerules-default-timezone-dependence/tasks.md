## 1. Implement the fix

- [x] 1.1 In `ZoneRulesBuilder.buildDSTTransitions`, replace the floating `LocalDateTime` recurrence seed with offset-aware temporals: build the `Period` from `startDate.atOffset(offsetFrom.getOffset())` and `periodEnd.atOffset(offsetFrom.getOffset())`
- [x] 1.2 Confirm the recurrence-set consumer still derives the correct local onset (`LocalDateTime.from(p.getStart())`) and that `ZoneOffsetTransition.of(...)` arguments are otherwise unchanged
- [x] 1.3 Add a brief comment explaining the offset-aware seed prevents the JVM-default-zone-dependent `UNTIL` comparison (reference `getLatestOnset` as the precedent)

## 2. Tests

- [x] 2.1 Add a dedicated regression spec (`ZoneRulesTimezoneIndependenceTest`) asserting historical-DST zones (`Asia/Tokyo` +09:00, `Asia/Shanghai` +08:00) resolve to their standard offset in winter and summer. NOTE: an in-test `TimeZone.setDefault` has no effect because `TemporalComparator.INSTANCE` captures the default zone once at class-load — the zone must be set at JVM startup, so a dedicated Gradle test task forces it
- [x] 2.2 Add a Gradle `timezoneIndependenceTest` task that runs the spec with `-Duser.timezone=Australia/Sydney` and wire it into `check`; exclude the spec from the default `test` task (which would run it under the host zone with no signal on UTC CI)
- [x] 2.3 Confirm the existing `'test build rules'` rows (including the `Asia/Tokyo` and `Asia/Shanghai` expectations) pass

## 3. Verify

- [x] 3.1 Run `ZoneRulesBuilderTest` under the host (non-UTC) default zone and confirm all rows pass — this is the configuration that previously failed
- [x] 3.2 Run the full timezone-related suite (`TimeZoneTest`, `CalendarBuilderTimezoneTest`, `VTimezoneTest`, `TimeZoneRegistryTest`, etc.) and confirm no regressions
- [x] 3.3 Run the full test suite and confirm green
