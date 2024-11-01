package net.fortuna.ical4j.transform.recurrence


import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.TemporalAdapter
import spock.lang.Specification

import static Frequency.YEARLY

class ByHourRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYHOUR rule'
        ByHourRule rule = [new NumberList(rulePart), frequency]

        expect: 'the rule transforms the dates correctly'
        rule.apply(dates) == expectedResult

        where:
        rulePart | frequency    | dates                                   | expectedResult
        '1'      | YEARLY   | [TemporalAdapter.parse('20150103T000000').temporal] | [TemporalAdapter.parse('20150103T010000').temporal]
    }
}
