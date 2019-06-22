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
import net.fortuna.ical4j.model.PropertyFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a DUE iCalendar component property.
 * <p/>
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
public class Due<T extends Temporal> extends DateProperty<T> {

    private static final long serialVersionUID = -2965312347832730406L;

    /**
     * Creates a new instance initialised with the parsed value.
     *
     * @param value the DUE value string to parse
     * @throws java.time.format.DateTimeParseException where the specified string is not a valid DUE value representation
     */
    public Due(final String value) {
        super(DUE, new Factory());
        setValue(value);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     * @throws java.time.format.DateTimeParseException when the specified string is not a valid date/date-time representation
     */
    public Due(final ParameterList aList, final String aValue) {
        super(DUE, aList, new Factory());
        setValue(aValue);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence of a VALUE parameter.
     *
     * @param aDate a date
     */
    public Due(final T aDate) {
        super(DUE, new Factory());
        setDate(aDate);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence of a VALUE parameter.
     *
     * @param aList a list of parameters for this component
     * @param aDate a date
     */
    public Due(final ParameterList aList, final T aDate) {
        super(DUE, aList, new Factory());
        setDate(aDate);
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Due> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(DUE);
        }

        public Due createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Due(parameters, value);
        }

        public Due createProperty() {
            return new Due<>(LocalDateTime.now(ZoneOffset.UTC));
        }
    }

}
