package net.fortuna.ical4j.model.component

import net.fortuna.ical4j.model.ContentBuilder
import spock.lang.Specification

class VAvailabilityFactoryTest extends Specification {

    def 'verify vavailability creation'() {
        given: 'a content builder'
        ContentBuilder builder = []

        when: 'a vavailability spec is provided'
        VAvailability availability = builder.vavailability() {
            uid '1'
            available {
                uid '11'
            }
        }

        then: 'result is as expected'
        availability.propertyList.uid[0].value == '1'
        availability.getComponents().size() == 1
        availability.getComponents()[0].propertyList.uid[0].value == '11'
    }
}
