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

import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.transform.recurrence.*;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.*;

/**
 * $Id$ [18-Apr-2004]
 * <p/>
 * Defines a recurrence.
 *
 * @author Ben Fortuna
 * @version 2.0
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

    private static final String RSCALE = "RSCALE";

    private static final String SKIP = "SKIP";

    public enum Frequency {
        SECONDLY, MINUTELY, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY;
    }

    public enum RScale {

        JAPANESE("Japanese"),
        BUDDHIST("ThaiBuddhist"),
        ROC("Minguo"),
        ISLAMIC("islamic"),
        ISO8601("ISO"),

        CHINESE("ISO"),
        ETHIOPIC("Ethiopic"),
        HEBREW("ISO"),
        GREGORIAN("ISO");

        private final String chronology;

        RScale(String chronology) {
            this.chronology = chronology;
        }

        public String getChronology() {
            return chronology;
        }
    }

    public enum Skip {
        OMIT, BACKWARD, FORWARD;
    }

    /**
     * Second frequency resolution.
     * @deprecated use {@link Frequency} instead.
     */
    @Deprecated
    public static final String SECONDLY = "SECONDLY";

    /**
     * Minute frequency resolution.
     * @deprecated use {@link Frequency} instead.
     */
    @Deprecated
    public static final String MINUTELY = "MINUTELY";

    /**
     * Hour frequency resolution.
     * @deprecated use {@link Frequency} instead.
     */
    @Deprecated
    public static final String HOURLY = "HOURLY";

    /**
     * Day frequency resolution.
     * @deprecated use {@link Frequency} instead.
     */
    @Deprecated
    public static final String DAILY = "DAILY";

    /**
     * Week frequency resolution.
     * @deprecated use {@link Frequency} instead.
     */
    @Deprecated
    public static final String WEEKLY = "WEEKLY";

    /**
     * Month frequency resolution.
     * @deprecated use {@link Frequency} instead.
     */
    @Deprecated
    public static final String MONTHLY = "MONTHLY";

    /**
     * Year frequency resolution.
     * @deprecated use {@link Frequency} instead.
     */
    @Deprecated
    public static final String YEARLY = "YEARLY";

    /**
     * When calculating dates matching this recur ({@code getDates()} or {@code getNextDate}),
     * this property defines the maximum number of attempt to find a matching date by
     * incrementing the seed.
     * <p>The default value is 1000. A value of -1 corresponds to no maximum.</p>
     */
    public static final String KEY_MAX_INCREMENT_COUNT = "net.fortuna.ical4j.recur.maxincrementcount";

    private static int maxIncrementCount;

    static {
        maxIncrementCount = Configurator.getIntProperty(KEY_MAX_INCREMENT_COUNT).orElse(1000);
    }

    private transient Logger log = LoggerFactory.getLogger(Recur.class);

    private Frequency frequency;

    private Skip skip;

    private Date until;

    private RScale rscale;

    private Integer count;

    private Integer interval;

    private NumberList secondList;

    private NumberList minuteList;

    private NumberList hourList;

    private WeekDayList dayList;

    private NumberList monthDayList;

    private NumberList yearDayList;

    private NumberList weekNoList;

    private MonthList monthList;

    private NumberList setPosList;

    private Map<String, Transformer<DateList>> transformers;

    private WeekDay.Day weekStartDay;

    private int calendarWeekStartDay;

    private Map<String, String> experimentalValues = new HashMap<String, String>();

    // Calendar field we increment based on frequency.
    private int calIncField;

    /**
     * Default constructor.
     */
    private Recur() {
        // default week start is Monday per RFC5545
        calendarWeekStartDay = Calendar.MONDAY;
        initTransformers();
    }

    /**
     * Constructs a new instance from the specified string value.
     *
     * @param aValue a string representation of a recurrence.
     * @throws ParseException thrown when the specified string contains an invalid representation of an UNTIL date value
     * @throws IllegalArgumentException where the recurrence string contains an unrecognised token
     */
    public Recur(final String aValue) throws ParseException {
        this(aValue, CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING));
    }

    /**
     * Constructs a new recurrence from the specified string value.
     * @param aValue a string representation of a recurrence.
     * @param experimentalTokensAllowed allow unrecognised tokens in the recurrence
     * @throws ParseException
     */
    public Recur(final String aValue, boolean experimentalTokensAllowed) throws ParseException {
        // default week start is Monday per RFC5545
        calendarWeekStartDay = Calendar.MONDAY;

        Chronology chronology = Chronology.ofLocale(Locale.getDefault());
        Iterator<String> tokens = Arrays.asList(aValue.split("[;=]")).iterator();
        while (tokens.hasNext()) {
            final String token = tokens.next();
            if (FREQ.equals(token)) {
                frequency = Frequency.valueOf(nextToken(tokens, token));
            } else if (SKIP.equals(token)) {
                skip = Skip.valueOf(nextToken(tokens, token));
            } else if (RSCALE.equals(token)) {
                rscale = RScale.valueOf(nextToken(tokens, token));
                chronology = Chronology.of(rscale.getChronology());
            } else if (UNTIL.equals(token)) {
                final String untilString = nextToken(tokens, token);
                if (untilString != null && untilString.contains("T")) {
                    until = new DateTime(untilString);
                    // UNTIL must be specified in UTC time..
                    ((DateTime) until).setUtc(true);
                } else {
                    until = new Date(untilString);
                }
            } else if (COUNT.equals(token)) {
                count = Integer.parseInt(nextToken(tokens, token));
            } else if (INTERVAL.equals(token)) {
                interval = Integer.parseInt(nextToken(tokens, token));
            } else if (BYSECOND.equals(token)) {
                secondList = new NumberList(nextToken(tokens, token), chronology.range(ChronoField.SECOND_OF_MINUTE), false);
            } else if (BYMINUTE.equals(token)) {
                minuteList = new NumberList(nextToken(tokens, token), chronology.range(ChronoField.MINUTE_OF_HOUR), false);
            } else if (BYHOUR.equals(token)) {
                hourList = new NumberList(nextToken(tokens, token), chronology.range(ChronoField.HOUR_OF_DAY), false);
            } else if (BYDAY.equals(token)) {
                dayList = new WeekDayList(nextToken(tokens, token));
            } else if (BYMONTHDAY.equals(token)) {
                monthDayList = new NumberList(nextToken(tokens, token), chronology.range(ChronoField.DAY_OF_MONTH), true);
            } else if (BYYEARDAY.equals(token)) {
                yearDayList = new NumberList(nextToken(tokens, token), chronology.range(ChronoField.DAY_OF_YEAR), true);
            } else if (BYWEEKNO.equals(token)) {
                weekNoList = new NumberList(nextToken(tokens, token), chronology.range(ChronoField.ALIGNED_WEEK_OF_YEAR), true);
            } else if (BYMONTH.equals(token)) {
                monthList = new MonthList(nextToken(tokens, token), chronology.range(ChronoField.MONTH_OF_YEAR));
            } else if (BYSETPOS.equals(token)) {
                setPosList = new NumberList(nextToken(tokens, token), chronology.range(ChronoField.DAY_OF_YEAR), true);
            } else if (WKST.equals(token)) {
                weekStartDay = WeekDay.Day.valueOf(nextToken(tokens, token));
                calendarWeekStartDay = WeekDay.getCalendarDay(WeekDay.getWeekDay(weekStartDay));
            } else {
                if (experimentalTokensAllowed) {
                    // assume experimental value..
                    experimentalValues.put(token, nextToken(tokens, token));
                } else {
                    throw new IllegalArgumentException(String.format("Invalid recurrence rule part: %s=%s",
                            token, nextToken(tokens, token)));
                }
            }
        }
        validateFrequency();
        initTransformers();
    }

    private String nextToken(Iterator<String> tokens, String lastToken) {
        try {
            return tokens.next();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Missing expected token, last token: " + lastToken);
        }
    }

    /**
     * @param frequency a recurrence frequency string
     * @param until     maximum recurrence date
     */
    @Deprecated
    public Recur(final String frequency, final Date until) {
        this(Frequency.valueOf(frequency), until);
    }

    /**
     * @param frequency a recurrence frequency string
     * @param until     maximum recurrence date
     */
    public Recur(final Frequency frequency, final Date until) {
        // default week start is Monday per RFC5545
        calendarWeekStartDay = Calendar.MONDAY;
        this.frequency = frequency;
        this.until = until;
        validateFrequency();
        initTransformers();
    }

    /**
     * @param frequency a recurrence frequency string
     * @param count     maximum recurrence count
     */
    @Deprecated
    public Recur(final String frequency, final int count) {
        this(Frequency.valueOf(frequency), count);
    }

    /**
     * @param frequency a recurrence frequency string
     * @param count     maximum recurrence count
     */
    public Recur(final Frequency frequency, final int count) {
        // default week start is Monday per RFC5545
        calendarWeekStartDay = Calendar.MONDAY;
        this.frequency = frequency;
        this.count = count;
        validateFrequency();
        initTransformers();
    }

    private void initTransformers() {
        transformers = new HashMap<>();
        Chronology chronology = rscale != null ? Chronology.of(rscale.getChronology())
                : Chronology.ofLocale(Locale.getDefault());
        if (secondList != null) {
            transformers.put(BYSECOND, new BySecondRule(secondList, frequency, Optional.ofNullable(weekStartDay)));
        } else {
            secondList = new NumberList(chronology.range(ChronoField.SECOND_OF_MINUTE), false);
        }
        if (minuteList != null) {
            transformers.put(BYMINUTE, new ByMinuteRule(minuteList, frequency, Optional.ofNullable(weekStartDay)));
        } else {
            minuteList = new NumberList(chronology.range(ChronoField.MINUTE_OF_HOUR), false);
        }
        if (hourList != null) {
            transformers.put(BYHOUR, new ByHourRule(hourList, frequency, Optional.ofNullable(weekStartDay)));
        } else {
            hourList = new NumberList(chronology.range(ChronoField.HOUR_OF_DAY), false);
        }
        if (monthDayList != null) {
            transformers.put(BYMONTHDAY, new ByMonthDayRule(monthDayList, frequency, Optional.ofNullable(weekStartDay), skip));
        } else {
            monthDayList = new NumberList(chronology.range(ChronoField.DAY_OF_MONTH), true);
        }
        if (yearDayList != null) {
            transformers.put(BYYEARDAY, new ByYearDayRule(yearDayList, frequency, Optional.ofNullable(weekStartDay)));
        } else {
            yearDayList = new NumberList(chronology.range(ChronoField.DAY_OF_YEAR), true);
        }
        if (weekNoList != null) {
            transformers.put(BYWEEKNO, new ByWeekNoRule(weekNoList, frequency, Optional.ofNullable(weekStartDay)));
        } else {
            weekNoList = new NumberList(chronology.range(ChronoField.ALIGNED_WEEK_OF_YEAR), true);
        }
        if (monthList != null) {
            transformers.put(BYMONTH, new ByMonthRule(monthList, frequency,
                    Optional.ofNullable(weekStartDay), skip));
        } else {
            monthList = new MonthList(chronology.range(ChronoField.MONTH_OF_YEAR));
        }
        if (dayList != null) {
            transformers.put(BYDAY, new ByDayRule(dayList, deriveFilterType(), Optional.ofNullable(weekStartDay)));
        } else {
            dayList = new WeekDayList();
        }
        if (setPosList != null) {
            transformers.put(BYSETPOS, new BySetPosRule(setPosList));
        } else {
            setPosList = new NumberList(chronology.range(ChronoField.DAY_OF_YEAR), true);
        }
    }

    private Frequency deriveFilterType() {
        if (frequency == Frequency.DAILY || !getYearDayList().isEmpty() || !getMonthDayList().isEmpty()) {
            return Frequency.DAILY;
        } else if (frequency == Frequency.WEEKLY || !getWeekNoList().isEmpty()) {
            return Frequency.WEEKLY;
        } else if (frequency == Frequency.MONTHLY || !getMonthList().isEmpty()) {
            return Frequency.MONTHLY;
        } else {
            return frequency;
        }
    }

    /**
     * Accessor for the configured BYDAY list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the dayList.
     */
    public final WeekDayList getDayList() {
        return dayList;
    }

    /**
     * Accessor for the configured BYHOUR list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the hourList.
     */
    public final NumberList getHourList() {
        return hourList;
    }

    /**
     * Accessor for the configured BYMINUTE list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the minuteList.
     */
    public final NumberList getMinuteList() {
        return minuteList;
    }

    /**
     * Accessor for the configured BYMONTHDAY list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the monthDayList.
     */
    public final NumberList getMonthDayList() {
        return monthDayList;
    }

    /**
     * Accessor for the configured BYMONTH list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the monthList.
     */
    public final MonthList getMonthList() {
        return monthList;
    }

    /**
     * Accessor for the configured BYSECOND list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the secondList.
     */
    public final NumberList getSecondList() {
        return secondList;
    }

    /**
     * Accessor for the configured BYSETPOS list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the setPosList.
     */
    public final NumberList getSetPosList() {
        return setPosList;
    }

    /**
     * Accessor for the configured BYWEEKNO list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the weekNoList.
     */
    public final NumberList getWeekNoList() {
        return weekNoList;
    }

    /**
     * Accessor for the configured BYYEARDAY list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the yearDayList.
     */
    public final NumberList getYearDayList() {
        return yearDayList;
    }

    /**
     * @return Returns the count or -1 if the rule does not have a count.
     */
    public final int getCount() {
        return Optional.ofNullable(count).orElse(-1);
    }

    /**
     * @return Returns the experimentalValues.
     */
    public final Map<String, String> getExperimentalValues() {
        return experimentalValues;
    }

    /**
     * @return Returns the frequency.
     */
    public final Frequency getFrequency() {
        return frequency;
    }

    /**
     *
     * @return leap month skip behaviour.
     */
    public Skip getSkip() {
        return skip;
    }

    /**
     * @return Returns the interval or -1 if the rule does not have an interval defined.
     */
    public final int getInterval() {
        return Optional.ofNullable(interval).orElse(-1);
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
    public final WeekDay.Day getWeekStartDay() {
        return weekStartDay;
    }

    /**
     * @param weekStartDay The weekStartDay to set.
     * @deprecated will be removed in a future version to support immutable pattern.
     */
    @Deprecated
    public final void setWeekStartDay(final WeekDay.Day weekStartDay) {
        this.weekStartDay = weekStartDay;
        if (weekStartDay != null) {
            calendarWeekStartDay = WeekDay.getCalendarDay(WeekDay.getWeekDay(weekStartDay));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        final StringBuilder b = new StringBuilder();
        if (rscale != null) {
            b.append(RSCALE);
            b.append('=');
            b.append(rscale);
            b.append(';');
        }
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
        if (count != null) {
            b.append(';');
            b.append(COUNT);
            b.append('=');
            b.append(count);
        }
        if (interval != null) {
            b.append(';');
            b.append(INTERVAL);
            b.append('=');
            b.append(interval);
        }
        if (!monthList.isEmpty()) {
            b.append(';');
            b.append(BYMONTH);
            b.append('=');
            b.append(monthList);
        }
        if (!weekNoList.isEmpty()) {
            b.append(';');
            b.append(BYWEEKNO);
            b.append('=');
            b.append(weekNoList);
        }
        if (!yearDayList.isEmpty()) {
            b.append(';');
            b.append(BYYEARDAY);
            b.append('=');
            b.append(yearDayList);
        }
        if (!monthDayList.isEmpty()) {
            b.append(';');
            b.append(BYMONTHDAY);
            b.append('=');
            b.append(monthDayList);
        }
        if (!dayList.isEmpty()) {
            b.append(';');
            b.append(BYDAY);
            b.append('=');
            b.append(dayList);
        }
        if (!hourList.isEmpty()) {
            b.append(';');
            b.append(BYHOUR);
            b.append('=');
            b.append(hourList);
        }
        if (!minuteList.isEmpty()) {
            b.append(';');
            b.append(BYMINUTE);
            b.append('=');
            b.append(minuteList);
        }
        if (!secondList.isEmpty()) {
            b.append(';');
            b.append(BYSECOND);
            b.append('=');
            b.append(secondList);
        }
        if (!setPosList.isEmpty()) {
            b.append(';');
            b.append(BYSETPOS);
            b.append('=');
            b.append(setPosList);
        }
        if (skip != null) {
            b.append(';');
            b.append(SKIP);
            b.append('=');
            b.append(skip);
        }
        return b.toString();
    }

    /**
     * Returns a list of start dates in the specified period represented by this recur. Any date fields not specified by
     * this recur are retained from the period start, and as such you should ensure the period start is initialised
     * correctly.
     *
     * @param periodStart the start of the period
     * @param periodEnd   the end of the period
     * @param value       the type of dates to generate (i.e. date/date-time)
     * @return a list of dates
     */
    public final DateList getDates(final Date periodStart,
                                   final Date periodEnd, final Value value) {
        return getDates(periodStart, periodStart, periodEnd, value, -1);
    }

    /**
     * Convenience method for retrieving recurrences in a specified period.
     *
     * @param seed   a seed date for generating recurrence instances
     * @param period the period of returned recurrence dates
     * @param value  type of dates to generate
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
     *
     * @param seed        the start date of this Recurrence's first instance
     * @param periodStart the start of the period
     * @param periodEnd   the end of the period
     * @param value       the type of dates to generate (i.e. date/date-time)
     * @return a list of dates represented by this recur instance
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
     *
     * @param seed        the start date of this Recurrence's first instance
     * @param periodStart the start of the period
     * @param periodEnd   the end of the period
     * @param value       the type of dates to generate (i.e. date/date-time)
     * @param maxCount    limits the number of instances returned. Up to one years
     *                    worth extra may be returned. Less than 0 means no limit
     * @return a list of dates represented by this recur instance
     */
    public final DateList getDates(final Date seed, final Date periodStart,
                                   final Date periodEnd, final Value value,
                                   final int maxCount) {

        final DateList dates = new DateList(value);
        if (seed instanceof DateTime) {
            if (((DateTime) seed).isUtc()) {
                dates.setUtc(true);
            } else {
                dates.setTimeZone(((DateTime) seed).getTimeZone());
            }
        }
        final Calendar cal = getCalendarInstance(seed, true);
        final Calendar rootSeed = (Calendar)cal.clone();
        
        // optimize the start time for selecting candidates
        // (only applicable where a COUNT is not specified)
        if (count == null) {
            final Calendar seededCal = (Calendar) cal.clone();
            while (seededCal.getTime().before(periodStart)) {
                cal.setTime(seededCal.getTime());
                increment(seededCal);
            }
        }

        HashSet<Date> invalidCandidates = new HashSet<Date>();
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
                    && (dates.size() + invalidCandidates.size()) >= getCount()) {
                break;
            }

//            if (Value.DATE_TIME.equals(value)) {
            if (candidateSeed instanceof DateTime) {
                if (dates.isUtc()) {
                    ((DateTime) candidateSeed).setUtc(true);
                } else {
                    ((DateTime) candidateSeed).setTimeZone(dates.getTimeZone());
                }
            }

            // rootSeed = date used for the seed for the RRule at the
            //            start of the first period.
            // candidateSeed = date used for the start of 
            //                 the current period.
            final DateList candidates = getCandidates(rootSeed, candidateSeed, value);
            if (!candidates.isEmpty()) {
                noCandidateIncrementCount = 0;
                // sort candidates for identifying when UNTIL date is exceeded..
                Collections.sort(candidates);
                for (Date candidate1 : candidates) {
                    candidate = candidate1;
                    // don't count candidates that occur before the seed date..
                    if (!candidate.before(seed)) {
                        // candidates exclusive of periodEnd..
                        if (candidate.before(periodStart)
                                || candidate.after(periodEnd)) {
                            invalidCandidates.add(candidate);
                        } else if (getCount() >= 1
                                && (dates.size() + invalidCandidates.size()) >= getCount()) {
                            break;
                        } else if (!candidate.before(periodStart) && !candidate.after(periodEnd)
                            && (getUntil() == null || !candidate.after(getUntil()))) {

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
     *
     * @param seed      the start date of this Recurrence's first instance
     * @param startDate the date to start the search
     * @return the next date in the recurrence series after startDate
     */
    public final Date getNextDate(final Date seed, final Date startDate) {

        final Calendar cal = getCalendarInstance(seed, true);
        final Calendar rootSeed = (Calendar)cal.clone();

        // optimize the start time for selecting candidates
        // (only applicable where a COUNT is not specified)
        if (count == null) {
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
                } else {
                    ((DateTime) candidateSeed).setTimeZone(((DateTime) seed).getTimeZone());
                }
            }

            final DateList candidates = getCandidates(rootSeed, candidateSeed, value);
            if (!candidates.isEmpty()) {
                noCandidateIncrementCount = 0;
                // sort candidates for identifying when UNTIL date is exceeded..
                Collections.sort(candidates);

                for (Date candidate1 : candidates) {
                    candidate = candidate1;
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
     *
     * @param cal a java.util.Calendar to increment
     */
    private void increment(final Calendar cal) {
        // initialise interval..
        final int calInterval = (getInterval() >= 1) ? getInterval() : 1;
        cal.add(calIncField, calInterval);
    }

    /**
     * Returns a list of possible dates generated from the applicable BY* rules, using the specified date as a seed.
     *
     * @param date  the seed date
     * @param value the type of date list to return
     * @return a DateList
     */
    private DateList getCandidates(final Calendar rootSeed, final Date date, final Value value) {
        DateList dates = new DateList(value);
        if (date instanceof DateTime) {
            if (((DateTime) date).isUtc()) {
                dates.setUtc(true);
            } else {
                dates.setTimeZone(((DateTime) date).getTimeZone());
            }
        }
        dates.add(date);
        if (transformers.get(BYMONTH) != null) {
            dates = transformers.get(BYMONTH).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYMONTH processing: " + dates);
            }
        }

        if (transformers.get(BYWEEKNO) != null) {
            dates = transformers.get(BYWEEKNO).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYWEEKNO processing: " + dates);
            }
        }

        if (transformers.get(BYYEARDAY) != null) {
            dates = transformers.get(BYYEARDAY).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYYEARDAY processing: " + dates);
            }
        }

        if (transformers.get(BYMONTHDAY) != null) {
            dates = transformers.get(BYMONTHDAY).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYMONTHDAY processing: " + dates);
            }
        } else if ((frequency == Frequency.MONTHLY && dayList.isEmpty()) ||
                (frequency == Frequency.YEARLY && yearDayList.isEmpty() && weekNoList.isEmpty() && dayList.isEmpty())) {

            NumberList implicitMonthDayList = new NumberList();
            implicitMonthDayList.add(rootSeed.get(Calendar.DAY_OF_MONTH));
            ByMonthDayRule implicitRule = new ByMonthDayRule(implicitMonthDayList, frequency, Optional.ofNullable(weekStartDay), skip);
            dates = implicitRule.transform(dates);
        }

        if (transformers.get(BYDAY) != null) {
            dates = transformers.get(BYDAY).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYDAY processing: " + dates);
            }
        } else if (frequency == Frequency.WEEKLY || (frequency == Frequency.YEARLY && yearDayList.isEmpty()
                && !weekNoList.isEmpty() && monthDayList.isEmpty())) {

            ByDayRule implicitRule = new ByDayRule(new WeekDayList(WeekDay.getWeekDay(rootSeed)),
                    deriveFilterType(),  Optional.ofNullable(weekStartDay));
            dates = implicitRule.transform(dates);
        }

        if (transformers.get(BYHOUR) != null) {
            dates = transformers.get(BYHOUR).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYHOUR processing: " + dates);
            }
        }

        if (transformers.get(BYMINUTE) != null) {
            dates = transformers.get(BYMINUTE).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYMINUTE processing: " + dates);
            }
        }

        if (transformers.get(BYSECOND) != null) {
            dates = transformers.get(BYSECOND).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYSECOND processing: " + dates);
            }
        }

        if (transformers.get(BYSETPOS) != null) {
            dates = transformers.get(BYSETPOS).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after SETPOS processing: " + dates);
            }
        }
        return dates;
    }

    private void validateFrequency() {
        if (frequency == null) {
            throw new IllegalArgumentException("A recurrence rule MUST contain a FREQ rule part.");
        }
        if (Frequency.SECONDLY.equals(getFrequency())) {
            calIncField = Calendar.SECOND;
        } else if (Frequency.MINUTELY.equals(getFrequency())) {
            calIncField = Calendar.MINUTE;
        } else if (Frequency.HOURLY.equals(getFrequency())) {
            calIncField = Calendar.HOUR_OF_DAY;
        } else if (Frequency.DAILY.equals(getFrequency())) {
            calIncField = Calendar.DAY_OF_YEAR;
        } else if (Frequency.WEEKLY.equals(getFrequency())) {
            calIncField = Calendar.WEEK_OF_YEAR;
        } else if (Frequency.MONTHLY.equals(getFrequency())) {
            calIncField = Calendar.MONTH;
        } else if (Frequency.YEARLY.equals(getFrequency())) {
            calIncField = Calendar.YEAR;
        } else {
            throw new IllegalArgumentException("Invalid FREQ rule part '"
                    + frequency + "' in recurrence rule");
        }
    }

    /**
     * @param count The count to set.
     * @deprecated will be removed in a future version to support immutable pattern.
     */
    @Deprecated
    public final void setCount(final int count) {
        this.count = count;
        this.until = null;
    }

    /**
     * @param frequency The frequency to set.
     * @deprecated will be removed in a future version to support immutable pattern.
     */
    @Deprecated
    public final void setFrequency(final String frequency) {
        this.frequency = Frequency.valueOf(frequency);
        validateFrequency();
    }

    /**
     * @param interval The interval to set.
     * @deprecated will be removed in a future version to support immutable pattern.
     */
    @Deprecated
    public final void setInterval(final int interval) {
        this.interval = interval;
    }

    /**
     * @param until The until to set.
     * @deprecated will be removed in a future version to support immutable pattern.
     */
    @Deprecated
    public final void setUntil(final Date until) {
        this.until = until;
        this.count = -1;
    }

    /**
     * Construct a Calendar object and sets the time.
     *
     * @param date
     * @param lenient
     * @return
     */
    private Calendar getCalendarInstance(final Date date, final boolean lenient) {
        Calendar cal = Dates.getCalendarInstance(date);
        // A week should have at least 4 days to be considered as such per RFC5545
        cal.setMinimalDaysInFirstWeek(4);
        cal.setFirstDayOfWeek(calendarWeekStartDay);
        cal.setLenient(lenient);
        cal.setTime(date);

        return cal;
    }

    /**
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        log = LoggerFactory.getLogger(Recur.class);
    }

    /**
     * Support for building Recur instances.
     */
    public static class Builder {

        private Frequency frequency;

        private Skip skip;

        private Date until;

        private RScale rscale;

        private Integer count;

        private Integer interval;

        private NumberList secondList;

        private NumberList minuteList;

        private NumberList hourList;

        private WeekDayList dayList;

        private NumberList monthDayList;

        private NumberList yearDayList;

        private NumberList weekNoList;

        private MonthList monthList;

        private NumberList setPosList;

        private WeekDay.Day weekStartDay;

        public Builder frequency(Frequency frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder skip(Skip skip) {
            this.skip = skip;
            return this;
        }

        public Builder until(Date until) {
            this.until = until;
            return this;
        }

        public Builder rscale(RScale rscale) {
            this.rscale = rscale;
            return this;
        }

        public Builder count(Integer count) {
            this.count = count;
            return this;
        }

        public Builder interval(Integer interval) {
            this.interval = interval;
            return this;
        }

        public Builder secondList(NumberList secondList) {
            this.secondList = secondList;
            return this;
        }

        public Builder minuteList(NumberList minuteList) {
            this.minuteList = minuteList;
            return this;
        }

        public Builder hourList(NumberList hourList) {
            this.hourList = hourList;
            return this;
        }

        public Builder dayList(WeekDayList dayList) {
            this.dayList = dayList;
            return this;
        }

        public Builder monthDayList(NumberList monthDayList) {
            this.monthDayList = monthDayList;
            return this;
        }

        public Builder yearDayList(NumberList yearDayList) {
            this.yearDayList = yearDayList;
            return this;
        }

        public Builder weekNoList(NumberList weekNoList) {
            this.weekNoList = weekNoList;
            return this;
        }

        public Builder monthList(MonthList monthList) {
            this.monthList = monthList;
            return this;
        }

        public Builder setPosList(NumberList setPosList) {
            this.setPosList = setPosList;
            return this;
        }

        public Builder weekStartDay(WeekDay.Day weekStartDay) {
            this.weekStartDay = weekStartDay;
            return this;
        }

        public Recur build() {
            Recur recur = new Recur();
            recur.frequency = frequency;
            recur.rscale = rscale;
            recur.skip = skip;
            recur.until = until;
            recur.count = count;
            recur.interval = interval;
            recur.secondList = secondList;
            recur.minuteList = minuteList;
            recur.hourList = hourList;
            recur.dayList = dayList;
            recur.monthDayList = monthDayList;
            recur.yearDayList = yearDayList;
            recur.weekNoList = weekNoList;
            recur.monthList = monthList;
            recur.setPosList = setPosList;
            recur.weekStartDay = weekStartDay;
            recur.validateFrequency();
            recur.initTransformers();
            return recur;
        }
    }
}
