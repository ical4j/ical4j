package net.fortuna.ical4j.model

import net.fortuna.ical4j.AbstractTzurlIntegrationTest

class TimeZoneLoaderTest extends AbstractTzurlIntegrationTest {

    def 'assert timezone loads correctly'() {
        given: 'a timezone loader instance'
        TimeZoneLoader loader = ['zoneinfo/']

        when: 'a timezone is loaded'
        def tz = loader.loadVTimeZone(id)

        then: 'a non-null instance is returned'
        tz?.timeZoneId.get().value == id

        and: 'subsequent retrieval returns the same object'
        loader.loadVTimeZone(id) === tz

        where:
        id << ['Australia/Melbourne', 'Europe/London', 'Asia/Singapore']
    }

    def 'assert timezone loads correctly when overriding timezone cache'() {

        given: 'the timezone cache implementation is overridden'
        System.setProperty('net.fortuna.ical4j.timezone.cache.impl', 'net.fortuna.ical4j.util.MapTimeZoneCache')

        and: 'a timezone loader instance'
        TimeZoneLoader loader = ['zoneinfo/']

        when: 'a timezone is loaded'
        def tz = loader.loadVTimeZone(id)

        then: 'a non-null instance is returned'
        tz?.timeZoneId.get().value == id

        and: 'subsequent retrieval returns the same object'
        loader.loadVTimeZone(id) === tz

        where:
        id << ['Australia/Melbourne', 'Europe/London', 'Asia/Singapore']
    }
}
