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

import junit.framework.TestCase;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;

/**
 * $Id$
 *
 * Created on 10/12/2005
 *
 * Unit tests for the ExDate property.
 * @author Ben Fortuna
 */
public class ExDateTest extends TestCase {

    private static Logger LOG = LoggerFactory.getLogger(ExDateTest.class);
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
    }
    
    /**
     * Ensure timezones are correctly parsed for this property.
     * @throws Exception
     */
    public void testTimeZones() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(getClass().getResourceAsStream("/samples/valid/EXDATE.ics"));
        
        Component event = calendar.getComponent(Component.VEVENT);
        List<ExDate> exdates = event.getProperties(Property.EXDATE);
        for (ExDate exDate : exdates) {            
            assertNotNull("This EXDATE should have a timezone", exDate.getDates().getTimeZone());
        }
    }
    
    public void testDstOnlyVTimeZones() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();

        Calendar ical = builder.build(getClass().getResourceAsStream("/samples/valid/dst-only-vtimezone.ics"));
        VTimeZone vTZ = (VTimeZone) ical.getComponent(VTimeZone.VTIMEZONE);

        String id = vTZ.getTimeZoneId().getValue();
        assertEquals("Europe/Berlin", id);
        assertEquals(vTZ.getObservances().get(0), vTZ.getApplicableObservance(new Date("20180403")));

        VEvent vEvent = (VEvent) ical.getComponent(VEvent.VEVENT);
        DtStart start = vEvent.getStartDate();
        assertEquals(vTZ, start.getTimeZone().getVTimeZone());
        assertEquals(1522738800000L, start.getDate().getTime());
    }

    public void testShouldPreserveUtcTimezoneForExDate() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(getClass().getResourceAsStream("/samples/valid/EXDATE-IN-UTC.ics"));

        Component event = calendar.getComponent(Component.VEVENT);
        List<ExDate> exdates = event.getProperties(Property.EXDATE);
        for (ExDate exDate : exdates) {            
            for (Date dateEx : exDate.getDates()) {
                DateTime dateTimeEx = (DateTime) dateEx;
                assertNotNull(dateTimeEx);
                assertTrue("This exception date should be in UTC", dateTimeEx.isUtc());
            }
        }
    }
    
    /**
     * Allow date values by default if relaxed parsing enabled.
     */
    public void testRelaxedParsing() throws ParseException {
        try {
            new ExDate(new ParameterList(), "20080315");
            fail("Should throw ParseException");
        } catch (ParseException pe) {
            LOG.trace("Caught exception: " + pe.getMessage());
        }
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        new ExDate(new ParameterList(), "20080315");
    }
}
