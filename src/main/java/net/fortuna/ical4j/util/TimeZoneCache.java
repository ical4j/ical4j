package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.component.VTimeZone;

import java.util.function.Supplier;

public interface TimeZoneCache {

    VTimeZone getTimezone(String id);

    VTimeZone getTimezone(String id, Supplier<VTimeZone> putIfAbsent);

    @Deprecated
    boolean putIfAbsent(String id, VTimeZone timeZone);

    @Deprecated
    boolean containsId(String id);

    void clear();
}
