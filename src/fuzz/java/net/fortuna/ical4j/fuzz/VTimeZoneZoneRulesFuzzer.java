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
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.ZoneRulesBuilder;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 02 — ZoneRules built from a sender's VTIMEZONE (ZoneRulesBuilder through TimeZoneRegistryImpl#register, during the
 * parse). Modes: 0–1 a generated VTIMEZONE (0–4 observances), 2 a bundled zoneinfo definition renamed, 3 the same with
 * one line mutated. Registration succeeds or, under strict hints only, throws "Unable to build zone rules" (wrapped in
 * a ParserException). Differential: the offset of the registry's ZoneRules equals the TZOFFSETTO of the observance in
 * force (the computation of net.fortuna.ical4j.model.TimeZone) at 100 instants over 1900–2100 and around each onset;
 * in mode 2 it also equals the JDK's offset for the original zone. ZoneRulesBuilder#build on a well-formed VTIMEZONE
 * never throws (an IllegalArgumentException from ZoneOffsetTransitionRule.of under relaxed hints is a defect).
 */
public class VTimeZoneZoneRulesFuzzer {

    private static final String[] BUNDLED = {"Europe/Paris", "America/New_York", "Asia/Tokyo", "Australia/Lord_Howe",
            "Europe/Moscow", "America/Costa_Rica"};
    private static final Map<String, String> BUNDLED_TEXT = new HashMap<>();
    private static final DateTimeFormatter LOCAL = DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmss");
    private static final Instant NOW = Instant.now();
    private static long counter;
    private static boolean clampReported;
    private static boolean initialReported;

    public static void fuzzerInitialize() throws IOException {
        Ical.hints(false);
        for (String zone : BUNDLED) {
            try (InputStream in = VTimeZoneZoneRulesFuzzer.class.getClassLoader()
                    .getResourceAsStream("zoneinfo/" + zone + ".ics")) {
                String text = new String(in.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
                String block = text.substring(text.indexOf("BEGIN:VTIMEZONE"),
                        text.indexOf("END:VTIMEZONE") + "END:VTIMEZONE".length());
                BUNDLED_TEXT.put(zone, block);
            }
        }
    }

    private static String offset(FuzzedDataProvider d) {
        int seconds;
        switch (d.consumeInt(0, 4)) {
            case 0:
                seconds = d.consumeInt(-18 * 3600, 18 * 3600);
                break;
            case 1:
                seconds = d.pickValue(new int[]{0, 1800, 3600, 7200, -18000, -14400, 37800, 39600, 2079});
                break;
            default:
                seconds = d.consumeInt(-12, 14) * 3600 + d.pickValue(new int[]{0, 0, 0, 1800, 2700});
                break;
        }
        int abs = Math.abs(seconds);
        String s = String.format("%s%02d%02d", seconds < 0 ? "-" : "+", abs / 3600, abs % 3600 / 60);
        return abs % 60 != 0 || d.consumeInt(0, 7) == 0 ? s + String.format("%02d", abs % 60) : s;
    }

    private static LocalDateTime dateTime(FuzzedDataProvider d, String freq) {
        int year;
        // fine-grained frequencies are expanded from DTSTART to now during the parse: their DTSTART stays recent,
        // so that the run is not ended by that known, unbounded expansion (see README)
        switch (freq) {
            case "SECONDLY":
                return LocalDateTime.now().minusHours(d.consumeInt(0, 12));
            case "MINUTELY":
                return LocalDateTime.now().minusDays(d.consumeInt(0, 20));
            case "HOURLY":
                return LocalDateTime.now().minusDays(d.consumeInt(0, 700));
            default:
                break;
        }
        switch (d.consumeInt(0, 7)) {
            case 0:
                year = d.consumeInt(1, 9999);
                break;
            case 1:
                year = d.pickValue(new int[]{1601, 1883, 1900, 1916, 1970, 2007, 2038});
                break;
            default:
                year = d.consumeInt(1900, 2030);
                break;
        }
        return LocalDateTime.of(year, d.consumeInt(1, 12), d.consumeInt(1, 28), d.consumeInt(0, 23),
                d.pickValue(new int[]{0, 0, 30, 59}), d.consumeInt(0, 3) == 0 ? d.consumeInt(0, 59) : 0);
    }

    private static String until(FuzzedDataProvider d) {
        LocalDateTime t = d.consumeBoolean() ? LocalDateTime.now().minusYears(d.consumeInt(0, 80))
                : LocalDateTime.now().plusYears(d.consumeInt(0, 80));
        return t.format(LOCAL) + (d.consumeInt(0, 4) == 0 ? "" : "Z");
    }

    private static String observance(FuzzedDataProvider d) {
        String kind = d.consumeBoolean() ? "STANDARD" : "DAYLIGHT";
        StringBuilder b = new StringBuilder("BEGIN:").append(kind).append('\n');
        String freq = d.consumeInt(0, 5) == 0
                ? d.pickValue(new String[]{"SECONDLY", "MINUTELY", "HOURLY", "DAILY", "WEEKLY", "MONTHLY"})
                : "YEARLY";
        LocalDateTime start = dateTime(d, freq);
        switch (d.consumeInt(0, 9)) {
            case 0:
                b.append("DTSTART:").append(start.format(LOCAL)).append("Z\n");
                break;
            case 1:
                b.append("DTSTART;VALUE=DATE:").append(start.toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE))
                        .append('\n');
                break;
            case 2:
                break; // no DTSTART
            default:
                b.append("DTSTART:").append(start.format(LOCAL)).append('\n');
                break;
        }
        String from = offset(d);
        b.append("TZOFFSETFROM:").append(from).append('\n');
        b.append("TZOFFSETTO:").append(d.consumeInt(0, 5) == 0 ? from : offset(d)).append('\n');
        if (d.consumeInt(0, 3) != 0) {
            StringBuilder r = new StringBuilder("RRULE:FREQ=").append(freq);
            if (d.consumeInt(0, 6) != 0) {
                r.append(";BYMONTH=").append(d.consumeInt(0, 9) == 0
                        ? d.pickValue(new String[]{"", "13", "0", "2,11"}) : String.valueOf(d.consumeInt(1, 12)));
            }
            switch (d.consumeInt(0, 4)) {
                case 0:
                    r.append(";BYMONTHDAY=").append(d.consumeInt(-31, 31));
                    break;
                case 1:
                    break;
                default: {
                    int ordinal = d.consumeInt(0, 5) == 0 ? d.consumeInt(-53, 53) : d.consumeInt(-5, 5);
                    r.append(";BYDAY=").append(ordinal == 0 ? "" : String.valueOf(ordinal))
                            .append(d.pickValue(new String[]{"SU", "MO", "FR", "SA"}));
                    if (d.consumeInt(0, 4) == 0) {
                        r.append(";BYMONTHDAY=").append(d.consumeInt(-31, 31));
                    }
                    break;
                }
            }
            if (d.consumeInt(0, 3) == 0) {
                r.append(";UNTIL=").append(until(d));
            } else if (d.consumeInt(0, 5) == 0) {
                r.append(";COUNT=").append(d.consumeInt(1, 400));
            }
            b.append(r).append('\n');
        }
        int rdates = d.consumeInt(0, 3) == 0 ? d.consumeInt(1, 4) : 0;
        for (int i = 0; i < rdates; i++) {
            LocalDateTime t = start.plusYears(d.consumeInt(0, 100)).plusDays(d.consumeInt(0, 365));
            if (d.consumeBoolean()) {
                b.append("RDATE;VALUE=PERIOD:").append(t.format(LOCAL)).append('/')
                        .append(d.consumeBoolean() ? t.plusHours(1).format(LOCAL) : "PT1H").append('\n');
            } else {
                b.append("RDATE:").append(t.format(LOCAL)).append(d.consumeBoolean() ? "," + t.plusYears(1).format(LOCAL)
                        : "").append('\n');
            }
        }
        return b.append("END:").append(kind).append('\n').toString();
    }

    private static String mutate(FuzzedDataProvider d, String block) {
        List<String> lines = new ArrayList<>(Arrays.asList(block.split("\n")));
        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i);
            if (l.startsWith("TZOFFSET") || l.startsWith("DTSTART") || l.startsWith("RRULE") || l.startsWith("RDATE")) {
                candidates.add(i);
            }
        }
        int i = candidates.get(d.consumeInt(0, candidates.size() - 1));
        String line = lines.get(i);
        String name = line.substring(0, line.indexOf(':'));
        switch (d.consumeInt(0, 3)) {
            case 0:
                lines.remove(i);
                break;
            case 1:
                lines.add(i, line);
                break;
            default:
                String value = name.startsWith("TZOFFSET") ? offset(d)
                        : name.startsWith("RRULE") ? "FREQ=YEARLY;BYMONTH=" + d.consumeInt(1, 12) + ";BYDAY="
                        + d.consumeInt(-5, 5) + "SU" + (d.consumeBoolean() ? ";UNTIL=" + until(d) : "")
                        : dateTime(d, "YEARLY").format(LOCAL);
                lines.set(i, name + ":" + value);
                break;
        }
        return String.join("\n", lines);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) throws IOException {
        boolean relaxed = d.consumeBoolean();
        Ical.hints(relaxed);
        int mode = d.consumeInt(0, 3);
        String tzid = "X-Fuzz/" + (++counter);
        String original = null;
        String block;
        if (mode <= 1) {
            StringBuilder b = new StringBuilder("BEGIN:VTIMEZONE\nTZID:").append(tzid).append('\n');
            int n = d.consumeInt(0, 4);
            for (int i = 0; i < n; i++) {
                b.append(observance(d));
            }
            block = b.append("END:VTIMEZONE").toString();
        } else {
            original = d.pickValue(BUNDLED);
            block = BUNDLED_TEXT.get(original).replace("TZID:" + original, "TZID:" + tzid);
            if (mode == 3) {
                block = mutate(d, block);
            }
        }
        String ics = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//twake-fuzz//vtimezone//EN\n" + block + "\n"
                + "BEGIN:VEVENT\nUID:tz@twake-fuzz\nDTSTAMP:20240101T000000Z\nDTSTART;TZID=" + tzid
                + ":20240615T120000\nEND:VEVENT\nEND:VCALENDAR\n";
        Ical.debug("calendar", ics);

        CalendarBuilder builder = Ical.builder(false);
        Calendar calendar;
        try {
            calendar = builder.build(new java.io.StringReader(ics));
        } catch (ParserException e) {
            Throwable cause = e.getCause();
            if (cause != null && registration(cause)) {
                boolean documented = !relaxed && cause instanceof IllegalArgumentException
                        && String.valueOf(cause.getMessage()).startsWith("Unable to build zone rules");
                if (!documented) {
                    throw Ical.withoutJdkFrames("registering the VTIMEZONE fails" + (relaxed ? " under relaxed hints"
                            : ""), cause);
                }
            }
            return; // a malformed value rejected by its factory, or the documented strict rejection
        }
        if (calendar == null) {
            return;
        }
        Optional<VTimeZone> vtz = calendar.getComponent("VTIMEZONE");
        if (vtz.isEmpty()) {
            return;
        }
        VTimeZone vTimeZone = vtz.get();

        if (wellFormed(vTimeZone)) {
            try {
                new ZoneRulesBuilder().vTimeZone(vTimeZone).build();
            } catch (RuntimeException e) {
                // documented rejections: no observance in force now, a DTSTART not in local time; a defect: the
                // IllegalArgumentException of ZoneOffsetTransitionRule.of (BYMONTHDAY=-31...) and programming errors
                boolean transitionRule = Arrays.stream(e.getStackTrace())
                        .anyMatch(f -> f.getClassName().endsWith("ZoneOffsetTransitionRule"));
                boolean documented = !transitionRule && (String.valueOf(e.getMessage()).startsWith("VTIMEZONE")
                        || e instanceof ConstraintViolationException);
                if (!documented) {
                    throw Ical.withoutJdkFrames("ZoneRulesBuilder#build fails on a well-formed VTIMEZONE"
                            + (relaxed ? " (relaxed hints: the registry hides it)" : ""), e);
                }
            }
        }

        ZoneRules rules;
        try {
            rules = builder.getRegistry().getZoneId(tzid).getRules();
        } catch (DateTimeException e) {
            return; // no rules were built (no observance, or relaxed fallback): nothing to compare
        }

        List<Instant> probes = new ArrayList<>();
        Instant first = Instant.parse("1900-01-01T00:00:00Z");
        long step = (Instant.parse("2100-01-01T00:00:00Z").getEpochSecond() - first.getEpochSecond()) / 100;
        for (int i = 0; i <= 100; i++) {
            probes.add(first.plusSeconds(i * step + d.consumeInt(0, 86400)));
        }
        for (Observance o : vTimeZone.getObservances()) {
            Optional<DtStart<?>> start = o.getProperty(Property.DTSTART);
            Optional<TzOffsetFrom> from = o.getProperty(Property.TZOFFSETFROM);
            if (start.isPresent() && from.isPresent()) {
                try {
                    if (start.get().getDate() instanceof LocalDateTime) {
                        Instant onset = ((LocalDateTime) start.get().getDate()).toInstant(from.get().getOffset());
                        for (long delta : new long[]{-3600, -1, 0, 1, 3600, 86400 * 182L, 86400 * 365L}) {
                            probes.add(onset.plusSeconds(delta));
                        }
                    }
                } catch (DateTimeException e) {
                    // an unparsable DTSTART: no onset probe
                }
            }
        }

        TimeZone legacy = new TimeZone(vTimeZone);
        ZoneRules jdk = original == null || mode == 3 ? null : ZoneId.of(original).getRules();
        boolean everyRuleHasMonth = vTimeZone.getObservances().stream().allMatch(o -> o.getProperty(Property.RRULE)
                .map(r -> !((net.fortuna.ical4j.model.property.RRule<?>) r).getRecur().getMonthList().isEmpty())
                .orElse(true));
        // before the first explicit transition ZoneRules use their initial offsets (the current standard offset):
        // a defect of its own on every historical zone, reported once per run, then those instants are skipped
        Instant firstTransition = rules.getTransitions().isEmpty() ? null
                : rules.getTransitions().get(0).getInstant();
        for (Instant instant : probes) {
            if (firstTransition != null && instant.isBefore(firstTransition) && initialReported && !Ical.DEBUG) {
                continue;
            }
            ZoneOffset actual = rules.getOffset(instant);
            boolean initial = firstTransition != null && instant.isBefore(firstTransition);
            if (jdk != null && !actual.equals(jdk.getOffset(instant))) {
                if (initial) {
                    initialReported = true;
                    throw new AssertionError("before the first transition (" + firstTransition + ") the ZoneRules of"
                            + " the bundled " + original + " give " + actual + " at " + instant + ", the JDK "
                            + jdk.getOffset(instant));
                }
                throw new AssertionError("ZoneRules of the bundled " + original + " give " + actual + " at " + instant
                        + ", the JDK " + jdk.getOffset(instant));
            }
            Observance inForce;
            try {
                inForce = vTimeZone.getApplicableObservance(instant);
            } catch (DateTimeException e) {
                throw Ical.withoutJdkFrames("VTimeZone#getApplicableObservance fails at " + instant, e);
            }
            if (inForce == null) {
                continue; // before the first onset: no reference
            }
            ZoneOffset expected = ((TzOffsetTo) inForce.getRequiredProperty(Property.TZOFFSETTO)).getOffset();
            if (!actual.equals(expected)) {
                String message = "ZoneRules give " + actual + " at " + instant + " but the observance in force ("
                        + inForce.getName() + ", " + inForce.getProperty(Property.DTSTART).map(p -> ((Property) p)
                        .getValue()).orElse("?") + ") gives " + expected + (original != null ? " (" + original
                        + (mode == 3 ? ", mutated)" : ")") : "");
                if (initial) {
                    initialReported = true;
                    throw new AssertionError("before the first transition (" + firstTransition + "): " + message);
                }
                if (instant.isAfter(NOW)) {
                    if (everyRuleHasMonth) {
                        throw new AssertionError("future offset differs: " + message);
                    }
                    throw new AssertionError("future offset differs (an RRULE without BYMONTH): " + message);
                }
                throw new AssertionError("past offset differs: " + message);
            }
            int legacyOffset = legacy.getOffset(instant.toEpochMilli());
            if (legacyOffset != expected.getTotalSeconds() * 1000 && (!clampReported || Ical.DEBUG)) {
                clampReported = true;
                throw new AssertionError("net.fortuna.ical4j.model.TimeZone#getOffset(" + instant + ") = "
                        + legacyOffset / 1000 + " s, but the observance in force gives " + expected
                        + " (clamped to the raw offset " + legacy.getRawOffset() / 1000 + " s)");
            }
        }
    }

    /** The parse failed in the registration of the VTIMEZONE (not in a property factory). */
    private static boolean registration(Throwable cause) {
        for (StackTraceElement f : cause.getStackTrace()) {
            if (f.getClassName().endsWith("TimeZoneRegistryImpl") || f.getClassName().endsWith("ZoneRulesBuilder")
                    || f.getClassName().endsWith("ZoneRulesProviderImpl$ZoneIdPool")) {
                return true;
            }
        }
        return false;
    }

    /** Every observance has a local DTSTART, TZOFFSETFROM and TZOFFSETTO, all readable. */
    private static boolean wellFormed(VTimeZone vTimeZone) {
        if (vTimeZone.getObservances().isEmpty()) {
            return false;
        }
        for (Observance o : vTimeZone.getObservances()) {
            Optional<DtStart<?>> start = o.getProperty(Property.DTSTART);
            Optional<TzOffsetFrom> from = o.getProperty(Property.TZOFFSETFROM);
            Optional<TzOffsetTo> to = o.getProperty(Property.TZOFFSETTO);
            try {
                if (start.isEmpty() || !(start.get().getDate() instanceof LocalDateTime) || from.isEmpty()
                        || to.isEmpty()) {
                    return false;
                }
                from.get().getOffset();
                to.get().getOffset();
                o.getProperty(Property.RRULE).ifPresent(r -> ((net.fortuna.ical4j.model.property.RRule<?>) r)
                        .getRecur());
            } catch (DateTimeException | IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
}
