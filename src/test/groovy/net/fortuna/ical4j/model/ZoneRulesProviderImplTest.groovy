package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.Instant
import java.time.ZoneOffset
import java.time.zone.ZoneRules
import java.time.zone.ZoneRulesProvider

class ZoneRulesProviderImplTest extends Specification {

    def 'verify zone rules'() {
        given: 'a zone rules provider instance'
        TimeZoneRegistry registry = TimeZoneRegistryFactory.instance.createRegistry()
        registry.register(registry.getTimeZone("Australia/Melbourne"))
        ZoneRulesProviderImpl zoneRulesProvider = [registry]

        and: 'that is registered'
        ZoneRulesProvider.registerProvider(zoneRulesProvider)

        when: 'zone rules are requested'
        String tzId = registry.getZoneId("Australia/Melbourne")
        ZoneRules zoneRules = zoneRulesProvider.provideRules(tzId, false)

        then: 'an appropriate rules instance is provided'
        zoneRules != null && zoneRules.getStandardOffset(Instant.now()) == ZoneOffset.ofHours(10)
    }
}
