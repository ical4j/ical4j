package net.fortuna.ical4j.util

import net.fortuna.ical4j.model.WeekDay
import spock.lang.Specification

class ConfiguratorTest extends Specification {

    def 'assert retrieval of empty configuration property'() {
        given: 'an unconfigured property key'
        def key = 'unconfigured.property.key'

        when: 'retrieving the property'
        def prop = Configurator.getProperty(key)

        then: 'property is an empty optional'
        prop == Optional.empty()
    }

    def 'assert retrieval of configured property'() {
        given: 'an existing property key'
        def key = 'net.fortuna.ical4j.timezone.update.enabled'

        when: 'retrieving the property'
        def prop = Configurator.getProperty(key)

        then: 'property is a non-empty optional'
        prop.isPresent()
    }

    def 'assert retrieval of system property'() {
        given: 'an existing system property key'
        def key = 'java.io.tmpdir'

        when: 'retrieving the property'
        def prop = Configurator.getProperty(key)

        then: 'property is a non-empty optional'
        prop.isPresent()
    }

    def 'assert retrieval of integer property'() {
        given: 'an existing system property key'
        def key = "${ConfiguratorTest}.intProp"
        System.setProperty(key, '5')

        when: 'retrieving the property'
        def prop = Configurator.getIntProperty(key)

        then: 'property is a non-empty optional'
        prop.isPresent() && prop.get() == 5
    }

    def 'assert retrieval of enum property'() {
        given: 'an existing system property key'
        def key = "${ConfiguratorTest}.enumProp"
        System.setProperty(key, 'SU')

        when: 'retrieving the property'
        def prop = Configurator.getEnumProperty(WeekDay.Day.class, key)

        then: 'property is a non-empty optional'
        prop.isPresent() && prop.get() == WeekDay.Day.SU
    }

    def 'assert retrieval of object property'() {
        given: 'an existing system property key'
        def key = "${ConfiguratorTest}.objectProp"
        System.setProperty(key, 'java.util.Date')

        when: 'retrieving the property'
        def prop = Configurator.getObjectProperty(key)

        then: 'property is a non-empty optional'
        prop.isPresent() && prop.get() instanceof Date
    }
}
