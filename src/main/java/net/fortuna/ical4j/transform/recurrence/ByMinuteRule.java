package net.fortuna.ical4j.transform.recurrence;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static net.fortuna.ical4j.transform.recurrence.Frequency.*;

/**
 * Applies BYMINUTE rules specified in this Recur instance to the specified date list. If no BYMINUTE rules are
 * specified the date list is returned unmodified.
 */
public class ByMinuteRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private final List<Integer> minuteList;

    public ByMinuteRule(List<Integer> minuteList, Frequency frequency) {
        super(frequency);
        this.minuteList = minuteList;
    }

    @Override
    public List<T> apply(List<T> dates) {
        if (minuteList.isEmpty()) {
            return dates;
        }
        final List<T> minutelyDates = new ArrayList<>();
        for (final T date : dates) {
            if (EnumSet.of(HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY).contains(getFrequency())) {
                minutelyDates.addAll(new ExpansionFilter().apply(date));
            } else {
                Optional<T> limit = new LimitFilter().apply(date);
                limit.ifPresent(minutelyDates::add);
            }
        }
        return minutelyDates;
    }

    private class LimitFilter implements Function<T, Optional<T>> {
        @Override
        public Optional<T> apply(T date) {
            if (minuteList.contains(getMinute(date))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }

    private class ExpansionFilter implements Function<T, List<T>> {
        @Override
        public List<T> apply(T date) {
            List<T> retVal = new ArrayList<>();
            // construct a list of possible minutes..
            minuteList.forEach(minute -> {
                T candidate = withTemporalField(date, MINUTE_OF_HOUR, minute);
                retVal.add(candidate);
            });
            return retVal;
        }
    }
}
