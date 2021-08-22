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

import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.AbstractCalendarValidatorFactory;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;

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
public class Calendar implements Serializable, PropertyContainer, ComponentContainer<CalendarComponent> {

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

    private final PropertyList<Property> properties;

    private final ComponentList<CalendarComponent> components;

    private final Validator<Calendar> validator;

    /**
     * Default constructor.
     */
    public Calendar() {
        this(new PropertyList<Property>(), new ComponentList<CalendarComponent>());
    }

    /**
     * Constructs a new calendar with no properties and the specified components.
     * @param components a list of components to add to the calendar
     */
    public Calendar(final ComponentList<CalendarComponent> components) {
        this(new PropertyList<Property>(), components);
    }

    /**
     * Initialise a Calendar object using the default configured validator.
     * @param properties a list of initial calendar properties
     * @param components a list of initial calendar components
     */
    public Calendar(PropertyList<Property> properties, ComponentList<CalendarComponent> components) {
        this(properties, components, AbstractCalendarValidatorFactory.getInstance().newInstance());
    }

    /**
     * Constructor.
     * @param p a list of properties
     * @param c a list of components
     * @param validator used to ensure the validity of the calendar instance
     */
    public Calendar(PropertyList<Property> p, ComponentList<CalendarComponent> c, Validator<Calendar> validator) {
        this.properties = p;
        this.components = c;
        this.validator = validator;
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
        
        this(new PropertyList<Property>(c.getProperties()),
        		new ComponentList<CalendarComponent>(c.getComponents()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return BEGIN +
                ':' +
                VCALENDAR +
                Strings.LINE_SEPARATOR +
                getProperties() +
                getComponents() +
                END +
                ':' +
                VCALENDAR +
                Strings.LINE_SEPARATOR;
    }

    /**
     * @return Returns the components.
     */
    public final ComponentList<CalendarComponent> getComponents() {
        return components;
    }

    /**
     * @return Returns the properties.
     */
    public final PropertyList<Property> getProperties() {
        return properties;
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
        validator.validate(this);
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
        for (final Property property : getProperties()) {
            property.validate();
        }
    }

    /**
     * Invoke validation on the calendar components in its current state.
     * @throws ValidationException where any of the calendar components is not in a valid state
     */
    private void validateComponents() throws ValidationException {
        for (Component component : getComponents()) {
            component.validate();
        }
    }

    /**
     * Returns the mandatory prodid property.
     * @return the PRODID property, or null if property doesn't exist
     */
    public final ProdId getProductId() {
        return getProperty(Property.PRODID);
    }

    /**
     * Returns the mandatory version property.
     * @return the VERSION property, or null if property doesn't exist
     */
    public final Version getVersion() {
        return getProperty(Property.VERSION);
    }

    /**
     * Returns the optional calscale property.
     * @return the CALSCALE property, or null if property doesn't exist
     */
    public final CalScale getCalendarScale() {
        return getProperty(Property.CALSCALE);
    }

    /**
     * Returns the optional method property.
     * @return the METHOD property, or null if property doesn't exist
     */
    public final Method getMethod() {
        return getProperty(Property.METHOD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public final int hashCode() {
        return new HashCodeBuilder().append(getProperties()).append(
                getComponents()).toHashCode();
    }
}
