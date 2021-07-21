package net.fortuna.ical4j.model.component

import net.fortuna.ical4j.model.ContentBuilder
import spock.lang.Specification

class VResourceFactoryTest extends Specification {

    def 'verify vresource creation'() {
        given: 'a content builder'
        ContentBuilder builder = []

        when: 'a vresource spec is provided'
        VResource resource = builder.vresource() {
            uid '1'
        }

        then: 'result is as expected'
        resource.properties.uid[0].value == '1'
    }
}
