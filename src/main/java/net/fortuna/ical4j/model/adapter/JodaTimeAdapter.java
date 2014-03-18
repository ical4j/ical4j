package net.fortuna.ical4j.model.adapter;

import net.fortuna.ical4j.model.DateRange;
import net.fortuna.ical4j.model.Dur;
import org.joda.time.*;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.DurationConverter;
import org.joda.time.convert.IntervalConverter;
import org.joda.time.convert.PeriodConverter;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * Created by fortuna on 18/03/14.
 */
public class JodaTimeAdapter {

    static {
        ConverterManager.getInstance().addDurationConverter(new DurationConverter() {
            public long getDurationMillis(Object object) {
                Dur dur = (Dur) object;
                Date instant = new Date();
                return dur.getTime(instant).getTime() - instant.getTime();
            }

            public Class<?> getSupportedType() {
                return Dur.class;
            }
        });

        ConverterManager.getInstance().addPeriodConverter(new PeriodConverter() {
            public void setInto(ReadWritablePeriod period, Object object, Chronology chrono) {
                Dur dur = (Dur) object;
                period.setWeeks(dur.getWeeks());
                period.setDays(dur.getDays());
                period.setHours(dur.getHours());
                period.setMinutes(dur.getMinutes());
                period.setSeconds(dur.getSeconds());
            }

            public PeriodType getPeriodType(Object object) {
                return PeriodType.forFields(new DurationFieldType[] {
                        DurationFieldType.weeks(),
                        DurationFieldType.days(),
                        DurationFieldType.hours(),
                        DurationFieldType.minutes(),
                        DurationFieldType.seconds(),
                });
            }

            public Class<?> getSupportedType() {
                return Dur.class;
            }
        });

        ConverterManager.getInstance().addIntervalConverter(new IntervalConverter() {
            public boolean isReadableInterval(Object object, Chronology chrono) {
                return false;
            }

            public void setInto(ReadWritableInterval writableInterval, Object object, Chronology chrono) {
                DateRange period = (DateRange) object;
                writableInterval.setEndMillis(period.getRangeEnd().getTime());
                writableInterval.setStartMillis(period.getRangeStart().getTime());
            }

            public Class<?> getSupportedType() {
                return DateRange.class;
            }
        });
    }

    public static final <T, V> V adapt(T value, Class<V> clazz) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return clazz.getConstructor(Object.class).newInstance(value);
    }
}
