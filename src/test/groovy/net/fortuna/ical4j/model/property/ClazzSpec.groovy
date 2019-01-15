package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import spock.lang.Specification

class ClazzSpec extends Specification {

    Clazz.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def clazz = factory.createProperty(new ParameterList(), value)

        then: 'the returned value is the constant instance'
        clazz.is(constantInstance)

        where:
        value   | constantInstance
        'CONFIDENTIAL' | Clazz.CONFIDENTIAL
        'PRIVATE' | Clazz.PRIVATE
        'PUBLIC' | Clazz.PUBLIC
    }
}
