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

import net.fortuna.ical4j.transform.recurrence.*;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.temporal.*;
import java.util.*;

/**
 * $Id$ [18-Apr-2004]
 *
 * <pre>
 *     3.3.10.  Recurrence Rule
 *
 *    Value Name:  RECUR
 *
 *    Purpose:  This value type is used to identify properties that contain
 *       a recurrence rule specification.
 *
 *    Format Definition:  This value type is defined by the following
 *       notation:
 *
 *        recur           = recur-rule-part *( ";" recur-rule-part )
 *                        ;
 *                        ; The rule parts are not ordered in any
 *                        ; particular sequence.
 *                        ;
 *                        ; The FREQ rule part is REQUIRED,
 *                        ; but MUST NOT occur more than once.
 *                        ;
 *                        ; The UNTIL or COUNT rule parts are OPTIONAL,
 *                        ; but they MUST NOT occur in the same 'recur'.
 *                        ;
 *
 *                        ; The other rule parts are OPTIONAL,
 *                        ; but MUST NOT occur more than once.
 *
 *        recur-rule-part = ( "FREQ" "=" freq )
 *                        / ( "UNTIL" "=" enddate )
 *                        / ( "COUNT" "=" 1*DIGIT )
 *                        / ( "INTERVAL" "=" 1*DIGIT )
 *                        / ( "BYSECOND" "=" byseclist )
 *                        / ( "BYMINUTE" "=" byminlist )
 *                        / ( "BYHOUR" "=" byhrlist )
 *                        / ( "BYDAY" "=" bywdaylist )
 *                        / ( "BYMONTHDAY" "=" bymodaylist )
 *                        / ( "BYYEARDAY" "=" byyrdaylist )
 *                        / ( "BYWEEKNO" "=" bywknolist )
 *                        / ( "BYMONTH" "=" bymolist )
 *                        / ( "BYSETPOS" "=" bysplist )
 *                        / ( "WKST" "=" weekday )
 *
 *        freq        = "SECONDLY" / "MINUTELY" / "HOURLY" / "DAILY"
 *                    / "WEEKLY" / "MONTHLY" / "YEARLY"
 *
 *        enddate     = date / date-time
 *
 *        byseclist   = ( seconds *("," seconds) )
 *
 *        seconds     = 1*2DIGIT       ;0 to 60
 *
 *        byminlist   = ( minutes *("," minutes) )
 *
 *        minutes     = 1*2DIGIT       ;0 to 59
 *
 *        byhrlist    = ( hour *("," hour) )
 *
 *        hour        = 1*2DIGIT       ;0 to 23
 *
 *        bywdaylist  = ( weekdaynum *("," weekdaynum) )
 *
 *        weekdaynum  = [[plus / minus] ordwk] weekday
 *
 *        plus        = "+"
 *
 *        minus       = "-"
 *
 *        ordwk       = 1*2DIGIT       ;1 to 53
 *
 *        weekday     = "SU" / "MO" / "TU" / "WE" / "TH" / "FR" / "SA"
 *        ;Corresponding to SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY,
 *        ;FRIDAY, and SATURDAY days of the week.
 *
 *        bymodaylist = ( monthdaynum *("," monthdaynum) )
 *
 *        monthdaynum = [plus / minus] ordmoday
 *
 *        ordmoday    = 1*2DIGIT       ;1 to 31
 *
 *        byyrdaylist = ( yeardaynum *("," yeardaynum) )
 *
 *        yeardaynum  = [plus / minus] ordyrday
 *
 *        ordyrday    = 1*3DIGIT      ;1 to 366
 *
 *        bywknolist  = ( weeknum *("," weeknum) )
 *
 *        weeknum     = [plus / minus] ordwk
 *
 *        bymolist    = ( monthnum *("," monthnum) )
 *
 *        monthnum    = 1*2DIGIT       ;1 to 12
 *
 *        bysplist    = ( setposday *("," setposday) )
 *
 *        setposday   = yeardaynum
 * </pre>
 *
 * @author Ben Fortuna
 * @version 2.0
 */
public class Recur<T extends Temporal> implements Serializable {

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

    private static final int maxIncrementCount;

    static {
        maxIncrementCount = Configurator.getIntProperty(KEY_MAX_INCREMENT_COUNT).orElse(1000);
    }

    private transient Logger log = LoggerFactory.getLogger(Recur.class);

    private static final Comparator<Temporal> CANDIDATE_SORTER = new TemporalComparator();

    private Frequency frequency;

    private Skip skip;

    private TemporalAdapter<T> until;

    private RScale rscale;

    private Integer count;

    private Integer interval;

    private List<Integer> secondList = new NumberList(ChronoField.SECOND_OF_MINUTE.range(), false);

    private List<Integer> minuteList = new NumberList(ChronoField.MINUTE_OF_HOUR.range(), false);

    private List<Integer> hourList = new NumberList(ChronoField.HOUR_OF_DAY.range(), false);

    private List<WeekDay> dayList = new WeekDayList();

    private List<Integer> monthDayList = new NumberList(ChronoField.DAY_OF_MONTH.range(), true);

    private List<Integer> yearDayList = new NumberList(ChronoField.DAY_OF_YEAR.range(), true);

    private List<Integer> weekNoList = new NumberList(WeekFields.ISO.weekOfWeekBasedYear().range(), true);

    private List<Month> monthList = new MonthList(ChronoField.MONTH_OF_YEAR.range());

    private List<Integer> setPosList = new NumberList(ChronoField.DAY_OF_YEAR.range(), true);

    private WeekDay weekStartDay;

    private final Map<String, String> experimentalValues = new HashMap<String, String>();

    // Temporal field we increment based on frequency.
    private TemporalUnit calIncField;

    /**
     * Default constructor.
     */
    private Recur() {
    }

    /**
     * Constructs a new instance from the specified string value.
     *
     * @param aValue a string representation of a recurrence.
     */
    public Recur(final String aValue) {
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
                until = TemporalAdapter.parse(untilString);
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
                dayList.addAll(new WeekDayList(nextToken(tokens, token)));
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
                weekStartDay = WeekDay.getWeekDay(WeekDay.Day.valueOf(nextToken(tokens, token)));
            } else {
                if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
                    // assume experimental value..
                    experimentalValues.put(token, nextToken(tokens, token));
                } else {
                    throw new IllegalArgumentException(String.format("Invalid recurrence rule part: %s=%s",
                            token, nextToken(tokens, token)));
                }
            }
        }
        validateFrequency();
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
    public Recur(final String frequency, final T until) {
        this(Frequency.valueOf(frequency), until);
    }

    /**
     * @param frequency a recurrence frequency string
     * @param until     maximum recurrence date
     */
    public Recur(final Frequency frequency, final T until) {
        this.frequency = frequency;
        this.until = new TemporalAdapter<T>(until);
        validateFrequency();
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
        this.frequency = frequency;
        this.count = count;
        validateFrequency();
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
    public final List<WeekDay> getDayList() {
        return dayList;
    }

    /**
     * Accessor for the configured BYHOUR list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the hourList.
     */
    public final List<Integer> getHourList() {
        return hourList;
    }

    /**
     * Accessor for the configured BYMINUTE list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the minuteList.
     */
    public final List<Integer> getMinuteList() {
        return minuteList;
    }

    /**
     * Accessor for the configured BYMONTHDAY list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the monthDayList.
     */
    public final List<Integer> getMonthDayList() {
        return monthDayList;
    }

    /**
     * Accessor for the configured BYMONTH list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the monthList.
     */
    public final List<Month> getMonthList() {
        return monthList;
    }

    /**
     * Accessor for the configured BYSECOND list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the secondList.
     */
    public final List<Integer> getSecondList() {
        return secondList;
    }

    /**
     * Accessor for the configured BYSETPOS list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the setPosList.
     */
    public final List<Integer> getSetPosList() {
        return setPosList;
    }

    /**
     * Accessor for the configured BYWEEKNO list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the weekNoList.
     */
    public final List<Integer> getWeekNoList() {
        return weekNoList;
    }

    /**
     * Accessor for the configured BYYEARDAY list.
     * NOTE: Any changes to the returned list will have no effect on the recurrence rule processing.
     *
     * @return Returns the yearDayList.
     */
    public final List<Integer> getYearDayList() {
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
    public final T getUntil() {
        return until != null ? until.getTemporal() : null;
    }

    /**
     * @return Returns the weekStartDay or null if there is none.
     */
    public final WeekDay getWeekStartDay() {
        return weekStartDay;
    }

    /**
     * @param weekStartDay The weekStartDay to set.
     * @deprecated will be removed in a future version to support immutable pattern.
     */
    @Deprecated
    public final void setWeekStartDay(final WeekDay weekStartDay) {
        this.weekStartDay = weekStartDay;
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
            b.append(NumberList.toString(weekNoList));
        }
        if (!yearDayList.isEmpty()) {
            b.append(';');
            b.append(BYYEARDAY);
            b.append('=');
            b.append(NumberList.toString(yearDayList));
        }
        if (!monthDayList.isEmpty()) {
            b.append(';');
            b.append(BYMONTHDAY);
            b.append('=');
            b.append(NumberList.toString(monthDayList));
        }
        if (!dayList.isEmpty()) {
            b.append(';');
            b.append(BYDAY);
            b.append('=');
            b.append(WeekDayList.toString(dayList));
        }
        if (!hourList.isEmpty()) {
            b.append(';');
            b.append(BYHOUR);
            b.append('=');
            b.append(NumberList.toString(hourList));
        }
        if (!minuteList.isEmpty()) {
            b.append(';');
            b.append(BYMINUTE);
            b.append('=');
            b.append(NumberList.toString(minuteList));
        }
        if (!secondList.isEmpty()) {
            b.append(';');
            b.append(BYSECOND);
            b.append('=');
            b.append(NumberList.toString(secondList));
        }
        if (!setPosList.isEmpty()) {
            b.append(';');
            b.append(BYSETPOS);
            b.append('=');
            b.append(NumberList.toString(setPosList));
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
     * @return a list of dates
     */
    public final List<T> getDates(final T periodStart, final T periodEnd) {
        return getDates(periodStart, periodStart, periodEnd, -1);
    }

    /**
     * Convenience method for retrieving recurrences in a specified period.
     *
     * @param seed   a seed date for generating recurrence instances
     * @param period the period of returned recurrence dates
     * @return a list of dates
     */
    public final List<T> getDates(final T seed, final Period<T> period) {
        return getDates(seed, period.getStart(), period.getEnd(), -1);
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
     * @return a list of dates represented by this recur instance
     */
    public final List<T> getDates(final T seed, final T periodStart, final T periodEnd) {
        return getDates(seed, periodStart, periodEnd, -1);
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
     * @param maxCount    limits the number of instances returned. Up to one years
     *                    worth extra may be returned. Less than 0 means no limit
     * @return a list of dates represented by this recur instance
     */
    public final List<T> getDates(final T seed, final T periodStart, final T periodEnd, final int maxCount) {

        final List<T> dates = new ArrayList<>();

        T candidateSeed = seed;

        // optimize the start time for selecting candidates
        // (only applicable where a COUNT is not specified)
        if (count == null) {
            while (TemporalAdapter.isBefore(candidateSeed, periodStart)) {
                candidateSeed = increment(candidateSeed);
                if (candidateSeed == null) {
                    return dates;
                }
            }
        }

        HashSet<T> invalidCandidates = new HashSet<>();
        int noCandidateIncrementCount = 0;
        T candidate = null;
        while ((maxCount < 0) || (dates.size() < maxCount)) {
            if (getUntil() != null && candidate != null && TemporalAdapter.isAfter(candidate, getUntil())) {
                break;
            }
            if (periodEnd != null && candidate != null && TemporalAdapter.isAfter(candidate, periodEnd)) {
                break;
            }
            if (getCount() >= 1 && (dates.size() + invalidCandidates.size()) >= getCount()) {
                break;
            }

            // rootSeed = date used for the seed for the RRule at the
            //            start of the first period.
            // candidateSeed = date used for the start of 
            //                 the current period.
            final List<T> candidates = getCandidates(seed, candidateSeed);
            if (!candidates.isEmpty()) {
                noCandidateIncrementCount = 0;
                // sort candidates for identifying when UNTIL date is exceeded..
                if (seed instanceof LocalDate) {
                    candidates.sort(new TemporalComparator(ChronoUnit.DAYS));
                } else {
                    candidates.sort(CANDIDATE_SORTER);
                }
                for (T candidate1 : candidates) {
                    candidate = candidate1;
                    // don't count candidates that occur before the seed date..
                    if (!TemporalAdapter.isBefore(candidate, seed)) {
                        // candidates exclusive of periodEnd..
                        if (TemporalAdapter.isBefore(candidate, periodStart) || TemporalAdapter.isAfter(candidate, periodEnd)) {
                            invalidCandidates.add(candidate);
                        } else if (getCount() >= 1 && (dates.size() + invalidCandidates.size()) >= getCount()) {
                            break;
                        } else if (!TemporalAdapter.isBefore(candidate, periodStart) && !TemporalAdapter.isAfter(candidate, periodEnd)
                            && (getUntil() == null || !TemporalAdapter.isAfter(candidate, getUntil()))) {

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
            candidateSeed = increment(candidateSeed);
            if (candidateSeed == null) {
                break;
            }
        }
        // sort final list..
        if (seed instanceof LocalDate) {
            dates.sort(new TemporalComparator(ChronoUnit.DAYS));
        } else {
            dates.sort(CANDIDATE_SORTER);
        }
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
    public final T getNextDate(final T seed, final T startDate) {

        T candidateSeed = seed;

        // optimize the start time for selecting candidates
        // (only applicable where a COUNT is not specified)
        if (count == null) {
            while (TemporalAdapter.isBefore(candidateSeed, startDate)) {
                candidateSeed = increment(candidateSeed);
            }
        }

        int invalidCandidateCount = 0;
        int noCandidateIncrementCount = 0;
        T candidate = candidateSeed;

        while (true) {
            if (getUntil() != null && TemporalAdapter.isAfter(candidate, getUntil())) {
                break;
            }

            if (getCount() > 0 && invalidCandidateCount >= getCount()) {
                break;
            }

            final List<T> candidates = getCandidates(seed, candidateSeed);
            if (!candidates.isEmpty()) {
                noCandidateIncrementCount = 0;
                // sort candidates for identifying when UNTIL date is exceeded..
                if (seed instanceof LocalDate) {
                    candidates.sort(new TemporalComparator(ChronoUnit.DAYS));
                } else {
                    candidates.sort(CANDIDATE_SORTER);
                }

                for (T candidate1 : candidates) {
                    candidate = candidate1;
                    // don't count candidates that occur before the seed date..
                    if (!TemporalAdapter.isBefore(candidate, seed)) {
                        // Candidate must be after startDate because
                        // we want the NEXT occurrence
                        if (!TemporalAdapter.isAfter(candidate, startDate)) {
                            invalidCandidateCount++;
                        } else if (getCount() > 0 && invalidCandidateCount >= getCount()) {
                            break;
                        } else if (!(getUntil() != null && TemporalAdapter.isAfter(candidate, getUntil()))) {
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
            candidateSeed = increment(candidateSeed);
        }
        return null;
    }

    /**
     * Increments the specified temporal according to the frequency and interval specified in this recurrence rule.
     *
     * @param cal a {@link Temporal} value to increment
     */
    private T increment(final T cal) {
        // initialise interval..
        final int calInterval = (getInterval() >= 1) ? getInterval() : 1;
        return (T) cal.plus(calInterval, calIncField);
    }

    /**
     * Returns a list of possible dates generated from the applicable BY* rules, using the specified date as a seed.
     *
     * @param date  the seed date
     * @return a List of Temporal of the same type as the seed date
     */
    private List<T> getCandidates(final T rootSeed, final T date) {
        List<T> dates = new ArrayList<>();
        dates.add(date);
        if (!monthList.isEmpty()) {
            dates = new ByMonthRule<T>(monthList, frequency).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYMONTH processing: " + dates);
            }
        }

        if (!weekNoList.isEmpty()) {
            dates = new ByWeekNoRule<T>(weekNoList, frequency, WeekDay.getDayOfWeek(weekStartDay)).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYWEEKNO processing: " + dates);
            }
        }

        if (!yearDayList.isEmpty()) {
            dates = new ByYearDayRule<T>(yearDayList, frequency).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYYEARDAY processing: " + dates);
            }
        }

        if (!monthDayList.isEmpty()) {
            dates = new ByMonthDayRule<T>(monthDayList, frequency).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYMONTHDAY processing: " + dates);
            }
        } else if (frequency == Frequency.MONTHLY || (frequency == Frequency.YEARLY && yearDayList.isEmpty()
                && weekNoList.isEmpty() && dayList.isEmpty())) {

            List<Integer> implicitMonthDayList = new NumberList(ChronoField.DAY_OF_MONTH.range(), false);
            // where seed doesn't provide timezone rules derive using system default timezone..
            implicitMonthDayList.add(new TemporalAdapter<>(rootSeed).toLocalTime().getDayOfMonth());
            ByMonthDayRule<T> implicitRule = new ByMonthDayRule<>(implicitMonthDayList, frequency);
            dates = implicitRule.transform(dates);
        }

        if (!dayList.isEmpty()) {
            dates = new ByDayRule<T>(dayList, deriveFilterType(), WeekDay.getDayOfWeek(weekStartDay)).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYDAY processing: " + dates);
            }
        } else if (frequency == Frequency.WEEKLY || (frequency == Frequency.YEARLY && yearDayList.isEmpty()
                && !weekNoList.isEmpty() && monthDayList.isEmpty())) {

            ByDayRule<T> implicitRule = new ByDayRule<>(rootSeed, deriveFilterType(), WeekDay.getDayOfWeek(getWeekStartDay()));
            dates = implicitRule.transform(dates);
        }

        if (!hourList.isEmpty()) {
            dates = new ByHourRule<T>(hourList, frequency).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYHOUR processing: " + dates);
            }
        }

        if (!minuteList.isEmpty()) {
            dates = new ByMinuteRule<T>(minuteList, frequency).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYMINUTE processing: " + dates);
            }
        }

        if (!secondList.isEmpty()) {
            dates = new BySecondRule<T>(secondList, frequency).transform(dates);
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Dates after BYSECOND processing: " + dates);
            }
        }

        if (!setPosList.isEmpty()) {
            dates = new BySetPosRule<T>(setPosList).transform(dates);
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
            calIncField = ChronoUnit.SECONDS;
        } else if (Frequency.MINUTELY.equals(getFrequency())) {
            calIncField = ChronoUnit.MINUTES;
        } else if (Frequency.HOURLY.equals(getFrequency())) {
            calIncField = ChronoUnit.HOURS;
        } else if (Frequency.DAILY.equals(getFrequency())) {
            calIncField = ChronoUnit.DAYS;
        } else if (Frequency.WEEKLY.equals(getFrequency())) {
            calIncField = ChronoUnit.WEEKS;
        } else if (Frequency.MONTHLY.equals(getFrequency())) {
            calIncField = ChronoUnit.MONTHS;
        } else if (Frequency.YEARLY.equals(getFrequency())) {
            calIncField = ChronoUnit.YEARS;
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
    public final void setUntil(final T until) {
        this.until = new TemporalAdapter<T>(until);
        this.count = -1;
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
    public static class Builder<T extends Temporal> {

        private Frequency frequency;

        private Skip skip;

        private T until;

        private RScale rscale;

        private Integer count;

        private Integer interval;

        private List<Integer> secondList;

        private List<Integer> minuteList;

        private List<Integer> hourList;

        private List<WeekDay> dayList;

        private List<Integer> monthDayList;

        private List<Integer> yearDayList;

        private List<Integer> weekNoList;

        private List<Month> monthList;

        private List<Integer> setPosList;

        private WeekDay weekStartDay;

        public Builder<T> frequency(Frequency frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder skip(Skip skip) {
            this.skip = skip;
            return this;
        }

        public Builder<T> until(T until) {
            this.until = until;
            return this;
        }

        public Builder rscale(RScale rscale) {
            this.rscale = rscale;
            return this;
        }

        public Builder<T> count(Integer count) {
            this.count = count;
            return this;
        }

        public Builder<T> interval(Integer interval) {
            this.interval = interval;
            return this;
        }

        public Builder<T> secondList(List<Integer> secondList) {
            this.secondList = secondList;
            return this;
        }

        public Builder<T> minuteList(List<Integer> minuteList) {
            this.minuteList = minuteList;
            return this;
        }

        public Builder<T> hourList(List<Integer> hourList) {
            this.hourList = hourList;
            return this;
        }

        public Builder<T> dayList(List<WeekDay> dayList) {
            this.dayList = dayList;
            return this;
        }

        public Builder<T> monthDayList(List<Integer> monthDayList) {
            this.monthDayList = monthDayList;
            return this;
        }

        public Builder<T> yearDayList(List<Integer> yearDayList) {
            this.yearDayList = yearDayList;
            return this;
        }

        public Builder<T> weekNoList(List<Integer> weekNoList) {
            this.weekNoList = weekNoList;
            return this;
        }

        public Builder<T> monthList(List<Month> monthList) {
            this.monthList = monthList;
            return this;
        }

        public Builder<T> setPosList(List<Integer> setPosList) {
            this.setPosList = setPosList;
            return this;
        }

        public Builder<T> weekStartDay(WeekDay weekStartDay) {
            this.weekStartDay = weekStartDay;
            return this;
        }

        public Recur<T> build() {
            Recur<T> recur = new Recur<>();
            recur.frequency = frequency;
            recur.rscale = rscale;
            recur.skip = skip;
            if (until != null) {
                recur.until = new TemporalAdapter<T>(until);
            }
            recur.count = count;
            recur.interval = interval;
            if (secondList != null) {
                recur.secondList.addAll(secondList);
            }
            if (minuteList != null) {
                recur.minuteList.addAll(minuteList);
            }
            if (hourList != null) {
                recur.hourList.addAll(hourList);
            }
            if (dayList != null) {
                recur.dayList.addAll(dayList);
            }
            if (monthDayList != null) {
                recur.monthDayList.addAll(monthDayList);
            }
            if (yearDayList != null) {
                recur.yearDayList.addAll(yearDayList);
            }
            if (weekNoList != null) {
                recur.weekNoList.addAll(weekNoList);
            }
            if (monthList != null) {
                recur.monthList.addAll(monthList);
            }
            if (setPosList != null) {
                recur.setPosList.addAll(setPosList);
            }
            recur.weekStartDay = weekStartDay;
            recur.validateFrequency();
            return recur;
        }
    }
}
