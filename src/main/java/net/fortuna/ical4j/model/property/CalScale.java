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

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a CALSCALE iCalendar property.
 *
 * @author benf
 */
public class CalScale extends Property {

    private static final long serialVersionUID = 7446184786984981423L;

    /**
     * Constant for Gregorian calendar representation.
     */
    public static final CalScale GREGORIAN = new ImmutableCalScale("GREGORIAN");

    /**
     * @author Ben Fortuna An immutable instance of CalScale.
     */
    private static final class ImmutableCalScale extends CalScale {

        private static final long serialVersionUID = 1750949550694413878L;

        /**
         * @param value
         */
        private ImmutableCalScale(final String value) {
            super(new ParameterList(true), value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setValue(final String aValue) {
            throw new UnsupportedOperationException(
                    "Cannot modify constant instances");
        }
    }

    private String value;

    /**
     * Default constructor.
     */
    public CalScale() {
        super(CALSCALE, new Factory());
    }

    /**
     * @param aValue a value string for this component
     */
    public CalScale(final String aValue) {
        super(CALSCALE, new Factory());
        this.value = aValue;
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public CalScale(final ParameterList aList, final String aValue) {
        super(CALSCALE, aList, new Factory());
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final String aValue) {
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate() throws ValidationException {
        if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            if (!GREGORIAN.getValue().equalsIgnoreCase(value)) {
                throw new ValidationException("Invalid value [" + value + "]");
            }
        } else {
            if (!GREGORIAN.getValue().equals(value)) {
                throw new ValidationException("Invalid value [" + value + "]");
            }
        }
    }

    public static class Factory extends Content.Factory implements PropertyFactory<CalScale> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(CALSCALE);
        }

        @Override
        public CalScale createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {

            CalScale calScale;
            if (GREGORIAN.getValue().equals(value)) {
                calScale = GREGORIAN;
            } else {
                calScale = new CalScale(parameters, value);
            }
            return calScale;
        }

        @Override
        public CalScale createProperty() {
            return new CalScale();
        }
    }

}
