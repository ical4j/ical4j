package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

/**
 * Applies BYYEARDAY rules specified in this Recur instance to the specified date list. If no BYYEARDAY rules are
 * specified the date list is returned unmodified.
 */
public class ByYearDayRule extends AbstractDateExpansionRule {

    private transient Logger log = LoggerFactory.getLogger(ByYearDayRule.class);

    private final NumberList yearDayList;

    public ByYearDayRule(NumberList yearDayList) {
        this.yearDayList = yearDayList;
    }

    public ByYearDayRule(NumberList yearDayList, Optional<WeekDay.Day> weekStartDay) {
        super(weekStartDay);
        this.yearDayList = yearDayList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (yearDayList.isEmpty()) {
            return dates;
        }
        final DateList yearDayDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            final Calendar cal = getCalendarInstance(date, false);
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
                yearDayDates.add(Dates.getInstance(cal.getTime(), yearDayDates.getType()));
            }
        }
        return yearDayDates;
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
