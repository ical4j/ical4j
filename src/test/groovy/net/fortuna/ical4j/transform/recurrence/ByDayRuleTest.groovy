package net.fortuna.ical4j.transform.recurrence


import net.fortuna.ical4j.model.TemporalAdapter
import net.fortuna.ical4j.model.WeekDay
import net.fortuna.ical4j.model.WeekDayList
import spock.lang.Specification

import static net.fortuna.ical4j.model.Recur.Frequency.WEEKLY

class ByDayRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYDAY rule'
        ByDayRule rule = [new WeekDayList(rulePart), frequency]

        expect: 'the rule transforms the dates correctly'
        rule.transform(dates) == expectedResult

        where:
        rulePart    | frequency | dates                                     | expectedResult
        WeekDay.FR  | WEEKLY    | [TemporalAdapter.parse('20150103').temporal] | [TemporalAdapter.parse('20150102').temporal]
    }
}
