package net.fortuna.ical4j.filter

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.parameter.Role
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.Organizer
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors

class FilterExpressionTest extends Specification {

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

    def 'test filter expression equals'() {
        given: 'a filter expression'
        def filter = new FilterExpression().equalTo('organizer', organiser.value)
            .equalTo('attendee', attendee.value)

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        filter.toComponentPredicate().test(event)
    }

    @Ignore
    def 'test filter expression equals function'() {
        given: 'a filter expression'
        def filter = new FilterExpression().lessThanEqual('due', 'now(-P1D)')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
            due '20210727T000000'
        }

        expect: 'filter matches the event'
        filter.toComponentPredicate().test(event)
    }

    def 'test filter expression equals parameter'() {
        given: 'a filter expression'
        def filter = new FilterExpression().equalTo('role', Role.CHAIR)

        and: 'an event'
        VEvent event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'a filtered list of attendees matches expected'
        event.getProperties('attendee').stream()
                .filter(filter.toParameterPredicate()).collect(Collectors.toList()).size() == 0
    }

    def 'test filter expression in'() {
        given: 'a filter expression'
        def filter = new FilterExpression().in('organizer', [organiser.value, attendee.value])

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        filter.toComponentPredicate().test(event)
    }

    def 'test filter expression contains'() {
        given: 'a filter expression'
        def filter = new FilterExpression().contains('attendee', 'example.com')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        filter.toComponentPredicate().test(event)
    }

    def 'test filter expression missing'() {
        given: 'a filter expression'
        def filter = new FilterExpression().notExists('due')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        filter.toComponentPredicate().test(event)
    }

    def 'test filter expression missing parameter'() {
        given: 'a filter expression'
        def filter = new FilterExpression().notExists('role:CHAIR')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        filter.toParameterPredicate().test(event.properties)
    }

    def 'test filter expression not missing'() {
        given: 'a filter expression'
        def filter = new FilterExpression().notExists('attendee')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        filter.toComponentPredicate().negate().test(event)
    }

    def 'test filter expression parsing'() {
        given: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
            sequence('2')
        }

        expect: 'filter matches the event'
        FilterExpression.parse(expression).toComponentPredicate().test(event) == expectedResult

        where: 'filter expression'
        expression                                                      | expectedResult
        "organizer= ${organiser.value} and attendee =${attendee.value}" | true
        "organizer in [${organiser.value}, ${attendee.value}]"          | true
        'attendee contains "example.com"'                               | true
        'attendee contains "C@example.com"'                             | false
        'due not exists'                                                | true
        'organizer not exists'                                          | false
        'attendee exists'                                               | true
        'request-status not exists'                                     | true
        'sequence > 1'                                                  | true
    }

    def 'test invalid filter expression parsing'() {
        when: 'an attempt to parse an invalid filter'
        FilterExpression.parse(expression)

        then: 'exception thrown for invalid filter expressions'
        thrown(IllegalArgumentException)

        where: 'filter expression'
        expression << [
            "organizer= <invalidchar and attendee =${attendee.value}",
            "organizer in ${organiser.value}, ${attendee.value}",
            'attendee contains example.com',
            'missing due',
            '!organizer missing',
        ]
    }
}
