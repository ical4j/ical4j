package net.fortuna.ical4j.transform.recurrence


import net.fortuna.ical4j.model.DateList
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

class BySecondRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYSECOND rule'
        BySecondRule rule = [new NumberList(rulePart)]

        and: 'a list of dates'
        DateList dateList = [Value.DATE_TIME]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        rulePart | dates                      | expectedResult
        '1'      | [new DateTime('20150103T000000Z')] | [new DateTime('20150103T000001Z')]
    }
}
