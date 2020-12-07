package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Recur.Frequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Year;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static net.fortuna.ical4j.model.Recur.Frequency.YEARLY;

/**
 * Applies BYYEARDAY rules specified in this Recur instance to the specified date list. If no BYYEARDAY rules are
 * specified the date list is returned unmodified.
 */
public class ByYearDayRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private transient Logger log = LoggerFactory.getLogger(ByYearDayRule.class);

    private final List<Integer> yearDayList;

    public ByYearDayRule(List<Integer> yearDayList, Frequency frequency) {
        super(frequency);
        this.yearDayList = yearDayList;
    }

    @Override
    public List<T> transform(List<T> dates) {
        if (yearDayList.isEmpty()) {
            return dates;
        }
        final List<T> yearDayDates = new ArrayList<>();
        for (final T date : dates) {
            if (getFrequency() == YEARLY) {
                yearDayDates.addAll(new ExpansionFilter().apply(date));
            } else {
                Optional<T> limit = new LimitFilter().apply(date);
                limit.ifPresent(yearDayDates::add);
            }
        }
        return yearDayDates;
    }

    private class LimitFilter implements Function<T, Optional<T>> {
        @Override
        public Optional<T> apply(T date) {
            if (yearDayList.contains(getDayOfYear(date))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }

    private class ExpansionFilter implements Function<T, List<T>> {

        @Override
        public List<T> apply(T date) {
            List<T> retVal = new ArrayList<>();
            // construct a list of possible year days..
            final int numDaysInYear = Year.of(getYear(date)).length();
            for (final int yearDay : yearDayList) {
                if (yearDay == 0 || yearDay < -numDaysInYear || yearDay > numDaysInYear) {
                    if (log.isTraceEnabled()) {
                        log.trace("Invalid day of year: " + yearDay);
                    }
                    continue;
                }
                T candidate;
                if (yearDay > 0) {
                    if (numDaysInYear < yearDay) {
                        continue;
                    }
                    candidate = withTemporalField(date, DAY_OF_YEAR, yearDay);
                } else {
                    if (numDaysInYear < -yearDay) {
                        continue;
                    }
                    candidate = withTemporalField(date, DAY_OF_YEAR, numDaysInYear + 1 + yearDay);
                }
                retVal.add(candidate);
            }
            return retVal;
        }
    }

    /**
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        log = LoggerFactory.getLogger(ByYearDayRule.class);
    }
}
