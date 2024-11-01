package net.fortuna.ical4j.transform.recurrence;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

/**
 * Applies BYSECOND rules specified in this Recur instance to the specified date list. If no BYSECOND rules are
 * specified the date list is returned unmodified.
 */
public class BySecondRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private final List<Integer> secondList;

    public BySecondRule(List<Integer> secondList, Frequency frequency) {
        super(frequency);
        this.secondList = secondList;
    }

    @Override
    public List<T> apply(List<T> dates) {
        if (secondList.isEmpty()) {
            return dates;
        }
        final List<T> secondlyDates = new ArrayList<>();
        for (final T date : dates) {
            if (getFrequency() == Frequency.SECONDLY) {
                Optional<T> limit = new LimitFilter().apply(date);
                limit.ifPresent(secondlyDates::add);
            } else {
                secondlyDates.addAll(new ExpansionFilter().apply(date));
            }
        }
        return secondlyDates;
    }

    private class LimitFilter implements Function<T, Optional<T>> {
        @Override
        public Optional<T> apply(T date) {
            if (secondList.contains(getSecond(date))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }

    private class ExpansionFilter implements Function<T, List<T>> {
        @Override
        public List<T> apply(T date) {
            List<T> retVal = new ArrayList<>();
            // construct a list of possible seconds..
            secondList.forEach(second -> {
                T candidate = withTemporalField(date, SECOND_OF_MINUTE, second);
                retVal.add(candidate);
            });
            return retVal;
        }
    }
}
