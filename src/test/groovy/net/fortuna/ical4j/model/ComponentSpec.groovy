package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.component.VEvent
import spock.lang.Specification

class ComponentSpec extends Specification {

    def "test Component.calculateRecurrenceSet"() {
        given: 'a component'
        VEvent component = new ContentBuilder().with {
            vevent {
                dtstart '20140630T000000'
                rrule 'FREQ=MONTHLY'
            }
        }
        and: 'an expected list of periods'
        def expectedPeriods = new PeriodList()
        expectedPeriods.addAll(expectedResults.collect { new Period(it)})

        expect: 'calculate recurrence set returns the expected results'
        component.calculateRecurrenceSet(period) == expectedPeriods

        where:
        period    | expectedResults
        new Period('20140630T000000Z/20150630T000000Z') | ['20140730T000000/PT0S',
                                                                   '20140830T000000/PT0S',
                                                                   '20140930T000000/PT0S',
                                                                   '20141030T000000/PT0S',
                                                                   '20141130T000000/PT0S',
                                                                   '20141230T000000/PT0S',
                                                                   '20150130T000000/PT0S',
                                                                   '20150330T000000/PT0S',
                                                                   '20150430T000000/PT0S',
                                                                   '20150530T000000/PT0S',
                                                                   '20150630T000000/PT0S']
    }
}
