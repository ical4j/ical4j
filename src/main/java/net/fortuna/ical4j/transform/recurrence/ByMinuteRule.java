package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.util.Dates;

import java.util.Calendar;
import java.util.Optional;

/**
 * Applies BYMINUTE rules specified in this Recur instance to the specified date list. If no BYMINUTE rules are
 * specified the date list is returned unmodified.
 */
public class ByMinuteRule extends AbstractRecurrenceRule {

    private final NumberList minuteList;

    public ByMinuteRule(NumberList minuteList) {
        this.minuteList = minuteList;
    }

    public ByMinuteRule(NumberList minuteList, Optional<WeekDay.Day> weekStartDay) {
        super(weekStartDay);
        this.minuteList = minuteList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (minuteList.isEmpty()) {
            return dates;
        }
        final DateList minutelyDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            final Calendar cal = getCalendarInstance(date, true);
            for (final Integer minute : minuteList) {
                cal.set(Calendar.MINUTE, minute);
                minutelyDates.add(Dates.getInstance(cal.getTime(), minutelyDates.getType()));
            }
        }
        return minutelyDates;
    }
}
