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

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Encodable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;

/**
 * $Id$ [18-Apr-2004]
 * <p/>
 * Defines an RSVP parameter.
 *
 * @author benfortuna
 */
public class Rsvp extends Parameter implements Encodable {

    private static final long serialVersionUID = -5381653882942018012L;

    private static final String VALUE_TRUE = "TRUE";

    private static final String VALUE_FALSE = "FALSE";

    /**
     * RSVP is required.
     */
    public static final Rsvp TRUE = new Rsvp(VALUE_TRUE);

    /**
     * RSVP not required.
     */
    public static final Rsvp FALSE = new Rsvp(VALUE_FALSE);

    private final Boolean rsvp;

    /**
     * @param aValue a string representation of an RSVP
     */
    public Rsvp(final String aValue) {
        this(Boolean.valueOf(aValue));
    }

    /**
     * @param aValue a boolean value
     */
    public Rsvp(final Boolean aValue) {
        super(RSVP);
        this.rsvp = aValue;
    }

    /**
     * @return Returns the rsvp.
     */
    public final Boolean getRsvp() {
        return rsvp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        if (rsvp) {
            return VALUE_TRUE;
        } else {
            return VALUE_FALSE;
        }
    }

    public static class Factory extends Content.Factory implements ParameterFactory<Rsvp> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(RSVP);
        }

        @Override
        public Rsvp createParameter(final String value) {
            switch (value) {
                case VALUE_FALSE: return FALSE;
                case VALUE_TRUE: return TRUE;
            }
            return new Rsvp(value);
        }
    }

}
