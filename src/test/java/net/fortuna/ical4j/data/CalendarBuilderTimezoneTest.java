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
package net.fortuna.ical4j.data;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

/**
 * $Id: CalendarBuilderTimezoneTest.java [Jul 1, 2008]
 *
 * Test case for CalendarBuilder and handling of icalendar streams
 * where VTIMZONES are included after other components.
 *
 * @author randy
 */
public class CalendarBuilderTimezoneTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected final void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_VALIDATION, true);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected final void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);

        System.clearProperty("net.fortuna.ical4j.timezone.utcDefault");
    }
    

   /**
     * Test that VTIMEZONES that are included after VEVENT 
     * are correctly handled and that dates defined before the
     * VTIMEZONE are parsed properly.
     */
    public void testVTimeZoneAfterVEvent() throws Exception {

        // Evolution includes VTIMEZONE defs after VEVENT defs,
        // which is allowed by RFC-2445
        InputStream in = getClass().getResourceAsStream(
                "/samples/valid/evolution.ics");
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = null;

        calendar = builder.build(in);
        assertNotNull("Calendar is null", calendar);
        List<CalendarComponent> comps = calendar.getComponents(Component.VEVENT);
        assertTrue("VEVENT not found", comps.size() == 1);
        VEvent vevent = (VEvent) comps.get(0);

        DtStart dtstart = vevent.getStartDate();
        DateTime dateTime = (DateTime) dtstart.getDate();

        assertEquals("date value not correct", "20080624T130000", dtstart
                .getValue());
        assertNotNull("timezone not present", dateTime.getTimeZone());
        assertEquals("timezone not correct",
                "/softwarestudio.org/Tzfile/America/Chicago", dateTime
                        .getTimeZone().getID());

    }

    public void testTwoDaylights() throws IOException, ParserException {

        System.setProperty("net.fortuna.ical4j.timezone.utcDefault", "true");

        String ical = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Test - ECPv4.9.9//NONSGML v1.0//EN\n" +
                "CALSCALE:GREGORIAN\n" +
                "METHOD:PUBLISH\n" +
                "BEGIN:VTIMEZONE\n" +
                "TZID:Europe/Amsterdam\n" +
                "BEGIN:DAYLIGHT\n" +
                "TZOFFSETFROM:+0100\n" +
                "TZOFFSETTO:+0200\n" +
                "TZNAME:CEST\n" +
                "DTSTART:20190331T010000\n" +
                "END:DAYLIGHT\n" +
                "BEGIN:STANDARD\n" +
                "TZOFFSETFROM:+0200\n" +
                "TZOFFSETTO:+0100\n" +
                "TZNAME:CET\n" +
                "DTSTART:20191027T010000\n" +
                "END:STANDARD\n" +
                "BEGIN:DAYLIGHT\n" +
                "TZOFFSETFROM:+0100\n" +
                "TZOFFSETTO:+0200\n" +
                "TZNAME:CEST\n" +
                "DTSTART:20200329T010000\n" +
                "END:DAYLIGHT\n" +
                "BEGIN:STANDARD\n" +
                "TZOFFSETFROM:+0200\n" +
                "TZOFFSETTO:+0100\n" +
                "TZNAME:CET\n" +
                "DTSTART:20201025T010000\n" +
                "END:STANDARD\n" +
                "END:VTIMEZONE\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART;TZID=Europe/Amsterdam:20200503T173000\n" +
                "DTEND;TZID=Europe/Amsterdam:20200503T200000\n" +
                "DTSTAMP:20191006T163046\n" +
                "CREATED:20190924T180719Z\n" +
                "LAST-MODIFIED:20191006T154131Z\n" +
                "SUMMARY:Test summary\n" +
                "DESCRIPTION:Test description \\n\n" +
                "END:VEVENT\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART;TZID=Europe/Amsterdam:20191006T190000\n" +
                "DTEND;TZID=Europe/Amsterdam:20191006T203000\n" +
                "DTSTAMP:20191006T163047\n" +
                "CREATED:20190912T190803Z\n" +
                "LAST-MODIFIED:20190918T193650Z\n" +
                "SUMMARY:Second test summary\n" +
                "DESCRIPTION:Second test description \\n\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";

        StringReader in = new StringReader(ical);
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = null;

        calendar = builder.build(in);
        assertNotNull("Calendar is null", calendar);
        List<CalendarComponent> comps = calendar.getComponents(Component.VEVENT);
        assertEquals("2 VEVENTs not found", 2, comps.size());
        VEvent vevent0 = (VEvent) comps.get(0);

        DtStart dtstart0 = vevent0.getStartDate();
        DateTime dateTime = (DateTime) dtstart0.getDate();

        assertEquals("date value not correct", "20200503T173000", dtstart0
                .getValue());
        assertNotNull("timezone not present", dateTime.getTimeZone());
        assertEquals("timezone not correct",
                "Europe/Amsterdam", dateTime
                        .getTimeZone().getID());

        DtEnd dtend0 = vevent0.getEndDate();
        dateTime = (DateTime) dtend0.getDate();
         assertEquals("date value not correct", "20200503T200000", dtend0
                .getValue());
        assertNotNull("timezone not present", dateTime.getTimeZone());
        assertEquals("timezone not correct",
                "Europe/Amsterdam", dateTime
                        .getTimeZone().getID());

        VEvent vevent1 = (VEvent) comps.get(1);
        DtStart dtstart1 = vevent1.getStartDate();
        dateTime = (DateTime) dtstart1.getDate();

        assertEquals("date value not correct", "20191006T190000", dtstart1
                .getValue());
        assertNotNull("timezone not present", dateTime.getTimeZone());
        assertEquals("timezone not correct",
                "Europe/Amsterdam", dateTime
                        .getTimeZone().getID());

        DtEnd dtend1 = vevent1.getEndDate();
        dateTime = (DateTime) dtend1.getDate();
         assertEquals("date value not correct", "20191006T203000", dtend1
                .getValue());
        assertNotNull("timezone not present", dateTime.getTimeZone());
        assertEquals("timezone not correct",
                "Europe/Amsterdam", dateTime
                        .getTimeZone().getID());

    }
}
