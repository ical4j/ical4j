package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.component.VTimeZone;

public interface TimeZoneCache {

    VTimeZone getTimezone(String id);

    boolean putIfAbsent(String id, VTimeZone timeZone);

    boolean containsId(String id);

    void clear();
}
