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

import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.util.Strings;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines an iCalendar component. Subclasses of this class provide additional validation and typed values for specific
 * iCalendar components.
 * @author Ben Fortuna
 */
public abstract class Component implements Serializable {

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

    private String name;

    private PropertyList properties;

    /**
     * Constructs a new component containing no properties.
     * @param s a component name
     */
    protected Component(final String s) {
        this(s, new PropertyList());
    }

    /**
     * Constructor made protected to enforce the use of <code>ComponentFactory</code> for component instantiation.
     * @param s component name
     * @param p a list of properties
     */
    protected Component(final String s, final PropertyList p) {
        this.name = s;
        this.properties = p;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(BEGIN);
        buffer.append(':');
        buffer.append(getName());
        buffer.append(Strings.LINE_SEPARATOR);
        buffer.append(getProperties());
        buffer.append(END);
        buffer.append(':');
        buffer.append(getName());
        buffer.append(Strings.LINE_SEPARATOR);

        return buffer.toString();
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
     * Perform validation on a component and its properties.
     * @throws ValidationException where the component is not in a valid state
     */
    public final void validate() throws ValidationException {
        validate(true);
    }

    /**
     * Perform validation on a component.
     * @param recurse indicates whether to validate the component's properties
     * @throws ValidationException where the component is not in a valid state
     */
    public abstract void validate(final boolean recurse)
            throws ValidationException;

    /**
     * Invoke validation on the component properties in its current state.
     * @throws ValidationException where any of the component properties is not in a valid state
     */
    protected final void validateProperties() throws ValidationException {
        for (final Iterator i = getProperties().iterator(); i.hasNext();) {
            final Property property = (Property) i.next();
            property.validate();
        }
    }

    /**
     * {@inheritDoc}
     */
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
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getProperties())
                .toHashCode();
    }

    /**
     * Create a (deep) copy of this component.
     * @return the component copy
     * @throws IOException where an error occurs reading the component data
     * @throws ParseException where parsing component data fails
     * @throws URISyntaxException where component data contains an invalid URI
     */
    public Component copy() throws ParseException, IOException,
            URISyntaxException {

        // Deep copy properties..
        final PropertyList newprops = new PropertyList(getProperties());

        return ComponentFactory.getInstance().createComponent(getName(),
                newprops);
    }
    
    /**
     * Calculates the recurrence set for this component using the specified period.
     * The recurrence set is derived from a combination of the component start date,
     * recurrence rules and dates, and exception rules and dates. Note that component
     * transparency and anniversary-style dates do not affect the resulting
     * intersection.
     * <p>If an explicit DURATION is not specified, the effective duration of each
     *  returned period is derived from the DTSTART and DTEND or DUE properties.
     * If the component has no DURATION, DTEND or DUE, the effective duration is set
     *  to PT0S</p>
     * @param period a range to calculate recurrences for
     * @return a list of periods
     */
    public final PeriodList calculateRecurrenceSet(final Period period) {
        
//        validate();
        
        final PeriodList recurrenceSet = new PeriodList();

        final DtStart start = (DtStart) getProperty(Property.DTSTART);
        DateProperty end = (DateProperty) getProperty(Property.DTEND);
        if (end == null) {
            end = (DateProperty) getProperty(Property.DUE);
        }
        Duration duration = (Duration) getProperty(Property.DURATION);
        
        // if no start date specified return empty list..
        if (start == null) {
            return recurrenceSet;
        }

        final Value startValue = (Value) start.getParameter(Parameter.VALUE);
        
        // initialise timezone..
//        if (startValue == null || Value.DATE_TIME.equals(startValue)) {
        if (start.isUtc()) {
            recurrenceSet.setUtc(true);
        }
        else if (start.getDate() instanceof DateTime) {
            recurrenceSet.setTimeZone(((DateTime) start.getDate()).getTimeZone());
        }
        
        // if an explicit event duration is not specified, derive a value for recurring
        // periods from the end date..
        Dur rDuration;
        // if no end or duration specified, end date equals start date..
        if (end == null && duration == null) {
            rDuration = new Dur(start.getDate(), start.getDate());
        }
        else if (duration == null) {
            rDuration = new Dur(start.getDate(), end.getDate());
        }
        else {
            rDuration = duration.getDuration();
        }
        
        // add recurrence dates..
        for (final Iterator i = getProperties(Property.RDATE).iterator(); i.hasNext();) {
            final RDate rdate = (RDate) i.next();
            final Value rdateValue = (Value) rdate.getParameter(Parameter.VALUE);
            if (Value.PERIOD.equals(rdateValue)) {
                for (final Iterator j = rdate.getPeriods().iterator(); j.hasNext();) {
                    final Period rdatePeriod = (Period) j.next();
                    if (period.intersects(rdatePeriod)) {
                        recurrenceSet.add(rdatePeriod);
                    }
                }
            }
            else if (Value.DATE_TIME.equals(rdateValue)) {
                for (final Iterator j = rdate.getDates().iterator(); j.hasNext();) {
                    final DateTime rdateTime = (DateTime) j.next();
                    if (period.includes(rdateTime)) {
                        recurrenceSet.add(new Period(rdateTime, rDuration));
                    }
                }
            }
            else {
                for (final Iterator j = rdate.getDates().iterator(); j.hasNext();) {
                    final Date rdateDate = (Date) j.next();
                    if (period.includes(rdateDate)) {
                        recurrenceSet.add(new Period(new DateTime(rdateDate), rDuration));
                    }
                }
            }
        }
        
        // allow for recurrence rules that start prior to the specified period
        // but still intersect with it..
        final DateTime startMinusDuration = new DateTime(period.getStart());
        startMinusDuration.setTime(rDuration.negate().getTime(
                period.getStart()).getTime());
            
        // add recurrence rules..
        for (final Iterator i = getProperties(Property.RRULE).iterator(); i.hasNext();) {
            final RRule rrule = (RRule) i.next();
            final DateList rruleDates = rrule.getRecur().getDates(start.getDate(),
                    new Period(startMinusDuration, period.getEnd()), startValue);
            for (final Iterator j = rruleDates.iterator(); j.hasNext();) {
                final Date rruleDate = (Date) j.next();
                recurrenceSet.add(new Period(new DateTime(rruleDate), rDuration));
            }
        }
    
        // add initial instance if intersection with the specified period..
        Period startPeriod = null;
        if (end != null) {
            startPeriod = new Period(new DateTime(start.getDate()),
                    new DateTime(end.getDate()));
        }
        else {
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
        
        // subtract exception dates..
        for (final Iterator i = getProperties(Property.EXDATE).iterator(); i.hasNext();) {
            final ExDate exdate = (ExDate) i.next();
            for (final Iterator j = recurrenceSet.iterator(); j.hasNext();) {
                final Period recurrence = (Period) j.next();
                // for DATE-TIME instances check for DATE-based exclusions also..
                if (exdate.getDates().contains(recurrence.getStart())
                        || exdate.getDates().contains(new Date(recurrence.getStart()))) {
                    j.remove();
                }
            }
        }
        
        // subtract exception rules..
        for (final Iterator i = getProperties(Property.EXRULE).iterator(); i.hasNext();) {
            final ExRule exrule = (ExRule) i.next();
            final DateList exruleDates = exrule.getRecur().getDates(start.getDate(),
                    period, startValue);
            for (final Iterator j = recurrenceSet.iterator(); j.hasNext();) {
                final Period recurrence = (Period) j.next();
                // for DATE-TIME instances check for DATE-based exclusions also..
                if (exruleDates.contains(recurrence.getStart())
                        || exruleDates.contains(new Date(recurrence.getStart()))) {
                    j.remove();
                }
            }
        }

        return recurrenceSet;
    }
}
