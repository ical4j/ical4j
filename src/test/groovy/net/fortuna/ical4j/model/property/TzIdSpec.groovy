package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import spock.lang.Specification

import java.time.DateTimeException

class TzIdSpec extends Specification {

    def 'test successfull instance creation'() {
        expect: 'instance is created successfully'
        new TzId.Factory().createProperty([] as ParameterList, tzIdString) as String == "TZID:$expectedString\r\n"

        where:
        tzIdString              | expectedString
        'Australia/Melbourne'   | 'Australia/Melbourne'
        'UTC+10'                | 'UTC+10:00'
    }

    def 'test custom zone id instance creation'() {
        given: 'custom zone ids are registered'

        expect: 'instance is created successfully'
        new TzId.Factory().createProperty([] as ParameterList, tzIdString) as String == "TZID:$expectedString\r\n"

        where:
        tzIdString                          | expectedString
        '/tzurl.org/Australia/Melbourne'    | '/tzurl.org/Australia/Melbourne'
        'Canberra, Melbourne, Sydney'       | 'Canberra, Melbourne, Sydney'
    }

    def 'test unsuccessfull instance creation'() {
        when: 'attempt to create instance'
        new TzId.Factory().createProperty([] as ParameterList, tzIdString)

        then: 'instance is not created successfully'
        thrown(expectedException)

        where:
        tzIdString                          | expectedException
        'Unknown'                           | DateTimeException
        '/tzurl.org/Australia/Melbourne'    | DateTimeException
        'Canberra, Melbourne, Sydney'       | DateTimeException
    }
}
