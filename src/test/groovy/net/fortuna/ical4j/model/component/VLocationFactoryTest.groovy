package net.fortuna.ical4j.model.component

import net.fortuna.ical4j.model.ContentBuilder
import spock.lang.Specification

class VLocationFactoryTest extends Specification {

    def 'verify vlocation creation'() {
        given: 'a content builder'
        ContentBuilder builder = []

        when: 'a vlocation spec is provided'
        VLocation location = builder.vlocation() {
            uid '1'
        }

        then: 'result is as expected'
        location.getProperty('UID').value == '1'
    }
}
