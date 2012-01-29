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

import java.io.Serializable;
import java.util.Calendar;

import net.fortuna.ical4j.util.Numbers;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * $Id$
 * 
 * Created: 19/12/2004
 *
 * Defines a day of the week with a possible offset related to
 * a MONTHLY or YEARLY occurrence.
 * 
 * @author Ben Fortuna
 */
public class WeekDay implements Serializable {
    
    private static final long serialVersionUID = -4412000990022011469L;

    /**
     * Sunday.
     */
    public static final WeekDay SU = new WeekDay("SU", 0);

    /**
     * Monday.
     */
    public static final WeekDay MO = new WeekDay("MO", 0);

    /**
     * Tuesday.
     */
    public static final WeekDay TU = new WeekDay("TU", 0);

    /**
     * Wednesday.
     */
    public static final WeekDay WE = new WeekDay("WE", 0);

    /**
     * Thursday.
     */
    public static final WeekDay TH = new WeekDay("TH", 0);

    /**
     * Friday.
     */
    public static final WeekDay FR = new WeekDay("FR", 0);

    /**
     * Saturday.
     */
    public static final WeekDay SA = new WeekDay("SA", 0);

    private String day;
    
    private int offset;
    
    /**
     * @param value a string representation of a week day
     */
    public WeekDay(final String value) {
        if (value.length() > 2) {
            offset = Numbers.parseInt(value.substring(0, value.length() - 2));
        }
        else {
            offset = 0;
        }
        day = value.substring(value.length() - 2);
        validateDay();
    }
    
    /**
     * @param day a string representation of a week day
     * @param offset a month offset value
     */
    private WeekDay(final String day, final int offset) {
        this.day = day;
        this.offset = offset;
    }
    
    /**
     * Constructs a new weekday instance based on the specified
     * instance and offset.
     * @param weekDay a week day template for the instance
     * @param offset a month offset value
     */
    public WeekDay(final WeekDay weekDay, final int offset) {
        this.day = weekDay.getDay();
        this.offset = offset;
    }
    
    private void validateDay() {
        if (!SU.day.equals(day)
            && !MO.day.equals(day)
            && !TU.day.equals(day)
            && !WE.day.equals(day)
            && !TH.day.equals(day)
            && !FR.day.equals(day)
            && !SA.day.equals(day)) {
            throw new IllegalArgumentException("Invalid day: " + day);
        }
    }
    /**
     * @return Returns the day.
     */
    public final String getDay() {
        return day;
    }
    
    /**
     * @return Returns the offset.
     */
    public final int getOffset() {
        return offset;
    }
        
    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        if (getOffset() != 0) {
            b.append(getOffset());
        }
        b.append(getDay());
        return b.toString();
    }
    
    /**
     * Returns a weekday representation of the specified calendar.
     * @param cal a calendar (java.util)
     * @return a weekday instance representing the specified calendar
     */
    public static final WeekDay getWeekDay(final Calendar cal) {
        return new WeekDay(getDay(cal.get(Calendar.DAY_OF_WEEK)), 0);
    }
    
    /**
     * Returns a weekday/offset representation of the specified calendar.
     * @param cal a calendar (java.util)
     * @return a weekday instance representing the specified calendar
     */
    public static final WeekDay getMonthlyOffset(final Calendar cal) {
        return new WeekDay(getDay(cal.get(Calendar.DAY_OF_WEEK)), cal.get(Calendar.DAY_OF_WEEK_IN_MONTH));
    }
    
    /**
     * Returns a weekday/negative offset representation of the specified calendar.
     * @param cal a calendar (java.util)
     * @return a weekday instance representing the specified calendar
     */
    public static final WeekDay getNegativeMonthlyOffset(final Calendar cal) {
        return new WeekDay(getDay(cal.get(Calendar.DAY_OF_WEEK)), cal.get(Calendar.DAY_OF_WEEK_IN_MONTH) - 6);
    }
    
    /**
     * Returns the corresponding day constant to the specified
     * java.util.Calendar.DAY_OF_WEEK property.
     * @param calDay a property value of java.util.Calendar.DAY_OF_WEEK
     * @return a string, or null if an invalid DAY_OF_WEEK property is
     * specified
     */
    public static WeekDay getDay(final int calDay) {
        WeekDay day = null;
        switch (calDay) {
            case Calendar.SUNDAY:
                day = SU;
                break;
            case Calendar.MONDAY:
                day = MO;
                break;
            case Calendar.TUESDAY:
                day = TU;
                break;
            case Calendar.WEDNESDAY:
                day = WE;
                break;
            case Calendar.THURSDAY:
                day = TH;
                break;
            case Calendar.FRIDAY:
                day = FR;
                break;
            case Calendar.SATURDAY:
                day = SA;
                break;
            default:
                break;
        }
        return day;
    }
    
    /**
     * Returns the corresponding <code>java.util.Calendar.DAY_OF_WEEK</code>
     * constant for the specified <code>WeekDay</code>.
     * @param weekday a week day instance
     * @return the corresponding <code>java.util.Calendar</code> day
     */
    public static int getCalendarDay(final WeekDay weekday) {
        int calendarDay = -1;
        if (SU.getDay().equals(weekday.getDay())) {
            calendarDay = Calendar.SUNDAY;
        }
        else if (MO.getDay().equals(weekday.getDay())) {
            calendarDay = Calendar.MONDAY;
        }
        else if (TU.getDay().equals(weekday.getDay())) {
            calendarDay = Calendar.TUESDAY;
        }
        else if (WE.getDay().equals(weekday.getDay())) {
            calendarDay = Calendar.WEDNESDAY;
        }
        else if (TH.getDay().equals(weekday.getDay())) {
            calendarDay = Calendar.THURSDAY;
        }
        else if (FR.getDay().equals(weekday.getDay())) {
            calendarDay = Calendar.FRIDAY;
        }
        else if (SA.getDay().equals(weekday.getDay())) {
            calendarDay = Calendar.SATURDAY;
        }
        return calendarDay;
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean equals(final Object arg0) {
        if (arg0 == null) {
            return false;
        }
        if (!(arg0 instanceof WeekDay)) {
            return false;
        }
        final WeekDay wd = (WeekDay) arg0;
        return ObjectUtils.equals(wd.getDay(), getDay())
            && wd.getOffset() == getOffset();
    }
    
    /**
     * {@inheritDoc}
     */
    public final int hashCode() {
        return new HashCodeBuilder().append(getDay())
            .append(getOffset()).toHashCode();
    }
}
