package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

import static net.fortuna.ical4j.model.Recur.Frequency.DAILY
import static net.fortuna.ical4j.model.Recur.Frequency.YEARLY

class ByMonthRuleTest extends Specification {

    def 'verify transformations by month'() {
        given: 'a bymonth rule'
        ByMonthRule rule = [new MonthList(byMonthPart), frequency]

        and: 'a list of dates'
        DateList dateList = [valueType]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        byMonthPart     | frequency     | dates                             | valueType       | expectedResult
        '1,2'         | YEARLY | [new Date('20150103')]            | Value.DATE      | [new Date('20150103'), new Date('20150203')]
        '2'         | YEARLY | [new DateTime("20200229T000000")] | Value.DATE_TIME | [new DateTime("20200229T000000")]
        '10,12'         | YEARLY | [new Date("20181010")]            | Value.DATE      | [new Date("20181010"), new Date("20181210")]
        '10,12'         | DAILY | [new Date("20181010"), new Date("20181110"), new Date("20181210")]            | Value.DATE      | [new Date("20181010"), new Date("20181210")]
        '5L'        | YEARLY    | [new Date('20150103')]            | Value.DATE                        | []
    }

    def 'verify transformations by month with skip backward'() {
        given: 'a bymonth rule'
        ByMonthRule rule = [new MonthList(byMonthPart), frequency, Recur.Skip.BACKWARD]

        and: 'a list of dates'
        DateList dateList = [valueType]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        byMonthPart | frequency | dates                             | valueType                         | expectedResult
        '5L'        | YEARLY    | [new Date('20150103')]            | Value.DATE                        | [new Date('20150503')]
    }

    def 'verify transformations by month with skip forward'() {
        given: 'a bymonth rule'
        ByMonthRule rule = [new MonthList(byMonthPart), frequency, Recur.Skip.FORWARD]

        and: 'a list of dates'
        DateList dateList = [valueType]
        dateList.addAll(dates)

        expect: 'the rule transforms the dates correctly'
        rule.transform(dateList) == expectedResult

        where:
        byMonthPart | frequency | dates                             | valueType     | expectedResult
        '5L'        | YEARLY    | [new Date('20150103')]            | Value.DATE    | [new Date('20150603')]
    }
}
