/**
 * Copyright (c) 2010, Ben Fortuna
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

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;

import java.net.URISyntaxException;

/**
 * $Id: Rsvp.java,v 1.16 2010/03/06 12:57:25 fortuna Exp $ [18-Apr-2004]
 *
 * Defines an RSVP parameter.
 * @author benfortuna
 */
public class Derived extends Parameter {

    private static final long serialVersionUID = -5381653882942018012L;

    private static final String VALUE_TRUE = "TRUE";

    private static final String VALUE_FALSE = "FALSE";

    /**
     * Is a derived object.
     */
    public static final Derived TRUE = new Derived(VALUE_TRUE);

    /**
     * Is not a derived object.
     */
    public static final Derived FALSE = new Derived(VALUE_FALSE);

    private Boolean value;

    /**
     * @param aValue a string representation
     */
    public Derived(final String aValue) {
        this(Boolean.valueOf(aValue));
    }

    /**
     * @param aValue a boolean value
     */
    public Derived(final Boolean aValue) {
        super(DERIVED, new Factory());
        this.value = aValue;
    }

    /**
     * @return Returns the rsvp.
     */
    public final Boolean getDerived() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return getDerived().toString().toUpperCase();
    }

    public static class Factory extends Content.Factory
            implements ParameterFactory<Parameter> {
        private static final long serialVersionUID = 1L;
    
        public Factory() {
          super(DERIVED);
        }
    
        public Parameter createParameter(final String value)
                throws URISyntaxException {
            if (Boolean.TRUE.toString().equals(value)) {
                return Derived.TRUE;
            }

            if (Boolean.FALSE.toString().equals(value)) {
                return Derived.FALSE;
            }

            return new Derived(value);
        }
    }
}
