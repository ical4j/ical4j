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
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import net.fortuna.ical4j.validate.property.CalendarPropertyValidators;

import static net.fortuna.ical4j.model.property.immutable.ImmutableCalScale.GREGORIAN;

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

    public static final String VALUE_GREGORIAN = "GREGORIAN";

    private String value;

    /**
     * Default constructor.
     */
    public CalScale() {
        super(CALSCALE);
    }

    /**
     * @param aValue a value string for this component
     */
    public CalScale(final String aValue) {
        super(CALSCALE);
        this.value = aValue;
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public CalScale(final ParameterList aList, final String aValue) {
        super(CALSCALE, aList);
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
    public ValidationResult validate() throws ValidationException {
        return CalendarPropertyValidators.CALSCALE.validate(this);
    }

    @Override
    protected PropertyFactory<CalScale> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<CalScale> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(CALSCALE);
        }

        @Override
        public CalScale createProperty(final ParameterList parameters, final String value) {

            if (parameters.getAll().isEmpty() && VALUE_GREGORIAN.equalsIgnoreCase(value)) {
                return GREGORIAN;
            }
            return new CalScale(parameters, value);
        }

        @Override
        public CalScale createProperty() {
            return new CalScale();
        }
    }
}
