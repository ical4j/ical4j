package net.fortuna.ical4j.transform.recurrence


import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.TemporalAdapter
import spock.lang.Specification

import static net.fortuna.ical4j.model.Recur.Frequency.YEARLY

class ByMonthDayRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYMONTHDAY rule'
        ByMonthDayRule rule = [new NumberList(rulePart), frequency]

        expect: 'the rule transforms the dates correctly'
        rule.transform(dates) == expectedResult

        where:
        rulePart | frequency       | dates                      | expectedResult
        '1'      | YEARLY | [TemporalAdapter.parse('20150103').temporal] | [TemporalAdapter.parse('20150101').temporal]
    }
}
