package net.fortuna.ical4j.model


import spock.lang.Specification

import static net.fortuna.ical4j.model.property.immutable.ImmutableCalScale.GREGORIAN
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.PUBLISH
import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0

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

    def 'test comparison of lists'() {
        expect: 'list comparison matches expected'
        new PropertyList(list1) <=> new PropertyList(list2) == expectedResult

        where:
        list1   | list2 | expectedResult
        []      | []    | 0
        [VERSION_2_0] | [VERSION_2_0] | 0
        [VERSION_2_0] | [GREGORIAN]   | 1
        [VERSION_2_0, GREGORIAN] | [GREGORIAN]   | 1
        [GREGORIAN] | [VERSION_2_0, GREGORIAN]   | -1
        [GREGORIAN, PUBLISH] | [VERSION_2_0, GREGORIAN] | 1
        [GREGORIAN, PUBLISH] | [PUBLISH, GREGORIAN] | 0
    }
}
