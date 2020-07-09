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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
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

        VEvent event = calendar.getComponents().getRequired(Component.VEVENT);
        List<Property> exdates = event.getProperties().get(Property.EXDATE);
        for (Property exDate : exdates) {
            assertTrue("This EXDATE should have a timezone", exDate.getParameters().getFirst(Parameter.TZID).isPresent());
        }
    }
    
    public void testDstOnlyVTimeZones() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();

        Calendar ical = builder.build(getClass().getResourceAsStream("/samples/valid/dst-only-vtimezone.ics"));
        VTimeZone vTZ = ical.getComponents().getRequired(VTimeZone.VTIMEZONE);

        String id = vTZ.getProperties().getRequired(Property.TZID).getValue();
        assertEquals("Europe/Berlin", id);
        assertEquals(vTZ.getObservances().getAll().get(0),
                vTZ.getApplicableObservance(TemporalAdapter.parse("20180403T000000Z").getTemporal()));

        VEvent vEvent = ical.getComponents().getRequired(VEvent.VEVENT);
        DtStart<ZonedDateTime> start = vEvent.getProperties().getRequired("DTSTART");
        assertEquals(1522738800000L, Instant.from(start.getDate()).toEpochMilli());
    }

    public void testShouldPreserveUtcTimezoneForExDate() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(getClass().getResourceAsStream("/samples/valid/EXDATE-IN-UTC.ics"));

        VEvent event = calendar.getComponents().getRequired(Component.VEVENT);
        List<Property> exdates = event.getProperties().get(Property.EXDATE);
        for (Property exDate : exdates) {
            for (Instant dateEx : ((ExDate<Instant>) exDate).getDates()) {
                assertNotNull(dateEx);
            }
        }
    }
    
    /**
     * Allow date values by default if relaxed parsing enabled.
     */
    public void testRelaxedParsing() throws DateTimeParseException {
        try {
            ExDate<Instant> property = new ExDate<>(new ParameterList(), "20080315");
            property.getDates();
            fail("Should throw DateTimeParseException");
        } catch (DateTimeParseException pe) {
            LOG.trace("Caught exception: " + pe.getMessage());
        }
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        new ExDate(new ParameterList(), "20080315");
    }
}
