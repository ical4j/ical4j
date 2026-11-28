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
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.ZoneRulesProviderImpl;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.TzOffsetTo;

import java.io.IOException;
import java.io.StringReader;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 07 — the JVM-wide zone-id pool (ZoneRulesProviderImpl.ZoneIdPool, 1500 ids) and duplicate TZID definitions, driven
 * by a sequence of 1–20 calendars, each parsed by its own CalendarBuilder, kept or dropped. "Ran out of available zone
 * IDs" never occurs once dropped builders are collected while fewer than 1500 VTIMEZONEs are referenced, and a single
 * calendar cannot exhaust the pool; every DTSTART;TZID resolves with the rules of the definition getTimeZone(id)
 * returns, or for an IANA id it does not define, with the zone's own rules (an override in another calendar never
 * leaks); a date-time of a collected registry never gets an NPE or another registry's rules.
 */
public class TimeZoneRegistryStateFuzzer {

    private static final String[] TZIDS = {"X-Fuzz/1", "X-Fuzz/2", "X-Fuzz/3", "Europe/Paris", "America/New_York",
            "UTC", "GMT"};
    private static final String[] OFFSETS = {"+0000", "+0100", "+0530", "-0500", "+1300", "-0930"};
    private static final Instant WHEN = Instant.parse("2024-01-15T12:00:00Z");
    private static long executions;
    private static boolean singleCalendarReported;

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    private static final class Spec {
        final List<String[]> definitions = new ArrayList<>(); // {tzid, offset}
        final Set<String> events = new LinkedHashSet<>();

        String text() {
            StringBuilder b = new StringBuilder("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//twake-fuzz//registry//EN\n");
            for (String[] def : definitions) {
                b.append("BEGIN:VTIMEZONE\nTZID:").append(def[0]).append("\nBEGIN:STANDARD\nDTSTART:19700101T000000\n")
                        .append("TZOFFSETFROM:").append(def[1]).append("\nTZOFFSETTO:").append(def[1])
                        .append("\nEND:STANDARD\nEND:VTIMEZONE\n");
            }
            int i = 0;
            for (String id : events) {
                b.append("BEGIN:VEVENT\nUID:e").append(i++).append("@twake-fuzz\nDTSTAMP:20240101T000000Z\n")
                        .append("DTSTART;TZID=").append(id).append(":20240115T120000\nEND:VEVENT\n");
            }
            return b.append("END:VCALENDAR\n").toString();
        }
    }

    private static final class Parsed {
        final Spec spec;
        final Calendar calendar;
        final CalendarBuilder builder;

        Parsed(Spec spec, Calendar calendar, CalendarBuilder builder) {
            this.spec = spec;
            this.calendar = calendar;
            this.builder = builder;
        }
    }

    private static boolean ranOut(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (String.valueOf(t.getMessage()).contains("Ran out of available zone IDs")) {
                return true;
            }
        }
        return false;
    }

    private static void collect() {
        System.gc();
        ZoneRulesProviderImpl.getInstance().ifPresent(p -> p.getZoneIdPool().cleanup());
    }

    private static Parsed parse(Spec spec) throws IOException, ParserException {
        CalendarBuilder builder = new CalendarBuilder();
        return new Parsed(spec, builder.build(new StringReader(spec.text())), builder);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) throws IOException {
        Ical.hints(d.consumeInt(0, 3) == 0);
        int n = d.consumeInt(1, 20);
        List<Parsed> kept = new ArrayList<>();
        ZonedDateTime retained = null;
        int keptDefinitions = 0;
        for (int i = 0; i < n; i++) {
            Spec spec = new Spec();
            int defs;
            // (the maximum, not the minimum: an exhausted FuzzedDataProvider returns the minimum)
            if (d.consumeInt(0, 63) == 63) {
                // one calendar defining the same TZID 1600 times: it alone would hold more ids than the pool has
                defs = 1600;
            } else {
                defs = d.consumeInt(0, 7) == 0 ? d.consumeInt(0, 200) : d.consumeInt(0, 6);
            }
            String sameTzid = d.pickValue(TZIDS);
            for (int j = 0; j < defs; j++) {
                spec.definitions.add(new String[]{defs >= 1600 ? sameTzid : d.pickValue(TZIDS), d.pickValue(OFFSETS)});
            }
            int events = d.consumeInt(1, 4);
            for (int j = 0; j < events; j++) {
                spec.events.add(d.pickValue(TZIDS));
            }

            Parsed parsed;
            try {
                parsed = parse(spec);
            } catch (ParserException e) {
                if (!ranOut(e)) {
                    return;
                }
                // the pool is refilled only when the registries holding its ids are collected
                collect();
                try {
                    parsed = parse(spec);
                } catch (ParserException again) {
                    if (!ranOut(again)) {
                        return;
                    }
                    if (defs >= 1500) {
                        if (!singleCalendarReported || Ical.DEBUG) {
                            singleCalendarReported = true;
                            throw new AssertionError("one calendar with " + defs + " VTIMEZONEs (TZID " + sameTzid
                                    + ") exhausts the JVM-wide zone-id pool: every registration allocates a new id, even"
                                    + " for an already registered TZID");
                        }
                        return;
                    }
                    if (keptDefinitions + defs < 1500) {
                        throw new AssertionError("\"Ran out of available zone IDs\" while only " + (keptDefinitions
                                + defs) + " VTIMEZONEs are referenced, after the dropped builders were collected");
                    }
                    return;
                }
            }
            if (parsed.calendar == null) {
                return;
            }
            if (d.consumeBoolean()) {
                check(parsed, "right after its parse");
            }
            if (d.consumeBoolean()) {
                kept.add(parsed);
                keptDefinitions += defs;
            } else if (retained == null && d.consumeBoolean()) {
                retained = firstZoned(parsed);
            }
        }
        // the lazily parsed values of the kept calendars, read after the other registries were created
        for (Parsed p : kept) {
            check(p, "after " + n + " calendars were parsed");
        }

        if (++executions % 16 == 0 || Ical.DEBUG) {
            kept.clear();
            collect();
            // bounded state: once the dropped builders are collected, a custom VTIMEZONE parses
            Spec fixed = new Spec();
            fixed.definitions.add(new String[]{"X-Fuzz/fixed", "+0200"});
            fixed.events.add("X-Fuzz/fixed");
            try {
                Parsed p = parse(fixed);
                check(p, "after a full collection");
            } catch (ParserException e) {
                throw new AssertionError("a calendar with one custom VTIMEZONE fails after every builder was dropped"
                        + " and collected: " + e.getMessage(), e);
            }
            if (retained != null) {
                // a date-time whose registry has been collected: its zone id may have been released and reallocated
                ZoneOffset before = retained.getOffset();
                try {
                    ZoneOffset now = retained.getZone().getRules().getOffset(retained.toInstant());
                    if (!now.equals(before)) {
                        throw new AssertionError("a date-time of a collected registry (" + retained + ") now resolves"
                                + " with another registry's rules: offset " + now + " instead of " + before);
                    }
                } catch (java.time.zone.ZoneRulesException e) {
                    // the id is no longer known: reported as such, not as wrong rules
                }
            }
        }
    }

    private static ZonedDateTime firstZoned(Parsed p) {
        for (Object o : p.calendar.getComponents("VEVENT")) {
            try {
                Optional<DtStart<Temporal>> start = ((VEvent) o).getStartDate();
                if (start.isPresent() && start.get().getDate() instanceof ZonedDateTime) {
                    return (ZonedDateTime) start.get().getDate();
                }
            } catch (DateTimeException e) {
                // unresolvable: none retained
            }
        }
        return null;
    }

    private static void check(Parsed p, String when) {
        TimeZoneRegistry registry = p.builder.getRegistry();
        for (Object o : p.calendar.getComponents("VEVENT")) {
            VEvent event = (VEvent) o;
            Optional<DtStart<Temporal>> start = event.getStartDate();
            if (start.isEmpty()) {
                continue;
            }
            String tzid = start.get().getParameter(net.fortuna.ical4j.model.Parameter.TZID)
                    .map(t -> ((net.fortuna.ical4j.model.Parameter) t).getValue()).orElse(null);
            Temporal date;
            try {
                date = start.get().getDate();
            } catch (DateTimeException e) {
                continue; // a TZID this calendar does not define and no zone knows
            }
            if (!(date instanceof ZonedDateTime) || tzid == null) {
                continue;
            }
            ZoneOffset actual = ((ZonedDateTime) date).getOffset();
            boolean defined = p.spec.definitions.stream().anyMatch(def -> def[0].equals(tzid));
            ZoneOffset expected;
            String source;
            if (defined) {
                TimeZone tz = registry.getTimeZone(tzid);
                Observance obs = tz == null ? null : tz.getVTimeZone().getApplicableObservance(WHEN);
                if (obs == null) {
                    continue;
                }
                expected = ((TzOffsetTo) obs.getRequiredProperty(Property.TZOFFSETTO)).getOffset();
                source = "the definition getTimeZone(\"" + tzid + "\") returns";
            } else if (!tzid.startsWith("X-")) {
                expected = ZoneId.of(tzid).getRules().getOffset(WHEN);
                source = "the zone " + tzid + " (not defined by this calendar)";
            } else {
                continue;
            }
            if (!actual.equals(expected)) {
                String detail = "DTSTART;TZID=" + tzid + " resolves to offset " + actual + ", " + source + " gives "
                        + expected + " (" + when + "; definitions of this calendar: " + definitions(p.spec, tzid) + ")";
                if (defined) {
                    throw new AssertionError("duplicate or overridden TZID resolves with another definition: " + detail);
                }
                throw new AssertionError("an IANA TZID resolves with another calendar's override: " + detail);
            }
        }
    }

    private static String definitions(Spec spec, String tzid) {
        List<String> offsets = new ArrayList<>();
        spec.definitions.stream().filter(def -> def[0].equals(tzid)).forEach(def -> offsets.add(def[1]));
        return offsets.size() > 20 ? offsets.size() + " definitions" : offsets.toString();
    }
}
