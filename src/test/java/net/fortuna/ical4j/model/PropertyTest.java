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

import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationEntry;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * $Id$
 *
 * Created on 22/10/2006
 *
 * Unit tests for Property-specific functionality. Also provides static helper
 * assertions used by subclass tests (which no longer extend this class after
 * the JUnit 5 migration).
 * @author Ben Fortuna
 */
public class PropertyTest {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyTest.class);

    @AfterEach
    void tearDown() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }

    /**
     * Asserts that {@code property}'s {@link Property#getValue() value} matches
     * {@code expectedValue}.
     */
    public static void assertGetValue(Property property, String expectedValue) {
        assertEquals(expectedValue, property.getValue());
    }

    /**
     * Asserts that {@code property}'s {@link Property#toString() toString}
     * representation matches {@code expectedValue}.
     */
    public static void assertToString(Property property, String expectedValue) {
        assertEquals(expectedValue, property.toString());
    }

    /**
     * Asserts that {@code property} equals itself and not equal to a different mock property.
     */
    public static void assertPropertyEquals(Property property) {
        assertEquals(property, property);

        @SuppressWarnings("serial")
        Property notEqual = new Property("notEqual", new ParameterList()) {
            @Override
            public String getValue() {
                return "";
            }

            @Override
            public void setValue(String value) {
            }

            @Override
            public ValidationResult validate() throws ValidationException {
                return ValidationResult.EMPTY;
            }

            @Override
            protected PropertyFactory<?> newFactory() {
                return null;
            }
        };

        assertNotEquals(property, notEqual, "Properties are equal");
        assertNotEquals(notEqual, property, "Properties are equal");
    }

    /**
     * Asserts that a deep copy of {@code property} initially equals the original, and
     * that subsequent mutation of the copy results in inequality.
     */
    public static void assertCopy(Property property) throws IOException, URISyntaxException {
        Property copy = property.copy();
        assertEquals(property, copy);

        copy.add(Value.BOOLEAN);
        assertNotEquals(property, copy);
        assertNotEquals(copy, property);
    }

    /**
     * Asserts that {@code property} validates successfully.
     */
    public static void assertValidation(Property property) throws ValidationException {
        property.validate();
    }

    /**
     * Asserts that {@code property} validates successfully when relaxed validation
     * is enabled.
     */
    public static void assertRelaxedValidation(Property property) throws ValidationException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        try {
            property.validate();
        } finally {
            CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
        }
    }

    /**
     * Asserts that validating {@code property} either yields a result with errors,
     * or throws a {@link ValidationException}.
     */
    public static void assertValidationException(Property property) {
        try {
            ValidationResult result = property.validate();
            assertTrue(result.hasErrors());
        } catch (ValidationException e) {
            LOG.debug("Exception caught", e);
        }
    }

    /**
     * Asserts that {@code property} is immutable, i.e. mutation operations throw
     * {@link UnsupportedOperationException}.
     */
    @SuppressWarnings("serial")
    public static void assertImmutable(Property property) throws IOException, URISyntaxException {
        try {
            property.setValue("");
            fail("UnsupportedOperationException should be thrown");
        } catch (UnsupportedOperationException uoe) {
        }

        try {
            property.add(new Parameter("name") {
                @Override
                public String getValue() {
                    return null;
                }
            });
            fail("UnsupportedOperationException should be thrown");
        } catch (UnsupportedOperationException uoe) {
        }
    }

    @ParameterizedTest(name = "equals")
    @MethodSource("equalsData")
    public void testEquals(Property property) {
        assertPropertyEquals(property);
    }

    @ParameterizedTest(name = "getValue")
    @MethodSource("getValueData")
    public void testGetValue(Property property, String expectedValue) {
        assertGetValue(property, expectedValue);
    }

    @ParameterizedTest(name = "validation")
    @MethodSource("validationData")
    public void testValidation(Property property) throws ValidationException {
        assertValidation(property);
    }

    @ParameterizedTest(name = "validationException")
    @MethodSource("validationExceptionData")
    public void testValidationException(Property property) {
        assertValidationException(property);
    }

    @SuppressWarnings("serial")
    private static Property mockProperty() {
        return new Property("name", new ParameterList()) {
            @Override
            public String getValue() {
                return "value";
            }

            @Override
            public void setValue(String value) {
            }

            @Override
            public ValidationResult validate() throws ValidationException {
                return ValidationResult.EMPTY;
            }

            @Override
            protected PropertyFactory<?> newFactory() {
                return null;
            }
        };
    }

    @SuppressWarnings("serial")
    private static Property mockInvalidProperty() {
        return new Property("name", new ParameterList()) {
            @Override
            public String getValue() {
                return "value";
            }

            @Override
            public void setValue(String value) {
            }

            @Override
            public ValidationResult validate() throws ValidationException {
                return new ValidationResult(new ValidationEntry("Fail",
                        ValidationEntry.Severity.ERROR, getName()));
            }

            @Override
            protected PropertyFactory<?> newFactory() {
                return null;
            }
        };
    }

    static Stream<Arguments> equalsData() {
        return Stream.of(
                Arguments.of(mockProperty())
        );
    }

    static Stream<Arguments> getValueData() {
        return Stream.of(
                Arguments.of(mockProperty(), "value")
        );
    }

    static Stream<Arguments> validationData() {
        return Stream.of(
                Arguments.of(mockProperty())
        );
    }

    static Stream<Arguments> validationExceptionData() {
        return Stream.of(
                Arguments.of(mockInvalidProperty())
        );
    }
}
