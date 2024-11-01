package net.fortuna.ical4j.model;

import net.fortuna.ical4j.util.CompatibilityHints;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.Period;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Support adapter for {@link java.time.temporal.TemporalAmount} representation in iCalendar format.
 */
public class TemporalAmountAdapter implements Serializable {

    private final TemporalAmount duration;

    public TemporalAmountAdapter(TemporalAmount duration) {
        Objects.requireNonNull(duration, "duration");
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
            retVal = durationToString((Duration) duration, seed);
        }
        return retVal;
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
        var adjustedSeed = seed.plus(period);
        if (period.getYears() != 0) {
            long weeks = Math.abs(seed.until(adjustedSeed, ChronoUnit.WEEKS));
            retVal = String.format(Locale.US, "P%dW", weeks);
        } else if (period.getMonths() != 0) {
            long weeks = Math.abs(seed.until(adjustedSeed, ChronoUnit.WEEKS));
            retVal = String.format(Locale.US, "P%dW", weeks);
        } else if (period.getDays() % 7 == 0) {
            long weeks = Math.abs(seed.until(adjustedSeed, ChronoUnit.WEEKS));
            retVal = String.format(Locale.US, "P%dW", weeks);
        } else {
            long days = Math.abs(seed.until(adjustedSeed, ChronoUnit.DAYS));
            retVal = String.format(Locale.US, "P%dD", days);
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
    private String durationToString(Duration duration, Temporal seed) {
        String retVal = null;
        var absDuration = duration.abs();
        var adjustedSeed = seed.plus(absDuration);
        long days = 0;
        if (duration.getSeconds() != 0) {
            days = seed.until(adjustedSeed, ChronoUnit.DAYS);
        }

        if (days != 0) {
            var durationMinusDays = absDuration.minusDays(days);
            if (durationMinusDays.getSeconds() != 0) {
                adjustedSeed = seed.plus(durationMinusDays);
                long hours = seed.until(adjustedSeed, ChronoUnit.HOURS);

                adjustedSeed = seed.plus(durationMinusDays.minusHours(hours));
                long minutes = seed.until(adjustedSeed, ChronoUnit.MINUTES);

                adjustedSeed = seed.plus(durationMinusDays.minusHours(hours).minusMinutes(minutes));
                long seconds = seed.until(adjustedSeed, ChronoUnit.SECONDS);

                if (hours > 0) {
                    if (seconds > 0) {
                        retVal = String.format(Locale.US, "P%dDT%dH%dM%dS", days, hours, minutes, seconds);
                    } else if (minutes > 0) {
                        retVal = String.format(Locale.US, "P%dDT%dH%dM", days, hours, minutes);
                    } else {
                        retVal = String.format(Locale.US, "P%dDT%dH", days, hours);
                    }
                } else if (minutes > 0) {
                    if (seconds > 0) {
                        retVal = String.format(Locale.US, "P%dDT%dM%dS", days, minutes, seconds);
                    } else {
                        retVal = String.format(Locale.US, "P%dDT%dM", days, minutes);
                    }
                } else if (seconds > 0) {
                    retVal = String.format(Locale.US, "P%dDT%dS", days, seconds);
                }
            } else {
                retVal = String.format(Locale.US, "P%dD", days);
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

    public Duration toDuration() {
        if (duration instanceof Duration) {
            return (Duration) duration;
        } else {
            var seed = LocalDateTime.now();
            long days = seed.until(seed.plus(duration), ChronoUnit.DAYS);
            return Duration.ofDays(days);
        }
    }

    public static TemporalAmountAdapter parse(String value) {
        return parse(value, CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING));
    }

    public static TemporalAmountAdapter parse(String value, boolean lenient) {
        TemporalAmount retVal;
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

    public static TemporalAmountAdapter from(Temporal start, Temporal end) {
        if (start instanceof LocalDate) {
            return from((LocalDate) start, (LocalDate) end);
        }
        return new TemporalAmountAdapter(Duration.between(start, end));
    }

    public static TemporalAmountAdapter from(LocalDate start, LocalDate end) {
        return new TemporalAmountAdapter(Period.between(start, end));
    }

    public static TemporalAmountAdapter fromDateRange(Date start, Date end) {
        TemporalAmount duration;
        long durationMillis = end.getTime() - start.getTime();
        if (durationMillis % (24 * 60 * 60 * 1_000) == 0) {
            duration = java.time.Period.ofDays((int) (durationMillis / (24 * 60 * 60 * 1_000)));
        } else {
            duration = java.time.Duration.ofMillis(durationMillis);
        }
        return new TemporalAmountAdapter(duration);
    }

    @SuppressWarnings("deprecation")
    public static TemporalAmountAdapter from(Dur dur) {
        TemporalAmount duration;
        if (dur.getWeeks() > 0) {
            var p = Period.ofWeeks(dur.getWeeks());
            if (dur.isNegative()) {
                p = p.negated();
            }
            duration = p;
        } else {
            var d = Duration.ofDays(dur.getDays())
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

    public static TemporalAmountAdapter between(Temporal t1, Temporal t2) {
        TemporalAmount difference;
        if (t1 instanceof LocalDate && t2 instanceof LocalDate) {
            difference = Period.between((LocalDate) t1, (LocalDate) t2);
        } else {
            difference = Duration.between(t1, t2);
        }
        return new TemporalAmountAdapter(difference);
    }

    /**
     * Returns a date representing the end of this duration from the specified start date.
     * @param start the date to start the duration
     * @return the end of the duration as a date
     * @deprecated use <code>getDuration().addTo(start)</code>, where start is a {@link Temporal}
     */
    @Deprecated
    public final Date getTime(final Date start) {
        return Date.from(Instant.from(duration.addTo(start.toInstant())));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        var that = (TemporalAmountAdapter) o;

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
