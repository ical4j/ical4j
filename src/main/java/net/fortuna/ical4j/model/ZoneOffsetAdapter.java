package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Objects;

/**
 * Support adapter for {@link java.time.ZoneOffset} to output in iCalendar format.
 */
public class ZoneOffsetAdapter implements Serializable {

    private final ZoneOffset offset;

    public ZoneOffsetAdapter(ZoneOffset offset) {
        Objects.requireNonNull(offset, "offset");
        this.offset = offset;
    }

    public ZoneOffset getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        var retVal = "";
        int hours = Math.abs(offset.getTotalSeconds()) / (60 * 60);
        if (offset.getTotalSeconds() < 0) {
            hours = -hours;
        }
        int minutes = Math.abs(offset.getTotalSeconds()) % (60 * 60) / 60;
        int seconds = Math.abs(offset.getTotalSeconds()) % (60 * 60) % 60;
        if (seconds > 0) {
            retVal = String.format(Locale.US, "%+03d%02d%02d", hours, minutes, seconds);
        } else {
            retVal = String.format(Locale.US, "%+03d%02d", hours, minutes);
        }
        return retVal;
    }

    /**
     * @param utcOffset
     * @return a zoneoffset representing the specified utcoffset
     */
    public static ZoneOffset from(@SuppressWarnings("deprecation") UtcOffset utcOffset) {
        return ZoneOffset.of(utcOffset.toString());
    }
}
