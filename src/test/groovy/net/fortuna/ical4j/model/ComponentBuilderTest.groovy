package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.component.VEvent
import spock.lang.Specification

class ComponentBuilderTest extends Specification {

    def 'test build component'() {
        given: 'a component builder instance'
        ComponentBuilder builder = []

        and: 'builder is initialised'
        builder.factories(Arrays.asList(new VEvent.Factory())).name('vevent')

        when: 'build method called'
        Component c = builder.build()

        then: 'resulting component is initialised accordingly'
        c == new ContentBuilder().vevent()
    }

    def 'test build invalid component'() {
        given: 'a component builder instance'
        ComponentBuilder builder = []

        and: 'builder is initialised'
        builder.factories(Arrays.asList(new VEvent.Factory())).name('vtodo')

        when: 'build method called'
        Component c = builder.build()

        then: 'an exception is thrown'
        thrown(IllegalArgumentException)
    }
}
