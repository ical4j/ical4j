package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import spock.lang.Specification

class TranspSpec extends Specification {

    Transp.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def transp = factory.createProperty(new ParameterList(), value)

        then: 'the returned value is the constant instance'
        transp.is(constantInstance)

        where:
        value   | constantInstance
        'OPAQUE' | Transp.OPAQUE
        'TRANSPARENT' | Transp.TRANSPARENT
    }
}
