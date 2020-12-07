package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.CalendarDateFormat
import net.fortuna.ical4j.model.NumberList
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static Frequency.DAILY
import static Frequency.SECONDLY

class BySecondRuleTest extends Specification {

    @Shared
    def dateFormat = CalendarDateFormat.FLOATING_DATE_TIME_FORMAT

    @Unroll
    def 'verify transformations by day'() {
        given: 'a BYSECOND rule'
        BySecondRule rule = [new NumberList(rulePart), frequency]

        expect: 'the rule transforms the dates correctly'
        rule.transform(dates) == expectedResult

        where:
        rulePart | frequency       | dates                              | expectedResult
        '1'      | DAILY    | [dateFormat.parse('20150103T000000')] | [dateFormat.parse('20150103T000001')]
        '1'      | SECONDLY | [dateFormat.parse('20150103T000001'), dateFormat.parse('20150103T000002')] | [dateFormat.parse('20150103T000001')]
    }
}
