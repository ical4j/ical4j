/*
 * $Id$ [15-May-2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.ValidationException;

/**
 * Defines methods for validating parameters and parameter
 * lists.
 * 
 * @author Ben Fortuna
 */
public final class ParameterValidator {

    private static ParameterValidator instance = new ParameterValidator();

    /**
     * Constructor made private to enforce singleton.
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
    public void assertOneOrLess(final String paramName,
            final ParameterList parameters) throws ValidationException {

        if (parameters.getParameters(paramName).size() > 1) {
            throw new ValidationException(
                "Parameter [" + paramName + "] must only be specified once");
        }
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
    public void assertOne(final String paramName,
            final ParameterList parameters) throws ValidationException {

        if (parameters.getParameters(paramName).size() != 1) {
            throw new ValidationException(
                "Parameter [" + paramName + "] must be specified once");
        }
    }
    
    /**
     * Ensure a parameter doesn't occur in the specified list.
     * @param propertyName the name of a property
     * @param properties a list of properties
     * @throws ValidationException thrown when the specified property
     * is found in the list of properties
     */
    public void assertNone(final String paramName, final ParameterList parameters) throws ValidationException {
        if (parameters.getParameters(paramName).size() > 0) {
            throw new ValidationException(
                "Parameter [" + paramName + "] is not applicable");
        }
    }

    /**
     * @return Returns the instance.
     */
    public static ParameterValidator getInstance() {
        return instance;
    }
}
