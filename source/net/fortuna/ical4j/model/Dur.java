/*
 * $Id$
 *
 * Created on 20/06/2005
 *
 * Copyright (c) 2005, Ben Fortuna
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

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents a duration of time in iCalendar. Note that according to RFC2445
 * durations represented in weeks are mutually exclusive of other duration
 * fields.
 * <pre>
 * 4.3.6   Duration
 * 
 *    Value Name: DURATION
 * 
 *    Purpose: This value type is used to identify properties that contain
 *    a duration of time.
 * 
 *    Formal Definition: The value type is defined by the following
 *    notation:
 * 
 *      dur-value  = (["+"] / "-") "P" (dur-date / dur-time / dur-week)
 * 
 *      dur-date   = dur-day [dur-time]
 *      dur-time   = "T" (dur-hour / dur-minute / dur-second)
 *      dur-week   = 1*DIGIT "W"
 *      dur-hour   = 1*DIGIT "H" [dur-minute]
 *      dur-minute = 1*DIGIT "M" [dur-second]
 *      dur-second = 1*DIGIT "S"
 *      dur-day    = 1*DIGIT "D"
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Dur implements Comparable {
    
    private static final int DAYS_PER_WEEK = 7;
    
    private static final int WEEKS_PER_YEAR = 52;
    
    private static final int SECONDS_PER_MINUTE = 60;
    
    private static final int MINUTES_PER_HOUR = 60;
    
    private static final int HOURS_PER_DAY = 24;
    
    private static final int DAYS_PER_YEAR = 365;
    
    private static Log log = LogFactory.getLog(Dur.class);

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

        for (StringTokenizer t = new StringTokenizer(value, "+-PWDTHMS", true); t.hasMoreTokens();) {
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
                if (log.isDebugEnabled()) {
                    log.debug("Redundant [P] token ignored.");
                }
            }
            else if ("W".equals(token)) {
                weeks = Integer.parseInt(prevToken);
            }
            else if ("D".equals(token)) {
                days = Integer.parseInt(prevToken);
            }
            else if ("T".equals(token)) {
                // does nothing..
                if (log.isDebugEnabled()) {
                    log.debug("Redundant [T] token ignored.");
                }
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
        this.weeks = weeks;
        this.days = 0;
        this.hours = 0;
        this.minutes = 0;
        this.seconds = 0;
    }
    
    /**
     * Constructs a new duration from the specified arguments.
     * @param days duration in days
     * @param hours duration in hours
     * @param minutes duration in minutes
     * @param seconds duration in seconds
     */
    public Dur(final int days, final int hours, final int minutes, final int seconds) {
        this.weeks = 0;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }
    
    /**
     * Constructs a new duration representing the time between the two
     * specified dates. The end date may precede the start date in order
     * to represent a negative duration.
     * @param start the start date of the duration
     * @param end the end date of the duration
     */
    public Dur(final Date start, final Date end) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        
        int yearDelta = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
        int weekDelta = endCal.get(Calendar.WEEK_OF_YEAR) - startCal.get(Calendar.WEEK_OF_YEAR);
        int dayDelta = endCal.get(Calendar.DAY_OF_YEAR) - startCal.get(Calendar.DAY_OF_YEAR);
        int hourDelta = endCal.get(Calendar.HOUR_OF_DAY) - startCal.get(Calendar.HOUR_OF_DAY);
        int minuteDelta = endCal.get(Calendar.MINUTE) - startCal.get(Calendar.MINUTE);
        int secondDelta = endCal.get(Calendar.SECOND) - startCal.get(Calendar.SECOND);
        
        // test for negativity..
        if (yearDelta < 0
                || (yearDelta == 0 && dayDelta < 0)
                || (yearDelta == 0 && dayDelta == 0 && hourDelta < 0)
                || (yearDelta == 0 && dayDelta == 0 && hourDelta == 0 && minuteDelta < 0)
                || (yearDelta == 0 && dayDelta == 0 && hourDelta == 0 && minuteDelta == 0 && secondDelta < 0)) {
            negative = true;
        }
        
        if ((dayDelta % DAYS_PER_WEEK) + hourDelta + minuteDelta + secondDelta == 0) {
            // weeks..
            while (startCal.get(Calendar.YEAR) != endCal.get(Calendar.YEAR)) {
                weekDelta = WEEKS_PER_YEAR * (endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR));
//                if (weekDelta < 0) {
//                    negative = true;
//                }
                weeks += weekDelta;
                startCal.add(Calendar.WEEK_OF_YEAR, weekDelta);
            }
            if (startCal.get(Calendar.WEEK_OF_YEAR) != endCal.get(Calendar.WEEK_OF_YEAR)) {
                weekDelta = endCal.get(Calendar.WEEK_OF_YEAR) - startCal.get(Calendar.WEEK_OF_YEAR);
//                if (weeks == 0 && weekDelta < 0) {
//                    negative = true;
//                }
                weeks += weekDelta;
            }
            weeks = Math.abs(weeks);
            days = 0;
            hours = 0;
            minutes = 0;
            seconds = 0;
        }
        else {
            // seconds..
            if (secondDelta > 0 && negative) {
                startCal.add(Calendar.MINUTE, 1);
                seconds = SECONDS_PER_MINUTE - secondDelta;
            }
            else if (secondDelta < 0 && !negative) {
                startCal.add(Calendar.MINUTE, 1);
                seconds = SECONDS_PER_MINUTE + secondDelta;
            }
            else {
                seconds = Math.abs(secondDelta);
            }

            // minutes..
            minuteDelta = endCal.get(Calendar.MINUTE) - startCal.get(Calendar.MINUTE);
            if (minuteDelta > 0 && negative) {
                startCal.add(Calendar.HOUR, 1);
                minutes = MINUTES_PER_HOUR - minuteDelta;
            }
            else if (minuteDelta < 0 && !negative) {
                startCal.add(Calendar.HOUR, 1);
                minutes = MINUTES_PER_HOUR + minuteDelta;
            }
            else {
                minutes = Math.abs(minuteDelta);
            }

            // hours..
            hourDelta = endCal.get(Calendar.HOUR_OF_DAY) - startCal.get(Calendar.HOUR_OF_DAY);
            if (hourDelta > 0 && negative) {
                startCal.add(Calendar.DAY_OF_YEAR, 1);
                hours = HOURS_PER_DAY - hourDelta;
            }
            else if (hourDelta < 0 && !negative) {
                startCal.add(Calendar.DAY_OF_YEAR, 1);
                hours = HOURS_PER_DAY + hourDelta;
            }
            else {
                hours = Math.abs(hourDelta);
            }
            
            // days..
            while (startCal.get(Calendar.YEAR) != endCal.get(Calendar.YEAR)) {
                dayDelta = DAYS_PER_YEAR * (endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR));
                days += dayDelta;
                startCal.add(Calendar.DAY_OF_YEAR, dayDelta);
            }
            if (startCal.get(Calendar.DAY_OF_YEAR) != endCal.get(Calendar.DAY_OF_YEAR)) {
                days += endCal.get(Calendar.DAY_OF_YEAR) - startCal.get(Calendar.DAY_OF_YEAR);
            }
            days = Math.abs(days);
            weeks = 0;
        }
    }
    
    /**
     * Returns a date representing the end of this duration from
     * the specified start date.
     * @param start the date to start the duration
     * @return the end of the duration as a date
     */
    public final Date getTime(final Date start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.WEEK_OF_YEAR, weeks);
        cal.add(Calendar.DAY_OF_WEEK, days);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer();
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
        }
        return b.toString();
    }
    
    /**
     * @param arg0
     * @return
     */
    public final int compareTo(final Object arg0) {
        return compareTo((Dur) arg0);
    }
    
    /**
     * Compares this duration with another.
     * @param arg0
     * @return
     */
    public final int compareTo(final Dur arg0) {
        if (isNegative() != arg0.isNegative()) {
            return Boolean.valueOf(isNegative()).compareTo(Boolean.valueOf(arg0.isNegative()));
        }
        else if (getWeeks() != arg0.getWeeks()) {
            return getWeeks() - arg0.getWeeks();
        }
        else if (getDays() != arg0.getDays()) {
            return getDays() - arg0.getDays();
        }
        else if (getHours() != arg0.getHours()) {
            return getHours() - arg0.getHours();
        }
        else if (getMinutes() != arg0.getMinutes()) {
            return getMinutes() - arg0.getMinutes();
        }
        return getSeconds() - arg0.getSeconds();
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
}
