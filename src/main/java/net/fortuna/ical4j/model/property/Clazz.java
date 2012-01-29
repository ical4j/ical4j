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
 * Defines a CLASS iCalendar property.
 * 
 * <pre>
 *     4.8.1.3 Classification
 *     
 *        Property Name: CLASS
 *     
 *        Purpose: This property defines the access classification for a
 *        calendar component.
 *     
 *        Value Type: TEXT
 *     
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *     
 *        Conformance: The property can be specified once in a &quot;VEVENT&quot;,
 *        &quot;VTODO&quot; or &quot;VJOURNAL&quot; calendar components.
 *     
 *        Description: An access classification is only one component of the
 *        general security system within a calendar application. It provides a
 *        method of capturing the scope of the access the calendar owner
 *        intends for information within an individual calendar entry. The
 *        access classification of an individual iCalendar component is useful
 *        when measured along with the other security components of a calendar
 *        system (e.g., calendar user authentication, authorization, access
 *        rights, access role, etc.). Hence, the semantics of the individual
 *        access classifications cannot be completely defined by this memo
 *        alone. Additionally, due to the &quot;blind&quot; nature of most exchange
 *        processes using this memo, these access classifications cannot serve
 *        as an enforcement statement for a system receiving an iCalendar
 *        object. Rather, they provide a method for capturing the intention of
 *        the calendar owner for the access to the calendar component.
 *     
 *        Format Definition: The property is defined by the following notation:
 *     
 *          class      = &quot;CLASS&quot; classparam &quot;:&quot; classvalue CRLF
 *     
 *          classparam = *(&quot;;&quot; xparam)
 *     
 *          classvalue = &quot;PUBLIC&quot; / &quot;PRIVATE&quot; / &quot;CONFIDENTIAL&quot; / iana-token
 *                     / x-name
 *          ;Default is PUBLIC
 *     
 *        Example: The following is an example of this property:
 *     
 *          CLASS:PUBLIC
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Clazz extends Property {

    private static final long serialVersionUID = 4939943639175551481L;

    /**
     * Constant for public classification.
     */
    public static final Clazz PUBLIC = new ImmutableClazz("PUBLIC");

    /**
     * Constant for private classification.
     */
    public static final Clazz PRIVATE = new ImmutableClazz("PRIVATE");

    /**
     * Constant for confidential classification.
     */
    public static final Clazz CONFIDENTIAL = new ImmutableClazz("CONFIDENTIAL");

    /**
     * @author Ben Fortuna An immutable instance of Clazz.
     */
    private static final class ImmutableClazz extends Clazz {

        private static final long serialVersionUID = 5978394762293365042L;

        /**
         * @param value
         */
        private ImmutableClazz(final String value) {
            super(new ParameterList(true), value);
        }

        /**
         * {@inheritDoc}
         */
        public void setValue(final String aValue) {
            throw new UnsupportedOperationException(
                    "Cannot modify constant instances");
        }
    }

    private String value;

    /**
     * Default constructor.
     */
    public Clazz() {
        super(CLASS, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aValue a value string for this component
     */
    public Clazz(final String aValue) {
        super(CLASS, PropertyFactoryImpl.getInstance());
        this.value = aValue;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Clazz(final ParameterList aList, final String aValue) {
        super(CLASS, aList, PropertyFactoryImpl.getInstance());
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
