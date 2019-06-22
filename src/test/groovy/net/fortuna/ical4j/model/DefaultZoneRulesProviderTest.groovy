package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.ZoneId

class DefaultZoneRulesProviderTest extends Specification {

    def 'asset new instance available ids'() {
        given: 'a zone rules provider'
        DefaultZoneRulesProvider provider = []

        when: 'requesting available ids'
        def availableIds = provider.provideZoneIds()

        then: 'a non empty set is returned'
        !availableIds.isEmpty()
    }

    def 'test get global zone id'() {
        given: 'a zone rules provider'
        ZoneId.availableZoneIds

        and: 'a local zone id'
        def tzId = 'Australia/Melbourne'

        when: 'requesting the corresponding global id'
        def globalId = TimeZoneRegistry.getGlobalZoneId(tzId)

        then: 'a valid id is returned'
        TimeZoneRegistry.ZONE_IDS.get(globalId.id) == tzId
    }

    def 'test get zone id'() {
        given: 'a zone rules provider'
        ZoneId.availableZoneIds

        and: 'a local zone id'
        def tzId = 'Australia/Melbourne'

        when: 'requesting a zone id using the mapped global id'
        def zoneId = TimeZoneRegistry.getGlobalZoneId(tzId)

        then: 'a valid zone id is returned'
        zoneId != null
    }
}
