package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.DateList
import net.fortuna.ical4j.model.WeekDay
import net.fortuna.ical4j.model.WeekDayList
import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

import static net.fortuna.ical4j.model.Recur.Frequency.WEEKLY

class ByDayRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYDAY rule'
        ByDayRule rule = [new WeekDayList(rulePart), frequency]

        and: 'a list of dates'
        DateList dateList = [Value.DATE]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        rulePart    | frequency              | dates                  | expectedResult
        WeekDay.FR  | WEEKLY | [new Date('20150103')] | [new Date('20150102')]
    }
}
