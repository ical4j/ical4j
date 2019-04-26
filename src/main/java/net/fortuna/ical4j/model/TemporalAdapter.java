package net.fortuna.ical4j.model;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

/**
 * The iCalendar specification supports multiple representations of date/time values, as outlined
 * below. This class encapsulates a {@link Temporal} value with a date/time {@link FormatType}
 * that provides support for all corresponding representations in the specification.
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
 * @param <T> A concrete implementation of {@link Temporal}
 */
public class TemporalAdapter<T extends Temporal> {

    public static DateTimeFormatter DATE_TIME_DEFAULT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    public static DateTimeFormatter DATE_TIME_UTC = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    /**
     * A formatter capable of parsing to multiple temporal types based on the input string.
     */
    private static DateTimeFormatter PARSE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd['T'HHmmss[X]]");

    public enum FormatType {
        Date, DateTimeFloating, DateTimeUtc
    }

    private final T temporal;

    private final FormatType formatType;

    public TemporalAdapter(TemporalAdapter<T> adapter) {
        this.temporal = adapter.temporal;
        this.formatType = adapter.formatType;
    }

    public TemporalAdapter(T temporal, FormatType formatType) {
        this.temporal = temporal;
        this.formatType = formatType;
    }

    public T getTemporal() {
        return temporal;
    }

    public FormatType getFormatType() {
        return formatType;
    }

    @Override
    public String toString() {
        switch (formatType) {
            case Date:
            default:
                return toString(DateTimeFormatter.BASIC_ISO_DATE);

            case DateTimeFloating:
                return toString(DATE_TIME_DEFAULT);

            case DateTimeUtc:
                return toString(DATE_TIME_UTC);
        }
    }

    public String toString(DateTimeFormatter formatter) {
        return formatter.format(temporal);
    }

    /**
     * Parse a string representation of a temporal value.
     * @param value a string representing a temporal
     * @return an adapter containing the parsed temporal value and format type
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static <T extends Temporal> TemporalAdapter<T> parse(String value) throws DateTimeParseException {
        TemporalAccessor temporal = PARSE_FORMATTER.parseBest(value, Instant::from,
                LocalDateTime::from, LocalDate::from);

        TemporalAdapter retVal;
        if (temporal instanceof Instant) {
            retVal = new TemporalAdapter<>((Instant) temporal, FormatType.DateTimeUtc);
        } else if (temporal instanceof LocalDateTime) {
            retVal = new TemporalAdapter<>((LocalDateTime) temporal, FormatType.DateTimeFloating);
        } else {
            retVal = new TemporalAdapter<>((LocalDate) temporal, FormatType.Date);
        }
        return (TemporalAdapter<T>) retVal;
    }

    /**
     * Parse a string representation of a zoned temporal value.
     * @param value a string representing a zoned temporal
     * @return an adapter containing the parsed temporal value and format type
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static TemporalAdapter<ZonedDateTime> parse(String value, ZoneId zoneId) throws DateTimeParseException {
        ZonedDateTime temporal = DATE_TIME_DEFAULT.withZone(zoneId).parse(value, ZonedDateTime::from);
        return new TemporalAdapter<>(temporal, FormatType.DateTimeFloating);
    }

    public static <T extends Temporal> List<TemporalAdapter<T>> parseList(String value) throws DateTimeParseException {
        List<TemporalAdapter<T>> retVal = new ArrayList<>();
        String[] dates = value.split(",");
        for (String date : dates) {
            retVal.add(parse(date));
        }
        return retVal;
    }

    @SuppressWarnings("deprecation")
    public static TemporalAdapter from(Date date) {
        Temporal temporal = date.toInstant();

        FormatType formatType;
        if (date instanceof DateTime) {
            DateTime dateTime = (DateTime) date;
            if (dateTime.isUtc()) {
                formatType = FormatType.DateTimeUtc;
            } else {
                temporal = ZonedDateTime.ofInstant(date.toInstant(),
                        dateTime.getTimeZone().toZoneId());
                formatType = FormatType.DateTimeFloating;
            }
        } else {
            formatType = FormatType.Date;
        }
        return new TemporalAdapter<>(temporal, formatType);
    }
}
