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
 * Defines a UID iCalendar component property.
 * 
 * <pre>
 *     4.8.4.7 Unique Identifier
 *     
 *        Property Name: UID
 *     
 *        Purpose: This property defines the persistent, globally unique
 *        identifier for the calendar component.
 *     
 *        Value Type: TEXT
 *     
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *     
 *        Conformance: The property MUST be specified in the &quot;VEVENT&quot;, &quot;VTODO&quot;,
 *        &quot;VJOURNAL&quot; or &quot;VFREEBUSY&quot; calendar components.
 *     
 *        Description: The UID itself MUST be a globally unique identifier. The
 *        generator of the identifier MUST guarantee that the identifier is
 *        unique. There are several algorithms that can be used to accomplish
 *        this. The identifier is RECOMMENDED to be the identical syntax to the
 *        [RFC 822] addr-spec. A good method to assure uniqueness is to put the
 *        domain name or a domain literal IP address of the host on which the
 *        identifier was created on the right hand side of the &quot;@&quot;, and on the
 *        left hand side, put a combination of the current calendar date and
 *        time of day (i.e., formatted in as a DATE-TIME value) along with some
 *        other currently unique (perhaps sequential) identifier available on
 *        the system (for example, a process id number). Using a date/time
 *        value on the left hand side and a domain name or domain literal on
 *        the right hand side makes it possible to guarantee uniqueness since
 *        no two hosts should be using the same domain name or IP address at
 *        the same time. Though other algorithms will work, it is RECOMMENDED
 *        that the right hand side contain some domain identifier (either of
 *        the host itself or otherwise) such that the generator of the message
 *        identifier can guarantee the uniqueness of the left hand side within
 *        the scope of that domain.
 *     
 *        This is the method for correlating scheduling messages with the
 *        referenced &quot;VEVENT&quot;, &quot;VTODO&quot;, or &quot;VJOURNAL&quot; calendar component.
 *     
 *        The full range of calendar components specified by a recurrence set
 *        is referenced by referring to just the &quot;UID&quot; property value
 *        corresponding to the calendar component. The &quot;RECURRENCE-ID&quot; property
 *        allows the reference to an individual instance within the recurrence
 *        set.
 *     
 *        This property is an important method for group scheduling
 *        applications to match requests with later replies, modifications or
 *        deletion requests. Calendaring and scheduling applications MUST
 *        generate this property in &quot;VEVENT&quot;, &quot;VTODO&quot; and &quot;VJOURNAL&quot; calendar
 *        components to assure interoperability with other group scheduling
 *        applications. This identifier is created by the calendar system that
 *        generates an iCalendar object.
 *     
 *        Implementations MUST be able to receive and persist values of at
 *        least 255 characters for this property.
 *     
 *        Format Definition: The property is defined by the following notation:
 *     
 *          uid        = &quot;UID&quot; uidparam &quot;:&quot; text CRLF
 *     
 *          uidparam   = *(&quot;;&quot; xparam)
 *     
 *        Example: The following is an example of this property:
 *     
 *          UID:19960401T080045Z-4000F192713-0052@host1.com
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Uid extends Property implements Escapable {

    private static final long serialVersionUID = -7139407612536588584L;

    private String value;

    /**
     * Default constructor.
     */
    public Uid() {
        super(UID, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aValue a value string for this component
     */
    public Uid(final String aValue) {
        super(UID, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Uid(final ParameterList aList, final String aValue) {
        super(UID, aList, PropertyFactoryImpl.getInstance());
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
