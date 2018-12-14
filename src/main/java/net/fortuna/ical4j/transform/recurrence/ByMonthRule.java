package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Dates;

import java.util.*;
import java.util.function.Function;

/**
 * Applies BYMONTH rules specified in this Recur instance to the specified date list. If no BYMONTH rules are
 * specified the date list is returned unmodified.
 */
public class ByMonthRule extends AbstractDateExpansionRule {

    private final NumberList monthList;

    private final Frequency frequency;

    public ByMonthRule(NumberList monthList, Frequency frequency) {
        this(monthList, frequency, Optional.empty());
    }

    public ByMonthRule(NumberList monthList, Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(weekStartDay);
        this.monthList = monthList;
        this.frequency = frequency;
    }

    @Override
    public DateList transform(DateList dates) {
        if (monthList.isEmpty()) {
            return dates;
        }
        final DateList monthlyDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            if (frequency == Frequency.YEARLY) {
                monthlyDates.addAll(new ExpansionFilter(monthlyDates.getType()).apply(date));
            } else {
                monthlyDates.addAll(new LimitFilter(monthlyDates.getType()).apply(date));
            }
        }
        return monthlyDates;
    }

    private class LimitFilter implements Function<Date, List<Date>> {

        private final Value type;

        public LimitFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            final Calendar cal = getCalendarInstance(date, true);
            // Java months are zero-based..
            if (monthList.contains(cal.get(Calendar.MONTH) + 1)) {
                return Arrays.asList(date);
            }
            return Collections.emptyList();
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
            final Calendar cal = getCalendarInstance(date, true);
            // construct a list of possible months..
            monthList.forEach(month -> {
                // Java months are zero-based..
//                cal.set(Calendar.MONTH, month - 1);
                cal.roll(Calendar.MONTH, (month - 1) - cal.get(Calendar.MONTH));
                retVal.add(Dates.getInstance(cal.getTime(), type));
            });
            return retVal;
        }
    }
}
