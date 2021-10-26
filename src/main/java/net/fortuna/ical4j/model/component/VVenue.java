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
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

/**
 * $Id $ [Apr 5, 2004]
 *
 * Defines an iCalendar VVENUE component.
 *
 * <pre>
 * 4.  Venue Component
 *
 *    Component Name: "VVENUE"
 *
 *    Purpose: Provide a grouping of component properties that describe an
 *    event venue.
 *
 *    Format Definition: A "VVENUE" calendar component is defined by the
 *    following notation:
 *      venuec  = "BEGIN" ":" "VVENUE" CRLF
 *              venueprop
 *              "END" ":" "VVENUE" CRLF
 *
 *      venueprop  = *(
 *
 *                ; the following are all REQUIRED,
 *                ; but MUST NOT occur more than once
 *
 *                uid
 *
 *                ; the following are optional,
 *                ; but MUST NOT occur more than once
 *
 *                name / description / street-address / extended-address /
 *                locality / region / country / postal-code / tzid / geo /
 *                location-type / categories
 *
 *                ; the following are optional,
 *                ; and MAY occur more than once
 *
 *                tel / url
 *              )
 *
 *    Description: A "VVENUE" calendar component is a grouping of component
 *    properties that represent a venue where an event occurs.  This
 *    extends the "LOCATION" property of "VEVENT" and "TODO" components,
 *    providing the ability to specify detailed information about the event
 *    venue.
 *
 * </pre>
 *
 * @author Ben Fortuna
 * @author Mike Douglass
 */
public class VVenue extends CalendarComponent {

	private static final long serialVersionUID = 4502423035501438515L;

	/**
     * Default constructor.
     */
    public VVenue() {
        super(VVENUE);
    }

    /**
     * Constructs a new instance containing the specified properties.
     * @param properties a list of properties
     */
    public VVenue(final PropertyList properties) {
        super(VVENUE, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return BEGIN +
                ':' +
                getName() +
                Strings.LINE_SEPARATOR +
                getProperties() +
                END +
                ':' +
                getName() +
                Strings.LINE_SEPARATOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(final boolean recurse) throws ValidationException {
        ComponentValidator.VVENUE.validate(this);
        if (recurse) {
            validateProperties();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Validator getValidator(Method method) {
        // No method validation required.. 
        return EMPTY_VALIDATOR;
    }

    public static class Factory extends Content.Factory implements ComponentFactory<VVenue> {

        public Factory() {
            super(VVENUE);
        }

        @Override
        public VVenue createComponent() {
            return new VVenue();
        }

        @Override
        public VVenue createComponent(PropertyList properties) {
            return new VVenue(properties);
        }

        @Override
        public VVenue createComponent(PropertyList properties, ComponentList subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", VVENUE));
        }
    }
}
