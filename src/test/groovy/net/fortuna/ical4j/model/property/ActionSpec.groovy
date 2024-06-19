package net.fortuna.ical4j.model.property


import spock.lang.Specification

import static net.fortuna.ical4j.model.property.immutable.ImmutableAction.*

class ActionSpec extends Specification {

    Action.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def action = factory.createProperty(value)

        then: 'the returned value is the constant instance'
        action.is(constantInstance)

        where:
        value   | constantInstance
        'AUDIO' | AUDIO
        'DISPLAY' | DISPLAY
        'EMAIL' | EMAIL
        'PROCEDURE' | PROCEDURE
    }
}
