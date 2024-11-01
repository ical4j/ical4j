package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.component.VEvent
import spock.lang.Specification

import java.time.temporal.Temporal

class ComponentSpec extends Specification {

    def "test Component.calculateRecurrenceSet"() {
        given: 'a component'
        VEvent component = new ContentBuilder().with {
            vevent {
                dtstart '20140630T000000', parameters: parameters { tzid_ 'Australia/Melbourne' }
                dtend '20140630T010000', parameters: parameters { tzid_ 'Australia/Melbourne' }
                rrule 'FREQ=MONTHLY'
            }
        }
        and: 'an expected list of periods'
        def expectedPeriods = expectedResults.collect { Period.parse(it)} as Set

        expect: 'calculate recurrence set returns the expected results'
        component.calculateRecurrenceSet(period) == expectedPeriods

        where:
        period    | expectedResults
        Period.parse('20140629T000000Z/20150630T000000Z') | ['20140630T000000/PT1H','20140730T000000/PT1H',
                                                                   '20140830T000000/PT1H',
                                                                   '20140930T000000/PT1H',
                                                                   '20141030T000000/PT1H',
                                                                   '20141130T000000/PT1H',
                                                                   '20141230T000000/PT1H',
                                                                   '20150130T000000/PT1H',
                                                                   '20150330T000000/PT1H',
                                                                   '20150430T000000/PT1H',
                                                                   '20150530T000000/PT1H', '20150630T000000/PT1H']
    }

    def "test Component.calculateRecurrenceSet with RDATE"() {
        given: 'a component'
        VEvent component = new ContentBuilder().with {
            vevent {
                dtstart '20221014T194500'
                dtend '20221014T194501'
                rdate '20221014T194500,20221028T194500,20221111T194500,20221125T194500,20221209T194500,20230113T194500'
            }
        }
        and: 'an expected list of periods'
        def expectedPeriods = new HashSet<Period<? extends Temporal>>()
        expectedPeriods.addAll(expectedResults.collect { Period.parse(it)})

        expect: 'calculate recurrence set returns the expected results'
        component.calculateRecurrenceSet(period) == expectedPeriods

        where:
        period    | expectedResults
        Period.parse('20221014T194500/20230113T194500') | ['20221014T194500/PT1S', '20221028T194500/PT1S', '20221111T194500/PT1S', '20221125T194500/PT1S', '20221209T194500/PT1S', '20230113T194500/PT1S']
    }

    def 'test functional property modifier'() {
        given: 'a component'
        VEvent event = [false]

        when: 'a null property is applied via functional method'
        event.with((c, p) -> { if (p != null) c.add(p); return c }, null)

        then: 'the property is not added'
        event.getProperties().isEmpty()
    }
}
