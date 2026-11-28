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
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.TemporalAdapter;
import net.fortuna.ical4j.model.TemporalAmountAdapter;
import net.fortuna.ical4j.model.TemporalComparator;
import net.fortuna.ical4j.model.UtcOffset;
import net.fortuna.ical4j.model.property.TzOffsetFrom;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.regex.Pattern;

/**
 * 16 — DURATION, PERIOD, UTC-OFFSET and DATE-TIME values. Parsing throws only DateTimeParseException,
 * ArithmeticException or IllegalArgumentException (a StringIndexOutOfBoundsException or NPE is a defect). An accepted
 * duration written with TemporalAmountAdapter#toString(seed), for seeds across DST transitions, is an RFC 5545
 * dur-value that re-parses into an amount of the same type that moves the seed to the same instant; periods, period
 * lists, offsets and date-times round-trip; 100 KB of input parses in under 1 s.
 */
public class DurationPeriodOffsetFuzzer {

    /** RFC 5545 §3.3.6 dur-value. */
    private static final Pattern DUR_VALUE = Pattern.compile(
            "[+-]?P(?:\\d+W|\\d+D(?:T(?:\\d+H(?:\\d+M(?:\\d+S)?)?|\\d+M(?:\\d+S)?|\\d+S))?"
                    + "|T(?:\\d+H(?:\\d+M(?:\\d+S)?)?|\\d+M(?:\\d+S)?|\\d+S))");

    private static final String[] DESIGNATORS = {"P", "T", "W", "D", "H", "M", "S", "Y", "-", "+", ".5", ",", "/",
            "Z", "60", "0000", "9999", "10000"};
    private static final String[] SAMPLES = {"P1W", "P1W2D", "PT36H", "P15DT5H0M20S", "-PT15M", "PT", "P", "+P1D",
            "P0D", "PT0S", "P2Y", "P1M", "PT1H30S", "PT0.5S", "-P-1D", "P9223372036854775807D", "PT99999999999H"};

    /** Seeds on, around and away from DST transitions (Paris, Sao Paulo, Lord Howe's 30 minutes, Apia's lost day). */
    private static final Temporal[] SEEDS = {
            LocalDateTime.of(2024, 1, 15, 9, 0),
            ZonedDateTime.of(2024, 3, 30, 3, 0, 0, 0, ZoneId.of("Europe/Paris")),
            ZonedDateTime.of(2024, 3, 31, 1, 30, 0, 0, ZoneId.of("Europe/Paris")),
            ZonedDateTime.of(2024, 10, 26, 23, 0, 0, 0, ZoneId.of("Europe/Paris")),
            ZonedDateTime.of(2018, 11, 3, 23, 30, 0, 0, ZoneId.of("America/Sao_Paulo")),
            ZonedDateTime.of(2024, 4, 6, 12, 0, 0, 0, ZoneId.of("Australia/Lord_Howe")),
            ZonedDateTime.of(2011, 12, 29, 12, 0, 0, 0, ZoneId.of("Pacific/Apia")),
            ZonedDateTime.of(2024, 6, 1, 12, 0, 0, 0, ZoneId.of("UTC")),
            LocalDate.of(2024, 2, 28)};

    private static long executions;

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    private static String value(FuzzedDataProvider d) {
        switch (d.consumeInt(0, 3)) {
            case 0:
                return d.pickValue(SAMPLES);
            case 1:
                return d.consumeString(40);
            default: {
                StringBuilder b = new StringBuilder();
                if (d.consumeInt(0, 3) == 0) {
                    b.append(d.consumeBoolean() ? '-' : '+');
                }
                b.append('P');
                int parts = d.consumeInt(0, 6);
                for (int i = 0; i < parts; i++) {
                    if (d.consumeBoolean()) {
                        b.append(d.consumeBoolean() ? d.consumeInt(0, 100) : d.consumeLong());
                    }
                    b.append(d.pickValue(DESIGNATORS));
                }
                return b.toString();
            }
        }
    }

    private static boolean sameInstant(Temporal a, Temporal b) {
        return TemporalComparator.INSTANCE.compare(a, b) == 0 && a.getClass() == b.getClass();
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) {
        boolean lenient = d.consumeBoolean();
        Ical.hints(d.consumeBoolean());
        int selector = d.consumeInt(0, 4);
        Temporal seed = d.pickValue(SEEDS);
        String v = value(d);
        Ical.debug("value", v + " (selector " + selector + ", lenient " + lenient + ", seed " + seed + ")");
        switch (selector) {
            case 0:
                duration(v, lenient, seed);
                break;
            case 1:
                period(v.contains("/") || d.consumeBoolean() ? v : "20240101T090000Z/" + v);
                break;
            case 2:
                periodList(v);
                break;
            case 3:
                offset(d.consumeBoolean() ? v : d.pickValue(new String[]{"+", "-"}) + d.consumeString(8));
                break;
            case 4:
                dateTime(d.consumeBoolean() ? v : d.pickValue(new String[]{"20240101", "20240101T090000",
                        "20240101T090000Z", "00000101T000000", "99991231T235960", "100000101", "20240230T250000",
                        "20240101T090000+0100"}) + d.consumeString(4));
                break;
            default:
                break;
        }
        // the linearity check (4 values of 100 KB) is expensive: every 256th execution, and always when reproducing,
        // after the case of the input
        if (++executions % 256 == 0 || Ical.DEBUG) {
            linear(v);
        }
    }

    private static void duration(String v, boolean lenient, Temporal seed) {
        TemporalAmountAdapter parsed;
        try {
            parsed = TemporalAmountAdapter.parse(v, lenient);
        } catch (DateTimeParseException | ArithmeticException | IllegalArgumentException e) {
            return;
        }
        TemporalAmount amount = parsed.getDuration();
        if (amount instanceof java.time.Duration && !(seed instanceof LocalDateTime || seed instanceof ZonedDateTime)) {
            seed = SEEDS[1];
        }
        Temporal expected;
        try {
            expected = seed.plus(amount);
        } catch (DateTimeException | ArithmeticException e) {
            return; // an amount beyond the range of java.time
        }
        String out;
        try {
            out = parsed.toString(seed);
        } catch (DateTimeException | ArithmeticException e) {
            return;
        }
        if (!DUR_VALUE.matcher(out).matches()) {
            throw new AssertionError("duration [" + v + "] (" + amount + ") is written [" + out + "] from seed " + seed
                    + ": not an RFC 5545 dur-value");
        }
        TemporalAmountAdapter back;
        try {
            back = TemporalAmountAdapter.parse(out, false);
        } catch (DateTimeParseException | ArithmeticException | IllegalArgumentException e) {
            throw new AssertionError("duration [" + v + "] is written [" + out + "], which does not re-parse", e);
        }
        if (back.getDuration().getClass() != amount.getClass()) {
            throw new AssertionError("duration [" + v + "] (" + amount.getClass().getSimpleName() + ") is written ["
                    + out + "], which re-parses as a " + back.getDuration().getClass().getSimpleName());
        }
        Temporal actual = seed.plus(back.getDuration());
        if (!sameInstant(expected, actual)) {
            throw new AssertionError("duration [" + v + "] (" + amount + ") written [" + out + "] from seed " + seed
                    + " moves it to " + actual + " instead of " + expected);
        }
    }

    private static void period(String v) {
        Period<Temporal> p;
        try {
            p = Period.parse(v);
        } catch (DateTimeParseException | ArithmeticException | IllegalArgumentException e) {
            return;
        }
        String out = p.toString();
        Period<Temporal> back;
        try {
            back = Period.parse(out);
        } catch (DateTimeParseException | ArithmeticException | IllegalArgumentException e) {
            throw new AssertionError("period [" + v + "] is written [" + out + "], which does not re-parse", e);
        }
        if (!sameInstant(p.getStart(), back.getStart()) || !sameInstant(p.getEnd(), back.getEnd())) {
            throw new AssertionError("period [" + v + "] (" + p.getStart() + " to " + p.getEnd() + ") is written ["
                    + out + "], which re-parses as " + back.getStart() + " to " + back.getEnd());
        }
    }

    private static void periodList(String v) {
        PeriodList<Temporal> list;
        try {
            list = PeriodList.parse(v);
        } catch (DateTimeParseException | ArithmeticException | IllegalArgumentException e) {
            return;
        }
        String out = list.toString();
        PeriodList<Temporal> back;
        try {
            back = PeriodList.parse(out);
        } catch (DateTimeParseException | ArithmeticException | IllegalArgumentException e) {
            throw new AssertionError("period list [" + v + "] is written [" + out + "], which does not re-parse", e);
        }
        if (back.getPeriods().size() != list.getPeriods().size()) {
            throw new AssertionError("period list [" + v + "] is written [" + out + "], which re-parses into "
                    + back.getPeriods().size() + " periods instead of " + list.getPeriods().size());
        }
    }

    @SuppressWarnings("deprecation")
    private static void offset(String v) {
        try {
            UtcOffset o = new UtcOffset(v);
            UtcOffset back = new UtcOffset(o.toString());
            if (!back.equals(o)) {
                throw new AssertionError("UTC offset [" + v + "] is written [" + o + "], which re-parses as " + back);
            }
        } catch (IllegalArgumentException | DateTimeException e) {
            // documented
        }
        try {
            TzOffsetFrom p = new TzOffsetFrom(v);
            TzOffsetFrom back = new TzOffsetFrom(p.getValue());
            if (!back.getOffset().equals(p.getOffset())) {
                throw new AssertionError("TZOFFSETFROM [" + v + "] is written [" + p.getValue() + "], which re-parses as "
                        + back.getOffset());
            }
        } catch (IllegalArgumentException | DateTimeException e) {
            // documented
        }
    }

    private static void dateTime(String v) {
        TemporalAdapter<Temporal> t;
        try {
            t = TemporalAdapter.parse(v);
        } catch (DateTimeParseException | ArithmeticException | IllegalArgumentException e) {
            return;
        }
        String out = t.toString();
        TemporalAdapter<Temporal> back;
        try {
            back = TemporalAdapter.parse(out);
        } catch (DateTimeParseException e) {
            throw new AssertionError("date-time [" + v + "] is written [" + out + "], which does not re-parse", e);
        }
        if (!sameInstant(t.getTemporal(), back.getTemporal())) {
            throw new AssertionError("date-time [" + v + "] (" + t.getTemporal() + ") is written [" + out
                    + "], which re-parses as " + back.getTemporal());
        }
        try {
            TemporalAdapter.parse(v, ZoneId.of("Europe/Paris")).getTemporal();
        } catch (DateTimeParseException e) {
            // documented
        }
    }

    private static void linear(String unit) {
        if (unit.isEmpty()) {
            return;
        }
        StringBuilder b = new StringBuilder(100_000);
        while (b.length() < 100_000) {
            b.append(unit);
        }
        String big = b.toString();
        long start = System.nanoTime();
        for (String value : new String[]{big, "P" + big, big + "D", "P" + big + "W1D"}) {
            for (boolean lenient : new boolean[]{false, true}) {
                try {
                    TemporalAmountAdapter.parse(value, lenient);
                } catch (DateTimeParseException | ArithmeticException | IllegalArgumentException e) {
                    // documented
                }
            }
            try {
                Period.parse("20240101T000000Z/" + value);
            } catch (DateTimeParseException | ArithmeticException | IllegalArgumentException e) {
                // documented
            }
            try {
                PeriodList.parse(value);
            } catch (DateTimeParseException | ArithmeticException | IllegalArgumentException e) {
                // documented
            }
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        if (ms > 4000) {
            throw new AssertionError("superlinear parsing: 4 values of 100 KB took " + ms + " ms (unit [" + unit + "])");
        }
    }
}
