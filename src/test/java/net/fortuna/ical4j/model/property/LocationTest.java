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

import junit.framework.TestSuite;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.Calendars;

import java.io.IOException;
import java.util.List;

/**
 * $Id$
 *
 * Created on 20/02/2006
 *
 * Unit tests for Location property.
 * @author Ben Fortuna
 */
public class LocationTest extends PropertyTest {

    /**
	 * @param property
	 * @param expectedValue
	 */
	public LocationTest(Location property, String expectedValue) {
		super(property, expectedValue);
	}

	/**
	 * @param testMethod
	 * @param property
	 */
	public LocationTest(String testMethod, Location property) {
		super(testMethod, property);
	}

	/**
     * Test correct parsing of quoted text.
     * @throws IOException
     * @throws ParserException
     */
    public void testQuotedText() throws IOException, ParserException, ConstraintViolationException {
        Calendar calendar = Calendars.load(getClass().getResource("/samples/valid/mansour.ics"));
        List<VEvent> event = calendar.getComponents(Component.VEVENT);
        assertEquals("At \"The Terrace\" Complex > Melbourne \"\\,",
				event.get(0).getRequiredProperty(Property.LOCATION).getValue());
    }
    
    /**
     * @return
     * @throws ParserException 
     * @throws IOException 
     */
    public static TestSuite suite() throws IOException, ParserException, ConstraintViolationException {
    	TestSuite suite = new TestSuite();
    	//testQuotedText..
        Calendar calendar = Calendars.load(LocationTest.class.getResource("/samples/valid/mansour.ics"));
		List<VEvent> event = calendar.getComponents(Component.VEVENT);
        Location location = event.get(0).getRequiredProperty(Property.LOCATION);
        suite.addTest(new LocationTest(location, "At \"The Terrace\" Complex > Melbourne \"\\,"));
    	return suite;
    }
}
