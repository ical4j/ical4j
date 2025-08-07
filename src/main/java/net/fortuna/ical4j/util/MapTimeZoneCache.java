package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.component.VTimeZone;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * A simple in-memory cache for time zones, using a {@link ConcurrentMap} to store
 * time zones by their ID. This implementation is thread-safe and allows for
 * concurrent access.
 *
 * <p>This class implements the {@link TimeZoneCache} interface, providing methods
 * to retrieve, add, and check for time zones in the cache.</p>
 *
 * @see TimeZoneCache
 */
public class MapTimeZoneCache implements TimeZoneCache {

    private final ConcurrentMap<String, VTimeZone> mapCache;

    public MapTimeZoneCache() {
        mapCache = new ConcurrentHashMap<>();
    }

    @Override
    public VTimeZone getTimezone(String id) {
        return mapCache.get(id);
    }

    @Override
    public VTimeZone getTimezone(String id, Supplier<VTimeZone> putIfAbsent) {
        if (!containsId(id)) {
            mapCache.put(id, putIfAbsent.get());
        }
        return getTimezone(id);
    }

    @Override
    public boolean putIfAbsent(String id, VTimeZone timeZone) {
        var v = mapCache.get(id);
        if (v == null) {
            mapCache.put(id, timeZone);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsId(String id) {
        return mapCache.containsKey(id);
    }

    @Override
    public void clear() {
        mapCache.clear();
    }
}
