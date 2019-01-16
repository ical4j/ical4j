package net.fortuna.ical4j.model

import spock.lang.Specification

class DateRangeTest extends Specification {

    def 'test hashcode equality'() {
        given: 'a date range'
        DateRange range1 = [new DateTime('20140803T120100'), new DateTime('20140804T120100')]

        and: 'a second identical date range'
        DateRange range2 = [new DateTime('20140803T120100'), new DateTime('20140804T120100')]

        expect: 'object equality'
        range1 == range2

        and: 'hashcode equality'
        range1.hashCode() == range2.hashCode()
    }
}
