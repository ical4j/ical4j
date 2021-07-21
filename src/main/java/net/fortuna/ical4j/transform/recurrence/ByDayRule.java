package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.WeekDayList;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.time.temporal.Temporal;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;

/**
 * Applies BYDAY rules specified in this Recur instance to the specified date list. If no BYDAY rules are specified
 * the date list is returned unmodified.
 */
public class ByDayRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private final List<WeekDay> dayList;

    private final WeekFields weekFields;

    public ByDayRule(T seed, Frequency frequency) {
        this(seed, frequency, null);
    }

    public ByDayRule(T seed, Frequency frequency, DayOfWeek firstDayOfWeek) {
        super(frequency);
        this.dayList = new WeekDayList(WeekDay.getWeekDay(getDayOfWeek(seed)));
        if (firstDayOfWeek != null) {
            weekFields = WeekFields.of(firstDayOfWeek, 1);
        } else {
            weekFields = WeekFields.of(Locale.getDefault());
        }
    }

    public ByDayRule(List<WeekDay> dayList, Frequency frequency) {
        this(dayList, frequency, null);
    }

    public ByDayRule(List<WeekDay> dayList, Frequency frequency, DayOfWeek firstDayOfWeek) {
        super(frequency);
        this.dayList = dayList;
        if (firstDayOfWeek != null) {
            weekFields = WeekFields.of(firstDayOfWeek, 1);
        } else {
            weekFields = WeekFields.of(Locale.getDefault());
        }
    }

    @Override
    public List<T> transform(List<T> dates) {
        if (dayList.isEmpty()) {
            return dates;
        }
        final List<T> weekDayDates = new ArrayList<>();

        Function<T, List<T>> transformer = null;
        switch (getFrequency()) {
            case WEEKLY: transformer = new WeeklyExpansionFilter(); break;
            case MONTHLY: transformer = new MonthlyExpansionFilter(); break;
            case YEARLY: transformer = new YearlyExpansionFilter(); break;
            case DAILY:
            default: transformer = new LimitFilter();
        }

        for (final T date : dates) {
            List<T> transformed = transformer.apply(date);

            // filter by offset
            List<T> filtered = new ArrayList<>();
            dayList.forEach(day -> filtered.addAll(getOffsetDates(transformed.stream().filter(d ->
                    getDayOfWeek(d) == WeekDay.getDayOfWeek(day))
                .collect(Collectors.toList()), day.getOffset())));
            weekDayDates.addAll(filtered);
        }
        return weekDayDates;
    }

    private class LimitFilter implements Function<T, List<T>> {
        @Override
        public List<T> apply(T date) {
            if (dayList.contains(WeekDay.getWeekDay(getDayOfWeek(date)))) {
                return Collections.singletonList(date);
            }
            return Collections.emptyList();
        }
    }

    private class WeeklyExpansionFilter implements Function<T, List<T>> {
        @Override
        public List<T> apply(T date) {
            List<T> retVal = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                T candidate = withTemporalField(date, weekFields.dayOfWeek(), i);
                if (dayList.parallelStream().map(WeekDay::getDayOfWeek).anyMatch(calDay -> getDayOfWeek(candidate) == calDay)) {
                    retVal.add(candidate);
                }
            }
            return retVal;
        }
    }

    private class MonthlyExpansionFilter implements Function<T, List<T>> {
        @Override
        public List<T> apply(T date) {
            List<T> retVal = new ArrayList<>();
            Month month = Month.of(getMonth(date).getMonthOfYear());
            boolean leapYear = Year.isLeap(getYear(date));
            // construct a list of possible month days..
            for (int i = 1; i <= month.length(leapYear); i++) {
                T candidate = withTemporalField(date, DAY_OF_MONTH, i);
                if (dayList.parallelStream().map(WeekDay::getDayOfWeek).anyMatch(calDay -> getDayOfWeek(candidate) == calDay)) {
                    retVal.add(candidate);
                }
            }
            return retVal;
        }
    }

    private class YearlyExpansionFilter implements Function<T, List<T>> {
        @Override
        public List<T> apply(T date) {
            List<T> retVal = new ArrayList<>();
            int year = getYear(date);
            // construct a list of possible year days..
            for (int i = 1; i <= Year.of(year).length(); i++) {
                T candidate = withTemporalField(date, DAY_OF_YEAR, i);
                if (dayList.parallelStream().map(WeekDay::getDayOfWeek).anyMatch(calDay -> getDayOfWeek(candidate) == calDay)) {
                    retVal.add(candidate);
                }
            }
            return retVal;
        }
    }

    /**
     * Returns a single-element sublist containing the element of <code>list</code> at <code>offset</code>. Valid
     * offsets are from 1 to the size of the list. If an invalid offset is supplied, all elements from <code>list</code>
     * are added to <code>sublist</code>.
     *
     * @param dates
     * @param offset
     */
    private List<T> getOffsetDates(final List<T> dates, final int offset) {
        if (offset == 0) {
            return dates;
        }
        final List<T> offsetDates = new ArrayList<>();
        final int size = dates.size();
        if (offset < 0 && offset >= -size) {
            offsetDates.add(dates.get(size + offset));
        } else if (offset > 0 && offset <= size) {
            offsetDates.add(dates.get(offset - 1));
        }
        return offsetDates;
    }
}
