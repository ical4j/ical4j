package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.component.VTimeZone;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;

public class JCacheTimeZoneCache implements TimeZoneCache {

    private final Cache<String, VTimeZone> jcacheCache;

    public JCacheTimeZoneCache() {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        Cache cache = cacheManager.getCache("ical4j.timezones", String.class, VTimeZone.class);
        if (cache == null) {
            synchronized (JCacheTimeZoneCache.class) {
                cache = cacheManager.getCache("ical4j.timezones", String.class, VTimeZone.class);
                if (cache == null) {
                    MutableConfiguration<String, VTimeZone> cacheConfig = new MutableConfiguration<>();
                    cacheConfig.setTypes(String.class, VTimeZone.class);
                    cache = cacheManager.createCache("ical4j.timezones", cacheConfig);
                }
            }
        }
        jcacheCache = cache;
    }

    @Override
    public VTimeZone getTimezone(String id) {
        return jcacheCache.get(id);
    }

    @Override
    public boolean putIfAbsent(String id, VTimeZone timeZone) {
        return jcacheCache.putIfAbsent(id, timeZone);
    }

    @Override
    public boolean containsId(String id) {
        return jcacheCache.containsKey(id);
    }

    @Override
    public void clear() {
        jcacheCache.clear();
    }
}
