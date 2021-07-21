package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

class ParameterListTest extends Specification {

    def 'test list append with groovy left shift operator'() {
        given: 'a parameter list'
        ParameterList list = []

        when: 'a parameter is appended with the left shift operator'
        list << Value.BINARY

        then: 'it is added to the list'
        list.value[0] == Value.BINARY
    }

    def 'test list concat with groovy left shift operator'() {
        given: 'a parameter list'
        ParameterList list = []

        when: 'another parameter list is appended with the left shift operator'
        list << new ParameterList() << Value.BINARY

        then: 'it is added to the list'
        list.value[0] == Value.BINARY
    }
}
