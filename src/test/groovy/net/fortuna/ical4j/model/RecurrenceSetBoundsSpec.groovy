package net.fortuna.ical4j.model

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.component.VEvent
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Bounds applied by {@link RecurrenceSet.Builder} (via {@link Component#calculateRecurrenceSet}):
 * period start inclusive, period end exclusive, zero-length period = point query. See issue #603.
 */
class RecurrenceSetBoundsSpec extends Specification {

    static final String DAILY_UTC = '''BEGIN:VCALENDAR\r
VERSION:2.0\r
PRODID:-//ical4j//test//EN\r
BEGIN:VEVENT\r
UID:bounds-1\r
DTSTAMP:20240101T000000Z\r
DTSTART:20240101T000000Z\r
DTEND:20240101T010000Z\r
RRULE:FREQ=DAILY\r
SUMMARY:daily at midnight\r
END:VEVENT\r
END:VCALENDAR\r
'''

    static final String RDATE_UTC = '''BEGIN:VCALENDAR\r
VERSION:2.0\r
PRODID:-//ical4j//test//EN\r
BEGIN:VEVENT\r
UID:bounds-2\r
DTSTAMP:20240101T000000Z\r
DTSTART:20240101T000000Z\r
DTEND:20240101T010000Z\r
RDATE:20240102T000000Z,20240103T000000Z\r
SUMMARY:rdate at midnight\r
END:VEVENT\r
END:VCALENDAR\r
'''

    static final String DAILY_DATE = '''BEGIN:VCALENDAR\r
VERSION:2.0\r
PRODID:-//ical4j//test//EN\r
BEGIN:VEVENT\r
UID:bounds-3\r
DTSTAMP:20240101T000000Z\r
DTSTART;VALUE=DATE:20240101\r
RRULE:FREQ=DAILY\r
SUMMARY:all day daily\r
END:VEVENT\r
END:VCALENDAR\r
'''

    /** daily event with no DTEND: a zero-length occurrence (issue #82) */
    static final String DAILY_ZERO_LENGTH = '''BEGIN:VCALENDAR\r
VERSION:2.0\r
PRODID:-//ical4j//test//EN\r
BEGIN:VEVENT\r
UID:bounds-4\r
DTSTAMP:20240101T000000Z\r
DTSTART:20240101T000000Z\r
RRULE:FREQ=DAILY\r
SUMMARY:zero length daily\r
END:VEVENT\r
END:VCALENDAR\r
'''

    static VEvent event(String ics) {
        new CalendarBuilder().build(new StringReader(ics)).getComponent('VEVENT').get() as VEvent
    }

    /** occurrence starts normalised to Instant (parsed UTC values are OffsetDateTime) or LocalDate */
    static List starts(Set<Period> periods) {
        periods.collect { it.start instanceof LocalDate ? it.start : Instant.from(it.start) }
                .sort { a, b -> TemporalComparator.INSTANCE.compare(a, b) }
    }

    def 'RRULE instance starting at the period end is excluded'() {
        when:
        def periods = event(DAILY_UTC).calculateRecurrenceSet(
                new Period<>(Instant.parse('2024-01-01T00:00:00Z'), Instant.parse('2024-01-03T00:00:00Z')))

        then:
        starts(periods) == [Instant.parse('2024-01-01T00:00:00Z'), Instant.parse('2024-01-02T00:00:00Z')]
    }

    def 'RDATE date starting at the period end is excluded'() {
        when:
        def periods = event(RDATE_UTC).calculateRecurrenceSet(
                new Period<>(Instant.parse('2024-01-01T00:00:00Z'), Instant.parse('2024-01-03T00:00:00Z')))

        then:
        starts(periods) == [Instant.parse('2024-01-01T00:00:00Z'), Instant.parse('2024-01-02T00:00:00Z')]
    }

    def 'date-valued daily event queried for a single day yields one instance'() {
        when:
        def periods = event(DAILY_DATE).calculateRecurrenceSet(
                new Period<>(LocalDate.of(2024, 1, 2), LocalDate.of(2024, 1, 3)))

        then:
        starts(periods) == [LocalDate.of(2024, 1, 2)]
    }

    def 'instance starting at the period start is included'() {
        when:
        def periods = event(DAILY_UTC).calculateRecurrenceSet(
                new Period<>(Instant.parse('2024-01-02T00:00:00Z'), Instant.parse('2024-01-03T00:00:00Z')))

        then:
        starts(periods) == [Instant.parse('2024-01-02T00:00:00Z')]
    }

    def 'instance ending exactly at the period start is excluded'() {
        when:
        def periods = event(DAILY_UTC).calculateRecurrenceSet(
                new Period<>(Instant.parse('2024-01-02T01:00:00Z'), Instant.parse('2024-01-02T12:00:00Z')))

        then:
        periods.isEmpty()
    }

    def 'instance overlapping the period start is included'() {
        when:
        def periods = event(DAILY_UTC).calculateRecurrenceSet(
                new Period<>(Instant.parse('2024-01-02T00:30:00Z'), Instant.parse('2024-01-03T00:00:00Z')))

        then:
        starts(periods) == [Instant.parse('2024-01-02T00:00:00Z')]
    }

    def 'zero-length occurrence is included when its instant lies in the half-open window'() {
        when:
        def periods = event(DAILY_ZERO_LENGTH).calculateRecurrenceSet(
                new Period<>(Instant.parse(from), Instant.parse(to)))

        then:
        starts(periods) == expected.collect { Instant.parse(it) }

        where:
        from                   | to                     | expected
        // at exactly bounds.start -> included; at exactly bounds.end -> excluded
        '2024-01-02T00:00:00Z' | '2024-01-03T00:00:00Z' | ['2024-01-02T00:00:00Z']
        // strictly inside -> included
        '2024-01-01T12:00:00Z' | '2024-01-02T12:00:00Z' | ['2024-01-02T00:00:00Z']
        // window ends exactly at the occurrence -> excluded
        '2024-01-01T12:00:00Z' | '2024-01-02T00:00:00Z' | []
    }

    def 'zero-length period is a point query matching an instance at that instant'() {
        given:
        def event = event(DAILY_UTC)
        // getOccurrence matches with equals(), so query with the event's own temporal type
        def fifth = event.getDateTimeStart().date.plus(4, ChronoUnit.DAYS)

        expect:
        Instant.from(event.getOccurrence(fifth)?.getRecurrenceId()?.date) == Instant.parse('2024-01-05T00:00:00Z')
        starts(event.calculateRecurrenceSet(
                new Period<>(Instant.parse('2024-01-05T00:00:00Z'), Instant.parse('2024-01-05T00:00:00Z')))) ==
                [Instant.parse('2024-01-05T00:00:00Z')]
    }

    def 'zero-length period between instances is empty'() {
        when: 'a point query at 02:00, after the 00:00-01:00 instance has ended'
        def periods = event(DAILY_UTC).calculateRecurrenceSet(
                new Period<>(Instant.parse('2024-01-05T02:00:00Z'), Instant.parse('2024-01-05T02:00:00Z')))

        then:
        periods.isEmpty()
    }
}
