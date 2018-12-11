package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.DateList
import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

class ByMonthRuleTest extends Specification {

    def 'verify transformations by month'() {
        given: 'a bymonth rule'
        ByMonthRule rule = [new NumberList(byMonthPart), Optional.empty()]

        and: 'a list of dates'
        DateList dateList = [Value.DATE]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        byMonthPart     | dates                           | expectedResult
        '1,2'         | [new Date('20150103')]    | [new Date('20150103'), new Date('20150203')]
    }
}
