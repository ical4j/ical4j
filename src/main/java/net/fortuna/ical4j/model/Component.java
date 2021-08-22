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

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.stream.Collectors;

/**
 * $Id$ [Apr 5, 2004]
 * <p/>
 * Defines an iCalendar component. Subclasses of this class provide additional validation and typed values for specific
 * iCalendar components.
 *
 * @author Ben Fortuna
 */
public abstract class Component implements Serializable, PropertyContainer {

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

    private final PropertyList<Property> properties;

    protected final ComponentList<? extends Component> components;

    /**
     * Constructs a new component containing no properties.
     *
     * @param s a component name
     */
    protected Component(final String s) {
        this(s, new PropertyList<>(), new ComponentList<>());
    }

    protected Component(final String s, final PropertyList<Property> p) {
        this(s, p, new ComponentList<>());
    }

    /**
     * Constructor made protected to enforce the use of <code>ComponentFactory</code> for component instantiation.
     *
     * @param s component name
     * @param p a list of properties
     */
    protected Component(final String s, final PropertyList<Property> p, ComponentList<? extends Component> c) {
        this.name = s;
        this.properties = p;
        this.components = c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public String toString() {
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
     * @return Returns the name.
     */
    public final String getName() {
        return name;
    }

    /**
     * @return Returns the properties.
     */
    public final PropertyList<Property> getProperties() {
        return properties;
    }

    /**
     * Convenience method for retrieving a required named property.
     *
     * @param name name of the property to retrieve
     * @return the first matching property in the property list with the specified name
     * @throws ConstraintViolationException when a property is not found
     */
    protected final Property getRequiredProperty(String name) throws ConstraintViolationException {
        Property p = getProperties().getProperty(name);
        if (p == null) {
            throw new ConstraintViolationException(String.format("Missing %s property", name));
        }
        return p;
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
    public abstract void validate(final boolean recurse)
            throws ValidationException;

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
     * Create a (deep) copy of this component.
     *
     * @return the component copy
     * @throws IOException        where an error occurs reading the component data
     * @throws ParseException     where parsing component data fails
     * @throws URISyntaxException where component data contains an invalid URI
     */
    public final <T extends Component> T copy() throws ParseException, IOException, URISyntaxException {

        // Deep copy properties..
        final PropertyList<Property> newprops = new PropertyList<>(properties);
        final ComponentList<Component> newc = new ComponentList<>(components);

        return new ComponentFactoryImpl().createComponent(getName(), newprops, newc);
    }

    /**
     * Calculates the recurrence set for this component using the specified period.
     * The recurrence set is derived from a combination of the component start date,
     * recurrence rules and dates, and exception rules and dates. Note that component
     * transparency and anniversary-style dates do not affect the resulting
     * intersection.
     * <p>If an explicit DURATION is not specified, the effective duration of each
     * returned period is derived from the DTSTART and DTEND or DUE properties.
     * If the component has no DURATION, DTEND or DUE, the effective duration is set
     * to PT0S</p>
     *
     * @param period a range to calculate recurrences for
     * @return a list of periods
     */
    public final PeriodList calculateRecurrenceSet(final Period period) {

//        validate();

        final PeriodList recurrenceSet = new PeriodList();

        final DtStart start = getProperty(Property.DTSTART);
        DateProperty end = getProperty(Property.DTEND);
        if (end == null) {
            end = getProperty(Property.DUE);
        }
        Duration duration = getProperty(Property.DURATION);

        // if no start date specified return empty list..
        if (start == null) {
            return recurrenceSet;
        }

        final Value startValue = start.getParameter(Parameter.VALUE);

        // initialise timezone..
//        if (startValue == null || Value.DATE_TIME.equals(startValue)) {
        if (start.isUtc()) {
            recurrenceSet.setUtc(true);
        } else if (start.getDate() instanceof DateTime) {
            recurrenceSet.setTimeZone(((DateTime) start.getDate()).getTimeZone());
        }

        // if an explicit event duration is not specified, derive a value for recurring
        // periods from the end date..
        TemporalAmount rDuration;
        // if no end or duration specified, end date equals start date..
        if (end == null && duration == null) {
            rDuration = java.time.Duration.ZERO;
        } else if (duration == null) {
            rDuration = TemporalAmountAdapter.fromDateRange(start.getDate(), end.getDate()).getDuration();
        } else {
            rDuration = duration.getDuration();
        }

        // add recurrence dates..
        List<RDate> rDates = getProperties(Property.RDATE);
        recurrenceSet.addAll(rDates.stream().filter(p -> p.getParameter(Parameter.VALUE) == Value.PERIOD)
                .map(RDate::getPeriods).flatMap(PeriodList::stream).filter(period::intersects)
                .collect(Collectors.toList()));

        recurrenceSet.addAll(rDates.stream().filter(p -> p.getParameter(Parameter.VALUE) == Value.DATE_TIME)
                .map(DateListProperty::getDates).flatMap(DateList::stream).filter(period::includes)
                .map(rdateTime -> new Period((DateTime) rdateTime, rDuration)).collect(Collectors.toList()));

        recurrenceSet.addAll(rDates.stream().filter(p -> p.getParameter(Parameter.VALUE) == Value.DATE)
                .map(DateListProperty::getDates).flatMap(DateList::stream).filter(period::includes)
                .map(rdateDate -> new Period(new DateTime(rdateDate), rDuration)).collect(Collectors.toList()));

        // allow for recurrence rules that start prior to the specified period
        // but still intersect with it..
        final DateTime startMinusDuration = new DateTime(period.getStart());
        startMinusDuration.setTime(Date.from(period.getStart().toInstant().minus(rDuration)).getTime());

        // add recurrence rules..
        List<RRule> rRules = getProperties(Property.RRULE);
        if (!rRules.isEmpty()) {
            recurrenceSet.addAll(rRules.stream().map(r -> r.getRecur().getDates(start.getDate(),
                    new Period(startMinusDuration, period.getEnd()), startValue)).flatMap(DateList::stream)
                    .map(rruleDate -> new Period(new DateTime(rruleDate), rDuration)).collect(Collectors.toList()));
        } else {
            // add initial instance if intersection with the specified period..
            Period startPeriod;
            if (end != null) {
                startPeriod = new Period(new DateTime(start.getDate()),
                        new DateTime(end.getDate()));
            } else {
                /*
                 * PeS: Anniversary type has no DTEND nor DUR, define DUR
                 * locally, otherwise we get NPE
                 */
                if (duration == null) {
                    duration = new Duration(rDuration);
                }

                startPeriod = new Period(new DateTime(start.getDate()),
                        duration.getDuration());
            }
            if (period.intersects(startPeriod)) {
                recurrenceSet.add(startPeriod);
            }
        }

        // subtract exception dates..
        List<ExDate> exDateProps = getProperties(Property.EXDATE);
        List<Date> exDates = exDateProps.stream().map(DateListProperty::getDates).flatMap(DateList::stream)
                .collect(Collectors.toList());

        recurrenceSet.removeIf(recurrence -> {
            // for DATE-TIME instances check for DATE-based exclusions also..
            return exDates.contains(recurrence.getStart()) || exDates.contains(new Date(recurrence.getStart()));
        });

        // subtract exception rules..
        List<ExRule> exRules = getProperties(Property.EXRULE);
        List<Date> exRuleDates = exRules.stream().map(e -> e.getRecur().getDates(start.getDate(),
                period, startValue)).flatMap(DateList::stream).collect(Collectors.toList());

        recurrenceSet.removeIf(recurrence -> {
            // for DATE-TIME instances check for DATE-based exclusions also..
            return exRuleDates.contains(recurrence.getStart())
                    || exRuleDates.contains(new Date(recurrence.getStart()));
        });

        // set a link to the origin
        recurrenceSet.forEach( p -> p.setComponent(this));
        
        return recurrenceSet;
    }
}
