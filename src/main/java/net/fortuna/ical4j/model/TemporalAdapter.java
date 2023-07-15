package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.TimeZones;

import java.io.Serializable;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
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
 * Note that where a local (i.e. floating) temporal type is used we need to apply a {@link ZoneId} for calculations such as
 * recurrence inclusions and other date-based comparisons. Use {@link TemporalAdapter#isFloating(Temporal)} to determine floating
 * instances.
 *
 * @param <T> A concrete implementation of {@link Temporal}
 */
public class TemporalAdapter<T extends Temporal> implements Serializable {

    private static final TemporalComparator COMPARATOR = new TemporalComparator();

    private final String valueString;

    private final TzId tzId;

    private transient final TimeZoneRegistry timeZoneRegistry;

    private transient volatile T temporal;

    public TemporalAdapter(TemporalAdapter<T> adapter) {
        this.temporal = adapter.temporal;
        this.valueString = adapter.valueString;
        this.tzId = adapter.tzId;
        this.timeZoneRegistry = adapter.timeZoneRegistry;
    }

    public TemporalAdapter(T temporal) {
        this(temporal, null);
    }

    public TemporalAdapter(T temporal, TimeZoneRegistry timeZoneRegistry) {
        Objects.requireNonNull(temporal, "temporal");
        this.temporal = temporal;
        this.valueString = toString(temporal);
        if (isDateTimePrecision(temporal) && !isFloating(temporal) && !isUtc(temporal)) {
//            ZoneId zoneId = ((ZonedDateTime) temporal).getZone();
//            this.tzId = new TzId(zoneId.getId());
            this.tzId = new TzId.Factory().createParameter(TimeZones.getDefault().toZoneId().getId());
        } else {
            this.tzId = null;
        }
        this.timeZoneRegistry = timeZoneRegistry;
    }

    private TemporalAdapter(String valueString) {
        this(valueString, null);
    }

    /**
     * Support lazy parsing of value string using a zone id to allow full initialisation of
     * {@link java.time.zone.ZoneRulesProvider} instances.
     *
     * @param value a string representation of a floating date/time value
     * @param tzId a zone id to apply to the parsed value
     */
    private TemporalAdapter(String value, TzId tzId) {
        this(value, tzId, null);
    }

    /**
     *
     * @param value a string representation of a floating date/time value
     * @param tzId a zone id to apply to the parsed value
     * @param timeZoneRegistry timezone definitions
     */
    private TemporalAdapter(String value, TzId tzId, TimeZoneRegistry timeZoneRegistry) {
        this.valueString = value;
        this.tzId = tzId;
        this.timeZoneRegistry = timeZoneRegistry;
    }

    @SuppressWarnings("unchecked")
    public T getTemporal() {
        if (temporal == null) {
            synchronized (valueString) {
                if (temporal == null) {
                    if (tzId != null) {
                        try {
                            temporal = (T) CalendarDateFormat.FLOATING_DATE_TIME_FORMAT.parse(valueString,
                                    tzId.toZoneId(timeZoneRegistry));
                        } catch (DateTimeParseException e) {
                            // TZID specified for non-floating date-time. If relaxed validation enabled fall back
                            // to default parser and ignore TZID..
                            if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
                                temporal = (T) CalendarDateFormat.DEFAULT_PARSE_FORMAT.parse(valueString);
                            } else {
                                throw e;
                            }
                        }
                    } else {
                        temporal = (T) CalendarDateFormat.DEFAULT_PARSE_FORMAT.parse(valueString);
                    }
                    /*
                    temporal = (T) Proxy.newProxyInstance(ChronoZonedDateTime.class.getClassLoader(),
                            new Class[]{ChronoZonedDateTime.class},
                            new InvocationHandler() {
                                private ChronoZonedDateTime<LocalDate> temporal;

                                @Override
                                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                    if (temporal == null) {
                                        temporal = CalendarDateFormat.FLOATING_DATE_TIME_FORMAT.parse(valueString, tzId.toZoneId());
                                    }
                                    return method.invoke(temporal, args);
                                }
                            });

                     */
                }
            }
        }
        return temporal;
    }

    @Override
    public String toString() {
        return toString(getTemporal());
    }

    public String toString(ZoneId zoneId) {
        return toString(getTemporal(), zoneId);
    }

    private String toString(T temporal) {
        if (getTemporal() instanceof ChronoZonedDateTime) {
            return toString(CalendarDateFormat.FLOATING_DATE_TIME_FORMAT,
                    ((ChronoZonedDateTime<?>)temporal).getZone(), temporal);
        }
        return toString(getTemporal(), TimeZones.getDefault().toZoneId());
    }

    /**
     * Return a string representation of the given temporal value, using the specified zone ID
     * for applicable values (i.e. values that support the DATE-TIME format).
     * @param temporal a temporal value
     * @param zoneId the overriding zone id for DATE-TIME instances
     * @return a string
     */
    private String toString(T temporal, ZoneId zoneId) {
        if (ZoneOffset.UTC.equals(zoneId)) {
            return toInstantString(temporal);
        } else if (!isDateTimePrecision(temporal)) {
            return toString(CalendarDateFormat.DATE_FORMAT, temporal);
        } else {
            if (isFloating(getTemporal())) {
                return toString(CalendarDateFormat.FLOATING_DATE_TIME_FORMAT, temporal);
            } else if (isUtc(getTemporal())) {
                return toString(CalendarDateFormat.UTC_DATE_TIME_FORMAT, temporal);
            } else {
                return toString(CalendarDateFormat.FLOATING_DATE_TIME_FORMAT, zoneId, temporal);
            }
        }
    }

    /**
     * Return an ISO8601 instant string for the specified temporal value.
     * @param temporal a temporal value
     * @return a string
     */
    private String toInstantString(T temporal) {
        return toString(CalendarDateFormat.UTC_DATE_TIME_FORMAT, temporal);
    }

    private String toString(CalendarDateFormat format, T temporal) {
        return format.format(temporal);
    }

    private String toString(CalendarDateFormat format, ZoneId zoneId, T temporal) {
        return format.format(temporal, zoneId);
    }

    public ZonedDateTime toLocalTime() {
        return toLocalTime(TimeZones.getDefault().toZoneId());
    }

    public ZonedDateTime toLocalTime(ZoneId zoneId) {
        return toLocalTime(getTemporal(), zoneId);
    }
    public static ZonedDateTime toLocalTime(Temporal temporal, ZoneId zoneId) {
        if (isFloating(temporal)) {
            if (temporal instanceof LocalDateTime) {
                return ((LocalDateTime) temporal).atZone(zoneId);
            } else {
                return ((LocalDate) temporal).atStartOfDay().atZone(zoneId);
            }
        } else if (isUtc(temporal)) {
            return ((Instant) temporal).atZone(zoneId);
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
        return parse(value, CalendarDateFormat.DEFAULT_PARSE_FORMAT);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Temporal> TemporalAdapter<T> parse(String value, CalendarDateFormat parseFormat) throws DateTimeParseException {
        return new TemporalAdapter<>((T) parseFormat.parse(value));
    }

    /**
     * Parse a string representation of a temporal value applicable to the specified timezone.
     *
     * @param value a string representing a floating temporal value
     * @param zoneId a timezone applied to the parsed value
     * @return an adapter containing the parsed temporal value
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static TemporalAdapter<ZonedDateTime> parse(String value, ZoneId zoneId) {
        return new TemporalAdapter<>(CalendarDateFormat.FLOATING_DATE_TIME_FORMAT.parse(value, zoneId));
    }

    /**
     * Parse a string representation of a temporal value applicable to the specified timezone.
     *
     * @param value a string representing a floating temporal value
     * @param tzId a timezone applied to the parsed value
     * @return an adapter containing the parsed temporal value
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static TemporalAdapter<ZonedDateTime> parse(String value, TzId tzId) {
        return new TemporalAdapter<>(value, tzId);
    }

    /**
     *
     * @param value a string representing a floating temporal value
     * @param tzId a timezone applied to the parsed value
     * @param timeZoneRegistry timezone definitions
     * @return
     */
    public static TemporalAdapter<ZonedDateTime> parse(String value, TzId tzId, TimeZoneRegistry timeZoneRegistry) {
        return new TemporalAdapter<>(value, tzId, timeZoneRegistry);
    }

    /**
     * This method provides support for conversion of legacy {@link Date} and {@link DateTime} instances to temporal
     * values.
     *
     * @param date a date/time instance
     * @return a temporal adapter instance equivalent to the specified date/time value
     */
    @SuppressWarnings("deprecation")
    public static TemporalAdapter<?> from(Date date) {
        Temporal temporal;
        if (date instanceof DateTime) {
            DateTime dateTime = (DateTime) date;
            if (dateTime.isUtc()) {
                temporal = date.toInstant();
            } else if (dateTime.getTimeZone() == null) {
                temporal = LocalDateTime.ofInstant(date.toInstant(), TimeZones.getDefault().toZoneId());
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
        return !ChronoField.OFFSET_SECONDS.isSupportedBy(date) &&
                !ChronoField.INSTANT_SECONDS.isSupportedBy(date);
    }

    /**
     * Indicates whether the temporal type represents a UTC date/time value.
     * @return true if the temporal type is in UTC time, otherwise false
     */
    public static boolean isUtc(Temporal date) {
        return ChronoField.INSTANT_SECONDS.isSupportedBy(date) &&
                !ChronoField.OFFSET_SECONDS.isSupportedBy(date);
    }

    /**
     * Indicates whether the temporal type represents a date/time value.
     * @return true if the temporal type has DATE-TIME precision, otherwise false
     */
    public static boolean isDateTimePrecision(Temporal date) {
        return ChronoField.INSTANT_SECONDS.isSupportedBy(date) ||
                ChronoField.HOUR_OF_DAY.isSupportedBy(date);
    }

    public static <T extends Temporal> boolean isBefore(T date1, T date2) {
        return COMPARATOR.compare(date1, date2) < 0;
    }

    public static <T extends Temporal> boolean isAfter(T date1, T date2) {
        return COMPARATOR.compare(date1, date2) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemporalAdapter<?> that = (TemporalAdapter<?>) o;
        return valueString.equals(that.valueString) && Objects.equals(tzId, that.tzId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueString, tzId);
    }
}
