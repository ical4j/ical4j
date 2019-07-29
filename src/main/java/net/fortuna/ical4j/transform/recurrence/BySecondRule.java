package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Dates;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Applies BYSECOND rules specified in this Recur instance to the specified date list. If no BYSECOND rules are
 * specified the date list is returned unmodified.
 */
public class BySecondRule extends AbstractDateExpansionRule {

    private final NumberList secondList;

    public BySecondRule(NumberList secondList, Frequency frequency) {
        super(frequency);
        this.secondList = secondList;
    }

    public BySecondRule(NumberList secondList, Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.secondList = secondList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (secondList.isEmpty()) {
            return dates;
        }
        final DateList secondlyDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            if (getFrequency() == Frequency.SECONDLY) {
                Optional<Date> limit = new LimitFilter().apply(date);
                if (limit.isPresent()) {
                    secondlyDates.add(limit.get());
                }
            } else {
                secondlyDates.addAll(new ExpansionFilter(secondlyDates.getType()).apply(date));
            }
        }
        return secondlyDates;
    }

    private class LimitFilter implements Function<Date, Optional<Date>> {

        @Override
        public Optional<Date> apply(Date date) {
            final Calendar cal = getCalendarInstance(date, true);
            if (secondList.contains(cal.get(Calendar.SECOND))) {
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
            // construct a list of possible seconds..
            secondList.forEach(second -> {
                cal.set(Calendar.SECOND, second);
                retVal.add(Dates.getInstance(getTime(date, cal), type));
            });
            return retVal;
        }
    }
}
