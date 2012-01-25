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

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.Validator;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.util.PropertyValidator;
import net.fortuna.ical4j.util.Strings;

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
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        b.append(BEGIN);
        b.append(':');
        b.append(getName());
        b.append(Strings.LINE_SEPARATOR);
        b.append(getProperties());
        b.append(END);
        b.append(':');
        b.append(getName());
        b.append(Strings.LINE_SEPARATOR);
        return b.toString();
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse)
            throws ValidationException {

        /*
         * ; 'uiid' is required, but MUST NOT occur more ; than once uiid /
         */
        PropertyValidator.getInstance().assertOne(Property.UID,
                getProperties());

        /*
         *                ; the following are optional,
         *                ; but MUST NOT occur more than once
         *
         *                name / description / street-address / extended-address /
         *                locality / region / country / postal-code / tzid / geo /
         *                location-type / categories /
         *                dtstamp / created / last-modified
         */
        PropertyValidator.getInstance().assertOneOrLess(Property.NAME,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.STREET_ADDRESS,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.EXTENDED_ADDRESS,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LOCALITY,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.REGION,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.COUNTRY,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.POSTALCODE,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.TZID,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.GEO,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION_TYPE,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DTSTAMP,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CREATED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED,
                getProperties());

        /*
         * ; the following is optional, ; and MAY occur more than once tel / url / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Validator getValidator(Method method) {
        // No method validation required.. 
        return EMPTY_VALIDATOR;
    }
}
