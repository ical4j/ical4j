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
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * $Id$
 *
 * Created on 16/11/2008
 *
 * @author Ben
 *
 */
public class RequestStatusTest {

    @ParameterizedTest(name = "getValue")
    @MethodSource("getValueData")
    public void testGetValue(Property property, String expectedValue) {
        PropertyTest.assertGetValue(property, expectedValue);
    }

    @ParameterizedTest(name = "validation")
    @MethodSource("validationData")
    public void testValidation(Property property) throws ValidationException {
        PropertyTest.assertValidation(property);
    }

    @ParameterizedTest(name = "equals")
    @MethodSource("equalsData")
    public void testEquals(Property property) {
        PropertyTest.assertPropertyEquals(property);
    }

    /**
     * Example from RFC 5545 section 3.8.8.3: the description and extension data are TEXT values.
     */
    @Test
    public void testSetValueUnescapesComponents() {
        RequestStatus status = new RequestStatus(new ParameterList(),
                "2.8;Success\\, repeating event ignored. Scheduled as a single event.;RRULE:FREQ=WEEKLY\\;INTERVAL=2");
        assertEquals("2.8", status.getStatusCode());
        assertEquals("Success, repeating event ignored. Scheduled as a single event.", status.getDescription());
        assertEquals("RRULE:FREQ=WEEKLY;INTERVAL=2", status.getExData());
    }

    @Test
    public void testToStringEscapesLineBreaks() {
        RequestStatus status = new RequestStatus("3.1", "Invalid property value",
                "SUMMARY:x\r\nATTENDEE:mailto:mallory@example.com");
        assertEquals("REQUEST-STATUS:3.1;Invalid property value;SUMMARY:x\\nATTENDEE:mailto:mallory@example.com\r\n",
                status.toString());
    }

    /**
     * The status code is not escaped on output, so a line break in it would start new content lines.
     */
    @ParameterizedTest(name = "status code with a line break is rejected: {0}")
    @MethodSource("lineBreakStatusCodes")
    public void testStatusCodeLineBreakIsRejected(String statusCode) {
        assertThrows(IllegalArgumentException.class, () -> new RequestStatus(statusCode, "", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new RequestStatus(new ParameterList(), statusCode, "", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new RequestStatus(new ParameterList(), statusCode + ";Success"));
        RequestStatus status = newStatus();
        assertThrows(IllegalArgumentException.class, () -> status.setStatusCode(statusCode));
        assertThrows(IllegalArgumentException.class, () -> status.setValue(statusCode + ";Success"));
        assertEquals(RequestStatus.SUCCESS, status.getStatusCode());
    }

    static Stream<String> lineBreakStatusCodes() {
        return Stream.of("\rRRULE:FREQ=DAILY", "2.0\r\nATTENDEE:mailto:x@example.com", "2.0\nX-INJECTED:1",
                "2.0\r\n END:VEVENT\n");
    }

    @Test
    public void testStatusCodeLineBreakDoesNotAddContentLines() throws Exception {
        Calendar calendar = new CalendarBuilder().build(new StringReader(
                "BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//test//EN\r\nBEGIN:VEVENT\r\nUID:1\r\n"
                + "DTSTAMP:20240101T000000Z\r\nDTSTART:20240102T100000Z\r\nEND:VEVENT\r\nEND:VCALENDAR\r\n"));
        VEvent event = calendar.<VEvent>getComponent(Component.VEVENT).orElseThrow();
        assertThrows(IllegalArgumentException.class, () -> event.add(new RequestStatus("\rRRULE:FREQ=DAILY", "", "")));

        event.add(new RequestStatus("2.0", "Success", null));
        StringWriter out = new StringWriter();
        new CalendarOutputter(false).output(calendar, out);
        VEvent again = new CalendarBuilder().build(new StringReader(out.toString()))
                .<VEvent>getComponent(Component.VEVENT).orElseThrow();
        assertTrue(again.getProperties(Property.RRULE).isEmpty());
        assertEquals("2.0", again.<RequestStatus>getRequiredProperty(Property.REQUEST_STATUS).getStatusCode());
    }

    private static RequestStatus newStatus() {
        return new RequestStatus(RequestStatus.SUCCESS, null, null);
    }

    static Stream<Arguments> getValueData() {
        return Stream.of(
                Arguments.of(newStatus(), RequestStatus.SUCCESS),
                Arguments.of(new RequestStatus("2.8", "Success, repeating event ignored. Scheduled as a single event.",
                        "RRULE:FREQ=WEEKLY;INTERVAL=2"),
                        "2.8;Success\\, repeating event ignored. Scheduled as a single event.;RRULE:FREQ=WEEKLY\\;INTERVAL=2")
        );
    }

    static Stream<Arguments> validationData() {
        return Stream.of(
                Arguments.of(newStatus())
        );
    }

    static Stream<Arguments> equalsData() {
        return Stream.of(
                Arguments.of(newStatus())
        );
    }
}
