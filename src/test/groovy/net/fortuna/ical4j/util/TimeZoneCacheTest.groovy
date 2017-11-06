package net.fortuna.ical4j.util

import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import spock.lang.Specification

class TimeZoneCacheTest extends Specification {

    def 'assert cache works as expected'() {
        given: 'an empty cache instance'
        cache.clear()

        when: 'a timezone is added to the cache'
        def tz = TimeZoneRegistryFactory.instance.createRegistry().getTimeZone('Australia/Melbourne').getVTimeZone()
        def isAdded = cache.putIfAbsent(tz.timeZoneId.value, tz)

        then: 'timezone exists in the cache'
        isAdded && cache.getTimezone(tz.timeZoneId.value) == tz

        where:
        cache << [new MapTimeZoneCache(), new JCacheTimeZoneCache()]
    }
}
