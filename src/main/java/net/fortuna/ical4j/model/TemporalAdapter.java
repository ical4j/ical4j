package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Objects;

/**
 * The iCalendar specification supports multiple representations of date/time values, as outlined
 * below. This class encapsulates a {@link Temporal} value
 * and provides support for all corresponding representations in the specification.
 *
 * The recommended {@link Temporal} implementations for use with iCal4j are as follows:
 *
 * <ul>
 *     <li>{@link LocalDate} - represents an iCalendar DATE value as defined in section 3.3.4 of RFC5545</li>
 *     <li>{@link LocalDateTime} - represents an iCalendar FORM #1: DATE-TIME value as defined in section 3.3.5 of RFC5545</li>
 *     <li>{@link java.time.Instant} - represents an iCalendar FORM #2: DATE-TIME value as defined in section 3.3.5 of RFC5545</li>
 *     <li>{@link ZonedDateTime} - represents an iCalendar FORM #3: DATE-TIME value as defined in section 3.3.5 of RFC5545</li>
 * </ul>
 *
 * Note that a local (i.e. floating) temporal type is used we need to apply a {@link ZoneId} for calculations such as
 * recurrence inclusions and other date-based comparisons. Use {@link TemporalAdapter#isFloating(Temporal)} to determine floating
 * instances.
 *
 * @param <T> A concrete implementation of {@link Temporal}
 */
public class TemporalAdapter<T extends Temporal> implements Serializable {

    public static DateTimeFormatter DATE_TIME_DEFAULT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    public static DateTimeFormatter DATE_TIME_UTC = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneId.of("UTC"));

    /**
     * A formatter capable of parsing to multiple temporal types based on the input string.
     */
    private static CalendarDateFormat PARSE_FORMAT = new CalendarDateFormat(
            DateTimeFormatter.ofPattern("yyyyMMdd['T'HHmmss[X]]"), Instant::from, LocalDateTime::from, LocalDate::from);

    private final T temporal;

    public TemporalAdapter(TemporalAdapter<T> adapter) {
        this.temporal = adapter.temporal;
    }

    public TemporalAdapter(T temporal) {
        Objects.requireNonNull(temporal, "temporal");
        this.temporal = temporal;
    }

    public T getTemporal() {
        return temporal;
    }

    @Override
    public String toString() {
        if (temporal instanceof LocalDate) {
            return toString(CalendarDateFormat.DATE_FORMAT);
        } else {
            if (isFloating(temporal)) {
                return toString(CalendarDateFormat.FLOATING_DATE_TIME_FORMAT);
            } else if (isUtc(temporal)) {
                return toString(CalendarDateFormat.UTC_DATE_TIME_FORMAT);
            } else {
                return toString(CalendarDateFormat.FLOATING_DATE_TIME_FORMAT, ZoneId.systemDefault());
            }
        }
    }

    public String toString(CalendarDateFormat format) {
        return format.format(temporal);
    }

    public String toString(CalendarDateFormat format, ZoneId zoneId) {
        return format.format(temporal, zoneId);
    }

    public ZonedDateTime toLocalTime() {
        if (isFloating(temporal)) {
            return ((LocalDateTime) temporal).atZone(ZoneId.systemDefault());
        } else if (isUtc(temporal)) {
            return ((Instant) temporal).atZone(ZoneId.systemDefault());
        } else {
            return ZonedDateTime.from(temporal);
        }
    }

    /**
     * Parse a string representation of a temporal value.
     *
     * @param value a string representing a temporal
     * @return an adapter containing the parsed temporal value and format type
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static <T extends Temporal> TemporalAdapter<T> parse(String value) throws DateTimeParseException {
        return new TemporalAdapter<>((T) PARSE_FORMAT.parse(value));
    }

    /**
     * Parse a string representation of a temporal value applicable to the specified timezone.
     *
     * @param value a string representing a floating temporal value
     * @param zoneId a timezone applied to the parsed value
     * @return an adapter containing the parsed temporal value
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static TemporalAdapter<ZonedDateTime> parse(String value, ZoneId zoneId) throws DateTimeParseException {
        return new TemporalAdapter<>(CalendarDateFormat.FLOATING_DATE_TIME_FORMAT.parse(value, zoneId));
    }

    /**
     * This method provides support for conversion of legacy {@link Date} and {@link DateTime} instances to temporal
     * values.
     *
     * @param date a date/time instance
     * @return a temporal adapter instance equivalent to the specified date/time value
     */
    @SuppressWarnings("deprecation")
    public static TemporalAdapter from(Date date) {
        Temporal temporal;
        if (date instanceof DateTime) {
            DateTime dateTime = (DateTime) date;
            if (dateTime.isUtc()) {
                temporal = date.toInstant();
            } else {
                temporal = ZonedDateTime.ofInstant(date.toInstant(), dateTime.getTimeZone().toZoneId());
            }
        } else {
            temporal = LocalDate.from(date.toInstant());
        }
        return new TemporalAdapter<>(temporal);
    }

    /**
     * Indicates whether the temporal type represents a "floating" date/time value.
     * @return true if the temporal type is floating, otherwise false
     */
    public static boolean isFloating(Temporal date) {
        return date instanceof LocalDateTime || date instanceof LocalTime;
    }

    /**
     * Indicates whether the temporal type represents a UTC date/time value.
     * @return true if the temporal type is in UTC time, otherwise false
     */
    public static boolean isUtc(Temporal date) {
        return date instanceof Instant;
    }

    public static <T extends Temporal> boolean isBefore(T date1, T date2) {
        if (date1 instanceof LocalDate) {
            return ((LocalDate) date1).isBefore((LocalDate) date2);
        } else if (date1 instanceof LocalDateTime) {
            return ((LocalDateTime) date1).isBefore((LocalDateTime) date2);
        }
        return Instant.from(date1).isBefore(Instant.from(date2));
    }

    public static <T extends Temporal> boolean isAfter(T date1, T date2) {
        if (date1 instanceof LocalDate) {
            return ((LocalDate) date1).isAfter((LocalDate) date2);
        } else if (date1 instanceof LocalDateTime) {
            return ((LocalDateTime) date1).isAfter((LocalDateTime) date2);
        }
        return Instant.from(date1).isAfter(Instant.from(date2));
    }
}
