package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import spock.lang.Specification

class MethodSpec extends Specification {

    Method.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def method = factory.createProperty(new ParameterList(), value)

        then: 'the returned value is the constant instance'
        method.is(constantInstance)

        where:
        value   | constantInstance
        'ADD' | Method.ADD
        'CANCEL' | Method.CANCEL
        'COUNTER' | Method.COUNTER
        'DECLINECOUNTER' | Method.DECLINE_COUNTER
        'PUBLISH' | Method.PUBLISH
        'REFRESH' | Method.REFRESH
        'REPLY' | Method.REPLY
        'REQUEST' | Method.REQUEST
    }
}
