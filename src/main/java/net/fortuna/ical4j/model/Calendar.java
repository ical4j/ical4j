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
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Iterator;

import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.ComponentValidator;
import net.fortuna.ical4j.util.PropertyValidator;
import net.fortuna.ical4j.util.Strings;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines an iCalendar calendar.
 * 
 * <pre>
 *    4.6 Calendar Components
 *    
 *       The body of the iCalendar object consists of a sequence of calendar
 *       properties and one or more calendar components. The calendar
 *       properties are attributes that apply to the calendar as a whole. The
 *       calendar components are collections of properties that express a
 *       particular calendar semantic. For example, the calendar component can
 *       specify an event, a to-do, a journal entry, time zone information, or
 *       free/busy time information, or an alarm.
 *    
 *       The body of the iCalendar object is defined by the following
 *       notation:
 *    
 *         icalbody   = calprops component
 *    
 *         calprops   = 2*(
 *    
 *                    ; 'prodid' and 'version' are both REQUIRED,
 *                    ; but MUST NOT occur more than once
 *    
 *                    prodid /version /
 *    
 *                    ; 'calscale' and 'method' are optional,
 *                    ; but MUST NOT occur more than once
 *    
 *                    calscale        /
 *                    method          /
 *    
 *                    x-prop
 *    
 *                    )
 *    
 *         component  = 1*(eventc / todoc / journalc / freebusyc /
 *                    / timezonec / iana-comp / x-comp)
 *    
 *         iana-comp  = &quot;BEGIN&quot; &quot;:&quot; iana-token CRLF
 *    
 *                      1*contentline
 *    
 *                      &quot;END&quot; &quot;:&quot; iana-token CRLF
 *    
 *         x-comp     = &quot;BEGIN&quot; &quot;:&quot; x-name CRLF
 *    
 *                      1*contentline
 *    
 *                      &quot;END&quot; &quot;:&quot; x-name CRLF
 * </pre>
 * 
 * Example 1 - Creating a new calendar:
 * 
 * <pre><code>
 * Calendar calendar = new Calendar();
 * calendar.getProperties().add(new ProdId(&quot;-//Ben Fortuna//iCal4j 1.0//EN&quot;));
 * calendar.getProperties().add(Version.VERSION_2_0);
 * calendar.getProperties().add(CalScale.GREGORIAN);
 * 
 * // Add events, etc..
 * </code></pre>
 * 
 * @author Ben Fortuna
 */
public class Calendar implements Serializable {

    private static final long serialVersionUID = -1654118204678581940L;

    /**
     * Begin token.
     */
    public static final String BEGIN = "BEGIN";

    /**
     * Calendar token.
     */
    public static final String VCALENDAR = "VCALENDAR";

    /**
     * End token.
     */
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
     * Constructs a new calendar with no properties and the specified components.
     * @param components a list of components to add to the calendar
     */
    public Calendar(final ComponentList components) {
        this(new PropertyList(), components);
    }

    /**
     * Constructor.
     * @param p a list of properties
     * @param c a list of components
     */
    public Calendar(final PropertyList p, final ComponentList c) {
        this.properties = p;
        this.components = c;
    }

    /**
     * Creates a deep copy of the specified calendar.
     * @param c the calendar to copy
     * @throws IOException where an error occurs reading calendar data
     * @throws ParseException where calendar parsing fails
     * @throws URISyntaxException where an invalid URI string is encountered
     */
    public Calendar(Calendar c) throws ParseException, IOException,
            URISyntaxException {
        
        this(new PropertyList(c.getProperties()), new ComponentList(c
                .getComponents()));
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(BEGIN);
        buffer.append(':');
        buffer.append(VCALENDAR);
        buffer.append(Strings.LINE_SEPARATOR);
        buffer.append(getProperties());
        buffer.append(getComponents());
        buffer.append(END);
        buffer.append(':');
        buffer.append(VCALENDAR);
        buffer.append(Strings.LINE_SEPARATOR);

        return buffer.toString();
    }

    /**
     * @return Returns the components.
     */
    public final ComponentList getComponents() {
        return components;
    }

    /**
     * Convenience method for retrieving a list of named components.
     * @param name name of components to retrieve
     * @return a component list containing only components with the specified name
     */
    public final ComponentList getComponents(final String name) {
        return getComponents().getComponents(name);
    }

    /**
     * Convenience method for retrieving a named component.
     * @param name name of the component to retrieve
     * @return the first matching component in the component list with the specified name
     */
    public final Component getComponent(final String name) {
        return getComponents().getComponent(name);
    }

    /**
     * @return Returns the properties.
     */
    public final PropertyList getProperties() {
        return properties;
    }

    /**
     * Convenience method for retrieving a list of named properties.
     * @param name name of properties to retrieve
     * @return a property list containing only properties with the specified name
     */
    public final PropertyList getProperties(final String name) {
        return getProperties().getProperties(name);
    }

    /**
     * Convenience method for retrieving a named property.
     * @param name name of the property to retrieve
     * @return the first matching property in the property list with the specified name
     */
    public final Property getProperty(final String name) {
        return getProperties().getProperty(name);
    }

    /**
     * Perform validation on the calendar, its properties and its components in its current state.
     * @throws ValidationException where the calendar is not in a valid state
     */
    public final void validate() throws ValidationException {
        validate(true);
    }

    /**
     * Perform validation on the calendar in its current state.
     * @param recurse indicates whether to validate the calendar's properties and components
     * @throws ValidationException where the calendar is not in a valid state
     */
    public void validate(final boolean recurse) throws ValidationException {
        // 'prodid' and 'version' are both REQUIRED,
        // but MUST NOT occur more than once
        PropertyValidator.getInstance().assertOne(Property.PRODID, properties);
        PropertyValidator.getInstance().assertOne(Property.VERSION, properties);

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            // require VERSION:2.0 for RFC2445..
            if (!Version.VERSION_2_0.equals(getProperty(Property.VERSION))) {
                throw new ValidationException("Unsupported Version: " + getProperty(Property.VERSION).getValue());
            }
        }
        
        // 'calscale' and 'method' are optional,
        // but MUST NOT occur more than once
        PropertyValidator.getInstance().assertOneOrLess(Property.CALSCALE,
                properties);
        PropertyValidator.getInstance().assertOneOrLess(Property.METHOD,
                properties);

        // must contain at least one component
        if (getComponents().isEmpty()) {
            throw new ValidationException(
                    "Calendar must contain at least one component");
        }

        // validate properties..
        for (final Iterator i = getProperties().iterator(); i.hasNext();) {
            final Property property = (Property) i.next();

            if (!(property instanceof XProperty)
                    && !property.isCalendarProperty()) {
                throw new ValidationException("Invalid property: "
                        + property.getName());
            }
        }

        // validate components..
        for (final Iterator i = getComponents().iterator(); i.hasNext();) {
            final Component component = (Component) i.next();
            if (!(component instanceof CalendarComponent)) {
                throw new ValidationException("Not a valid calendar component: " + component.getName());
            }
        }

//        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            // validate method..
            final Method method = (Method) getProperty(Property.METHOD);
            if (Method.PUBLISH.equals(method)) {
                if (getComponent(Component.VEVENT) != null) {
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    
                    if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
                        ComponentValidator.assertNone(Component.VTODO, getComponents());
                    }
                }
                else if (getComponent(Component.VFREEBUSY) != null) {
                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTIMEZONE, getComponents());
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                }
                else if (getComponent(Component.VTODO) != null) {
//                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                    ComponentValidator.assertNone(Component.VEVENT, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                }
                else if (getComponent(Component.VJOURNAL) != null) {
//                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                    ComponentValidator.assertNone(Component.VEVENT, getComponents());
//                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                }
            }
            else if (Method.REQUEST.equals(getProperty(Property.METHOD))) {
                if (getComponent(Component.VEVENT) != null) {
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                }
                else if (getComponent(Component.VFREEBUSY) != null) {
                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTIMEZONE, getComponents());
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                }
                else if (getComponent(Component.VTODO) != null) {
//                  ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                }
            }
            else if (Method.REPLY.equals(getProperty(Property.METHOD))) {
                if (getComponent(Component.VEVENT) != null) {
                    ComponentValidator.assertOneOrLess(Component.VTIMEZONE, getComponents());
                    
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                }
                else if (getComponent(Component.VFREEBUSY) != null) {
                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTIMEZONE, getComponents());
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                }
                else if (getComponent(Component.VTODO) != null) {
                    ComponentValidator.assertOneOrLess(Component.VTIMEZONE, getComponents());
                    
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
//                  ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                }
            }
            else if (Method.ADD.equals(getProperty(Property.METHOD))) {
                if (getComponent(Component.VEVENT) != null) {
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                }
                else if (getComponent(Component.VTODO) != null) {
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                }
                else if (getComponent(Component.VJOURNAL) != null) {
                    ComponentValidator.assertOneOrLess(Component.VTIMEZONE, getComponents());
                    
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, getComponents());
//                  ComponentValidator.assertNone(Component.VTODO, getComponents());
                }
            }
            else if (Method.CANCEL.equals(getProperty(Property.METHOD))) {
                if (getComponent(Component.VEVENT) != null) {
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                }
                else if (getComponent(Component.VTODO) != null) {
                    ComponentValidator.assertOneOrLess(Component.VTIMEZONE, getComponents());
                    
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                }
                else if (getComponent(Component.VJOURNAL) != null) {
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, getComponents());
//                  ComponentValidator.assertNone(Component.VTODO, getComponents());
                }
            }
            else if (Method.REFRESH.equals(getProperty(Property.METHOD))) {
                if (getComponent(Component.VEVENT) != null) {
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                }
                else if (getComponent(Component.VTODO) != null) {
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTIMEZONE, getComponents());
                }
            }
            else if (Method.COUNTER.equals(getProperty(Property.METHOD))) {
                if (getComponent(Component.VEVENT) != null) {
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                }
                else if (getComponent(Component.VTODO) != null) {
                    ComponentValidator.assertOneOrLess(Component.VTIMEZONE, getComponents());
                    
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                }
            }
            else if (Method.DECLINE_COUNTER.equals(getProperty(Property.METHOD))) {
                if (getComponent(Component.VEVENT) != null) {
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                    ComponentValidator.assertNone(Component.VTODO, getComponents());
                    ComponentValidator.assertNone(Component.VTIMEZONE, getComponents());
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                }
                else if (getComponent(Component.VTODO) != null) {
                    ComponentValidator.assertNone(Component.VALARM, getComponents());
                    ComponentValidator.assertNone(Component.VFREEBUSY, getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, getComponents());
                    ComponentValidator.assertNone(Component.VJOURNAL, getComponents());
                }
            }
//        }
            
            // perform ITIP validation on components..
            if (method != null) {
                for (final Iterator i = getComponents().iterator(); i.hasNext();) {
                    final CalendarComponent component = (CalendarComponent) i.next();
                    component.validate(method);
                }
            }
        
        if (recurse) {
            validateProperties();
            validateComponents();
        }
    }

    /**
     * Invoke validation on the calendar properties in its current state.
     * @throws ValidationException where any of the calendar properties is not in a valid state
     */
    private void validateProperties() throws ValidationException {
        for (final Iterator i = getProperties().iterator(); i.hasNext();) {
            final Property property = (Property) i.next();
            property.validate();
        }
    }

    /**
     * Invoke validation on the calendar components in its current state.
     * @throws ValidationException where any of the calendar components is not in a valid state
     */
    private void validateComponents() throws ValidationException {
        for (final Iterator i = getComponents().iterator(); i.hasNext();) {
            final Component component = (Component) i.next();
            component.validate();
        }
    }

    /**
     * Returns the mandatory prodid property.
     * @return the PRODID property, or null if property doesn't exist
     */
    public final ProdId getProductId() {
        return (ProdId) getProperty(Property.PRODID);
    }

    /**
     * Returns the mandatory version property.
     * @return the VERSION property, or null if property doesn't exist
     */
    public final Version getVersion() {
        return (Version) getProperty(Property.VERSION);
    }

    /**
     * Returns the optional calscale property.
     * @return the CALSCALE property, or null if property doesn't exist
     */
    public final CalScale getCalendarScale() {
        return (CalScale) getProperty(Property.CALSCALE);
    }

    /**
     * Returns the optional method property.
     * @return the METHOD property, or null if property doesn't exist
     */
    public final Method getMethod() {
        return (Method) getProperty(Property.METHOD);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof Calendar) {
            final Calendar calendar = (Calendar) arg0;
            return new EqualsBuilder().append(getProperties(), calendar.getProperties())
                .append(getComponents(), calendar.getComponents()).isEquals();
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    public final int hashCode() {
        return new HashCodeBuilder().append(getProperties()).append(
                getComponents()).toHashCode();
    }
}
