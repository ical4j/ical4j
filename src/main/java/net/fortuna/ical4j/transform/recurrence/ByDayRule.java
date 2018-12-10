package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
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
public class ByDayRule extends AbstractRecurrenceRule {

    public enum FilterType {
        Daily, Weekly, Monthly, Yearly;
    }

    private final WeekDayList dayList;

    private final FilterType filterType;

    public ByDayRule(WeekDayList dayList, FilterType filterType) {
        this.dayList = dayList;
        this.filterType = filterType;
    }

    public ByDayRule(WeekDayList dayList, FilterType filterType, Optional<WeekDay.Day> weekStartDay) {
        super(weekStartDay);
        this.dayList = dayList;
        this.filterType = filterType;
    }

    @Override
    public DateList transform(DateList dates) {
        if (dayList.isEmpty()) {
            return dates;
        }
        final DateList weekDayDates = Dates.getDateListInstance(dates);

        Function<Date, List<Date>> transformer = null;
        switch (filterType) {
            case Weekly: transformer = new WeeklyWeekDayFilter(dates.getType()); break;
            case Monthly: transformer = new MonthlyWeekDayFilter(dates.getType()); break;
            case Yearly: transformer = new YearlyWeekDayFilter(dates.getType()); break;
            case Daily:
            default: transformer = new DailyWeekDayFilter();
        }

        for (final Date date : dates) {
            List<Date> transformed = transformer.apply(date);

            // filter by offset
            List<Date> filtered = new ArrayList<>();
            dayList.forEach(day -> filtered.addAll(getOffsetDates(transformed.stream().filter(d ->
                getCalendarInstance(d, true).get(Calendar.DAY_OF_WEEK) == WeekDay.getCalendarDay(day))
                .collect(Collectors.toCollection(() -> Dates.getDateListInstance(weekDayDates))), day.getOffset())));

//            for (final WeekDay weekDay : dayList) {
//                // if BYYEARDAY or BYMONTHDAY is specified filter existing
//                // list..
//                if (!getYearDayList().isEmpty() || !getMonthDayList().isEmpty()) {
//                    final Calendar cal = getCalendarInstance(date, true);
//                    if (weekDay.equals(WeekDay.getWeekDay(cal))) {
//                        weekDayDates.add(date);
//                    }
//                } else {
//                    weekDayDates.addAll(getAbsWeekDays(date, dates.getType(), weekDay));
//                }
//            }
            weekDayDates.addAll(filtered);
        }
        return weekDayDates;
    }

    private class DailyWeekDayFilter implements Function<Date, List<Date>> {

        @Override
        public List<Date> apply(Date date) {
            final Calendar cal = getCalendarInstance(date, true);
            if (dayList.contains(WeekDay.getWeekDay(cal))) {
                return Arrays.asList(date);
            }
            return Collections.emptyList();
        }
    }

    private class WeeklyWeekDayFilter implements Function<Date, List<Date>> {

        private final Value type;

        public WeeklyWeekDayFilter(Value type) {
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
                    retVal.add(Dates.getInstance(cal.getTime(), type));
                }
                cal.add(Calendar.DAY_OF_WEEK, 1);
            }
            return retVal;
        }
    }

    private class MonthlyWeekDayFilter implements Function<Date, List<Date>> {

        private final Value type;

        public MonthlyWeekDayFilter(Value type) {
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
                    retVal.add(Dates.getInstance(cal.getTime(), type));
                }
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            return retVal;
        }
    }

    private class YearlyWeekDayFilter implements Function<Date, List<Date>> {

        private final Value type;

        public YearlyWeekDayFilter(Value type) {
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
                    retVal.add(Dates.getInstance(cal.getTime(), type));
                }
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
            return retVal;
        }
    }

    /**
     * Returns a list of applicable dates corresponding to the specified week day in accordance with the frequency
     * specified by this recurrence rule.
     *
     * @param date
     * @param weekDay
     * @return
     */
//    private List<Date> getAbsWeekDays(final Date date, final Value type, final WeekDay weekDay) {
//        final Calendar cal = getCalendarInstance(date, true);
//        final DateList days = new DateList(type);
//        if (date instanceof DateTime) {
//            if (((DateTime) date).isUtc()) {
//                days.setUtc(true);
//            } else {
//                days.setTimeZone(((DateTime) date).getTimeZone());
//            }
//        }
//        final int calDay = WeekDay.getCalendarDay(weekDay);
//        if (calDay == -1) {
//            // a matching weekday cannot be identified..
//            return days;
//        }
//        if (Frequency.DAILY.equals(frequency)) {
//            if (cal.get(Calendar.DAY_OF_WEEK) == calDay) {
//                days.add(Dates.getInstance(cal.getTime(), type));
//            }
//        } else if (Frequency.WEEKLY.equals(frequency) || !getWeekNoList().isEmpty()) {
//            final int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
//            // construct a list of possible week days..
//            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
//            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
//                cal.add(Calendar.DAY_OF_WEEK, 1);
//            }
////            final int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
//            if (cal.get(Calendar.WEEK_OF_YEAR) == weekNo) {
//                days.add(Dates.getInstance(cal.getTime(), type));
////                cal.add(Calendar.DAY_OF_WEEK, Dates.DAYS_PER_WEEK);
//            }
//        } else if (Frequency.MONTHLY.equals(frequency) || !getMonthList().isEmpty()) {
//            final int month = cal.get(Calendar.MONTH);
//            // construct a list of possible month days..
//            cal.set(Calendar.DAY_OF_MONTH, 1);
//            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
//                cal.add(Calendar.DAY_OF_MONTH, 1);
//            }
//            while (cal.get(Calendar.MONTH) == month) {
//                days.add(Dates.getInstance(cal.getTime(), type));
//                cal.add(Calendar.DAY_OF_MONTH, Dates.DAYS_PER_WEEK);
//            }
//        } else if (Frequency.YEARLY.equals(frequency)) {
//            final int year = cal.get(Calendar.YEAR);
//            // construct a list of possible year days..
//            cal.set(Calendar.DAY_OF_YEAR, 1);
//            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
//                cal.add(Calendar.DAY_OF_YEAR, 1);
//            }
//            while (cal.get(Calendar.YEAR) == year) {
//                days.add(Dates.getInstance(cal.getTime(), type));
//                cal.add(Calendar.DAY_OF_YEAR, Dates.DAYS_PER_WEEK);
//            }
//        }
//        return getOffsetDates(days, weekDay.getOffset());
//    }

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
