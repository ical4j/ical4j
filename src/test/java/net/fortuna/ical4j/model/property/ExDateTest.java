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
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    protected void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
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

        Optional<VEvent> event = calendar.getComponent(Component.VEVENT);
        List<Property> exdates = event.get().getProperties(Property.EXDATE);
        for (Property exDate : exdates) {
            assertTrue("This EXDATE should have a timezone", exDate.getParameter(Parameter.TZID).isPresent());
        }
    }
    
    public void testDstOnlyVTimeZones() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();

        Calendar ical = builder.build(getClass().getResourceAsStream("/samples/valid/dst-only-vtimezone.ics"));
        Optional<VTimeZone> vTZ = ical.getComponent(VTimeZone.VTIMEZONE);

        String id = vTZ.get().getProperty(Property.TZID).get().getValue();
        assertEquals("Europe/Berlin", id);
        assertEquals(vTZ.get().getObservances().get(0), vTZ.get().getApplicableObservance(TemporalAdapter.parse("20180403").getTemporal()));

        Optional<VEvent> vEvent = ical.getComponent(VEvent.VEVENT);
        Optional<DtStart<?>> start = vEvent.get().getStartDate();
        Optional<TzId> startTzId = start.get().getParameter(Parameter.TZID);
        assertTrue(startTzId.equals(vTZ.get().getTimeZoneId().get().getParameter(Parameter.TZID)));
        assertEquals(1522738800000L, Instant.from(start.get().getDate()).toEpochMilli());
    }

    public void testShouldPreserveUtcTimezoneForExDate() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(getClass().getResourceAsStream("/samples/valid/EXDATE-IN-UTC.ics"));

        Optional<VEvent> event = calendar.getComponent(Component.VEVENT);
        List<Property> exdates = event.get().getProperties(Property.EXDATE);
        for (Property exDate : exdates) {
            for (Instant dateEx : ((ExDate<Instant>) exDate).getDates().getDates()) {
                assertNotNull(dateEx);
            }
        }
    }
    
    /**
     * Allow date values by default if relaxed parsing enabled.
     */
    public void testRelaxedParsing() throws DateTimeParseException {
        try {
            new ExDate(new ArrayList<>(), "20080315");
            fail("Should throw DateTimeParseException");
        } catch (DateTimeParseException pe) {
            LOG.trace("Caught exception: " + pe.getMessage());
        }
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        new ExDate(new ArrayList<>(), "20080315");
    }
}
