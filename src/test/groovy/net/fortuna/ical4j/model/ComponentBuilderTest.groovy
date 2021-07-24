package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.component.VEvent
import spock.lang.Specification

class ComponentBuilderTest extends Specification {

    def 'test build component'() {
        given: 'a component builder instance'
        ComponentBuilder builder = [Arrays.asList(new VEvent.Factory())]

        and: 'builder is initialised'
        builder.name('vevent')

        when: 'build method called'
        Component c = builder.build()

        then: 'resulting component is initialised accordingly'
        c == new ContentBuilder().vevent()
    }

    def 'test build invalid component'() {
        given: 'a component builder instance'
        ComponentBuilder builder = [Arrays.asList(new VEvent.Factory())]

        and: 'builder is initialised'
        builder.name('vtodo')

        when: 'build method called'
        Component c = builder.build()

        then: 'an exception is thrown'
        thrown(IllegalArgumentException)
    }
}
