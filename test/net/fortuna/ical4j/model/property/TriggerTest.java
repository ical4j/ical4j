/*
 * Created on 7/03/2005
 *
 * $Id$
 *
 * Copyright (c) 2005, Ben Fortuna
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
