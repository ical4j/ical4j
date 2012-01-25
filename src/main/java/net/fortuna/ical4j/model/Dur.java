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
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import net.fortuna.ical4j.util.Dates;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * $Id$
 *
 * Created on 20/06/2005
 *
 * Represents a duration of time in iCalendar. Note that according to RFC2445 durations represented in weeks are
 * mutually exclusive of other duration fields.
 * 
 * <pre>
 *  4.3.6   Duration
 *  
 *     Value Name: DURATION
 *  
 *     Purpose: This value type is used to identify properties that contain
 *     a duration of time.
 *  
 *     Formal Definition: The value type is defined by the following
 *     notation:
 *  
 *       dur-value  = ([&quot;+&quot;] / &quot;-&quot;) &quot;P&quot; (dur-date / dur-time / dur-week)
 *  
 *       dur-date   = dur-day [dur-time]
 *       dur-time   = &quot;T&quot; (dur-hour / dur-minute / dur-second)
 *       dur-week   = 1*DIGIT &quot;W&quot;
 *       dur-hour   = 1*DIGIT &quot;H&quot; [dur-minute]
 *       dur-minute = 1*DIGIT &quot;M&quot; [dur-second]
 *       dur-second = 1*DIGIT &quot;S&quot;
 *       dur-day    = 1*DIGIT &quot;D&quot;
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Dur implements Comparable, Serializable {

    private static final long serialVersionUID = 5013232281547134583L;

    private static final int DAYS_PER_WEEK = 7;

    private static final int SECONDS_PER_MINUTE = 60;

    private static final int MINUTES_PER_HOUR = 60;

    private static final int HOURS_PER_DAY = 24;

    private static final int DAYS_PER_YEAR = 365;

    private boolean negative;

    private int weeks;

    private int days;

    private int hours;

    private int minutes;

    private int seconds;

    /**
     * Constructs a new duration instance from a string representation.
     * @param value a string representation of a duration
     */
    public Dur(final String value) {
        negative = false;
        weeks = 0;
        days = 0;
        hours = 0;
        minutes = 0;
        seconds = 0;

        String token = null;
        String prevToken = null;

        final StringTokenizer t = new StringTokenizer(value, "+-PWDTHMS", true);
        while (t.hasMoreTokens()) {
            prevToken = token;
            token = t.nextToken();

            if ("+".equals(token)) {
                negative = false;
            }
            else if ("-".equals(token)) {
                negative = true;
            }
            else if ("P".equals(token)) {
                // does nothing..
            }
            else if ("W".equals(token)) {
                weeks = Integer.parseInt(prevToken);
            }
            else if ("D".equals(token)) {
                days = Integer.parseInt(prevToken);
            }
            else if ("T".equals(token)) {
                // does nothing..
            }
            else if ("H".equals(token)) {
                hours = Integer.parseInt(prevToken);
            }
            else if ("M".equals(token)) {
                minutes = Integer.parseInt(prevToken);
            }
            else if ("S".equals(token)) {
                seconds = Integer.parseInt(prevToken);
            }
        }
    }

    /**
     * Constructs a new duration from the specified weeks.
     * @param weeks a duration in weeks.
     */
    public Dur(final int weeks) {
        this.weeks = Math.abs(weeks);
        this.days = 0;
        this.hours = 0;
        this.minutes = 0;
        this.seconds = 0;
        this.negative = weeks < 0;
    }

    /**
     * Constructs a new duration from the specified arguments.
     * @param days duration in days
     * @param hours duration in hours
     * @param minutes duration in minutes
     * @param seconds duration in seconds
     */
    public Dur(final int days, final int hours, final int minutes,
            final int seconds) {

        if (!(days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0)
                && !(days <= 0 && hours <= 0 && minutes <= 0 && seconds <= 0)) {
            
            throw new IllegalArgumentException("Invalid duration representation");
        }
        
        this.weeks = 0;
        this.days = Math.abs(days);
        this.hours = Math.abs(hours);
        this.minutes = Math.abs(minutes);
        this.seconds = Math.abs(seconds);
        
        this.negative = days < 0 || hours < 0 || minutes < 0 || seconds < 0;
    }

    /**
     * Constructs a new duration representing the time between the two specified dates. The end date may precede the
     * start date in order to represent a negative duration.
     * @param date1 the first date of the duration
     * @param date2 the second date of the duration
     */
    public Dur(final Date date1, final Date date2) {
        
        Date start = null;
        Date end = null;
        
        // Negative range? (start occurs after end)
        negative = date1.compareTo(date2) > 0;
        if (negative) {
            // Swap the dates (which eliminates the need to bother with
            // negative after this!)
            start = date2;
            end = date1;
        }
        else {
            start = date1;
            end = date2;
        }

        final Calendar startCal;
        if (start instanceof net.fortuna.ical4j.model.Date) {
            startCal = Dates.getCalendarInstance((net.fortuna.ical4j.model.Date)start);
        } else {
            startCal = Calendar.getInstance();
        }
        startCal.setTime(start);
        final Calendar endCal = Calendar.getInstance(startCal.getTimeZone());
        endCal.setTime(end);

        // Init our duration interval (which is in units that evolve as we
        // compute, below)
        int dur = 0;

        // Count days to get to the right year (loop in the very rare chance
        // that a leap year causes us to come up short)
        int nYears = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
        while (nYears > 0) {
            startCal.add(Calendar.DATE, DAYS_PER_YEAR * nYears);
            dur += DAYS_PER_YEAR * nYears;
            nYears = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
        }

        // Count days to get to the right day
        dur += endCal.get(Calendar.DAY_OF_YEAR)
                - startCal.get(Calendar.DAY_OF_YEAR);

        // Count hours to get to right hour
        dur *= HOURS_PER_DAY; // days -> hours
        dur += endCal.get(Calendar.HOUR_OF_DAY)
                - startCal.get(Calendar.HOUR_OF_DAY);

        // ... to the right minute
        dur *= MINUTES_PER_HOUR; // hours -> minutes
        dur += endCal.get(Calendar.MINUTE) - startCal.get(Calendar.MINUTE);

        // ... and second
        dur *= SECONDS_PER_MINUTE; // minutes -> seconds
        dur += endCal.get(Calendar.SECOND) - startCal.get(Calendar.SECOND);

        // Now unwind our units
        seconds = dur % SECONDS_PER_MINUTE;
        dur = dur / SECONDS_PER_MINUTE; // seconds -> minutes (drop remainder seconds)
        minutes = dur % MINUTES_PER_HOUR;
        dur /= MINUTES_PER_HOUR; // minutes -> hours (drop remainder minutes)
        hours = dur % HOURS_PER_DAY;
        dur /= HOURS_PER_DAY; // hours -> days (drop remainder hours)
        days = dur;
        weeks = 0;

        // Special case for week-only representation
        if (seconds == 0 && minutes == 0 && hours == 0
                && (days % DAYS_PER_WEEK) == 0) {
            weeks = days / DAYS_PER_WEEK;
            days = 0;
        }
    }

    /**
     * Returns a date representing the end of this duration from the specified start date.
     * @param start the date to start the duration
     * @return the end of the duration as a date
     */
    public final Date getTime(final Date start) {
        final Calendar cal;
        if (start instanceof net.fortuna.ical4j.model.Date) {
            cal = Dates.getCalendarInstance((net.fortuna.ical4j.model.Date)start);
        } else {
            cal = Calendar.getInstance();
        }

        cal.setTime(start);
        if (isNegative()) {
            cal.add(Calendar.WEEK_OF_YEAR, -weeks);
            cal.add(Calendar.DAY_OF_WEEK, -days);
            cal.add(Calendar.HOUR_OF_DAY, -hours);
            cal.add(Calendar.MINUTE, -minutes);
            cal.add(Calendar.SECOND, -seconds);
        }
        else {
            cal.add(Calendar.WEEK_OF_YEAR, weeks);
            cal.add(Calendar.DAY_OF_WEEK, days);
            cal.add(Calendar.HOUR_OF_DAY, hours);
            cal.add(Calendar.MINUTE, minutes);
            cal.add(Calendar.SECOND, seconds);
        }
        return cal.getTime();
    }

    /**
     * Provides a negation of this instance.
     * @return a Dur instance that represents a negation of this instance
     */
    public final Dur negate() {
        final Dur negated = new Dur(days, hours, minutes, seconds);
        negated.weeks = weeks;
        negated.negative = !negative;
        return negated;
    }
    
    /**
     * Add two durations. Durations may only be added if they are both positive
     * or both negative durations.
     * @param duration the duration to add to this duration
     * @return a new instance representing the sum of the two durations.
     */
    public final Dur add(final Dur duration) {
        if ((!isNegative() && duration.isNegative())
                || (isNegative() && !duration.isNegative())) {
            
            throw new IllegalArgumentException(
                    "Cannot add a negative and a positive duration");
        }
        
        Dur sum = null;
        if (weeks > 0 && duration.weeks > 0) {
            sum = new Dur(weeks + duration.weeks);
        }
        else {
            int daySum = (weeks > 0) ? weeks * DAYS_PER_WEEK + days : days;
            int hourSum = hours;
            int minuteSum = minutes;
            int secondSum = seconds;
            
            if ((secondSum + duration.seconds) / SECONDS_PER_MINUTE > 0) {
                minuteSum += (secondSum + duration.seconds) / SECONDS_PER_MINUTE;
                secondSum = (secondSum + duration.seconds) % SECONDS_PER_MINUTE;
            }
            else {
                secondSum += duration.seconds;
            }
            
            if ((minuteSum + duration.minutes) / MINUTES_PER_HOUR > 0) {
                hourSum += (minuteSum + duration.minutes) / MINUTES_PER_HOUR;
                minuteSum = (minuteSum + duration.minutes) % MINUTES_PER_HOUR;
            }
            else {
                minuteSum += duration.minutes;
            }
            
            if ((hourSum + duration.hours) / HOURS_PER_DAY > 0) {
                daySum += (hourSum + duration.hours) / HOURS_PER_DAY;
                hourSum = (hourSum + duration.hours) % HOURS_PER_DAY;
            }
            else {
                hourSum += duration.hours;
            }
            
            daySum += (duration.weeks > 0) ? duration.weeks * DAYS_PER_WEEK
                    + duration.days : duration.days;
            
            sum = new Dur(daySum, hourSum, minuteSum, secondSum);
        }
        sum.negative = negative;
        return sum;
    }
    
    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        if (negative) {
            b.append('-');
        }
        b.append('P');
        if (weeks > 0) {
            b.append(weeks);
            b.append('W');
        }
        else {
            if (days > 0) {
                b.append(days);
                b.append('D');
            }
            if (hours > 0 || minutes > 0 || seconds > 0) {
                b.append('T');
                if (hours > 0) {
                    b.append(hours);
                    b.append('H');
                }
                if (minutes > 0) {
                    b.append(minutes);
                    b.append('M');
                }
                if (seconds > 0) {
                    b.append(seconds);
                    b.append('S');
                }
            }
            // handle case of zero length duration
            if ((hours + minutes + seconds + days + weeks) == 0) {
                b.append("T0S");
            }
        }
        return b.toString();
    }

    /**
     * {@inheritDoc}
     */
    public final int compareTo(final Object arg0) {
        return compareTo((Dur) arg0);
    }

    /**
     * Compares this duration with another, acording to their length.
     * @param arg0 another duration instance
     * @return a postive value if this duration is longer, zero if the duration
     * lengths are equal, otherwise a negative value
     */
    public final int compareTo(final Dur arg0) {
        int result;
        if (isNegative() != arg0.isNegative()) {
            // return Boolean.valueOf(isNegative()).compareTo(Boolean.valueOf(arg0.isNegative()));
            // for pre-java 1.5 compatibility..
            if (isNegative()) {
                return Integer.MIN_VALUE;
            }
            else {
                return Integer.MAX_VALUE;
            }
        }
        else if (getWeeks() != arg0.getWeeks()) {
            result = getWeeks() - arg0.getWeeks();
        }
        else if (getDays() != arg0.getDays()) {
            result = getDays() - arg0.getDays();
        }
        else if (getHours() != arg0.getHours()) {
            result = getHours() - arg0.getHours();
        }
        else if (getMinutes() != arg0.getMinutes()) {
            result = getMinutes() - arg0.getMinutes();
        }
        else {
            result = getSeconds() - arg0.getSeconds();
        }
        // invert sense of all tests if both durations are negative
        if (isNegative()) {
            return -result;
        }
        else {
            return result;
        }
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
        return new HashCodeBuilder().append(weeks).append(days).append(
                hours).append(minutes).append(seconds).append(negative).toHashCode();
    }
    
    /**
     * @return Returns the days.
     */
    public final int getDays() {
        return days;
    }

    /**
     * @return Returns the hours.
     */
    public final int getHours() {
        return hours;
    }

    /**
     * @return Returns the minutes.
     */
    public final int getMinutes() {
        return minutes;
    }

    /**
     * @return Returns the negative.
     */
    public final boolean isNegative() {
        return negative;
    }

    /**
     * @return Returns the seconds.
     */
    public final int getSeconds() {
        return seconds;
    }

    /**
     * @return Returns the weeks.
     */
    public final int getWeeks() {
        return weeks;
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
