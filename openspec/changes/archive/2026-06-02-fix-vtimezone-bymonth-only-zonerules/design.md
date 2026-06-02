## Context

`ZoneRulesBuilder.build()` converts a `VTimeZone` into a JSR-310 `java.time.zone.ZoneRules`. Future DST transitions are built by `buildTransitionRules(List<Observance>, ZoneOffset)`, which translates each observance's `RRULE` into a `ZoneOffsetTransitionRule`.

The current guard (`ZoneRulesBuilder.java:132`) only checks that the recur month list is non-empty, then unconditionally reads `getRecur().getDayList().get(0)` at line 134 and again at line 138:

```java
if (rrule.isPresent() && !rrule.get().getRecur().getMonthList().isEmpty()) {
    var recurMonth = Month.of(rrule.get().getRecur().getMonthList().get(0).getMonthOfYear());
    int dayOfMonth   = rrule.get().getRecur().getDayList().get(0).getOffset();   // line 134
    if (dayOfMonth == 0) {
        dayOfMonth   = rrule.get().getRecur().getMonthDayList().get(0);
    }
    var dayOfWeek    = WeekDay.getDayOfWeek(rrule.get().getRecur().getDayList().get(0)); // line 138
    ...
}
```

When the `RRULE` is `FREQ=YEARLY;BYMONTH=1` (no `BYDAY`, no `BYMONTHDAY`) the day list is empty, so `get(0)` throws `IndexOutOfBoundsException`. This propagates up through `TimeZoneRegistryImpl.register` → `DefaultContentHandler.endComponent` → `CalendarBuilder.build`, surfacing as a `ParserException` at the `END:VTIMEZONE` line. The offending shape is exactly what Apple Calendar emits for non-DST zones (e.g. `Asia/Tokyo`: single `STANDARD`, `TZOFFSETFROM == TZOFFSETTO`, yearly `BYMONTH`-only rule).

`buildDSTTransitions` (line 89) already skips observances where from-offset equals to-offset; `buildTransitionRules` lacks that guard.

## Goals / Non-Goals

**Goals:**
- Parse calendars containing non-DST / fixed-date `VTIMEZONE` definitions that use a `BYMONTH`-only `RRULE`.
- Produce a correct fixed-date transition rule when no weekday is specified, deriving the day from `DTSTART`.
- Skip observances that describe no offset change, consistent with `buildDSTTransitions`.
- Preserve existing behavior for `BYDAY`-based rules.

**Non-Goals:**
- No general RRULE-to-ZoneRules rewrite; only the missing-day-list and no-effect cases are addressed.
- No change to historical transition handling (`buildStandardOffsetTransitions`, `buildDSTTransitions`) beyond what already exists.
- No public API signature changes.

## Decisions

**Decision: Derive day-of-month from `DTSTART` with a null day-of-week when day list is empty (Option A).**

`java.time.zone.ZoneOffsetTransitionRule.of(month, dayOfMonthIndicator, dayOfWeek, time, ...)` accepts a `null` `dayOfWeek`, which means "the transition is always on day-of-month `dayOfMonthIndicator`" — a fixed-date rule. This is the precise semantics of a `BYMONTH`-only yearly rule whose day is pinned by `DTSTART`. The revised branch:

```java
if (rrule.isPresent() && !rrule.get().getRecur().getMonthList().isEmpty()
        && !offsetFrom.getOffset().equals(offsetTo.getOffset())) {        // no-effect guard
    var recurMonth = Month.of(rrule.get().getRecur().getMonthList().get(0).getMonthOfYear());
    var dayList    = rrule.get().getRecur().getDayList();
    int dayOfMonth;
    DayOfWeek dayOfWeek;
    if (!dayList.isEmpty()) {
        dayOfMonth = dayList.get(0).getOffset();
        if (dayOfMonth == 0) {
            dayOfMonth = rrule.get().getRecur().getMonthDayList().isEmpty()
                ? startDate.getDate().getDayOfMonth()
                : rrule.get().getRecur().getMonthDayList().get(0);
        }
        dayOfWeek = WeekDay.getDayOfWeek(dayList.get(0));
    } else if (!rrule.get().getRecur().getMonthDayList().isEmpty()) {
        dayOfMonth = rrule.get().getRecur().getMonthDayList().get(0);
        dayOfWeek  = null;                                                // fixed date
    } else {
        dayOfMonth = startDate.getDate().getDayOfMonth();                 // derive from DTSTART
        dayOfWeek  = null;                                                // fixed date
    }
    ...
    transitionRules.add(ZoneOffsetTransitionRule.of(recurMonth, dayOfMonth, dayOfWeek, time, ...));
}
```

*Alternative considered — Option B (only skip no-effect rules):* trivial, fixes the Apple `Asia/Tokyo` case because its from/to offsets are equal, but a `BYMONTH`-only rule with a *real* offset change would still crash. Rejected as incomplete; the no-effect guard is still adopted as a complementary safeguard.

**Decision: Also adopt the no-effect guard (`!offsetFrom.equals(offsetTo)`).** Mirrors `buildDSTTransitions` and avoids emitting meaningless rules. For the Apple `Asia/Tokyo` case this alone short-circuits the branch, but the day-derivation logic is what makes the fix general.

**Decision: `MONTHDAY` fallback when day list present but ordinal is 0.** Preserve the existing `dayOfMonth == 0 → monthDayList.get(0)` path, but guard it so an empty `monthDayList` falls back to the `DTSTART` day instead of throwing.

## Risks / Trade-offs

- [A `null` day-of-week changes the generated rule type for affected zones] → For previously-crashing inputs there is no prior behavior to regress; for inputs that reached `ZoneRules.of`, from-offset already equalled to-offset so the rule had no observable effect. Add a regression test asserting the Apple `Asia/Tokyo` calendar parses and resolves the event's zone.
- [Day derived from `DTSTART` could differ from author intent if `DTSTART` is unusual] → `DTSTART` is the normative anchor of an observance per RFC 5545; deriving from it is the correct source of truth.
- [Ordinal-weekday handling regressions] → Covered by the "existing BYDAY-based rules unchanged" requirement and a regression test on a standard DST zone (e.g. `BYMONTH=3;BYDAY=-1SU`).

## Migration Plan

Internal bug fix; no data migration. Rollback is reverting the `ZoneRulesBuilder` change. Previously-failing parses begin to succeed — no consumer needs to change.

## Open Questions

- None blocking. (If a `BYMONTH`-only rule ever pairs with a genuine offset change in the wild, the fixed-date rule from `DTSTART` is the intended result; no further handling anticipated.)
