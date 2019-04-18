package net.fortuna.ical4j.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

public class TemporalAdapter<T extends Temporal> {

    static DateTimeFormatter DATE_TIME_DEFAULT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    static DateTimeFormatter DATE_TIME_UTC = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    public enum FormatType {
        Date, DateTimeFloating, DateTimeUtc
    }

    private final T temporal;

    private final FormatType formatType;

    public TemporalAdapter(TemporalAdapter adapter) {
        this((T) adapter.temporal, adapter.formatType);
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
                return DateTimeFormatter.BASIC_ISO_DATE.format(temporal);

            case DateTimeFloating:
                return DATE_TIME_DEFAULT.format(temporal);

            case DateTimeUtc:
                return DATE_TIME_UTC.format(temporal);
        }
    }

    public static TemporalAdapter parse(String value) throws DateTimeParseException {
        TemporalAccessor temporal = DATE_TIME_UTC.parseBest(value, LocalDateTime::from,
                LocalDate::from);

        TemporalAdapter retVal;
        if (temporal instanceof LocalDateTime) {
            retVal = new TemporalAdapter<>((LocalDateTime) temporal, FormatType.DateTimeFloating);
        } else {
            retVal = new TemporalAdapter<>((LocalDate) temporal, FormatType.Date);
        }
        return retVal;
    }

    public static List<TemporalAdapter> parseList(String value) throws DateTimeParseException {
        List<TemporalAdapter> retVal = new ArrayList<>();
        String[] dates = value.split(",");
        for (String date : dates) {
            TemporalAccessor temporal = DATE_TIME_UTC.parseBest(value, LocalDateTime::from,
                    LocalDate::from);

            if (temporal instanceof LocalDateTime) {
                retVal.add(new TemporalAdapter<>((LocalDateTime) temporal, FormatType.DateTimeFloating));
            } else {
                retVal.add(new TemporalAdapter<>((LocalDate) temporal, FormatType.Date));
            }
        }
        return retVal;
    }

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
