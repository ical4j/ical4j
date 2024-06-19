package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.property.Contact
import net.fortuna.ical4j.model.property.Version
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Ignore
import spock.lang.Specification

import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0

class PropertyBuilderTest extends Specification {

    def 'test build property'() {
        given: 'a property builder instance'
        PropertyBuilder builder = [Arrays.asList(new Version.Factory())]

        and: 'builder is initialised'
        builder.name('version').value("2.0")

        when: 'build method called'
        Property p = builder.build()

        then: 'resulting property is initialised accordingly'
        p == VERSION_2_0
    }

    @Ignore
    def 'test build invalid property'() {
        given: 'a property builder instance'
        PropertyBuilder builder = [Arrays.asList(new Version.Factory())]

        and: 'builder is initialised'
        builder.name('dtend').value('20150403')

        and: 'relaxed validation is disabled'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, false)

        when: 'build method called'
        Property p = builder.build()

        then: 'an exception is thrown'
        thrown(IllegalArgumentException)
    }

    def 'test build encoded property'() {
        given: 'a property builder instance'
        PropertyBuilder builder = []

        and: 'builder is initialised'
        builder.name('X-MICROSOFT-LOCATIONS').value('[{\\"DisplayName\\":\\"Microsoft Teams Meeting\\"\\, \\"LocationCode\\":\\"013454\\"}]')

        when: 'build method called'
        Property p = builder.build()

        then: 'resulting property is initialised accordingly'
        p.value == '[{"DisplayName":"Microsoft Teams Meeting", "LocationCode":"013454"}]'
    }

    def 'test build property with a prefix'() {
        given: 'a property builder instance'
        PropertyBuilder builder = [Arrays.asList(new Contact.Factory())]

        and: 'builder is initialised'
        builder.name('work.contact').value('mailto:work@example.com')

        when: 'build method called'
        Property p = builder.build()

        then: 'resulting property is initialised accordingly'
        p.class == Contact && p.prefix == 'work' && p.value == 'mailto:work@example.com'
    }
}
