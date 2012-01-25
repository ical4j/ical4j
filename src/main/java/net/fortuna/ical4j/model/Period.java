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
public class Period extends DateRange implements Comparable {
    
    private static final long serialVersionUID = 7321090422911676490L;

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
        super(parseStartDate(aValue), parseEndDate(aValue, true));

        // period may end in either a date-time or a duration..
        try {
            parseEndDate(aValue, false);
        }
        catch (ParseException pe) {
            // duration = DurationFormat.getInstance().parse(aValue);
            duration = parseDuration(aValue);
        }
        normalise();
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
        super(start, end);
        normalise();
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
        super(start, new DateTime(duration.getTime(start)));
        this.duration = duration;
        normalise();
    }

    private static DateTime parseStartDate(String value) throws ParseException {
        return new DateTime(value.substring(0, value.indexOf('/')));
    }
    
    private static DateTime parseEndDate(String value, boolean resolve) throws ParseException {
        DateTime end = null;
        try {
            end = new DateTime(value.substring(value.indexOf('/') + 1));
        }
        catch (ParseException e) {
            if (resolve) {
                final Dur duration = parseDuration(value);
                end = new DateTime(duration.getTime(parseStartDate(value)));
            }
            else {
                throw e;
            }
        }
        return end;
    }
    
    private static Dur parseDuration(String value) {
        return new Dur(value.substring(value.indexOf('/') + 1));
    }
    
    private void normalise() {
        // ensure the end timezone is the same as the start..
        if (getStart().isUtc()) {
            getEnd().setUtc(true);
        }
        else {
            getEnd().setTimeZone(getStart().getTimeZone());
        }
    }
    
    /**
     * Returns the duration of this period. If an explicit duration is not
     * specified, the duration is derived from the end date.
     * 
     * @return the duration of this period in milliseconds.
     */
    public final Dur getDuration() {
        if (duration == null) {
            return new Dur(getStart(), getEnd());
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
        return (DateTime) getRangeEnd();
    }

    /**
     * @return Returns the start.
     */
    public final DateTime getStart() {
        return (DateTime) getRangeStart();
    }

    /**
     * @param date a date to test for inclusion
     * @param inclusive indicates if the start and end of the period are included in the test
     * @return true if the specified date occurs within the current period
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
     * @param period a period to subtract from this one
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
     * @param utc indicates whether the period is in UTC time
     */
    public void setUtc(final boolean utc) {
        getStart().setUtc(utc);
        getEnd().setUtc(utc);
    }
    
    /**
     * Updates the start and (possible) end times of this period to reflect
     * the specified timezone status.
     * @param timezone a timezone for the period
     */
    public final void setTimeZone(final TimeZone timezone) {
        getStart().setUtc(false);
        getStart().setTimeZone(timezone);
        getEnd().setUtc(false);
        getEnd().setTimeZone(timezone);
    }
    
    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        b.append(getStart());
        b.append('/');
        if (duration == null) {
            b.append(getEnd());
        }
        else {
            // b.append(DurationFormat.getInstance().format(duration));
            b.append(duration);
        }
        return b.toString();
    }

    /**
     * {@inheritDoc}
     */
    public final int compareTo(final Object arg0) {
        return compareTo((Period) arg0);
    }

    /**
     * Compares the specified period with this period.
     * 
     * @param arg0 a period to compare with this one
     * @return a postive value if this period is greater, negative if the other is
     * greater, or zero if they are equal
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
        else if (duration == null) {
            final int endCompare = getEnd().compareTo(arg0.getEnd());
            if (endCompare != 0) {
                return endCompare;
            }
        }
        // ..or durations
        return getDuration().compareTo(arg0.getDuration());
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public final int hashCode() {
        return new HashCodeBuilder().append(getStart())
            .append((duration == null) ? (Object) getEnd() : duration).toHashCode();
    }
}
