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

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.ParameterValidator;
import net.fortuna.ical4j.util.Strings;

/**
 * Defines a DTEND iCalendar component property.
 * 
 * <pre>
 * 4.8.2.2 Date/Time End
 * 
 *    Property Name: DTEND
 * 
 *    Purpose: This property specifies the date and time that a calendar
 *    component ends.
 * 
 *    Value Type: The default value type is DATE-TIME. The value type can
 *    be set to a DATE value type.
 * 
 *    Property Parameters: Non-standard, value data type, time zone
 *    identifier property parameters can be specified on this property.
 * 
 *    Conformance: This property can be specified in "VEVENT" or
 *    "VFREEBUSY" calendar components.
 * 
 *    Description: Within the "VEVENT" calendar component, this property
 *    defines the date and time by which the event ends. The value MUST be
 *    later in time than the value of the "DTSTART" property.
 * 
 *    Within the "VFREEBUSY" calendar component, this property defines the
 *    end date and time for the free or busy time information. The time
 *    MUST be specified in the UTC time format. The value MUST be later in
 *    time than the value of the "DTSTART" property.
 * 
 *    Format Definition: The property is defined by the following notation:
 * 
 *      dtend      = "DTEND" dtendparam":" dtendval CRLF
 * 
 *      dtendparam = *(
 * 
 *                 ; the following are optional,
 *                 ; but MUST NOT occur more than once
 * 
 *                 (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
 *                 (";" tzidparam) /
 * 
 *                 ; the following is optional,
 *                 ; and MAY occur more than once
 * 
 *                 (";" xparam)
 * 
 *                 )
 * 
 * 
 * 
 *      dtendval   = date-time / date
 *      ;Value MUST match value type
 * </pre>
 *
 * Examples:
 * 
 * <pre>
 *  // construct an end date from a start date and a duration..
 *  DtStart start = ...
 *  Dur oneWeek = new Dur("1W");
 *  DtEnd end = new DtEnd(oneWeek.getTime(start.getDate());
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class DtEnd extends DateProperty {
    
    private static final long serialVersionUID = 8107416684717228297L;

    /**
     * Default constructor. The time value is initialised to the
     * time of instantiation.
     */
    public DtEnd() {
        super(DTEND);
    }
    
    /**
     * @param aList
     *            a list of parameters for this component
     * @param aValue
     *            a value string for this component
     * @throws ParseException
     *             when the specified string is not a valid date/date-time
     *             representation
     */
    public DtEnd(final ParameterList aList, final String aValue)
            throws ParseException {
        super(DTEND, aList);
        setValue(aValue);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence
     * of a VALUE parameter.
     * @param aDate
     *            a date
     */
    public DtEnd(final Date aDate) {
        super(DTEND);
        setDate(aDate);
    }
    
    /**
     * Constructs a new DtEnd with the specified time.
     * @param time the time of the DtEnd
     * @param utc specifies whether time is UTC
     */
    public DtEnd(final Date time, final boolean utc) {
        super(DTEND);
        setDate(time);
        setUtc(utc);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence
     * of a VALUE parameter.
     *
     * @param aList
     *            a list of parameters for this component
     * @param aDate
     *            a date
     */
    public DtEnd(final ParameterList aList, final Date aDate) {
        super(DTEND, aList);
        setDate(aDate);
    }

    /**
     * @see net.fortuna.ical4j.model.Property#validate()
     */
    public final void validate() throws ValidationException {
        super.validate();

        /*
         * ; the following are optional, ; but MUST NOT occur more than once
         *
         * (";" "VALUE" "=" ("DATE-TIME" / "DATE")) / (";" tzidparam) /
         */
        ParameterValidator.getInstance().assertOneOrLess(Parameter.VALUE,
                getParameters());

        Parameter valueParam = getParameters().getParameter(Parameter.VALUE);

        if (valueParam == null && !(getDate() instanceof DateTime)) {
            throw new ValidationException("VALUE parameter is invalid for time instance");
        }
        if (getDate() instanceof DateTime) {
            DateTime dateTime = (DateTime) getDate();
            if (dateTime.isUtc()) {
                ParameterValidator.getInstance().assertNone(Parameter.TZID,
                        getParameters());
            }
            else {
                ParameterValidator.getInstance().assertOneOrLess(Parameter.TZID,
                        getParameters());
            }
        }

        /*
         * ; the following is optional, ; and MAY occur more than once
         *
         * (";" xparam)
         */
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public final void setValue(final String aValue) throws ParseException {
        // value can be either a date-time or a date..
        if (Value.DATE.equals(getParameters().getParameter(Parameter.VALUE))) {
            setDate(new Date(aValue));
        }
        else {
            setDate(new DateTime(aValue));
        }
    }

    /*
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public final String getValue() {
        return Strings.valueOf(getDate());
    }
}
