package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.Dates;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Optional;

public abstract class AbstractRecurrenceRule implements Transformer<DateList>, Serializable {

    private final int calendarWeekStartDay;

    public AbstractRecurrenceRule() {
        // default week start is Monday per RFC5545
        this(Optional.of(WeekDay.Day.MO));
    }

    public AbstractRecurrenceRule(Optional<WeekDay.Day> weekStartDay) {
        this.calendarWeekStartDay = WeekDay.getCalendarDay(WeekDay.getWeekDay(weekStartDay.orElse(WeekDay.Day.MO)));
    }

    /**
     * Construct a Calendar object and sets the time.
     *
     * @param date
     * @param lenient
     * @return
     */
    protected Calendar getCalendarInstance(final Date date, final boolean lenient) {
        Calendar cal = Dates.getCalendarInstance(date);
        // A week should have at least 4 days to be considered as such per RFC5545
        cal.setMinimalDaysInFirstWeek(4);
        cal.setFirstDayOfWeek(calendarWeekStartDay);
        cal.setLenient(lenient);
        cal.setTime(date);

        return cal;
    }
}
