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
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.Dates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$ [18-Apr-2004]
 *
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

    /**
     * Second frequency resolution.
     */
    public static final String SECONDLY = "SECONDLY";

    /**
     * Minute frequency resolution.
     */
    public static final String MINUTELY = "MINUTELY";

    /**
     * Hour frequency resolution.
     */
    public static final String HOURLY = "HOURLY";

    /**
     * Day frequency resolution.
     */
    public static final String DAILY = "DAILY";

    /**
     * Week frequency resolution.
     */
    public static final String WEEKLY = "WEEKLY";

    /**
     * Month frequency resolution.
     */
    public static final String MONTHLY = "MONTHLY";

    /**
     * Year frequency resolution.
     */
    public static final String YEARLY = "YEARLY";

    /**
     * When calculating dates matching this recur ({@code getDates()} or {@code getNextDate}),
     *  this property defines the maximum number of attempt to find a matching date by
     * incrementing the seed.
     * <p>The default value is 1000. A value of -1 corresponds to no maximum.</p>
     */
    public static final String KEY_MAX_INCREMENT_COUNT = "net.fortuna.ical4j.recur.maxincrementcount";

    private static int maxIncrementCount;
    static {
        final String value = Configurator.getProperty(KEY_MAX_INCREMENT_COUNT);
        if (value != null && value.length() > 0) {
            maxIncrementCount = Integer.parseInt(value);
        } else {
            maxIncrementCount = 1000;
        }
    }

    private transient Log log = LogFactory.getLog(Recur.class);

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

    // Calendar field we increment based on frequency.
    private int calIncField;

    /**
     * Default constructor.
     */
    public Recur() {
    }
    
    /**
     * Constructs a new instance from the specified string value.
     * @param aValue a string representation of a recurrence.
     * @throws ParseException thrown when the specified string contains an invalid representation of an UNTIL date value
     */
    public Recur(final String aValue) throws ParseException {
        final StringTokenizer t = new StringTokenizer(aValue, ";=");
        while (t.hasMoreTokens()) {
            final String token = t.nextToken();
            if (FREQ.equals(token)) {
                frequency = nextToken(t, token);
            }
            else if (UNTIL.equals(token)) {
                final String untilString = nextToken(t, token);
                if (untilString != null && untilString.indexOf("T") >= 0) {
                    until = new DateTime(untilString);
                    // UNTIL must be specified in UTC time..
                    ((DateTime) until).setUtc(true);
                }
                else {
                    until = new Date(untilString);
                }
            }
            else if (COUNT.equals(token)) {
                count = Integer.parseInt(nextToken(t, token));
            }
            else if (INTERVAL.equals(token)) {
                interval = Integer.parseInt(nextToken(t, token));
            }
            else if (BYSECOND.equals(token)) {
                secondList = new NumberList(nextToken(t, token), 0, 59, false);
            }
            else if (BYMINUTE.equals(token)) {
                minuteList = new NumberList(nextToken(t, token), 0, 59, false);
            }
            else if (BYHOUR.equals(token)) {
                hourList = new NumberList(nextToken(t, token), 0, 23, false);
            }
            else if (BYDAY.equals(token)) {
                dayList = new WeekDayList(nextToken(t, token));
            }
            else if (BYMONTHDAY.equals(token)) {
                monthDayList = new NumberList(nextToken(t, token), 1, 31, true);
            }
            else if (BYYEARDAY.equals(token)) {
                yearDayList = new NumberList(nextToken(t, token), 1, 366, true);
            }
            else if (BYWEEKNO.equals(token)) {
                weekNoList = new NumberList(nextToken(t, token), 1, 53, true);
            }
            else if (BYMONTH.equals(token)) {
                monthList = new NumberList(nextToken(t, token), 1, 12, false);
            }
            else if (BYSETPOS.equals(token)) {
                setPosList = new NumberList(nextToken(t, token), 1, 366, true);
            }
            else if (WKST.equals(token)) {
                weekStartDay = nextToken(t, token);
            }
            // assume experimental value..
            else {
                experimentalValues.put(token, nextToken(t, token));
            }
        }
        validateFrequency();
    }

    private String nextToken(StringTokenizer t, String lastToken) {
        try {
            return t.nextToken();
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Missing expected token, last token: " + lastToken);
        }
    }
    
    /**
     * @param frequency a recurrence frequency string
     * @param until maximum recurrence date
     */
    public Recur(final String frequency, final Date until) {
        this.frequency = frequency;
        this.until = until;
        validateFrequency();
    }

    /**
     * @param frequency a recurrence frequency string
     * @param count maximum recurrence count
     */
    public Recur(final String frequency, final int count) {
        this.frequency = frequency;
        this.count = count;
        validateFrequency();
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
            hourList = new NumberList(0, 23, false);
        }
        return hourList;
    }

    /**
     * @return Returns the minuteList.
     */
    public final NumberList getMinuteList() {
        if (minuteList == null) {
            minuteList = new NumberList(0, 59, false);
        }
        return minuteList;
    }

    /**
     * @return Returns the monthDayList.
     */
    public final NumberList getMonthDayList() {
        if (monthDayList == null) {
            monthDayList = new NumberList(1, 31, true);
        }
        return monthDayList;
    }

    /**
     * @return Returns the monthList.
     */
    public final NumberList getMonthList() {
        if (monthList == null) {
            monthList = new NumberList(1, 12, false);
        }
        return monthList;
    }

    /**
     * @return Returns the secondList.
     */
    public final NumberList getSecondList() {
        if (secondList == null) {
            secondList = new NumberList(0, 59, false);
        }
        return secondList;
    }

    /**
     * @return Returns the setPosList.
     */
    public final NumberList getSetPosList() {
        if (setPosList == null) {
            setPosList = new NumberList(1, 366, true);
        }
        return setPosList;
    }

    /**
     * @return Returns the weekNoList.
     */
    public final NumberList getWeekNoList() {
        if (weekNoList == null) {
            weekNoList = new NumberList(1, 53, true);
        }
        return weekNoList;
    }

    /**
     * @return Returns the yearDayList.
     */
    public final NumberList getYearDayList() {
        if (yearDayList == null) {
            yearDayList = new NumberList(1, 366, true);
        }
        return yearDayList;
    }

    /**
     * @return Returns the count or -1 if the rule does not have a count.
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
     * @return Returns the interval or -1 if the rule does not have an interval defined.
     */
    public final int getInterval() {
        return interval;
    }

    /**
     * @return Returns the until or null if there is none.
     */
    public final Date getUntil() {
        return until;
    }

    /**
     * @return Returns the weekStartDay or null if there is none.
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
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        b.append(FREQ);
        b.append('=');
        b.append(frequency);
        if (weekStartDay != null) {
            b.append(';');
            b.append(WKST);
            b.append('=');
            b.append(weekStartDay);
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
        if (interval >= 1) {
            b.append(';');
            b.append(INTERVAL);
            b.append('=');
            b.append(interval);
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
     * Returns a list of start dates in the specified period represented by this recur. Any date fields not specified by
     * this recur are retained from the period start, and as such you should ensure the period start is initialised
     * correctly.
     * @param periodStart the start of the period
     * @param periodEnd the end of the period
     * @param value the type of dates to generate (i.e. date/date-time)
     * @return a list of dates
     */
    public final DateList getDates(final Date periodStart,
            final Date periodEnd, final Value value) {
        return getDates(periodStart, periodStart, periodEnd, value, -1);
    }

    /**
     * Convenience method for retrieving recurrences in a specified period.
     * @param seed a seed date for generating recurrence instances
     * @param period the period of returned recurrence dates
     * @param value type of dates to generate
     * @return a list of dates
     */
    public final DateList getDates(final Date seed, final Period period,
            final Value value) {
        return getDates(seed, period.getStart(), period.getEnd(), value, -1);
    }

    /**
     * Returns a list of start dates in the specified period represented by this recur. This method includes a base date
     * argument, which indicates the start of the fist occurrence of this recurrence. The base date is used to inject
     * default values to return a set of dates in the correct format. For example, if the search start date (start) is
     * Wed, Mar 23, 12:19PM, but the recurrence is Mon - Fri, 9:00AM - 5:00PM, the start dates returned should all be at
     * 9:00AM, and not 12:19PM.
     * @return a list of dates represented by this recur instance
     * @param seed the start date of this Recurrence's first instance
     * @param periodStart the start of the period
     * @param periodEnd the end of the period
     * @param value the type of dates to generate (i.e. date/date-time)
     */
    public final DateList getDates(final Date seed, final Date periodStart,
            final Date periodEnd, final Value value) {
         return getDates(seed, periodStart, periodEnd, value, -1);
    }

    /**
     * Returns a list of start dates in the specified period represented by this recur. This method includes a base date
     * argument, which indicates the start of the fist occurrence of this recurrence. The base date is used to inject
     * default values to return a set of dates in the correct format. For example, if the search start date (start) is
     * Wed, Mar 23, 12:19PM, but the recurrence is Mon - Fri, 9:00AM - 5:00PM, the start dates returned should all be at
     * 9:00AM, and not 12:19PM.
     * @return a list of dates represented by this recur instance
     * @param seed the start date of this Recurrence's first instance
     * @param periodStart the start of the period
     * @param periodEnd the end of the period
     * @param value the type of dates to generate (i.e. date/date-time)
     * @param maxCount limits the number of instances returned. Up to one years
     *       worth extra may be returned. Less than 0 means no limit
     */
    public final DateList getDates(final Date seed, final Date periodStart,
                                   final Date periodEnd, final Value value,
                                   final int maxCount) {

        final DateList dates = new DateList(value);
        if (seed instanceof DateTime) {
            if (((DateTime) seed).isUtc()) {
                dates.setUtc(true);
            }
            else {
                dates.setTimeZone(((DateTime) seed).getTimeZone());
            }
        }
        final Calendar cal = Dates.getCalendarInstance(seed);
        cal.setTime(seed);

        // optimize the start time for selecting candidates
        // (only applicable where a COUNT is not specified)
        if (getCount() < 1) {
            final Calendar seededCal = (Calendar) cal.clone();
            while (seededCal.getTime().before(periodStart)) {
                cal.setTime(seededCal.getTime());
                increment(seededCal);
            }
        }

        int invalidCandidateCount = 0;
        int noCandidateIncrementCount = 0;
        Date candidate = null;
        while ((maxCount < 0) || (dates.size() < maxCount)) {
            final Date candidateSeed = Dates.getInstance(cal.getTime(), value);

            if (getUntil() != null && candidate != null
                    && candidate.after(getUntil())) {

                break;
            }
            if (periodEnd != null && candidate != null
                    && candidate.after(periodEnd)) {

                break;
            }
            if (getCount() >= 1
                    && (dates.size() + invalidCandidateCount) >= getCount()) {

                break;
            }

//            if (Value.DATE_TIME.equals(value)) {
            if (candidateSeed instanceof DateTime) {
                if (dates.isUtc()) {
                    ((DateTime) candidateSeed).setUtc(true);
                }
                else {
                    ((DateTime) candidateSeed).setTimeZone(dates.getTimeZone());
                }
            }

            final DateList candidates = getCandidates(candidateSeed, value);
            if (!candidates.isEmpty()) {
                noCandidateIncrementCount = 0;
                // sort candidates for identifying when UNTIL date is exceeded..
                Collections.sort(candidates);
                for (final Iterator i = candidates.iterator(); i.hasNext();) {
                    candidate = (Date) i.next();
                    // don't count candidates that occur before the seed date..
                    if (!candidate.before(seed)) {
                        // candidates exclusive of periodEnd..
                        if (candidate.before(periodStart)
                                || !candidate.before(periodEnd)) {
                            invalidCandidateCount++;
                        } else if (getCount() >= 1
                                && (dates.size() + invalidCandidateCount) >= getCount()) {
                            break;
                        } else if (!(getUntil() != null
                                && candidate.after(getUntil()))) {
                            dates.add(candidate);
                        }
                    }
                }
            } else {
                noCandidateIncrementCount++;
                if ((maxIncrementCount > 0) && (noCandidateIncrementCount > maxIncrementCount)) {
                    break;
                }
            }
            increment(cal);
        }
        // sort final list..
        Collections.sort(dates);
        return dates;
    }
    
    /**
     * Returns the the next date of this recurrence given a seed date
     * and start date.  The seed date indicates the start of the fist 
     * occurrence of this recurrence. The start date is the
     * starting date to search for the next recurrence.  Return null
     * if there is no occurrence date after start date.
     * @return the next date in the recurrence series after startDate
     * @param seed the start date of this Recurrence's first instance
     * @param startDate the date to start the search
     */
    public final Date getNextDate(final Date seed, final Date startDate) {

        final Calendar cal = Dates.getCalendarInstance(seed);
        cal.setTime(seed);

        // optimize the start time for selecting candidates
        // (only applicable where a COUNT is not specified)
        if (getCount() < 1) {
            final Calendar seededCal = (Calendar) cal.clone();
            while (seededCal.getTime().before(startDate)) {
                cal.setTime(seededCal.getTime());
                increment(seededCal);
            }
        }

        int invalidCandidateCount = 0;
        int noCandidateIncrementCount = 0;
        Date candidate = null;
        final Value value = seed instanceof DateTime ? Value.DATE_TIME : Value.DATE;
        
        while (true) {
            final Date candidateSeed = Dates.getInstance(cal.getTime(), value);

            if (getUntil() != null && candidate != null && candidate.after(getUntil())) {
                break;
            }
            
            if (getCount() > 0 && invalidCandidateCount >= getCount()) {
                break;
            }

            if (Value.DATE_TIME.equals(value)) {
                if (((DateTime) seed).isUtc()) {
                    ((DateTime) candidateSeed).setUtc(true);
                }
                else {
                    ((DateTime) candidateSeed).setTimeZone(((DateTime) seed).getTimeZone());
                }
            }

            final DateList candidates = getCandidates(candidateSeed, value);
            if (!candidates.isEmpty()) {
                noCandidateIncrementCount = 0;
                // sort candidates for identifying when UNTIL date is exceeded..
                Collections.sort(candidates);

                for (final Iterator i = candidates.iterator(); i.hasNext();) {
                    candidate = (Date) i.next();
                    // don't count candidates that occur before the seed date..
                    if (!candidate.before(seed)) {
                        // Candidate must be after startDate because
                        // we want the NEXT occurrence
                        if (!candidate.after(startDate)) {
                            invalidCandidateCount++;
                        } else if (getCount() > 0
                                && invalidCandidateCount >= getCount()) {
                            break;
                        } else if (!(getUntil() != null
                                && candidate.after(getUntil()))) {
                            return candidate;
                        }
                    }
                }
            } else {
                noCandidateIncrementCount++;
                if ((maxIncrementCount > 0) && (noCandidateIncrementCount > maxIncrementCount)) {
                    break;
                }
            }
            increment(cal);
        }
        return null;
    }

    /**
     * Increments the specified calendar according to the frequency and interval specified in this recurrence rule.
     * @param cal a java.util.Calendar to increment
     */
    private void increment(final Calendar cal) {
        // initialise interval..
        final int calInterval = (getInterval() >= 1) ? getInterval() : 1;
        cal.add(calIncField, calInterval);
    }

    /**
     * Returns a list of possible dates generated from the applicable BY* rules, using the specified date as a seed.
     * @param date the seed date
     * @param value the type of date list to return
     * @return a DateList
     */
    private DateList getCandidates(final Date date, final Value value) {
        DateList dates = new DateList(value);
        if (date instanceof DateTime) {
            if (((DateTime) date).isUtc()) {
                dates.setUtc(true);
            }
            else {
                dates.setTimeZone(((DateTime) date).getTimeZone());
            }
        }
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
     * Applies BYSETPOS rules to <code>dates</code>. Valid positions are from 1 to the size of the date list. Invalid
     * positions are ignored.
     * @param dates
     */
    private DateList applySetPosRules(final DateList dates) {
        // return if no SETPOS rules specified..
        if (getSetPosList().isEmpty()) {
            return dates;
        }
        // sort the list before processing..
        Collections.sort(dates);
        final DateList setPosDates = getDateListInstance(dates);
        final int size = dates.size();
        for (final Iterator i = getSetPosList().iterator(); i.hasNext();) {
            final Integer setPos = (Integer) i.next();
            final int pos = setPos.intValue();
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
     * Applies BYMONTH rules specified in this Recur instance to the specified date list. If no BYMONTH rules are
     * specified the date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getMonthVariants(final DateList dates) {
        if (getMonthList().isEmpty()) {
            return dates;
        }
        final DateList monthlyDates = getDateListInstance(dates);
        for (final Iterator i = dates.iterator(); i.hasNext();) {
            final Date date = (Date) i.next();
            final Calendar cal = Dates.getCalendarInstance(date);
            cal.setTime(date);
            for (final Iterator j = getMonthList().iterator(); j.hasNext();) {
                final Integer month = (Integer) j.next();
                // Java months are zero-based..
//                cal.set(Calendar.MONTH, month.intValue() - 1);
                cal.roll(Calendar.MONTH, (month.intValue() - 1) - cal.get(Calendar.MONTH));
                monthlyDates.add(Dates.getInstance(cal.getTime(), monthlyDates.getType()));
            }
        }
        return monthlyDates;
    }

    /**
     * Applies BYWEEKNO rules specified in this Recur instance to the specified date list. If no BYWEEKNO rules are
     * specified the date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getWeekNoVariants(final DateList dates) {
        if (getWeekNoList().isEmpty()) {
            return dates;
        }
        final DateList weekNoDates = getDateListInstance(dates);
        for (final Iterator i = dates.iterator(); i.hasNext();) {
            final Date date = (Date) i.next();
            final Calendar cal = Dates.getCalendarInstance(date);
            cal.setTime(date);
            for (final Iterator j = getWeekNoList().iterator(); j.hasNext();) {
                final Integer weekNo = (Integer) j.next();
                cal.set(Calendar.WEEK_OF_YEAR, Dates.getAbsWeekNo(cal.getTime(), weekNo.intValue()));
                weekNoDates.add(Dates.getInstance(cal.getTime(), weekNoDates.getType()));
            }
        }
        return weekNoDates;
    }

    /**
     * Applies BYYEARDAY rules specified in this Recur instance to the specified date list. If no BYYEARDAY rules are
     * specified the date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getYearDayVariants(final DateList dates) {
        if (getYearDayList().isEmpty()) {
            return dates;
        }
        final DateList yearDayDates = getDateListInstance(dates);
        for (final Iterator i = dates.iterator(); i.hasNext();) {
            final Date date = (Date) i.next();
            final Calendar cal = Dates.getCalendarInstance(date);
            cal.setTime(date);
            for (final Iterator j = getYearDayList().iterator(); j.hasNext();) {
                final Integer yearDay = (Integer) j.next();
                cal.set(Calendar.DAY_OF_YEAR, Dates.getAbsYearDay(cal.getTime(), yearDay.intValue()));
                yearDayDates.add(Dates.getInstance(cal.getTime(), yearDayDates.getType()));
            }
        }
        return yearDayDates;
    }

    /**
     * Applies BYMONTHDAY rules specified in this Recur instance to the specified date list. If no BYMONTHDAY rules are
     * specified the date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getMonthDayVariants(final DateList dates) {
        if (getMonthDayList().isEmpty()) {
            return dates;
        }
        final DateList monthDayDates = getDateListInstance(dates);
        for (final Iterator i = dates.iterator(); i.hasNext();) {
            final Date date = (Date) i.next();
            final Calendar cal = Dates.getCalendarInstance(date);
            cal.setLenient(false);
            cal.setTime(date);
            for (final Iterator j = getMonthDayList().iterator(); j.hasNext();) {
                final Integer monthDay = (Integer) j.next();
                try {
                    cal.set(Calendar.DAY_OF_MONTH, Dates.getAbsMonthDay(cal.getTime(), monthDay.intValue()));
                    monthDayDates.add(Dates.getInstance(cal.getTime(), monthDayDates.getType()));
                }
                catch (IllegalArgumentException iae) {
                    if (log.isTraceEnabled()) {
                        log.trace("Invalid day of month: " + Dates.getAbsMonthDay(cal
                                .getTime(), monthDay.intValue()));
                    }
                }
            }
        }
        return monthDayDates;
    }

    /**
     * Applies BYDAY rules specified in this Recur instance to the specified date list. If no BYDAY rules are specified
     * the date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getDayVariants(final DateList dates) {
        if (getDayList().isEmpty()) {
            return dates;
        }
        final DateList weekDayDates = getDateListInstance(dates);
        for (final Iterator i = dates.iterator(); i.hasNext();) {
            final Date date = (Date) i.next();
            for (final Iterator j = getDayList().iterator(); j.hasNext();) {
                final WeekDay weekDay = (WeekDay) j.next();
                // if BYYEARDAY or BYMONTHDAY is specified filter existing
                // list..
                if (!getYearDayList().isEmpty() || !getMonthDayList().isEmpty()) {
                    final Calendar cal = Dates.getCalendarInstance(date);
                    cal.setTime(date);
                    if (weekDay.equals(WeekDay.getWeekDay(cal))) {
                        weekDayDates.add(date);
                    }
                }
                else {
                    weekDayDates.addAll(getAbsWeekDays(date, dates.getType(), weekDay));
                }
            }
        }
        return weekDayDates;
    }

    /**
     * Returns a list of applicable dates corresponding to the specified week day in accordance with the frequency
     * specified by this recurrence rule.
     * @param date
     * @param weekDay
     * @return
     */
    private List getAbsWeekDays(final Date date, final Value type, final WeekDay weekDay) {
        final Calendar cal = Dates.getCalendarInstance(date);
        // default week start is Monday per RFC5545
        int calendarWeekStartDay = Calendar.MONDAY;
        if (weekStartDay != null) {
        	calendarWeekStartDay = WeekDay.getCalendarDay(new WeekDay(weekStartDay));
        }
        cal.setFirstDayOfWeek(calendarWeekStartDay);
        cal.setTime(date);
        
        final DateList days = new DateList(type);
        if (date instanceof DateTime) {
            if (((DateTime) date).isUtc()) {
                days.setUtc(true);
            }
            else {
                days.setTimeZone(((DateTime) date).getTimeZone());
            }
        }
        final int calDay = WeekDay.getCalendarDay(weekDay);
        if (calDay == -1) {
            // a matching weekday cannot be identified..
            return days;
        }
        if (DAILY.equals(getFrequency())) {
            if (cal.get(Calendar.DAY_OF_WEEK) == calDay) {
                days.add(Dates.getInstance(cal.getTime(), type));
            }
        }
        else if (WEEKLY.equals(getFrequency()) || !getWeekNoList().isEmpty()) {
            final int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
            // construct a list of possible week days..
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
                cal.add(Calendar.DAY_OF_WEEK, 1);
            }
//            final int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
            if (cal.get(Calendar.WEEK_OF_YEAR) == weekNo) {
                days.add(Dates.getInstance(cal.getTime(), type));
//                cal.add(Calendar.DAY_OF_WEEK, Dates.DAYS_PER_WEEK);
            }
        }
        else if (MONTHLY.equals(getFrequency()) || !getMonthList().isEmpty()) {
            final int month = cal.get(Calendar.MONTH);
            // construct a list of possible month days..
            cal.set(Calendar.DAY_OF_MONTH, 1);
            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            while (cal.get(Calendar.MONTH) == month) {
                days.add(Dates.getInstance(cal.getTime(), type));
                cal.add(Calendar.DAY_OF_MONTH, Dates.DAYS_PER_WEEK);
            }
        }
        else if (YEARLY.equals(getFrequency())) {
            final int year = cal.get(Calendar.YEAR);
            // construct a list of possible year days..
            cal.set(Calendar.DAY_OF_YEAR, 1);
            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
            while (cal.get(Calendar.YEAR) == year) {
                days.add(Dates.getInstance(cal.getTime(), type));
                cal.add(Calendar.DAY_OF_YEAR, Dates.DAYS_PER_WEEK);
            }
        }
        return getOffsetDates(days, weekDay.getOffset());
    }

    /**
     * Returns a single-element sublist containing the element of <code>list</code> at <code>offset</code>. Valid
     * offsets are from 1 to the size of the list. If an invalid offset is supplied, all elements from <code>list</code>
     * are added to <code>sublist</code>.
     * @param list
     * @param offset
     * @param sublist
     */
    private List getOffsetDates(final DateList dates, final int offset) {
        if (offset == 0) {
            return dates;
        }
        final List offsetDates = getDateListInstance(dates);
        final int size = dates.size();
        if (offset < 0 && offset >= -size) {
            offsetDates.add(dates.get(size + offset));
        }
        else if (offset > 0 && offset <= size) {
            offsetDates.add(dates.get(offset - 1));
        }
        return offsetDates;
    }

    /**
     * Applies BYHOUR rules specified in this Recur instance to the specified date list. If no BYHOUR rules are
     * specified the date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getHourVariants(final DateList dates) {
        if (getHourList().isEmpty()) {
            return dates;
        }
        final DateList hourlyDates = getDateListInstance(dates);
        for (final Iterator i = dates.iterator(); i.hasNext();) {
            final Date date = (Date) i.next();
            final Calendar cal = Dates.getCalendarInstance(date);
            cal.setTime(date);
            for (final Iterator j = getHourList().iterator(); j.hasNext();) {
                final Integer hour = (Integer) j.next();
                cal.set(Calendar.HOUR_OF_DAY, hour.intValue());
                hourlyDates.add(Dates.getInstance(cal.getTime(), hourlyDates.getType()));
            }
        }
        return hourlyDates;
    }

    /**
     * Applies BYMINUTE rules specified in this Recur instance to the specified date list. If no BYMINUTE rules are
     * specified the date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getMinuteVariants(final DateList dates) {
        if (getMinuteList().isEmpty()) {
            return dates;
        }
        final DateList minutelyDates = getDateListInstance(dates);
        for (final Iterator i = dates.iterator(); i.hasNext();) {
            final Date date = (Date) i.next();
            final Calendar cal = Dates.getCalendarInstance(date);
            cal.setTime(date);
            for (final Iterator j = getMinuteList().iterator(); j.hasNext();) {
                final Integer minute = (Integer) j.next();
                cal.set(Calendar.MINUTE, minute.intValue());
                minutelyDates.add(Dates.getInstance(cal.getTime(), minutelyDates.getType()));
            }
        }
        return minutelyDates;
    }

    /**
     * Applies BYSECOND rules specified in this Recur instance to the specified date list. If no BYSECOND rules are
     * specified the date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getSecondVariants(final DateList dates) {
        if (getSecondList().isEmpty()) {
            return dates;
        }
        final DateList secondlyDates = getDateListInstance(dates);
        for (final Iterator i = dates.iterator(); i.hasNext();) {
            final Date date = (Date) i.next();
            final Calendar cal = Dates.getCalendarInstance(date);
            cal.setTime(date);
            for (final Iterator j = getSecondList().iterator(); j.hasNext();) {
                final Integer second = (Integer) j.next();
                cal.set(Calendar.SECOND, second.intValue());
                secondlyDates.add(Dates.getInstance(cal.getTime(), secondlyDates.getType()));
            }
        }
        return secondlyDates;
    }

    private void validateFrequency() {
        if (frequency == null) {
            throw new IllegalArgumentException(
                    "A recurrence rule MUST contain a FREQ rule part.");
        }
        if (SECONDLY.equals(getFrequency())) {
            calIncField = Calendar.SECOND;
        }
        else if (MINUTELY.equals(getFrequency())) {
            calIncField = Calendar.MINUTE;
        }
        else if (HOURLY.equals(getFrequency())) {
            calIncField = Calendar.HOUR_OF_DAY;
        }
        else if (DAILY.equals(getFrequency())) {
            calIncField = Calendar.DAY_OF_YEAR;
        }
        else if (WEEKLY.equals(getFrequency())) {
            calIncField = Calendar.WEEK_OF_YEAR;
        }
        else if (MONTHLY.equals(getFrequency())) {
            calIncField = Calendar.MONTH;
        }
        else if (YEARLY.equals(getFrequency())) {
            calIncField = Calendar.YEAR;
        }
        else {
            throw new IllegalArgumentException("Invalid FREQ rule part '"
                    + frequency + "' in recurrence rule");
        }
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
        validateFrequency();
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
    
    /**
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        log = LogFactory.getLog(Recur.class);
    }
    
    /**
     * Instantiate a new datelist with the same type, timezone and utc settings
     *  as the origList.
     * @param origList
     * @return a new empty list.
     */
    private static final DateList getDateListInstance(final DateList origList) {
        final DateList list = new DateList(origList.getType());
        if (origList.isUtc()) {
            list.setUtc(true);
        } else {
            list.setTimeZone(origList.getTimeZone());
        }
        return list;
    }

}
