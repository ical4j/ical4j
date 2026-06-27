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

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.Calendars;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * $Id$
 *
 * Created on 20/02/2006
 *
 * Unit tests for Location property.
 * @author Ben Fortuna
 */
public class LocationTest {

    @ParameterizedTest(name = "getValue")
    @MethodSource("getValueData")
    public void testGetValue(Property property, String expectedValue) {
        PropertyTest.assertGetValue(property, expectedValue);
    }

    /**
     * Test correct parsing of quoted text.
     */
    @Test
    public void testQuotedText() throws IOException, ParserException, ConstraintViolationException {
        Calendar calendar = Calendars.load(getClass().getResource("/samples/valid/mansour.ics"));
        List<VEvent> event = calendar.getComponents(Component.VEVENT);
        assertEquals("At \"The Terrace\" Complex > Melbourne \"\\,",
                event.get(0).getRequiredProperty(Property.LOCATION).getValue());
    }

    static Stream<Arguments> getValueData() throws IOException, ParserException, ConstraintViolationException {
        // testQuotedText..
        Calendar calendar = Calendars.load(LocationTest.class.getResource("/samples/valid/mansour.ics"));
        List<VEvent> event = calendar.getComponents(Component.VEVENT);
        Location location = event.get(0).getRequiredProperty(Property.LOCATION);
        return Stream.of(
                Arguments.of(location, "At \"The Terrace\" Complex > Melbourne \"\\,")
        );
    }
}
