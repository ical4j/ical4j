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

        expect: 'calculate recurrence set returns the expected results'
        component.calculateRecurrenceSet(period) == expectedResults

        where:
        period    | expectedResults
        new Period('20140630T000000Z/20150630T000000Z') | []
    }
}
