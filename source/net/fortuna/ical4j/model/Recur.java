/*
 * $Id$ [18-Apr-2004]
 *
 * Copyright (c) 2004, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
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
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.fortuna.ical4j.util.DateFormat;
import net.fortuna.ical4j.util.DateTimeFormat;

/**
 * Defines a recurrence.
 *
 * @author benfortuna
 */
public class Recur implements Serializable {

    private static final String FREQ = "FREQ";

    private static final String UNTIL = "UNTIL";

    private static final String COUNT = "COUNT";

    private static final String INTERVAL = "INTERVAL";

    private static final String BYSECOND = "BYSECOND";

    private static final String BYMINUTE = "BYMINUTE";

    private static final String BYHOUR = "BYHOUR";

    private static final String BYDAY = "BYDAY";

    private static final String BYMONTHDAY = "BYMONTHDAY";

    private static final String BYYEARDAY = "BYYEARDAY";

    private static final String BYWEEKNO = "BYWEEKNO";

    private static final String BYMONTH = "BYMONTH";

    private static final String BYSETPOS = "BYSETPOS";

    private static final String WKST = "WKST";

    // frequencies..
    public static final String SECONDLY = "SECONDLY";

    public static final String MINUTELY = "MINUTELY";

    public static final String HOURLY = "HOURLY";

    public static final String DAILY = "DAILY";

    public static final String WEEKLY = "WEEKLY";

    public static final String MONTHLY = "MONTHLY";

    public static final String YEARLY = "YEARLY";

    // weekdays..
    public static final String SU = "SU";

    public static final String MO = "MO";

    public static final String TU = "TU";

    public static final String WE = "WE";

    public static final String TH = "TH";

    public static final String FR = "FR";

    public static final String SA = "SA";

    private String frequency;

    private Date until;

    private int count = -1;

    private int interval = -1;

    private NumberList secondList;

    private NumberList minuteList;

    private NumberList hourList;

    private DayList dayList;

    private NumberList monthDayList;

    private NumberList yearDayList;

    private NumberList weekNoList;

    private NumberList monthList;

    private NumberList setPosList;

    private String weekStartDay;

    private Map experimentalValues;

    /**
     * Constructor.
     *
     * @param aValue
     *            a string representation of a recurrence.
     * @throws ParseException thrown when the specified string
     * contains an invalid representation of an UNTIL date value
     */
    public Recur(final String aValue) throws ParseException {

        experimentalValues = new HashMap();

        for (StringTokenizer t = new StringTokenizer(aValue, ";="); t
                .hasMoreTokens();) {

            String token = t.nextToken();

            if (FREQ.equals(token)) {
                frequency = t.nextToken();
            }
            else if (UNTIL.equals(token)) {
                String untilString = t.nextToken();

                try {
                    until = DateFormat.getInstance().parse(untilString);
                }
                catch (ParseException pe) {
                    until = DateTimeFormat.getInstance().parse(untilString);
                }
            }
            else if (COUNT.equals(token)) {
                count = Integer.parseInt(t.nextToken());
            }
            else if (INTERVAL.equals(token)) {
                interval = Integer.parseInt(t.nextToken());
            }
            else if (BYSECOND.equals(token)) {
                secondList = new NumberList(t.nextToken());
            }
            else if (BYMINUTE.equals(token)) {
                minuteList = new NumberList(t.nextToken());
            }
            else if (BYHOUR.equals(token)) {
                hourList = new NumberList(t.nextToken());
            }
            else if (BYDAY.equals(token)) {
                dayList = new DayList(t.nextToken());
            }
            else if (BYMONTHDAY.equals(token)) {
                monthDayList = new NumberList(t.nextToken());
            }
            else if (BYYEARDAY.equals(token)) {
                yearDayList = new NumberList(t.nextToken());
            }
            else if (BYWEEKNO.equals(token)) {
                weekNoList = new NumberList(t.nextToken());
            }
            else if (BYMONTH.equals(token)) {
                monthList = new NumberList(t.nextToken());
            }
            else if (BYSETPOS.equals(token)) {
                setPosList = new NumberList(t.nextToken());
            }
            else if (WKST.equals(token)) {
                weekStartDay = t.nextToken();
            }
            // assume experimental value..
            else {
                experimentalValues.put(token, t.nextToken());
            }
        }
    }

    public Recur(final String frequency, final Date until) {
		this.frequency = frequency;
		this.until = until;
	}

    public Recur(final String frequency, final int count) {
		this.frequency = frequency;
		this.count = count;
	}

    /**
     * @return Returns the dayList.
     */
    public final DayList getDayList() {
        return dayList;
    }

    /**
     * @return Returns the hourList.
     */
    public final NumberList getHourList() {
        return hourList;
    }

    /**
     * @return Returns the minuteList.
     */
    public final NumberList getMinuteList() {
        return minuteList;
    }

    /**
     * @return Returns the monthDayList.
     */
    public final NumberList getMonthDayList() {
        return monthDayList;
    }

    /**
     * @return Returns the monthList.
     */
    public final NumberList getMonthList() {
        return monthList;
    }

    /**
     * @return Returns the secondList.
     */
    public final NumberList getSecondList() {
        return secondList;
    }

    /**
     * @return Returns the setPosList.
     */
    public final NumberList getSetPosList() {
        return setPosList;
    }

    /**
     * @return Returns the weekNoList.
     */
    public final NumberList getWeekNoList() {
        return weekNoList;
    }

    /**
     * @return Returns the yearDayList.
     */
    public final NumberList getYearDayList() {
        return yearDayList;
    }

    /**
     * @return Returns the count.
     */
    public final int getCount() {
        return count;
    }

    /**
     * @return Returns the experimentalValues.
     */
    public final Map getExperimentalValues() {
        return experimentalValues;
    }

    /**
     * @return Returns the frequency.
     */
    public final String getFrequency() {
        return frequency;
    }

    /**
     * @return Returns the interval.
     */
    public final int getInterval() {
        return interval;
    }

    /**
     * @return Returns the until.
     */
    public final Date getUntil() {
        return until;
    }

    /**
     * @return Returns the weekStartDay.
     */
    public final String getWeekStartDay() {
        return weekStartDay;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {

        StringBuffer b = new StringBuffer();

        b.append(FREQ);
        b.append('=');
        b.append(frequency);

        if (interval >= 0) {
            b.append(';');
            b.append(INTERVAL);
            b.append('=');
            b.append(interval);
        }

        if (until != null) {
            b.append(';');
            b.append(UNTIL);
            b.append('=');
            // although UNTIL may be either date or date-time,
            // for now we'll assume its a date and output as such..
            b.append(DateFormat.getInstance().format(until));
        }

        if (count >= 0) {
            b.append(';');
            b.append(COUNT);
            b.append('=');
            b.append(count);
        }

        if (secondList != null) {
            b.append(';');
            b.append(BYSECOND);
            b.append('=');
            b.append(secondList);
        }

        if (minuteList != null) {
            b.append(';');
            b.append(BYMINUTE);
            b.append('=');
            b.append(minuteList);
        }

        if (hourList != null) {
            b.append(';');
            b.append(BYHOUR);
            b.append('=');
            b.append(hourList);
        }

        if (dayList != null) {
            b.append(';');
            b.append(BYDAY);
            b.append('=');
            b.append(dayList);
        }

        if (monthDayList != null) {
            b.append(';');
            b.append(BYMONTHDAY);
            b.append('=');
            b.append(monthDayList);
        }

        if (yearDayList != null) {
            b.append(';');
            b.append(BYYEARDAY);
            b.append('=');
            b.append(yearDayList);
        }

        if (weekNoList != null) {
            b.append(';');
            b.append(BYWEEKNO);
            b.append('=');
            b.append(weekNoList);
        }

        if (monthList != null) {
            b.append(';');
            b.append(BYMONTH);
            b.append('=');
            b.append(monthList);
        }

        if (setPosList != null) {
            b.append(';');
            b.append(BYSETPOS);
            b.append('=');
            b.append(setPosList);
        }

        if (weekStartDay != null) {
            b.append(';');
            b.append(WKST);
            b.append('=');
            b.append(weekStartDay);
        }

        return b.toString();
    }
}