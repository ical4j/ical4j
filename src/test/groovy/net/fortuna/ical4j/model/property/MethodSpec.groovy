package net.fortuna.ical4j.model.property


import spock.lang.Specification

import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.*

class MethodSpec extends Specification {

    Method.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def method = factory.createProperty(value)

        then: 'the returned value is the constant instance'
        method.is(constantInstance)

        where:
        value   | constantInstance
        'ADD' | ADD
        'CANCEL' | CANCEL
        'COUNTER' | COUNTER
        'DECLINECOUNTER' | DECLINE_COUNTER
        'PUBLISH' | PUBLISH
        'REFRESH' | REFRESH
        'REPLY' | REPLY
        'REQUEST' | REQUEST
    }
}
