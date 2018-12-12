package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.util.Dates;

import java.util.Calendar;
import java.util.Optional;

/**
 * Applies BYMONTH rules specified in this Recur instance to the specified date list. If no BYMONTH rules are
 * specified the date list is returned unmodified.
 */
public class ByMonthRule extends AbstractRecurrenceRule {

    private final NumberList monthList;

    private final int interval;

    public ByMonthRule(NumberList monthList, Optional<Integer> interval) {
        this(monthList, interval, Optional.empty());
    }

    public ByMonthRule(NumberList monthList, Optional<Integer> interval, Optional<WeekDay.Day> weekStartDay) {
        super(weekStartDay);
        this.monthList = monthList;
        this.interval = interval.orElse(1);
    }

    @Override
    public DateList transform(DateList dates) {
        if (monthList.isEmpty()) {
            return dates;
        }
        final DateList monthlyDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            final Calendar cal = getCalendarInstance(date, true);
            final Calendar freqEnd = getCalendarInstance(date, true);
            freqEnd.add(Calendar.MONTH, interval);
            for (final Integer month : monthList) {
                // Java months are zero-based..
//                cal.set(Calendar.MONTH, month.intValue() - 1);
                cal.roll(Calendar.MONTH, (month - 1) - cal.get(Calendar.MONTH));
                if (cal.after(freqEnd)) {
                    break; // Do not break out of the FREQ-defined boundary
                }
                monthlyDates.add(Dates.getInstance(cal.getTime(), monthlyDates.getType()));
            }
        }
        return monthlyDates;
    }
}
