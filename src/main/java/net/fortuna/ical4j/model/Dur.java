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

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.threeten.bp.*;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.temporal.TemporalAmount;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * $Id$
 * <p/>
 * Created on 20/06/2005
 * <p/>
 * Represents a duration of time in iCalendar. Note that according to RFC2445 durations represented in weeks are
 * mutually exclusive of other duration fields.
 * <p/>
 * <pre>
 *  3.3.6.  Duration
 *
 *  Value Name:  DURATION
 *
 *  Purpose:  This value type is used to identify properties that contain
 *  a duration of time.
 *
 *  Format Definition:  This value type is defined by the following
 *  notation:
 *
 *  dur-value  = (["+"] / "-") "P" (dur-date / dur-time / dur-week)
 *
 *  dur-date   = dur-day [dur-time]
 *  dur-time   = "T" (dur-hour / dur-minute / dur-second)
 *  dur-week   = 1*DIGIT "W"
 *  dur-hour   = 1*DIGIT "H" [dur-minute]
 *  dur-minute = 1*DIGIT "M" [dur-second]
 *  dur-second = 1*DIGIT "S"
 *  dur-day    = 1*DIGIT "D"
 *
 *  Description:  If the property permits, multiple "duration" values are
 *  specified by a COMMA-separated list of values.  The format is
 *  based on the [ISO.8601.2004] complete representation basic format
 *  with designators for the duration of time.  The format can
 *  represent nominal durations (weeks and days) and accurate
 *  durations (hours, minutes, and seconds).  Note that unlike
 *  [ISO.8601.2004], this value type doesn't support the "Y" and "M"
 *  designators to specify durations in terms of years and months.
 *
 *  The duration of a week or a day depends on its position in the
 *  calendar.  In the case of discontinuities in the time scale, such
 *  as the change from standard time to daylight time and back, the
 *  computation of the exact duration requires the subtraction or
 *  addition of the change of duration of the discontinuity.  Leap
 *  seconds MUST NOT be considered when computing an exact duration.
 *  When computing an exact duration, the greatest order time
 *  components MUST be added first, that is, the number of days MUST
 *  be added first, followed by the number of hours, number of
 *  minutes, and number of seconds.
 *
 *  Negative durations are typically used to schedule an alarm to
 *  trigger before an associated time (see Section 3.8.6.3).
 *
 *  No additional content value encoding (i.e., BACKSLASH character
 *  encoding, see Section 3.3.11) are defined for this value type.
 *
 *  Example:  A duration of 15 days, 5 hours, and 20 seconds would be:
 *
 *  P15DT5H0M20S
 *
 *  A duration of 7 weeks would be:
 *
 *  P7W
 * </pre>
 *
 * @author Ben Fortuna
 */
public class Dur implements Comparable<Dur>, Serializable {

    private static final long serialVersionUID = 5013232281547134583L;

    private static final int DAYS_PER_WEEK = 7;
    private static final int SECONDS_PER_DAY = 3600 * 24;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;

    private TemporalAmount duration;

    /**
     * Constructs a new duration instance from a string representation.
     *
     * @param value a string representation of a duration
     */
    public Dur(final String value) {
        try {
            duration = Period.parse(value);
        } catch (DateTimeParseException e) {
            duration = Duration.parse(value);
        }
    }

    /**
     * Constructs a new duration from the specified weeks.
     *
     * @param weeks a duration in weeks.
     */
    public Dur(final int weeks) {
        duration = Period.ofWeeks(weeks);
    }

    /**
     * Constructs a new duration from the specified arguments.
     *
     * @param days    duration in days
     * @param hours   duration in hours
     * @param minutes duration in minutes
     * @param seconds duration in seconds
     */
    public Dur(final int days, final int hours, final int minutes,
               final int seconds) {

        if (!(days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0)
                && !(days <= 0 && hours <= 0 && minutes <= 0 && seconds <= 0)) {
            throw new IllegalArgumentException("Invalid duration representation");
        }

        Duration duration = Duration.ZERO;
        duration = duration.plus(Duration.ofDays(days));
        duration = duration.plus(Duration.ofHours(hours));
        duration = duration.plus(Duration.ofMinutes(minutes));
        duration = duration.plus(Duration.ofSeconds(seconds));
        this.duration = duration;
    }

    /**
     * Constructs a new duration representing the time between the two specified dates. The end date may precede the
     * start date in order to represent a negative duration.
     *
     * @param date1 the first date of the duration
     * @param date2 the second date of the duration
     */
    public Dur(final Date date1, final Date date2) {
        duration = Duration.between(LocalDateTime.ofInstant(Instant.ofEpochMilli(date1.getTime()), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(date2.getTime()), ZoneId.systemDefault()));
//        if (((Duration)duration).getSeconds() % SECONDS_PER_DAY == 0) {
//            duration = Period.between(LocalDate.from(Instant.ofEpochMilli(date1.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()),
//                    LocalDate.from(Instant.ofEpochMilli(date2.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()));
//        }
    }

    private Dur(TemporalAmount duration) {
        this.duration = duration;
    }

    /**
     * Returns a date representing the end of this duration from the specified start date.
     *
     * @param start the date to start the duration
     * @return the end of the duration as a date
     */
    public final Date getTime(final Date start) {
        TimeZone tz = start instanceof DateTime ? ((net.fortuna.ical4j.model.DateTime) start).getTimeZone() : null;
        ZoneId zid = tz != null ? ZoneId.of(tz.getID()) : ZoneId.systemDefault();
        LocalDateTime result = LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getTime()), zid).plus(duration);
        return new Date(result.atZone(zid).toInstant().toEpochMilli());
    }

    /**
     * Provides a negation of this instance.
     * @return a Dur instance that represents a negation of this instance
     */
    public final Dur negate() {
        if (duration instanceof Duration) {
            return new Dur(((Duration) duration).negated());
        } else {
            return new Dur(((Period) duration).negated());
        }
    }
    /**
     * Add two durations. Durations may only be added if they are both positive
     * or both negative durations.
     * @param duration the duration to add to this duration
     * @return a new instance representing the sum of the two durations.
     */
    public final Dur add(final Dur duration) {
        if (this.duration instanceof Period) {
            return new Dur(((Period) this.duration).plus(duration.duration));
        } else {
            return new Dur(((Duration) this.duration).plus((Duration) duration.duration));
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        if (duration == Duration.ZERO || duration == Period.ZERO) {
            return duration.toString();
        } else if (duration instanceof Duration) {
            return toString((Duration) duration);
        } else {
            return toString((Period) duration);
        }
    }

    private String toString(Duration d) {
        final StringBuilder b = new StringBuilder();
        if (d.isNegative()) {
            b.append('-');
        }
        b.append('P');
        long seconds = d.abs().getSeconds();
        if (seconds >= SECONDS_PER_DAY) {
            b.append(seconds / SECONDS_PER_DAY);
            b.append('D');
            seconds = seconds % SECONDS_PER_DAY;
        }
        if (seconds > 0) {
            b.append('T');
            if (seconds >= SECONDS_PER_HOUR) {
                b.append(seconds / SECONDS_PER_HOUR);
                b.append('H');
                seconds = seconds % SECONDS_PER_HOUR;
            }
            if (seconds >= SECONDS_PER_MINUTE) {
                b.append(seconds / SECONDS_PER_MINUTE);
                b.append('M');
                seconds = seconds % SECONDS_PER_MINUTE;
            }
            if (seconds > 0) {
                b.append(seconds);
                b.append('S');
            }
        }
        return b.toString();
    }

    private String toString(Period p) {
        final StringBuilder b = new StringBuilder();
        int days = p.getDays();
        if (p.isNegative()) {
            b.append('-');
            days = -days;
        }
        b.append('P');
        if (days >= DAYS_PER_WEEK) {
            b.append(days / DAYS_PER_WEEK);
            b.append('W');
            days = days % DAYS_PER_WEEK;
        }
        if (days > 0) {
            b.append(days);
            b.append('D');
        }
        return b.toString();
    }

    /**
     * Compares this duration with another, acording to their length.
     *
     * @param arg0 another duration instance
     * @return a postive value if this duration is longer, zero if the duration
     * lengths are equal, otherwise a negative value
     */
    public final int compareTo(final Dur arg0) {
        if (duration instanceof Duration && arg0.duration instanceof Duration) {
            return ((Duration) duration).compareTo((Duration) arg0.duration);
        }
        //return duration.compareTo(arg0.duration);
        Instant now = Instant.now();
        return now.plus(duration).compareTo(now.plus(arg0.duration));
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (obj instanceof Dur) {
            return ((Dur) obj).compareTo(this) == 0;
        }
        return super.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(duration).toHashCode();
    }

    /**
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
