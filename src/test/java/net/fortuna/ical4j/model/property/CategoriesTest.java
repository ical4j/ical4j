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
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestSuite;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.Calendars;

/**
 * $Id$
 *
 * Created on 17/03/2007
 *
 * Unit testing for {@link Categories}.
 * @author Ben Fortuna
 */
public class CategoriesTest extends PropertyTest {

    private Categories value;
    private int expectedCategories;


    /**
     * @param property
     * @param expectedValue
     */
    public CategoriesTest(Categories property, String expectedValue) {
        super(property, expectedValue);
    }

    /**
     * @param testMethod
     * @param property
     */
    public CategoriesTest(String testMethod, Categories property) {
        super(testMethod, property);
    }

    /**
     * @param testMethod
     * @param property
     */
    public CategoriesTest(String testMethod, Categories property, int expectedCategories) {
        super(testMethod, property);
        this.value = property;
        this.expectedCategories = expectedCategories;
    }

    /**
     * Test escaping of commas in categories.
     */
    public void testCommaEscaping() throws ValidationException, IOException,
            ParserException {
        Categories cat1 = new Categories("test1");
        Categories cat2 = new Categories("test2");
        Categories cat3 = new Categories("test1,test2,test 1\\,2\\,3");

        VEvent event = new VEvent();
        event.getProperties().add(cat1);
        event.getProperties().add(cat2);
        event.getProperties().add(cat3);

        Calendar calendar = new Calendar();
        calendar.getComponents().add(event);

        StringWriter tempOut = new StringWriter();
        CalendarOutputter cout = new CalendarOutputter(false);
        cout.output(calendar, tempOut);

        CalendarBuilder builder = new CalendarBuilder();
        calendar = builder.build(new StringReader(tempOut.getBuffer()
                .toString()));

        PropertyList categories = calendar.getComponent(Component.VEVENT)
                .getProperties(Property.CATEGORIES);

        assertEquals(cat1, categories.get(0));
        assertEquals(cat2, categories.get(1));
        assertEquals(cat3, categories.get(2));
        assertEquals(3, cat3.getCategories().size());
    }

    /**
     * Test escaping of commas in categories.
     */
    public void testCommaEscapingCount() throws ValidationException, IOException,
            ParserException {

        assertEquals(expectedCategories, value.getCategories().size());
    }

    /**
     * @return
     * @throws ValidationException
     * @throws IOException
     * @throws ParserException
     */
    public static TestSuite suite() throws IOException, ValidationException,
            ParserException {
        TestSuite suite = new TestSuite();
        String list = "one,two,three";
        Categories categories = new Categories(list);
        suite.addTest(new CategoriesTest(categories, list));

        // Test escaping of categories string representation..
        Calendar calendar = Calendars.load("etc/samples/valid/categories.ics");
        Categories orig = (Categories) calendar.getComponent(Component.VEVENT)
                .getProperty(Property.CATEGORIES);

        StringWriter tempOut = new StringWriter();
        CalendarOutputter cout = new CalendarOutputter();
        cout.output(calendar, tempOut);

        CalendarBuilder builder = new CalendarBuilder();
        calendar = builder.build(new StringReader(tempOut.getBuffer()
                .toString()));

        Categories copy = (Categories) calendar.getComponent(Component.VEVENT)
                .getProperty(Property.CATEGORIES);
        assertEquals(orig, copy);
        suite.addTest(new CategoriesTest(copy, orig.getValue()));

        // other tests..
        suite.addTest(new CategoriesTest("testCommaEscaping", null));
        suite.addTest(new CategoriesTest("testCommaEscapingCount", new Categories("a\\,b"), 1));
        suite.addTest(new CategoriesTest("testCommaEscapingCount", new Categories("a,b\\,c"), 2));

        return suite;
    }
}
