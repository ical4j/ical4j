package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.transform.Transformer;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.util.List;

import static java.time.temporal.ChronoField.*;

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
abstract class AbstractDateExpansionRule<T extends Temporal> implements Transformer<List<T>>, Serializable {

    private final Frequency frequency;

    AbstractDateExpansionRule(Frequency frequency) {
        this.frequency = frequency;
    }

    Frequency getFrequency() {
        return frequency;
    }

    int getSecond(T date) {
        return getTemporalField(date, SECOND_OF_MINUTE);
    }

    int getMinute(T date) {
        return getTemporalField(date, MINUTE_OF_HOUR);
    }

    int getHour(T date) {
        return getTemporalField(date, HOUR_OF_DAY);
    }

    DayOfWeek getDayOfWeek(T date) {
        return DayOfWeek.of(getTemporalField(date, DAY_OF_WEEK));
    }

    int getDayOfMonth(T date) {
        return getTemporalField(date, DAY_OF_MONTH);
    }

    int getDayOfYear(T date) {
        return getTemporalField(date, DAY_OF_YEAR);
    }

    int getMonth(T date) {
        return getTemporalField(date, MONTH_OF_YEAR);
    }

    int getYear(T date) {
        return getTemporalField(date, YEAR);
    }

    private int getTemporalField(T date, TemporalField field) {
        if (date.isSupported(field)) {
            return date.get(field);
        } else {
            return ZonedDateTime.from(date).get(field);
        }
    }

    @SuppressWarnings("unchecked")
    T withTemporalField(T date, TemporalField field, int value) {
        if (date.isSupported(field)) {
            return (T) date.with(field, value);
        }
        throw new IllegalArgumentException("Invalid temporal type for this rule");
    }
}
