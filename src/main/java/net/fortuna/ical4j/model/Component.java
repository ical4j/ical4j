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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jooq.lambda.Unchecked;

import java.io.Serializable;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.stream.Collectors;

/**
 * $Id$ [Apr 5, 2004]
 * <p/>
 * Defines an iCalendar component. Subclasses of this class provide additional validation and typed values for specific
 * iCalendar components.
 *
 * @author Ben Fortuna
 */
public abstract class Component extends Content implements Serializable, PropertyContainer {

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
        return BEGIN + ':' + name + Strings.LINE_SEPARATOR + properties + components + END + ':' + name +
                Strings.LINE_SEPARATOR;
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

    /**
     * @return Returns the properties.
     * @deprecated to avoid confusion with how to mutate a PropertyList from v4.x onwards this method is temporarily
     * deprecated.
     * @see PropertyContainer#add(Property)
     * @see PropertyContainer#remove(Property)
     * @see PropertyContainer#removeAll(String...)
     * @see PropertyContainer#replace(Property)
     */
    @Deprecated
    @Override
    public final PropertyList getPropertyList() {
        return properties;
    }

    @Override
    public void setProperties(PropertyList properties) {
        this.properties = properties;
    }

    /**
     * Perform validation on a component and its properties.
     *
     * @throws ValidationException where the component is not in a valid state
     */
    public final void validate() throws ValidationException {
        validate(true);
    }

    /**
     * Perform validation on a component.
     *
     * @param recurse indicates whether to validate the component's properties
     * @throws ValidationException where the component is not in a valid state
     */
    public abstract void validate(final boolean recurse) throws ValidationException;

    /**
     * Invoke validation on the component properties in its current state.
     *
     * @throws ValidationException where any of the component properties is not in a valid state
     */
    protected final void validateProperties() throws ValidationException {
        for (final Property property : getProperties()) {
            property.validate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public boolean equals(final Object arg0) {
        if (arg0 instanceof Component) {
            final Component c = (Component) arg0;
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
    public <T extends Component> T copy() {
        return (T) newFactory().createComponent(new PropertyList(properties.getAll().parallelStream()
                .map(Unchecked.function(Property::copy)).collect(Collectors.toList())));
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
     * @return a list of periods representing component occurrences within the specified boundary
     */
    public final <T extends Temporal> Set<Period<T>> calculateRecurrenceSet(final Period<T> period) {

        final Set<Period<T>> recurrenceSet = new TreeSet<>();

        final Optional<DtStart<T>> start = getProperty(Property.DTSTART);
        Optional<DateProperty<T>> end = getProperty(Property.DTEND);
        if (!end.isPresent()) {
            end = getProperty(Property.DUE);
        }
        Optional<Duration> duration = getProperty(Property.DURATION);

        // if no start date specified return empty list..
        if (!start.isPresent()) {
            return Collections.emptySet();
        }

        // if an explicit event duration is not specified, derive a value for recurring
        // periods from the end date..
        TemporalAmount rDuration;
        // if no end or duration specified, end date equals start date..
        if (!end.isPresent() && !duration.isPresent()) {
            rDuration = java.time.Duration.ZERO;
        } else if (!duration.isPresent()) {
            rDuration = TemporalAmountAdapter.between(start.get().getDate(), end.get().getDate()).getDuration();
        } else {
            rDuration = duration.get().getDuration();
        }

        // add recurrence dates..
        List<Property> rDates = getProperties(Property.RDATE);
        for (Property p : rDates) {
            Optional<Value> value = p.getParameter(Parameter.VALUE);
            if (value.equals(Optional.of(Value.PERIOD))) {
                recurrenceSet.addAll(((RDate<T>) p).getPeriods().orElse(Collections.emptySet()).parallelStream()
                        .filter(period::intersects).collect(Collectors.toList()));
            } else {
                recurrenceSet.addAll(((DateListProperty<T>) p).getDates().parallelStream()
                        .filter(period::includes).map(d -> new Period<>(d, rDuration)).collect(Collectors.toList()));
            }
        }

        // allow for recurrence rules that start prior to the specified period
        // but still intersect with it..
        final T startMinusDuration = (T) period.getStart().minus(rDuration);

        // add recurrence rules..
        List<Property> rRules = getProperties(Property.RRULE);
        if (!rRules.isEmpty()) {
            recurrenceSet.addAll(rRules.stream().map(r -> ((RRule) r).getRecur().getDates(start.get().getDate(),
                    startMinusDuration, period.getEnd())).flatMap(List<T>::stream)
                    .map(rruleDate -> new Period<T>(rruleDate, rDuration)).collect(Collectors.toList()));
        } else {
            // add initial instance if intersection with the specified period..
            Period<T> startPeriod;
            if (end.isPresent()) {
                startPeriod = new Period<>(start.get().getDate(), end.get().getDate());
            } else {
                /*
                 * PeS: Anniversary type has no DTEND nor DUR, define DUR
                 * locally, otherwise we get NPE
                 */
                startPeriod = duration.map(value -> new Period<>(start.get().getDate(), value.getDuration())).orElseGet(
                        () -> new Period<>(start.get().getDate(), new Duration(rDuration).getDuration()));
            }
            if (period.intersects(startPeriod)) {
                recurrenceSet.add(startPeriod);
            }
        }

        // subtract exception dates..
        List<Property> exDateProps = getProperties(Property.EXDATE);
        List<Temporal> exDates = exDateProps.stream().map(p -> ((DateListProperty<T>) p).getDates())
                .flatMap(List<T>::stream).collect(Collectors.toList());

        recurrenceSet.removeIf(recurrence -> exDates.contains(recurrence.getStart()));

        // subtract exception rules..
        List<Property> exRules = getProperties(Property.EXRULE);
        List<Object> exRuleDates = exRules.stream().map(e -> ((ExRule) e).getRecur().getDates(start.get().getDate(),
                period)).flatMap(List<T>::stream).collect(Collectors.toList());

        recurrenceSet.removeIf(recurrence -> exRuleDates.contains(recurrence.getStart()));

        // set a link to the origin
        recurrenceSet.forEach( p -> p.setComponent(this));

        return recurrenceSet;
    }
}
