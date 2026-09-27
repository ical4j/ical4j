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

import spock.lang.Specification

import java.nio.file.Files

/**
 * Global zone resolution: the bundled-definition zone rules provider is registered lazily and programmatically,
 * never through ServiceLoader (see the lazy-default-zone-rules-provider OpenSpec change, issue #808).
 */
class GlobalZoneResolutionTest extends Specification {

    /** Runs a JVM with the current test runtime classpath and returns [exitCode, combined output]. */
    static List fork(List<String> args) {
        def java = new File(System.getProperty('java.home'), 'bin/java').path
        def process = new ProcessBuilder([java, '-cp', System.getProperty('java.class.path')] + args)
                .redirectErrorStream(true).start()
        def output = process.inputStream.text
        [process.waitFor(), output]
    }

    def 'the JVM starts with a custom JFR repository while iCal4j is on the classpath'() {
        given:
        def repository = Files.createTempDirectory('jfr-repo').toFile()

        when:
        def (exitCode, output) = fork(["-XX:FlightRecorderOptions=repository=${repository.path}".toString(), '-version'])

        then:
        !output.contains('ServiceConfigurationError')
        exitCode == 0

        cleanup:
        repository.deleteDir()
    }

    def 'no ical4j zone ids are visible before the first global resolution'() {
        when:
        // timezone updates are disabled so the probe doesn't depend on the network
        def (exitCode, output) = fork(['-Dnet.fortuna.ical4j.timezone.update.enabled=false',
                                       GlobalZoneResolutionProbe.name])

        then:
        exitCode == 0
        output.contains('before=false')
        output.contains('resolved=ical4j~')
        output.contains('after=true')
    }

    def 'a provider instance answers from the map it was constructed with'() {
        given:
        def before = new HashMap(TimeZoneRegistry.ZONE_IDS)
        def ids = new HashMap<String, String>()

        when:
        def provider = new DefaultZoneRulesProvider(new TimeZoneLoader('zoneinfo/'), ids)

        then:
        !ids.isEmpty()
        provider.provideZoneIds() == ids.keySet()
        TimeZoneRegistry.ZONE_IDS == before
    }

    def 'a registration that fails publishes nothing'() {
        given:
        def published = [:]

        when: 'the provider cannot be constructed, as on a platform that blocks ZoneRulesProvider'
        def registered = GlobalZoneRules.register({ ids -> throw new NoSuchMethodError('blocked') }, published)

        then:
        !registered
        published.isEmpty()
    }

    def 'a successful registration publishes resolvable ids'() {
        given:
        def published = [:]

        when:
        def registered = GlobalZoneRules.register(
                { ids -> new DefaultZoneRulesProvider(new TimeZoneLoader('zoneinfo/'), ids) }, published)

        then:
        registered
        !published.isEmpty()

        and: 'every published id resolves through java.time'
        def (id, olson) = published.find { it.value == 'Europe/Paris' }.with { [it.key, it.value] }
        java.time.ZoneId.of(id).rules.getOffset(java.time.Instant.parse('2026-07-01T12:00:00Z')) ==
                java.time.ZoneOffset.ofHours(2)
    }

    def 'global resolution is backed by the bundled definitions'() {
        when:
        def zone = TimeZoneRegistry.getGlobalZoneId('America/Los_Angeles')

        then:
        zone.id.startsWith('ical4j~')
        zone.rules.getOffset(java.time.Instant.parse('2026-07-01T12:00:00Z')) == java.time.ZoneOffset.ofHours(-7)
        zone.rules.getOffset(java.time.Instant.parse('2026-01-01T12:00:00Z')) == java.time.ZoneOffset.ofHours(-8)
    }

    def 'aliases still resolve through global resolution'() {
        expect:
        TimeZoneRegistry.getGlobalZoneId('Romance Standard Time') == TimeZoneRegistry.getGlobalZoneId('Europe/Paris')
    }

    def 'global resolution works when iCal4j is loaded by a non-system class loader'() {
        given: 'an isolated copy of iCal4j and its dependencies, as in a webapp, not delegating to the app loader'
        def urls = System.getProperty('java.class.path').split(File.pathSeparator)
                .collect { new File(it).toURI().toURL() } as URL[]
        def loader = new URLClassLoader(urls, ClassLoader.platformClassLoader)
        def registry = loader.loadClass('net.fortuna.ical4j.model.TimeZoneRegistry')

        expect: 'it is a distinct copy'
        !registry.is(TimeZoneRegistry)

        when:
        java.time.ZoneId zone = registry.getMethod('getGlobalZoneId', String).invoke(null, 'Australia/Melbourne')

        then: 'the bundled definitions are registered for that copy too'
        zone.id.startsWith('ical4j~')
        zone.rules.getOffset(java.time.Instant.parse('2026-07-01T12:00:00Z')) == java.time.ZoneOffset.ofHours(10)

        when: 'the second copy also parses a calendar with a VTIMEZONE, registering it via its own provider'
        def builder = loader.loadClass('net.fortuna.ical4j.data.CalendarBuilder').getConstructor().newInstance()
        def calendar = builder.build(new StringReader('''BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//ical4j//test//EN
BEGIN:VTIMEZONE
TZID:Test/Fixed
BEGIN:STANDARD
DTSTART:19700101T000000
TZOFFSETFROM:+0300
TZOFFSETTO:+0300
END:STANDARD
END:VTIMEZONE
BEGIN:VEVENT
UID:copy@ical4j
DTSTAMP:20260101T000000Z
DTSTART;TZID=Test/Fixed:20260315T100000
END:VEVENT
END:VCALENDAR
'''))
        def dtStart = calendar.getComponent('VEVENT').get().getRequiredProperty('DTSTART').date

        then: 'its synthetic-id provider registered alongside the first copy\'s'
        loader.loadClass('net.fortuna.ical4j.model.ZoneRulesProviderImpl').getMethod('isAvailable').invoke(null)
        dtStart.offset == java.time.ZoneOffset.ofHours(3)
    }

    def 'two synthetic-id providers allocate disjoint ids'() {
        when:
        def first = new ZoneRulesProviderImpl().provideZoneIds()
        def second = new ZoneRulesProviderImpl().provideZoneIds()

        then:
        first.disjoint(second)
        (first + second).every { it ==~ /ical4j-local-[0-9a-f]+-\d+/ }
    }
}
