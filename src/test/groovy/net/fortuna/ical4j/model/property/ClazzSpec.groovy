package net.fortuna.ical4j.model.property


import spock.lang.Specification

import static net.fortuna.ical4j.model.property.immutable.ImmutableClazz.*

class ClazzSpec extends Specification {

    Clazz.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def clazz = factory.createProperty(value)

        then: 'the returned value is the constant instance'
        clazz.is(constantInstance)

        where:
        value   | constantInstance
        'CONFIDENTIAL' | CONFIDENTIAL
        'PRIVATE' | PRIVATE
        'PUBLIC' | PUBLIC
    }
}
