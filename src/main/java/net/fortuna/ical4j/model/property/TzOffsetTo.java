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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.UtcOffset;
import net.fortuna.ical4j.model.ValidationException;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a TZOFFSETTO iCalendar component property.
 * @author benf
 */
public class TzOffsetTo extends Property {

    private static final long serialVersionUID = 8213874575051177732L;

    private UtcOffset offset;

    /**
     * Default constructor.
     */
    public TzOffsetTo() {
        super(TZOFFSETTO, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param value an offset value
     */
    public TzOffsetTo(String value) {
        super(TZOFFSETTO, PropertyFactoryImpl.getInstance());
        setValue(value);
    }
    
    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public TzOffsetTo(final ParameterList aList, final String aValue) {
        super(TZOFFSETTO, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param anOffset a timezone offset in milliseconds
     */
    public TzOffsetTo(final UtcOffset anOffset) {
        super(TZOFFSETTO, PropertyFactoryImpl.getInstance());
        offset = anOffset;
    }

    /**
     * @param aList a list of parameters for this component
     * @param anOffset a timezone offset in milliseconds
     */
    public TzOffsetTo(final ParameterList aList, final UtcOffset anOffset) {
        super(TZOFFSETTO, aList, PropertyFactoryImpl.getInstance());
        offset = anOffset;
    }

    /**
     * @return Returns the offset.
     */
    public final UtcOffset getOffset() {
        return offset;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        offset = new UtcOffset(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        if (offset != null) {
            return offset.toString();
        }
        return "";
    }

    /**
     * @param offset The offset to set.
     */
    public final void setOffset(final UtcOffset offset) {
        this.offset = offset;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
