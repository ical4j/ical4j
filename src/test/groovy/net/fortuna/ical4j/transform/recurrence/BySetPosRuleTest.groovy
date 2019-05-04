package net.fortuna.ical4j.transform.recurrence


import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.TemporalAdapter
import spock.lang.Specification

class BySetPosRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYSETPOS rule'
        BySetPosRule rule = [new NumberList(rulePart)]

        expect: 'the rule transforms the dates correctly'
        rule.transform(dates) == expectedResult

        where:
        rulePart | dates                               | expectedResult
        '1'      | [TemporalAdapter.parse('20150103').temporal] | [TemporalAdapter.parse('20150103').temporal]
    }
}
