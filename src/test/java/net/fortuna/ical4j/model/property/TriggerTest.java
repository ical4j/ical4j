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
import java.util.Date;

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.Value;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created on 7/03/2005
 *
 * $Id$
 *
 * @author Ben Fortuna
 *
 */
public class TriggerTest extends PropertyTest {
    
    private static Log log = LogFactory.getLog(TriggerTest.class);

    private Trigger trigger;
    
    /**
	 * @param property
	 * @param expectedValue
	 */
	public TriggerTest(Trigger property, String expectedValue) {
		super(property, expectedValue);
		this.trigger = property;
	}

	/**
	 * @param testMethod
	 * @param property
	 */
	public TriggerTest(String testMethod, Trigger property) {
		super(testMethod, property);
		this.trigger = property;
	}

	/**
     * @throws ParseException
     */
    public void testSetValue() throws ParseException {
        trigger.setValue(new DateTime(new Date(0).getTime()).toString());
        
        log.info(new DateTime(new Date(0).getTime()));
        log.info(trigger);

//        trigger.setValue(DurationFormat.getInstance().format(5000));
        trigger.setValue(new Dur(0, 0, 0, 5).toString());
        
//        log.info(DurationFormat.getInstance().format(5000));
        log.info(new Dur(0, 0, 0, 5));
        log.info(trigger);
    }

    /**
     * Unit test on a duration trigger.
     */
    public void testTriggerDuration() {
        assertNotNull(trigger.getDuration());
        assertNull(trigger.getDate());
        assertNull(trigger.getDateTime());
    }

    /**
     * Unit test on a date-time trigger.
     */
    public void testTriggerDateTime() throws ValidationException {
        assertNull(trigger.getDuration());
        assertNotNull(trigger.getDate());
        assertNotNull(trigger.getDateTime());
        trigger.validate();

        trigger.getParameters().add(Value.DURATION);
        assertValidationException(trigger);
    }
    
    /**
     * @return
     */
    public static TestSuite suite() {
    	TestSuite suite = new TestSuite();
        Trigger trigger = new Trigger();
    	suite.addTest(new TriggerTest("testSetValue", trigger));

    	trigger = new Trigger(new Dur(1, 0, 0, 0));
    	suite.addTest(new TriggerTest("testTriggerDuration", trigger));
        
    	trigger = new Trigger(new DateTime(new Date()));
    	suite.addTest(new TriggerTest("testTriggerDateTime", trigger));
        
    	return suite;
    }
}
