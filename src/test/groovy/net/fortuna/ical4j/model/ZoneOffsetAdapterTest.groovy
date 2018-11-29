package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.ZoneOffset

class ZoneOffsetAdapterTest extends Specification {

    def "verify string representation"() {
        expect:
        new ZoneOffsetAdapter(offset) as String == expectedValue

        where:
        offset                        | expectedValue
        ZoneOffset.ofHours(4)   | "+0400"
        ZoneOffset.ofHoursMinutes(5, 30)   | "+0530"
        ZoneOffset.ofHoursMinutesSeconds(-6, -30, -15)   | "-063015"
    }

    def 'verify zoneoffset creation'() {
        expect:
        ZoneOffsetAdapter.from(utcOffset) == expectedOffset

        where:
        utcOffset       | expectedOffset
        new UtcOffset('+1000')  | ZoneOffset.ofHours(10)
        new UtcOffset('-0600')  | ZoneOffset.ofHours(-6)
        new UtcOffset('+0545')  | ZoneOffset.ofHoursMinutes(5, 45)
    }

}
