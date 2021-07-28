package net.fortuna.ical4j.filter

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.Organizer
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by fortuna on 24/07/2017.
 */
class PropertyEqualToRuleTest extends Specification {

    @Shared
    ContentBuilder builder

    @Shared
    Organizer organiser

    @Shared
    Attendee attendee

    def setupSpec() {
        builder = new ContentBuilder()
        organiser = builder.organizer('Mailto:B@example.com')
        attendee = builder.attendee('Mailto:A@example.com')
    }

    def "Evaluate"() {
        given: 'a component with two properties'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'a property rule matches when applied'
        rule.test(event)

        where:
        rule << [new PropertyEqualToRule<VEvent>(organiser), new PropertyEqualToRule<VEvent>(attendee)]
    }
}
