package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.DateList
import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.Recur
import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

import static net.fortuna.ical4j.model.Recur.Frequency.YEARLY

class ByMonthDayRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYMONTHDAY rule'
        ByMonthDayRule rule = [new NumberList(rulePart), frequency]

        and: 'a list of dates'
        DateList dateList = [Value.DATE]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        rulePart | frequency       | dates                  | expectedResult
        '1'      | YEARLY | [new Date('20150103')] | [new Date('20150101')]
        '29'     | YEARLY           | [new Date('20150201')] | []
    }

    def 'verify transformations by day with skip forward'() {
        given: 'a BYMONTHDAY rule'
        ByMonthDayRule rule = [new NumberList(rulePart), frequency, Recur.Skip.FORWARD]

        and: 'a list of dates'
        DateList dateList = [Value.DATE]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        rulePart | frequency        | dates                  | expectedResult
        '1'      | YEARLY           | [new Date('20150103')] | [new Date('20150101')]
        '29'     | YEARLY           | [new Date('20150201')] | [new Date('20150301')]
    }

    def 'verify transformations by day with skip backward'() {
        given: 'a BYMONTHDAY rule'
        ByMonthDayRule rule = [new NumberList(rulePart), frequency, Recur.Skip.BACKWARD]

        and: 'a list of dates'
        DateList dateList = [Value.DATE]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        rulePart | frequency        | dates                  | expectedResult
        '1'      | YEARLY           | [new Date('20150103')] | [new Date('20150101')]
        '29'     | YEARLY           | [new Date('20150201')] | [new Date('20150228')]
    }
}
