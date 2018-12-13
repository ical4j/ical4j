package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

/**
 * Applies BYMONTHDAY rules specified in this Recur instance to the specified date list. If no BYMONTHDAY rules are
 * specified the date list is returned unmodified.
 */
public class ByMonthDayRule extends AbstractRecurrenceRule {

    private transient Logger log = LoggerFactory.getLogger(ByMonthDayRule.class);

    private final NumberList monthDayList;

    public ByMonthDayRule(NumberList monthDayList) {
        this.monthDayList = monthDayList;
    }

    public ByMonthDayRule(NumberList monthDayList, Optional<WeekDay.Day> weekStartDay) {
        super(weekStartDay);
        this.monthDayList = monthDayList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (monthDayList.isEmpty()) {
            return dates;
        }
        final DateList monthDayDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            final Calendar cal = getCalendarInstance(date, false);
            for (final int monthDay : monthDayList) {
                if (monthDay == 0 || monthDay < -Dates.MAX_DAYS_PER_MONTH || monthDay > Dates.MAX_DAYS_PER_MONTH) {
                    if (log.isTraceEnabled()) {
                        log.trace("Invalid day of month: " + monthDay);
                    }
                    continue;
                }
                final int numDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (monthDay > 0) {
                    if (numDaysInMonth < monthDay) {
                        continue;
                    }
                    cal.set(Calendar.DAY_OF_MONTH, monthDay);
                } else {
                    if (numDaysInMonth < -monthDay) {
                        continue;
                    }
                    cal.set(Calendar.DAY_OF_MONTH, numDaysInMonth);
                    cal.add(Calendar.DAY_OF_MONTH, monthDay + 1);
                }
                monthDayDates.add(Dates.getInstance(cal.getTime(), monthDayDates.getType()));
            }
        }
        return monthDayDates;
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
