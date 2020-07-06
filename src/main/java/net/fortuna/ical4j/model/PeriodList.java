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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.threeten.extra.Interval;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * $Id$ [23-Apr-2004]
 *
 * Defines a list of iCalendar periods. This class encapsulates a collection of naturally sorted periods that SHOULD
 * conform to a common set of rules, including temporal type, applicable timezone and string representation (format).
 *
 * Results for a consumed time query will have the same temporal type, format type and (where applicable) timezone as
 * the event start date.
 *
 * Results for a free-busy query will always be defined in UTC time (i.e. using temporal type {@link java.time.Instant})
 *
 * NOTE: By implementing the <code>java.util.SortedSet</code> interface period lists will always be sorted according
 * to natural ordering.
 * 
 * @author Ben Fortuna
 */
public class PeriodList<T extends Temporal> implements Serializable {

	private static final long serialVersionUID = -2317587285790834492L;

	private final Set<Period<T>> periods;

    private transient final CalendarDateFormat dateFormat;

    /**
     * Default constructor.
     */
    public PeriodList() {
        this(CalendarDateFormat.FLOATING_DATE_TIME_FORMAT);
    }

    public PeriodList(CalendarDateFormat dateFormat) {
        this(new TreeSet<>(), dateFormat);
    }

    public PeriodList(Collection<Period<T>> periods) {
        this(periods, CalendarDateFormat.FLOATING_DATE_TIME_FORMAT);
    }

    public PeriodList(Collection<Period<T>> periods, CalendarDateFormat dateFormat) {
        this.periods = new TreeSet<>(periods);
        this.dateFormat = dateFormat;
    }

    /**
     * Parses the specified string representation to create a list of periods.
     * 
     * @param aValue a string representation of a list of periods
     * @throws java.time.format.DateTimeParseException thrown when an invalid string representation of a period list
     * is provided
     */
    public static <T extends Temporal> PeriodList<T> parse(final String aValue) {
        return parse(aValue, CalendarDateFormat.FLOATING_DATE_TIME_FORMAT);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Temporal> PeriodList<T> parse(final String aValue, CalendarDateFormat calendarDateFormat) {
        return (PeriodList<T>) new PeriodList<>(
                Arrays.stream(aValue.split(",")).map(Period::parse).collect(Collectors.toList()),
                calendarDateFormat);
    }

    @Override
    public String toString() {
        return periods.stream().map(p -> p.toString(dateFormat)).collect(Collectors.joining(","));
    }

    public String toString(ZoneId zoneId) {
        return periods.stream().map(p -> p.toString(dateFormat, zoneId)).collect(Collectors.joining(","));
    }

    /**
     * Add a period to the list.
     *
     * @param period
     *            the period to add
     * @return true
     * @see java.util.List#add(java.lang.Object)
     */
    public final boolean add(final Period<T> period) {
        return periods.add(period);
    }

    /**
     * Remove a period from the list.
     *
     * @param period
     *            the period to remove
     * @return true if the list contained the specified period
     * @see java.util.List#remove(java.lang.Object)
     */
    public final boolean remove(final Object period) {
        return periods.remove(period);
    }

    /**
     * Returns a normalised version of this period list. Normalisation includes
     * combining overlapping periods, removing periods contained by other
     * periods, combining adjacent periods, and removing periods that consume
     * no time. NOTE: If the period list is
     * already normalised then this period list is returned.
     * 
     * @return a period list
     */
    public final PeriodList<T> normalise() {
        Period<T> prevPeriod = null;
        Period<T> period;
        final PeriodList<T> newList = new PeriodList<>(dateFormat);
        boolean normalised = false;
        for (Period<T> period1 : periods) {
            period = period1;
            if (period.getStart() instanceof LocalDate) {
                continue;
            }
            if (period.isEmpty()) {
                period = prevPeriod;
                normalised = true;
            } else if (prevPeriod != null) {
                Interval prevInterval = prevPeriod.toInterval();
                Interval periodInterval = period.toInterval();
                if (prevInterval.encloses(periodInterval)) {
                    // ignore periods contained by other periods..
                    period = prevPeriod;
                    normalised = true;
                } else if (prevPeriod.intersects(period)) {
                    // combine intersecting periods..
                    period = prevPeriod.add(period);
                    normalised = true;
                } else if (prevInterval.abuts(periodInterval)) {
                    // combine adjacent periods..
                    period = prevPeriod.add(period);
                    normalised = true;
                } else {
                    // if current period is recognised as distinct
                    // from previous period, add the previous period
                    // to the list..
                    newList.add(prevPeriod);
                }
            }
            prevPeriod = period;
        }
        // remember to add the last period to the list..
        if (prevPeriod != null) {
            newList.add(prevPeriod);
        }
        // only return new list if normalisation
        // has ocurred..
        if (normalised) {
            return newList;
        }
        else {
            return this;
	    }
    }

    /**
     * A convenience method that combines all the periods in the specified list to
     * this list. The result returned is a new PeriodList instance, except where
     * no periods are specified in the arguments. In such cases this instance is returned.
     * 
     * Normalisation is also performed automatically after all periods have been added.
     * 
     * @param periods a list of periods to add
     * @return a period list instance
     */
    public final PeriodList<T> add(final PeriodList<T> periods) {
        if (periods != null) {
            final PeriodList<T> newList = new PeriodList<>(dateFormat);
            newList.getPeriods().addAll(this.periods);
            newList.getPeriods().addAll(periods.periods);
            return newList.normalise();
        }
        return this;
    }

    /**
     * Subtracts the intersection of this list with the specified list of
     * periods from this list and returns the results as a new period list. If
     * no intersection is identified this list is returned.
     * 
     * @param subtractions
     *            a list of periods to subtract from this list
     * @return a period list
     */
    public final PeriodList<T> subtract(final PeriodList<T> subtractions) {
        if (subtractions == null || subtractions.isEmpty()) {
            return this;
        }
        
        PeriodList<T> result = this;
        PeriodList<T> tmpResult = new PeriodList<>(dateFormat);

        for (final Period<T> subtraction : subtractions.getPeriods()) {
            if (subtraction.getStart() instanceof LocalDate) {
                tmpResult.addAll(result.getPeriods().stream()
                        .filter(p -> !p.equals(subtraction)).collect(Collectors.toList()));
            } else {
                for (final Period<T> period : result.getPeriods()) {
                    tmpResult.addAll(period.subtract(subtraction).getPeriods());
                }
            }
            result = tmpResult;
            tmpResult = new PeriodList<>();
        }

        return result;
    }

    public Set<Period<T>> getPeriods() {
        return periods;
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(Collection<Period<T>> arg0) {
        for (Period<T> p : arg0) {
            add(p);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return periods.isEmpty();
    }

	public boolean equals(Object obj) {
		if (!(obj instanceof PeriodList)) {
			return false;
		}
		final PeriodList rhs = (PeriodList) obj;
		return new EqualsBuilder().append(periods, rhs.periods)
			.isEquals();
	}
	
	public int hashCode() {
		return new HashCodeBuilder().append(periods)
			.toHashCode();
	}
}
