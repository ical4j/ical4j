/*
 * $Id$
 *
 * Created on 26/06/2005
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Implements a collection of utility methods relevant to date processing.
 * 
 * @author Ben Fortuna
 */
public final class Dates {

    public static final long MILLIS_PER_SECOND = 1000;

    public static final long MILLIS_PER_MINUTE = 60000;

    public static final long MILLIS_PER_HOUR = 3600000;

    public static final long MILLIS_PER_DAY = 86400000;

    public static final long MILLIS_PER_WEEK = 604800000;
    
    public static final int DAYS_PER_WEEK = 7;

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
     * @param date
     * @param weekNo
     * @return
     */
    public static int getAbsWeekNo(final Date date, final int weekNo) {
        if (weekNo == 0 || weekNo < -53 || weekNo > 53) {
            throw new IllegalArgumentException("Invalid week number [" + weekNo + "]");
        }
        if (weekNo > 0) {
            return weekNo;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        // construct a list of possible week numbers..
        List weeks = new ArrayList();
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
     * @param date
     * @param yearDay
     * @return
     */
    public static int getAbsYearDay(final Date date, final int yearDay) {
        if (yearDay == 0 || yearDay < -366 || yearDay > 366) {
            throw new IllegalArgumentException("Invalid year day [" + yearDay + "]");
        }
        if (yearDay > 0) {
            return yearDay;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        // construct a list of possible year days..
        List days = new ArrayList();
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
     * @param date
     * @param monthDay
     * @return
     */
    public static int getAbsMonthDay(final java.util.Date date, final int monthDay) {
        if (monthDay == 0 || monthDay < -31 || monthDay > 31) {
            throw new IllegalArgumentException("Invalid month day [" + monthDay + "]");
        }
        if (monthDay > 0) {
            return monthDay;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        // construct a list of possible month days..
        List days = new ArrayList();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        while (cal.get(Calendar.MONTH) == month) {
            days.add(new Integer(cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return ((Integer) days.get(days.size() + monthDay)).intValue();
    }
}
