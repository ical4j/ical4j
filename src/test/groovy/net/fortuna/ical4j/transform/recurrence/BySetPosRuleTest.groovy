package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.DateList
import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

class BySetPosRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYSETPOS rule'
        BySetPosRule rule = [new NumberList(rulePart)]

        and: 'a list of dates'
        DateList dateList = [Value.DATE]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        rulePart | dates                  | expectedResult
        '1'      | [new Date('20150103')] | [new Date('20150103')]
    }
}
