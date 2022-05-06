package net.fortuna.ical4j.model


import spock.lang.Specification

import static net.fortuna.ical4j.model.property.immutable.ImmutableCalScale.GREGORIAN

class PropertyListTest extends Specification {

    def 'test list append with groovy left shift operator'() {
        given: 'a property list'
        PropertyList list = []

        when: 'a property is appended with the left shift operator'
        list = list << GREGORIAN

        then: 'it is added to the list'
        list.calscale[0] == GREGORIAN
    }

    def 'test list concat with groovy left shift operator'() {
        given: 'a parameter list'
        PropertyList list = []

        when: 'another property list is appended with the left shift operator'
        list = list << new PropertyList() << GREGORIAN

        then: 'it is added to the list'
        list.calscale[0] == GREGORIAN
    }
}
