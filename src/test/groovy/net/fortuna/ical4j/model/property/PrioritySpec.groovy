package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import spock.lang.Specification

class PrioritySpec extends Specification {

    Priority.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def priority = factory.createProperty(new ParameterList(), value)

        then: 'the returned value is the constant instance'
        priority.is(constantInstance)

        where:
        value   | constantInstance
        '9' | Priority.LOW
        '5' | Priority.MEDIUM
        '1' | Priority.HIGH
    }
}
