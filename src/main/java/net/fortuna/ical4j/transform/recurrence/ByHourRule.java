package net.fortuna.ical4j.transform.recurrence;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static net.fortuna.ical4j.transform.recurrence.Frequency.*;

/**
 * Applies BYHOUR rules specified in this Recur instance to the specified date list. If no BYHOUR rules are
 * specified the date list is returned unmodified.
 */
public class ByHourRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private final List<Integer> hourList;

    public ByHourRule(List<Integer> hourList, Frequency frequency) {
        super(frequency);
        this.hourList = hourList;
    }

    @Override
    public List<T> transform(List<T> dates) {
        if (hourList.isEmpty()) {
            return dates;
        }
        final List<T> hourlyDates = new ArrayList<>();
        for (final T date : dates) {
            if (EnumSet.of(DAILY, WEEKLY, MONTHLY, YEARLY).contains(getFrequency())) {
                hourlyDates.addAll(new ExpansionFilter().apply(date));
            } else {
                Optional<T> limit = new LimitFilter().apply(date);
                limit.ifPresent(hourlyDates::add);
            }
        }
        return hourlyDates;
    }

    private class LimitFilter implements Function<T, Optional<T>> {
        @Override
        public Optional<T> apply(T date) {
            if (hourList.contains(getHour(date))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }

    private class ExpansionFilter implements Function<T, List<T>> {
        @Override
        public List<T> apply(T date) {
            List<T> retVal = new ArrayList<>();
            // construct a list of possible hours..
            hourList.forEach(hour -> {
                T candidate = withTemporalField(date, HOUR_OF_DAY, hour);
                retVal.add(candidate);
            });
            return retVal;
        }
    }
}
