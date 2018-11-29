package net.fortuna.ical4j.model;

import java.time.ZoneOffset;

public class ZoneOffsetFactory {

    public ZoneOffset create(UtcOffset utcOffset) {
        return ZoneOffset.of(utcOffset.toString());
    }
}
