package net.fortuna.ical4j.model

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.time.LocalDate
import java.time.Year
import java.time.ZoneOffset

class ZoneRulesBuilderTest extends Specification {

    @Shared
    TimeZoneRegistry timeZoneRegistry = TimeZoneRegistryFactory.instance.createRegistry()

    @Unroll
    def 'test build rules'() {
        given: 'a local date-time'
        def localDate = LocalDate.ofYearDay(Year.now().value, dayOfYear).atStartOfDay()

        expect: 'rule definitions match expected'
        def zonerules = new ZoneRulesBuilder()
                .vTimeZone(timeZoneRegistry.getTimeZone(tzId).getVTimeZone()).build()
        zonerules.getOffset(localDate) == expectedOffset

        and:
        Instant localInstant = localDate.toInstant(expectedOffset)
        zonerules.isDaylightSavings(localInstant) == java.util.TimeZone.getTimeZone(tzId)
                .inDaylightTime(java.util.Date.from(localInstant));

        where:
        tzId                    | dayOfYear     | expectedOffset
        'UTC'                   | 1             | ZoneOffset.UTC
        'Australia/Melbourne'   | 1             | ZoneOffset.ofHours(11)
        'Australia/Melbourne'   | 180           | ZoneOffset.ofHours(10)
//        'Europe/Lisbon'         | 1             | ZoneOffset.ofHours(1)
        'Europe/Madrid'         | 1             | ZoneOffset.ofHours(1)
        'America/Los_Angeles'   | 1             | ZoneOffset.ofHours(-8)
        'America/Los_Angeles'   | 180           | ZoneOffset.ofHours(-7)
    }
}
