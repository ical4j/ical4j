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
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;

import java.text.ParseException;
import java.time.Instant;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a COMPLETED iCalendar component property.
 * <p/>
 * <pre>
 *     4.8.2.1 Date/Time Completed
 *
 *        Property Name: COMPLETED
 *
 *        Purpose: This property defines the date and time that a to-do was
 *        actually completed.
 *
 *        Value Type: DATE-TIME
 *
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *
 *        Conformance: The property can be specified in a &quot;VTODO&quot; calendar
 *        component.
 *
 *        Description: The date and time MUST be in a UTC format.
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          completed  = &quot;COMPLETED&quot; compparam &quot;:&quot; date-time CRLF
 *
 *          compparam  = *(&quot;;&quot; xparam)
 * </pre>
 *
 * @author Ben Fortuna
 */
public class Completed extends DateProperty<Instant> {

    private static final long serialVersionUID = 6824213281785639181L;

    /**
     * Default constructor.
     */
    public Completed() {
        this(Instant.now());
    }

    /**
     * @param aValue a value string for this component
     * @throws ParseException when the specified string is not a valid date-time represenation
     */
    public Completed(final String aValue) throws ParseException {
        super(COMPLETED, new Factory());
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     * @throws ParseException when the specified string is not a valid date-time represenation
     */
    public Completed(final ParameterList aList, final String aValue)
            throws ParseException {
        super(COMPLETED, aList, new Factory());
        setValue(aValue);
    }

    /**
     * @param aDate a date
     */
    public Completed(final Instant aDate) {
        super(COMPLETED, new Factory());
        setDate(aDate);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aDate a date
     */
    public Completed(final ParameterList aList, final Instant aDate) {
        super(COMPLETED, aList, new Factory());
        setDate(aDate);
    }

    @Override
    public Property copy() throws ParseException {
        return new Factory().createProperty(getParameters(), getValue());
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Completed> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(COMPLETED);
        }

        public Completed createProperty(final ParameterList parameters, final String value) throws ParseException {
            return new Completed(parameters, value);
        }

        public Completed createProperty() {
            return new Completed();
        }
    }

}
