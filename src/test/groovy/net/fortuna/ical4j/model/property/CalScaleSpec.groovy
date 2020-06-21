package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import spock.lang.Specification

class CalScaleSpec extends Specification {

    CalScale.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def calScale = factory.createProperty(new ParameterList(), value)

        then: 'the returned value is the constant instance'
        calScale.is(constantInstance)

        where:
        value   | constantInstance
        'GREGORIAN' | CalScale.GREGORIAN
    }
}
