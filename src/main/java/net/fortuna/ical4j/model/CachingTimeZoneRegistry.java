package net.fortuna.ical4j.model;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;

public class CachingTimeZoneRegistry implements TimeZoneRegistry {
    
    private final TimeZoneRegistry delegate;
    
    private final Cache<String, TimeZone> cache;
    
    public CachingTimeZoneRegistry(TimeZoneRegistry delegate) {
        this(delegate, cacheInit());
    }
    
    public CachingTimeZoneRegistry(TimeZoneRegistry delegate, Cache<String, TimeZone> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }
    
    private static Cache cacheInit() {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        Cache cache = cacheManager.getCache("ical4j.timezones", String.class, TimeZone.class);
        if (cache == null) {
            MutableConfiguration<String, TimeZone> cacheConfig = new MutableConfiguration<>();
            cacheConfig.setTypes(String.class, TimeZone.class);
            cache = cacheManager.createCache("ical4j.timezones", cacheConfig);
        }
        return cache;
    }
    
    @Override
    public void register(final TimeZone timezone) {
        delegate.register(timezone);
    }
    
    @Override
    public void register(final TimeZone timezone, boolean update) {
        delegate.register(timezone, update);
    }
    
    @Override
    public void clear() {
        delegate.clear();
        cache.removeAll();
    }
    
    @Override
    public TimeZone getTimeZone(final String id) {
        if (!cache.containsKey(id)) {
            TimeZone tz = delegate.getTimeZone(id);
            if (tz != null) {
                cache.putIfAbsent(id, tz);
            }
        }
        return cache.get(id);
    }
}
