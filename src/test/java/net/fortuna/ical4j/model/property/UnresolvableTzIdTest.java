/**
 * Copyright (c) 2012, Ben Fortuna
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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies how a {@code DTSTART} carrying a {@code TZID} that resolves to no known zone is handled, across both
 * relaxed and strict validation modes (see the {@code tolerate-unresolvable-tzid-relaxed} OpenSpec change).
 */
public class UnresolvableTzIdTest {

    private static final String UNKNOWN_TZID_CALENDAR = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//NONSGML v1.0//EN\n" +
            "BEGIN:VEVENT\n" +
            "UID:c3b11f81-60c1-11f1-bc40-d843aea66ff2\n" +
            "DTSTAMP:20260605T120000Z\n" +
            "DTSTART;TZID=Unknown:20260605T120000\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

    // A self-contained VTIMEZONE so the TZID resolves locally without any network lookup.
    private static final String KNOWN_TZID_CALENDAR = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:-//Test//NONSGML v1.0//EN\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:Australia/Melbourne\n" +
            "BEGIN:STANDARD\n" +
            "DTSTART:20080406T030000\n" +
            "TZOFFSETFROM:+1100\n" +
            "TZOFFSETTO:+1000\n" +
            "TZNAME:AEST\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=4;BYDAY=1SU\n" +
            "END:STANDARD\n" +
            "BEGIN:DAYLIGHT\n" +
            "DTSTART:20081005T020000\n" +
            "TZOFFSETFROM:+1000\n" +
            "TZOFFSETTO:+1100\n" +
            "TZNAME:AEDT\n" +
            "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=1SU\n" +
            "END:DAYLIGHT\n" +
            "END:VTIMEZONE\n" +
            "BEGIN:VEVENT\n" +
            "UID:c3b11f81-60c1-11f1-bc40-d843aea66ff2\n" +
            "DTSTAMP:20260605T120000Z\n" +
            "DTSTART;TZID=Australia/Melbourne:20260605T120000\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";

    @AfterEach
    void tearDown() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }

    private static <T extends Property> T dtStartOf(CalendarBuilder builder, String ical)
            throws ParserException, IOException {
        Calendar calendar = builder.build(new StringReader(ical));
        var event = calendar.getComponent(Component.VEVENT).get();
        return event.getRequiredProperty(Property.DTSTART);
    }

    // -- relaxed validation: unresolvable TZID falls back to a floating value --

    @Test
    void getValue_unknownTimeZone_relaxed_returnsFloatingValue() throws ParserException, IOException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        DtStart<?> dtStart = dtStartOf(new CalendarBuilder(), UNKNOWN_TZID_CALENDAR);

        assertEquals("20260605T120000", dtStart.getValue());
    }

    @Test
    void getDate_unknownTimeZone_relaxed_returnsLocalDateTime() throws ParserException, IOException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        DtStart<?> dtStart = dtStartOf(new CalendarBuilder(), UNKNOWN_TZID_CALENDAR);

        Temporal date = dtStart.getDate();
        assertInstanceOf(LocalDateTime.class, date);
    }

    // -- strict validation (default): unresolvable TZID propagates a DateTimeException --

    @Test
    void getValue_unknownTimeZone_strict_throwsDateTimeException() throws ParserException, IOException {
        DtStart<?> dtStart = dtStartOf(new CalendarBuilder(), UNKNOWN_TZID_CALENDAR);

        assertThrows(DateTimeException.class, dtStart::getValue);
    }

    @Test
    void getDate_unknownTimeZone_strict_throwsDateTimeException() throws ParserException, IOException {
        DtStart<?> dtStart = dtStartOf(new CalendarBuilder(), UNKNOWN_TZID_CALENDAR);

        assertThrows(DateTimeException.class, dtStart::getDate);
    }

    // -- a resolvable TZID is unaffected, in either mode --

    @Test
    void knownTimeZone_strict_producesZonedDateTime() throws ParserException, IOException {
        assertKnownTimeZoneUnaffected();
    }

    @Test
    void knownTimeZone_relaxed_producesZonedDateTime() throws ParserException, IOException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        assertKnownTimeZoneUnaffected();
    }

    private void assertKnownTimeZoneUnaffected() throws ParserException, IOException {
        CalendarBuilder builder = new CalendarBuilder();
        DtStart<ZonedDateTime> dtStart = dtStartOf(builder, KNOWN_TZID_CALENDAR);

        ZonedDateTime date = dtStart.getDate();
        assertInstanceOf(ZonedDateTime.class, date);
        // the registry assigns a local zone id; it maps back to the original TZID
        assertEquals("Australia/Melbourne", builder.getRegistry().getTzId(date.getZone().getId()));
        assertEquals("20260605T120000", dtStart.getValue());
    }

    // -- validation still flags an unresolvable TZID, even while value access tolerates it --

    @Test
    void validate_unknownTimeZone_relaxed_reportsError() throws ParserException, IOException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        Calendar calendar = new CalendarBuilder().build(new StringReader(UNKNOWN_TZID_CALENDAR));

        ValidationResult result = calendar.validate();
        assertTrue(result.hasErrors());
    }

    @Test
    void validate_knownTimeZone_relaxed_reportsNoError() throws ParserException, IOException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        Calendar calendar = new CalendarBuilder().build(new StringReader(KNOWN_TZID_CALENDAR));

        ValidationResult result = calendar.validate();
        assertFalse(result.hasErrors(), result.getEntries().toString());
    }
}
