package net.fortuna.ical4j.model

import spock.lang.Specification

class PeriodListSpec extends Specification {

    def 'test hashcode equality'() {
        given: 'a period list'
        PeriodList list1 = PeriodList.parse('20140803T120100/P1D')

        and: 'a second identical list'
        PeriodList list2 = PeriodList.parse('20140803T120100/P1D')

        expect: 'object equality'
        list1 == list2

        and: 'hashcode equality'
        list1.hashCode() == list2.hashCode()
    }
}
