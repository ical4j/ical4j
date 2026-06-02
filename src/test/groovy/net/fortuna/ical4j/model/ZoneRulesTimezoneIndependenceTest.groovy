package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Guards against {@link ZoneRulesBuilder} deriving different {@link java.time.zone.ZoneRules}
 * depending on the JVM default time zone.
 *
 * <p>The bug this guards: historical DST observances bounded by a UTC {@code UNTIL} (e.g.
 * Asia/Tokyo's expired 1948-1951 DST) were filtered by comparing a floating recurrence candidate
 * against the UTC {@code UNTIL} via {@code TimeZones.getDefault()}. As a result the zone resolved
 * to +09:00 when the default zone was UTC (CI) but +10:00 east of UTC (e.g. Australia).
 *
 * <p>{@code TemporalComparator.INSTANCE} captures the default zone once at class-load, so this
 * behaviour can only be exercised by starting the JVM in a non-UTC zone. The Gradle
 * {@code timezoneIndependenceTest} task runs this spec with {@code -Duser.timezone=Australia/Sydney}.
 */
class ZoneRulesTimezoneIndependenceTest extends Specification {

    def 'historical-DST zones resolve to their standard offset regardless of JVM default zone: #tzId'() {
        given: 'a registry timezone whose DST is entirely historical (expired UNTIL)'
        def registry = TimeZoneRegistryFactory.instance.createRegistry()
        def vtz = registry.getTimeZone(tzId).getVTimeZone()

        when: 'zone rules are built and queried for present-day offsets'
        def zonerules = new ZoneRulesBuilder().vTimeZone(vtz).build()

        then: 'the offset is the standard offset in both winter and summer, independent of default zone'
        zonerules.getOffset(LocalDateTime.of(2026, 1, 1, 0, 0)) == expectedOffset
        zonerules.getOffset(LocalDateTime.of(2026, 6, 29, 0, 0)) == expectedOffset

        where:
        tzId             | expectedOffset
        'Asia/Tokyo'     | ZoneOffset.ofHours(9)
        'Asia/Shanghai'  | ZoneOffset.ofHours(8)
    }
}
