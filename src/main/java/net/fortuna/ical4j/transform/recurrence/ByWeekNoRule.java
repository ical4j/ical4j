package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;
import java.util.function.Function;

/**
 * Applies BYWEEKNO rules specified in this Recur instance to the specified date list. If no BYWEEKNO rules are
 * specified the date list is returned unmodified.
 */
public class ByWeekNoRule extends AbstractDateExpansionRule {

    private transient Logger log = LoggerFactory.getLogger(ByWeekNoRule.class);

    private final DateRange transformationDateRange;
    private final NumberList weekNoList;

    public ByWeekNoRule(NumberList weekNoList, Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.weekNoList = weekNoList;
        this.transformationDateRange = null;
    }

    public ByWeekNoRule(NumberList weekNoList, Frequency frequency, Optional<WeekDay.Day> weekStartDay, DateRange transformationDateRange) {
        super(frequency, weekStartDay);
        this.weekNoList = weekNoList;
        this.transformationDateRange = transformationDateRange;
    }

    @Override
    public DateList transform(DateList dates) {
        if (weekNoList.isEmpty()) {
            return dates;
        }
        final LimitFilter limitFilter = new ByWeekNoRule.LimitFilter(transformationDateRange);
        final DateList weekNoDates = Dates.getDateListInstance(dates);
        Calendar initCal = getCalendarInstance(dates.get(0), true);
        for (final Date date : dates) {
            final int numWeeksInYear = initCal.getActualMaximum(Calendar.WEEK_OF_YEAR);
            for (final Integer weekNo : weekNoList) {
                if (weekNo == 0 || weekNo < -Dates.MAX_WEEKS_PER_YEAR || weekNo > Dates.MAX_WEEKS_PER_YEAR) {
                    if (log.isTraceEnabled()) {
                        log.trace("Invalid week of year: " + weekNo);
                    }
                    continue;
                }
                final Calendar cal = getCalendarInstance(date, true);
                if (weekNo > 0) {
                    if (numWeeksInYear < weekNo) {
                        continue;
                    }
                    cal.set(Calendar.WEEK_OF_YEAR, weekNo);
                } else {
                    if (numWeeksInYear < -weekNo) {
                        continue;
                    }
                    cal.set(Calendar.WEEK_OF_YEAR, numWeeksInYear);
                    cal.add(Calendar.WEEK_OF_YEAR, weekNo + 1);
                }
                limitFilter.apply(Dates.getInstance(getTime(date, cal), weekNoDates.getType()))
                        .ifPresent(weekNoDates::add);
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
        log = LoggerFactory.getLogger(Recur.class);
    }

    private class LimitFilter implements Function<Date, Optional<Date>> {
        final private DateRange dateRange;
        public LimitFilter(DateRange dateRange) {
            this.dateRange = dateRange;
        }
        @Override
        public Optional<Date> apply(Date date) {
            if (dateRange == null || dateRange.includes(date, DateRange.INCLUSIVE_START)) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }
}
