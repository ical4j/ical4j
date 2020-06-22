package net.fortuna.ical4j.model.property


import spock.lang.Specification

class CalScaleSpec extends Specification {

    CalScale.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def calScale = factory.createProperty(value)

        then: 'the returned value is the constant instance'
        calScale.is(constantInstance)

        where:
        value   | constantInstance
        'GREGORIAN' | CalScale.GREGORIAN
    }
}
