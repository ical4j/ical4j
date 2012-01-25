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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$
 *
 * Created on 12/11/2005
 *
 * Unit tests for <code>Component</code> base class.
 * @author Ben Fortuna
 */
public class ComponentTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(ComponentTest.class);

    protected Component component;
    
    private Period period;
    
    private PeriodList expectedPeriods;
    
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
    public ComponentTest(String testMethod, Component component, Period period, PeriodList expectedPeriods) {
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
        PeriodList periods = component.calculateRecurrenceSet(period);
        assertEquals("Wrong number of periods", expectedPeriods.size(), periods.size());
        assertEquals(expectedPeriods, periods);
    }
    
    /**
     * @return
     */
    public static TestSuite suite() throws ValidationException, ParseException, IOException, URISyntaxException, ParserException  {
        TestSuite suite = new TestSuite();
        
        Component component = new Component("test") {
            public void validate(boolean recurse) throws ValidationException {
            }
        };
        suite.addTest(new ComponentTest("testCalculateRecurrenceSet", component, new Period(new DateTime(), new Dur(1, 0, 0, 0)), new PeriodList()));
        
        component = new Component("test") {
            public void validate(boolean recurse) throws ValidationException {
            }
        };
        // 10am-12pm for 7 days..
        component.getProperties().add(new DtStart("20080601T100000Z"));
        component.getProperties().add(new DtEnd("20080601T120000Z"));
        Recur recur = new Recur(Recur.DAILY, 7);
        component.getProperties().add(new RRule(recur));
        PeriodList expectedPeriods = new PeriodList();
        expectedPeriods.add(new Period("20080601T100000Z/PT2H"));
        expectedPeriods.add(new Period("20080602T100000Z/PT2H"));
        expectedPeriods.add(new Period("20080603T100000Z/PT2H"));
        expectedPeriods.add(new Period("20080604T100000Z/PT2H"));
        expectedPeriods.add(new Period("20080605T100000Z/PT2H"));
        expectedPeriods.add(new Period("20080606T100000Z/PT2H"));
        expectedPeriods.add(new Period("20080607T100000Z/PT2H"));
        suite.addTest(new ComponentTest("testCalculateRecurrenceSet", component, new Period(new DateTime("20080601T000000Z"), new Dur(7, 0, 0, 0)), expectedPeriods));

        component = new Component("test") {
            public void validate(boolean recurse) throws ValidationException {
            }
        };
        // weekly for 5 instances using DATE format and due date.
        component.getProperties().add(new DtStart(new Date("20080601")));
        component.getProperties().add(new Due(new Date("20080602")));
        recur = new Recur(Recur.WEEKLY, 5);
        component.getProperties().add(new RRule(recur));
        expectedPeriods = new PeriodList();
        expectedPeriods.add(new Period("20080601T000000Z/P1D"));
        expectedPeriods.add(new Period("20080608T000000Z/P1D"));
        expectedPeriods.add(new Period("20080615T000000Z/P1D"));
        expectedPeriods.add(new Period("20080622T000000Z/P1D"));
        expectedPeriods.add(new Period("20080629T000000Z/P1D"));
        suite.addTest(new ComponentTest("testCalculateRecurrenceSet", component, new Period(new DateTime("20080601T000000Z"), new Dur(6)), expectedPeriods));
        return suite;
    }
}
