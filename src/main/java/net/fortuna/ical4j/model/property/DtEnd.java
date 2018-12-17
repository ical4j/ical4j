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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a DTEND iCalendar component property.
 * <p/>
 * <pre>
 *     4.8.2.2 Date/Time End
 *
 *        Property Name: DTEND
 *
 *        Purpose: This property specifies the date and time that a calendar
 *        component ends.
 *
 *        Value Type: The default value type is DATE-TIME. The value type can
 *        be set to a DATE value type.
 *
 *        Property Parameters: Non-standard, value data type, time zone
 *        identifier property parameters can be specified on this property.
 *
 *        Conformance: This property can be specified in &quot;VEVENT&quot; or
 *        &quot;VFREEBUSY&quot; calendar components.
 *
 *        Description: Within the &quot;VEVENT&quot; calendar component, this property
 *        defines the date and time by which the event ends. The value MUST be
 *        later in time than the value of the &quot;DTSTART&quot; property.
 *
 *        Within the &quot;VFREEBUSY&quot; calendar component, this property defines the
 *        end date and time for the free or busy time information. The time
 *        MUST be specified in the UTC time format. The value MUST be later in
 *        time than the value of the &quot;DTSTART&quot; property.
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          dtend      = &quot;DTEND&quot; dtendparam&quot;:&quot; dtendval CRLF
 *
 *          dtendparam = *(
 *
 *                     ; the following are optional,
 *                     ; but MUST NOT occur more than once
 *
 *                     (&quot;;&quot; &quot;VALUE&quot; &quot;=&quot; (&quot;DATE-TIME&quot; / &quot;DATE&quot;)) /
 *                     (&quot;;&quot; tzidparam) /
 *
 *                     ; the following is optional,
 *                     ; and MAY occur more than once
 *
 *                     (&quot;;&quot; xparam)
 *
 *                     )
 *
 *
 *
 *          dtendval   = date-time / date
 *          ;Value MUST match value type
 * </pre>
 * <p/>
 * Examples:
 * <p/>
 * <pre>
 *      // construct an end date from a start date and a duration..
 *      DtStart start = ...
 *      Dur oneWeek = new Dur(&quot;1W&quot;);
 *      DtEnd end = new DtEnd(oneWeek.getTime(start.getDate());
 * </pre>
 *
 * @author Ben Fortuna
 */
public class DtEnd extends DateProperty {

    private static final long serialVersionUID = 8107416684717228297L;

    /**
     * Default constructor. The time value is initialised to the time of instantiation.
     */
    public DtEnd() {
        super(DTEND, new Factory());
    }

    /**
     * Creates a new DTEND property initialised with the specified timezone.
     *
     * @param timezone initial timezone
     */
    public DtEnd(TimeZone timezone) {
        super(DTEND, timezone, new Factory());
    }

    /**
     * Creates a new instance initialised with the parsed value.
     *
     * @param value the DTEND value string to parse
     * @throws ParseException where the specified string is not a valid DTEND value representation
     */
    public DtEnd(final String value) throws ParseException {
        super(DTEND, new Factory());
        setValue(value);
    }

    /**
     * Creates a new DTEND property initialised with the specified timezone and value.
     *
     * @param value    a string representation of a DTEND value
     * @param timezone initial timezone
     * @throws ParseException where the specified value is not a valid string
     *                        representation
     */
    public DtEnd(String value, TimeZone timezone) throws ParseException {
        super(DTEND, timezone, new Factory());
        setValue(value);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     * @throws ParseException when the specified string is not a valid date/date-time representation
     */
    public DtEnd(final ParameterList aList, final String aValue)
            throws ParseException {
        super(DTEND, aList, new Factory());
        setValue(aValue);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence of a VALUE parameter.
     *
     * @param aDate a date
     */
    public DtEnd(final Date aDate) {
        super(DTEND, new Factory());
        setDate(aDate);
    }

    /**
     * Constructs a new DtEnd with the specified time.
     *
     * @param time the time of the DtEnd
     * @param utc  specifies whether time is UTC
     */
    public DtEnd(final Date time, final boolean utc) {
        super(DTEND, new Factory());
        setDate(time);
        setUtc(utc);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence of a VALUE parameter.
     *
     * @param aList a list of parameters for this component
     * @param aDate a date
     */
    public DtEnd(final ParameterList aList, final Date aDate) {
        super(DTEND, aList, new Factory());
        setDate(aDate);
    }

    @PropertyFactory.Service
    public static class Factory extends Content.Factory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(DTEND);
        }

        public Property createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new DtEnd(parameters, value);
        }

        public Property createProperty() {
            return new DtEnd();
        }
    }

}
