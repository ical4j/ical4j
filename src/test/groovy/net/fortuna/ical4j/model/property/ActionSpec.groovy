package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import spock.lang.Specification

class ActionSpec extends Specification {

    Action.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def action = factory.createProperty(new ParameterList(), value)

        then: 'the returned value is the constant instance'
        action.is(constantInstance)

        where:
        value   | constantInstance
        'AUDIO' | Action.AUDIO
        'DISPLAY' | Action.DISPLAY
        'EMAIL' | Action.EMAIL
        'PROCEDURE' | Action.PROCEDURE
    }
}
