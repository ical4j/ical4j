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

import java.text.ParseException;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.ParameterValidator;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a EXDATE iCalendar component property.
 * @author benf
 */
public class ExDate extends DateListProperty {

    private static final long serialVersionUID = 2635730172243974463L;

    /**
     * Default constructor.
     */
    public ExDate() {
        super(EXDATE, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     * @throws ParseException where the specified value string is not a valid date-time/date representation
     */
    public ExDate(final ParameterList aList, final String aValue)
            throws ParseException {
        super(EXDATE, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param dList a list of dates
     */
    public ExDate(final DateList dList) {
        super(EXDATE, dList, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aList a list of parameters for this component
     * @param dList a list of dates
     */
    public ExDate(final ParameterList aList, final DateList dList) {
        super(EXDATE, aList, dList, PropertyFactoryImpl.getInstance());
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {

        /*
         * ; the following are optional, ; but MUST NOT occur more than once (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
         * (";" tzidparam) /
         */
        ParameterValidator.getInstance().assertOneOrLess(Parameter.VALUE,
                getParameters());

        final Parameter valueParam = getParameter(Parameter.VALUE);

        if (valueParam != null && !Value.DATE_TIME.equals(valueParam)
                && !Value.DATE.equals(valueParam)) {
            throw new ValidationException("Parameter [" + Parameter.VALUE
                    + "] is invalid");
        }

        ParameterValidator.getInstance().assertOneOrLess(Parameter.TZID,
                getParameters());

        /*
         * ; the following is optional, ; and MAY occur more than once (";" xparam)
         */
    }
}
