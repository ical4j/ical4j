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
 * Created on 01/03/2011
 *
 * Unit tests specific to {@link DateListProperty} and its subclasses.
 * @author arnaudq
 */
public class DateListPropertyTest extends PropertyTest {

    private DateListProperty property;

    /**
     * @param property
     * @param expectedValue
     */
    public DateListPropertyTest(DateListProperty property, String expectedValue) {
        super(property, expectedValue);
    }

    /**
     * @param testMethod
     * @param property
     */
    public DateListPropertyTest(String testMethod, DateListProperty property) {
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
            assertEquals(property.getTimeZone(), ((DateListProperty) copy).getTimeZone());
        }
        else {
            assertNull(((DateListProperty) copy).getTimeZone());
        }
    }

    /**
     * @return
     */
    public static TestSuite suite() throws ParseException {
        TimeZoneRegistry tzReg = DefaultTimeZoneRegistryFactory.getInstance()
                .createRegistry();

        TestSuite suite = new TestSuite();
        ExDate exZulu = new ExDate();
        exZulu.setValue("20111212T000000Z");
        suite.addTest(new DateListPropertyTest("testCopy", exZulu));

        ExDate exMelbourne = new ExDate();
        exMelbourne.setTimeZone(tzReg.getTimeZone("Australia/Melbourne"));
        exMelbourne.setValue("20111212T000000");

        suite.addTest(new DateListPropertyTest("testCopy", exMelbourne));
        return suite;
    }
}
