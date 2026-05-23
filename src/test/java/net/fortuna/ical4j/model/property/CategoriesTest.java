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
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * $Id$
 *
 * Created on 17/03/2007
 *
 * Unit testing for {@link Categories}.
 * @author Ben Fortuna
 */
public class CategoriesTest {

    @ParameterizedTest(name = "getValue")
    @MethodSource("getValueData")
    public void testGetValue(Property property, String expectedValue) {
        PropertyTest.assertGetValue(property, expectedValue);
    }

    /**
     * Test escaping of commas in categories.
     */
    @Test
    public void testCommaEscaping() throws ValidationException, IOException,
            ParserException, ConstraintViolationException {
        Categories cat1 = new Categories("test1");
        Categories cat2 = new Categories("test2");
        Categories cat3 = new Categories("test1,test2,test 1\\,2\\,3");

        var event = (VEvent) new VEvent().withProperty(cat1).withProperty(cat2).withProperty(cat3).getFluentTarget();

        Calendar calendar = new Calendar(new ComponentList<>(Collections.singletonList(event)));

        StringWriter tempOut = new StringWriter();
        CalendarOutputter cout = new CalendarOutputter(false);
        cout.output(calendar, tempOut);

        CalendarBuilder builder = new CalendarBuilder();
        calendar = builder.build(new StringReader(tempOut.getBuffer()
                .toString()));

        List<Property> categories = calendar.getComponents(Component.VEVENT)
                .get(0).getProperties(Property.CATEGORIES);

        assertEquals(cat1, categories.get(0));
        assertEquals(cat2, categories.get(1));
        assertEquals(cat3, categories.get(2));
        assertEquals(3, cat3.getCategories().getTexts().size());
    }

    /**
     * Test escaping of commas in categories - assert size.
     */
    @ParameterizedTest(name = "commaEscapingCount")
    @MethodSource("commaEscapingCountData")
    public void testCommaEscapingCount(Categories value, int expectedCategories)
            throws ValidationException, IOException, ParserException {
        assertEquals(expectedCategories, value.getCategories().getTexts().size());
    }

    static Stream<Arguments> getValueData() throws IOException, ValidationException,
            ParserException, ConstraintViolationException {
        Stream.Builder<Arguments> rows = Stream.builder();

        String list = "one,two,three";
        Categories categories = new Categories(list);
        rows.add(Arguments.of(categories, list));

        // Test escaping of categories string representation..
        Calendar calendar = Calendars.load(CategoriesTest.class.getResource("/samples/valid/categories.ics"));
        Categories orig = calendar.getComponents(Component.VEVENT).get(0).getRequiredProperty(Property.CATEGORIES);

        StringWriter tempOut = new StringWriter();
        CalendarOutputter cout = new CalendarOutputter();
        cout.output(calendar, tempOut);

        CalendarBuilder builder = new CalendarBuilder();
        calendar = builder.build(new StringReader(tempOut.getBuffer()
                .toString()));

        Categories copy = calendar.getComponents(Component.VEVENT).get(0).getRequiredProperty(Property.CATEGORIES);
        assertEquals(orig, copy);
        rows.add(Arguments.of(copy, orig.getValue()));

        return rows.build();
    }

    static Stream<Arguments> commaEscapingCountData() {
        return Stream.of(
                Arguments.of(new Categories("a\\,b"), 1),
                Arguments.of(new Categories("a,b\\,c"), 2)
        );
    }
}
