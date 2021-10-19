package net.fortuna.ical4j.model;

import net.fortuna.ical4j.util.CompatibilityHints;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.Date;

/**
 * Support adapter for {@link java.time.temporal.TemporalAmount} representation in iCalendar format.
 */
public class TemporalAmountAdapter implements Serializable {

    private final TemporalAmount duration;

    public TemporalAmountAdapter(TemporalAmount duration) {
        this.duration = duration;
    }

    public TemporalAmount getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return toString(LocalDateTime.now());
    }
    public String toString(Temporal seed) {
        String retVal;
        if (Duration.ZERO.equals(duration) || Period.ZERO.equals(duration)) {
            retVal = duration.toString();
        } else if (duration instanceof Period) {
            retVal = periodToString(((Period) duration).normalized(), seed);
        } else {
            retVal = durationToString((Duration) duration);
        }
        return  retVal;
    }

    /**
     * As the {@link Period} implementation doesn't support string representation in weeks, but does support
     * years and months, we need to generate a string that converts years, months and days to weeks.
     *
     * @param period a period instance
     * @return a string representation of the period that is compliant with the RFC5545 specification.
     */
    private String periodToString(Period period, Temporal seed) {
        String retVal;
        Temporal adjustedSeed = seed.plus(period);
        if (period.getYears() != 0) {
            long weeks = Math.abs(seed.until(adjustedSeed, ChronoUnit.WEEKS));
            retVal = String.format("P%dW", weeks);
        } else if (period.getMonths() != 0) {
            long weeks = Math.abs(seed.until(adjustedSeed, ChronoUnit.WEEKS));
            retVal = String.format("P%dW", weeks);
        } else if (period.getDays() % 7 == 0) {
            long weeks = Math.abs(seed.until(adjustedSeed, ChronoUnit.WEEKS));
            retVal = String.format("P%dW", weeks);
        } else {
            long days = Math.abs(seed.until(adjustedSeed, ChronoUnit.DAYS));
            retVal = String.format("P%dD", days);
        }
        if (period.isNegative() && !retVal.startsWith("-")) {
            return "-" + retVal;
        } else {
            return retVal;
        }
    }

    /**
     * As the {@link Duration} implementation doesn't support string representation in days (to avoid
     * confusion with {@link Period}), we need to generate a string that does support days.
     *
     * @param duration a duration instance
     * @return a string representation of the duration that is compliant with the RFC5545 specification.
     */
    private String durationToString(Duration duration) {
        String retVal = null;
        Duration absDuration = duration.abs();
        int days = 0;
        if (absDuration.getSeconds() != 0) {
            days = (int) absDuration.getSeconds() / (24 * 60 * 60);
        }

        if (days != 0) {
            Duration durationMinusDays = absDuration.minusDays(days);
            if (durationMinusDays.getSeconds() != 0) {
                int hours = (int) durationMinusDays.getSeconds() / (60 * 60);
                int minutes = (int) durationMinusDays.minusHours(hours).getSeconds() / 60;
                int seconds = (int) durationMinusDays.minusHours(hours).minusMinutes(minutes).getSeconds();
                if (hours > 0) {
                    if (seconds > 0) {
                        retVal = String.format("P%dDT%dH%dM%dS", days, hours, minutes, seconds);
                    } else if (minutes > 0) {
                        retVal = String.format("P%dDT%dH%dM", days, hours, minutes);
                    } else {
                        retVal = String.format("P%dDT%dH", days, hours);
                    }
                } else if (minutes > 0) {
                    if (seconds > 0) {
                        retVal = String.format("P%dDT%dM%dS", days, minutes, seconds);
                    } else {
                        retVal = String.format("P%dDT%dM", days, minutes);
                    }
                } else if (seconds > 0) {
                    retVal = String.format("P%dDT%dS", days, seconds);
                }
            } else {
                retVal = String.format("P%dD", days);
            }
        } else {
            retVal = absDuration.toString();
        }

        if (duration.isNegative()) {
            return "-" + retVal;
        } else {
            return retVal;
        }
    }

    public static TemporalAmountAdapter parse(String value) {
        return parse(value, CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING));
    }

    public static TemporalAmountAdapter parse(String value, boolean lenient) {
        TemporalAmount retVal = null;
        if (Arrays.asList("P", "PT").contains(value) && lenient) {
            retVal = Period.ZERO;
        }
        else if (value.matches("([+-])?P.*(W|D)")) {
            retVal = java.time.Period.parse(value);
        } else if (lenient && value.matches("P([+-]?[0-9]*[MHS])+")) {
            retVal = java.time.Duration.parse("PT" + value.substring(1));
        } else {
            retVal = java.time.Duration.parse(value);
        }
        return new TemporalAmountAdapter(retVal);
    }

    public static TemporalAmountAdapter fromDateRange(Date start, Date end) {
        TemporalAmount duration;
        long durationMillis = end.getTime() - start.getTime();
        if (durationMillis % (24 * 60 * 60 * 1000) == 0) {
            duration = java.time.Period.ofDays((int) (durationMillis / (24 * 60 * 60 * 1000)));
        } else {
            duration = java.time.Duration.ofMillis(durationMillis);
        }
        return new TemporalAmountAdapter(duration);
    }

    public static TemporalAmountAdapter from(Dur dur) {
        TemporalAmount duration;
        if (dur.getWeeks() > 0) {
            Period p = Period.ofWeeks(dur.getWeeks());
            if (dur.isNegative()) {
                p = p.negated();
            }
            duration = p;
        } else {
            Duration d = Duration.ofDays(dur.getDays())
                    .plusHours(dur.getHours())
                    .plusMinutes(dur.getMinutes())
                    .plusSeconds(dur.getSeconds());
            if (dur.isNegative()) {
                d = d.negated();
            }
            duration = d;
        }
        return new TemporalAmountAdapter(duration);
    }

    /**
     * Returns a date representing the end of this duration from the specified start date.
     * @param start the date to start the duration
     * @return the end of the duration as a date
     */
    public final Date getTime(final Date start) {
        return Date.from(Instant.from(duration.addTo(start.toInstant())));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TemporalAmountAdapter that = (TemporalAmountAdapter) o;

        return new EqualsBuilder()
                .append(duration, that.duration)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(duration)
                .toHashCode();
    }
}
