package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.*
import java.time.format.DateTimeParseException

class TemporalAdapterTest extends Specification {

    def "verify string representation"() {
        expect:
        new TemporalAdapter(temporal) as String == expectedValue

        where:
        temporal                                                        | expectedValue

        LocalDate.of(2001, 04, 03)                                      | '20010403'
        LocalDateTime.of(2001, 04, 03, 11, 00)                          | '20010403T110000'
        LocalDateTime.of(2001, 04, 03, 11, 00).toInstant(ZoneOffset.UTC)| '20010403T110000Z'
        LocalDateTime.of(2001, 04, 03, 11, 00)                          | '20010403T110000'
        ZonedDateTime.of(LocalDateTime.of(2022, 12, 16, 18, 0, 0),
                ZoneId.of("Europe/Warsaw"))                             | '20221216T180000'
        ZonedDateTime.of(2023, 11, 15, 8, 30, 0, 0,
                ZoneId.of("America/Sao_Paulo"))                         | '20231115T083000'
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
        TemporalAdapter<ZonedDateTime> parsed = TemporalAdapter.parse(dateString, expectedZone)
        parsed.temporal.zone == expectedZone

        and:
        parsed.toString(expectedZone) == dateString

        where:
        dateString              | expectedType      | expectedZone
        '20150504T120000'       | ZonedDateTime     | ZoneId.systemDefault()
        '20150504T120000'       | ZonedDateTime     | ZoneId.of("Australia/Melbourne")
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
        def adapter = TemporalAdapter.parse(dateString, ZoneId.systemDefault())
        adapter.temporal as String

        then:
        thrown(DateTimeParseException)

        where:
        dateString << ['20150504T120000Z']
    }

    def 'verify isbefore works correctly'() {
        expect: 'isbefore result matches expected'
        TemporalAdapter.isBefore(t1, t2) == isBefore

        where:
        t1  | t2    | isBefore
        Instant.now()   | LocalDate.now()   | false
    }
}
