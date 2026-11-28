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
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentGroup;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TemporalAmountAdapter;
import net.fortuna.ical4j.model.TemporalComparator;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * 03 — Component#calculateRecurrenceSet (RecurrenceSet.Builder) and ComponentGroup#calculateRecurrenceSet on a master
 * VEVENT (DTSTART of any form, DTEND/DURATION/none, 0–2 RRULEs, RDATE/EXDATE of mixed types, EXRULE) and 0–3
 * overrides, queried with a fuzzed window of at most 2 years. Only DateTimeException / IllegalArgumentException may be
 * thrown; the set over [a, c) is the union of the sets over [a, b) and [b, c); it equals a reference computed from
 * Recur#getDates, the RDATEs and the EXDATEs (matched by instant, by local date for DATE values); in the group, each
 * override replaces its master occurrence and contributes at most one occurrence.
 */
public class RecurrenceSetExpansionFuzzer {

    private static final DateTimeFormatter DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter LOCAL = DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmss");
    private static final String[] ZONES = {"Europe/Paris", "America/Sao_Paulo", "Australia/Lord_Howe"};
    private static final TemporalComparator CMP = TemporalComparator.INSTANCE;

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    /** A DATE / DATE-TIME property line of one of the four forms, at the given local time. */
    private static String dateProperty(String name, int form, LocalDateTime t, String zone) {
        switch (form) {
            case 0:
                return name + ";VALUE=DATE:" + t.toLocalDate().format(DATE);
            case 1:
                return name + ":" + t.format(LOCAL);
            case 2:
                return name + ":" + t.format(LOCAL) + "Z";
            default:
                return name + ";TZID=" + zone + ":" + t.format(LOCAL);
        }
    }

    private static String dateList(FuzzedDataProvider d, String name, LocalDateTime base, String zone, boolean periods) {
        int form = d.consumeInt(0, periods ? 4 : 3);
        int n = d.consumeInt(1, 3);
        List<String> values = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            LocalDateTime t = base.plusDays(d.consumeInt(0, 400)).plusHours(d.consumeInt(-2, 2) * d.consumeInt(0, 1));
            switch (form) {
                case 0:
                    values.add(t.toLocalDate().format(DATE));
                    break;
                case 2:
                    values.add(t.format(LOCAL) + "Z");
                    break;
                case 4:
                    values.add(t.format(LOCAL) + "Z/" + (d.consumeBoolean() ? "PT1H" : t.plusHours(2).format(LOCAL) + "Z"));
                    break;
                default:
                    values.add(t.format(LOCAL));
                    break;
            }
        }
        String params = form == 0 ? ";VALUE=DATE" : form == 3 ? ";TZID=" + zone : form == 4 ? ";VALUE=PERIOD" : "";
        return name + params + ":" + String.join(",", values);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) throws IOException {
        Ical.hints(d.consumeBoolean());
        String zone = d.pickValue(ZONES);
        int form = d.consumeInt(0, 3);
        LocalDateTime start = LocalDateTime.of(d.consumeInt(1990, 2030), d.consumeInt(1, 12), d.consumeInt(1, 28),
                d.consumeInt(0, 23), d.pickValue(new int[]{0, 0, 30}));
        // the window is bounded by the finest frequency: a 2-year window of a SECONDLY rule is a legitimately huge
        // answer, not a defect
        String freq = d.pickValue(new String[]{"DAILY", "WEEKLY", "MONTHLY", "YEARLY", "DAILY", "WEEKLY", "HOURLY",
                "MINUTELY"});
        if (form == 0 && (freq.equals("HOURLY") || freq.equals("MINUTELY"))) {
            freq = "DAILY"; // a time-based FREQ on a DATE DTSTART: scenario 04's UnsupportedTemporalTypeException
        }
        long maxWindowHours = freq.equals("MINUTELY") ? 24 : freq.equals("HOURLY") ? 24 * 30 : 24 * 730;

        StringBuilder master = new StringBuilder("BEGIN:VEVENT\nUID:master@twake-fuzz\nDTSTAMP:20240101T000000Z\n");
        master.append(dateProperty("DTSTART", form, start, zone)).append('\n');
        switch (d.consumeInt(0, 3)) {
            case 0:
                master.append(dateProperty("DTEND", d.consumeInt(0, 3) == 0 ? d.consumeInt(0, 3) : form,
                        start.plusHours(d.consumeInt(0, 50)), zone)).append('\n');
                break;
            case 1:
                master.append("DURATION:").append(d.pickValue(new String[]{"P1D", "PT1H", "PT0S", "P1W", "PT90M",
                        "P2DT3H", "-PT1H"})).append('\n');
                break;
            default:
                break;
        }
        int rrules = d.consumeInt(0, 2);
        for (int i = 0; i < rrules; i++) {
            master.append("RRULE:").append(Rules.rule(d, freq)).append('\n');
        }
        if (d.consumeBoolean()) {
            master.append(dateList(d, "RDATE", start, zone, true)).append('\n');
        }
        if (d.consumeBoolean()) {
            master.append(dateList(d, "EXDATE", start, zone, false)).append('\n');
        }
        if (d.consumeInt(0, 3) == 0) {
            master.append("EXRULE:").append(Rules.rule(d, freq)).append('\n');
        }
        master.append("END:VEVENT\n");

        StringBuilder overrides = new StringBuilder();
        int n = d.consumeInt(0, 3);
        for (int i = 0; i < n; i++) {
            LocalDateTime rid = start.plusDays(d.consumeInt(0, 60));
            overrides.append("BEGIN:VEVENT\nUID:master@twake-fuzz\nDTSTAMP:20240101T000000Z\n")
                    .append(dateProperty("RECURRENCE-ID", d.consumeInt(0, 3) == 0 ? d.consumeInt(0, 3) : form, rid,
                            d.pickValue(ZONES))).append('\n')
                    .append(dateProperty("DTSTART", form, rid.plusHours(d.consumeInt(-3, 3)), zone)).append('\n')
                    .append("END:VEVENT\n");
        }
        String ics = "BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//twake-fuzz//recurrence//EN\n" + master + overrides
                + "END:VCALENDAR\n";

        // the query window: up to 2 years (less for fine-grained rules), of any temporal type
        LocalDateTime a = start.plusDays(d.consumeInt(-30, 400)).plusHours(d.consumeInt(0, 23));
        long hours = Math.min(maxWindowHours, d.consumeLong(0, 24 * 730));
        LocalDateTime c = a.plusHours(hours);
        int windowType = d.consumeInt(0, 3);
        long split = hours > 1 ? d.consumeLong(1, hours - 1) : -1;
        Ical.debug("calendar", ics);
        Ical.debug("window", a + " .. " + c + " (type " + windowType + ", split at +" + split + "h)");

        Calendar calendar;
        try {
            calendar = Ical.parse(ics, false);
        } catch (ParserException e) {
            return;
        }
        if (calendar == null) {
            return;
        }
        List<VEvent> events = calendar.getComponents("VEVENT");
        VEvent masterEvent = events.stream().filter(e -> e.getProperty(Property.RECURRENCE_ID).isEmpty())
                .findFirst().orElse(null);
        if (masterEvent == null) {
            return;
        }

        Period<Temporal> window = new Period<>(Rules.of(a, windowType, ZoneId.of(zone)),
                Rules.of(c, windowType, ZoneId.of(zone)));
        Set<String> whole = starts(expand(masterEvent, window));
        if (whole == null) {
            return;
        }

        // a negative DURATION or a DTEND before DTSTART has no defined occurrence set (RFC 5545 3.8.2.2, 3.8.2.5):
        // it is still expanded above, so exceptions are caught, but the set oracles do not apply
        boolean inverted = inverted(masterEvent);

        // window additivity
        if (split > 0 && windowType != 0 && !inverted) {
            LocalDateTime b = a.plusHours(split);
            Temporal tb = Rules.of(b, windowType, ZoneId.of(zone));
            Set<String> left = starts(expand(masterEvent, new Period<>(window.getStart(), tb)));
            Set<String> right = starts(expand(masterEvent, new Period<>(tb, window.getEnd())));
            if (left != null && right != null) {
                Set<String> union = new TreeSet<>(left);
                union.addAll(right);
                if (!union.equals(whole)) {
                    throw new AssertionError("the recurrence set depends on how the window is split: [a, c) = "
                            + whole + ", [a, b) ∪ [b, c) = " + union + " (b = " + tb + ")");
                }
            }
        }

        // the reference
        if (!inverted && whole.size() < 5000) {
            reference(masterEvent, window, whole);
        }

        // the group: overrides replace their master occurrence
        if (events.size() > 1) {
            List<Period<Temporal>> grouped;
            try {
                grouped = new ComponentGroup<>(events, new Uid("master@twake-fuzz")).calculateRecurrenceSet(window);
            } catch (DateTimeException | IllegalArgumentException e) {
                return;
            }
            for (VEvent e : events) {
                Optional<RecurrenceId<Temporal>> rid = e.getProperty(Property.RECURRENCE_ID);
                if (rid.isEmpty()) {
                    continue;
                }
                Temporal r;
                try {
                    r = rid.get().getDate();
                } catch (DateTimeException ex) {
                    continue;
                }
                long own = grouped.stream().filter(p -> p.getComponent() == e).count();
                if (own > 1) {
                    throw new AssertionError("an override contributes " + own + " occurrences");
                }
                for (Period<Temporal> p : grouped) {
                    if (p.getComponent() == masterEvent && sameInstant(p.getStart(), r)) {
                        throw new AssertionError("the master occurrence " + p.getStart() + " survives its override"
                                + " RECURRENCE-ID " + rid.get().getValue() + " (" + r + ")");
                    }
                }
            }
        }
    }

    /** null when the expansion throws one of the documented exceptions. */
    private static Set<Period<Temporal>> expand(VEvent event, Period<Temporal> window) {
        try {
            return event.calculateRecurrenceSet(window);
        } catch (DateTimeException | IllegalArgumentException e) {
            if (e instanceof java.time.temporal.UnsupportedTemporalTypeException) {
                throw Ical.withoutJdkFrames("calculateRecurrenceSet throws UnsupportedTemporalTypeException", e);
            }
            return null;
        }
    }

    private static String key(Temporal t) {
        if (t instanceof LocalDate) {
            return "D" + t;
        } else if (t instanceof LocalDateTime) {
            return "L" + t;
        } else if (t instanceof ZonedDateTime) {
            return "I" + ((ZonedDateTime) t).toInstant();
        } else if (t instanceof OffsetDateTime) {
            return "I" + ((OffsetDateTime) t).toInstant();
        } else if (t instanceof Instant) {
            return "I" + t;
        }
        return "?" + t;
    }

    private static Set<String> starts(Set<Period<Temporal>> periods) {
        if (periods == null) {
            return null;
        }
        Set<String> keys = new TreeSet<>();
        periods.forEach(p -> keys.add(key(p.getStart())));
        return keys;
    }

    private static boolean sameInstant(Temporal x, Temporal y) {
        boolean xi = x.isSupported(ChronoField.INSTANT_SECONDS);
        boolean yi = y.isSupported(ChronoField.INSTANT_SECONDS);
        if (xi && yi) {
            return Instant.from(x).equals(Instant.from(y));
        }
        return CMP.compare(x, y) == 0;
    }

    /** RFC 5545 §3.8.5.1 as the scenario reads it: by instant, by local date when either value is a DATE. */
    private static boolean inverted(VEvent event) {
        Optional<Duration> duration = event.getProperty(Property.DURATION);
        if (duration.isPresent() && duration.get().getValue().startsWith("-")) {
            return true;
        }
        Optional<DtStart<Temporal>> start = event.getProperty(Property.DTSTART);
        Optional<DtEnd<Temporal>> end = event.getProperty(Property.DTEND);
        return start.isPresent() && end.isPresent() && start.get().getDate() != null && end.get().getDate() != null
                && CMP.compare(end.get().getDate(), start.get().getDate()) < 0;
    }

    private static boolean excludedBy(Temporal occurrence, Temporal exdate) {
        // a DATE EXDATE excludes every occurrence of that day
        if (exdate instanceof LocalDate) {
            return localDate(occurrence).equals(localDate(exdate));
        }
        boolean oi = occurrence.isSupported(ChronoField.INSTANT_SECONDS);
        boolean ei = exdate.isSupported(ChronoField.INSTANT_SECONDS);
        if (oi && ei) {
            return Instant.from(occurrence).equals(Instant.from(exdate));
        }
        // a DATE occurrence and a DATE-TIME EXDATE, or a floating value and an absolute one: RFC 5545 does not
        // define the match, so use ical4j's documented rule (TemporalComparator, as RecurrenceSet does)
        if (occurrence instanceof LocalDate || oi != ei) {
            return CMP.compare(occurrence, exdate) == 0;
        }
        return LocalDateTime.from(occurrence).equals(LocalDateTime.from(exdate));
    }

    private static LocalDate localDate(Temporal t) {
        return t instanceof LocalDate ? (LocalDate) t : LocalDate.from(t);
    }

    private static boolean withinBounds(Period<Temporal> bounds, Temporal start, Temporal end) {
        if (CMP.compare(bounds.getStart(), bounds.getEnd()) == 0) {
            int s = CMP.compare(start, bounds.getStart());
            return s == 0 || (s < 0 && CMP.compare(bounds.getStart(), end) < 0);
        }
        if (CMP.compare(start, end) == 0) {
            return CMP.compare(bounds.getStart(), start) <= 0 && CMP.compare(start, bounds.getEnd()) < 0;
        }
        return CMP.compare(bounds.getStart(), end) < 0 && CMP.compare(start, bounds.getEnd()) < 0;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void reference(VEvent event, Period<Temporal> window, Set<String> actual) {
        Temporal start;
        TemporalAmount duration;
        try {
            start = ((DateProperty<Temporal>) event.getProperty(Property.DTSTART).orElseThrow()).getDate();
            Optional<Property> dur = event.getProperty(Property.DURATION);
            Optional<Property> end = event.getProperty(Property.DTEND);
            if (dur.isPresent()) {
                duration = ((Duration) dur.get()).getDuration();
            } else if (end.isEmpty()) {
                duration = start.isSupported(ChronoField.SECOND_OF_DAY) ? java.time.Duration.ZERO
                        : java.time.Period.ofDays(1);
            } else {
                duration = TemporalAmountAdapter.between(start, ((DateProperty<Temporal>) end.get()).getDate())
                        .getDuration();
            }
        } catch (DateTimeException | IllegalArgumentException e) {
            return;
        }
        Temporal from = duration instanceof java.time.Duration && !window.getStart().isSupported(ChronoUnit.SECONDS)
                ? window.getStart().minus(((java.time.Duration) duration).toDays() + 1, ChronoUnit.DAYS)
                : window.getStart().minus(duration);

        List<Temporal[]> occurrences = new ArrayList<>(); // {start, end}
        try {
            List<Property> rrules = event.getProperties(Property.RRULE);
            if (rrules.isEmpty()) {
                occurrences.add(new Temporal[]{start, start.plus(duration)});
            }
            for (Property p : rrules) {
                for (Temporal t : ((RRule<Temporal>) p).getRecur().getDates(start, from, window.getEnd())) {
                    occurrences.add(new Temporal[]{t, t.plus(duration)});
                }
            }
            for (Property p : event.getProperties(Property.RDATE)) {
                if (Optional.of(Value.PERIOD).equals(p.getParameter(Parameter.VALUE))) {
                    for (Period<Temporal> period : ((RDate<Temporal>) p).getPeriods().orElse(Set.of())) {
                        occurrences.add(new Temporal[]{period.getStart(), period.getEnd()});
                    }
                } else {
                    for (Temporal t : ((DateListProperty<Temporal>) p).getDates()) {
                        occurrences.add(new Temporal[]{t, t.plus(duration)});
                    }
                }
            }
        } catch (DateTimeException | IllegalArgumentException e) {
            return;
        }
        List<Temporal> exdates = new ArrayList<>();
        List<Temporal> exrules = new ArrayList<>();
        try {
            for (Property p : event.getProperties(Property.EXDATE)) {
                exdates.addAll(((DateListProperty<Temporal>) p).getDates());
            }
            for (Property p : event.getProperties(Property.EXRULE)) {
                exrules.addAll(((ExRule<Temporal>) p).getRecur().getDates(start, from, window.getEnd()));
            }
        } catch (DateTimeException | IllegalArgumentException e) {
            return;
        }
        Set<String> expected = new TreeSet<>();
        Set<String> excludedByDate = new TreeSet<>();
        Set<String> excludedByExruleOfOtherType = new TreeSet<>();
        Set<String> exruleKeys = new java.util.HashSet<>();
        Map<Character, List<Temporal>> exrulesByCategory = new java.util.HashMap<>();
        for (Temporal x : exrules) {
            String k = key(x);
            exruleKeys.add(k);
            exrulesByCategory.computeIfAbsent(k.charAt(0), c -> new ArrayList<>()).add(x);
        }
        for (Temporal[] o : occurrences) {
            if (!withinBounds(window, o[0], o[1])) {
                continue;
            }
            boolean excluded = false;
            for (Temporal x : exdates) {
                if (excludedBy(o[0], x)) {
                    excluded = true;
                    if (x instanceof LocalDate != o[0] instanceof LocalDate) {
                        excludedByDate.add(key(o[0]));
                    }
                }
            }
            String k = key(o[0]);
            if (exruleKeys.contains(k)) {
                excluded = true;
                excludedByExruleOfOtherType.add(k);
            } else {
                // values of another category (DATE, floating, absolute): compared one by one
                for (Map.Entry<Character, List<Temporal>> e : exrulesByCategory.entrySet()) {
                    if (e.getKey() == k.charAt(0)) {
                        continue;
                    }
                    for (Temporal x : e.getValue()) {
                        if (sameInstant(o[0], x)) {
                            excluded = true;
                            excludedByExruleOfOtherType.add(k);
                            break;
                        }
                    }
                }
            }
            if (!excluded) {
                expected.add(key(o[0]));
            }
        }
        if (!expected.equals(actual)) {
            Set<String> missing = new TreeSet<>(expected);
            missing.removeAll(actual);
            Set<String> extra = new TreeSet<>(actual);
            extra.removeAll(expected);
            String detail = "missing " + missing + ", unexpected " + extra + " (window " + window.getStart() + " .. "
                    + window.getEnd() + ", DTSTART " + start + ")";
            if (!extra.isEmpty() && excludedByDate.containsAll(extra) && missing.isEmpty()) {
                throw new AssertionError("an EXDATE of another value type (DATE vs DATE-TIME) does not exclude the"
                        + " occurrence of that day: " + detail);
            }
            if (!extra.isEmpty() && missing.isEmpty() && extra.stream().allMatch(k -> excludedByDate.contains(k)
                    || excludedByExruleOfOtherType.contains(k))) {
                throw new AssertionError("an occurrence starting at an EXRULE date is not excluded (EXRULE removal"
                        + " compares whole periods, Period#equals: another duration or value type): " + detail);
            }
            throw new AssertionError("calculateRecurrenceSet differs from the reference: " + detail);
        }
    }
}
