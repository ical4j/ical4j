package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.component.XComponent
import net.fortuna.ical4j.util.CompatibilityHints
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

    def 'test build unrecognised component'() {
        given: 'a component builder instance'
        ComponentBuilder builder = [Arrays.asList(new VEvent.Factory())]

        and: 'builder is initialised'
        builder.name('vtodo')

        and: 'relaxed validation is disabled'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, false)

        when: 'build method called'
        Component c = builder.build()

        then: 'component vtodo is unrecognised'
        c instanceof XComponent
    }
}
