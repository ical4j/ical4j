package net.fortuna.ical4j.model


import net.fortuna.ical4j.model.parameter.Value
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification

class ParameterBuilderTest extends Specification {

    def setupSpec() {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false)
    }

    def 'test build parameter'() {
        given: 'a parameter builder instance'
        ParameterBuilder builder = [Arrays.asList(new Value.Factory())]

        and: 'builder is initialised'
        builder.name('value').value("test")

        when: 'build method called'
        Parameter p = builder.build()

        then: 'resulting parameter is initialised accordingly'
        p == new ContentBuilder().value('test')
    }

    def 'test build invalid parameter'() {
        given: 'a parameter builder instance'
        ParameterBuilder builder = [Arrays.asList(new Value.Factory())]

        and: 'builder is initialised'
        builder.name('type')

        when: 'build method called'
        Parameter p = builder.build()

        then: 'an exception is thrown'
        thrown(IllegalArgumentException)
    }
}
