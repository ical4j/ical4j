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

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.ValidationException;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a DUE iCalendar component property.
 * 
 * <pre>
 *     4.8.2.3 Date/Time Due
 *     
 *        Property Name: DUE
 *     
 *        Purpose: This property defines the date and time that a to-do is
 *        expected to be completed.
 *     
 *        Value Type: The default value type is DATE-TIME. The value type can
 *        be set to a DATE value type.
 *     
 *        Property Parameters: Non-standard, value data type, time zone
 *        identifier property parameters can be specified on this property.
 *     
 *        Conformance: The property can be specified once in a &quot;VTODO&quot; calendar
 *        component.
 *     
 *        Description: The value MUST be a date/time equal to or after the
 *        DTSTART value, if specified.
 *     
 *        Format Definition: The property is defined by the following notation:
 *     
 *          due        = &quot;DUE&quot; dueparam&quot;:&quot; dueval CRLF
 *     
 *          dueparam   = *(
 *                     ; the following are optional,
 *                     ; but MUST NOT occur more than once
 *     
 *                     (&quot;;&quot; &quot;VALUE&quot; &quot;=&quot; (&quot;DATE-TIME&quot; / &quot;DATE&quot;)) /
 *                     (&quot;;&quot; tzidparam) /
 *     
 *                     ; the following is optional,
 *                     ; and MAY occur more than once
 *     
 *                       *(&quot;;&quot; xparam)
 *     
 *                     )
 *     
 *     
 *     
 *          dueval     = date-time / date
 *          ;Value MUST match value type
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Due extends DateProperty {

    private static final long serialVersionUID = -2965312347832730406L;

    /**
     * Default constructor. The time value is initialised to the time of instantiation.
     */
    public Due() {
        super(DUE, PropertyFactoryImpl.getInstance());
        // defaults to UTC time..
        setDate(new DateTime(true));
    }

    /**
     * Creates a new DUE property initialised with the specified timezone.
     * @param timezone initial timezone
     */
    public Due(TimeZone timezone) {
        super(DUE, timezone, PropertyFactoryImpl.getInstance());
    }

    /**
     * Creates a new instance initialised with the parsed value.
     * @param value the DUE value string to parse
     * @throws ParseException where the specified string is not a valid DUE value representation
     */
    public Due(final String value) throws ParseException {
        super(DUE, PropertyFactoryImpl.getInstance());
        setValue(value);
    }

    /**
     * Creates a new DUE property initialised with the specified timezone and value.
     * @param value a string representation of a DUE value
     * @param timezone initial timezone
     * @throws ParseException where the specified value is not a valid string
     * representation
     */
    public Due(String value, TimeZone timezone) throws ParseException {
        super(DUE, timezone, PropertyFactoryImpl.getInstance());
        setValue(value);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     * @throws ParseException when the specified string is not a valid date/date-time representation
     */
    public Due(final ParameterList aList, final String aValue)
            throws ParseException {
        super(DUE, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence of a VALUE parameter.
     * @param aDate a date
     */
    public Due(final Date aDate) {
        super(DUE, PropertyFactoryImpl.getInstance());
        setDate(aDate);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence of a VALUE parameter.
     * @param aList a list of parameters for this component
     * @param aDate a date
     */
    public Due(final ParameterList aList, final Date aDate) {
        super(DUE, aList, PropertyFactoryImpl.getInstance());
        setDate(aDate);
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

        /*
         * ; the following is optional, ; and MAY occur more than once (";" xparam)
         */
    }
}
