package net.fortuna.ical4j.model

import spock.lang.Specification

class LocationTypeTest extends Specification {

    def 'assert string parsing'() {
        expect:
        LocationType.from(locationTypeString) == expectedType

        where:
        locationTypeString  | expectedType
        "public"            | LocationType.public_
        "bus-station"       | LocationType.bus_station
    }

    def 'assert string formatting'() {
        expect:
        locationType as String == expectedString

        where:
        locationType                | expectedString
        LocationType.public_     | "public"
        LocationType.bus_station | "bus-station"
    }
}
