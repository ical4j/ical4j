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

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;

import static net.fortuna.ical4j.validate.Validator.assertFalse;

/**
 * $Id$ [15-May-2004]
 *
 * Defines methods for validating parameters and parameter
 * lists.
 * 
 * @author Ben Fortuna
 */
public final class ParameterValidator {

    public static final String ASSERT_NONE_MESSAGE = "Parameter [{0}] is not applicable";

    public static final String ASSERT_ONE_OR_LESS_MESSAGE = "Parameter [{0}] must only be specified once";

    public static final String ASSERT_ONE_MESSAGE = "Parameter [{0}] must be specified once";

    private static final String ASSERT_NULL_OR_EQUAL_MESSAGE = "Parameter [{0}] is invalid";

    /**
     * Constructor made private to enforce static nature.
     */
    private ParameterValidator() {
    }

    /**
     * Ensure a parameter occurs no more than once.
     *
     * @param paramName
     *            the parameter name
     * @param parameters
     *            a list of parameters to query
     * @throws ValidationException
     *             when the specified parameter occurs more than once
     */
    public static void assertOneOrLess(final String paramName, final ParameterList parameters)
            throws ValidationException {

        assertFalse(parameters1 -> parameters1.getParameters(paramName).size() > 1, ASSERT_ONE_OR_LESS_MESSAGE, false,
                parameters, paramName);
    }

    /**
     * Ensure a parameter occurs once.
     *
     * @param paramName
     *            the parameter name
     * @param parameters
     *            a list of parameters to query
     * @throws ValidationException
     *             when the specified parameter does not occur once
     */
    public static void assertOne(final String paramName, final ParameterList parameters) throws ValidationException {
        assertFalse(parameters1 -> parameters1.getParameters(paramName).size() != 1, ASSERT_ONE_MESSAGE, false,
                parameters, paramName);
    }
    
    /**
     * Ensure a parameter doesn't occur in the specified list.
     * @param paramName the name of a parameter
     * @param parameters a list of parameters
     * @throws ValidationException thrown when the specified property
     * is found in the list of properties
     */
    public static void assertNone(final String paramName, final ParameterList parameters) throws ValidationException {
        assertFalse(parameters1 -> parameters1.getParameter(paramName) != null, ASSERT_NONE_MESSAGE, false,
                parameters, paramName);
    }

    /**
     * @param param a parameter instance
     * @param parameters a list of parameters
     * @throws ValidationException where the assertion fails
     */
    public static void assertNullOrEqual(final Parameter param, final ParameterList parameters) throws ValidationException {
        final Parameter p = parameters.getParameter(param.getName());
        if (p != null && !param.equals(p)) {
            throw new ValidationException(ASSERT_NULL_OR_EQUAL_MESSAGE, new Object[] {p});
        }
    }
}
