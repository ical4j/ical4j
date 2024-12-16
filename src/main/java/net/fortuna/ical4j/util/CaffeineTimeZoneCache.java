package net.fortuna.ical4j.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.fortuna.ical4j.model.component.VTimeZone;

import java.util.function.Supplier;

public class CaffeineTimeZoneCache implements TimeZoneCache {

    private final Cache<String, VTimeZone> cache;

    public CaffeineTimeZoneCache() {
        cache = Caffeine.newBuilder().maximumSize(10_000).build();
    }

    @Override
    public VTimeZone getTimezone(String id) {
        return cache.getIfPresent(id);
    }

    @Override
    public VTimeZone getTimezone(String id, Supplier<VTimeZone> putIfAbsent) {
        return cache.get(id, key -> putIfAbsent.get());
    }

    @Override
    public boolean putIfAbsent(String id, VTimeZone timeZone) {
        cache.put(id, timeZone);
        return true;
    }

    @Override
    public boolean containsId(String id) {
        return cache.getIfPresent(id) != null;
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }
}
