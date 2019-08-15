package net.fortuna.ical4j.transform.recurrence


import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.TemporalAdapter
import spock.lang.Specification

class BySetPosRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYSETPOS rule'
        BySetPosRule rule = [new NumberList(rulePart)]

        and: 'a list of dates'
        def dates = []
        dateStrings.each {
            dates << TemporalAdapter.parse(it).temporal
        }

        def expected = []
        expectedResult.each {
            expected << TemporalAdapter.parse(it).temporal
        }

        expect: 'the rule transforms the dates correctly'
        rule.transform(dates) == expected

        where:
        rulePart | dateStrings  | expectedResult
        '1'      | ['20150103'] | ['20150103']
        '-1'      | ['20150103', '20150113', '20150123'] | ['20150123']
    }
}
