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
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TextList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.AltRep;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.DelegatedTo;
import net.fortuna.ical4j.model.parameter.Dir;
import net.fortuna.ical4j.model.parameter.Language;
import net.fortuna.ical4j.model.parameter.Member;
import net.fortuna.ical4j.model.parameter.SentBy;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.RequestStatus;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.XProperty;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 06 — properties and parameters built programmatically from user text (an anonymous booker's title and name, a CN
 * written into a REPLY, X- values), put on a fixed VEVENT, written with CalendarOutputter(false) and re-parsed under
 * strict hints. The output re-parses into exactly one VEVENT holding the fixed properties plus the built one, with the
 * same name, parameters and value. Building may throw only IllegalArgumentException / URISyntaxException. Extra
 * properties or parameters on re-parse are the critical Injection signature; the leading/trailing-whitespace trim of
 * PropertyBuilder#value is allowed and reported once as a minor signature.
 */
public class PropertyOutputInjectionFuzzer {

    /** Critical: the re-parsed calendar holds content the harness did not build. */
    public static class Injection extends AssertionError {
        Injection(String message) {
            super(message);
        }
    }

    /** Minor: PropertyBuilder#value trims the value (once per run). */
    public static class WhitespaceTrimmed extends AssertionError {
        WhitespaceTrimmed(String message) {
            super(message);
        }
    }

    private static final String[] TOKENS = {"\r", "\n", "\r\n", "\r\n ", "\u0085", " ", "\u0000", "\"", ";", ":",
            ",", "\\", "^", "^n", "^'", "^^", "END:VEVENT", "BEGIN:VALARM", "RRULE:FREQ=DAILY", "ATTACH:", "mailto:",
            " ", "\t", "é", "😀", "\uD800", "\uDC00", "\r\nX-INJECTED:1", "\r\nATTENDEE:mailto:evil@x"};
    private static final String[] PROPERTIES = {"SUMMARY", "DESCRIPTION", "LOCATION", "COMMENT", "CATEGORIES",
            "X-PROPERTY", "X-PROPERTY-VALUE", "ATTENDEE", "ORGANIZER", "UID", "REQUEST-STATUS", "URL"};
    private static final String[] PARAMETERS = {"NONE", "CN", "X-PARAM", "MEMBER", "DELEGATED-TO", "SENT-BY",
            "ALTREP", "DIR", "LANGUAGE", "TZID"};
    private static final String[] FIXED = {"UID", "DTSTAMP", "DTSTART"};
    private static boolean trimReported;

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    private static String text(FuzzedDataProvider d) {
        StringBuilder b = new StringBuilder();
        int parts = d.consumeInt(0, 8);
        for (int i = 0; i < parts; i++) {
            b.append(d.consumeBoolean() ? d.pickValue(TOKENS) : d.consumeString(10));
        }
        return b.toString();
    }

    private static URI mailto(String s) throws URISyntaxException {
        return new URI("mailto", s, null);
    }

    private static Property property(String kind, FuzzedDataProvider d) throws URISyntaxException {
        switch (kind) {
            case "SUMMARY":
                return new Summary(text(d));
            case "DESCRIPTION":
                return new Description(text(d));
            case "LOCATION":
                return new Location(text(d));
            case "COMMENT":
                return new Comment(text(d));
            case "CATEGORIES": {
                List<String> items = new ArrayList<>();
                int n = d.consumeInt(1, 3);
                for (int i = 0; i < n; i++) {
                    items.add(text(d));
                }
                return new Categories(new TextList(items));
            }
            case "X-PROPERTY":
                return new XProperty("X-TWAKE-" + d.pickValue(new String[]{"MEETING-UID", "SPACE", "BOOKING"}),
                        text(d));
            case "X-PROPERTY-VALUE":
                return new XProperty("X-TWAKE-LINK", new ParameterList(List.of(d.pickValue(
                        new Value[]{Value.URI, Value.TEXT, Value.BINARY, Value.DATE_TIME, new Value("X-FOO")}))),
                        text(d));
            case "ATTENDEE":
                return new Attendee(mailto(text(d)));
            case "ORGANIZER":
                return new Organizer(mailto(text(d)));
            case "UID":
                return new Uid(text(d));
            case "REQUEST-STATUS":
                return new RequestStatus(d.pickValue(new String[]{"2.0", "3.7", text(d)}), text(d),
                        d.consumeBoolean() ? text(d) : null);
            default:
                return new Url(new URI(d.pickValue(new String[]{"https", "mailto", "x-app"}), text(d), null));
        }
    }

    private static Parameter parameter(String kind, FuzzedDataProvider d) throws URISyntaxException {
        switch (kind) {
            case "CN":
                return new Cn(text(d));
            case "X-PARAM":
                return new XParameter("X-TWAKE-PARAM", text(d));
            case "MEMBER":
                return new Member(Arrays.asList(mailto(text(d)), mailto("list@example.com")));
            case "DELEGATED-TO":
                return new DelegatedTo(List.of(mailto(text(d))));
            case "SENT-BY":
                return new SentBy(mailto(text(d)));
            case "ALTREP":
                return new AltRep(new URI("https", "example.com", "/" + text(d), null));
            case "DIR":
                return new Dir(new URI("ldap", "example.com", "/" + text(d), null));
            case "LANGUAGE":
                return new Language(text(d));
            default:
                return new TzId(text(d));
        }
    }

    private static String normalise(String s) {
        return s.replace("\r\n", "\n").replace('\r', '\n');
    }

    /** The value PropertyBuilder#value keeps: String#trim of the written value, CR/LF being written as \n. */
    private static String trimmed(String value) {
        int start = 0;
        int end = value.length();
        while (start < end && value.charAt(start) <= ' ' && value.charAt(start) != '\r' && value.charAt(start) != '\n') {
            start++;
        }
        while (end > start && value.charAt(end - 1) <= ' ' && value.charAt(end - 1) != '\r'
                && value.charAt(end - 1) != '\n') {
            end--;
        }
        return value.substring(start, end);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) throws IOException {
        Ical.hints(false);
        String propertyKind = d.pickValue(PROPERTIES);
        String parameterKind = d.pickValue(PARAMETERS);
        Property built;
        try {
            built = property(propertyKind, d);
            if (!parameterKind.equals("NONE")) {
                built.add(parameter(parameterKind, d));
            }
        } catch (URISyntaxException | IllegalArgumentException e) {
            return;
        }
        String builtValue = built.getValue();
        Ical.debug("property", Ical.abbreviate(built.toString(), 2000));

        Calendar calendar;
        try {
            calendar = Ical.parse("BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//twake-fuzz//injection//EN\r\n"
                    + "BEGIN:VEVENT\r\nUID:fixed@twake-fuzz\r\nDTSTAMP:20240101T000000Z\r\nDTSTART:20240102T100000Z\r\n"
                    + "END:VEVENT\r\nEND:VCALENDAR\r\n", false);
        } catch (ParserException e) {
            throw new IllegalStateException(e);
        }
        VEvent event = calendar.<VEvent>getComponent("VEVENT").orElseThrow();
        event.add(built);
        String out = Ical.write(calendar);

        Calendar reparsed;
        try {
            reparsed = Ical.parse(out, false);
        } catch (ParserException e) {
            // one signature per place where the parse fails (the ical4j frames of the cause), not per message
            throw Ical.withoutJdkFrames("the calendar with " + propertyKind + "/" + parameterKind
                    + " does not re-parse: " + e.getMessage() + "\n" + Ical.abbreviate(out, 1500),
                    e.getCause() != null ? e.getCause() : e);
        }
        if (reparsed == null || reparsed.getComponents().size() != 1
                || !reparsed.getComponents().get(0).getName().equals("VEVENT")) {
            throw new Injection("the re-parsed calendar does not hold exactly one VEVENT:\n" + Ical.abbreviate(out, 1500));
        }
        if (!reparsed.getProperties().equals(calendar.getProperties())) {
            throw new Injection("the re-parsed calendar has other calendar properties:\n" + Ical.abbreviate(out, 1500));
        }
        VEvent again = (VEvent) reparsed.getComponents().get(0);
        if (!again.getAlarms().isEmpty()) {
            throw new Injection("the re-parsed VEVENT has a VALARM:\n" + Ical.abbreviate(out, 1500));
        }
        List<Property> props = again.getProperties();
        List<String> names = new ArrayList<>();
        props.forEach(p -> names.add(p.getName()));
        List<String> expectedNames = new ArrayList<>(Arrays.asList(FIXED));
        expectedNames.add(built.getName());
        if (!names.equals(expectedNames)) {
            String message = "the re-parsed VEVENT holds " + names + " instead of " + expectedNames + ":\n"
                    + Ical.abbreviate(out, 1500);
            throw names.size() > expectedNames.size() ? new Injection(message) : new AssertionError(message);
        }
        Property p = props.get(3);
        List<Parameter> params = p.getParameterList().getAll();
        List<Parameter> builtParams = built.getParameterList().getAll();
        if (params.size() > builtParams.size()) {
            throw new Injection(propertyKind + "/" + parameterKind + ": parameters " + builtParams + " re-parse as "
                    + params + ":\n" + Ical.abbreviate(out, 1500));
        }
        for (int i = 0; i < builtParams.size(); i++) {
            Parameter x = builtParams.get(i);
            Optional<Parameter> y = i < params.size() ? Optional.of(params.get(i)) : Optional.empty();
            if (y.isEmpty() || !y.get().getName().equalsIgnoreCase(x.getName())
                    || !String.valueOf(y.get().getValue()).equals(normalise(String.valueOf(x.getValue())))) {
                throw new AssertionError(propertyKind + "/" + parameterKind + ": parameter " + x.getName() + "=["
                        + Ical.abbreviate(String.valueOf(x.getValue()), 300) + "] re-parses as "
                        + y.map(q -> q.getName() + "=[" + Ical.abbreviate(String.valueOf(q.getValue()), 300) + "]")
                        .orElse("nothing") + ":\n" + Ical.abbreviate(out, 1500));
            }
        }
        if (built instanceof Categories && p instanceof Categories) {
            // TextList is a set of non-empty items: duplicates and empty items are dropped by design, so compare
            // the items, not the joined value
            List<String> expected = new ArrayList<>(new java.util.LinkedHashSet<>(((Categories) built).getCategories()
                    .getTexts().stream().map(PropertyOutputInjectionFuzzer::normalise).filter(s -> !s.isEmpty())
                    .collect(java.util.stream.Collectors.toList())));
            List<String> actual = new ArrayList<>(((Categories) p).getCategories().getTexts());
            if (!actual.equals(expected) && builtValue != null && actual.equals(new ArrayList<>(
                    new TextList(trimmed(normalise(builtValue))).getTexts()))) {
                // PropertyBuilder#value trims the whole value: the trimming finding below, reported once per run
                if (!trimReported || Ical.DEBUG) {
                    trimReported = true;
                    throw new WhitespaceTrimmed(propertyKind + ": the value [" + Ical.abbreviate(builtValue, 300)
                            + "] re-parses trimmed (PropertyBuilder#value)");
                }
                return;
            }
            if (!actual.equals(expected)) {
                throw new AssertionError(propertyKind + "/" + parameterKind + ": items " + expected
                        + " re-parse as " + actual + ":\n" + Ical.abbreviate(out, 1500));
            }
            return;
        }
        String value = p.getValue();
        // a CR or CRLF is written as \n by design (37f79783): expected back as LF
        if (builtValue != null) {
            builtValue = normalise(builtValue);
        }
        if (!String.valueOf(value).equals(String.valueOf(builtValue))) {
            if (builtValue != null && String.valueOf(value).equals(trimmed(builtValue))) {
                if (!trimReported || Ical.DEBUG) {
                    trimReported = true;
                    throw new WhitespaceTrimmed(propertyKind + ": the value [" + Ical.abbreviate(builtValue, 300)
                            + "] re-parses trimmed (PropertyBuilder#value)");
                }
                return;
            }
            throw new AssertionError(propertyKind + "/" + parameterKind + ": value [" + Ical.abbreviate(builtValue, 300)
                    + "] re-parses as [" + Ical.abbreviate(String.valueOf(value), 300) + "]:\n"
                    + Ical.abbreviate(out, 1500));
        }
    }
}
