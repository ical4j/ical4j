package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.WeekDay;

import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.time.temporal.ChronoField.MONTH_OF_YEAR;

/**
 * Applies BYMONTH rules specified in this Recur instance to the specified date list. If no BYMONTH rules are
 * specified the date list is returned unmodified.
 */
public class ByMonthRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private final NumberList monthList;

    public ByMonthRule(NumberList monthList, Frequency frequency) {
        super(frequency);
        this.monthList = monthList;
    }

    public ByMonthRule(NumberList monthList, Frequency frequency, WeekDay.Day weekStartDay) {
        super(frequency, weekStartDay);
        this.monthList = monthList;
    }

    @Override
    public List<T> transform(List<T> dates) {
        if (monthList.isEmpty()) {
            return dates;
        }
        final List<T> monthlyDates = new ArrayList<>();
        for (final T date : dates) {
            if (getFrequency() == Frequency.YEARLY) {
                monthlyDates.addAll(new ExpansionFilter().apply(date));
            } else {
                Optional<T> limit = new LimitFilter().apply(date);
                if (limit.isPresent()) {
                    monthlyDates.add(limit.get());
                }
            }
        }
        return monthlyDates;
    }

    private class LimitFilter implements Function<T, Optional<T>> {

        @Override
        public Optional<T> apply(T date) {
            ZonedDateTime zonedDateTime = ZonedDateTime.from(date);
            // Java months are zero-based..
            if (monthList.contains(zonedDateTime.getMonth().getValue())) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }

    private class ExpansionFilter implements Function<T, List<T>> {

        @Override
        public List<T> apply(T date) {
            List<T> retVal = new ArrayList<>();
            // construct a list of possible months..
            monthList.forEach(month -> {
                T candidate = (T) date.with(MONTH_OF_YEAR, month);
                retVal.add(candidate);
            });
            return retVal;
        }
    }
}
