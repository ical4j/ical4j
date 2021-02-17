package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.TemporalAdapter
import net.fortuna.ical4j.model.WeekDay
import net.fortuna.ical4j.model.WeekDayList
import spock.lang.Specification

import java.time.DayOfWeek

import static Frequency.WEEKLY
import static net.fortuna.ical4j.model.WeekDay.*

class ByDayRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYDAY rule'
        ByDayRule rule = [new WeekDayList(rulePart), frequency, DayOfWeek.SUNDAY]

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
        rulePart | frequency | dateStrings  | expectedResult
        FR       | WEEKLY    | ['20150103'] | ['20150102']
        [SU, MO] as WeekDay[] | WEEKLY    | ['20110306'] | ['20110306', '20110307']
    }
}
