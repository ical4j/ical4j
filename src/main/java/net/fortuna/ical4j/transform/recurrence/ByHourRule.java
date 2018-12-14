package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.util.Dates;

import java.util.Calendar;
import java.util.Optional;

/**
 * Applies BYHOUR rules specified in this Recur instance to the specified date list. If no BYHOUR rules are
 * specified the date list is returned unmodified.
 */
public class ByHourRule extends AbstractRecurrenceRule {

    private final NumberList hourList;

    public ByHourRule(NumberList hourList) {
        this.hourList = hourList;
    }

    public ByHourRule(NumberList hourList, Optional<WeekDay.Day> weekStartDay) {
        super(weekStartDay);
        this.hourList = hourList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (hourList.isEmpty()) {
            return dates;
        }
        final DateList hourlyDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            final Calendar cal = getCalendarInstance(date, true);
            for (final Integer hour : hourList) {
                cal.set(Calendar.HOUR_OF_DAY, hour);
                hourlyDates.add(Dates.getInstance(cal.getTime(), hourlyDates.getType()));
            }
        }
        return hourlyDates;
    }
}
