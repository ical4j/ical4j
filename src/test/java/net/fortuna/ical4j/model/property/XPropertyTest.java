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

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.stream.Stream;

/**
 * $Id$
 *
 * Created on 26/06/2007
 *
 * @author Ben
 *
 */
public class XPropertyTest {

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

    @ParameterizedTest(name = "validationException")
    @MethodSource("validationExceptionData")
    public void testValidationException(Property property) {
        PropertyTest.assertValidationException(property);
    }

    @ParameterizedTest(name = "relaxedValidation")
    @MethodSource("relaxedValidationData")
    public void testRelaxedValidation(Property property) throws ValidationException {
        PropertyTest.assertRelaxedValidation(property);
    }

    @ParameterizedTest(name = "toString")
    @MethodSource("toStringData")
    public void testToString(Property property, String expectedValue) {
        PropertyTest.assertToString(property, expectedValue);
    }

    private static XProperty newTestProperty(String value) {
        XProperty p = new XProperty("TEST");
        p.setValue(value);
        return p;
    }

    private static XProperty newXTestProperty() {
        return new XProperty("X-TEST");
    }

    private static XProperty newTestUriProperty() {
        return new XProperty("TEST", new ParameterList(Collections.singletonList(Value.URI)),
                "geo:37.331684,-122.030758");
    }

    static Stream<Arguments> getValueData() {
        return Stream.of(
                Arguments.of(newTestProperty("value"), "value"),
                Arguments.of(newTestProperty("geo:37.331684,-122.030758"), "geo:37.331684,-122.030758"),
                Arguments.of(newTestUriProperty(), "geo:37.331684,-122.030758")
        );
    }

    static Stream<Arguments> validationData() {
        return Stream.of(
                Arguments.of(newXTestProperty())
        );
    }

    static Stream<Arguments> validationExceptionData() {
        return Stream.of(
                Arguments.of(newTestProperty("value"))
        );
    }

    static Stream<Arguments> relaxedValidationData() {
        return Stream.of(
                Arguments.of(newTestProperty("value"))
        );
    }

    static Stream<Arguments> toStringData() {
        return Stream.of(
                Arguments.of(newTestProperty("geo:37.331684,-122.030758"), "TEST:geo:37.331684\\,-122.030758\r\n"),
                Arguments.of(newTestUriProperty(), "TEST;VALUE=URI:geo:37.331684,-122.030758\r\n")
        );
    }
}
