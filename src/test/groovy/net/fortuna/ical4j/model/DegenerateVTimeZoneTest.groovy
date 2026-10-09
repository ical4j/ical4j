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
import net.fortuna.ical4j.data.ParserException
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.TzId
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Degenerate VTIMEZONE definitions must not abort parsing of the whole calendar (issues #531, #847, #750).
 */
class DegenerateVTimeZoneTest extends Specification {

    /** Issue #531: a VTIMEZONE with neither STANDARD nor DAYLIGHT (seen by DAVx5). */
    static final String NO_OBSERVANCES = '''BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//Example Corp.//CalDAV Client//EN
BEGIN:VTIMEZONE
TZID:UTC
END:VTIMEZONE
BEGIN:VEVENT
DTSTAMP:20260529T095200Z
UID:bc295665-5b3b-11f1-9a52-d843aea66ff2
DTSTART;TZID=UTC:20260528T120000
END:VEVENT
END:VCALENDAR
'''

    /** Issues #847 / #750: observances without DTSTART (natuurhuisje.nl feed). */
    static final String OBSERVANCES_WITHOUT_DTSTART = '''BEGIN:VCALENDAR
VERSION:2.0
PRODID:Natuurhuisje.nl
CALSCALE:GREGORIAN
BEGIN:VTIMEZONE
TZID:UTC
X-LIC-LOCATION:UTC
BEGIN:STANDARD
TZOFFSETFROM:+0200
TZOFFSETTO:+0100
END:STANDARD
BEGIN:DAYLIGHT
TZOFFSETFROM:+0100
TZOFFSETTO:+0200
END:DAYLIGHT
END:VTIMEZONE
BEGIN:VEVENT
UID:NH-81456-915712-booking-block@natuurhuisje.nl
DTSTAMP:20260302T163001Z
SUMMARY:Boeking natuurhuisje (915712)
DTSTART:20250509T000000Z
DTEND:20250511T000000Z
STATUS:CONFIRMED
END:VEVENT
END:VCALENDAR
'''

    /** A well-formed definition, as a control. */
    static final String WELL_FORMED = '''BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//ical4j//degenerate vtimezone test//EN
BEGIN:VTIMEZONE
TZID:Europe/Paris
BEGIN:STANDARD
DTSTART:19701025T030000
RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU
TZOFFSETFROM:+0200
TZOFFSETTO:+0100
END:STANDARD
BEGIN:DAYLIGHT
DTSTART:19700329T020000
RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU
TZOFFSETFROM:+0100
TZOFFSETTO:+0200
END:DAYLIGHT
END:VTIMEZONE
BEGIN:VEVENT
UID:well-formed@ical4j
DTSTAMP:20260101T000000Z
DTSTART;TZID=Europe/Paris:20260715T100000
END:VEVENT
END:VCALENDAR
'''

    def cleanup() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
    }

    static DtStart<?> dtStart(Calendar calendar) {
        VEvent event = calendar.getComponent('VEVENT').get()
        event.getRequiredProperty('DTSTART')
    }

    def 'a VTIMEZONE without observances is retained but does not abort parsing (#531)'() {
        given: 'a registry in strict mode'
        def registry = new TimeZoneRegistryImpl()

        when: 'the calendar is built'
        def calendar = new CalendarBuilder(registry).build(new StringReader(NO_OBSERVANCES))

        then: 'the calendar builds and the event resolves TZID=UTC via the global zone id'
        ZonedDateTime start = dtStart(calendar).date as ZonedDateTime
        start.toLocalDateTime().toString() == '2026-05-28T12:00'
        start.offset == ZoneOffset.UTC
        start.zone == TimeZoneRegistry.getGlobalZoneId('UTC')

        and: 'the definition is kept, but no zone rules were built for it'
        registry.getTimeZone('UTC').getVTimeZone().getObservances().isEmpty()
        registry.getZoneRules().isEmpty()

        and: 'the calendar round-trips with the VTIMEZONE intact'
        calendar.toString().contains('BEGIN:VTIMEZONE\r\nTZID:UTC\r\nEND:VTIMEZONE')
    }

    def 'observances without DTSTART are tolerated under relaxed hints (#847, #750)'() {
        given: 'the relaxed hint is enabled'
        CompatibilityHints.setHintEnabled(hint, true)
        def registry = new TimeZoneRegistryImpl()

        when: 'the calendar is built'
        def calendar = new CalendarBuilder(registry).build(new StringReader(OBSERVANCES_WITHOUT_DTSTART))

        then: 'the calendar builds and the UTC event dates are unaffected'
        Instant.from(dtStart(calendar).date) == Instant.parse('2025-05-09T00:00:00Z')

        and: 'the definition is kept without zone rules'
        registry.getTimeZone('UTC').getVTimeZone().getObservances().size() == 2
        registry.getZoneRules().isEmpty()

        where:
        hint << [CompatibilityHints.KEY_RELAXED_PARSING, CompatibilityHints.KEY_RELAXED_VALIDATION]
    }

    def 'observances without DTSTART fail fast in strict mode with a message naming the TZID'() {
        when: 'the calendar is built with no relaxed hints'
        new CalendarBuilder(new TimeZoneRegistryImpl()).build(new StringReader(OBSERVANCES_WITHOUT_DTSTART))

        then: 'parsing fails, naming the offending definition and the underlying cause'
        def e = thrown(ParserException)
        e.message.contains('UTC')
        e.message.contains('DTSTART')
    }

    def 'registering a definition without observances directly keeps it without zone rules'() {
        given:
        def registry = new TimeZoneRegistryImpl()
        def vTimeZone = new VTimeZone()
        vTimeZone.add(new TzId('Custom/Empty'))

        when:
        registry.register(new TimeZone(vTimeZone))

        then:
        registry.getTimeZone('Custom/Empty').getVTimeZone() == vTimeZone
        registry.getZoneRules().isEmpty()
    }

    def 'a well-formed definition still builds zone rules'() {
        given:
        def registry = new TimeZoneRegistryImpl()

        when:
        def calendar = new CalendarBuilder(registry).build(new StringReader(WELL_FORMED))

        then:
        registry.getZoneRules().size() == 1
        (dtStart(calendar).date as ZonedDateTime).offset == ZoneOffset.ofHours(2)
    }
}
