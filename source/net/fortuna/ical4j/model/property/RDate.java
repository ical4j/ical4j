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

import java.text.ParseException;
import java.util.Date;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.DateFormat;
import net.fortuna.ical4j.util.DateTimeFormat;
import net.fortuna.ical4j.util.ParameterValidator;

/**
 * Defines an RDATE iCalendar component property.
 *
 * @author benf
 */
public class RDate extends Property {

    private Date time;

    private Period period;

    // default value determined through inspection
    // of iCal-generated files..
    private boolean utc = true;

    /**
     * Default constructor.
     */
    public RDate() {
        super(RDATE);
        time = new Date();
    }
    
    /**
     * @param aList
     *            a list of parameters for this component
     * @param aValue
     *            a value string for this component
     * @throws ParseException
     *             where the specified value string is not a valid
     *             date-time/date representation
     */
    public RDate(final ParameterList aList, final String aValue)
            throws ParseException {
        super(RDATE, aList);
        setValue(aValue);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence
     * of a VALUE parameter.
     * @param aDate
     *            a date representation of a date or date-time
     */
    public RDate(final Date aDate) {
        super(RDATE);
        time = aDate;
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence
     * of a VALUE parameter.
     *
     * @param aList
     *            a list of parameters for this component
     * @param aDate
     *            a date representation of a date or date-time
     */
    public RDate(final ParameterList aList, final Date aDate) {
        super(RDATE, aList);
        time = aDate;
    }

    /**
     * Constructor.
     * @param aPeriod
     *            a period
     */
    public RDate(final Period aPeriod) {
        super(RDATE);
        period = aPeriod;
    }

    /**
     * Constructor.
     *
     * @param aList
     *            a list of parameters for this component
     * @param aPeriod
     *            a period
     */
    public RDate(final ParameterList aList, final Period aPeriod) {
        super(RDATE, aList);
        period = aPeriod;
    }

    /**
     * @see net.fortuna.ical4j.model.Property#validate()
     */
    public final void validate() throws ValidationException {

        /*
         * ; the following are optional, ; but MUST NOT occur more than once
         *
         * (";" "VALUE" "=" ("DATE-TIME" / "DATE" / "PERIOD")) / (";" tzidparam) /
         */
        ParameterValidator.getInstance().validateOneOrLess(Parameter.VALUE,
                getParameters());

        Parameter valueParam = getParameters().getParameter(Parameter.VALUE);

        if (valueParam != null
                && !Value.DATE_TIME.equals(valueParam.getValue())
                && !Value.DATE.equals(valueParam.getValue())) { throw new ValidationException(
                "Parameter [" + Parameter.VALUE + "] is invalid"); }

        ParameterValidator.getInstance().validateOneOrLess(Parameter.TZID,
                getParameters());

        /*
         * ; the following is optional, ; and MAY occur more than once
         *
         * (";" xparam)
         */
    }

    /**
     * @return Returns the period.
     */
    public final Period getPeriod() {
        return period;
    }

    /**
     * @return Returns the time.
     */
    public final Date getTime() {
        return time;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public final void setValue(final String aValue) throws ParseException {

        // value can be either a date-time or a date..
        Parameter valueParam = getParameters().getParameter(Parameter.VALUE);

        if (valueParam != null && Value.DATE.equals(valueParam.getValue())) {
            time = DateFormat.getInstance().parse(aValue);
        }
        else if (valueParam != null && Value.PERIOD.equals(valueParam)) {
            period = new Period(aValue);
        }
        else {
            time = DateTimeFormat.getInstance().parse(aValue);
        }
    }    

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public final String getValue() {
        if (getTime() != null) {

            Parameter valueParam = getParameters()
                    .getParameter(Parameter.VALUE);

            if (valueParam != null && Value.DATE.equals(valueParam.getValue())) {
                return DateFormat.getInstance().format(getTime());
            }
            else {
                return DateTimeFormat.getInstance().format(getTime(), isUtc());
            }
        }
        else if (getPeriod() != null) { return getPeriod().toString(); }

        return null;
    }

    /**
     * @return Returns the utc.
     */
    public final boolean isUtc() {
        return utc;
    }

    /**
     * @param utc
     *            The utc to set.
     */
    public final void setUtc(final boolean utc) {
        this.utc = utc;
    }
    
    /**
     * @param period The period to set.
     */
    public final void setPeriod(final Period period) {
        this.period = period;
        // unset time..
        time = null;
    }
    
    /**
     * @param time The time to set.
     */
    public final void setTime(final Date time) {
        this.time = time;
        // unset period..
        period = null;
    }
}