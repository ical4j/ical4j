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
package net.fortuna.ical4j.model;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

/**
 * $Id$
 *
 * Created on 12/11/2005
 *
 * Unit tests for <code>Component</code> base class.
 * @author Ben Fortuna
 */
public class ComponentTest<T extends Temporal> extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentTest.class);

    protected Component component;
    
    private Period<T> period;
    
    private List<Period<T>> expectedPeriods;
    
    /**
     * @param component
     */
    public ComponentTest(String testMethod, Component component) {
        super(testMethod);
    	this.component = component;
    }
    
    /**
     * @param testMethod
     * @param component
     * @param period
     * @param expectedPeriods
     */
    public ComponentTest(String testMethod, Component component, Period<T> period, List<Period<T>> expectedPeriods) {
        this(testMethod, component);
        this.period = period;
        this.expectedPeriods = expectedPeriods;
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }
    
    /**
     * Test whether the component is a calendar component.
     */
    public final void testIsCalendarComponent() {
        assertTrue("Component is not a calendar component", (component instanceof CalendarComponent));
    }
    
    /**
     * Test whether the component is a calendar component.
     */
    public final void testIsNotCalendarComponent() {
        assertFalse("Component is a calendar component", (component instanceof CalendarComponent));
    }
    
    /**
     * Test component validation.
     */
    public final void testValidation() throws ValidationException {
        component.validate();
    }
    
    /**
     * Test component validation.
     */
    public final void testRelaxedValidation() throws ValidationException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        component.validate();
    }
    
    /**
     * 
     */
    public final void testValidationException() {
        try {
            component.validate();
            fail("ValidationException should be thrown!");
        }
        catch (ValidationException ve) {
            LOG.debug("Exception caught", ve);
        }
    }
    
    public void testCalculateRecurrenceSet() {
        List<Period<T>> periods = component.calculateRecurrenceSet(period);
        assertEquals("Wrong number of periods", expectedPeriods.size(), periods.size());
        assertEquals(expectedPeriods, periods);
    }
    
    /**
     * @return
     */
    @SuppressWarnings("serial")
	public static TestSuite suite() throws ValidationException, ParseException, IOException, URISyntaxException, ParserException  {
        TestSuite suite = new TestSuite();
        
        Component component = new Component("test") {
            public void validate(boolean recurse) throws ValidationException {
            }

            @Override
            public Component copy() throws URISyntaxException, ParseException {
                return null;
            }
        };
        suite.addTest(new ComponentTest<>("testCalculateRecurrenceSet", component, new Period<>(LocalDate.now(),
                java.time.Period.ofDays(1)), new ArrayList<>()));
        
        component = new Component("test") {
            public void validate(boolean recurse) throws ValidationException {
            }
            @Override
            public Component copy() throws URISyntaxException, ParseException {
                return null;
            }
        };
        // 10am-12pm for 7 days..
        component.getProperties().add(new DtStart("20080601T100000Z"));
        component.getProperties().add(new DtEnd("20080601T120000Z"));
        Recur recur = new Recur.Builder().frequency(Recur.Frequency.DAILY).count(7).build();
        component.getProperties().add(new RRule(recur));
        List<Period<Instant>> expectedPeriods = new ArrayList<>();
        expectedPeriods.add(Period.parse("20080601T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080602T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080603T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080604T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080605T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080606T100000Z/PT2H"));
        expectedPeriods.add(Period.parse("20080607T100000Z/PT2H"));
        suite.addTest(new ComponentTest<>("testCalculateRecurrenceSet", component, new Period(TemporalAdapter.parse("20080601T000000Z").getTemporal(),
                java.time.Period.ofDays(7)), expectedPeriods));

        component = new Component("test") {
            public void validate(boolean recurse) throws ValidationException {
            }
            @Override
            public Component copy() throws URISyntaxException, ParseException {
                return null;
            }
        };
        // weekly for 5 instances using DATE format and due date.
        component.getProperties().add(new DtStart<>((LocalDate) TemporalAdapter.parse("20080601").getTemporal()));
        component.getProperties().add(new Due<>((LocalDate) TemporalAdapter.parse("20080602").getTemporal()));
        recur = new Recur.Builder().frequency(Recur.Frequency.WEEKLY).count(5).build();
        component.getProperties().add(new RRule(recur));
        List<Period<LocalDate>> expectedPeriods2 = new ArrayList<>();
        expectedPeriods2.add(Period.parse("20080601/P1D"));
        expectedPeriods2.add(Period.parse("20080608/P1D"));
        expectedPeriods2.add(Period.parse("20080615/P1D"));
        expectedPeriods2.add(Period.parse("20080622/P1D"));
        expectedPeriods2.add(Period.parse("20080629/P1D"));
        suite.addTest(new ComponentTest<>("testCalculateRecurrenceSet", component, new Period<>((LocalDate) TemporalAdapter.parse("20080601").getTemporal(),
                java.time.Period.ofWeeks(6)), expectedPeriods2));
        return suite;
    }
}
