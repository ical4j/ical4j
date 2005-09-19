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
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Dates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines a recurrence.
 * @version 2.0
 * @author Ben Fortuna
 */
public class Recur implements Serializable {

    private static final long serialVersionUID = -7333226591784095142L;

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

    private static Log log = LogFactory.getLog(Recur.class);

    private String frequency;

    private Date until;

    private int count = -1;

    private int interval = -1;

    private NumberList secondList;

    private NumberList minuteList;

    private NumberList hourList;

    private WeekDayList dayList;

    private NumberList monthDayList;

    private NumberList yearDayList;

    private NumberList weekNoList;

    private NumberList monthList;

    private NumberList setPosList;

    private String weekStartDay;

    private Map experimentalValues = new HashMap();

    /**
     * Constructs a new instance from the specified string value.
     * @param aValue
     *            a string representation of a recurrence.
     * @throws ParseException
     *             thrown when the specified string contains an invalid
     *             representation of an UNTIL date value
     */
    public Recur(final String aValue) throws ParseException {
        for (StringTokenizer t = new StringTokenizer(aValue, ";="); t
                .hasMoreTokens();) {
            String token = t.nextToken();
            if (FREQ.equals(token)) {
                frequency = t.nextToken();
            }
            else if (UNTIL.equals(token)) {
                String untilString = t.nextToken();
                try {
                    until = new DateTime(untilString);
                    // UNTIL must be specified in UTC time..
                    ((DateTime) until).setUtc(true);
                }
                catch (ParseException pe) {
                    until = new Date(untilString);
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
                dayList = new WeekDayList(t.nextToken());
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

    /**
     * @param frequency
     * @param until
     */
    public Recur(final String frequency, final Date until) {
        this.frequency = frequency;
        this.until = until;
    }

    /**
     * @param frequency
     * @param count
     */
    public Recur(final String frequency, final int count) {
        this.frequency = frequency;
        this.count = count;
    }

    /**
     * @return Returns the dayList.
     */
    public final WeekDayList getDayList() {
        if (dayList == null) {
            dayList = new WeekDayList();
        }
        return dayList;
    }

    /**
     * @return Returns the hourList.
     */
    public final NumberList getHourList() {
        if (hourList == null) {
            hourList = new NumberList();
        }
        return hourList;
    }

    /**
     * @return Returns the minuteList.
     */
    public final NumberList getMinuteList() {
        if (minuteList == null) {
            minuteList = new NumberList();
        }
        return minuteList;
    }

    /**
     * @return Returns the monthDayList.
     */
    public final NumberList getMonthDayList() {
        if (monthDayList == null) {
            monthDayList = new NumberList();
        }
        return monthDayList;
    }

    /**
     * @return Returns the monthList.
     */
    public final NumberList getMonthList() {
        if (monthList == null) {
            monthList = new NumberList();
        }
        return monthList;
    }

    /**
     * @return Returns the secondList.
     */
    public final NumberList getSecondList() {
        if (secondList == null) {
            secondList = new NumberList();
        }
        return secondList;
    }

    /**
     * @return Returns the setPosList.
     */
    public final NumberList getSetPosList() {
        if (setPosList == null) {
            setPosList = new NumberList();
        }
        return setPosList;
    }

    /**
     * @return Returns the weekNoList.
     */
    public final NumberList getWeekNoList() {
        if (weekNoList == null) {
            weekNoList = new NumberList();
        }
        return weekNoList;
    }

    /**
     * @return Returns the yearDayList.
     */
    public final NumberList getYearDayList() {
        if (yearDayList == null) {
            yearDayList = new NumberList();
        }
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
     * @param weekStartDay The weekStartDay to set.
     */
    public final void setWeekStartDay(final String weekStartDay) {
        this.weekStartDay = weekStartDay;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer();
        b.append(FREQ);
        b.append('=');
        b.append(frequency);
        if (weekStartDay != null) {
            b.append(';');
            b.append(WKST);
            b.append('=');
            b.append(weekStartDay);
        }
        if (interval >= 1) {
            b.append(';');
            b.append(INTERVAL);
            b.append('=');
            b.append(interval);
        }
        if (until != null) {
            b.append(';');
            b.append(UNTIL);
            b.append('=');
            // Note: date-time representations should always be in UTC time.
            b.append(until);
        }
        if (count >= 1) {
            b.append(';');
            b.append(COUNT);
            b.append('=');
            b.append(count);
        }
        if (!getMonthList().isEmpty()) {
            b.append(';');
            b.append(BYMONTH);
            b.append('=');
            b.append(monthList);
        }
        if (!getWeekNoList().isEmpty()) {
            b.append(';');
            b.append(BYWEEKNO);
            b.append('=');
            b.append(weekNoList);
        }
        if (!getYearDayList().isEmpty()) {
            b.append(';');
            b.append(BYYEARDAY);
            b.append('=');
            b.append(yearDayList);
        }
        if (!getMonthDayList().isEmpty()) {
            b.append(';');
            b.append(BYMONTHDAY);
            b.append('=');
            b.append(monthDayList);
        }
        if (!getDayList().isEmpty()) {
            b.append(';');
            b.append(BYDAY);
            b.append('=');
            b.append(dayList);
        }
        if (!getHourList().isEmpty()) {
            b.append(';');
            b.append(BYHOUR);
            b.append('=');
            b.append(hourList);
        }
        if (!getMinuteList().isEmpty()) {
            b.append(';');
            b.append(BYMINUTE);
            b.append('=');
            b.append(minuteList);
        }
        if (!getSecondList().isEmpty()) {
            b.append(';');
            b.append(BYSECOND);
            b.append('=');
            b.append(secondList);
        }
        if (!getSetPosList().isEmpty()) {
            b.append(';');
            b.append(BYSETPOS);
            b.append('=');
            b.append(setPosList);
        }
        return b.toString();
    }
    
    /**
     * Returns a list of start dates in the specified period represented by this recur.
     * Any date fields not specified by this recur are retained from the period start,
     * and as such you should ensure the period start is initialised correctly.
     * @param periodStart the start of the period
     * @param periodEnd the end of the period
     * @param value the type of dates to generate (i.e. date/date-time)
     * @return a list of dates
     */
    public final DateList getDates(final Date periodStart, final Date periodEnd, final Value value) {
        return getDates(periodStart, periodStart, periodEnd, value);
    }

    /**
     * Returns a list of start dates in the specified period represented
     * by this recur.  This method includes a base date argument, which
     * indicates the start of the fist occurrence of this recurrence.
     *
     * The base date is used to inject default values to return a set of dates in
     * the correct format.  For example, if the search start date (start) is
     * Wed, Mar 23, 12:19PM, but the recurrence is Mon - Fri, 9:00AM - 5:00PM,
     * the start dates returned should all be at 9:00AM, and not 12:19PM.
     *
     * @return a list of dates represented by this recur instance
     * @param base the start date of this Recurrence's first instance
     * @param periodStart the start of the period
     * @param periodEnd the end of the period
     * @param value the type of dates to generate (i.e. date/date-time)
     */
    public final DateList getDates(final Date seed, final Date periodStart,
            final Date periodEnd, final Value value) {
        DateList dates = new DateList(value);
        Calendar cal = Calendar.getInstance();
        cal.setTime(seed);
        int invalidCandidateCount = 0;
        while (!((getUntil() != null && cal.getTime().after(getUntil()))
                || (periodEnd != null && cal.getTime().after(periodEnd))
                || (getCount() >= 1 && (dates.size() + invalidCandidateCount) >= getCount()))) {
            DateList candidates = getCandidates(new Date(cal.getTime().getTime()), value);
            for (Iterator i = candidates.iterator(); i.hasNext();) {
                Date candidate = (Date) i.next();
                // don't count candidates that occur before the seed date..
                if (!candidate.before(seed)) {
                    if (candidate.before(periodStart) || candidate.after(periodEnd)) {
                        invalidCandidateCount++;
                    }
                    else if (getCount() >= 1 && (dates.size() + invalidCandidateCount) >= getCount()) {
                        break;
                    }
                    else if (!(getUntil() != null && cal.getTime().after(getUntil()))) {
                        dates.add(candidate);
                    }
                }
            }
            increment(cal);
        }
        // sort final list..
        Collections.sort(dates);
        return dates;
    }

    /**
     * Increments the specified calendar according to the
     * frequency and interval specified in this recurrence
     * rule.
     * @param cal a java.util.Calendar to increment
     */
    private void increment(final Calendar cal) {
        // initialise interval..
        int calInterval = (getInterval() >= 1) ? getInterval() : 1;
        if (SECONDLY.equals(getFrequency())) {
            cal.add(Calendar.SECOND, calInterval);
        }
        else if (MINUTELY.equals(getFrequency())) {
            cal.add(Calendar.MINUTE, calInterval);
        }
        else if (HOURLY.equals(getFrequency())) {
            cal.add(Calendar.HOUR_OF_DAY, calInterval);
        }
        else if (DAILY.equals(getFrequency())) {
            cal.add(Calendar.DAY_OF_YEAR, calInterval);
        }
        else if (WEEKLY.equals(getFrequency())) {
            cal.add(Calendar.WEEK_OF_YEAR, calInterval);
        }
        else if (MONTHLY.equals(getFrequency())) {
            cal.add(Calendar.MONTH, calInterval);
        }
        else if (YEARLY.equals(getFrequency())) {
            cal.add(Calendar.YEAR, calInterval);
        }
    }
    
    /**
     * Returns a list of possible dates generated from the applicable
     * BY* rules, using the specified date as a seed.
     * @param date the seed date
     * @param value the type of date list to return
     * @return a DateList
     */
    private DateList getCandidates(final Date date, final Value value) {
        DateList dates = new DateList(value);
        dates.add(date);
        dates = getMonthVariants(dates);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYMONTH processing: " + dates);
        }
        dates = getWeekNoVariants(dates);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYWEEKNO processing: " + dates);
        }
        dates = getYearDayVariants(dates);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYYEARDAY processing: " + dates);
        }
        dates = getMonthDayVariants(dates);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYMONTHDAY processing: " + dates);
        }
        dates = getDayVariants(dates);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYDAY processing: " + dates);
        }
        dates = getHourVariants(dates);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYHOUR processing: " + dates);
        }
        dates = getMinuteVariants(dates);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYMINUTE processing: " + dates);
        }
        dates = getSecondVariants(dates);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYSECOND processing: " + dates);
        }
        dates = applySetPosRules(dates);
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after SETPOS processing: " + dates);
        }
        return dates;
    }

    /**
     * Applies BYSETPOS rules to <code>dates</code>. Valid positions are from
     * 1 to the size of the date list. Invalid positions are ignored.
     * 
     * @param dates
     */
    private DateList applySetPosRules(final DateList dates) {
        // return if no SETPOS rules specified..
        if (getSetPosList().isEmpty()) {
            return dates;
        }
        // sort the list before processing..
        Collections.sort(dates);
        DateList setPosDates = new DateList(dates.getType());
        int size = dates.size();
        for (Iterator i = getSetPosList().iterator(); i.hasNext();) {
            Integer setPos = (Integer) i.next();
            int pos = setPos.intValue();
            if (pos > 0 && pos <= size) {
                setPosDates.add(dates.get(pos - 1));
            }
            else if (pos < 0 && pos >= -size) {
                setPosDates.add(dates.get(size + pos));
            }
        }
        return setPosDates;
    }
    
    /**
     * Applies BYMONTH rules specified in this Recur instance to the
     * specified date list. If no BYMONTH rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getMonthVariants(final DateList dates) {
        if (getMonthList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList monthlyDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getMonthList().iterator(); j.hasNext();) {
                Integer month = (Integer) j.next();
                // Java months are zero-based..
                cal.set(Calendar.MONTH, month.intValue() - 1);
                if (Value.DATE_TIME.equals(monthlyDates.getType())) {
                    monthlyDates.add(new DateTime(cal.getTime()));
                }
                else {
                    monthlyDates.add(new Date(cal.getTime()));
                }
            }
        }
        return monthlyDates;
    }

    /**
     * Applies BYWEEKNO rules specified in this Recur instance to the
     * specified date list. If no BYWEEKNO rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getWeekNoVariants(final DateList dates) {
        if (getWeekNoList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList weekNoDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getWeekNoList().iterator(); j.hasNext();) {
                Integer weekNo = (Integer) j.next();
                cal.set(Calendar.WEEK_OF_YEAR, Dates.getAbsWeekNo(cal.getTime(), weekNo.intValue()));
                if (Value.DATE_TIME.equals(weekNoDates.getType())) {
                    weekNoDates.add(new DateTime(cal.getTime()));
                }
                else {
                    weekNoDates.add(new Date(cal.getTime()));
                }
            }
        }
        return weekNoDates;
    }

    /**
     * Applies BYYEARDAY rules specified in this Recur instance to the
     * specified date list. If no BYYEARDAY rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getYearDayVariants(final DateList dates) {
        if (getYearDayList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList yearDayDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getYearDayList().iterator(); j.hasNext();) {
                Integer yearDay = (Integer) j.next();
                cal.set(Calendar.DAY_OF_YEAR, Dates.getAbsYearDay(cal.getTime(), yearDay.intValue()));
                if (Value.DATE_TIME.equals(yearDayDates.getType())) {
                    yearDayDates.add(new DateTime(cal.getTime()));
                }
                else {
                    yearDayDates.add(new Date(cal.getTime()));
                }
            }
        }
        return yearDayDates;
    }

    /**
     * Applies BYMONTHDAY rules specified in this Recur instance to the
     * specified date list. If no BYMONTHDAY rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getMonthDayVariants(final DateList dates) {
        if (getMonthDayList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList monthDayDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getMonthDayList().iterator(); j.hasNext();) {
                Integer monthDay = (Integer) j.next();
                cal.set(Calendar.DAY_OF_MONTH, Dates.getAbsMonthDay(cal.getTime(), monthDay.intValue()));
                if (Value.DATE_TIME.equals(monthDayDates.getType())) {
                    monthDayDates.add(new DateTime(cal.getTime()));
                }
                else {
                    monthDayDates.add(new Date(cal.getTime()));
                }
            }
        }
        return monthDayDates;
    }

    /**
     * Applies BYDAY rules specified in this Recur instance to the
     * specified date list. If no BYDAY rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getDayVariants(final DateList dates) {
        if (getDayList().isEmpty()) {
            return dates;
        }
        DateList weekDayDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            for (Iterator j = getDayList().iterator(); j.hasNext();) {
                WeekDay weekDay = (WeekDay) j.next();
                weekDayDates.addAll(getAbsWeekDays(date, dates.getType(), weekDay));
            }
        }
        return weekDayDates;
    }

    /**
     * Returns a list of applicable dates corresponding to the specified
     * week day in accordance with the frequency specified by this recurrence
     * rule.
     * @param date
     * @param weekDay
     * @return
     */
    private List getAbsWeekDays(final Date date, final Value type, final WeekDay weekDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        DateList days = new DateList(type);
        int calDay = WeekDay.getCalendarDay(weekDay);
        if (calDay == -1) {
            // a matching weekday cannot be identified..
            return days;
        }
        if (DAILY.equals(getFrequency())) {
            if (cal.get(Calendar.DAY_OF_WEEK) == calDay) {
                // only one date is applicable..
                if (Value.DATE_TIME.equals(type)) {
                    days.add(new DateTime(cal.getTime()));
                }
                else {
                    days.add(new Date(cal.getTime()));
                }
            }
        }
        else if (WEEKLY.equals(getFrequency())  || !getWeekNoList().isEmpty()) {
            //int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
            // construct a list of possible week days..
//            cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
                cal.add(Calendar.DAY_OF_WEEK, 1);
            }
            int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
            while (cal.get(Calendar.WEEK_OF_YEAR) == weekNo) {
                if (Value.DATE_TIME.equals(type)) {
                    days.add(new DateTime(cal.getTime()));
                }
                else {
                    days.add(new Date(cal.getTime()));
                }
                cal.add(Calendar.DAY_OF_WEEK, Dates.DAYS_PER_WEEK);
            }
        }
        else if (MONTHLY.equals(getFrequency())  || !getMonthList().isEmpty()) {
            int month = cal.get(Calendar.MONTH);
            // construct a list of possible month days..
            cal.set(Calendar.DAY_OF_MONTH, 1);
            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            while (cal.get(Calendar.MONTH) == month) {
                if (Value.DATE_TIME.equals(type)) {
                    days.add(new DateTime(cal.getTime()));
                }
                else {
                    days.add(new Date(cal.getTime()));
                }
                cal.add(Calendar.DAY_OF_MONTH, Dates.DAYS_PER_WEEK);
            }
        }
        else if (YEARLY.equals(getFrequency())) {
            int year = cal.get(Calendar.YEAR);
            // construct a list of possible year days..
            cal.set(Calendar.DAY_OF_YEAR, 1);
            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
            while (cal.get(Calendar.YEAR) == year) {
                if (Value.DATE_TIME.equals(type)) {
                    days.add(new DateTime(cal.getTime()));
                }
                else {
                    days.add(new Date(cal.getTime()));
                }
                cal.add(Calendar.DAY_OF_YEAR, Dates.DAYS_PER_WEEK);
            }
        }
        DateList weekDays = new DateList(days.getType());
        sublist(days, weekDay.getOffset(), weekDays);
        return weekDays;
    }

    /**
     * Returns a single-element sublist containing the element of
     * <code>list</code> at <code>offset</code>. Valid offsets are from 1
     * to the size of the list. If an invalid offset is supplied, all elements
     * from <code>list</code> are added to <code>sublist</code>.
     * 
     * @param list
     * @param offset
     * @param sublist
     */
    private void sublist(final List list, final int offset, final List sublist) {
        int size = list.size();
        if (offset < 0 && offset >= -size) {
            sublist.add(list.get(size + offset));
        } else if (offset > 0 && offset <= size) {
            sublist.add(list.get(offset - 1));
        } else {
            sublist.addAll(list);
        }
    }
    
    /**
     * Applies BYHOUR rules specified in this Recur instance to the specified
     * date list. If no BYHOUR rules are specified the date list is returned
     * unmodified.
     * 
     * @param dates
     * @return
     */
    private DateList getHourVariants(final DateList dates) {
        if (getHourList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList hourlyDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getHourList().iterator(); j.hasNext();) {
                Integer hour = (Integer) j.next();
                cal.set(Calendar.HOUR_OF_DAY, hour.intValue());
                if (Value.DATE_TIME.equals(hourlyDates.getType())) {
                    hourlyDates.add(new DateTime(cal.getTime()));
                }
                else {
                    hourlyDates.add(new Date(cal.getTime()));
                }
            }
        }
        return hourlyDates;
    }
    
    /**
     * Applies BYMINUTE rules specified in this Recur instance to the
     * specified date list. If no BYMINUTE rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getMinuteVariants(final DateList dates) {
        if (getMinuteList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList minutelyDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getMinuteList().iterator(); j.hasNext();) {
                Integer minute = (Integer) j.next();
                cal.set(Calendar.MINUTE, minute.intValue());
                if (Value.DATE_TIME.equals(minutelyDates.getType())) {
                    minutelyDates.add(new DateTime(cal.getTime()));
                }
                else {
                    minutelyDates.add(new Date(cal.getTime()));
                }
            }
        }
        return minutelyDates;
    }
    
    /**
     * Applies BYSECOND rules specified in this Recur instance to the
     * specified date list. If no BYSECOND rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getSecondVariants(final DateList dates) {
        if (getSecondList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList secondlyDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getSecondList().iterator(); j.hasNext();) {
                Integer second = (Integer) j.next();
                cal.set(Calendar.SECOND, second.intValue());
                if (Value.DATE_TIME.equals(secondlyDates.getType())) {
                    secondlyDates.add(new DateTime(cal.getTime()));
                }
                else {
                    secondlyDates.add(new Date(cal.getTime()));
                }
            }
        }
        return secondlyDates;
    }
    
    /**
     * @param count The count to set.
     */
    public final void setCount(final int count) {
        this.count = count;
        this.until = null;
    }
    
    /**
     * @param frequency The frequency to set.
     */
    public final void setFrequency(final String frequency) {
        this.frequency = frequency;
    }
    
    /**
     * @param interval The interval to set.
     */
    public final void setInterval(final int interval) {
        this.interval = interval;
    }
    
    /**
     * @param until The until to set.
     */
    public final void setUntil(final Date until) {
        this.until = until;
        this.count = -1;
    }
}
