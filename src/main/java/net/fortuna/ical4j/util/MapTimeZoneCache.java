package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.component.VTimeZone;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapTimeZoneCache implements TimeZoneCache {

    private final Map<String, VTimeZone> mapCache;

    public MapTimeZoneCache() {
        mapCache = new ConcurrentHashMap<>();
    }

    @Override
    public VTimeZone getTimezone(String id) {
        return mapCache.get(id);
    }

    @Override
    public boolean putIfAbsent(String id, VTimeZone timeZone) {
        VTimeZone v = mapCache.get(id);
        if (v == null) {
            v = mapCache.put(id, timeZone);
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
