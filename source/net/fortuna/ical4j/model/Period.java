/**
 * Copyright (c) 2008, Ben Fortuna
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

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * $Id$ [Apr 14, 2004]
 *
 * Defines a period of time. A period may be specified as either a start date
 * and end date, or a start date and duration. NOTE: End dates and durations are
 * implicitly derived when not explicitly specified. This means that you cannot
 * rely on the returned values from the getters to deduce whether a period has
 * an explicit end date or duration.
 * 
 * @author Ben Fortuna
 */
public class Period implements Serializable, Comparable {

    public static final int INCLUSIVE_START = 1;

    public static final int INCLUSIVE_END = 2;
    
    private static final long serialVersionUID = 7321090422911676490L;

    private DateTime start;

    private DateTime end;

    private Dur duration;

    /**
     * Constructor.
     * 
     * @param aValue
     *            a string representation of a period
     * @throws ParseException
     *             where the specified string is not a valid representation
     */
    public Period(final String aValue) throws ParseException {
        start = new DateTime(aValue.substring(0, aValue.indexOf('/')));

        // period may end in either a date-time or a duration..
        try {
            end = new DateTime(aValue.substring(aValue.indexOf('/') + 1));
        }
        catch (ParseException pe) {
            // duration = DurationFormat.getInstance().parse(aValue);
            duration = new Dur(aValue);
        }
    }

    /**
     * Constructs a new period with the specied start and end date.
     * 
     * @param start
     *            the start date of the period
     * @param end
     *            the end date of the period
     */
    public Period(final DateTime start, final DateTime end) {
        this.start = start;
        this.end = end;

        // ensure the end timezone is the same as the start..
        if (end != null) {
            end.setUtc(start.isUtc());
            if (!start.isUtc()) {
                end.setTimeZone(start.getTimeZone());
            }
        }
    }

    /**
     * Constructs a new period with the specified start date and duration.
     * 
     * @param start
     *            the start date of the period
     * @param duration
     *            the duration of the period
     */
    public Period(final DateTime start, final Dur duration) {
        this.start = start;
        this.duration = duration;
    }

    /**
     * Returns the duration of this period. If an explicit duration is not
     * specified, the duration is derived from the end date.
     * 
     * @return the duration of this period in milliseconds.
     */
    public final Dur getDuration() {
        if (end != null) {
            return new Dur(start, end);
        }
        return duration;
    }

    /**
     * Returns the end date of this period. If an explicit end date is not
     * specified, the end date is derived from the duration.
     * 
     * @return the end date of this period.
     */
    public final DateTime getEnd() {
        if (end == null) {
            final DateTime derived = new DateTime(duration.getTime(start).getTime());
            if (start.isUtc()) {
                derived.setUtc(true);
            }
            else {
                derived.setTimeZone(start.getTimeZone());
            }
            return derived;
        }
        return end;
    }

    /**
     * @return Returns the start.
     */
    public final DateTime getStart() {
        return start;
    }

    /**
     * Determines if the specified date occurs within this period (inclusive of
     * period start and end).
     * @param date
     * @return true if the specified date occurs within the current period
     * 
     */
    public final boolean includes(final Date date) {
        return includes(date, INCLUSIVE_START | INCLUSIVE_END);
    }

    /**
     * @param date
     * @param inclusive
     * @return
     * @deprecated use {@link Period#includes(Date, int)} instead.
     */
    public final boolean includes(final Date date, final boolean inclusive) {
        if (inclusive) {
            return includes(date, INCLUSIVE_START | INCLUSIVE_END);
        }
        else {
            return includes(date, 0);
        }
    }

    /**
     * Decides whether a date falls within this period.
     * @param date the date to be tested
     * @param inclusive specifies whether period start and end are included
     * in the calculation
     * @return true if the date is in the perod, false otherwise
     */
    public final boolean includes(final Date date, final int inclusiveMask) {
        boolean includes = true;
        if ((inclusiveMask & INCLUSIVE_START) > 0) {
            includes = includes && !getStart().after(date);
        }
        else {
            includes = includes && getStart().before(date);
        }
        if ((inclusiveMask & INCLUSIVE_END) > 0) {
            includes = includes && !getEnd().before(date);
        }
        else {
            includes = includes && getEnd().after(date);
        }
        return includes;
    }

    /**
     * Decides whether this period is completed before the given period starts.
     * 
     * @param period
     *            a period that may or may not start after this period ends
     * @return true if the specified period starts after this periods ends,
     *         otherwise false
     */
    public final boolean before(final Period period) {
        return (getEnd().before(period.getStart()));
    }

    /**
     * Decides whether this period starts after the given period ends.
     * 
     * @param period
     *            a period that may or may not end before this period starts
     * @return true if the specified period end before this periods starts,
     *         otherwise false
     */
    public final boolean after(final Period period) {
        return (getStart().after(period.getEnd()));
    }

    /**
     * Decides whether this period intersects with another one.
     * 
     * @param period
     *            a possible intersecting period
     * @return true if the specified period intersects this one, false
     *         otherwise.
     */
    public final boolean intersects(final Period period) {
        // Test for our start date in period
        // (Exclude if it is the end date of test range)
        if (period.includes(getStart()) && !period.getEnd().equals(getStart())) {
            return true;
        }
        // Test for test range's start date in our range
        // (Exclude if it is the end date of our range)
        else if (includes(period.getStart())
                && !getEnd().equals(period.getStart())) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether these periods are serial without a gap.
     * 
     * @return true if one period immediately follows the other, false otherwise
     */
    public final boolean adjacent(final Period period) {
        if (getStart().equals(period.getEnd())) {
            return true;
        } else if (getEnd().equals(period.getStart())) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether the given period is completely contained within this one.
     * 
     * @param period
     *            the period that may be contained by this one
     * @return true if this period covers all the dates of the specified period,
     *         otherwise false
     */
    public final boolean contains(final Period period) {
        // Test for period's start and end dates in our range
        return (includes(period.getStart()) && includes(period.getEnd()));
    }

    /**
     * Creates a period that encompasses both this period and another one. If
     * the other period is null, return a copy of this period. NOTE: Resulting
     * periods are specified by explicitly setting a start date and end date
     * (i.e. durations are implied).
     * 
     * @param period
     *            the period to add to this one
     * @return a period
     */
    public final Period add(final Period period) {
        DateTime newPeriodStart = null;
        DateTime newPeriodEnd = null;

        if (period == null) {
            newPeriodStart = getStart();
            newPeriodEnd = getEnd();
        }
        else {
            if (getStart().before(period.getStart())) {
                newPeriodStart = getStart();
            }
            else {
                newPeriodStart = period.getStart();
            }
            if (getEnd().after(period.getEnd())) {
                newPeriodEnd = getEnd();
            }
            else {
                newPeriodEnd = period.getEnd();
            }
        }

        return new Period(newPeriodStart, newPeriodEnd);
    }
    
    /**
     * Creates a set of periods resulting from the subtraction of the specified
     * period from this one. If the specified period is completely contained
     * in this period, the resulting list will contain two periods. Otherwise
     * it will contain one. If the specified period does not interest this period
     * a list containing this period is returned. If this period is completely
     * contained within the specified period an empty period list is returned.
     * @param period
     * @return a list containing zero, one or two periods.
     */
    public final PeriodList subtract(final Period period) {
        final PeriodList result = new PeriodList();
        
        if (period.contains(this)) {
            return result;
        }
        else if (!period.intersects(this)) {
            result.add(this);
            return result;
        }
        
        DateTime newPeriodStart;
        DateTime newPeriodEnd;
        if (!period.getStart().after(getStart())) {
            newPeriodStart = period.getEnd();
            newPeriodEnd = getEnd();
        }
        else if (!period.getEnd().before(getEnd())) {
            newPeriodStart = getStart();
            newPeriodEnd = period.getStart();
        }
        else {
            // subtraction consumed by this period..
            // initialise and add head period..
            newPeriodStart = getStart();
            newPeriodEnd = period.getStart();
            result.add(new Period(newPeriodStart, newPeriodEnd));
            // initialise tail period..
            newPeriodStart = period.getEnd();
            newPeriodEnd = getEnd();
        }
        result.add(new Period(newPeriodStart, newPeriodEnd));
        return result;
    }
    
    /**
     * An empty period is one that consumes no time.
     * @return true if this period consumes no time, otherwise false
     */
    public final boolean isEmpty() {
        return getStart().equals(getEnd());
    }
    
    /**
     * Updates the start and (possible) end times of this period to reflect
     * the specified UTC timezone status.
     * @param utc
     */
    public void setUtc(final boolean utc) {
        start.setUtc(utc);
        if (end != null) {
            getEnd().setUtc(utc);
        }
    }
    
    /**
     * Updates the start and (possible) end times of this period to reflect
     * the specified timezone status.
     * @param timezone
     */
    public final void setTimeZone(final TimeZone timezone) {
        start.setUtc(false);
        start.setTimeZone(timezone);
        if (end != null) {
            getEnd().setUtc(false);
            getEnd().setTimeZone(timezone);
        }
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        b.append(start);
        b.append('/');
        if (end != null) {
            b.append(end);
        } else {
            // b.append(DurationFormat.getInstance().format(duration));
            b.append(duration);
        }
        return b.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public final int compareTo(final Object arg0) {
        return compareTo((Period) arg0);
    }

    /**
     * Compares the specified period with this period.
     * 
     * @param arg0
     * @return
     */
    public final int compareTo(final Period arg0) {
        // Throws documented exception if type is wrong or parameter is null
        if (arg0 == null) {
            throw new ClassCastException("Cannot compare this object to null");
        }
        final int startCompare = getStart().compareTo(arg0.getStart());
        if (startCompare != 0) {
            return startCompare;
        }
        // start dates are equal, compare end dates..
        else if (end != null) {
            final int endCompare = end.compareTo(arg0.getEnd());
            if (endCompare != 0) {
                return endCompare;
            }
        }
        // ..or durations
        return getDuration().compareTo(arg0.getDuration());
    }

    /**
     * Uses {@link EqualsBuilder} to test equality.
     * @param o object being compared for equality
     * @return true if the objects are equal, false otherwise
     */
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Period)) {
            return false;
        }

        final Period period = (Period) o;
        return new EqualsBuilder().append(getStart(), period.getStart())
            .append(getEnd(), period.getEnd()).isEquals();
    }

    /**
     * Uses {@link HashCodeBuilder} to build hashcode.
     */
    public final int hashCode() {
        return new HashCodeBuilder().append(start)
            .append((end != null) ? (Object) end : duration).toHashCode();
    }
}
