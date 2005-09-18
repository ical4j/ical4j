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
 * Defines a DTSTART iCalendar component property.
 *
 * <pre>
 * 4.8.2.4 Date/Time Start
 * 
 *    Property Name: DTSTART
 * 
 *    Purpose: This property specifies when the calendar component begins.
 * 
 *    Value Type: The default value type is DATE-TIME. The time value MUST
 *    be one of the forms defined for the DATE-TIME value type. The value
 *    type can be set to a DATE value type.
 * 
 *    Property Parameters: Non-standard, value data type, time zone
 *    identifier property parameters can be specified on this property.
 * 
 *    Conformance: This property can be specified in the "VEVENT", "VTODO",
 *    "VFREEBUSY", or "VTIMEZONE" calendar components.
 * 
 *    Description: Within the "VEVENT" calendar component, this property
 *    defines the start date and time for the event. The property is
 *    REQUIRED in "VEVENT" calendar components. Events can have a start
 *    date/time but no end date/time. In that case, the event does not take
 *    up any time.
 * 
 *    Within the "VFREEBUSY" calendar component, this property defines the
 *    start date and time for the free or busy time information. The time
 *    MUST be specified in UTC time.
 * 
 *    Within the "VTIMEZONE" calendar component, this property defines the
 *    effective start date and time for a time zone specification. This
 *    property is REQUIRED within each STANDARD and DAYLIGHT part included
 *    in "VTIMEZONE" calendar components and MUST be specified as a local
 *    DATE-TIME without the "TZID" property parameter.
 * 
 *    Format Definition: The property is defined by the following notation:
 * 
 *      dtstart    = "DTSTART" dtstparam ":" dtstval CRLF
 * 
 *      dtstparam  = *(
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
 *                   *(";" xparam)
 * 
 *                 )
 * 
 * 
 * 
 *      dtstval    = date-time / date
 *      ;Value MUST match value type
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class DtStart extends DateProperty {
    
    private static final long serialVersionUID = -5707097476081111815L;

    /**
     * Default constructor. The time value is initialised to the
     * time of instantiation.
     */
    public DtStart() {
        super(DTSTART);
    }
    
    /**
     * @param aValue
     *            a value string for this component
     * @throws ParseException
     *             where the specified value string is not a valid
     *             date-time/date representation
     */
    public DtStart(final String aValue)
            throws ParseException {
        super(DTSTART);
        setValue(aValue);
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
    public DtStart(final ParameterList aList, final String aValue)
            throws ParseException {
        super(DTSTART, aList);
        setValue(aValue);
    }

    /**
     * Constructor. Date or Date-Time format is determined based
     * on the presence of a VALUE parameter.
     * @param aDate
     *            a date
     */
    public DtStart(final Date aDate) {
        super(DTSTART);
        setDate(aDate);
    }
    
    /**
     * Constructs a new DtStart with the specified time.
     * @param time the time of the DtStart
     * @param utc specifies whether time is UTC
     */
    public DtStart(final Date time, final boolean utc) {
        super(DTSTART);
        setDate(time);
        setUtc(utc);
    }

    /**
     * Constructor. Date or Date-Time format is determined based
     * on the presence of a VALUE parameter.
     * @param aList
     *            a list of parameters for this component
     * @param aDate
     *            a date
     */
    public DtStart(final ParameterList aList, final Date aDate) {
        super(DTSTART, aList);
        setDate(aDate);
    }

    /**
     * @return Returns the time.
     */
    public final Date getTime() {
        return getDate();
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

        if (valueParam != null && !Value.DATE_TIME.equals(valueParam) && !Value.DATE.equals(valueParam)) {
            throw new ValidationException(
                "Parameter [" + Parameter.VALUE + "] is invalid");
        }

        if (isUtc()) {
            ParameterValidator.getInstance().assertNone(Parameter.TZID,
                    getParameters());
            
        }
        else {
            ParameterValidator.getInstance().assertOneOrLess(Parameter.TZID,
                    getParameters());
        }

        /*
         *  ; the following is optional, ; and MAY occur more than once
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

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public final String getValue() {
        return Strings.valueOf(getTime());
    }
    
    /**
     * @param time The time to set.
     */
    public final void setTime(final Date time) {
        setDate(time);
    }
}
