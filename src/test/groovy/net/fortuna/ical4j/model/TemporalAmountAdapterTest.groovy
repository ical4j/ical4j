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
}
