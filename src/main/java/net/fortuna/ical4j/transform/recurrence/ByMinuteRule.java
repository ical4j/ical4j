package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Dates;

import java.util.*;
import java.util.function.Function;

import static net.fortuna.ical4j.model.Recur.Frequency.*;

/**
 * Applies BYMINUTE rules specified in this Recur instance to the specified date list. If no BYMINUTE rules are
 * specified the date list is returned unmodified.
 */
public class ByMinuteRule extends AbstractDateExpansionRule {

    private final NumberList minuteList;

    public ByMinuteRule(NumberList minuteList, Frequency frequency) {
        super(frequency);
        this.minuteList = minuteList;
    }

    public ByMinuteRule(NumberList minuteList, Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.minuteList = minuteList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (minuteList.isEmpty()) {
            return dates;
        }
        final DateList minutelyDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            if (EnumSet.of(HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY).contains(getFrequency())) {
                minutelyDates.addAll(new ExpansionFilter(minutelyDates.getType()).apply(date));
            } else {
                Optional<Date> limit = new LimitFilter().apply(date);
                if (limit.isPresent()) {
                    minutelyDates.add(limit.get());
                }
            }
        }
        return minutelyDates;
    }

    private class LimitFilter implements Function<Date, Optional<Date>> {

        @Override
        public Optional<Date> apply(Date date) {
            final Calendar cal = getCalendarInstance(date, true);
            if (minuteList.contains(cal.get(Calendar.MINUTE))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }

    private class ExpansionFilter implements Function<Date, List<Date>> {

        private final Value type;

        public ExpansionFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            List<Date> retVal = new ArrayList<>();
            final Calendar cal = getCalendarInstance(date, true);
            // construct a list of possible minutes..
            minuteList.forEach(minute -> {
                cal.set(Calendar.MINUTE, minute);
                retVal.add(Dates.getInstance(getTime(date, cal), type));
            });
            return retVal;
        }
    }
}
