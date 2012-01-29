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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fortuna.ical4j.model.ComponentTest;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.util.CompatibilityHints;

/**
 * @author Ben
 *
 */
public class CalendarComponentTest extends ComponentTest {

    private static final Log LOG = LogFactory.getLog(CalendarComponentTest.class);

    private CalendarComponent component;
    
    /**
     * @param testMethod
     * @param component
     */
    public CalendarComponentTest(String testMethod, CalendarComponent component) {
        super(testMethod, component);
        this.component = component;
    }

    /**
     * @param testMethod
     * @param component
     * @param period
     * @param expectedPeriods
     */
    public CalendarComponentTest(String testMethod, CalendarComponent component,
            Period period, PeriodList expectedPeriods) {
        super(testMethod, component, period, expectedPeriods);
        this.component = component;
    }
    
    /**
     * Test component iTIP METHOD validation.
     */
    public final void testPublishValidation() throws ValidationException {
        component.validate(Method.PUBLISH);
    }
    
    /**
     * Test component iTIP METHOD validation.
     */
    public final void testPublishRelaxedValidation() throws ValidationException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        component.validate(Method.PUBLISH);
    }
    
    /**
     * 
     */
    public final void testPublishValidationException() {
        try {
            component.validate(Method.PUBLISH);
            fail("ValidationException should be thrown!");
        }
        catch (ValidationException ve) {
            LOG.debug("Exception caught", ve);
        }
    }
    
    /**
     * Test component iTIP METHOD validation.
     */
    public final void testRequestValidation() throws ValidationException {
        component.validate(Method.PUBLISH);
    }
    
    /**
     * Test component iTIP METHOD validation.
     */
    public final void testRequestRelaxedValidation() throws ValidationException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        component.validate(Method.PUBLISH);
    }
    
    /**
     * 
     */
    public final void testRequestValidationException() {
        try {
            component.validate(Method.PUBLISH);
            fail("ValidationException should be thrown!");
        }
        catch (ValidationException ve) {
            LOG.debug("Exception caught", ve);
        }
    }

    /**
     * Test component iTIP METHOD validation.
     */
    public final void testReplyValidation() throws ValidationException {
        component.validate(Method.REPLY);
    }

    /**
     * Test component iTIP METHOD validation.
     */
    public final void testReplyRelaxedValidation() throws ValidationException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        component.validate(Method.REPLY);
    }

    /**
     *
     */
    public final void testReplyValidationException() {
        try {
            component.validate(Method.REPLY);
            fail("ValidationException should be thrown!");
        }
        catch (ValidationException ve) {
            LOG.debug("Exception caught", ve);
        }
    }

}
