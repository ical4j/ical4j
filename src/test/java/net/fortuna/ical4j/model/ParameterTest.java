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
package net.fortuna.ical4j.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created: [17/11/2008]
 *
 * Unit tests for {@link Parameter}. Also provides static helper assertions used
 * by subclass tests (which no longer extend this class after the JUnit 5
 * migration).
 *
 * @author fortuna
 *
 */
public class ParameterTest {

    /**
     * Asserts that {@code parameter}'s name matches {@code expectedName}.
     */
    public static void assertGetName(Parameter parameter, String expectedName) {
        assertEquals(expectedName, parameter.getName());
    }

    /**
     * Asserts that {@code parameter}'s value matches {@code expectedValue}.
     */
    public static void assertGetValue(Parameter parameter, String expectedValue) {
        assertEquals(expectedValue, parameter.getValue());
    }

    /**
     * Asserts that {@code parameter}'s {@link Parameter#toString()} representation
     * matches the expected {@code name=value} pair.
     */
    public static void assertToString(Parameter parameter, String expectedName, String expectedValue) {
        assertEquals(expectedName + "=" + expectedValue, parameter.toString());
    }

    @ParameterizedTest(name = "getName")
    @MethodSource("getNameData")
    public void testGetName(Parameter parameter, String expectedName) {
        assertGetName(parameter, expectedName);
    }

    @ParameterizedTest(name = "getValue")
    @MethodSource("getValueData")
    public void testGetValue(Parameter parameter, String expectedValue) {
        assertGetValue(parameter, expectedValue);
    }

    @ParameterizedTest(name = "toString")
    @MethodSource("toStringData")
    public void testToString(Parameter parameter, String expectedName, String expectedValue) {
        assertToString(parameter, expectedName, expectedValue);
    }

    @SuppressWarnings("serial")
    private static Parameter mockParameter() {
        return new Parameter("name") {
            @Override
            public String getValue() {
                return "value";
            }
        };
    }

    static Stream<Arguments> getNameData() {
        return Stream.of(
                Arguments.of(mockParameter(), "name")
        );
    }

    static Stream<Arguments> getValueData() {
        return Stream.of(
                Arguments.of(mockParameter(), "value")
        );
    }

    static Stream<Arguments> toStringData() {
        return Stream.of(
                Arguments.of(mockParameter(), "name", "value")
        );
    }
}
