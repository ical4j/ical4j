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
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

import java.time.temporal.Temporal;
import java.util.Optional;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a EXDATE iCalendar component property.
 *
 * @author benf
 */
public class ExDate<T extends Temporal> extends DateListProperty<T> {

    /**
     * Default constructor.
     */
    public ExDate() {
        super(EXDATE);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public ExDate(final ParameterList aList, final String aValue) {
        super(EXDATE, aList, Value.DATE_TIME);
        setValue(aValue);
    }

    /**
     * @param dList a list of dates
     */
    public ExDate(final DateList<T> dList) {
        super(EXDATE, dList);
    }

    /**
     * @param aList a list of parameters for this component
     * @param dList a list of dates
     */
    public ExDate(final ParameterList aList, final DateList<T> dList) {
        super(EXDATE, aList, dList, Value.DATE_TIME);
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        super.validate();

        /*
         * ; the following are optional, ; but MUST NOT occur more than once (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
         * (";" tzidparam) /
         */
        ParameterValidator.assertOneOrLess(Parameter.VALUE, getParameters().getAll());

        final Optional<Parameter> valueParam = getParameters().getFirst(Parameter.VALUE);

        if (valueParam.isPresent() && !Value.DATE_TIME.equals(valueParam.get())
                && !Value.DATE.equals(valueParam.get())) {
            throw new ValidationException("Parameter [" + Parameter.VALUE
                    + "] is invalid");
        }

        ParameterValidator.assertOneOrLess(Parameter.TZID, getParameters().getAll());

        /*
         * ; the following is optional, ; and MAY occur more than once (";" xparam)
         */
    }

    @Override
    protected PropertyFactory<ExDate<T>> newFactory() {
        return new Factory<>();
    }

    public static class Factory<T extends Temporal> extends Content.Factory implements PropertyFactory<ExDate<T>> {

        public Factory() {
            super(EXDATE);
        }

        public ExDate<T> createProperty(final ParameterList parameters, final String value) {
            return new ExDate<>(parameters, value);
        }

        public ExDate<T> createProperty() {
            return new ExDate<>();
        }
    }

}
