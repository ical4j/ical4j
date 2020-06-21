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

import net.fortuna.ical4j.model.CalendarDateFormat;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactory;

import java.time.Instant;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a LAST-MODIFIED iCalendar component property.
 * <p/>
 * <pre>
 *     4.8.7.3 Last Modified
 *
 *        Property Name: LAST-MODIFIED
 *
 *        Purpose: The property specifies the date and time that the
 *        information associated with the calendar component was last revised
 *        in the calendar store.
 *
 *             Note: This is analogous to the modification date and time for a
 *             file in the file system.
 *
 *        Value Type: DATE-TIME
 *
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *
 *        Conformance: This property can be specified in the &quot;EVENT&quot;, &quot;VTODO&quot;,
 *        &quot;VJOURNAL&quot; or &quot;VTIMEZONE&quot; calendar components.
 *
 *        Description: The property value MUST be specified in the UTC time
 *        format.
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          last-mod   = &quot;LAST-MODIFIED&quot; lstparam &quot;:&quot; date-time CRLF
 *
 *          lstparam   = *(&quot;;&quot; xparam)
 * </pre>
 *
 * @author benf
 */
public class LastModified extends DateProperty<Instant> {

    private static final long serialVersionUID = 5288572652052836062L;

    /**
     * Default constructor.
     */
    public LastModified() {
        this(Instant.now());
    }

    /**
     * @param aValue a date-time value
     */
    public LastModified(final String aValue) {
        this(new ParameterList(), aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public LastModified(final ParameterList aList, final String aValue) {
        super(LAST_MODIFIED, aList, CalendarDateFormat.UTC_DATE_TIME_FORMAT);
        setValue(aValue);
    }

    /**
     * @param aDate a date representation of a date-time value
     */
    public LastModified(final Instant aDate) {
        super(LAST_MODIFIED, CalendarDateFormat.UTC_DATE_TIME_FORMAT);
        setDate(aDate);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aDate a date representation of a date-time value
     */
    public LastModified(final ParameterList aList, final Instant aDate) {
        super(LAST_MODIFIED, aList, CalendarDateFormat.UTC_DATE_TIME_FORMAT);
        setDate(aDate);
    }

    @Override
    protected PropertyFactory<LastModified> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<LastModified> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(LAST_MODIFIED);
        }

        public LastModified createProperty(final ParameterList parameters, final String value) {
            return new LastModified(parameters, value);
        }

        public LastModified createProperty() {
            return new LastModified();
        }
    }

}
