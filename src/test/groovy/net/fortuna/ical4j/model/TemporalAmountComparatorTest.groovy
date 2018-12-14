package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.Duration

class TemporalAmountComparatorTest extends Specification {

    TemporalAmountComparator comparator = []

    def 'assert comparison is correct'() {
        expect:
        comparator.compare(o1, o2) == expectedResult

        where:
        o1                      | o2                   | expectedResult
        Duration.ofDays(1)  | Duration.ofDays(2)       | -1
        Duration.ofHours(-2)  | Duration.ofHours(2)       | -1
        java.time.Period.ofDays(3)  | java.time.Period.ofDays(2) | 1
        java.time.Period.ofMonths(12)  | java.time.Period.ofYears(1) | -1
        java.time.Period.ofMonths(12)  | java.time.Period.ofMonths(12) | 0
        java.time.Period.ofDays(1)  | java.time.Duration.ofDays(1) | Integer.MAX_VALUE
    }
}
