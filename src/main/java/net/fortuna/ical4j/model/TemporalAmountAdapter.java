package net.fortuna.ical4j.model;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Date;

/**
 * Support adapter for {@link java.time.temporal.TemporalAmount} representation in iCalendar format.
 */
public class TemporalAmountAdapter {

    private final TemporalAmount duration;

    public TemporalAmountAdapter(TemporalAmount duration) {
        this.duration = duration;
    }

    public TemporalAmount getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        String retVal = null;
        if (Duration.ZERO.equals(duration) || Period.ZERO.equals(duration)) {
            retVal = duration.toString();
        } else if (duration instanceof Period) {
            Period period = (Period) duration;
            if (period.getYears() > 0) {
                int weeks = Math.abs(period.getYears()) * 52;
                if (period.getYears() < 0) {
                    weeks = -weeks;
                }
                retVal = String.format("P%dW", weeks);
            } else if (period.getMonths() > 0) {
                int weeks = Math.abs(period.getMonths()) * 4;
                if (period.getMonths() < 0) {
                    weeks = -weeks;
                }
                retVal = String.format("P%dW", weeks);
            } else if (period.getDays() % 7 == 0) {
                int weeks = Math.abs(period.getDays()) / 7;
                if (period.getDays() < 0) {
                    weeks = -weeks;
                }
                retVal = String.format("P%dW", weeks);
            } else {
                retVal = duration.toString();
            }
        } else {
            retVal = duration.toString();
        }
        return  retVal;
    }

    public static TemporalAmountAdapter parse(String value) {
        TemporalAmount retVal = null;
        if (value.matches("P.*(W|D)$")) {
            retVal = java.time.Period.parse(value);
        } else {
            retVal = java.time.Duration.parse(value);
        }
        return new TemporalAmountAdapter(retVal);
    }

    public static TemporalAmountAdapter fromDateRange(Date start, Date end) {
        TemporalAmount duration;
        long durationMillis = end.getTime() - start.getTime();
        if (durationMillis % (24 * 60 * 60 * 1000) == 0) {
            duration = java.time.Period.ofDays((int) durationMillis / (24 * 60 * 60 * 1000));
        } else {
            duration = java.time.Duration.ofMillis(durationMillis);
        }
        return new TemporalAmountAdapter(duration);
    }

    public static TemporalAmount from(Dur duration) {
        if (duration.getWeeks() > 0) {
            return Period.ofWeeks(duration.getWeeks());
        }
        return Duration.ofDays(duration.getDays())
                .plusHours(duration.getHours())
                .plusMinutes(duration.getMinutes())
                .plusSeconds(duration.getSeconds());
    }
}
