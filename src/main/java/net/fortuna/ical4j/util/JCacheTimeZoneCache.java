package net.fortuna.ical4j.util;

/**
 * JCache is no longer supported in iCal4j. This implementation now points to a default in memory cache
 * instead.
 */
@Deprecated
public class JCacheTimeZoneCache extends CaffeineTimeZoneCache {
}
