package net.fortuna.ical4j.transform.recurrence


import net.fortuna.ical4j.model.NumberList
import net.fortuna.ical4j.model.Recur
import net.fortuna.ical4j.model.TemporalAdapter
import spock.lang.Specification

import static Frequency.YEARLY

class ByMonthDayRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYMONTHDAY rule'
        ByMonthDayRule rule = [new NumberList(rulePart), frequency]

        expect: 'the rule transforms the dates correctly'
        rule.apply(dates) == expectedResult

        where:
        rulePart | frequency       | dates                      | expectedResult
        '1'      | YEARLY | [TemporalAdapter.parse('20150103').temporal] | [TemporalAdapter.parse('20150101').temporal]
        '29'     | YEARLY           | [TemporalAdapter.parse('20150201').temporal] | []
    }

    def 'verify transformations by day with skip forward'() {
        given: 'a BYMONTHDAY rule'
        ByMonthDayRule rule = [new NumberList(rulePart), frequency, Recur.Skip.FORWARD]

        and: 'a list of dates'
        def dateList = dates.collect {TemporalAdapter.parse(it).temporal}

        expect: 'the rule transforms the dates correctly'
        rule.apply(dateList).collect { d -> new TemporalAdapter<>(d) as String} == expectedResult

        where:
        rulePart | frequency        | dates        | expectedResult
        '1'      | YEARLY           | ['20150103'] | ['20150101']
        '29'     | YEARLY           | ['20150201'] | ['20150301']
    }

    def 'verify transformations by day with skip backward'() {
        given: 'a BYMONTHDAY rule'
        ByMonthDayRule rule = [new NumberList(rulePart), frequency, Recur.Skip.BACKWARD]

        and: 'a list of dates'
        def dateList = dates.collect {TemporalAdapter.parse(it).temporal}

        expect: 'the rule transforms the dates correctly'
        rule.apply(dateList).collect { d -> new TemporalAdapter<>(d) as String} == expectedResult

        where:
        rulePart | frequency        | dates        | expectedResult
        '1'      | YEARLY           | ['20150103'] | ['20150101']
        '29'     | YEARLY           | ['20150201'] | ['20150228']
    }
}
