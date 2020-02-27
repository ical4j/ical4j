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
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.util.List;

import static net.fortuna.ical4j.validate.Validator.assertFalse;

/**
 * $Id$ [15-May-2004]
 *
 * Defines methods for validating properties and property lists.
 *
 * @author Ben Fortuna
 */
public final class PropertyValidator implements Validator<Property> {

    public static final String ASSERT_NONE_MESSAGE = "Property [{0}] is not applicable";

    public static final String ASSERT_ONE_OR_LESS_MESSAGE = "Property [{0}] must only be specified once";

    public static final String ASSERT_ONE_MESSAGE = "Property [{0}] must be specified once";

    public static final String ASSERT_ONE_OR_MORE_MESSAGE = "Property [{0}] must be specified at least once";

    private final List<ValidationRule> rules;

    public PropertyValidator(List<ValidationRule> rules) {
        this.rules = rules;
    }

    @Override
    public void validate(Property target) throws ValidationException {
        for (ValidationRule rule : rules) {
            boolean warnOnly = CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
                    && rule.isRelaxedModeSupported();

            switch (rule.getType()) {
                case None:
                    rule.getInstances().forEach(s -> assertFalse(input -> input.getParameter(s).isPresent(),
                            ParameterValidator.ASSERT_NONE_MESSAGE, warnOnly, target.getParameters(), s));
                    break;
                case One:
                    rule.getInstances().forEach(s -> assertFalse(input -> input.getParameters(s).size() != 1,
                            ParameterValidator.ASSERT_ONE_MESSAGE, warnOnly, target.getParameters(), s));
                    break;
                case OneOrLess:
                    rule.getInstances().forEach(s -> assertFalse(input -> input.getParameters(s).size() > 1,
                            ParameterValidator.ASSERT_ONE_OR_LESS_MESSAGE, warnOnly, target.getParameters(), s));
                    break;
            }
        }
    }

    /**
     * Ensure a property occurs no more than once.
     *
     * @param propertyName
     *            the property name
     * @param properties
     *            a list of properties to query
     * @throws ValidationException
     *             when the specified property occurs more than once
     */
    public static void assertOneOrLess(final String propertyName, final PropertyList properties) throws ValidationException {
        assertFalse(input -> input.getProperties(propertyName).size() > 1, ASSERT_ONE_OR_LESS_MESSAGE, false,
                properties, propertyName);
    }

    /**
     * Ensure a property occurs at least once.
     *
     * @param propertyName
     *            the property name
     * @param properties
     *            a list of properties to query
     * @throws ValidationException
     *             when the specified property occurs more than once
     */
    public static void assertOneOrMore(final String propertyName, final PropertyList properties) throws ValidationException {
        assertFalse(input -> input.getProperties(propertyName).size() < 1, ASSERT_ONE_OR_MORE_MESSAGE, false,
                properties, propertyName);
    }

    /**
     * Ensure a property occurs once.
     *
     * @param propertyName
     *            the property name
     * @param properties
     *            a list of properties to query
     * @throws ValidationException
     *             when the specified property does not occur once
     */
    public static void assertOne(final String propertyName, final PropertyList properties) throws ValidationException {
        assertFalse(input -> input.getProperties(propertyName).size() != 1, ASSERT_ONE_MESSAGE, false,
                properties, propertyName);
    }
    
    /**
     * Ensure a property doesn't occur in the specified list.
     * @param propertyName the name of a property
     * @param properties a list of properties
     * @throws ValidationException thrown when the specified property
     * is found in the list of properties
     */
    public static void assertNone(final String propertyName, final PropertyList properties) throws ValidationException {
        assertFalse(input -> input.getProperty(propertyName) != null, ASSERT_NONE_MESSAGE, false,
                properties, propertyName);
    }
}
