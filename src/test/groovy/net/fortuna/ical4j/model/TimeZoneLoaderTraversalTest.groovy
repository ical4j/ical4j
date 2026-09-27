package net.fortuna.ical4j.model

import spock.lang.Specification

class TimeZoneLoaderTraversalTest extends Specification {

    def 'reject timezone ids that traverse outside the resource prefix'() {
        given: 'a timezone loader instance'
        TimeZoneLoader loader = ['zoneinfo/']

        expect: 'a traversal id does not resolve a resource outside zoneinfo'
        loader.loadVTimeZone(id) == null

        where:
        id << ['../net/fortuna/ical4j/model/traversal-target',
               'Australia/../../net/fortuna/ical4j/model/traversal-target',
               '..\\net\\fortuna\\ical4j\\model\\traversal-target']
    }

    def 'legitimate timezone ids still resolve'() {
        given: 'a timezone loader instance'
        TimeZoneLoader loader = ['zoneinfo/']

        expect: 'a valid id containing a separator resolves normally'
        loader.loadVTimeZone(id)?.timeZoneId?.value == id

        where:
        id << ['Australia/Melbourne', 'Europe/London']
    }
}
