package net.fortuna.ical4j.model.property


import spock.lang.Specification

import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0

class VersionSpec extends Specification {

    Version.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def version = factory.createProperty(value)

        then: 'the returned value is the constant instance'
        version.is(constantInstance)

        where:
        value   | constantInstance
        '2.0' | VERSION_2_0
    }
}
