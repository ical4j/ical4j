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

import net.fortuna.ical4j.util.Numbers;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Objects;

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
    public static final WeekDay SU = new WeekDay(Day.SU, 0);

    /**
     * Monday.
     */
    public static final WeekDay MO = new WeekDay(Day.MO, 0);

    /**
     * Tuesday.
     */
    public static final WeekDay TU = new WeekDay(Day.TU, 0);

    /**
     * Wednesday.
     */
    public static final WeekDay WE = new WeekDay(Day.WE, 0);

    /**
     * Thursday.
     */
    public static final WeekDay TH = new WeekDay(Day.TH, 0);

    /**
     * Friday.
     */
    public static final WeekDay FR = new WeekDay(Day.FR, 0);

    /**
     * Saturday.
     */
    public static final WeekDay SA = new WeekDay(Day.SA, 0);

    public enum Day {
        SU(WeekDay.SU),
        MO(WeekDay.MO),
        TU(WeekDay.TU),
        WE(WeekDay.WE),
        TH(WeekDay.TH),
        FR(WeekDay.FR),
        SA(WeekDay.SA);

        public WeekDay weekDay;

        Day(WeekDay weekDay) {
            this.weekDay = weekDay;
        }
    }

    private final Day day;
    
    private final int offset;
    
    /**
     * @param value a string representation of a week day
     */
    public WeekDay(final String value) {
        if (value.length() > 2) {
            offset = Numbers.parseInt(value.substring(0, value.length() - 2));
        } else {
            offset = 0;
        }
        day = Day.valueOf(value.substring(value.length() - 2));
    }
    
    /**
     * @param day a string representation of a week day
     * @param offset a month offset value
     */
    private WeekDay(final Day day, final int offset) {
        Objects.requireNonNull(day, "day");
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
        Objects.requireNonNull(weekDay, "weekDay");
        this.day = weekDay.getDay();
        this.offset = offset;
    }

    /**
     * @return Returns the day.
     */
    public final Day getDay() {
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
        final StringBuilder b = new StringBuilder();
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
    public static WeekDay getWeekDay(final Calendar cal) {
        return getDay(cal.get(Calendar.DAY_OF_WEEK));
    }

    /**
     * Returns a weekday representation of the specified day of week.
     * @param dayOfWeek day of the week
     * @return a weekday instance representing the specified calendar
     */
    public static WeekDay getWeekDay(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case SUNDAY: return SU;
            case MONDAY: return MO;
            case TUESDAY: return TU;
            case WEDNESDAY: return WE;
            case THURSDAY: return TH;
            case FRIDAY: return FR;
            case SATURDAY: return SA;
            default: return null;
        }
    }

    /**
     * Returns a weekday/offset representation of the specified calendar.
     * @param cal a calendar (java.util)
     * @return a weekday instance representing the specified calendar
     */
    public static WeekDay getMonthlyOffset(final Calendar cal) {
        return new WeekDay(getDay(cal.get(Calendar.DAY_OF_WEEK)), cal.get(Calendar.DAY_OF_WEEK_IN_MONTH));
    }
    
    /**
     * Returns a weekday/negative offset representation of the specified calendar.
     * @param cal a calendar (java.util)
     * @return a weekday instance representing the specified calendar
     */
    public static WeekDay getNegativeMonthlyOffset(final Calendar cal) {
        Calendar calClone = (Calendar) cal.clone();
		int delta = -1;
		do {
			calClone.add(Calendar.DAY_OF_YEAR, 7);
			if(calClone.get(Calendar.MONTH)==cal.get(Calendar.MONTH)) {
				delta -= 1;
			}else {
				break;
			}
		}while(delta>-5);
		
		return new WeekDay(getDay(cal.get(Calendar.DAY_OF_WEEK)), delta);
    }
    
    /**
     * Returns the corresponding day constant to the specified
     * java.util.Calendar.DAY_OF_WEEK property.
     * @param calDay a property value of java.util.Calendar.DAY_OF_WEEK
     * @return a string, or null if an invalid DAY_OF_WEEK property is
     * specified
     */
    public static WeekDay getDay(final int calDay) {
        switch (calDay) {
            case Calendar.SUNDAY: return SU;
            case Calendar.MONDAY: return MO;
            case Calendar.TUESDAY: return TU;
            case Calendar.WEDNESDAY: return WE;
            case Calendar.THURSDAY: return TH;
            case Calendar.FRIDAY: return FR;
            case Calendar.SATURDAY: return SA;
            default: return null;
        }
    }

    /**
     * Returns the corresponding <code>java.util.Calendar.DAY_OF_WEEK</code>
     * constant for the specified <code>WeekDay</code>.
     * @param weekday a week day instance
     * @return the corresponding <code>java.util.Calendar</code> day
     */
    public static int getCalendarDay(final WeekDay weekday) {
        switch (weekday.day) {
            case SU: return Calendar.SUNDAY;
            case MO: return Calendar.MONDAY;
            case TU: return Calendar.TUESDAY;
            case WE: return Calendar.WEDNESDAY;
            case TH: return Calendar.THURSDAY;
            case FR: return Calendar.FRIDAY;
            case SA: return Calendar.SATURDAY;
            default: return -1;
        }
    }

    public static DayOfWeek getDayOfWeek(WeekDay weekday) {
        if (weekday == null) {
            return null;
        }
        
        switch (weekday.day) {
            case SU: return DayOfWeek.SUNDAY;
            case MO: return DayOfWeek.MONDAY;
            case TU: return DayOfWeek.TUESDAY;
            case WE: return DayOfWeek.WEDNESDAY;
            case TH: return DayOfWeek.THURSDAY;
            case FR: return DayOfWeek.FRIDAY;
            case SA: return DayOfWeek.SATURDAY;
            default: return null;
        }
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
        return Objects.equals(wd.getDay(), getDay())
            && wd.getOffset() == getOffset();
    }
    
    /**
     * {@inheritDoc}
     */
    public final int hashCode() {
        return new HashCodeBuilder().append(getDay()).append(getOffset()).toHashCode();
    }
}
