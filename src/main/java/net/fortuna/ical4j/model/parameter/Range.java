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
package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Strings;

/**
 * $Id$ [18-Apr-2004]
 *
 * Defines a Recurrence Identifier Range parameter.
 * @author benfortuna
 */
public class Range extends Parameter {

    private static final long serialVersionUID = -3057531444558393776L;

    private static final String VALUE_THISANDPRIOR = "THISANDPRIOR";

    private static final String VALUE_THISANDFUTURE = "THISANDFUTURE";

    /**
     * This and prior.
     */
    public static final Range THISANDPRIOR = new Range(VALUE_THISANDPRIOR);

    /**
     * This and future.
     */
    public static final Range THISANDFUTURE = new Range(VALUE_THISANDFUTURE);

    private String value;

    /**
     * @param aValue a string representation of a recurrence identifier range
     */
    public Range(final String aValue) {
        super(RANGE, ParameterFactoryImpl.getInstance());
        this.value = Strings.unquote(aValue);

        // allow arbitrary ranges for Lotus Notes..
        // eg. X-LOTUS-RECURID;RANGE=ALL:20101006T203000Z
        
        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY)) {
            // value must be one of finite list..
            if (!VALUE_THISANDPRIOR.equals(value)
                    && !VALUE_THISANDFUTURE.equals(value)) {
                throw new IllegalArgumentException("Invalid value [" + value + "]");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }
}
