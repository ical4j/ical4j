## Why

Six long-standing, independently verified defects in validation, recurrence calculation and timezone mapping each have a one-to-ten line fix, but together they produce wrong validation results, a crash, and a wrong UTC offset for real-world calendars (#363, #736, #691, #692, #709, #82). Landing them as one change keeps review small and closes six open issues at once.

## What Changes

- **#363** `CalendarValidatorImpl.validate` no longer runs per-component iTIP validation itself. `Calendar.validate(recurse)` already performs that in `validateComponents()` when `recurse` is true, so today `validate(false)` still validates components and `validate(true)` reports every component-level iTIP error twice.
- **#692** `ComponentValidator` rule sets for VEVENT, VFREEBUSY and VAVAILABILITY no longer list UID and DTSTAMP under both `One` and `OneOrLess`; a duplicated UID/DTSTAMP now yields one entry instead of two.
- **#691** `ComponentBuilder.hasName` compares case-insensitively, matching `PropertyBuilder.hasName` and RFC 5545 §3.1. Today a lower-case `begin:standard` defeats the TZID-stripping guard in `DefaultContentHandler`.
- **#709** The Microsoft timezone maps (`msTimezones`, `msTimezoneNames`) map `Central America Standard Time` to `America/Guatemala` (no DST, the CLDR territory-001 mapping) instead of `US/Central`, which observes DST and shifted summer events by an hour.
- **#736** `RecurrenceSet.Builder.build()` no longer throws `UnsupportedTemporalTypeException` when the query period has DATE bounds and the event duration is a time-based `Duration`; the look-back window is widened by whole days instead.
- **#82** `Period.intersects` treats a zero-length period as intersecting another period when its instant lies in that period's half-open range `[start, end)`. A zero-duration event is now returned by `calculateRecurrenceSet` when the query period starts exactly at the event's start.

None of these change a public signature. #363 and #82 change observable results of existing public methods (fewer duplicate validation entries; one more period in a boundary case), which is the point of the fixes.

## Capabilities

### New Capabilities
- `recurrence-set-bounds`: how `RecurrenceSet.Builder`/`Component.calculateRecurrenceSet` treat the query period's bounds — DATE-valued bounds with time-based durations, and zero-length periods at the boundary (`Period.intersects`).
- `content-builder-names`: case-insensitivity of `ComponentBuilder.hasName`.
- `ms-timezone-mapping`: correctness constraints on the Microsoft → IANA timezone alias tables, with the Central America entry as the first scenario.

### Modified Capabilities
- `calendar-validation`: "Unsupported (component, method) pairs" and "Calendar-level validation SHALL merge per-component iTIP results" are re-scoped to the recursive `Calendar.validate()` path; a new requirement states that `CalendarValidatorImpl` is calendar-level only and that a duplicated required property is reported once.

## Impact

- `src/main/java/net/fortuna/ical4j/validate/CalendarValidatorImpl.java`, `ComponentValidator.java`
- `src/main/java/net/fortuna/ical4j/model/ComponentBuilder.java`, `Period.java`, `RecurrenceSet.java`
- `src/main/resources/net/fortuna/ical4j/transform/compliance/msTimezones`, `msTimezoneNames`
- Tests: `CalendarValidatorImplTest`, new `ComponentValidatorTest`, `ComponentBuilderTest`, `TzHelperTest`, `PeriodSpec`, `VEventSpec`
- Consumers calling `new CalendarValidatorImpl().validate(calendar)` directly and relying on it to surface component iTIP errors must call `Calendar.validate()` (recursive) instead. Consumers that de-duplicated validation entries themselves can stop.
- RevAPI: no API-shape change expected.
- Closes #363, #736, #691, #692, #709, #82.
