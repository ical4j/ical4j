package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Recur.Frequency;

import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static net.fortuna.ical4j.model.Recur.Frequency.*;

/**
 * Applies BYHOUR rules specified in this Recur instance to the specified date list. If no BYHOUR rules are
 * specified the date list is returned unmodified.
 */
public class ByHourRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private final NumberList hourList;

    public ByHourRule(NumberList hourList, Frequency frequency) {
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
            ZonedDateTime zonedDateTime = ZonedDateTime.from(date);
            if (hourList.contains(zonedDateTime.getHour())) {
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
            hourList.forEach(hour -> {
                T candidate = (T) date.with(HOUR_OF_DAY, hour);
                retVal.add(candidate);
            });
            return retVal;
        }
    }
}
