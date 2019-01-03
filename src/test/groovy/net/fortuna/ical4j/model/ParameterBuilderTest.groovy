package net.fortuna.ical4j.model


import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

class ParameterBuilderTest extends Specification {

    def 'test build parameter'() {
        given: 'a parameter builder instance'
        ParameterBuilder builder = []

        and: 'builder is initialised'
        builder.factories(Arrays.asList(new Value.Factory())).name('value').value("test")

        when: 'build method called'
        Parameter p = builder.build()

        then: 'resulting parameter is initialised accordingly'
        p == new ContentBuilder().value('test')
    }

    def 'test build invalid parameter'() {
        given: 'a parameter builder instance'
        ParameterBuilder builder = []

        and: 'builder is initialised'
        builder.factories(Arrays.asList(new Value.Factory())).name('type')

        when: 'build method called'
        Parameter p = builder.build()

        then: 'an exception is thrown'
        thrown(IllegalArgumentException)
    }
}
