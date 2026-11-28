package net.fortuna.ical4j.model

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.component.VEvent
import spock.lang.Specification

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.zone.ZoneRules
import java.time.zone.ZoneRulesException

class ZoneRulesProviderImplTest extends Specification {

    def 'verify zone rules'() {
        given: 'a zone rules provider instance'
        TimeZoneRegistry registry = TimeZoneRegistryFactory.instance.createRegistry()
        registry.register(registry.getTimeZone("Australia/Melbourne"))

        when: 'zone rules are requested'
        String tzId = registry.getZoneId("Australia/Melbourne")
        ZoneRules zoneRules = ZoneRulesProviderImpl.INSTANCE.provideRules(tzId, false)

        then: 'an appropriate rules instance is provided'
        zoneRules != null && zoneRules.getStandardOffset(Instant.now()) == ZoneOffset.ofHours(10)
    }

    def 'rules are provided for caching so a zone id keeps them'() {
        given:
        TimeZoneRegistry registry = TimeZoneRegistryFactory.instance.createRegistry()
        registry.register(registry.getTimeZone("Australia/Melbourne"))
        String tzId = registry.getZoneId("Australia/Melbourne")

        expect:
        ZoneRulesProviderImpl.INSTANCE.provideRules(tzId, true).is(registry.zoneRules[tzId])
    }

    def 'an unknown zone id throws ZoneRulesException rather than returning null'() {
        when:
        ZoneRulesProviderImpl.INSTANCE.provideRules('ical4j-unknown-zone', forCaching)

        then:
        thrown(ZoneRulesException)

        where:
        forCaching << [true, false]
    }

    def 'a date-time keeps its custom zone rules after its builder is collected and the id released'() {
        given: 'the start of an event in a zone defined only by its calendar'
        ZonedDateTime start = parseStart()

        when: 'the builder and its registry are collected and the pool releases their zone ids'
        for (int i = 0; i < 10; i++) {
            System.gc()
        }
        ZoneRulesProviderImpl.INSTANCE.zoneIdPool.cleanup()

        then: 'the date-time still resolves the rules it was parsed with'
        start.zone.rules.getOffset(start.toInstant()) == ZoneOffset.ofHours(2)
        start.plusDays(1).offset == ZoneOffset.ofHours(2)
    }

    private static ZonedDateTime parseStart() {
        def ics = 'BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//test//EN\r\n' +
                'BEGIN:VTIMEZONE\r\nTZID:X-Custom\r\nBEGIN:STANDARD\r\nDTSTART:19700101T000000\r\n' +
                'TZOFFSETFROM:+0200\r\nTZOFFSETTO:+0200\r\nEND:STANDARD\r\nEND:VTIMEZONE\r\n' +
                'BEGIN:VEVENT\r\nUID:e0@test\r\nDTSTAMP:20240101T000000Z\r\n' +
                'DTSTART;TZID=X-Custom:20240115T120000\r\nEND:VEVENT\r\nEND:VCALENDAR\r\n'
        VEvent event = new CalendarBuilder().build(new StringReader(ics)).getComponent('VEVENT').get()
        return event.getRequiredProperty("DTSTART").date as ZonedDateTime
    }
}
