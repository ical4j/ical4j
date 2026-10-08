package net.fortuna.ical4j.model

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtStamp
import net.fortuna.ical4j.model.property.Uid
import spock.lang.Shared
import spock.lang.Specification

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoField

/**
 * Created by fortuna on 21/07/2017.
 */
class ComponentGroupTest extends Specification {

    @Shared
    ContentBuilder builder = []

    Uid uid

    VEvent event, rev1, rev2, rev3

    def setup() {
        uid = builder.uid('1')

        event = builder.vevent {
            uid(uid)
            dtstart('20101113', parameters: parameters() { value('DATE') })
            dtend('20101114', parameters: parameters() { value('DATE') })
            rrule('FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA')
        }

        rev1 = builder.vevent {
            uid(uid)
            sequence('1')
            dtstart('20101113', parameters: parameters() { value('DATE') })
            dtend('20101114', parameters: parameters() { value('DATE') })
            rrule('FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA')
        }

        rev2 = builder.vevent {
            uid(uid)
            sequence('2')
            dtstamp(new DtStamp(Instant.now()))
            dtstart('20101113', parameters: parameters() { value('DATE') })
            dtend('20101114', parameters: parameters() { value('DATE') })
            rrule('FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA')
        }

        rev3 = builder.vevent {
            uid(uid)
            sequence('3')
            recurrenceid('20101129', parameters: parameters() { value('DATE') })
            dtstamp(new DtStamp(Instant.now()))
            dtstart('20101130', parameters: parameters() { value('DATE') })
            dtend('20101201', parameters: parameters() { value('DATE') })
        }
    }

    def "GetRevisions"() {
        given: 'an event with 2 revisions'
        def components = new ComponentList<VEvent>([event, rev1])

        when: 'retrieving revisions from component group'
        def revisions = new ComponentGroup(components.all, uid).revisions

        then: 'the expected revisions are returned'
        revisions == [event, rev1]
    }

    def "GetLatestRevision"() {
        given: 'an event with 3 revisions'
        def components = new ComponentList<VEvent>([event, rev1, rev2])

        when: 'retrieving the latest revision from component group'
        def revision = new ComponentGroup(components.all, uid).latestRevision

        then: 'the expected revision is returned'
        revision == rev2
    }

    def "CalculateRecurrenceSet"() {
        given: 'an event with a revision'
        def components = new ComponentList<VEvent>([event, rev1])

        when: 'recurrence instances are calculated'
        Period period = Period.parse('20101113/P3W')
        def recurrences = new ComponentGroup(components.all, uid).calculateRecurrenceSet(period)

        then: 'the expected number of recurrences are returned'
        recurrences as Set == event.calculateRecurrenceSet(period)
    }

    def "CalculateRecurrenceSetWithException"() {
        given: 'an event with 2 revisions and instance override'
        def components = new ComponentList<VEvent>([event, rev1, rev2, rev3])

        when: 'recurrence instances are calculated'
        Period period = Period.parse '20101113/P3W'
        def recurrences = new ComponentGroup(components.all, uid).calculateRecurrenceSet(period)

        then: 'the overridden instance is replaced by the override, so the count is unchanged'
        recurrences.size() == event.calculateRecurrenceSet(period).size()

        and: 'the instance named by RECURRENCE-ID is removed'
        !recurrences.any { it.start == LocalDate.of(2010, 11, 29) }

        and: 'the rescheduled override occurrence is present and linked to the override (alongside the master instance that already falls on that Tuesday)'
        def override = recurrences.find { it.component == rev3 }
        override != null
        override.start == LocalDate.of(2010, 11, 30)
        override.duration == java.time.Period.ofDays(1)
    }

    // --- RECURRENCE-ID override handling (issue #510, #472) ---

    static final String MASTER_UTC = '''BEGIN:VEVENT\r
UID:event-21186\r
DTSTAMP:20210719T141546Z\r
DTSTART:20210701T000000Z\r
DTEND:20210701T003000Z\r
SUMMARY:test-05\r
RRULE:FREQ=DAILY\r
END:VEVENT\r
'''

    static Calendar calendar(String... components) {
        new CalendarBuilder().build(new StringReader(
                "BEGIN:VCALENDAR\r\nPRODID:-//test//EN\r\nVERSION:2.0\r\n${components.join('')}END:VCALENDAR\r\n"))
    }

    static List<Period> recurrences(Calendar cal, String from, String to) {
        new ComponentGroup(cal.getComponents('VEVENT'), new Uid('event-21186'))
                .calculateRecurrenceSet(new Period<Instant>(Instant.parse(from), Instant.parse(to)))
    }

    static List<Instant> instants(List<Period> periods) {
        periods.findAll { it.start.isSupported(ChronoField.INSTANT_SECONDS) }
                .collect { Instant.from(it.start) }
    }

    def 'rescheduled override with a bare RECURRENCE-ID replaces the master instance'() {
        given: 'a daily master and an override moved from 15 July to all-day 14 July (issue #510)'
        def cal = calendar(MASTER_UTC, """BEGIN:VEVENT\r
UID:event-21186\r
DTSTAMP:20210719T141546Z\r
DTSTART;VALUE=DATE:20210714\r
DTEND;VALUE=DATE:20210715\r
SUMMARY:test-05\r
${recurrenceId}\r
END:VEVENT\r
""")

        when: 'recurrences are calculated for 14-16 July'
        def periods = recurrences(cal, '2021-07-14T00:00:00Z', '2021-07-16T00:00:00Z')

        then: 'the 15 July master instance is gone'
        !instants(periods).contains(Instant.parse('2021-07-15T00:00:00Z'))

        and: 'the all-day override occurrence is present'
        def override = periods.find { it.start == LocalDate.of(2021, 7, 14) }
        override != null
        override.duration == java.time.Period.ofDays(1)
        override.component.getProperty('RECURRENCE-ID').isPresent()

        and: 'the remaining master instances are untouched'
        instants(periods) == [Instant.parse('2021-07-14T00:00:00Z'), Instant.parse('2021-07-16T00:00:00Z')]

        where:
        recurrenceId << ['RECURRENCE-ID:20210715T000000Z', 'RECURRENCE-ID;TZID=UTC:20210715T000000']
    }

    def 'override that keeps its start but changes its duration replaces the master instance'() {
        given:
        def cal = calendar(MASTER_UTC, '''BEGIN:VEVENT\r
UID:event-21186\r
DTSTAMP:20210719T141546Z\r
RECURRENCE-ID:20210715T000000Z\r
DTSTART:20210715T000000Z\r
DTEND:20210715T020000Z\r
END:VEVENT\r
''')

        when:
        def periods = recurrences(cal, '2021-07-14T00:00:00Z', '2021-07-16T00:00:00Z')
        def onThe15th = periods.findAll { Instant.from(it.start) == Instant.parse('2021-07-15T00:00:00Z') }

        then: 'exactly one period on the 15th, with the override duration'
        onThe15th.size() == 1
        onThe15th[0].duration == Duration.ofHours(2)
        onThe15th[0].component.getProperty('RECURRENCE-ID').isPresent()
    }

    def 'override moved outside the query period removes the master instance and adds nothing'() {
        given:
        def cal = calendar(MASTER_UTC, '''BEGIN:VEVENT\r
UID:event-21186\r
DTSTAMP:20210719T141546Z\r
RECURRENCE-ID:20210715T000000Z\r
DTSTART:20210801T000000Z\r
DTEND:20210801T003000Z\r
END:VEVENT\r
''')

        when:
        def periods = recurrences(cal, '2021-07-14T00:00:00Z', '2021-07-16T00:00:00Z')

        then:
        instants(periods) == [Instant.parse('2021-07-14T00:00:00Z'), Instant.parse('2021-07-16T00:00:00Z')]
    }

    def 'recurrence properties on an override do not generate extra occurrences'() {
        given: 'an override that wrongly carries the master RRULE (issue #472)'
        def cal = calendar(MASTER_UTC, '''BEGIN:VEVENT\r
UID:event-21186\r
DTSTAMP:20210719T141546Z\r
RECURRENCE-ID:20210715T000000Z\r
DTSTART:20210715T010000Z\r
DTEND:20210715T013000Z\r
RRULE:FREQ=DAILY\r
END:VEVENT\r
''')

        when:
        def periods = recurrences(cal, '2021-07-14T00:00:00Z', '2021-07-17T12:00:00Z')

        then: 'one override occurrence, and each remaining master instance exactly once'
        instants(periods) == [
                Instant.parse('2021-07-14T00:00:00Z'),
                Instant.parse('2021-07-15T01:00:00Z'),
                Instant.parse('2021-07-16T00:00:00Z'),
                Instant.parse('2021-07-17T00:00:00Z'),
        ]
        periods.count { it.component.getProperty('RECURRENCE-ID').isPresent() } == 1
    }

    def 'RECURRENCE-ID matches a zoned master instance by instant'() {
        given: 'a Paris master and an override whose RECURRENCE-ID is written in UTC'
        def cal = calendar('''BEGIN:VEVENT\r
UID:event-21186\r
DTSTAMP:20210719T141546Z\r
DTSTART;TZID=Europe/Paris:20210715T020000\r
DTEND;TZID=Europe/Paris:20210715T023000\r
RRULE:FREQ=DAILY\r
END:VEVENT\r
''', '''BEGIN:VEVENT\r
UID:event-21186\r
DTSTAMP:20210719T141546Z\r
RECURRENCE-ID:20210716T000000Z\r
DTSTART;TZID=Europe/Paris:20210716T090000\r
DTEND;TZID=Europe/Paris:20210716T093000\r
END:VEVENT\r
''')

        when:
        def periods = recurrences(cal, '2021-07-14T12:00:00Z', '2021-07-17T12:00:00Z')

        then: 'the 02:00 Paris instance on the 16th (00:00Z) is replaced by the 09:00 Paris override'
        instants(periods) == [
                Instant.parse('2021-07-15T00:00:00Z'),
                Instant.parse('2021-07-16T07:00:00Z'),
                Instant.parse('2021-07-17T00:00:00Z'),
        ]
    }

    def 'assert component list is unchanged when no mutation occurs'() {
        given: 'a component list instance'
        ComponentList<VEvent> componentList = []

        expect: 'the underlying component list is the same'
        ComponentGroup componentGroup = [componentList, uid]
        componentGroup.componentList === componentList

        and: 'after mutation the list is different'
        componentGroup.add(event)
        componentGroup.componentList != componentList
    }
}
