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

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationEntry;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import static org.junit.Assert.assertNotEquals;

/**
 * $Id$
 *
 * Created on 22/10/2006
 *
 * Unit tests for Property-specific functionality.
 * @author Ben Fortuna
 */
public class PropertyTest extends AbstractPropertyTest {

    private final Property property;

    private String expectedValue;

    /**
     * @param property
     */
    public PropertyTest(String testMethod, Property property) {
        super(testMethod);
        this.property = property;
    }

    /**
     * @param property
     * @param expectedValue
     */
    public PropertyTest(Property property, String expectedValue) {
        super("testGetValue");
        this.property = property;
        this.expectedValue = expectedValue;
    }

    /**
     * @param property
     * @param expectedValue
     */
    public PropertyTest(String testMethod, Property property, String expectedValue) {
        super(testMethod);
        this.property = property;
        this.expectedValue = expectedValue;
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }

    /**
     * 
     */
    public void testGetValue() {
        assertEquals(expectedValue, property.getValue());
    }

    /**
     *
     */
    public void testToString() {
        assertEquals(expectedValue, property.toString());
    }

    /**
     * Test equality of properties.
     */
    public void testEquals() {
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

        assertNotEquals("Properties are equal", property, notEqual);
        assertNotEquals("Properties are equal", notEqual, property);
    }

    /**
     * Test deep copy of properties.
     */
    public void testCopy() throws IOException, URISyntaxException {
        Property copy = property.copy();
        assertEquals(property, copy);

        copy.add(Value.BOOLEAN);
        assertNotEquals(property, copy);
        assertNotEquals(copy, property);
    }

    /**
     * @throws ValidationException
     */
    public final void testValidation() throws ValidationException {
        property.validate();
    }

    /**
     * @throws ValidationException
     */
    public final void testRelaxedValidation() throws ValidationException {
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        property.validate();
    }

    /**
     * 
     */
    public final void testValidationException() {
        try {
            ValidationResult result = property.validate();
//            fail("Should throw ValidationException");
            assertTrue(result.hasErrors());
        }
        catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    @SuppressWarnings("serial")
	public void testImmutable() throws IOException, URISyntaxException {
        try {
            property.setValue("");
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }

        try {
            property.add(new Parameter("name") {
                @Override
                public String getValue() {
                    return null;
                }
            });
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
    }

    /**
     * @return
     */
    public static TestSuite suite() throws Exception {
        TestSuite suite = new TestSuite();

        @SuppressWarnings("serial")
		Property property = new Property("name", new ParameterList()) {
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

        @SuppressWarnings("serial")
		Property invalidProperty = new Property("name", new ParameterList()) {
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
        suite.addTest(new PropertyTest("testEquals", property));
        suite.addTest(new PropertyTest(property, "value"));
        suite.addTest(new PropertyTest("testValidation", property));
        suite.addTest(new PropertyTest("testValidationException", invalidProperty));

        return suite;
    }
}
