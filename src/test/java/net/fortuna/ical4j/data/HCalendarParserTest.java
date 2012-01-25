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

import java.io.FileReader;
import java.io.IOException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.Calendars;
import junit.framework.TestCase;

/**
 * $Id$
 *
 * Created on 18/11/2007
 *
 * @author fortuna
 *
 */
public class HCalendarParserTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for {@link net.fortuna.ical4j.data.HCalendarParser#parse(java.io.Reader, net.fortuna.ical4j.data.ContentHandler)}.
     */
    public void testParseReaderContentHandler() throws IOException, ParserException {
        Calendar icsCalendar = Calendars.load("etc/samples/hcalendar/example1.ics");
        // remove prod-id which seems to be not handled by hcalendar..
        icsCalendar.getProperties().remove(icsCalendar.getProperty(Property.PRODID));
        
        CalendarBuilder builder = new CalendarBuilder(new HCalendarParser());
        Calendar hcalCalendar = builder.build(new FileReader("etc/samples/hcalendar/example1.html"));
        
//        assertEquals(icsCalendar, hcalCalendar);
        assertEquals(icsCalendar.getProperties().size(), hcalCalendar.getProperties().size());
        assertEquals(icsCalendar.getComponents().size(), hcalCalendar.getComponents().size());
    }

}
