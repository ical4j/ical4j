package net.fortuna.ical4j.model

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.*

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
                .inDaylightTime(java.util.Date.from(localInstant))

        where:
        tzId                    | dayOfYear     | expectedOffset
        'UTC'                   | 1             | ZoneOffset.UTC
        'Australia/Melbourne'   | 1             | ZoneOffset.ofHours(11)
        'Australia/Melbourne'   | 180           | ZoneOffset.ofHours(10)
        'Europe/Lisbon'         | 1             | ZoneOffset.UTC
        'Europe/London'         | 1             | ZoneOffset.UTC
        'Europe/Madrid'         | 1             | ZoneOffset.ofHours(1)
        'America/Los_Angeles'   | 1             | ZoneOffset.ofHours(-8)
        'America/Los_Angeles'   | 180           | ZoneOffset.ofHours(-7)
    }

    @Unroll
    def 'verify date in daylight time for timezone: #date #timezone'() {
        expect: 'specified date is in daylight time'
        def tz = timeZoneRegistry.getTimeZone(timezone)
        def zonerules = new ZoneRulesBuilder().vTimeZone(tz.getVTimeZone()).build()

        def instant = Instant.from(TemporalAdapter.parse(date).toLocalTime())
        zonerules.isDaylightSavings(instant) == inDaylightTime

        and:
        zonerules.getDaylightSavings(instant) == Duration.parse(savings)

        where:
        date				| timezone					| inDaylightTime    | savings
        '20110328T110000'	| 'America/Los_Angeles'		| true              | 'PT1H'
        '20231214T170000'	| 'America/Los_Angeles'		| false             | 'PT0S'
        '20110328T110000'	| 'Australia/Melbourne'		| true              | 'PT1H'
        '20110228T110000'	| 'Europe/London'		    | false             | 'PT0S'
        '20231115T083000'	| 'America/Sao_Paulo'		| false             | 'PT0S'
        '20271101T140000'	| 'Australia/Sydney'		| true              | 'PT1H'
        '20270901T140000'	| 'Australia/Sydney'		| false             | 'PT0S'
    }

    def 'test zoneid offsets'() {
        given: 'a zone rules definition'
        def zonerules = new ZoneRulesBuilder()
                .vTimeZone(timeZoneRegistry.getTimeZone(tzId).getVTimeZone()).build()

        expect: 'offset matches expected for given date'
        zonerules.getOffset(localDate) == expectedOffset

        where:
        tzId                    | localDate                                     | expectedOffset
        'America/Los_Angeles'   | LocalDateTime.of(2023, 12, 14, 17, 0, 0, 0)   | ZoneOffset.ofHours(-8)
    }

    def 'test relaxed parsing of invalid tz definitions'() {

        given: 'relaxed parsing enabled'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

        when: 'an invalid tz def is parsed'
        def tzstring = '''BEGIN:VCALENDAR
BEGIN:VTIMEZONE
TZID:US/Central
BEGIN:STANDARD
DTSTART:20070101T000000Z
RRULE:FREQ=YEARLY;WKST=SU;BYMONTH=11;BYDAY=1SU
TZOFFSETFROM:-0500
TZOFFSETTO:-0600
END:STANDARD
BEGIN:DAYLIGHT
DTSTART:20070101T000000Z
RRULE:FREQ=YEARLY;WKST=SU;BYMONTH=3;BYDAY=1SU
TZOFFSETFROM:-0600
TZOFFSETTO:-0500
END:DAYLIGHT
END:VTIMEZONE
END:VCALENDAR'''

        def cal = new CalendarBuilder().build(new StringReader(tzstring))
        def vtimezone = cal.getComponent('VTIMEZONE').get()

        then:
        noExceptionThrown()
    }
}
