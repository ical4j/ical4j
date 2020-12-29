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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a REPEAT iCalendar component property.
 *
 * @author benf
 */
public class Repeat extends Property {

    private static final long serialVersionUID = -1765522613173314831L;

    private int count;

    /**
     * Default constructor.
     */
    public Repeat() {
        super(REPEAT, new Factory());
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Repeat(final ParameterList aList, final String aValue) {
        super(REPEAT, aList, new Factory());
        setValue(aValue);
    }

    /**
     * @param aCount a repetition count
     */
    public Repeat(final int aCount) {
        super(REPEAT, new Factory());
        count = aCount;
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aCount a repetition count
     */
    public Repeat(final ParameterList aList, final int aCount) {
        super(REPEAT, aList, new Factory());
        count = aCount;
    }

    /**
     * @return Returns the count.
     */
    public final int getCount() {
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setValue(final String aValue) {
        count = Integer.parseInt(aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return String.valueOf(getCount());
    }

    /**
     * @param count The count to set.
     */
    public final void setCount(final int count) {
        this.count = count;
    }

    @Override
    public void validate() throws ValidationException {

    }

    public static class Factory extends Content.Factory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(REPEAT);
        }

        @Override
        public Property createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Repeat(parameters, value);
        }

        @Override
        public Property createProperty() {
            return new Repeat();
        }
    }

}
