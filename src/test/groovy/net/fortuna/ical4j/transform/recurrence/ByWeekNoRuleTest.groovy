package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.DateList
import net.fortuna.ical4j.model.DateRange
import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.Recur
import net.fortuna.ical4j.model.WeekDay
import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

class ByWeekNoRuleTest extends Specification {

    def 'verify transformations by week number'() {
        given: 'a BYWEEKNO rule'
        ByWeekNoRule rule = [new NumberList(byWeekNoPart), Recur.Frequency.DAILY, Optional.of(WeekDay.Day.MO)]

        and: 'a list of dates'
        DateList dateList = [Value.DATE]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        byWeekNoPart     | dates                  | expectedResult
        '1,2,3'         | [new Date('20150103')] | [new Date('20150103'), new Date('20150110'), new Date('20150117')]
        '1,3,5'         | [new Date('20150630')] | [new Date('20141230'), new Date('20150113'), new Date('20150127')]
        '49,50,51'      | [new Date('20221205')] | [new Date('20221205'), new Date('20221212'), new Date('20221219')]
    }

    def 'verify transformations by week number with date range'() {
        given: 'a BYWEEKNO rule with range date'
        and: 'a list of dates'
        DateList dateList = [Value.DATE]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        ByWeekNoRule rule = [
                new NumberList(byWeekNoPart),
                Recur.Frequency.DAILY,
                Optional.of(WeekDay.Day.MO),
                dateRange
        ]
        rule.transform(dateList) == expectedResult

        where:
        byWeekNoPart        | dates                  | dateRange    | expectedResult
        '48,49,50,51,52'   | [new Date('20221128')] | new DateRange(new Date('20221205'),new Date('20221226')) | [new Date('20221205'), new Date('20221212'), new Date('20221219')]
    }
}
