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
        'Pacific/Tahiti'   | 1           | ZoneOffset.ofHours(-10)
        'Pacific/Midway'   | 1           | ZoneOffset.ofHours(-11)
        'Asia/Tokyo'       | 1           | ZoneOffset.ofHours(9)
        'Asia/Tokyo'       | 180         | ZoneOffset.ofHours(9)
        'Asia/Shanghai'    | 180         | ZoneOffset.ofHours(8)
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

    def 'build DST transitions from non-recurring (single date-time) observances'() {
        given: 'a VTIMEZONE whose DAYLIGHT/STANDARD observances are one-off transitions (no RRULE)'
        // This is the form emitted by some clients (e.g. Apple/iCloud) - see issue #793.
        def tzstring = '''\
                BEGIN:VCALENDAR
                VERSION:2.0
                BEGIN:VTIMEZONE
                TZID:America/New_York
                X-LIC-LOCATION:America/New_York
                BEGIN:DAYLIGHT
                DTSTART;VALUE=DATE-TIME:20250309T070000
                TZNAME:EDT
                TZOFFSETFROM:-0500
                TZOFFSETTO:-0400
                END:DAYLIGHT
                BEGIN:STANDARD
                DTSTART;VALUE=DATE-TIME:20251102T060000
                TZNAME:EST
                TZOFFSETFROM:-0400
                TZOFFSETTO:-0500
                END:STANDARD
                END:VTIMEZONE
                END:VCALENDAR
                '''.stripIndent()
        def vtimezone = new CalendarBuilder().build(new StringReader(tzstring)).getComponent('VTIMEZONE').get()

        when: 'zone rules are built from the VTIMEZONE'
        def zonerules = new ZoneRulesBuilder().vTimeZone(vtimezone).build()

        then: 'the one-off DST transitions are applied rather than collapsing to a single fixed offset'
        // before the fix these observances produced no transitions, so summer incorrectly returned EST (-05:00)
        zonerules.getOffset(LocalDateTime.of(2025, 2, 21, 14, 30)) == ZoneOffset.ofHours(-5)
        zonerules.getOffset(LocalDateTime.of(2025, 7, 15, 14, 30)) == ZoneOffset.ofHours(-4)
    }

    def 'parse non-DST VTIMEZONE with BYMONTH-only rule (Apple Asia/Tokyo)'() {
        given: 'a calendar with an Apple-style non-DST Asia/Tokyo VTIMEZONE and an event'
        def cal = '''BEGIN:VCALENDAR
VERSION:2.0
CALSCALE:GREGORIAN
BEGIN:VTIMEZONE
TZID:Asia/Tokyo
BEGIN:STANDARD
DTSTART:20000101T000000
RRULE:FREQ=YEARLY;BYMONTH=1
TZNAME:JST
TZOFFSETFROM:+0900
TZOFFSETTO:+0900
END:STANDARD
END:VTIMEZONE
BEGIN:VEVENT
UID:325B6458-1654-4318-9050-F0BEF24518B6
DTSTART;TZID=Asia/Tokyo;VALUE=DATE-TIME:20260413T130000
DTEND;TZID=Asia/Tokyo;VALUE=DATE-TIME:20260413T133000
DTSTAMP;VALUE=DATE-TIME:20260530T140853Z
RRULE:FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=MO;UNTIL=20990331T040000Z
END:VEVENT
END:VCALENDAR'''

        when: 'the calendar is parsed'
        def calendar = new CalendarBuilder().build(new StringReader(cal))

        then: 'no exception is thrown'
        noExceptionThrown()

        when: 'zone rules are built from the embedded VTIMEZONE'
        def vtimezone = calendar.getComponent('VTIMEZONE').get()
        def zonerules = new ZoneRulesBuilder().vTimeZone(vtimezone).build()

        then: 'the zone resolves to a fixed +09:00 offset'
        zonerules.getOffset(LocalDateTime.of(2026, 4, 13, 13, 0)) == ZoneOffset.ofHours(9)

        and: 'no transition rule is generated for the no-effect (from == to) observance'
        zonerules.getTransitionRules().isEmpty()
    }

    def 'BYMONTH-only observance yields a fixed-date transition rule derived from DTSTART'() {
        given: 'an alternating zone whose STANDARD return uses a BYMONTH-only (fixed-date) rule'
        def cal = '''BEGIN:VCALENDAR
BEGIN:VTIMEZONE
TZID:Test/FixedReturn
BEGIN:DAYLIGHT
DTSTART:20000326T020000
RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU
TZOFFSETFROM:+0100
TZOFFSETTO:+0200
END:DAYLIGHT
BEGIN:STANDARD
DTSTART:20001015T030000
RRULE:FREQ=YEARLY;BYMONTH=10
TZOFFSETFROM:+0200
TZOFFSETTO:+0100
END:STANDARD
END:VTIMEZONE
END:VCALENDAR'''

        when: 'zone rules are built'
        def calendar = new CalendarBuilder().build(new StringReader(cal))
        def zonerules = new ZoneRulesBuilder().vTimeZone(calendar.getComponent('VTIMEZONE').get()).build()
        def rules = zonerules.getTransitionRules()

        then: 'the October (BYMONTH-only) rule uses the DTSTART day-of-month with no weekday constraint'
        def october = rules.find { it.month == java.time.Month.OCTOBER }
        october != null
        october.dayOfMonthIndicator == 15
        october.dayOfWeek == null

        and: 'the March rule (BYDAY=-1SU) is unchanged and keeps its weekday constraint'
        def march = rules.find { it.month == java.time.Month.MARCH }
        march != null
        march.dayOfWeek == DayOfWeek.SUNDAY
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
