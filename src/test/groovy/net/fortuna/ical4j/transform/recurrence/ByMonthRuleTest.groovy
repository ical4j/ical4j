package net.fortuna.ical4j.transform.recurrence


import net.fortuna.ical4j.model.MonthList
import net.fortuna.ical4j.model.TemporalAdapter
import spock.lang.Specification
import spock.lang.Unroll

import static Frequency.DAILY
import static Frequency.YEARLY

class ByMonthRuleTest extends Specification {

    @Unroll
    def 'verify transformations by month'() {
        given: 'a bymonth rule'
        ByMonthRule rule = [new MonthList(byMonthPart), frequency]

        expect: 'the rule transforms the dates correctly'
        rule.transform(dates) == expectedResult

        where:
        byMonthPart | frequency | dates                                                         | expectedResult
        '1,2'       | YEARLY    | [TemporalAdapter.parse('20150103').temporal]            |[TemporalAdapter.parse('20150103').temporal, TemporalAdapter.parse('20150203').temporal]
        '2'         | YEARLY    | [TemporalAdapter.parse("20200229T000000").temporal]     |[TemporalAdapter.parse("20200229T000000").temporal]
        '10,12'     | YEARLY    | [TemporalAdapter.parse("20181010").temporal]            |[TemporalAdapter.parse("20181010").temporal, TemporalAdapter.parse("20181210").temporal]
        '10,12'     | DAILY     | [TemporalAdapter.parse("20181010").temporal, TemporalAdapter.parse("20181110").temporal, TemporalAdapter.parse("20181210").temporal] | [TemporalAdapter.parse("20181010").temporal, TemporalAdapter.parse("20181210").temporal]
    }
}
