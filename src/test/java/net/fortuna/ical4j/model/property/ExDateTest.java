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
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * $Id$
 *
 * Created on 10/12/2005
 *
 * Unit tests for the ExDate property.
 * @author Ben Fortuna
 */
public class ExDateTest {

    private static final Logger LOG = LoggerFactory.getLogger(ExDateTest.class);
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
    }
    
    /**
     * Ensure timezones are correctly parsed for this property.
     * @throws Exception
     */
    @Test
    public void testTimeZones() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(getClass().getResourceAsStream("/samples/valid/EXDATE.ics"));

        List<VEvent> event = calendar.getComponents(Component.VEVENT);
        List<Property> exdates = event.get(0).getProperties(Property.EXDATE);
        for (Property exDate : exdates) {
            assertTrue("This EXDATE should have a timezone", exDate.getParameter(Parameter.TZID).isPresent());
        }
    }

    @Test
    public void testDstOnlyVTimeZones() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();

        Calendar ical = builder.build(getClass().getResourceAsStream("/samples/valid/dst-only-vtimezone.ics"));
        List<VTimeZone> vTZ = ical.getComponents(VTimeZone.VTIMEZONE);

        String id = vTZ.get(0).getRequiredProperty(Property.TZID).getValue();
        assertEquals("Europe/Berlin", id);
        assertEquals(vTZ.get(0).getObservances().get(0),
                vTZ.get(0).getApplicableObservance(TemporalAdapter.parse("20180403T000000Z").getTemporal()));

        List<VEvent> vEvent = ical.getComponents(VEvent.VEVENT);
        DtStart<ZonedDateTime> start = vEvent.get(0).getRequiredProperty("DTSTART");
        assertEquals(1522738800000L, Instant.from(start.getDate()).toEpochMilli());
    }

    @Test
    public void testShouldPreserveUtcTimezoneForExDate() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(getClass().getResourceAsStream("/samples/valid/EXDATE-IN-UTC.ics"));

        List<VEvent> event = calendar.getComponents(Component.VEVENT);
        List<Property> exdates = event.get(0).getProperties(Property.EXDATE);
        for (Property exDate : exdates) {
            for (Instant dateEx : ((ExDate<Instant>) exDate).getDates()) {
                assertNotNull(dateEx);
            }
        }
    }
    
    /**
     * Allow date values by default if relaxed parsing enabled.
     */
    @Test
    @Ignore
    public void testRelaxedValidation() {
        ExDate<Instant> property = new ExDate<>(new ParameterList(), "20080315");
        try {
            ValidationResult result = property.validate();
            assertTrue(result.hasErrors());
        } catch (ValidationException pe) {
            LOG.trace("Caught exception: " + pe.getMessage());
        }
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        new ExDate<>(new ParameterList(), "20080315").validate();
    }
}
