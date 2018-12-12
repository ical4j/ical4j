package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.DateList
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

class ByMonthRuleTest extends Specification {

    def 'verify transformations by month'() {
        given: 'a bymonth rule'
        ByMonthRule rule = [new NumberList(byMonthPart), Optional.empty()]

        and: 'a list of dates'
        DateList dateList = [valueType]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        byMonthPart     | dates                                | valueType   | expectedResult
        '1,2'         | [new Date('20150103')]          | Value.DATE  | [new Date('20150103'), new Date('20150203')]
        '2'         | [new DateTime("20200229T000000")] | Value.DATE_TIME| [new DateTime("20200229T000000")]
    }
}
