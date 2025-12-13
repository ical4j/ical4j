package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.Instant
import java.time.ZoneOffset
import java.time.zone.ZoneRules

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
}
