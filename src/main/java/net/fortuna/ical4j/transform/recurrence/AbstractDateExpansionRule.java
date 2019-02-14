package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.Dates;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Optional;

/**
 * Subclasses provide implementations to expand (or limit) a list of dates based on rule requirements as
 * specified in RFC5545.
 *
 * <pre>
 *     3.3.10.  Recurrence Rule
 *
 *       ...
 *
 *       BYxxx rule parts modify the recurrence in some manner.  BYxxx rule
 *       parts for a period of time that is the same or greater than the
 *       frequency generally reduce or limit the number of occurrences of
 *       the recurrence generated.  For example, "FREQ=DAILY;BYMONTH=1"
 *       reduces the number of recurrence instances from all days (if
 *       BYMONTH rule part is not present) to all days in January.  BYxxx
 *       rule parts for a period of time less than the frequency generally
 *       increase or expand the number of occurrences of the recurrence.
 *       For example, "FREQ=YEARLY;BYMONTH=1,2" increases the number of
 *       days within the yearly recurrence set from 1 (if BYMONTH rule part
 *       is not present) to 2.
 *
 *       If multiple BYxxx rule parts are specified, then after evaluating
 *       the specified FREQ and INTERVAL rule parts, the BYxxx rule parts
 *       are applied to the current set of evaluated occurrences in the
 *       following order: BYMONTH, BYWEEKNO, BYYEARDAY, BYMONTHDAY, BYDAY,
 *       BYHOUR, BYMINUTE, BYSECOND and BYSETPOS; then COUNT and UNTIL are
 *       evaluated.
 *
 *       The table below summarizes the dependency of BYxxx rule part
 *       expand or limit behavior on the FREQ rule part value.
 *
 *       The term "N/A" means that the corresponding BYxxx rule part MUST
 *       NOT be used with the corresponding FREQ value.
 *
 *       BYDAY has some special behavior depending on the FREQ value and
 *       this is described in separate notes below the table.
 *
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |          |SECONDLY|MINUTELY|HOURLY |DAILY  |WEEKLY|MONTHLY|YEARLY|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYMONTH   |Limit   |Limit   |Limit  |Limit  |Limit |Limit  |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYWEEKNO  |N/A     |N/A     |N/A    |N/A    |N/A   |N/A    |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYYEARDAY |Limit   |Limit   |Limit  |N/A    |N/A   |N/A    |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYMONTHDAY|Limit   |Limit   |Limit  |Limit  |N/A   |Expand |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYDAY     |Limit   |Limit   |Limit  |Limit  |Expand|Note 1 |Note 2|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYHOUR    |Limit   |Limit   |Limit  |Expand |Expand|Expand |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYMINUTE  |Limit   |Limit   |Expand |Expand |Expand|Expand |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYSECOND  |Limit   |Expand  |Expand |Expand |Expand|Expand |Expand|
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *    |BYSETPOS  |Limit   |Limit   |Limit  |Limit  |Limit |Limit  |Limit |
 *    +----------+--------+--------+-------+-------+------+-------+------+
 *
 *       Note 1:  Limit if BYMONTHDAY is present; otherwise, special expand
 *                for MONTHLY.
 *
 *       Note 2:  Limit if BYYEARDAY or BYMONTHDAY is present; otherwise,
 *                special expand for WEEKLY if BYWEEKNO present; otherwise,
 *                special expand for MONTHLY if BYMONTH present; otherwise,
 *                special expand for YEARLY.
 * </pre>
 */
public abstract class AbstractDateExpansionRule implements Transformer<DateList>, Serializable {

    private final Frequency frequency;

    private final int calendarWeekStartDay;

    public AbstractDateExpansionRule(Frequency frequency) {
        // default week start is Monday per RFC5545
        this(frequency, Optional.of(WeekDay.Day.MO));
    }

    public AbstractDateExpansionRule(Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        this.frequency = frequency;
        this.calendarWeekStartDay = WeekDay.getCalendarDay(WeekDay.getWeekDay(weekStartDay.orElse(WeekDay.Day.MO)));
    }

    protected Frequency getFrequency() {
        return frequency;
    }

    /**
     * Construct a Calendar object and sets the time.
     *
     * @param date
     * @param lenient
     * @return
     */
    protected Calendar getCalendarInstance(final Date date, final boolean lenient) {
        Calendar cal = Dates.getCalendarInstance(date);
        // A week should have at least 4 days to be considered as such per RFC5545
        cal.setMinimalDaysInFirstWeek(4);
        cal.setFirstDayOfWeek(calendarWeekStartDay);
        cal.setLenient(lenient);
        cal.setTime(date);

        return cal;
    }

    /**
     * Get a DateTime from cal.getTime() with the timezone of the given reference date.
     *
     * @param referenceDate
     * @param cal
     * @return
     */
    protected static Date getTime(final Date referenceDate, final Calendar cal) {
        final Date zonedDate = new DateTime(referenceDate);
        zonedDate.setTime(cal.getTime().getTime());
        return zonedDate;
    }
}
