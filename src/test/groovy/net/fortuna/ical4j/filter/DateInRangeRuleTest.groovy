package net.fortuna.ical4j.filter

import net.fortuna.ical4j.model.DateRange
import spock.lang.Shared
import spock.lang.Specification

class DateInRangeRuleTest extends Specification {

    @Shared def rangeFrom = Calendar.instance
    @Shared def rangeTo = Calendar.instance

    def setupSpec() {
        rangeTo.add(Calendar.DAY_OF_MONTH, 7)
    }

    def 'assert rule match'() {
        given: 'a date range rule'
        DateInRangeRule rule = [range, DateRange.INCLUSIVE_START | DateRange.INCLUSIVE_END]

        expect:
        rule.test(date) == test

        where:
        range                                                 | date       | test
        new DateRange(rangeFrom.getTime(), rangeTo.getTime()) | new Date() | true
    }
}
