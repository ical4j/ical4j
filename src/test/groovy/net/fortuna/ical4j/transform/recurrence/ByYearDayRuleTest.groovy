package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.CalendarDateFormat
import net.fortuna.ical4j.model.NumberList
import spock.lang.Shared
import spock.lang.Specification

import static net.fortuna.ical4j.model.Recur.Frequency.YEARLY

class ByYearDayRuleTest extends Specification {

    @Shared
    def dateFormat = CalendarDateFormat.DATE_FORMAT

    def 'verify transformations by day'() {
        given: 'a BYYEARDAY rule'
        ByYearDayRule rule = [new NumberList(rulePart), frequency]

        expect: 'the rule transforms the dates correctly'
        rule.transform(dates) == expectedResult

        where:
        rulePart | frequency       | dates                  | expectedResult
        '1'      | YEARLY | [dateFormat.parse('20150103')] | [dateFormat.parse('20150101')]
    }
}
