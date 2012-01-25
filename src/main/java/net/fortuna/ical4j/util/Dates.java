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
package net.fortuna.ical4j.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.parameter.Value;

/**
 * $Id$
 *
 * Created on 26/06/2005
 *
 * Implements a collection of utility methods relevant to date processing.
 * 
 * @author Ben Fortuna
 */
public final class Dates {

    /**
     * Number of milliseconds in one second.
     */
    public static final long MILLIS_PER_SECOND = 1000;

    /**
     * Number of milliseconds in one minute.
     */
    public static final long MILLIS_PER_MINUTE = 60000;

    /**
     * Number of milliseconds in one hour.
     */
    public static final long MILLIS_PER_HOUR = 3600000;

    /**
     * Number of milliseconds in one day.
     */
    public static final long MILLIS_PER_DAY = 86400000;

    /**
     * Number of milliseconds in one week.
     */
    public static final long MILLIS_PER_WEEK = 604800000;
    
    /**
     * Number of days in one week.
     */
    public static final int DAYS_PER_WEEK = 7;

    /**
     * Constant indicating precision to the second.
     */
    public static final int PRECISION_SECOND = 0;

    /**
     * Constant indicating precision to the day.
     */
    public static final int PRECISION_DAY = 1;

    /**
     * Maximum number of weeks per year.
     */
    public static final int MAX_WEEKS_PER_YEAR = 53;

    /**
     * Maximum number of days per year.
     */
    public static final int MAX_DAYS_PER_YEAR = 366;

    /**
     * Maximum number of days per month.
     */
    public static final int MAX_DAYS_PER_MONTH = 31;

    private static final String INVALID_WEEK_MESSAGE = "Invalid week number [{0}]";
    
    private static final String INVALID_YEAR_DAY_MESSAGE = "Invalid year day [{0}]";
    
    private static final String INVALID_MONTH_DAY_MESSAGE = "Invalid month day [{0}]";
    
    /**
     * Constructor made private to prevent instantiation.
     */
    private Dates() {
    }

    /**
     * Returns the absolute week number for the year specified by the
     * supplied date. Note that a value of zero (0) is invalid for the
     * weekNo parameter and an <code>IllegalArgumentException</code>
     * will be thrown.
     * @param date a date instance representing a week of the year
     * @param weekNo a week number offset
     * @return the absolute week of the year for the specified offset
     */
    public static int getAbsWeekNo(final java.util.Date date, final int weekNo) {
        if (weekNo == 0 || weekNo < -MAX_WEEKS_PER_YEAR || weekNo > MAX_WEEKS_PER_YEAR) {
            throw new IllegalArgumentException(MessageFormat.format(INVALID_WEEK_MESSAGE,
                    new Object[] {new Integer(weekNo)}));
        }
        if (weekNo > 0) {
            return weekNo;
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int year = cal.get(Calendar.YEAR);
        // construct a list of possible week numbers..
        final List weeks = new ArrayList();
        cal.set(Calendar.WEEK_OF_YEAR, 1);
        while (cal.get(Calendar.YEAR) == year) {
            weeks.add(new Integer(cal.get(Calendar.WEEK_OF_YEAR)));
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        }
        return ((Integer) weeks.get(weeks.size() + weekNo)).intValue();
    }

    /**
     * Returns the absolute year day for the year specified by the
     * supplied date. Note that a value of zero (0) is invalid for the
     * yearDay parameter and an <code>IllegalArgumentException</code>
     * will be thrown.
     * @param date a date instance representing a day of the year
     * @param yearDay a day of year offset
     * @return the absolute day of month for the specified offset
     */
    public static int getAbsYearDay(final java.util.Date date, final int yearDay) {
        if (yearDay == 0 || yearDay < -MAX_DAYS_PER_YEAR || yearDay > MAX_DAYS_PER_YEAR) {
            throw new IllegalArgumentException(MessageFormat.format(INVALID_YEAR_DAY_MESSAGE,
                    new Object[] {new Integer(yearDay)}));
        }
        if (yearDay > 0) {
            return yearDay;
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int year = cal.get(Calendar.YEAR);
        // construct a list of possible year days..
        final List days = new ArrayList();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        while (cal.get(Calendar.YEAR) == year) {
            days.add(new Integer(cal.get(Calendar.DAY_OF_YEAR)));
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return ((Integer) days.get(days.size() + yearDay)).intValue();
    }

    /**
     * Returns the absolute month day for the month specified by the
     * supplied date. Note that a value of zero (0) is invalid for the
     * monthDay parameter and an <code>IllegalArgumentException</code>
     * will be thrown.
     * @param date a date instance representing a day of the month
     * @param monthDay a day of month offset
     * @return the absolute day of month for the specified offset
     */
    public static int getAbsMonthDay(final java.util.Date date, final int monthDay) {
        if (monthDay == 0 || monthDay < -MAX_DAYS_PER_MONTH || monthDay > MAX_DAYS_PER_MONTH) {
            throw new IllegalArgumentException(MessageFormat.format(INVALID_MONTH_DAY_MESSAGE,
                    new Object[] {new Integer(monthDay)}));
        }
        if (monthDay > 0) {
            return monthDay;
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int month = cal.get(Calendar.MONTH);
        // construct a list of possible month days..
        final List days = new ArrayList();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        while (cal.get(Calendar.MONTH) == month) {
            days.add(new Integer(cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return ((Integer) days.get(days.size() + monthDay)).intValue();
    }
    
    /**
     * Returns a new date instance of the specified type. If no type is
     * specified a DateTime instance is returned.
     * @param date a seed Java date instance
     * @param type the type of date instance
     * @return an instance of <code>net.fortuna.ical4j.model.Date</code>
     */
    public static Date getInstance(final java.util.Date date, final Value type) {
        if (Value.DATE.equals(type)) {
            return new Date(date);
        }
        return new DateTime(date);
    }
    
    /**
     * Returns an instance of <code>java.util.Calendar</code> that is suitably
     * initialised for working with the specified date.
     * @param date a date instance
     * @return a <code>java.util.Calendar</code>
     */
    public static Calendar getCalendarInstance(final Date date) {
        Calendar instance = null;
        if (date instanceof DateTime) {
            final DateTime dateTime = (DateTime) date;
            if (dateTime.getTimeZone() != null) {
                instance = Calendar.getInstance(dateTime.getTimeZone());
            }
            else if (dateTime.isUtc()) {
                instance = Calendar.getInstance(TimeZones.getUtcTimeZone());
            }
            else {
            	// a date-time without a timezone but not UTC is floating
                instance = Calendar.getInstance();
            }
        }
        else {
            instance = Calendar.getInstance(TimeZones.getDateTimeZone());
        }
        return instance;
    }
    
    /**
     * @param time the time value to round
     * @param precision the rounding precision
     * @return a round time value
     * @deprecated It is not all that useful to perform rounding without specifying an
     * explicit timezone.
     */
    public static long round(final long time, final int precision) {
        return round(time, precision, TimeZone.getDefault());
//        return round(time, precision, TimeZone.getTimeZone(TimeZones.UTC_ID));
        /*
        long newTime = time;
        if (precision == PRECISION_DAY) {
            long remainder = newTime%(1000*60*60); // get the mod remainder using milliseconds*seconds*min
            newTime = newTime-remainder;
              // remove the remainder from the time to clear the milliseconds, seconds and minutes
        }
        else if (precision == PRECISION_SECOND) {
            long remainder = newTime%(1000); // get the mod remainder using milliseconds
            newTime = newTime-remainder;  // remove the remainder from the time to clear the milliseconds
        }
        return newTime;
	*/
    }
    
    /**
     * Rounds a time value to remove any precision smaller than specified.
     * @param time the time value to round
     * @param precision the rounding precision
     * @param tz the timezone of the rounded value
     * @return a round time value
     */
    public static long round(final long time, final int precision, final TimeZone tz) {
        if ((precision == PRECISION_SECOND) && ((time % Dates.MILLIS_PER_SECOND) == 0)) {
            return time;
        }
        final Calendar cal = Calendar.getInstance(tz);
        cal.setTimeInMillis(time);
        if (precision == PRECISION_DAY) {
//            return (long) Math.floor(time / (double) Dates.MILLIS_PER_DAY) * Dates.MILLIS_PER_DAY;
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
        }
        else if (precision == PRECISION_SECOND) {
//            return (long) Math.floor(time / (double) Dates.MILLIS_PER_SECOND) * Dates.MILLIS_PER_SECOND;
            cal.clear(Calendar.MILLISECOND);
        }
        // unrecognised precision..
        return cal.getTimeInMillis();
    }

    /**
     * Returns the {@code System.currentTimeMillis()}, rounded to the second.
     * <p>By doing a rough rounding here, we avoid an expensive java.util.Calendar based
     *  rounding later on.</p>
     * @return the current time in millisec.
     */
    public static long getCurrentTimeRounded() {
        return (long) Math.floor(System.currentTimeMillis() / (double) Dates.MILLIS_PER_SECOND) * Dates.MILLIS_PER_SECOND;
    }
}
