package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.component.VEvent
import spock.lang.Specification

class ComponentSpec extends Specification {

    def "test Component.calculateRecurrenceSet"() {
        given: 'a component'
        VEvent component = new ContentBuilder().with {
            vevent {
                dtstart '20140630T000000Z'
                dtend '20140630T010000Z'
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
        new Period('20140630T000000Z/20150630T000000Z') | ['20140630T000000Z/PT1H','20140730T000000Z/PT1H',
                                                                   '20140830T000000Z/PT1H',
                                                                   '20140930T000000Z/PT1H',
                                                                   '20141030T000000Z/PT1H',
                                                                   '20141130T000000Z/PT1H',
                                                                   '20141230T000000Z/PT1H',
                                                                   '20150130T000000Z/PT1H',
                                                                   '20150330T000000Z/PT1H',
                                                                   '20150430T000000Z/PT1H',
                                                                   '20150530T000000Z/PT1H', '20150630T000000Z/PT1H']
    }
}
