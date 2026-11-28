/**
 * Copyright (c) 2026, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.fortuna.ical4j.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import net.fortuna.ical4j.model.Month;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TemporalComparator;
import net.fortuna.ical4j.model.WeekDay;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.zone.ZoneOffsetTransition;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 04 — Recur#getDates / #getNextDate on sender-chosen rules, seeds and windows. Parsing throws only
 * IllegalArgumentException / DateTimeParseException; generation only a DateTimeException (an out-of-range year), never
 * an UnsupportedTemporalTypeException, and returns within the time limit. The returned list is sorted, distinct,
 * >= seed, inside [start, end], <= UNTIL, at most COUNT and maxCount dates, of the seed's type and matching
 * BYMONTH/BYDAY/BYHOUR/BYMINUTE/BYSECOND; getNextDate(seed, d) is the first date of getDates(seed, d + 1, far, 1).
 */
public class RecurDateGenerationFuzzer {

    private static final TemporalComparator CMP = TemporalComparator.INSTANCE;

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    private static void fail(String what, String rule, Temporal seed, Object detail) {
        throw new AssertionError(what + " — rule [" + rule + "], seed " + seed + " (" + seed.getClass().getSimpleName()
                + "): " + detail);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) {
        Ical.hints(d.consumeBoolean());
        int type = d.consumeInt(0, 3);
        ZoneId zone = ZoneId.of(d.pickValue(Rules.ZONES));
        LocalDateTime seedTime = Rules.dateTime(d);
        Temporal seed = Rules.of(seedTime, type, zone);

        LocalDateTime startTime;
        switch (d.consumeInt(0, 3)) {
            case 0:
                startTime = Rules.dateTime(d);
                break;
            case 1:
                startTime = seedTime.minusDays(d.consumeInt(0, 400));
                break;
            default:
                startTime = seedTime.plusDays(d.consumeInt(0, 3000)).plusSeconds(d.consumeInt(-86400, 86400));
                break;
        }
        LocalDateTime endTime = startTime.plusDays(d.consumeInt(0, 800)).plusSeconds(d.consumeInt(0, 86400));
        // the window: mostly of the seed's type, sometimes of another one
        int windowType = d.consumeInt(0, 4) == 0 ? d.consumeInt(0, 3) : type;
        Temporal periodStart = Rules.of(startTime, windowType, zone);
        Temporal periodEnd = Rules.of(endTime, windowType, zone);
        int maxCount = d.pickValue(new int[]{-1, -1, 0, 1, 2, 10, 100, 1000});
        boolean useStream = d.consumeBoolean();
        String rule = Rules.rule(d);

        Ical.debug("rule", rule);
        Ical.debug("seed", seed);
        Ical.debug("window", periodStart + " .. " + periodEnd + ", maxCount " + maxCount + ", stream " + useStream);
        Recur<Temporal> recur;
        try {
            recur = new Recur<>(rule);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return;
        }
        // The window is kept to about 20000 possible dates (periods × the BY* expansions). A legitimately huge answer
        // (FREQ=SECONDLY over two years) is not a defect; a sparse fine-grained rule over a long window is one
        // (FREQ=SECONDLY;BYWEEKNO=13 over two years walks every second, maxCount or not: found by this harness), but
        // it would end every run within seconds, so the window is bounded for every input.
        boolean bounded = (recur.getCount() > 0 && recur.getCount() <= 5000) || (maxCount > 0 && maxCount <= 5000);
        {
            long periods = Math.max(1, 20000 / expansion(recur));
            long span = periods * unitSeconds(recur) * Math.max(recur.getInterval(), 1);
            LocalDateTime cap = startTime.plusSeconds(Math.min(span, 3_000_000_000L));
            if (endTime.isAfter(cap)) {
                endTime = cap;
                periodEnd = Rules.of(endTime, windowType, zone);
            }
        }

        // Known unbounded walks, one interval at a time from the seed (found by this harness: a timeout in
        // Recur#getNextDate's `count == null` loop, and the COUNT path of RecurDateSpliterator that records every
        // candidate before the window in invalidCandidates). They are kept reachable up to WALK_LIMIT intervals, so
        // that fuzzing goes on past them; a slow call below that limit is still a finding.
        if (walk(recur, seedTime, startTime, true) > WALK_LIMIT) {
            return;
        }
        // an unbounded result is pulled lazily, at most STREAM_LIMIT dates: a timeout then means the generator is slow
        // to produce few dates, not that the answer is big
        if (!bounded) {
            useStream = true;
        }
        List<Temporal> dates;
        try {
            dates = useStream ? recur.getDatesAsStream(seed, periodStart, periodEnd, maxCount).limit(STREAM_LIMIT)
                    .collect(java.util.stream.Collectors.toList())
                    : recur.getDates(seed, periodStart, periodEnd, maxCount);
        } catch (UnsupportedTemporalTypeException e) {
            throw e;
        } catch (DateTimeException | IllegalArgumentException e) {
            // an out-of-range year, or "Invalid temporal type for this rule" (BYHOUR on a DATE seed)
            return;
        }

        Set<Temporal> seen = new HashSet<>();
        Temporal previous = null;
        Temporal until = recur.getUntil();
        for (Temporal date : dates) {
            if (date.getClass() != seed.getClass()) {
                fail("date of another type than the seed", rule, seed, date + " " + date.getClass().getSimpleName());
            }
            if (!seen.add(date)) {
                fail("duplicate date", rule, seed, date);
            }
            if (previous != null && CMP.compare(previous, date) >= 0 && !useStream) {
                fail("dates not sorted", rule, seed, previous + " then " + date);
            }
            previous = date;
            if (CMP.compare(date, seed) < 0) {
                fail("date before the seed", rule, seed, date);
            }
            if (CMP.compare(date, periodStart) < 0 || CMP.compare(date, periodEnd) > 0) {
                fail("date outside [periodStart, periodEnd]", rule, seed,
                        date + " not in [" + periodStart + ", " + periodEnd + "]");
            }
            if (until != null && CMP.compare(date, until) > 0) {
                fail("date after UNTIL", rule, seed, date + " > " + until);
            }
            checkFields(recur, rule, seed, date);
        }
        if (recur.getCount() > 0 && dates.size() > recur.getCount()) {
            fail("more dates than COUNT", rule, seed, dates.size() + " > " + recur.getCount());
        }
        if (maxCount > 0 && dates.size() > maxCount) {
            fail("more dates than maxCount", rule, seed, dates.size() + " > " + maxCount);
        }

        // getNextDate(seed, from) == first of getDates(seed, from + 1, far future, 1)
        Temporal from = !dates.isEmpty() && d.consumeBoolean() ? dates.get(d.consumeInt(0, dates.size() - 1))
                : Rules.of(startTime, type, zone);
        if (walk(recur, seedTime, local(from), false) > WALK_LIMIT) {
            return;
        }
        Temporal next;
        List<Temporal> expected;
        try {
            next = recur.getNextDate(seed, from);
            Temporal after = from.plus(1, type == 0 ? ChronoUnit.DAYS : ChronoUnit.SECONDS);
            Temporal base = CMP.compare(seed, from) > 0 ? seed : from;
            // as far as java.time goes: getNextDate has no horizon (an INTERVAL of 2^31 minutes is 4000 years)
            Temporal far = base.plus(999_999_000L - local(base).getYear(), ChronoUnit.YEARS);
            expected = recur.getDates(seed, after, far, 1);
        } catch (UnsupportedTemporalTypeException e) {
            throw e;
        } catch (DateTimeException | IllegalArgumentException e) {
            return;
        }
        Temporal first = expected.isEmpty() ? null : expected.get(0);
        if (first == null ? next != null : !first.equals(next)) {
            fail("getNextDate differs from getDates", rule, seed, "getNextDate(" + from + ") = " + next
                    + ", first of getDates after it = " + first);
        }
    }

    private static final long WALK_LIMIT = 200_000L;

    private static final int STREAM_LIMIT = 50_000;

    /**
     * The candidates the generator walks one by one from the seed to `to`: getNextDate always does without COUNT
     * (one interval at a time); with COUNT, both getNextDate and the spliterator go through every candidate (the
     * intervals times their BY* expansion) until COUNT of them.
     */
    private static long walk(Recur<?> recur, LocalDateTime seed, LocalDateTime to, boolean spliterator) {
        long intervals = Math.abs(ChronoUnit.SECONDS.between(seed, to))
                / (unitSeconds(recur) * Math.max(recur.getInterval(), 1));
        if (recur.getCount() > 0) {
            long candidates = intervals > Long.MAX_VALUE / 400_000 ? Long.MAX_VALUE : intervals * expansion(recur);
            return Math.min(candidates, recur.getCount());
        }
        return spliterator ? 0 : intervals;
    }

    private static LocalDateTime local(Temporal t) {
        if (t instanceof LocalDate) {
            return ((LocalDate) t).atStartOfDay();
        } else if (t instanceof LocalDateTime) {
            return (LocalDateTime) t;
        } else if (t instanceof ZonedDateTime) {
            return ((ZonedDateTime) t).toLocalDateTime();
        }
        return ((OffsetDateTime) t).toLocalDateTime();
    }

    /** An upper bound of the dates one period of the rule may expand to. */
    private static long expansion(Recur<?> recur) {
        long factor = 1;
        int freq = recur.getFrequency().ordinal(); // SECONDLY 0 … YEARLY 6
        if (freq > 0 && !recur.getSecondList().isEmpty()) {
            factor *= recur.getSecondList().size();
        }
        if (freq > 1 && !recur.getMinuteList().isEmpty()) {
            factor *= recur.getMinuteList().size();
        }
        if (freq > 2 && !recur.getHourList().isEmpty()) {
            factor *= recur.getHourList().size();
        }
        boolean dayParts = !recur.getDayList().isEmpty() || !recur.getMonthDayList().isEmpty()
                || !recur.getYearDayList().isEmpty() || !recur.getWeekNoList().isEmpty()
                || !recur.getMonthList().isEmpty();
        if (dayParts) {
            factor *= freq == 6 ? 366 : freq == 5 ? 31 : freq == 4 ? 7 : 1;
        }
        return factor;
    }

    private static long unitSeconds(Recur<?> recur) {
        switch (recur.getFrequency()) {
            case SECONDLY:
                return 1;
            case MINUTELY:
                return 60;
            case HOURLY:
                return 3600;
            case DAILY:
                return 86400;
            case WEEKLY:
                return 7 * 86400;
            case MONTHLY:
                return 28 * 86400;
            default:
                return 365 * 86400;
        }
    }

    private static boolean nearTransition(ZonedDateTime z) {
        Instant i = z.toInstant();
        ZoneOffsetTransition t = z.getZone().getRules().nextTransition(i.minus(3, ChronoUnit.DAYS));
        return t != null && t.getInstant().isBefore(i.plus(3, ChronoUnit.DAYS));
    }

    /** BY* parts checked on the date's own fields (skipped for RSCALE rules, and near a DST transition). */
    private static void checkFields(Recur<Temporal> recur, String rule, Temporal seed, Temporal date) {
        if (rule.toUpperCase(java.util.Locale.ROOT).contains("RSCALE")) {
            return;
        }
        LocalDate day;
        LocalDateTime local = null;
        if (date instanceof LocalDate) {
            day = (LocalDate) date;
        } else if (date instanceof LocalDateTime) {
            local = (LocalDateTime) date;
            day = local.toLocalDate();
        } else if (date instanceof ZonedDateTime) {
            if (nearTransition((ZonedDateTime) date)) {
                return;
            }
            local = ((ZonedDateTime) date).toLocalDateTime();
            day = local.toLocalDate();
        } else if (date instanceof OffsetDateTime) {
            local = ((OffsetDateTime) date).toLocalDateTime();
            day = local.toLocalDate();
        } else {
            return;
        }
        List<Month> months = recur.getMonthList();
        if (!months.isEmpty() && months.stream().noneMatch(m -> !m.isLeapMonth()
                && m.getMonthOfYear() == day.getMonthValue())) {
            fail("date outside BYMONTH", rule, seed, date);
        }
        List<WeekDay> days = recur.getDayList();
        if (!days.isEmpty()) {
            DayOfWeek dow = day.getDayOfWeek();
            if (days.stream().noneMatch(w -> WeekDay.getDayOfWeek(w) == dow)) {
                fail("date outside BYDAY", rule, seed, date + " is a " + dow);
            }
        }
        if (local != null) {
            int h = local.getHour();
            int m = local.getMinute();
            int s = local.getSecond();
            if (!recur.getHourList().isEmpty() && !recur.getHourList().contains(h)) {
                fail("date outside BYHOUR", rule, seed, date);
            }
            if (!recur.getMinuteList().isEmpty() && !recur.getMinuteList().contains(m)) {
                fail("date outside BYMINUTE", rule, seed, date);
            }
            if (!recur.getSecondList().isEmpty() && !recur.getSecondList().contains(s)) {
                fail("date outside BYSECOND", rule, seed, date);
            }
        }
    }
}
