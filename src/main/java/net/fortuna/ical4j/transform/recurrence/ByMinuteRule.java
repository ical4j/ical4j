package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.WeekDay;

import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static net.fortuna.ical4j.model.Recur.Frequency.*;

/**
 * Applies BYMINUTE rules specified in this Recur instance to the specified date list. If no BYMINUTE rules are
 * specified the date list is returned unmodified.
 */
public class ByMinuteRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private final NumberList minuteList;

    public ByMinuteRule(NumberList minuteList, Frequency frequency) {
        super(frequency);
        this.minuteList = minuteList;
    }

    public ByMinuteRule(NumberList minuteList, Frequency frequency, WeekDay.Day weekStartDay) {
        super(frequency, weekStartDay);
        this.minuteList = minuteList;
    }

    @Override
    public List<T> transform(List<T> dates) {
        if (minuteList.isEmpty()) {
            return dates;
        }
        final List<T> minutelyDates = new ArrayList<>();
        for (final T date : dates) {
            if (EnumSet.of(HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY).contains(getFrequency())) {
                minutelyDates.addAll(new ExpansionFilter().apply(date));
            } else {
                Optional<T> limit = new LimitFilter().apply(date);
                if (limit.isPresent()) {
                    minutelyDates.add(limit.get());
                }
            }
        }
        return minutelyDates;
    }

    private class LimitFilter implements Function<T, Optional<T>> {

        @Override
        public Optional<T> apply(T date) {
            ZonedDateTime zonedDateTime = ZonedDateTime.from(date);
            if (minuteList.contains(zonedDateTime.getMinute())) {
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
                T candidate = (T) date.with(MINUTE_OF_HOUR, minute);
                retVal.add(candidate);
            });
            return retVal;
        }
    }
}
