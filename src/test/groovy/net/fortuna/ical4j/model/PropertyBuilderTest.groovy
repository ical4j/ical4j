package net.fortuna.ical4j.model


import net.fortuna.ical4j.model.property.Version
import spock.lang.Ignore
import spock.lang.Specification

class PropertyBuilderTest extends Specification {

    def 'test build property'() {
        given: 'a property builder instance'
        PropertyBuilder builder = []

        and: 'builder is initialised'
        builder.factories(Arrays.asList(new Version.Factory())).name('version').value("2.0")

        when: 'build method called'
        Property p = builder.build()

        then: 'resulting property is initialised accordingly'
        p == Version.VERSION_2_0
    }

    @Ignore
    def 'test build invalid property'() {
        given: 'a property builder instance'
        PropertyBuilder builder = []

        and: 'builder is initialised'
        builder.factories(Arrays.asList(new Version.Factory())).name('dtend').value('20150403')

        when: 'build method called'
        Property p = builder.build()

        then: 'an exception is thrown'
        thrown(IllegalArgumentException)
    }
}
