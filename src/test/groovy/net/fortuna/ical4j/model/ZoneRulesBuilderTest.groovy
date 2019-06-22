package net.fortuna.ical4j.model

import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant
import java.time.ZoneOffset

class ZoneRulesBuilderTest extends Specification {

    @Shared
    TimeZoneRegistry timeZoneRegistry = TimeZoneRegistryFactory.instance.createRegistry()

    def 'test build rules'() {
        expect: 'rule definitions match expected'
        def zonerules = new ZoneRulesBuilder().vTimeZone(timeZoneRegistry.getTimeZone(tzId).getVTimeZone()).build()
        zonerules.getOffset(Instant.now()) == expectedOffset
        
        Instant now = Instant.now();
        zonerules.isDaylightSavings(now) == java.util.TimeZone.getTimeZone(tzId).inDaylightTime(java.util.Date.from(now));

        where:
        tzId                    | expectedOffset
        'UTC'                   | ZoneOffset.UTC
        'Australia/Melbourne'   | ZoneOffset.ofHours(10)
        'Europe/Lisbon'         | ZoneOffset.ofHours(1)
        'America/Los_Angeles'   | ZoneOffset.ofHours(-7)
    }
}
