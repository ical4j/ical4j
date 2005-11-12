/*
 * $Id$
 * 
 * Created: [Apr 6, 2004]
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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;

/**
 * Defines a TRANSP iCalendar component property.
 *
 * @author benf
 */
public class Transp extends Property {
    
    private static final long serialVersionUID = 3801479657311785518L;

    public static final Transp OPAQUE = new ImmutableTransp("OPAQUE");

    public static final Transp TRANSPARENT = new ImmutableTransp("TRANSPARENT");
    
    /**
     * @author Ben Fortuna
     * An immutable instance of Transp.
     */
    private static final class ImmutableTransp extends Transp {
        
        private static final long serialVersionUID = -6595830107310111996L;
        
        /**
         * @param value
         */
        private ImmutableTransp(final String value) {
            super(new ParameterList(true), value);
        }
        
        /* (non-Javadoc)
         * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
         */
        public void setValue(final String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }

    private String value;

    /**
     * Default constructor.
     */
    public Transp() {
        super(TRANSP);
    }
    
    /**
     * @param aValue
     *            a value string for this component
     */
    public Transp(final String aValue) {
        super(TRANSP);
        this.value = aValue;
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param aValue
     *            a value string for this component
     */
    public Transp(final ParameterList aList, final String aValue) {
        super(TRANSP, aList);
        this.value = aValue;
    }
        
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public void setValue(final String aValue) {
        this.value = aValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public final String getValue() {
        return value;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#validate()
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
