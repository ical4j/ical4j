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
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.AbstractCalendarValidatorFactory;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
 * calendar.add(new ProdId(&quot;-//Ben Fortuna//iCal4j 1.0//EN&quot;));
 * calendar.add(Version.VERSION_2_0);
 * calendar.add(CalScale.GREGORIAN);
 * 
 * // Add events, etc..
 * </code></pre>
 * 
 * @author Ben Fortuna
 */
public class Calendar implements Prototype<Calendar>, Serializable, PropertyContainer, ComponentContainer<CalendarComponent>,
    FluentCalendar, CalendarPropertyAccessor {

    private static final long serialVersionUID = -1654118204678581940L;

    /**
     * Smart merging of properties that identifies whether to add or replace existing properties.
     */
    public static final BiFunction<Calendar, List<Property>, Calendar> MERGE_PROPERTIES = (c, list) -> {
        if (list != null && !list.isEmpty()) {
            list.forEach(p -> {
                switch (p.getName()) {
                    case Property.VERSION:
                    case Property.METHOD:
                    case Property.PRODID:
                        c.replace(p.copy());
                        break;
                    default:
                        c.add(p.copy());
                }
            });
        }
        return c;
    };

    public static final BiFunction<Calendar, List<CalendarComponent>, Calendar> MERGE_COMPONENTS = (c, list) -> {
        list.forEach(component -> {
            if (!c.getComponents().contains(component)) {
                c.add((CalendarComponent) component.copy());
            }
        });
        return c;
    };

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

    private ComponentList<CalendarComponent> components;

    private final Validator<Calendar> validator;

    /**
     * Default constructor.
     */
    public Calendar() {
        this(new PropertyList(), new ComponentList<>());
    }

    /**
     * Constructs a new calendar with no properties and the specified components.
     * @param components a list of components to add to the calendar
     */
    public Calendar(final ComponentList<? extends CalendarComponent> components) {
        this(new PropertyList(), components);
    }

    /**
     * Initialise a Calendar object using the default configured validator.
     * @param properties a list of initial calendar properties
     * @param components a list of initial calendar components
     */
    public Calendar(PropertyList properties, ComponentList<? extends CalendarComponent> components) {
        this(properties, components, AbstractCalendarValidatorFactory.getInstance().newInstance());
    }

    /**
     * Constructor.
     * @param p a list of properties
     * @param c a list of components
     * @param validator used to ensure the validity of the calendar instance
     */
    public Calendar(PropertyList p, ComponentList<? extends CalendarComponent> c, Validator<Calendar> validator) {
        this.properties = p;
        this.components = new ComponentList<>(c.getAll());
        this.validator = validator;
    }

    /**
     * Creates a shallow copy of the specified calendar.
     * @param c the calendar to copy
     */
    public Calendar(Calendar c) {
        this(c.properties, c.components);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return BEGIN + ':' + VCALENDAR + Strings.LINE_SEPARATOR + properties + components + END + ':' + VCALENDAR +
                Strings.LINE_SEPARATOR;
    }

    @Override
    public Calendar getFluentTarget() {
        return this;
    }

    public <C extends CalendarComponent> List<C> getComponents() {
        return ComponentContainer.super.getComponents();
    }

    /**
     * @return Returns the underlying component list.
     */
    @Override
    public final ComponentList<CalendarComponent> getComponentList() {
        return components;
    }

    @Override
    public void setComponentList(ComponentList<CalendarComponent> components) {
        this.components = components;
    }

    /**
     * Explicitly override due to conflict with groovy implicit method..
     * @return
     * @param <T>
     */
    public <T extends Property> List<T> getProperties() {
        return CalendarPropertyAccessor.super.getProperties();
    }

    /**
     * @return Returns the underlying property list.
     */
    @Override
    public final PropertyList getPropertyList() {
        return properties;
    }

    @Override
    public void setPropertyList(PropertyList properties) {
        this.properties = properties;
    }

    /**
     * Perform validation on the calendar, its properties and its components in its current state.
     * @throws ValidationException where the calendar is not in a valid state
     */
    public ValidationResult validate() throws ValidationException {
        return validate(true);
    }

    /**
     * Perform validation on the calendar in its current state.
     * @param recurse indicates whether to validate the calendar's properties and components
     * @throws ValidationException where the calendar is not in a valid state
     */
    public ValidationResult validate(final boolean recurse) throws ValidationException {
        var result = validator.validate(this);
        if (recurse) {
            result = result.merge(validateProperties());
            result = result.merge(validateComponents());
        }
        return result;
    }

    /**
     * Invoke validation on the calendar properties in its current state.
     * @throws ValidationException where any of the calendar properties is not in a valid state
     */
    private ValidationResult validateProperties() throws ValidationException {
        var result = new ValidationResult();
        for (final Property property : getProperties()) {
            result = result.merge(property.validate());
        }
        return result;
    }

    /**
     * Invoke validation on the calendar components in its current state.
     * @throws ValidationException where any of the calendar components is not in a valid state
     */
    private ValidationResult validateComponents() throws ValidationException {
        var result = new ValidationResult();
        Optional<Method> method = getProperty(Property.METHOD);
        if (method.isPresent()) {
            for (var c : getComponents()) {
                result = result.merge(c.validate(method.get()));
            }
        } else {
            for (var c : getComponents()) {
                result = result.merge(c.validate());
            }
        }
        return result;
    }

    /**
     * Creates a deep copy of the calendar.
     * @return a new calendar instance that protects against mutation of the source calendar
     */
    public final Calendar copy() {
        return new Calendar(
                new PropertyList(getProperties().parallelStream()
                        .map(Property::copy).collect(Collectors.toList())),
                new ComponentList<>(getComponents().parallelStream()
                    .map(c -> (CalendarComponent) c.copy()).collect(Collectors.toList())));
    }

    /**
     * Merge all properties and components from the specified calendar with this instance.
     * Note that the merge process is not very sophisticated, and may result in invalid calendar
     * data (e.g. multiple properties of a type that should only be specified once).
     * @param c2 the second calendar to merge
     * @return a Calendar instance containing all properties and components from both of the specified calendars
     */
    public Calendar merge(final Calendar c2) {
        Calendar copy = copy();
        copy.with(MERGE_PROPERTIES, c2.getProperties());
        copy.with(MERGE_COMPONENTS, c2.getComponents());
        return copy;
    }

    /**
     * Splits a calendar object into distinct calendar objects for unique identifiers (UID).
     * @return an array of calendar objects
     */
    public Calendar[] split() {
        // if calendar contains one component or less, or is composed entirely of timezone
        // definitions, return the original calendar unmodified..
        if (getComponents().size() <= 1
                || getComponents(Component.VTIMEZONE).size() == getComponents().size()) {
            return new Calendar[] {this};
        }

        final List<VTimeZone> timezoneList = getComponents(Component.VTIMEZONE);
        final IndexedComponentList<VTimeZone> timezones = new IndexedComponentList<>(
                timezoneList, Property.TZID);

        final Map<Uid, Calendar> calendars = new HashMap<>();
        for (final var c : getComponents()) {
            if (c instanceof VTimeZone) {
                continue;
            }

            final Optional<Uid> uid = c.getUid();
            if (uid.isPresent()) {
                var uidCal = calendars.get(uid.get());
                if (uidCal == null) {
                    // remove METHOD property for split calendars..
                    PropertyList splitProps = (PropertyList) getPropertyList().removeAll(Property.METHOD);
                    uidCal = new Calendar(splitProps, new ComponentList<>());
                    calendars.put(uid.get(), uidCal);
                }

                for (final var p : c.getProperties()) {
                    final Optional<TzId> tzid = p.getParameter(Parameter.TZID);
                    if (tzid.isPresent()) {
                        final VTimeZone timezone = timezones.getComponent(tzid.get().getValue());
                        if (!uidCal.getComponents().contains(timezone)) {
                            uidCal.add(timezone);
                        }
                    }
                }
                uidCal.add(c);
            }
        }
        return calendars.values().toArray(Calendar[]::new);
    }

    /**
     * Returns a unique identifier as specified by components in the calendar instance.
     * @return the UID property
     * @throws ConstraintViolationException if zero or more than one unique identifier(s) is
     * found in the specified calendar
     */
    public Uid getUid() throws ConstraintViolationException {
        Uid uid = null;
        for (final var c : components.getAll()) {
            for (final var foundUid : c.getProperties(Property.UID)) {
                if (uid != null && !uid.equals(foundUid)) {
                    throw new ConstraintViolationException("More than one UID found in calendar");
                }
                uid = (Uid) foundUid;
            }
        }
        if (uid == null) {
            throw new ConstraintViolationException("Calendar must specify a single unique identifier (UID)");
        }
        return uid;
    }

    /**
     * Returns an appropriate MIME Content-Type for the calendar object instance.
     * @param charset an optional encoding
     * @return a content type string
     */
    public String getContentType(Charset charset) {
        final var b = new StringBuilder("text/calendar");

        final Optional<Method> method = getProperty(Property.METHOD);
        if (method.isPresent()) {
            b.append("; method=");
            b.append(method.get().getValue());
        }

        if (charset != null) {
            b.append("; charset=");
            b.append(charset);
        }
        return b.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof Calendar) {
            final var calendar = (Calendar) arg0;
            return new EqualsBuilder().append(getProperties(), calendar.getProperties())
                .append(components, calendar.components).isEquals();
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return new HashCodeBuilder().append(properties).append(components).toHashCode();
    }
}
