package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.Duration

class TemporalAmountAdapterTest extends Specification {

    def "verify string representation"() {
        expect:
        new TemporalAmountAdapter(duration) as String == expectedValue

        where:
        duration                    | expectedValue
        Duration.ofHours(4)         | "PT4H"
        Duration.ofDays(12)         | "PT288H"
        java.time.Period.ofDays(12) | "P12D"
        java.time.Period.ofWeeks(7) | "P7W"
        java.time.Period.ofDays(365) | "P365D"
        java.time.Period.ofDays(364) | "P52W"
        java.time.Period.ofYears(1) | "P52W"
        java.time.Period.ofMonths(6) | "P24W"
    }

    def 'verify temporalamount creation'() {
        expect:
        TemporalAmountAdapter.from(duration) == expectedTemporalAmount

        where:
        duration       | expectedTemporalAmount
        new Dur(1, 2, 3, 4)  | Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4)
        new Dur(5)  | java.time.Period.ofWeeks(5)
    }
}
