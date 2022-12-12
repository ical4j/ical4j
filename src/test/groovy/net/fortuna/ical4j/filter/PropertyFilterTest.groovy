package net.fortuna.ical4j.filter

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.parameter.Role
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.Organizer
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors

class PropertyFilterTest extends Specification {

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

    def 'test filter expression equals parameter'() {
        given: 'a filter expression'
        def filter = FilterExpression.equalTo('role', Role.CHAIR.value)

        and: 'an event'
        VEvent event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'a filtered list of attendees matches expected'
        event.getProperties('attendee').stream()
                .filter(new PropertyFilter().predicate(filter)).collect(Collectors.toList()).size() == 0
    }

    def 'test filter expression missing parameter'() {
        given: 'a filter expression'
        def filter = FilterExpression.notExists('role:CHAIR')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        new PropertyFilter().predicate(filter).test(organiser)
    }
}
