package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Dates;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Applies BYMONTH rules specified in this Recur instance to the specified date list. If no BYMONTH rules are
 * specified the date list is returned unmodified.
 */
public class ByMonthRule extends AbstractDateExpansionRule {

    private final MonthList monthList;

    private final Recur.Skip skip;

    public ByMonthRule(MonthList monthList, Frequency frequency) {
        this(monthList, frequency, Recur.Skip.OMIT);
    }

    public ByMonthRule(MonthList monthList, Frequency frequency, Recur.Skip skip) {
        this(monthList, frequency, Optional.empty(), skip);
    }

    public ByMonthRule(MonthList monthList, Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        this(monthList, frequency, weekStartDay, Recur.Skip.OMIT);
    }

    public ByMonthRule(MonthList monthList, Frequency frequency, Optional<WeekDay.Day> weekStartDay, Recur.Skip skip) {
        super(frequency, weekStartDay);
        this.monthList = monthList;
        this.skip = skip;
    }

    @Override
    public DateList transform(DateList dates) {
        if (monthList.isEmpty()) {
            return dates;
        }
        final DateList monthlyDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            if (getFrequency() == Frequency.YEARLY) {
                monthlyDates.addAll(new ExpansionFilter(monthlyDates.getType()).apply(date));
            } else {
                Optional<Date> limit = new LimitFilter().apply(date);
                if (limit.isPresent()) {
                    monthlyDates.add(limit.get());
                }
            }
        }
        return monthlyDates;
    }

    private class LimitFilter implements Function<Date, Optional<Date>> {

        @Override
        public Optional<Date> apply(Date date) {
            final Calendar cal = getCalendarInstance(date, true);
            // Java months are zero-based..
            if (monthList.contains(Month.valueOf(cal.get(Calendar.MONTH) + 1))) {
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
            final Calendar cal = getCalendarInstance(date, true);
            // construct a list of possible months..
            for (Month month : monthList) {
                if (month.isLeapMonth()) {
                    if (skip == Recur.Skip.BACKWARD) {
                        cal.roll(Calendar.MONTH, (month.getMonthOfYear() - 1) - cal.get(Calendar.MONTH));
                    } else if (skip == Recur.Skip.FORWARD) {
                        cal.roll(Calendar.MONTH, month.getMonthOfYear() - cal.get(Calendar.MONTH));
                    } else {
                        continue;
                    }
                } else {
                    // Java months are zero-based..
//                cal.set(Calendar.MONTH, month - 1);
                    cal.roll(Calendar.MONTH, (month.getMonthOfYear() - 1) - cal.get(Calendar.MONTH));
                }
                retVal.add(Dates.getInstance(getTime(date, cal), type));
            }
            return retVal;
        }
    }
}
