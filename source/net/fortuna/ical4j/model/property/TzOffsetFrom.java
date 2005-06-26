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
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
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
import net.fortuna.ical4j.model.UtcOffset;

/**
 * Defines a TZOFFSETFROM iCalendar component property.
 *
 * @author benf
 */
public class TzOffsetFrom extends Property {
    
    private static final long serialVersionUID = 450274263165493502L;

    private UtcOffset offset;

    /**
     * Default constructor.
     */
    public TzOffsetFrom() {
        super(TZOFFSETFROM);
    }
    
    /**
     * @param aValue
     *            a value string for this component
     */
    public TzOffsetFrom(final String aValue) {
        super(TZOFFSETFROM);
        setValue(aValue);
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param aValue
     *            a value string for this component
     */
    public TzOffsetFrom(final ParameterList aList, final String aValue) {
        super(TZOFFSETFROM, aList);
        setValue(aValue);
    }

    /**
     * @param anOffset
     *            a timezone offset in milliseconds
     */
    public TzOffsetFrom(final UtcOffset anOffset) {
        super(TZOFFSETFROM);
        offset = anOffset;
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param anOffset
     *            a timezone offset in milliseconds
     */
    public TzOffsetFrom(final ParameterList aList, final UtcOffset anOffset) {
        super(TZOFFSETFROM, aList);
        offset = anOffset;
    }

    /**
     * @return Returns the offset.
     */
    public final UtcOffset getOffset() {
        return offset;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public final void setValue(final String aValue) {
        offset = new UtcOffset(aValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Property#getValue()
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
}