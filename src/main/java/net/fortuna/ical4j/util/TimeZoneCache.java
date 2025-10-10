package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.component.VTimeZone;

import java.util.function.Supplier;

/**
 * An interface for a cache that stores time zones identified by their IDs.
 * This interface provides methods to retrieve, add, and check for time zones
 * in the cache.
 *
 * <p>Implementations of this interface should ensure thread safety and
 * efficient access to time zone data.</p>
 *
 * @see VTimeZone
 */
public interface TimeZoneCache {

    VTimeZone getTimezone(String id);

    VTimeZone getTimezone(String id, Supplier<VTimeZone> putIfAbsent);

    @Deprecated
    boolean putIfAbsent(String id, VTimeZone timeZone);

    @Deprecated
    boolean containsId(String id);

    void clear();
}
