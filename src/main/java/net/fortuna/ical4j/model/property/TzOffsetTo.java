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

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;

import java.time.ZoneOffset;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a TZOFFSETTO iCalendar component property.
 *
 * @author benf
 */
public class TzOffsetTo extends Property {

    private static final long serialVersionUID = 8213874575051177732L;

    private ZoneOffsetAdapter offset;

    /**
     * Default constructor.
     */
    public TzOffsetTo() {
        super(TZOFFSETTO);
    }

    /**
     * @param value an offset value
     */
    public TzOffsetTo(String value) {
        super(TZOFFSETTO);
        setValue(value);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public TzOffsetTo(final ParameterList aList, final String aValue) {
        super(TZOFFSETTO, aList);
        setValue(aValue);
    }

    /**
     * @param anOffset a timezone offset in milliseconds
     */
    @Deprecated
    public TzOffsetTo(final UtcOffset anOffset) {
        this(ZoneOffsetAdapter.from(anOffset));
    }

    /**
     * @param anOffset a timezone offset in milliseconds
     */
    public TzOffsetTo(final ZoneOffset anOffset) {
        super(TZOFFSETTO);
        offset = new ZoneOffsetAdapter(anOffset);
    }

    /**
     * @param aList    a list of parameters for this component
     * @param anOffset a timezone offset in milliseconds
     */
    @Deprecated
    public TzOffsetTo(final ParameterList aList, final UtcOffset anOffset) {
        this(aList, ZoneOffsetAdapter.from(anOffset));
    }

    /**
     * @param aList    a list of parameters for this component
     * @param anOffset a timezone offset in milliseconds
     */
    public TzOffsetTo(final ParameterList aList, final ZoneOffset anOffset) {
        super(TZOFFSETTO, aList);
        offset = new ZoneOffsetAdapter(anOffset);
    }

    /**
     * @return Returns the offset.
     */
    public final ZoneOffset getOffset() {
        return offset.getOffset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setValue(final String aValue) {
        offset = new ZoneOffsetAdapter(ZoneOffset.of(aValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        if (offset != null) {
            return offset.toString();
        }
        return "";
    }

    /**
     * @param offset The offset to set.
     */
    public final void setOffset(final ZoneOffset offset) {
        this.offset = new ZoneOffsetAdapter(offset);
    }

    @Override
    public void validate() throws ValidationException {

    }

    @Override
    protected PropertyFactory<TzOffsetTo> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<TzOffsetTo> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(TZOFFSETTO);
        }

        @Override
        public TzOffsetTo createProperty(final ParameterList parameters, final String value) {
            return new TzOffsetTo(parameters, value);
        }

        @Override
        public TzOffsetTo createProperty() {
            return new TzOffsetTo();
        }
    }

}
