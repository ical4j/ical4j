package net.fortuna.ical4j.transform.recurrence


import net.fortuna.ical4j.model.MonthList
import net.fortuna.ical4j.model.Recur
import net.fortuna.ical4j.model.TemporalAdapter
import spock.lang.Specification
import spock.lang.Unroll

import java.time.chrono.HijrahDate
import java.time.chrono.JapaneseDate
import java.time.chrono.MinguoDate
import java.time.chrono.ThaiBuddhistDate

import static Frequency.DAILY
import static Frequency.YEARLY

class ByMonthRuleTest extends Specification {

    @Unroll
    def 'verify transformations by month'() {
        given: 'a bymonth rule'
        ByMonthRule rule = [new MonthList(byMonthPart), frequency]

        expect: 'the rule transforms the dates correctly'
        rule.apply(dates) == expectedResult

        where:
        byMonthPart | frequency | dates                                                         | expectedResult
        '1,2'       | YEARLY    | [TemporalAdapter.parse('20150103').temporal]            |[TemporalAdapter.parse('20150103').temporal, TemporalAdapter.parse('20150203').temporal]
        '2'         | YEARLY    | [TemporalAdapter.parse("20200229T000000").temporal]     |[TemporalAdapter.parse("20200229T000000").temporal]
        '10,12'     | YEARLY    | [TemporalAdapter.parse("20181010").temporal]            |[TemporalAdapter.parse("20181010").temporal, TemporalAdapter.parse("20181210").temporal]
        '10,12'     | DAILY     | [TemporalAdapter.parse("20181010").temporal, TemporalAdapter.parse("20181110").temporal, TemporalAdapter.parse("20181210").temporal] | [TemporalAdapter.parse("20181010").temporal, TemporalAdapter.parse("20181210").temporal]
    }

    @Unroll
    def 'verify non-Gregorian transformations by month'() {
        given: 'a bymonth rule'
        ByMonthRule rule = [new MonthList(byMonthPart), frequency]

        expect: 'the rule transforms the dates correctly'
        rule.apply(dates) == expectedResult

        where:
        byMonthPart | frequency | dates                             | expectedResult
        '1,2'       | YEARLY    | [JapaneseDate.of(2012, 1, 1)]     | [JapaneseDate.of(2012, 1, 1), JapaneseDate.of(2012, 2, 1)]
        '1,2'       | YEARLY    | [ThaiBuddhistDate.of(2012, 1, 1)] | [ThaiBuddhistDate.of(2012, 1, 1), ThaiBuddhistDate.of(2012, 2, 1)]
        '1,2'       | YEARLY    | [MinguoDate.of(2012, 1, 1)]       | [MinguoDate.of(2012, 1, 1), MinguoDate.of(2012, 2, 1)]
        '1,2'       | YEARLY    | [HijrahDate.of(1400, 1, 1)]       | [HijrahDate.of(1400, 1, 1), HijrahDate.of(1400, 2, 1)]
        '2'         | YEARLY    | [TemporalAdapter.parse("20200229T000000").temporal] | [TemporalAdapter.parse("20200229T000000").temporal]
        '10,12'     | YEARLY    | [TemporalAdapter.parse("20181010").temporal]        | [TemporalAdapter.parse("20181010").temporal, TemporalAdapter.parse("20181210").temporal]
        '10,12'     | DAILY     | [TemporalAdapter.parse("20181010").temporal, TemporalAdapter.parse("20181110").temporal, TemporalAdapter.parse("20181210").temporal]            | [TemporalAdapter.parse("20181010").temporal, TemporalAdapter.parse("20181210").temporal]
        '5L'        | YEARLY    | [TemporalAdapter.parse('20150103').temporal]            | []
    }

    def 'verify transformations by month with skip backward'() {
        given: 'a bymonth rule'
        ByMonthRule rule = [new MonthList(byMonthPart), frequency, Recur.Skip.BACKWARD]

        and: 'a list of dates'
        def dateList = dates.collect {TemporalAdapter.parse(it).temporal}

        expect: 'the rule transforms the dates correctly'
        rule.apply(dateList).collect { d -> new TemporalAdapter<>(d) as String} == expectedResult

        where:
        byMonthPart | frequency | dates         | expectedResult
        '5L'        | YEARLY    | ['20150103']  | ['20150503']
    }

    def 'verify transformations by month with skip forward'() {
        given: 'a bymonth rule'
        ByMonthRule rule = [new MonthList(byMonthPart), frequency, Recur.Skip.FORWARD]

        and: 'a list of dates'
        def dateList = dates.collect {TemporalAdapter.parse(it).temporal}

        expect: 'the rule transforms the dates correctly'
        rule.apply(dateList).collect { d -> new TemporalAdapter<>(d) as String} == expectedResult

        where:
        byMonthPart | frequency | dates         | expectedResult
        '5L'        | YEARLY    | ['20150103']  | ['20150603']
    }
}
