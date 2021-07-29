package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification

class PrioritySpec extends Specification {

    Priority.Factory factory = []

    def cleanup() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)
    }

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

    def 'test relaxed parsing with invalid values'() {
        when: 'relaxed parsing is enabled'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

        and: 'factory is invoked with invalid value'
        def priority = factory.createProperty(new ParameterList(), value)

        then: 'the returned priority is UNDEFINED'
        priority == Priority.UNDEFINED

        where:
        value << ['', 'low', 'blah']
    }
}
