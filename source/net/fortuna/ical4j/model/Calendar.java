/*
 * $Id$ [Apr 5, 2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.Iterator;

import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.PropertyValidator;

/**
 * Defines an iCalendar calendar.
 * 
 * <pre>
 *   4.6 Calendar Components
 *   
 *      The body of the iCalendar object consists of a sequence of calendar
 *      properties and one or more calendar components. The calendar
 *      properties are attributes that apply to the calendar as a whole. The
 *      calendar components are collections of properties that express a
 *      particular calendar semantic. For example, the calendar component can
 *      specify an event, a to-do, a journal entry, time zone information, or
 *      free/busy time information, or an alarm.
 *   
 *      The body of the iCalendar object is defined by the following
 *      notation:
 *   
 *        icalbody   = calprops component
 *   
 *        calprops   = 2*(
 *   
 *                   ; 'prodid' and 'version' are both REQUIRED,
 *                   ; but MUST NOT occur more than once
 *   
 *                   prodid /version /
 *   
 *                   ; 'calscale' and 'method' are optional,
 *                   ; but MUST NOT occur more than once
 *   
 *                   calscale        /
 *                   method          /
 *   
 *                   x-prop
 *   
 *                   )
 *   
 *        component  = 1*(eventc / todoc / journalc / freebusyc /
 *                   / timezonec / iana-comp / x-comp)
 *   
 *        iana-comp  = "BEGIN" ":" iana-token CRLF
 *   
 *                     1*contentline
 *   
 *                     "END" ":" iana-token CRLF
 *   
 *        x-comp     = "BEGIN" ":" x-name CRLF
 *   
 *                     1*contentline
 *   
 *                     "END" ":" x-name CRLF
 * </pre>
 * 
 * Example 1 - Creating a new calendar:
 * 
 * <pre><code>
 * Calendar calendar = new Calendar();
 * calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
 * calendar.getProperties().add(Version.VERSION_2_0);
 * calendar.getProperties().add(CalScale.GREGORIAN);
 *
 * // Add events, etc..
 * </code></pre>
 *
 * @author Ben Fortuna
 */
public class Calendar implements Serializable, TimeZoneProvider {
    
    private static final long serialVersionUID = -1654118204678581940L;

    public static final String BEGIN = "BEGIN";

    public static final String VCALENDAR = "VCALENDAR";

    public static final String END = "END";

    private PropertyList properties;

    private ComponentList components;

    /**
     * Default constructor.
     */
    public Calendar() {
        this(new PropertyList(), new ComponentList());
    }

    /**
     * Constructs a new calendar with no properties and
     * the specified components.
     * @param components a list of components to add to
     * the calendar
     */
    public Calendar(final ComponentList components) {
        this(new PropertyList(), components);
    }

    /**
     * Constructor.
     *
     * @param p
     *            a list of properties
     * @param c
     *            a list of components
     */
    public Calendar(final PropertyList p, final ComponentList c) {
        this.properties = p;
        this.components = c;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(BEGIN);
        buffer.append(':');
        buffer.append(VCALENDAR);
        buffer.append("\r\n");
        buffer.append(getProperties());
        buffer.append(getComponents());
        buffer.append(END);
        buffer.append(':');
        buffer.append(VCALENDAR);
        buffer.append("\r\n");

        return buffer.toString();
    }

    /**
     * @return Returns the components.
     */
    public final ComponentList getComponents() {
        return components;
    }

    /**
     * @return Returns the properties.
     */
    public final PropertyList getProperties() {
        return properties;
    }

    /**
     * Perform validation on the calendar, its properties
     * and its components in its current state.
     * @throws ValidationException
     *             where the calendar is not in a valid state
     */
    public final void validate() throws ValidationException {
        validate(true);
    }

    /**
     * Perform validation on the calendar in its current state.
     * @param recurse indicates whether to validate the calendar's
     * properties and components
     * @throws ValidationException
     *             where the calendar is not in a valid state
     */
    public final void validate(final boolean recurse) throws ValidationException {
        // 'prodid' and 'version' are both REQUIRED,
        // but MUST NOT occur more than once
        PropertyValidator.getInstance().validateOne(Property.PRODID, properties);
        PropertyValidator.getInstance().validateOne(Property.VERSION, properties);

        // 'calscale' and 'method' are optional,
        // but MUST NOT occur more than once
        PropertyValidator.getInstance()
                .validateOneOrLess(Property.CALSCALE, properties);
        PropertyValidator.getInstance().validateOneOrLess(Property.METHOD, properties);

        // must contain at least one component
        if (getComponents().isEmpty()) { throw new ValidationException(
                "Calendar must contain at least one component"); }

        // validate properties..
        for (Iterator i = getProperties().iterator(); i.hasNext();) {
            Property property = (Property) i.next();

            if (!(property instanceof XProperty)
                 && !property.isCalendarProperty()) {
                throw new IllegalArgumentException(
                    "Invalid property: " + property.getName());
            }
        }

        // validate components..
        for (Iterator i = getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();

            if (!component.isCalendarComponent()) {
                throw new IllegalArgumentException(
                    "Invalid component: " + component.getName());
            }
        }

        if (recurse) {
            validateProperties();
            validateComponents();
        }
    }

    /**
     * Invoke validation on the calendar properties in its current state.
     * @throws ValidationException
     *             where any of the calendar properties is not in a valid state
     */
    private void validateProperties() throws ValidationException {
        for (Iterator i = getProperties().iterator(); i.hasNext();) {
            Property property = (Property) i.next();
            property.validate();
        }
    }

    /**
     * Invoke validation on the calendar components in its current state.
     * @throws ValidationException
     *             where any of the calendar components is not in a valid state
     */
    private void validateComponents() throws ValidationException {
        for (Iterator i = getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            component.validate();
        }
    }
    
    /**
     * Two calendars are equal if and only if their
     * property lists and component lists are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof Calendar) {
            Calendar calendar = (Calendar) arg0;
            return getProperties().equals(calendar.getProperties())
                    && getComponents().equals(calendar.getComponents());
        }
        return super.equals(arg0);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        return getProperties().hashCode() + getComponents().hashCode();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.TimeZoneProvider#getVTimeZone(net.fortuna.ical4j.model.parameter.TzId)
     */
    public final VTimeZone getVTimeZone(final TzId tzId) {
        ComponentList vTimeZones = getComponents().getComponents(Component.VTIMEZONE);
        for (Iterator i = vTimeZones.iterator(); i.hasNext();) {
            VTimeZone vTimeZone = (VTimeZone) i.next();
            if (tzId.getValue().equals(vTimeZone.getProperties().getProperty(Property.TZID).getValue())) {
                return vTimeZone;
            }
        }
        return VTimeZone.getVTimeZone(tzId.getValue());
    }
}
