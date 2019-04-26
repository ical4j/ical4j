package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.*
import java.time.format.DateTimeParseException
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

    def 'verify date string parsing'() {
        expect:
        def parsed = TemporalAdapter.parse(dateString)
        parsed.temporal.class == expectedType

        where:
        dateString              | expectedType
        '20150504'              | LocalDate
        '20150504T120000'       | LocalDateTime
        '20150504T120000Z'      | Instant
    }

    def 'verify zoned date string parsing'() {
        expect:
        def parsed = TemporalAdapter.parse(dateString, ZoneId.systemDefault())
        parsed.temporal.class == expectedType
        parsed.temporal.zone == expectedZone

        where:
        dateString              | expectedType      | expectedZone
        '20150504T120000'       | ZonedDateTime     | ZoneId.systemDefault()
    }

    def 'verify invalid date string parsing'() {
        when: 'attempted parsing'
        TemporalAdapter.parse(dateString)

        then:
        thrown(DateTimeParseException)

        where:
        dateString << ['2015-05-04', '20150504T12:00:00']
    }

    def 'verify invalid zoned date string parsing'() {
        when: 'attempted parsing'
        TemporalAdapter.parse(dateString, ZoneId.systemDefault())

        then:
        thrown(DateTimeParseException)

        where:
        dateString << ['20150504T120000Z']
    }
}
