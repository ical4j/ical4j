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

import net.fortuna.ical4j.util.TimeZones;
import org.threeten.extra.Interval;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.*;

/**
 * $Id$ [Apr 14, 2004]
 *
 * Defines a period of time. A period may be specified as either a start date and end date,
 * or a start date and duration. This class enforces a consistent string representation of start date and
 * (where specified) end date via a common {@link CalendarDateFormat}.
 *
 * NOTE: End dates and durations are implicitly derived when not explicitly specified. This means that you cannot
 * rely on the returned values from the getters to deduce whether a period has an explicit end date or duration.
 *
 * <pre>
 *     3.3.9.  Period of Time
 *
 *    Value Name:  PERIOD
 *
 *    Purpose:  This value type is used to identify values that contain a
 *       precise period of time.
 *
 *    Format Definition:  This value type is defined by the following
 *       notation:
 *
 *        period     = period-explicit / period-start
 *
 *        period-explicit = date-time "/" date-time
 *        ; [ISO.8601.2004] complete representation basic format for a
 *        ; period of time consisting of a start and end.  The start MUST
 *        ; be before the end.
 *
 *        period-start = date-time "/" dur-value
 *        ; [ISO.8601.2004] complete representation basic format for a
 *        ; period of time consisting of a start and positive duration
 *        ; of time.
 *
 *
 *    Description:  If the property permits, multiple "period" values are
 *       specified by a COMMA-separated list of values.  There are two
 *       forms of a period of time.  First, a period of time is identified
 *       by its start and its end.  This format is based on the
 *       [ISO.8601.2004] complete representation, basic format for "DATE-
 *       TIME" start of the period, followed by a SOLIDUS character
 *       followed by the "DATE-TIME" of the end of the period.  The start
 *       of the period MUST be before the end of the period.  Second, a
 *       period of time can also be defined by a start and a positive
 *       duration of time.  The format is based on the [ISO.8601.2004]
 *       complete representation, basic format for the "DATE-TIME" start of
 *       the period, followed by a SOLIDUS character, followed by the
 *       [ISO.8601.2004] basic format for "DURATION" of the period.
 *
 *    Example:  The period starting at 18:00:00 UTC, on January 1, 1997 and
 *       ending at 07:00:00 UTC on January 2, 1997 would be:
 *
 *        19970101T180000Z/19970102T070000Z
 *
 *       The period start at 18:00:00 on January 1, 1997 and lasting 5
 *       hours and 30 minutes would be:
 *
 *        19970101T180000Z/PT5H30M
 *
 *       No additional content value encoding (i.e., BACKSLASH character
 *       encoding, see Section 3.3.11) is defined for this value type.
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Period<T extends Temporal> implements Comparable<Period<T>>, Serializable {

    private static final TemporalComparator DATE_RANGE_COMPARATOR = TemporalComparator.INSTANCE;

    private final T start;

    private Component component;

    private final T end;

    private final TemporalAmountAdapter duration;

    private transient final CalendarDateFormat dateFormat;

    /**
     * Constructs a new period with the specified start and end date.
     * 
     * @param start the start date of the period
     * @param end the end date of the period
     */
    public Period(final T start, final T end) {
        this(start, end, CalendarDateFormat.from(start));
    }

    /**
     * Constructs a new period with the specified start and end date with a common date format.
     *
     * @param start the start date of the period
     * @param end the end date of the period
     * @param dateFormat the format used to generate string representations
     */
    public Period(final T start, final T end, CalendarDateFormat dateFormat) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        Objects.requireNonNull(dateFormat, "dateFormat");
        this.start = start;
        this.end = end;
        this.duration = null;
        this.dateFormat = dateFormat;
    }

    /**
     * Constructs a new period with the specified start date and duration.
     *
     * @param start the start date of the period
     * @param duration the duration of the period
     */
    @Deprecated
    public Period(final DateTime start, final Dur duration) {
        this((T) TemporalAdapter.from(start).getTemporal(), TemporalAmountAdapter.from(duration));
    }

    /**
     * Constructs a new period with the specified start date and duration.
     *
     * @param start the start date of the period
     * @param duration the duration of the period
     */
    public Period(final T start, final TemporalAmount duration) {
        this(start, new TemporalAmountAdapter(duration));
    }

    public Period(final T start, final TemporalAmount duration, CalendarDateFormat dateFormat) {
        this(start, new TemporalAmountAdapter(duration), dateFormat);
    }

    /**
     * Constructs a new period with the specified start date and duration.
     *
     * @param start the start date of the period
     * @param duration the duration of the period
     */
    private Period(final T start, final TemporalAmountAdapter duration) {
        this(start, duration, CalendarDateFormat.from(start));
    }

    /**
     * Constructs a new period with the specified start date and duration.
     *
     * @param start the start date of the period
     * @param duration the duration of the period
     * @param dateFormat
     */
    private Period(final T start, final TemporalAmountAdapter duration, CalendarDateFormat dateFormat) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(duration, "duration");
        Objects.requireNonNull(dateFormat, "dateFormat");
        this.start = start;
        this.duration = duration;
        this.end = (T) start.plus(duration.getDuration());
        this.dateFormat = dateFormat;
    }

    /**
     * Parse a string representation of a period.
     *
     * @param value a string representation of a period
     * @param <T> the expected temporal type of the resulting period
     * @return a new period instance
     * @throws DateTimeParseException if the text cannot be parsed to a period
     */
    public static <T extends Temporal> Period<T> parse(String value) {
        T start = parseStartDate(value);
        T end = null;
        TemporalAmountAdapter duration = null;
        try {
            end = parseEndDate(value, false);
        } catch (DateTimeParseException e) {
            duration = parseDuration(value);
        }
        if (end != null) {
            return new Period<>(start, end, CalendarDateFormat.from(start));
        } else {
            return new Period<>(start, duration, CalendarDateFormat.from(start));
        }
    }

    private static <T extends Temporal> T parseStartDate(String value) throws DateTimeParseException {
        TemporalAdapter<T> parsedValue = TemporalAdapter.parse(value.substring(0, value.indexOf('/')));
        return parsedValue.getTemporal();
    }

    private static <T extends Temporal> T parseEndDate(String value, boolean resolve) throws DateTimeParseException {
        Temporal end;
        try {
            end = TemporalAdapter.parse(value.substring(value.indexOf('/') + 1)).getTemporal();
        } catch (DateTimeParseException e) {
            if (resolve) {
                final TemporalAmount duration = parseDuration(value).getDuration();
                end = parseStartDate(value).plus(duration);
            } else {
                throw e;
            }
        }
        return (T) end;
    }
    
    private static TemporalAmountAdapter parseDuration(String value) {
        String durationString = value.substring(value.indexOf('/') + 1);
        return TemporalAmountAdapter.parse(durationString);
    }

    /**
     * Returns the duration of this period. If an explicit duration is not
     * specified, the duration is derived from the end date.
     * 
     * @return the duration of this period in milliseconds.
     */
    public final TemporalAmount getDuration() {
        if (duration == null) {
            return TemporalAmountAdapter.from(start, end).getDuration();
        }
        return duration.getDuration();
    }

    /**
     * Returns the end date of this period. If an explicit end date is not
     * specified, the end date is derived from the duration.
     * 
     * @return the end date of this period.
     */
    public final T getEnd() {
        return end;
    }

    /**
     * @return Returns the start.
     */
    public final T getStart() {
        return start;
    }

    /**
     * @param date a date to test for inclusion
     * @param inclusive indicates if the start and end of the period are included in the test
     * @return true if the specified date occurs within the current period
     * @deprecated use {@link Period#includes(Temporal)} instead.
     */
    @Deprecated
    public final boolean includes(final Date date, final boolean inclusive) {
        return includes(date.toInstant());
    }

    /**
     * Determines if the specified date occurs within this period (inclusive of
     * period start and end).
     * @param date a date to test for inclusion
     * @return true if the specified date occurs within the current period
     *
     */
    public final boolean includes(final Temporal date) {
        Objects.requireNonNull(date, "date");
        Instant dateInstant = (date instanceof LocalDateTime) ? ((LocalDateTime) date)
                .toInstant(ZoneOffset.from(ZonedDateTime.now())) : Instant.from(date);
        return start.equals(date) || end.equals(date)
                || toInterval().encloses(Interval.of(dateInstant, Duration.ZERO));
    }

    /**
     * Creates a period that encompasses both this period and another one. If
     * the other period is null, return a copy of this period.
     *
     * NOTE: Resulting periods are specified by explicitly setting a start date and end date
     * (i.e. durations are implied).
     * 
     * @param period the period to add to this one
     * @return a period
     */
    public final Period<T> add(final Period<T> period) {
        T newPeriodStart;
        T newPeriodEnd;

        if (period == null) {
            newPeriodStart = getStart();
            newPeriodEnd = getEnd();
        } else {
            Interval thisInterval = TemporalAdapter.isFloating(getStart()) ? toInterval(TimeZones.getDefault().toZoneId()) : toInterval();
            Interval thatInterval = TemporalAdapter.isFloating(getStart()) ? period.toInterval(TimeZones.getDefault().toZoneId()) : period.toInterval();

            if (thisInterval.getStart().isBefore(thatInterval.getStart())) {
                newPeriodStart = getStart();
            } else {
                newPeriodStart = period.getStart();
            }

            if (thisInterval.getEnd().isAfter(thatInterval.getEnd())) {
                newPeriodEnd = getEnd();
            } else {
                newPeriodEnd = period.getEnd();
            }
        }

        return new Period<>(newPeriodStart, newPeriodEnd);
    }
    
    /**
     * Creates a set of periods resulting from the subtraction of the specified
     * period from this one.
     *
     * If the specified period is completely contained in this period, the resulting list will contain two periods.
     * Otherwise it will contain one.
     *
     * If the specified period does not intersect this period a list containing this period is returned.
     *
     * If this period is completely contained within the specified period an empty period list is returned.
     *
     * @param period a period to subtract from this one
     * @return a list containing zero, one or two periods.
     */
    public final PeriodList<T> subtract(final Period<T> period) {
        if (period.equals(this)) {
            return new PeriodList<>(dateFormat);
        } else if (!period.intersects(this) || !TemporalAdapter.isDateTimePrecision(start)) {
            return new PeriodList<>(Collections.singletonList(this), dateFormat);
        }
        return subtractInterval(period);
    }

    private PeriodList<T> subtractInterval(Period<T> period) {
        Interval thisInterval = TemporalAdapter.isFloating(getStart())
                ? toInterval(TimeZones.getDefault().toZoneId()) : toInterval();
        Interval thatInterval = TemporalAdapter.isFloating(getStart())
                ? period.toInterval(TimeZones.getDefault().toZoneId()) : period.toInterval();
        
        if (thatInterval.encloses(thisInterval)) {
            return new PeriodList<>(period.dateFormat);
        } else if (!thatInterval.overlaps(thisInterval)) {
            return new PeriodList<>(Collections.singletonList(this));
        }

        final List<Period<T>> result = new ArrayList<>();

        T newPeriodStart;
        T newPeriodEnd;
        if (!thatInterval.getStart().isAfter(thisInterval.getStart())) {
            newPeriodStart = period.getEnd();
            newPeriodEnd = getEnd();
        } else if (!thatInterval.getEnd().isBefore(thisInterval.getEnd())) {
            newPeriodStart = getStart();
            newPeriodEnd = period.getStart();
        } else {
            // subtraction consumed by this period..
            // initialise and add head period..
            newPeriodStart = getStart();
            newPeriodEnd = period.getStart();
            result.add(new Period<T>(newPeriodStart, newPeriodEnd));
            // initialise tail period..
            newPeriodStart = period.getEnd();
            newPeriodEnd = getEnd();
        }
        result.add(new Period<>(newPeriodStart, newPeriodEnd));
        return new PeriodList<>(result);
    }
    
    /**
     * An empty period is one that consumes no time.
     * @return true if this period consumes no time, otherwise false
     */
    public final boolean isEmpty() {
        if (!TemporalAdapter.isDateTimePrecision(start)) {
            return start.equals(end);
        }
        return toInterval().isEmpty();
    }

    /**
     * Formats the period as an iCalendar compatible string.
     * NOTE: Where a period represents floating date/time values the default local
     * timezone is applied prior to formatting.
     *
     * @return a string representation of the period
     */
    @Override
    public String toString() {
        return toString(dateFormat);
    }

    String toString(CalendarDateFormat dateFormat) {
        final StringBuilder b = new StringBuilder();
        b.append(dateFormat.format(getStart()));
        b.append('/');
        if (duration == null) {
            b.append(dateFormat.format(getEnd()));
        } else {
            b.append(duration);
        }
        return b.toString();
    }

    /**
     * Formats the period as an iCalendar compatible string in the specified timezone.
     *
     * @return a string representation of the period as applied in the specified timezone
     */
    public String toString(ZoneId zoneId) {
        return toString(dateFormat, zoneId);
    }

    String toString(CalendarDateFormat dateFormat, ZoneId zoneId) {
        final StringBuilder b = new StringBuilder();
        b.append(dateFormat.format(getStart(), zoneId));
        b.append('/');
        if (duration == null) {
            b.append(dateFormat.format(getEnd(), zoneId));
        } else {
            b.append(duration);
        }
        return b.toString();
    }

    /**
     * Decides whether this period intersects with another one.
     *
     * @param other a possible intersecting period
     * @return true if the specified period intersects this one, false otherwise.
     */
    public boolean intersects(Period<?> other) {
        Objects.requireNonNull(other, "other");
        Interval thisInterval = toInterval();
        Interval thatInterval = other.toInterval();
        return thisInterval.overlaps(thatInterval);
    }

    public Interval toInterval() {
        return toInterval(TimeZones.getDefault().toZoneId());
    }

    public Interval toInterval(ZoneId zoneId) {
        if (start instanceof LocalDate) {
            return Interval.of(((LocalDate) start).atStartOfDay(zoneId).toInstant(),
                    ((LocalDate) end).atStartOfDay(zoneId).toInstant());
//            throw new UnsupportedOperationException("Unable to create Interval from date-only temporal.");
        } else if (start instanceof Instant) {
            return Interval.of((Instant) start, (Instant) end);
        } else {
            // calculate zone offset based on current applicable rules
            ZoneOffset zoneOffset = zoneId.getRules().getOffset(Instant.now());
            if (duration != null) {
                return Interval.of(LocalDateTime.from(start).toInstant(zoneOffset), duration.toDuration());
            } else {
                return Interval.of(LocalDateTime.from(start).toInstant(zoneOffset),
                        LocalDateTime.from(end).toInstant(zoneOffset));
            }
        }
    }

    /**
     * Compares the specified period with this period.
     * First, compare the start dates.  If they are the same, compare the end dates.
     * 
     * @param period a period to compare with this one
     * @return a postive value if this period is greater, negative if the other is
     * greater, or zero if they are equal
     */
    @Override
    public final int compareTo(final Period<T> period) {
        // Throws documented exception if type is wrong or parameter is null
        if (period == null) {
            throw new ClassCastException("Cannot compare this object to null");
        }
        return compareTo(period, DATE_RANGE_COMPARATOR);
    }

    private int compareTo(final Period<T> period, TemporalComparator comparator) {
        final int startCompare = comparator.compare(getStart(), period.getStart());
        if (startCompare != 0) {
            return startCompare;
        }
        // start dates are equal, compare end dates..
        else if (duration == null) {
            final int endCompare = comparator.compare(getEnd(), period.getEnd());
            if (endCompare != 0) {
                return endCompare;
            }
        }
        // ..or durations
        return new TemporalAmountComparator().compare(getDuration(), period.getDuration());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period<?> period = (Period<?>) o;
        return start.equals(period.start) &&
                Objects.equals(end, period.end) &&
                Objects.equals(duration, period.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, duration);
    }

    public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}
}
