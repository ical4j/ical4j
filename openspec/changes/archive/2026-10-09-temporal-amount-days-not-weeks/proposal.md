## Why

`TemporalAmountAdapter.toString(seed)` serialises a `java.time.Period` containing years or months as a whole number of weeks, discarding the remainder: one month from 1 June 2020 (30 days) becomes `P4W` (28 days) and six months from 1 April 2021 (183 days) becomes `P26W` (182 days). RFC 5545 §3.3.6 allows a duration to be expressed in weeks *or* in days (plus time), never a mix, so the only lossless representation for such spans is days. Issue #419; the two tests written for it are still `@Ignore`d.

## What Changes

- `periodToString` measures the span between the seed and `seed.plus(period)` in calendar days and emits `PnW` only when that is an exact multiple of seven days, otherwise `PnD`. Years and months are no longer special-cased; the sign handling is unchanged.
- **Behaviour change**: `DURATION` values written from a `Period` of years or months whose calendar span is not a whole number of weeks change from a truncated `PnW` to the exact `PnD`. `P7D`-style inputs still serialise as `P1W`; `Duration` serialisation is untouched.
- The two `@Ignore`d #419 tests in `TemporalAmountAdapterTest` are enabled with fixed seeds, and the two rows that encoded the truncation (`P52W` for one year, `P26W` for six months) are corrected to `P365D` and `P183D`. The `-P26W` row for six months back from 1 April is correct (182 days) and stays.

## Capabilities

### New Capabilities
- `temporal-amount-serialisation`: how `TemporalAmountAdapter` renders a `java.time.Period` or `Duration` as an RFC 5545 `dur-value` relative to a seed.

### Modified Capabilities
<!-- none -->

## Impact

- `src/main/java/net/fortuna/ical4j/model/TemporalAmountAdapter.java` — `periodToString` only; no signature changes.
- `src/test/groovy/net/fortuna/ical4j/model/TemporalAmountAdapterTest.groovy` — corrected rows, enabled tests, new rows.
- Consumers writing `DURATION:P1M`-style periods via ical4j get a day count instead of a short week count; any parser reading it back gets the correct length.
- No dependency or API-shape changes; RevAPI not expected to report.
- Fixes #419.
