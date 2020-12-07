package net.fortuna.ical4j.transform.recurrence;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.time.temporal.ChronoField.MONTH_OF_YEAR;

/**
 * Applies BYMONTH rules specified in this Recur instance to the specified date list. If no BYMONTH rules are
 * specified the date list is returned unmodified.
 *
 * Valid temporal types for this rule include: {@link java.time.ZonedDateTime}, {@link java.time.LocalDateTime}
 * and {@link java.time.LocalDate}
 */
public class ByMonthRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private final List<Integer> monthList;

    public ByMonthRule(List<Integer> monthList, Frequency frequency) {
        super(frequency);
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
                limit.ifPresent(monthlyDates::add);
            }
        }
        return monthlyDates;
    }

    private class LimitFilter implements Function<T, Optional<T>> {
        @Override
        public Optional<T> apply(T date) {
            // Java months are zero-based..
            if (monthList.contains(getMonth(date))) {
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
                T candidate = withTemporalField(date, MONTH_OF_YEAR, month);
                retVal.add(candidate);
            });
            return retVal;
        }
    }
}
