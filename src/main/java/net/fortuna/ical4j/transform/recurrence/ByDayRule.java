package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.WeekDayList;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Dates;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Applies BYDAY rules specified in this Recur instance to the specified date list. If no BYDAY rules are specified
 * the date list is returned unmodified.
 */
public class ByDayRule extends AbstractDateExpansionRule {

    private final WeekDayList dayList;

    public ByDayRule(WeekDayList dayList, Frequency frequency) {
        super(frequency);
        this.dayList = dayList;
    }

    public ByDayRule(WeekDayList dayList, Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.dayList = dayList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (dayList.isEmpty()) {
            return dates;
        }
        final DateList weekDayDates = Dates.getDateListInstance(dates);

        Function<Date, List<Date>> transformer = null;
        switch (getFrequency()) {
            case WEEKLY: transformer = new WeeklyExpansionFilter(dates.getType()); break;
            case MONTHLY: transformer = new MonthlyExpansionFilter(dates.getType()); break;
            case YEARLY: transformer = new YearlyExpansionFilter(dates.getType()); break;
            case DAILY:
            default: transformer = new LimitFilter();
        }

        for (final Date date : dates) {
            List<Date> transformed = transformer.apply(date);

            // filter by offset
            List<Date> filtered = new ArrayList<>();
            dayList.forEach(day -> filtered.addAll(getOffsetDates(transformed.stream().filter(d ->
                getCalendarInstance(d, true).get(Calendar.DAY_OF_WEEK) == WeekDay.getCalendarDay(day))
                .collect(Collectors.toCollection(() -> Dates.getDateListInstance(weekDayDates))), day.getOffset())));
            weekDayDates.addAll(filtered);
        }
        return weekDayDates;
    }

    private class LimitFilter implements Function<Date, List<Date>> {

        @Override
        public List<Date> apply(Date date) {
            final Calendar cal = getCalendarInstance(date, true);
            if (dayList.contains(WeekDay.getWeekDay(cal))) {
                return Arrays.asList(date);
            }
            return Collections.emptyList();
        }
    }

    private class WeeklyExpansionFilter implements Function<Date, List<Date>> {

        private final Value type;

        public WeeklyExpansionFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            List<Date> retVal = new ArrayList<>();
            final Calendar cal = getCalendarInstance(date, true);
            final int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
            // construct a list of possible week days..
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            while (cal.get(Calendar.WEEK_OF_YEAR) == weekNo) {
                if (!dayList.stream().map(weekDay -> WeekDay.getCalendarDay(weekDay))
                        .filter(calDay -> cal.get(Calendar.DAY_OF_WEEK) == calDay)
                        .collect(Collectors.toList()).isEmpty()) {
                    retVal.add(Dates.getInstance(getTime(date, cal), type));
                }
                cal.add(Calendar.DAY_OF_WEEK, 1);
            }
            return retVal;
        }
    }

    private class MonthlyExpansionFilter implements Function<Date, List<Date>> {

        private final Value type;

        public MonthlyExpansionFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            List<Date> retVal = new ArrayList<>();
            final Calendar cal = getCalendarInstance(date, true);
            final int month = cal.get(Calendar.MONTH);
            // construct a list of possible month days..
            cal.set(Calendar.DAY_OF_MONTH, 1);
            while (cal.get(Calendar.MONTH) == month) {
                if (!dayList.stream().map(weekDay -> WeekDay.getCalendarDay(weekDay))
                        .filter(calDay -> cal.get(Calendar.DAY_OF_WEEK) == calDay)
                        .collect(Collectors.toList()).isEmpty()) {
                    retVal.add(Dates.getInstance(getTime(date, cal), type));
                }
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            return retVal;
        }
    }

    private class YearlyExpansionFilter implements Function<Date, List<Date>> {

        private final Value type;

        public YearlyExpansionFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            List<Date> retVal = new ArrayList<>();
            final Calendar cal = getCalendarInstance(date, true);
            final int year = cal.get(Calendar.YEAR);
            // construct a list of possible year days..
            cal.set(Calendar.DAY_OF_YEAR, 1);
            while (cal.get(Calendar.YEAR) == year) {
                if (!dayList.stream().map(weekDay -> WeekDay.getCalendarDay(weekDay))
                        .filter(calDay -> cal.get(Calendar.DAY_OF_WEEK) == calDay)
                        .collect(Collectors.toList()).isEmpty()) {
                    retVal.add(Dates.getInstance(getTime(date, cal), type));
                }
                cal.add(Calendar.DAY_OF_YEAR, 1);
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
    private List<Date> getOffsetDates(final DateList dates, final int offset) {
        if (offset == 0) {
            return dates;
        }
        final List<Date> offsetDates = Dates.getDateListInstance(dates);
        final int size = dates.size();
        if (offset < 0 && offset >= -size) {
            offsetDates.add(dates.get(size + offset));
        } else if (offset > 0 && offset <= size) {
            offsetDates.add(dates.get(offset - 1));
        }
        return offsetDates;
    }
}
