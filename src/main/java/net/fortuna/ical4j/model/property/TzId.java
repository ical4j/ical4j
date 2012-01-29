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

import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.ValidationException;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a TZID iCalendar component property.
 * 
 * <pre>
 *     4.8.3.1 Time Zone Identifier
 *     
 *        Property Name: TZID
 *     
 *        Purpose: This property specifies the text value that uniquely
 *        identifies the &quot;VTIMEZONE&quot; calendar component.
 *     
 *        Value Type: TEXT
 *     
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *     
 *        Conformance: This property MUST be specified in a &quot;VTIMEZONE&quot;
 *        calendar component.
 *     
 *        Description: This is the label by which a time zone calendar
 *        component is referenced by any iCalendar properties whose data type
 *        is either DATE-TIME or TIME and not intended to specify a UTC or a
 *        &quot;floating&quot; time. The presence of the SOLIDUS character (US-ASCII
 *        decimal 47) as a prefix, indicates that this TZID represents an
 *        unique ID in a globally defined time zone registry (when such
 *        registry is defined).
 *     
 *             Note: This document does not define a naming convention for time
 *             zone identifiers. Implementers may want to use the naming
 *             conventions defined in existing time zone specifications such as
 *             the public-domain Olson database [TZ]. The specification of
 *             globally unique time zone identifiers is not addressed by this
 *             document and is left for future study.
 *     
 *        Format Definition: This property is defined by the following
 *        notation:
 *     
 *          tzid       = &quot;TZID&quot; tzidpropparam &quot;:&quot; [tzidprefix] text CRLF
 *     
 *          tzidpropparam      = *(&quot;;&quot; xparam)
 *     
 *          ;tzidprefix        = &quot;/&quot;
 *          ; Defined previously. Just listed here for reader convenience.
 *     
 *        Example: The following are examples of non-globally unique time zone
 *        identifiers:
 *     
 *          TZID:US-Eastern
 *     
 *          TZID:California-Los_Angeles
 *     
 *        The following is an example of a fictitious globally unique time zone
 *        identifier:
 *     
 *          TZID:/US-New_York-New_York
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class TzId extends Property implements Escapable {

    private static final long serialVersionUID = -522764921502407137L;

    /**
     * Timezone identifier prefix.
     */
    public static final String PREFIX = "/";

    private String value;

    /**
     * Default constructor.
     */
    public TzId() {
        super(TZID, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aValue a value string for this component
     */
    public TzId(final String aValue) {
        super(TZID, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public TzId(final ParameterList aList, final String aValue) {
        super(TZID, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
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
