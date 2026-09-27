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
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification

import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

class TimeZoneRegistryFallbackTest extends Specification {

    /** A Europe/Paris definition whose rules deliberately diverge from tzdb: a fixed +05:00. */
    static final String DIVERGENT_PARIS = '''BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//ical4j//fallback test//EN
BEGIN:VTIMEZONE
TZID:Europe/Paris
BEGIN:STANDARD
DTSTART:19700101T000000
TZOFFSETFROM:+0500
TZOFFSETTO:+0500
END:STANDARD
END:VTIMEZONE
BEGIN:VEVENT
UID:divergent@ical4j
DTSTAMP:20260101T000000Z
DTSTART;TZID=Europe/Paris:20260715T100000
END:VEVENT
END:VCALENDAR
'''

    static ZonedDateTime dtStart(Calendar calendar) {
        VEvent event = calendar.getComponent('VEVENT').get()
        DtStart<ZonedDateTime> dtStart = event.getRequiredProperty('DTSTART')
        dtStart.date
    }

    def 'with the provider available, the calendar\'s own VTIMEZONE rules apply'() {
        given: 'the default registry on a standard JVM'
        def registry = new TimeZoneRegistryImpl()

        when:
        def calendar = new CalendarBuilder(registry).build(new StringReader(DIVERGENT_PARIS))

        then: 'the fixed +05:00 from the VTIMEZONE is used, not tzdb'
        dtStart(calendar).offset == ZoneOffset.ofHours(5)

        and: 'one set of rules was registered for the definition'
        registry.zoneRules.size() == 1
    }

    /** A tzdb-faithful Europe/Paris definition. */
    static final String PARIS_VTIMEZONE = '''BEGIN:VTIMEZONE
TZID:Europe/Paris
BEGIN:DAYLIGHT
DTSTART:19810329T020000
RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU
TZOFFSETFROM:+0100
TZOFFSETTO:+0200
END:DAYLIGHT
BEGIN:STANDARD
DTSTART:19961027T030000
RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU
TZOFFSETFROM:+0200
TZOFFSETTO:+0100
END:STANDARD
END:VTIMEZONE
'''

    static String calendar(String vTimeZone, String tzId, String localDateTime = '20260315T100000') {
        """BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//ical4j//fallback test//EN
${vTimeZone}BEGIN:VEVENT
UID:fallback@ical4j
DTSTAMP:20260101T000000Z
DTSTART;TZID=${tzId}:${localDateTime}
END:VEVENT
END:VCALENDAR
"""
    }

    /** A registry in fallback mode, as on a platform where the zone rules provider cannot be installed. */
    static TimeZoneRegistryImpl fallbackRegistry() {
        new TimeZoneRegistryImpl('zoneinfo/', false, false)
    }

    def cleanup() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
    }

    def 'the provider installs normally on a standard JVM'() {
        expect:
        ZoneRulesProviderImpl.isAvailable()
        ZoneRulesProviderImpl.getInstance().get().is(ZoneRulesProviderImpl.INSTANCE)
    }

    def 'fallback mode keeps VTIMEZONE definitions but registers no zone rules'() {
        given:
        def registry = fallbackRegistry()

        when:
        new CalendarBuilder(registry).build(new StringReader(calendar(PARIS_VTIMEZONE, 'Europe/Paris')))

        then: 'the definition is kept'
        registry.getTimeZone('Europe/Paris').vTimeZone.getRequiredProperty('TZID').value == 'Europe/Paris'

        and: 'no synthetic zone id or rules were created'
        registry.zoneRules.isEmpty()
        registry.getTzId('ical4j-local-0') == null
    }

    def 'fallback mode resolves an Olson TZID through global resolution'() {
        given:
        def registry = fallbackRegistry()
        def global = TimeZoneRegistry.getGlobalZoneId('Europe/Paris')

        when:
        def cal = new CalendarBuilder(registry).build(new StringReader(calendar(PARIS_VTIMEZONE, 'Europe/Paris')))

        then:
        dtStart(cal).zone == global
        dtStart(cal).toLocalDateTime() == LocalDateTime.of(2026, 3, 15, 10, 0)
        dtStart(cal).offset == ZoneOffset.ofHours(1)

        and: 'a direct registry lookup agrees'
        registry.getZoneId('Europe/Paris') == global
    }

    def 'fallback mode maps a Windows zone name through the alias tables'() {
        when:
        def cal = new CalendarBuilder(fallbackRegistry()).build(
                new StringReader(calendar('', 'Romance Standard Time')))

        then:
        dtStart(cal).zone == TimeZoneRegistry.getGlobalZoneId('Europe/Paris')
    }

    def 'fallback mode applies platform rules, not a divergent VTIMEZONE'() {
        when:
        def cal = new CalendarBuilder(fallbackRegistry()).build(new StringReader(DIVERGENT_PARIS))

        then: 'Paris summer time, not the definition\'s fixed +05:00'
        dtStart(cal).offset == ZoneOffset.ofHours(2)
    }

    static final String OFFICE_VTIMEZONE = '''BEGIN:VTIMEZONE
TZID:My Office Time
BEGIN:STANDARD
DTSTART:19700101T000000
TZOFFSETFROM:+0300
TZOFFSETTO:+0300
END:STANDARD
END:VTIMEZONE
'''

    def 'fallback mode: a TZID with no platform equivalent fails in strict mode'() {
        given:
        def cal = new CalendarBuilder(fallbackRegistry()).build(
                new StringReader(calendar(OFFICE_VTIMEZONE, 'My Office Time')))

        when:
        dtStart(cal)

        then:
        thrown(DateTimeException)
    }

    def 'fallback mode: a TZID with no platform equivalent is ignored under relaxed validation'() {
        given:
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true)
        def cal = new CalendarBuilder(fallbackRegistry()).build(
                new StringReader(calendar(OFFICE_VTIMEZONE, 'My Office Time')))

        when:
        VEvent event = cal.getComponent('VEVENT').get()
        def date = event.getRequiredProperty('DTSTART').date

        then: 'parsed without the TZID'
        date == LocalDateTime.of(2026, 3, 15, 10, 0)
    }

    def 'fallback mode round-trips VTIMEZONE and TZID parameters'() {
        when:
        def cal = new CalendarBuilder(fallbackRegistry()).build(new StringReader(calendar(PARIS_VTIMEZONE, 'Europe/Paris')))
        def output = cal.toString()

        then:
        output.contains('BEGIN:VTIMEZONE\r\nTZID:Europe/Paris\r\n')
        output.contains('DTSTART;TZID=Europe/Paris:20260315T100000\r\n')
    }
}
