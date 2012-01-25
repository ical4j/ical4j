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

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.ValidationException;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a TRANSP iCalendar component property.
 * 
 * <pre>
 *     4.8.2.7 Time Transparency
 *     
 *        Property Name: TRANSP
 *     
 *        Purpose: This property defines whether an event is transparent or not
 *        to busy time searches.
 *     
 *        Value Type: TEXT
 *     
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *     
 *        Conformance: This property can be specified once in a &quot;VEVENT&quot;
 *        calendar component.
 *     
 *        Description: Time Transparency is the characteristic of an event that
 *        determines whether it appears to consume time on a calendar. Events
 *        that consume actual time for the individual or resource associated
 *        with the calendar SHOULD be recorded as OPAQUE, allowing them to be
 *        detected by free-busy time searches. Other events, which do not take
 *        up the individual's (or resource's) time SHOULD be recorded as
 *        TRANSPARENT, making them invisible to free-busy time searches.
 *     
 *        Format Definition: The property is specified by the following
 *        notation:
 *     
 *          transp     = &quot;TRANSP&quot; tranparam &quot;:&quot; transvalue CRLF
 *     
 *          tranparam  = *(&quot;;&quot; xparam)
 *     
 *          transvalue = &quot;OPAQUE&quot;      ;Blocks or opaque on busy time searches.
 *                     / &quot;TRANSPARENT&quot; ;Transparent on busy time searches.
 *             ;Default value is OPAQUE
 *     
 *        Example: The following is an example of this property for an event
 *        that is transparent or does not block on free/busy time searches:
 *     
 *          TRANSP:TRANSPARENT
 *     
 *        The following is an example of this property for an event that is
 *        opaque or blocks on free/busy time searches:
 *     
 *          TRANSP:OPAQUE
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Transp extends Property {

    private static final long serialVersionUID = 3801479657311785518L;

    /**
     * Opaque.
     */
    public static final Transp OPAQUE = new ImmutableTransp("OPAQUE");

    /**
     * Transparent.
     */
    public static final Transp TRANSPARENT = new ImmutableTransp("TRANSPARENT");

    /**
     * @author Ben Fortuna An immutable instance of Transp.
     */
    private static final class ImmutableTransp extends Transp {

        private static final long serialVersionUID = -6595830107310111996L;

        private ImmutableTransp(final String value) {
            super(new ParameterList(true), value);
        }

        public void setValue(final String aValue) {
            throw new UnsupportedOperationException(
                    "Cannot modify constant instances");
        }
    }

    private String value;

    /**
     * Default constructor.
     */
    public Transp() {
        super(TRANSP, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aValue a value string for this component
     */
    public Transp(final String aValue) {
        super(TRANSP, PropertyFactoryImpl.getInstance());
        this.value = aValue;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Transp(final ParameterList aList, final String aValue) {
        super(TRANSP, aList, PropertyFactoryImpl.getInstance());
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(final String aValue) {
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
