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
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * $Id$
 * <p/>
 * Created: [Apr 14, 2004]
 * <p/>
 * Defines a FREEBUSY iCalendar component property.
 * <p/>
 * <pre>
 *     4.8.2.6 Free/Busy Time
 *
 *        Property Name: FREEBUSY
 *
 *        Purpose: The property defines one or more free or busy time
 *        intervals.
 *
 *        Value Type: PERIOD. The date and time values MUST be in an UTC time
 *        format.
 *
 *        Property Parameters: Non-standard or free/busy time type property
 *        parameters can be specified on this property.
 *
 *        Conformance: The property can be specified in a &quot;VFREEBUSY&quot; calendar
 *        component.
 *
 *        Property Parameter: &quot;FBTYPE&quot; and non-standard parameters can be
 *        specified on this property.
 *
 *        Description: These time periods can be specified as either a start
 *        and end date-time or a start date-time and duration. The date and
 *        time MUST be a UTC time format.
 *
 *        &quot;FREEBUSY&quot; properties within the &quot;VFREEBUSY&quot; calendar component
 *        SHOULD be sorted in ascending order, based on start time and then end
 *        time, with the earliest periods first.
 *
 *        The &quot;FREEBUSY&quot; property can specify more than one value, separated by
 *        the COMMA character (US-ASCII decimal 44). In such cases, the
 *        &quot;FREEBUSY&quot; property values SHOULD all be of the same &quot;FBTYPE&quot;
 *        property parameter type (e.g., all values of a particular &quot;FBTYPE&quot;
 *        listed together in a single property).
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          freebusy   = &quot;FREEBUSY&quot; fbparam &quot;:&quot; fbvalue
 *                       CRLF
 *
 *          fbparam    = *(
 *                     ; the following is optional,
 *                     ; but MUST NOT occur more than once
 *
 *                     (&quot;;&quot; fbtypeparam) /
 *
 *                     ; the following is optional,
 *                     ; and MAY occur more than once
 *
 *                     (&quot;;&quot; xparam)
 *
 *                     )
 *
 *          fbvalue    = period *[&quot;,&quot; period]
 *          ;Time value MUST be in the UTC time format.
 * </pre>
 *
 * @author Ben Fortuna
 */
public class FreeBusy extends Property {

    private static final long serialVersionUID = -6415954847619338567L;

    private PeriodList<Instant> periods;

    /**
     * Default constructor.
     */
    public FreeBusy() {
        super(FREEBUSY);
        periods = new PeriodList<>();
    }

    /**
     * @param aValue a freebusy value
     */
    public FreeBusy(final String aValue) {
        super(FREEBUSY);
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public FreeBusy(final ParameterList aList, final String aValue) {
        super(FREEBUSY, aList);
        setValue(aValue);
    }

    /**
     * @param pList a list of periods
     */
    public FreeBusy(final List<Period<Instant>> pList) {
        super(FREEBUSY);
        periods = new PeriodList<>(pList);
    }

    /**
     * @param aList a list of parameters for this component
     * @param pList a list of periods
     */
    public FreeBusy(final ParameterList aList, final List<Period<Instant>> pList) {
        super(FREEBUSY, aList);
        periods = new PeriodList<>(pList);
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {

        /*
         * ; the following is optional, ; but MUST NOT occur more than once (";" fbtypeparam) /
         */
        ParameterValidator.assertOneOrLess(Parameter.FBTYPE, getParameters().getAll());

        /*
         * ; the following is optional, ; and MAY occur more than once (";" xparam)
         */
    }

    /**
     * @return Returns the periods.
     */
    public final List<Period<Instant>> getPeriods() {
        return new ArrayList<>(periods.getPeriods());
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        periods = PeriodList.parse(aValue, CalendarDateFormat.UTC_DATE_TIME_FORMAT);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return periods.toString();
    }

    @Override
    protected PropertyFactory<FreeBusy> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<FreeBusy> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(FREEBUSY);
        }

        public FreeBusy createProperty(final ParameterList parameters, final String value) {
            return new FreeBusy(parameters, value);
        }

        public FreeBusy createProperty() {
            return new FreeBusy();
        }
    }

}
