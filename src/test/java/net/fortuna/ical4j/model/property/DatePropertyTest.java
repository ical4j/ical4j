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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.DefaultTimeZoneRegistryFactory;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.model.TimeZoneRegistry;

/**
 * $Id$
 *
 * Created on 31/03/2007
 *
 * Unit tests specific to {@link DateProperty} and its subclasses.
 * @author Ben
 */
public class DatePropertyTest extends PropertyTest {

    private DateProperty property;

    /**
     * @param property
     * @param expectedValue
     */
    public DatePropertyTest(DateProperty property, String expectedValue) {
        super(property, expectedValue);
    }

    /**
     * @param testMethod
     * @param property
     */
    public DatePropertyTest(String testMethod, DateProperty property) {
        super(testMethod, property);
        this.property = property;
    }

    /**
     * 
     */
    public void testCopy() throws IOException, URISyntaxException,
            ParseException {
        Property copy = property.copy();
        assertEquals(property, copy);
        if (property.getTimeZone() != null) {
            assertEquals(property.getTimeZone(), ((DateProperty) copy).getTimeZone());
        }
        else {
            assertNull(((DateProperty) copy).getTimeZone());
        }
    }

    /**
     * @return
     */
    public static TestSuite suite() {
        TimeZoneRegistry tzReg = DefaultTimeZoneRegistryFactory.getInstance()
                .createRegistry();

        TestSuite suite = new TestSuite();
        DtStamp dtStamp = new DtStamp();
        // dtStamp.getParameters().add(new TzId("Australia/Melbourne"));
        // dtStamp.setTimeZone(tzReg.getTimeZone("Australia/Melbourne"));
        suite.addTest(new DatePropertyTest("testCopy", dtStamp));

        DtStart dtStart = new DtStart(new DateTime());
        // dtStart.getParameters().add(new TzId("Australia/Melbourne"));
        dtStart.setTimeZone(tzReg.getTimeZone("Australia/Melbourne"));
        suite.addTest(new DatePropertyTest("testCopy", dtStart));
        return suite;
    }
}
