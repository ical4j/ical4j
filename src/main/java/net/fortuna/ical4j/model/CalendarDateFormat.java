package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Support conversion of temporal values to/from iCalendar string representations.
 *
 * <pre>
 * 3.3.4.  Date
 *
 * Value Name:  DATE
 *
 * Purpose:  This value type is used to identify values that contain a
 * calendar date.
 *
 * Format Definition:  This value type is defined by the following
 * notation:
 *
 * date               = date-value
 *
 * date-value         = date-fullyear date-month date-mday
 * date-fullyear      = 4DIGIT
 * date-month         = 2DIGIT        ;01-12
 * date-mday          = 2DIGIT        ;01-28, 01-29, 01-30, 01-31
 * ;based on month/year
 *
 * Description:  If the property permits, multiple "date" values are
 * specified as a COMMA-separated list of values.  The format for the
 * value type is based on the [ISO.8601.2004] complete
 * representation, basic format for a calendar date.  The textual
 * format specifies a four-digit year, two-digit month, and two-digit
 * day of the month.  There are no separator characters between the
 * year, month, and day component text.
 *
 * No additional content value encoding (i.e., BACKSLASH character
 * encoding, see Section 3.3.11) is defined for this value type.
 *
 * Example:  The following represents July 14, 1997:
 *
 * 19970714
 *
 * 3.3.5.  Date-Time
 *
 * Value Name:  DATE-TIME
 *
 * Purpose:  This value type is used to identify values that specify a
 * precise calendar date and time of day.
 *
 * Format Definition:  This value type is defined by the following
 * notation:
 *
 * date-time  = date "T" time ;As specified in the DATE and TIME
 * ;value definitions
 *
 * Description:  If the property permits, multiple "DATE-TIME" values
 * are specified as a COMMA-separated list of values.  No additional
 * content value encoding (i.e., BACKSLASH character encoding, see
 * Section 3.3.11) is defined for this value type.
 *
 * The "DATE-TIME" value type is used to identify values that contain
 * a precise calendar date and time of day.  The format is based on
 * the [ISO.8601.2004] complete representation, basic format for a
 * calendar date and time of day.  The text format is a concatenation
 * of the "date", followed by the LATIN CAPITAL LETTER T character,
 * the time designator, followed by the "time" format.
 *
 * The "DATE-TIME" value type expresses time values in three forms:
 *
 * The form of date and time with UTC offset MUST NOT be used.  For
 * example, the following is not valid for a DATE-TIME value:
 *
 * 19980119T230000-0800       ;Invalid time format
 *
 * FORM #1: DATE WITH LOCAL TIME
 *
 * The date with local time form is simply a DATE-TIME value that
 * does not contain the UTC designator nor does it reference a time
 * zone.  For example, the following represents January 18, 1998, at
 * 11 PM:
 *
 * 19980118T230000
 *
 * DATE-TIME values of this type are said to be "floating" and are
 * not bound to any time zone in particular.  They are used to
 * represent the same hour, minute, and second value regardless of
 * which time zone is currently being observed.  For example, an
 * event can be defined that indicates that an individual will be
 * busy from 11:00 AM to 1:00 PM every day, no matter which time zone
 * the person is in.  In these cases, a local time can be specified.
 * The recipient of an iCalendar object with a property value
 * consisting of a local time, without any relative time zone
 * information, SHOULD interpret the value as being fixed to whatever
 * time zone the "ATTENDEE" is in at any given moment.  This means
 * that two "Attendees", in different time zones, receiving the same
 * event definition as a floating time, may be participating in the
 * event at different actual times.  Floating time SHOULD only be
 * used where that is the reasonable behavior.
 *
 * In most cases, a fixed time is desired.  To properly communicate a
 * fixed time in a property value, either UTC time or local time with
 * time zone reference MUST be specified.
 *
 * The use of local time in a DATE-TIME value without the "TZID"
 * property parameter is to be interpreted as floating time,
 * regardless of the existence of "VTIMEZONE" calendar components in
 * the iCalendar object.
 *
 * FORM #2: DATE WITH UTC TIME
 *
 * The date with UTC time, or absolute time, is identified by a LATIN
 * CAPITAL LETTER Z suffix character, the UTC designator, appended to
 * the time value.  For example, the following represents January 19,
 * 1998, at 0700 UTC:
 *
 * 19980119T070000Z
 *
 * The "TZID" property parameter MUST NOT be applied to DATE-TIME
 * properties whose time values are specified in UTC.
 *
 * FORM #3: DATE WITH LOCAL TIME AND TIME ZONE REFERENCE
 *
 * The date and local time with reference to time zone information is
 * identified by the use the "TZID" property parameter to reference
 * the appropriate time zone definition.  "TZID" is discussed in
 * detail in Section 3.2.19.  For example, the following represents
 * 2:00 A.M. in New York on January 19, 1998:
 *
 * TZID=America/New_York:19980119T020000
 *
 * If, based on the definition of the referenced time zone, the local
 * time described occurs more than once (when changing from daylight
 * to standard time), the DATE-TIME value refers to the first
 * occurrence of the referenced time.  Thus, TZID=America/
 * New_York:20071104T013000 indicates November 4, 2007 at 1:30 A.M.
 * EDT (UTC-04:00).  If the local time described does not occur (when
 * changing from standard to daylight time), the DATE-TIME value is
 * interpreted using the UTC offset before the gap in local times.
 * Thus, TZID=America/New_York:20070311T023000 indicates March 11,
 * 2007 at 3:30 A.M. EDT (UTC-04:00), one hour after 1:30 A.M. EST
 * (UTC-05:00).
 *
 * A time value MUST only specify the second 60 when specifying a
 * positive leap second.  For example:
 *
 * 19970630T235960Z
 *
 * Implementations that do not support leap seconds SHOULD interpret
 * the second 60 as equivalent to the second 59.
 *
 * Example:  The following represents July 14, 1997, at 1:30 PM in New
 * York City in each of the three time formats, using the "DTSTART"
 * property.
 *
 * DTSTART:19970714T133000                   ; Local time
 * DTSTART:19970714T173000Z                  ; UTC time
 * DTSTART;TZID=America/New_York:19970714T133000
 * ; Local time and time
 * ; zone reference
 * </pre>
 *
 */
public class CalendarDateFormat implements Serializable {

    private static class LocalDateTemporalQuery implements TemporalQuery<LocalDate>, Serializable {
        @Override
        public LocalDate queryFrom(TemporalAccessor temporal) {
            return LocalDate.from(temporal);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return obj != null && getClass() == obj.getClass();
        }
    }

    private static class LocalDateTimeTemporalQuery implements TemporalQuery<LocalDateTime>, Serializable {
        @Override
        public LocalDateTime queryFrom(TemporalAccessor temporal) {
            return LocalDateTime.from(temporal);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return obj != null && getClass() == obj.getClass();
        }
    }

    private static class InstantTemporalQuery implements TemporalQuery<Instant>, Serializable {
        @Override
        public Instant queryFrom(TemporalAccessor temporal) {
            return Instant.from(temporal);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return obj != null && getClass() == obj.getClass();
        }
    }

    public static final CalendarDateFormat DATE_FORMAT = new CalendarDateFormat(
            "yyyyMMdd", new LocalDateTemporalQuery());

    public static final CalendarDateFormat FLOATING_DATE_TIME_FORMAT = new CalendarDateFormat(
            "yyyyMMdd'T'HHmmss", new LocalDateTimeTemporalQuery());

    public static final CalendarDateFormat UTC_DATE_TIME_FORMAT = new CalendarDateFormat(
            "yyyyMMdd'T'HHmmss'Z'", new InstantTemporalQuery());

    public static final CalendarDateFormat RELAXED_DATE_TIME_FORMAT = new CalendarDateFormat(
            "yyyyMMdd'T'HHmmss[X]", new InstantTemporalQuery(), new LocalDateTimeTemporalQuery());

    /**
     * A formatter capable of parsing to multiple temporal types based on the input string.
     */
    public static final CalendarDateFormat DEFAULT_PARSE_FORMAT = new CalendarDateFormat(
            "yyyyMMdd['T'HHmmss[X]]", new InstantTemporalQuery(), new LocalDateTimeTemporalQuery(),
            new LocalDateTemporalQuery());

    private final String pattern;

    private transient volatile DateTimeFormatter formatter;

    private final TemporalQuery<? extends TemporalAccessor>[] parsers;

    @SafeVarargs
    public CalendarDateFormat(String pattern, TemporalQuery<? extends TemporalAccessor>...parsers) {
        Objects.requireNonNull(pattern, "pattern");
        this.pattern = pattern;
        this.parsers = parsers;
    }

    private DateTimeFormatter getFormatter() {
        if (formatter == null) {
            synchronized (pattern) {
                if (formatter == null) {
                    formatter = DateTimeFormatter.ofPattern(pattern);
                    if (pattern.endsWith("'Z'")) {
                        formatter = formatter.withZone(ZoneOffset.UTC);
                    }
                }
            }
        }
        return formatter;
    }

    public TemporalAccessor parse(String dateString) {
        if (parsers.length > 1) {
            return getFormatter().parseBest(dateString, parsers);
        } else if (parsers.length > 0) {
            return getFormatter().parse(dateString, parsers[0]);
        } else {
            return getFormatter().parse(dateString);
        }
    }

    public ZonedDateTime parse(String dateString, ZoneId zoneId) {
        return getFormatter().withZone(zoneId).parse(dateString, ZonedDateTime::from);
    }

    public String format(TemporalAccessor date) {
        return getFormatter().format(date);
    }

    public String format(TemporalAccessor date, ZoneId zoneId) {
        return getFormatter().withZone(zoneId).format(date);
    }

    public static CalendarDateFormat from(List<? extends Temporal> list) {
        if (!list.isEmpty()) {
            return from(list.get(0));
        }
        return FLOATING_DATE_TIME_FORMAT;
    }

    public static CalendarDateFormat from(Temporal temporal) {
        if (temporal instanceof Instant) {
            return UTC_DATE_TIME_FORMAT;
        } else if (temporal instanceof LocalDate) {
            return DATE_FORMAT;
        } else {
            return FLOATING_DATE_TIME_FORMAT;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarDateFormat that = (CalendarDateFormat) o;
        return pattern.equals(that.pattern) &&
                Arrays.equals(parsers, that.parsers);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(pattern);
        result = 31 * result + Arrays.hashCode(parsers);
        return result;
    }
}
