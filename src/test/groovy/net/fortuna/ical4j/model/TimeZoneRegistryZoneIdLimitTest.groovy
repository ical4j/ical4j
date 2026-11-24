/*
 *  Copyright (c) 2026, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */


package net.fortuna.ical4j.model

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.component.Observance
import net.fortuna.ical4j.model.component.Standard
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.TzId
import net.fortuna.ical4j.model.property.TzOffsetFrom
import net.fortuna.ical4j.model.property.TzOffsetTo
import spock.lang.Specification

import java.time.DateTimeException
import java.time.Instant
import java.time.ZoneOffset

class TimeZoneRegistryZoneIdLimitTest extends Specification {

    private static String vtimezone(String tzId, String offset = '+0100') {
        ['BEGIN:VTIMEZONE', "TZID:$tzId", 'BEGIN:STANDARD', 'DTSTART:19700101T000000',
         "TZOFFSETFROM:$offset", "TZOFFSETTO:$offset", 'END:STANDARD', 'END:VTIMEZONE'].join('\r\n')
    }

    private static String calendar(List<String> vtimezones) {
        (['BEGIN:VCALENDAR', 'VERSION:2.0', 'PRODID:-//ical4j//test//EN'] + vtimezones
                + ['END:VCALENDAR', '']).join('\r\n')
    }

    private static ZoneRulesProviderImpl.ZoneIdPool pool() {
        ZoneRulesProviderImpl.getInstance().orElseThrow().getZoneIdPool()
    }

    /** Return a registry's zone ids to the shared pool, so other tests in this JVM are not affected. */
    private static void release(TimeZoneRegistry registry) {
        new ArrayList<>(registry.getZoneRules().keySet()).each { pool().release(it) }
    }

    def 'a repeated TZID reuses its zone id instead of taking another from the shared pool'() {
        given: 'more copies of one VTIMEZONE than the shared pool has zone ids'
        def available = pool().availableZoneIds()
        def ics = calendar((0..available).collect { vtimezone('X-Fuzz/1') })
        def builder = new CalendarBuilder()

        when: 'the calendar is parsed'
        builder.build(new StringReader(ics))

        then: 'it parses, the TZID resolves, and only one zone id was taken'
        builder.registry.getZoneId('X-Fuzz/1') != null
        builder.registry.getZoneRules().size() == 1
        pool().availableZoneIds() == available - 1

        and: 'another calendar still parses while the first registry is in use'
        new CalendarBuilder().build(new StringReader(calendar([vtimezone('X-Other', '+0200')])))

        cleanup:
        release(builder.registry)
    }

    def 'registering a TZID again replaces its rules under the same zone id'() {
        given: 'a registry with a definition'
        def registry = TimeZoneRegistryFactory.instance.createRegistry()
        registry.register(new TimeZone(new CalendarBuilder().build(new StringReader(
                calendar([vtimezone('X-Replaced', '+0100')]))).getComponent(Component.VTIMEZONE).get()))
        def zoneId = registry.getZoneId('X-Replaced')

        when: 'a different definition with the same TZID is registered'
        registry.register(new TimeZone(new CalendarBuilder().build(new StringReader(
                calendar([vtimezone('X-Replaced', '+0200')]))).getComponent(Component.VTIMEZONE).get()))

        then: 'the zone id is unchanged and the latest definition applies'
        registry.getZoneId('X-Replaced') == zoneId
        registry.getZoneRules().size() == 1
        registry.getZoneId('X-Replaced').rules.getOffset(Instant.now()) == ZoneOffset.ofHours(2)

        cleanup:
        release(registry)
    }

    def 'a registry takes no more zone ids than its limit'() {
        given: 'a calendar with more distinct TZIDs than the per-registry limit'
        def limit = TimeZoneRegistryImpl.DEFAULT_MAX_ZONE_IDS
        def available = pool().availableZoneIds()
        def ics = calendar((0..<(limit + 20)).collect { vtimezone("X-Fuzz/$it") })
        def builder = new CalendarBuilder()

        when: 'the calendar is parsed'
        def parsed = builder.build(new StringReader(ics))

        then: 'it parses, every definition is retained, and only the limit was taken from the pool'
        parsed.getComponents(Component.VTIMEZONE).size() == limit + 20
        builder.registry.getTimeZone("X-Fuzz/${limit + 19}") != null
        builder.registry.getZoneRules().size() == limit
        pool().availableZoneIds() == available - limit

        and: 'definitions within the limit have zone rules, later ones are retained without them'
        builder.registry.getZoneId("X-Fuzz/${limit - 1}") != null

        when:
        builder.registry.getZoneId("X-Fuzz/$limit")

        then:
        thrown(DateTimeException)

        cleanup:
        release(builder.registry)
    }

    def 'an empty shared pool does not fail registration'() {
        given: 'a registry allowed more zone ids than the shared pool has left'
        System.setProperty(TimeZoneRegistryImpl.KEY_MAX_ZONE_IDS, Integer.MAX_VALUE as String)
        def registry = TimeZoneRegistryFactory.instance.createRegistry()
        // allocate() also returns ids of registries collected since, so leave a margin beyond what is free now..
        pool().cleanup()
        def count = pool().availableZoneIds() + 50
        // built directly, as parsing them would register each one (and take a zone id) in its builder's registry..
        def definitions = (0..<count).collect {
            new TimeZone((VTimeZone) new VTimeZone(new PropertyList([new TzId("X-Drain/$it")]),
                    new ComponentList([(Observance) new Standard().withProperty(new DtStart<>('19700101T000000'))
                            .withProperty(new TzOffsetFrom('+0100')).withProperty(new TzOffsetTo('+0100'))
                            .getFluentTarget()])))
        }

        when: 'more definitions are registered than the pool has zone ids'
        definitions.each { registry.register(it) }

        then: 'registration does not throw, the pool ran out, and every definition is retained'
        noExceptionThrown()
        pool().availableZoneIds() == 0
        registry.getZoneRules().size() < count
        (0..<count).every { registry.getTimeZone("X-Drain/$it") != null }

        cleanup:
        System.clearProperty(TimeZoneRegistryImpl.KEY_MAX_ZONE_IDS)
        release(registry)
    }
}
