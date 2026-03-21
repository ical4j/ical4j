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

import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.model.Property.UID;

/**
 * $Id$ [Apr 5, 2004]
 * <p/>
 * Defines an iCalendar component. Subclasses of this class provide additional validation and typed values for specific
 * iCalendar components.
 *
 * @author Ben Fortuna
 */
public abstract class Component extends Content implements Serializable,
        PropertyContainer, FluentComponent, Comparable<Component> {

    private static final long serialVersionUID = 4943193483665822201L;

    /**
     * Component start token.
     */
    public static final String BEGIN = "BEGIN";

    /**
     * Component end token.
     */
    public static final String END = "END";

    /**
     * Component token.
     */
    public static final String VSTATUS = "VSTATUS";

    /**
     * Component token.
     */
    public static final String PARTICIPANT = "PARTICIPANT";

    /**
     * Component token.
     */
    public static final String VLOCATION = "VLOCATION";

    /**
     * Component token.
     */
    public static final String VRESOURCE = "VRESOURCE";

    /**
     * Component token.
     */
    public static final String VEVENT = "VEVENT";

    /**
     * Component token.
     */
    public static final String VTODO = "VTODO";

    /**
     * Component token.
     */
    public static final String VJOURNAL = "VJOURNAL";

    /**
     * Component token.
     */
    public static final String VFREEBUSY = "VFREEBUSY";

    /**
     * Component token.
     */
    public static final String VTIMEZONE = "VTIMEZONE";

    /**
     * Component token.
     */
    public static final String VALARM = "VALARM";

    /**
     * Component token.
     */
    public static final String VAVAILABILITY = "VAVAILABILITY";

    /**
     * Component token.
     */
    public static final String VVENUE = "VVENUE";

    /**
     * Component token.
     */
    public static final String AVAILABLE = "AVAILABLE";

    /**
     * Prefix for non-standard components.
     */
    public static final String EXPERIMENTAL_PREFIX = "X-";

    private final String name;

    protected PropertyList properties;

    protected ComponentList<? extends Component> components;

    /**
     * Constructs a new component containing no properties.
     *
     * @param s a component name
     */
    protected Component(final String s) {
        this(s, new PropertyList(), new ComponentList<>());
    }

    protected Component(final String s, final PropertyList p) {
        this(s, p, new ComponentList<>());
    }

    /**
     * Constructor made protected to enforce the use of <code>ComponentFactory</code> for component instantiation.
     *
     * @param s component name
     * @param p a list of properties
     */
    protected Component(final String s, final PropertyList p, ComponentList<? extends Component> c) {
        this.name = s;
        this.properties = p;
        this.components = c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public String toString() {
        return BEGIN + ':' + name + Strings.LINE_SEPARATOR + properties + components + END + ':' + name
                + Strings.LINE_SEPARATOR;
    }

    /**
     * @return Returns the name.
     */
    @Override
    public final String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return properties.toString();
    }

    public <T extends Property> List<T> getProperties() {
        return PropertyContainer.super.getProperties();
    }

    @Override
    public Component getFluentTarget() {
        return this;
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
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public Optional<Uid> getUid() {
        return getProperty(UID);
    }

    /**
     * Perform validation on a component and its properties.
     *
     * @throws ValidationException where the component is not in a valid state
     */
    public ValidationResult validate() throws ValidationException {
        return validate(true);
    }

    /**
     * Perform validation on a component.
     *
     * @param recurse indicates whether to validate the component's properties
     * @throws ValidationException where the component is not in a valid state
     */
    public abstract ValidationResult validate(final boolean recurse) throws ValidationException;

    /**
     * Invoke validation on the component properties in its current state.
     *
     * @throws ValidationException where any of the component properties is not in a valid state
     */
    protected ValidationResult validateProperties() throws ValidationException {
        var result = new ValidationResult();
        for (final var property : getProperties()) {
            result = result.merge(property.validate());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public boolean equals(final Object arg0) {
        if (arg0 instanceof Component) {
            final var c = (Component) arg0;
            return new EqualsBuilder().append(getName(), c.getName())
                    .append(getProperties(), c.getProperties()).isEquals();
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getProperties())
                .toHashCode();
    }

    /**
     * Returns a new component factory used to create deep copies.
     * @return a component factory instance
     */
    protected abstract ComponentFactory<? extends Component> newFactory();

    /**
     * Create a (deep) copy of this component.
     * @return the component copy
     */
    public Component copy() {
        return newFactory().createComponent(new PropertyList(getProperties().parallelStream()
                .map(Property::copy).collect(Collectors.toList())));
    }

    /**
     * Calculates the recurrence set for this component using the specified period.
     * The recurrence set is derived from a combination of the component start date,
     * recurrence rules and dates, and exception rules and dates. Note that component
     * transparency and anniversary-style dates do not affect the resulting
     * intersection.
     *
     * <p>If an explicit DURATION is not specified, the effective duration of each
     * returned period is derived from the DTSTART and DTEND or DUE properties.
     * If the component has no DURATION, DTEND or DUE, the effective duration is set
     * to PT0S</p>
     *
     * NOTE: As a component may be defined in terms of floating date-time values (i.e. without a specific
     * timezone), when calculating a recurrence set we must explicitly provide an applicable timezone
     * for calculations.
     *
     * @param period a range that defines the boundary for calculations
     * @return a set of periods representing component occurrences within the specified boundary
     */
    public final <T extends Temporal> Set<Period<T>> calculateRecurrenceSet(final Period<? extends Temporal> period) {

        final Optional<DtStart<T>> start = getProperty(DTSTART);
        Optional<DateProperty<T>> end = getProperty(DTEND);
        if (end.isEmpty()) {
            end = getProperty(DUE);
        }
        Optional<Duration> duration = getProperty(DURATION);

        // if no start date specified return empty list..
        if (start.isEmpty()) {
            return Collections.emptySet();
        }

        RecurrenceSet.Builder<T> builder = new RecurrenceSet.Builder<>();
        builder.start(start.map(DtStart::getDate).orElse(null))
                .duration(duration.map(Duration::getDuration).orElse(null))
                .end(end.map(DateProperty::getDate).orElse(null))
                .period(period);

        // add recurrence dates..
        for (var p : getProperties(RDATE)) {
            Optional<Value> value = p.getParameter(Parameter.VALUE);
            if (value.equals(Optional.of(Value.PERIOD))) {
                builder.recurrencePeriods(((RDate<T>) p).getPeriods().orElse(Collections.emptySet()));
            } else {
                builder.recurrenceDates(((DateListProperty<T>) p).getDates());
            }
        }

        // add recurrence rules..
        builder.recurrenceRules(getProperties(RRULE).stream().map(r -> ((RRule<T>) r).getRecur())
                .collect(Collectors.toList()));

        // subtract exception dates..
        builder.exceptionDates(getProperties(EXDATE).stream().map(p -> ((DateListProperty<T>) p).getDates())
                .flatMap(List<T>::stream).collect(Collectors.toList()));

        // subtract exception rules..
        builder.exceptionRules(getProperties(EXRULE).stream().map(r -> ((ExRule<T>) r).getRecur())
                .collect(Collectors.toList()));

        final Set<Period<T>> recurrenceSet = builder.build();
        // set a link to the origin
        recurrenceSet.forEach( p -> p.setComponent(this));

        return recurrenceSet;
    }

    @Override
    public int compareTo(@NotNull Component o) {
        if (this.equals(o)) {
            return 0;
        }
        return Comparator.comparing(Component::getName)
                .thenComparing(Component::getPropertyList)
                .compare(this, o);
    }
}
