package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.ZoneOffset

class ZoneOffsetFactoryTest extends Specification {

    ZoneOffsetFactory factory = []

    def 'verify zoneoffset creation'() {
        expect:
        factory.create(utcOffset) == expectedOffset

        where:
        utcOffset       | expectedOffset
        new UtcOffset('+1000')  | ZoneOffset.ofHours(10)
        new UtcOffset('-0600')  | ZoneOffset.ofHours(-6)
        new UtcOffset('+0545')  | ZoneOffset.ofHoursMinutes(5, 45)
    }
}
