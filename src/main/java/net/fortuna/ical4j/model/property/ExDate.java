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

import java.text.ParseException;
import java.time.temporal.Temporal;
import java.util.List;
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

    private static final long serialVersionUID = 2635730172243974463L;

    /**
     * Default constructor.
     */
    public ExDate() {
        super(EXDATE, new Factory());
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     * @throws ParseException where the specified value string is not a valid date-time/date representation
     */
    public ExDate(final List<Parameter> aList, final String aValue)
            throws ParseException {
        super(EXDATE, aList, new Factory());
        setValue(aValue);
    }

    /**
     * @param dList a list of dates
     */
    public ExDate(final DateList<T> dList) {
        super(EXDATE, dList, new Factory());
    }

    /**
     * @param aList a list of parameters for this component
     * @param dList a list of dates
     */
    public ExDate(final List<Parameter> aList, final DateList<T> dList) {
        super(EXDATE, aList, dList, new Factory());
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {

        /*
         * ; the following are optional, ; but MUST NOT occur more than once (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
         * (";" tzidparam) /
         */
        ParameterValidator.assertOneOrLess(Parameter.VALUE,
                getParameters());

        final Optional<Parameter> valueParam = getParameter(Parameter.VALUE);

        if (valueParam.isPresent() && !Value.DATE_TIME.equals(valueParam.get())
                && !Value.DATE.equals(valueParam.get())) {
            throw new ValidationException("Parameter [" + Parameter.VALUE
                    + "] is invalid");
        }

        ParameterValidator.assertOneOrLess(Parameter.TZID,
                getParameters());

        /*
         * ; the following is optional, ; and MAY occur more than once (";" xparam)
         */
    }

    @Override
    public Property copy() throws ParseException {
        return new Factory().createProperty(getParameters(), getValue());
    }

    public static class Factory extends Content.Factory implements PropertyFactory<ExDate> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(EXDATE);
        }

        public ExDate createProperty(final List<Parameter> parameters, final String value) throws ParseException {
            return new ExDate(parameters, value);
        }

        public ExDate createProperty() {
            return new ExDate();
        }
    }

}
