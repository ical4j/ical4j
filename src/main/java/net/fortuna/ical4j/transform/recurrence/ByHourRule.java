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

import static net.fortuna.ical4j.model.Recur.Frequency.*;

/**
 * Applies BYHOUR rules specified in this Recur instance to the specified date list. If no BYHOUR rules are
 * specified the date list is returned unmodified.
 */
public class ByHourRule extends AbstractDateExpansionRule {

    private final NumberList hourList;

    public ByHourRule(NumberList hourList, Frequency frequency) {
        super(frequency);
        this.hourList = hourList;
    }

    public ByHourRule(NumberList hourList, Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.hourList = hourList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (hourList.isEmpty()) {
            return dates;
        }
        final DateList hourlyDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            if (EnumSet.of(DAILY, WEEKLY, MONTHLY, YEARLY).contains(getFrequency())) {
                hourlyDates.addAll(new ExpansionFilter(hourlyDates.getType()).apply(date));
            } else {
                Optional<Date> limit = new LimitFilter().apply(date);
                if (limit.isPresent()) {
                    hourlyDates.add(limit.get());
                }
            }
        }
        return hourlyDates;
    }

    private class LimitFilter implements Function<Date, Optional<Date>> {

        @Override
        public Optional<Date> apply(Date date) {
            final Calendar cal = getCalendarInstance(date, true);
            if (hourList.contains(cal.get(Calendar.HOUR_OF_DAY))) {
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
            hourList.forEach(hour -> {
                cal.set(Calendar.HOUR_OF_DAY, hour);
                retVal.add(Dates.getInstance(getTime(date, cal), type));
            });
            return retVal;
        }
    }
}
