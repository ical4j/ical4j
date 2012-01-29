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
package net.fortuna.ical4j.model.component;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;


/**
 *
 */
public class VAvailabilityTestCase extends TestCase
{
    public void testVAvailability() throws ParserException, IOException
    {
        CalendarBuilder calendarBuilder = new CalendarBuilder();
        String availability = getVAvailabilityICal();
        Reader reader = new StringReader(availability);
        Calendar calendar = calendarBuilder.build(reader);
        Component availabilityComponent = calendar.getComponent(Component.VAVAILABILITY);
        Assert.assertNotNull(availabilityComponent);
        Assert.assertFalse(((VAvailability) availabilityComponent).getAvailable().isEmpty());
        String iCalString = calendar.toString();
        Assert.assertTrue(iCalString.contains("BEGIN:AVAILABLE"));
        Assert.assertEquals(iCalString.trim(), availability);
    }

    private String getVAvailabilityICal()
    {
        return "BEGIN:VCALENDAR\r\n"
               + "CALSCALE:GREGORIAN\r\n"
               + "PRODID:-//example.com//iCalendar 2.0//EN\r\n"
               + "VERSION:2.0\r\n"
               + "BEGIN:VTIMEZONE\r\n"
               + "LAST-MODIFIED:20040110T032845Z\r\n"
               + "TZID:America/Montreal\r\n"
               + "BEGIN:DAYLIGHT\r\n"
               + "DTSTART:20000404T020000\r\n"
               + "RRULE:FREQ=YEARLY;BYMONTH=4;BYDAY=1SU\r\n"
               + "TZNAME:EDT\r\n"
               + "TZOFFSETFROM:-0500\r\n"
               + "TZOFFSETTO:-0400\r\n"
               + "END:DAYLIGHT\r\n"
               + "BEGIN:STANDARD\r\n"
               + "DTSTART:20001026T020000\r\n"
               + "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU\r\n"
               + "TZNAME:EST\r\n"
               + "TZOFFSETFROM:-0400\r\n"
               + "TZOFFSETTO:-0500\r\n"
               + "END:STANDARD\r\n"
               + "END:VTIMEZONE\r\n"
               + "BEGIN:VAVAILABILITY\r\n"
               + "UID:7661631d-05a1-43e3-9779-3638fbd778aa\r\n"
               + "DTSTAMP:20111005T133225Z\r\n"
               + "DTSTART;TZID=America/Montreal:20111002T000000\r\n"
               + "BEGIN:AVAILABLE\r\n"
               + "UID:7661631d-05a1-43e3-9779-3638fbd778aa\r\n"
               + "SUMMARY:Monday to Friday from 9:00 to 18:00\r\n"
               + "DTSTART;TZID=America/Montreal:20111002T090000\r\n"
               + "DTEND;TZID=America/Montreal:20111002T180000\r\n"
               + "RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR\r\n"
               + "END:AVAILABLE\r\n"
               + "END:VAVAILABILITY\r\n"
               + "END:VCALENDAR";
    }
}
