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

import java.util.Iterator;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.Validator;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.util.PropertyValidator;
import net.fortuna.ical4j.util.Strings;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines an iCalendar VAVAILABILITY component.
 *
 * <pre>
   Component Name:  VAVAILABILITY

   Purpose:  Provide a grouping of component properties that describe
      the availability associated with a calendar user.

   Format Definition:  A "VAVAILABILITY" calendar component is defined
      by the following notation:

          availabilityc  = "BEGIN" ":" "VAVAILABILITY" CRLF
                           availabilityprop *availablec
                           "END" ":" "VAVAILABILITY" CRLF

          availabilityprop  = *(

                            ; the following are REQUIRED,
                            ; but MUST NOT occur more than once

                            dtstamp / dtstart / uid

                            ; the following are OPTIONAL,
                            ; but MUST NOT occur more than once

                            busytype / created / last-mod /
                            organizer / seq / summary / url /

                            ; either 'dtend' or 'duration' may appear
                            ; in a 'availabilityprop', but 'dtend' and
                            ; 'duration' MUST NOT occur in the same
                            ; 'availabilityprop'

                            dtend / duration /

                            ; the following are OPTIONAL,
                            ; and MAY occur more than once

                            categories / comment / contact / x-prop

                            )

 *
 * </pre>
 *
 * @author Ben Fortuna
 * @author Mike Douglass
 */
public class VAvailability extends CalendarComponent {

	private static final long serialVersionUID = -3001603309266267258L;
	
	private ComponentList available;

    /**
     * Default constructor.
     */
    public VAvailability() {
        super(VAVAILABILITY);
        this.available = new ComponentList();
        getProperties().add(new DtStamp());
    }

    /**
     * Constructs a new instance containing the specified properties.
     * @param properties a list of properties
     */
    public VAvailability(final PropertyList properties) {
        super(VAVAILABILITY, properties);
        this.available = new ComponentList();
    }

    /**
     * Constructor.
     * @param properties a list of properties
     * @param available a list of available components
     */
    public VAvailability(final PropertyList properties, final ComponentList available) {
        super(VEVENT, properties);
        this.available = available;
    }

    /**
     * Returns the list of available times.
     * @return a component list
     */
    public final ComponentList getAvailable() {
        return available;
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
        b.append(getAvailable());
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

        // validate that getAvailable() only contains Available components
        final Iterator iterator = getAvailable().iterator();
        while (iterator.hasNext()) {
            final Component component = (Component) iterator.next();

            if (!(component instanceof Available)) {
                throw new ValidationException("Component ["
                        + component.getName() + "] may not occur in VAVAILABILITY");
            }
        }

        /*
         * ; dtstamp / dtstart / uid are required, but MUST NOT occur more than once /
         */
        PropertyValidator.getInstance().assertOne(Property.DTSTART,
                getProperties());
        PropertyValidator.getInstance().assertOne(Property.DTSTAMP,
                getProperties());
        PropertyValidator.getInstance().assertOne(Property.UID,
                getProperties());

        /*       If specified, the "DTSTART" and "DTEND" properties in
         *      "VAVAILABILITY" components and "AVAILABLE" sub-components MUST be
         *      "DATE-TIME" values specified as either date with UTC time or date
         *      with local time and a time zone reference.
         */
        final DtStart start = (DtStart) getProperty(Property.DTSTART);
        if (Value.DATE.equals(start.getParameter(Parameter.VALUE))) {
            throw new ValidationException("Property [" + Property.DTSTART
                    + "] must be a " + Value.DATE_TIME);
        }

        /*
         * ; either 'dtend' or 'duration' may appear in ; a 'eventprop', but 'dtend' and 'duration' ; MUST NOT occur in
         * the same 'eventprop' dtend / duration /
         */
        if (getProperty(Property.DTEND) != null) {
            PropertyValidator.getInstance().assertOne(Property.DTEND,
                    getProperties());
            /* Must be DATE_TIME */
            final DtEnd end = (DtEnd) getProperty(Property.DTEND);
            if (Value.DATE.equals(end.getParameter(Parameter.VALUE))) {
                throw new ValidationException("Property [" + Property.DTEND
                        + "] must be a " + Value.DATE_TIME);
            }

            if (getProperty(Property.DURATION) != null) {
                throw new ValidationException("Only one of Property [" + Property.DTEND
                        + "] or [" + Property.DURATION +
                        " must appear a VAVAILABILITY");
            }
        }

        /*
         *                ; the following are optional,
         *                ; but MUST NOT occur more than once
         *
         *                  busytype / created / last-mod /
         *                  organizer / seq / summary / url /
         */
        PropertyValidator.getInstance().assertOneOrLess(Property.BUSYTYPE,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CREATED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.ORGANIZER,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.URL,
                getProperties());

        /*
         * ; the following are optional, ; and MAY occur more than once
         *                 categories / comment / contact / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Validator getValidator(Method method) {
        // TODO Auto-generated method stub
        return null;
    }
}
