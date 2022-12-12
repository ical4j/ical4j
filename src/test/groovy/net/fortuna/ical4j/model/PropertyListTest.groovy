package net.fortuna.ical4j.model


import net.fortuna.ical4j.model.property.CalScale
import spock.lang.Specification

class PropertyListTest extends Specification {

    def 'test list append with groovy left shift operator'() {
        given: 'a property list'
        PropertyList list = []

        when: 'a property is appended with the left shift operator'
        list << CalScale.GREGORIAN

        then: 'it is added to the list'
        list.calscale[0] == CalScale.GREGORIAN
    }

    def 'test list concat with groovy left shift operator'() {
        given: 'a parameter list'
        PropertyList list = []

        when: 'another property list is appended with the left shift operator'
        list << new PropertyList() << CalScale.GREGORIAN

        then: 'it is added to the list'
        list.calscale[0] == CalScale.GREGORIAN
    }
}
