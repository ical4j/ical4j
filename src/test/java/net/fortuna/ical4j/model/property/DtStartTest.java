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

import java.text.ParseException;
import java.util.Calendar;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.TimeZones;

/**
 * $Id$
 *
 * Created on 10/12/2005
 *
 * @author fortuna
 *
 */
public class DtStartTest extends TestCase {

    private TimeZone timezone;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        TimeZoneRegistry tzReg = TimeZoneRegistryFactory.getInstance().createRegistry();
        timezone = tzReg.getTimeZone("Australia/Melbourne");
    }
    
    /*
     * Test method for 'net.fortuna.ical4j.model.property.DtStart.DtStart(String)'
     */
    public void testDtStartString() throws ParseException {
        ParameterList params = new ParameterList();
        params.add(Value.DATE);
        DtStart dtStart = new DtStart(params, "20060811");
        
        Calendar calendar = Calendar.getInstance(TimeZones.getDateTimeZone());
        calendar.clear();
        calendar.set(2006, 7, 11);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        
        assertEquals(dtStart.getDate(), calendar.getTime());
    }

    /**
     * Unit tests for timezone constructor.
     */
    public void testDtStartTimezone() throws ParseException {
        DtStart dtStart = new DtStart(timezone);

        dtStart.setValue(new DateTime().toString());
        assertEquals(timezone, dtStart.getTimeZone());

        // initialising with DATE value should reset timezone..
        dtStart.setDate(new Date());
        assertNull(dtStart.getTimeZone());
    }

    /**
     * Unit tests for value/timezone constructor.
     */
    public void testDtStartStringTimezone() throws ParseException {
        String value = new DateTime().toString();
        DtStart dtStart = new DtStart(value, timezone);

        assertEquals(timezone, dtStart.getTimeZone());
        assertEquals(value, dtStart.getValue());
    }
    
    /**
     * Test non-utc timezone works.
     */
    public void testNonUtcTimezone() throws ParseException {
        DtStart start = new DtStart();
        start.getParameters().add(Value.DATE_TIME);
        start.getParameters().add(new TzId("GMT"));
        start.setValue("20070101T080000");
        
        assertEquals("DTSTART;VALUE=DATE-TIME;TZID=GMT:20070101T080000" + Strings.LINE_SEPARATOR,
                start.toString());
    }
}
