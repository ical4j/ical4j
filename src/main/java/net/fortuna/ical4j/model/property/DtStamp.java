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
 * Defines a DTSTAMP iCalendar component property.
 * <p/>
 * <pre>
 *     4.8.7.2 Date/Time Stamp
 *
 *        Property Name: DTSTAMP
 *
 *        Purpose: The property indicates the date/time that the instance of
 *        the iCalendar object was created.
 *
 *        Value Type: DATE-TIME
 *
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *
 *        Conformance: This property MUST be included in the &quot;VEVENT&quot;, &quot;VTODO&quot;,
 *        &quot;VJOURNAL&quot; or &quot;VFREEBUSY&quot; calendar components.
 *
 *        Description: The value MUST be specified in the UTC time format.
 *
 *        This property is also useful to protocols such as [IMIP] that have
 *        inherent latency issues with the delivery of content. This property
 *        will assist in the proper sequencing of messages containing iCalendar
 *        objects.
 *
 *        This property is different than the &quot;CREATED&quot; and &quot;LAST-MODIFIED&quot;
 *        properties. These two properties are used to specify when the
 *        particular calendar data in the calendar store was created and last
 *        modified. This is different than when the iCalendar object
 *        representation of the calendar service information was created or
 *        last modified.
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          dtstamp    = &quot;DTSTAMP&quot; stmparam &quot;:&quot; date-time CRLF
 *
 *          stmparam   = *(&quot;;&quot; xparam)
 * </pre>
 *
 * @author Ben Fortuna
 */
public class DtStamp extends UtcProperty {

    private static final long serialVersionUID = 7581197869433744070L;

    /**
     * Default constructor. Initialises the dateTime value to the time of instantiation.
     */
    public DtStamp() {
        super(DTSTAMP, new Factory());
    }

    /**
     * @param aValue a string representation of a DTSTAMP value
     * @throws ParseException if the specified value is not a valid representation
     */
    public DtStamp(final String aValue) throws ParseException {
        this(new ParameterList(), aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     * @throws ParseException where the specified value string is not a valid date-time/date representation
     */
    public DtStamp(final ParameterList aList, final String aValue)
            throws ParseException {
        super(DTSTAMP, aList, new Factory());
        setValue(aValue);
    }

    /**
     * @param aDate a date representing a date-time
     */
    public DtStamp(final DateTime aDate) {
        super(DTSTAMP, new Factory());
        // time must be in UTC..
        aDate.setUtc(true);
        setDate(aDate);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aDate a date representing a date-time
     */
    public DtStamp(final ParameterList aList, final DateTime aDate) {
        super(DTSTAMP, aList, new Factory());
        // time must be in UTC..
        aDate.setUtc(true);
        setDate(aDate);
    }

    public static class Factory extends Content.Factory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(DTSTAMP);
        }

        @Override
        public Property createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new DtStamp(parameters, value);
        }

        @Override
        public Property createProperty() {
            return new DtStamp();
        }
    }

}
