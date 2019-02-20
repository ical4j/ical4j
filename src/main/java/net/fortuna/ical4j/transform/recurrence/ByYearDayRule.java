package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.fortuna.ical4j.model.Recur.Frequency.YEARLY;

/**
 * Applies BYYEARDAY rules specified in this Recur instance to the specified date list. If no BYYEARDAY rules are
 * specified the date list is returned unmodified.
 */
public class ByYearDayRule extends AbstractDateExpansionRule {

    private transient Logger log = LoggerFactory.getLogger(ByYearDayRule.class);

    private final NumberList yearDayList;

    public ByYearDayRule(NumberList yearDayList, Frequency frequency) {
        super(frequency);
        this.yearDayList = yearDayList;
    }

    public ByYearDayRule(NumberList yearDayList, Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.yearDayList = yearDayList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (yearDayList.isEmpty()) {
            return dates;
        }
        final DateList yearDayDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            if (getFrequency() == YEARLY) {
                yearDayDates.addAll(new ExpansionFilter(yearDayDates.getType()).apply(date));
            } else {
                Optional<Date> limit = new LimitFilter().apply(date);
                if (limit.isPresent()) {
                    yearDayDates.add(limit.get());
                }
            }
        }
        return yearDayDates;
    }

    private class LimitFilter implements Function<Date, Optional<Date>> {

        @Override
        public Optional<Date> apply(Date date) {
            final Calendar cal = getCalendarInstance(date, true);
            if (yearDayList.contains(cal.get(Calendar.DAY_OF_YEAR))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }

    private class ExpansionFilter implements Function<Date, List<Date>> {

        private final Value type;

        public ExpansionFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            List<Date> retVal = new ArrayList<>();
            final Calendar cal = getCalendarInstance(date, false);
            // construct a list of possible year days..
            for (final int yearDay : yearDayList) {
                if (yearDay == 0 || yearDay < -Dates.MAX_DAYS_PER_YEAR || yearDay > Dates.MAX_DAYS_PER_YEAR) {
                    if (log.isTraceEnabled()) {
                        log.trace("Invalid day of year: " + yearDay);
                    }
                    continue;
                }
                final int numDaysInYear = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
                if (yearDay > 0) {
                    if (numDaysInYear < yearDay) {
                        continue;
                    }
                    cal.set(Calendar.DAY_OF_YEAR, yearDay);
                } else {
                    if (numDaysInYear < -yearDay) {
                        continue;
                    }
                    cal.set(Calendar.DAY_OF_YEAR, numDaysInYear);
                    cal.add(Calendar.DAY_OF_YEAR, yearDay + 1);
                }
                retVal.add(Dates.getInstance(getTime(date, cal), type));
            }
            return retVal;
        }
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
}
