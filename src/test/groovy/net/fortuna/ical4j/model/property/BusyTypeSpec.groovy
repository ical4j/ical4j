package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import spock.lang.Specification

class BusyTypeSpec extends Specification {

    BusyType.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def busyType = factory.createProperty(new ParameterList(), value)

        then: 'the returned value is the constant instance'
        busyType.is(constantInstance)

        where:
        value   | constantInstance
        'BUSY-UNAVAILABLE' | BusyType.BUSY_UNAVAILABLE
        'BUSY-TENTATIVE' | BusyType.BUSY_TENTATIVE
    }
}
