package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.util.Dates;

import java.util.Calendar;
import java.util.Optional;

/**
 * Applies BYSECOND rules specified in this Recur instance to the specified date list. If no BYSECOND rules are
 * specified the date list is returned unmodified.
 */
public class BySecondRule extends AbstractRecurrenceRule {

    private final NumberList secondList;

    public BySecondRule(NumberList secondList) {
        this.secondList = secondList;
    }

    public BySecondRule(NumberList secondList, Optional<WeekDay.Day> weekStartDay) {
        super(weekStartDay);
        this.secondList = secondList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (secondList.isEmpty()) {
            return dates;
        }
        final DateList secondlyDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            final Calendar cal = getCalendarInstance(date, true);
            for (final Integer second : secondList) {
                cal.set(Calendar.SECOND, second);
                secondlyDates.add(Dates.getInstance(cal.getTime(), secondlyDates.getType()));
            }
        }
        return secondlyDates;
    }
}
