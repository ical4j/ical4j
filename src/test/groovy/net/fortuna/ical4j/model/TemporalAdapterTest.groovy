package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.UnsupportedTemporalTypeException


class TemporalAdapterTest extends Specification {

    def "verify string representation"() {
        expect:
        new TemporalAdapter(temporal, format) as String == expectedValue

        where:
        temporal                       | format                                              |   expectedValue
        LocalDate.of(2001, 04, 03)     | TemporalAdapter.FormatType.Date                     | '20010403'
        LocalDateTime.of(2001, 04, 03, 11, 00) | TemporalAdapter.FormatType.Date             | '20010403'

        LocalDateTime.of(2001, 04, 03, 11, 00) | TemporalAdapter.FormatType.DateTimeUtc      | '20010403T110000Z'

        LocalDateTime.of(2001, 04, 03, 11, 00) | TemporalAdapter.FormatType.DateTimeFloating | '20010403T110000'
    }

    def "verify formatting error handling"() {
        when:
        new TemporalAdapter(temporal, format) as String

        then:
        thrown(UnsupportedTemporalTypeException)

        where:
        temporal                       | format
        LocalDate.of(2001, 04, 03)     | TemporalAdapter.FormatType.DateTimeUtc
        LocalDate.of(2001, 04, 03)     | TemporalAdapter.FormatType.DateTimeFloating
    }
}
