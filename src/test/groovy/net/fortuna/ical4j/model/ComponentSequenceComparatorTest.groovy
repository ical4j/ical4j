package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.property.DtStamp
import net.fortuna.ical4j.model.property.Sequence
import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant

class ComponentSequenceComparatorTest extends Specification {

    @Shared
    ContentBuilder builder = []

    @Shared
    ComponentSequenceComparator comparator = []

    @Shared
    Sequence seq0, seq1, seq2

    @Shared
    DtStamp dt1, dt2, dt3

    def setupSpec() {
        seq0 = [0]
        seq1 = [1]
        seq2 = [2]

        dt1 = [Instant.ofEpochMilli(1000)]
        dt2 = [Instant.ofEpochMilli(2000)]
        dt3 = [Instant.ofEpochMilli(3000)]
    }

    def "Compare equality"() {
        expect:
        comparator.compare((Component) o1, (Component) o2) == 0

        where:
        o1                              | o2
        builder.vevent {}               | builder.vevent {}
        builder.vevent {sequence(seq0)}    | builder.vevent {}
    }

    def "Compare sequence ordering"() {
        expect:
        comparator.compare((Component) o1, (Component) o2) == expectedResult

        where:
        o1                              | o2                                | expectedResult
        builder.vevent {}               | builder.vevent {sequence(seq1)}    | -1
        builder.vevent {sequence(seq1)}  | builder.vevent {sequence(seq0)}    | 1
    }

    def "Compare dtstamp ordering"() {
        expect:
        comparator.compare((Component) o1, (Component) o2) == expectedResult

        where:
        o1                              | o2                                | expectedResult
        builder.vevent {}               | builder.vevent {dtstamp(dt1)}    | -1
        builder.vevent {dtstamp(dt2)}  | builder.vevent {dtstamp(dt1)}    | 1
    }
}
