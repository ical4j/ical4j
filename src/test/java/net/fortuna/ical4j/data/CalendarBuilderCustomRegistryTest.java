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
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.util.Strings;

import java.io.StringReader;
import java.net.URISyntaxException;

/**
 * $Id: CalendarBuilderCustomRegistryTest.java [Nov 16, 2009]
 *
 * Test case for CalendarBuilder with custom property and parameter factories.
 *
 * @author arnaudq
 */
public class CalendarBuilderCustomRegistryTest extends TestCase {

    private static final String SCHEDULE_STATUS = "SCHEDULE-STATUS-1";

    private static final String VEVENT_WITH_SCHEDULE_STATUS =
            "BEGIN:VCALENDAR\r\n"
             + "PRODID:-//Sample/Calendar//EN\r\n"
             + "VERSION:2.0\r\n"
             + "BEGIN:VEVENT\r\n"
             + "UID:12\r\n"
             + "DTSTAMP:20071212T121212Z\r\n"
             + "ORGANIZER:mailto:org@example.com\r\n"
             + "ATTENDEE;SCHEDULE-STATUS-1=2.0:mailto:attendee@example.com\r\n"
             + "END:VEVENT\r\n"
             + "END:VCALENDAR";


    @SuppressWarnings("serial")
	private static class ScheduleStatus extends Parameter {

        private String value;

        public ScheduleStatus(String aValue, ParameterFactory factory) {
            super(SCHEDULE_STATUS, factory);
            value = Strings.unquote(aValue);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final String getValue() {
            return value;
        }
    }

    /**
     * Test the calendar builder with a custom parameters.
     */
    public void testCustomParameter() throws Exception {

        // try to build with a regular builder
        CalendarBuilder builder = new CalendarBuilder();
        Calendar cal = builder.build(new StringReader(VEVENT_WITH_SCHEDULE_STATUS));
        assertTrue(cal.getComponent(Component.VEVENT).getProperty(Property.ATTENDEE)
                .getParameter(SCHEDULE_STATUS) instanceof XParameter);

        // try to build with a custom parameter factory
        final ParameterFactoryRegistry paramFactory = new ParameterFactoryRegistry();
        paramFactory.register(SCHEDULE_STATUS,
            new ParameterFactory() {
                static final long serialVersionUID = 8871483730211383100L;

                @Override
                public boolean supports(String name) {
                    return SCHEDULE_STATUS.equals(name);
                }

                @Override
                public Parameter createParameter(final String value) throws URISyntaxException {
                    return new ScheduleStatus(value, this);
                }
            });
        builder = new CalendarBuilder(
                CalendarParserFactory.getInstance().get(),
                new PropertyFactoryRegistry(),
                paramFactory,
                TimeZoneRegistryFactory.getInstance().createRegistry());

        cal = builder.build(new StringReader(VEVENT_WITH_SCHEDULE_STATUS));

        VEvent event = (VEvent)cal.getComponent(Component.VEVENT);
        VEvent eventBis = (VEvent)event.copy();
        assertEquals(eventBis, event);
    }
}
