package net.fortuna.ical4j.transform.recurrence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.temporal.Temporal;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Applies BYWEEKNO rules specified in this Recur instance to the specified date list. If no BYWEEKNO rules are
 * specified the date list is returned unmodified.
 */
public class ByWeekNoRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private transient Logger log = LoggerFactory.getLogger(ByWeekNoRule.class);

    private final List<Integer> weekNoList;

    private final WeekFields weekFields;

    public ByWeekNoRule(List<Integer> weekNoList, Frequency frequency) {
        this(weekNoList, frequency, null);
    }
    public ByWeekNoRule(List<Integer> weekNoList, Frequency frequency, DayOfWeek firstDayOfWeek) {
        super(frequency);
        this.weekNoList = weekNoList;
        if (firstDayOfWeek != null) {
            // RFC5545 3.3.10
            // Week number one of the calendar year
            // is the first week that contains at least four (4) days in that
            // calendar year.
            weekFields = WeekFields.of(firstDayOfWeek, 4);
        } else {
            // RFC5545 3.3.10
            // This (WKST) is also significant when
            // in a YEARLY "RRULE" when a BYWEEKNO rule part is specified.  The
            // default value is MO.
            weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);
        }
    }

    @Override
    public List<T> apply(List<T> dates) {
        if (weekNoList.isEmpty()) {
            return dates;
        }
        final List<T> weekNoDates = new ArrayList<>();
        for (final T date : dates) {
            final int numWeeksInYear = (int) weekFields.weekOfWeekBasedYear().rangeRefinedBy(date).getMaximum();
            for (final int weekNo : weekNoList) {
                if (weekNo == 0 || weekNo < -numWeeksInYear || weekNo > numWeeksInYear) {
                    if (log.isTraceEnabled()) {
                        log.trace("Invalid week of year: " + weekNo);
                    }
                    continue;
                }
                T candidate;
                if (weekNo > 0) {
//                    if (numWeeksInYear < weekNo) {
//                        continue;
//                    }
                    candidate = withTemporalField(date, weekFields.weekOfWeekBasedYear(), weekNo);
                } else {
                    if (numWeeksInYear < -weekNo) {
                        continue;
                    }
                    candidate = withTemporalField(date, weekFields.weekOfWeekBasedYear(), numWeeksInYear + 1 + weekNo);
                }
                weekNoDates.add(candidate);
            }
        }
        return weekNoDates;
    }

    /**
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        log = LoggerFactory.getLogger(ByWeekNoRule.class);
    }
}
