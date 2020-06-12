package net.fortuna.ical4j.model.property


import spock.lang.Specification

class DurationSpec extends Specification {

    Duration.Factory factory = []

    def 'verify parsing'() {
        expect:
        Duration duration = factory.createProperty([], value)
        duration.value == expectedValue

        where:
        value   | expectedValue
        'P7D'   | 'P1W'
        '-P1W'   | '-P1W'
        'P52W'  | 'P52W'
        'PT10M' | 'PT10M'
    }
}
