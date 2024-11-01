package net.fortuna.ical4j.filter

import net.fortuna.ical4j.filter.expression.BooleanExpression
import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.Organizer
import net.fortuna.ical4j.model.property.Summary
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class ComponentFilterTest extends Specification {

    @Shared
    ContentBuilder builder

    @Shared
    Organizer organiser

    @Shared
    Attendee attendee

    @Shared
    Summary summary

    def setupSpec() {
        builder = new ContentBuilder()
        organiser = builder.organizer('Mailto:B@example.com')
        attendee = builder.attendee('Mailto:A@example.com')
        summary = builder.summary('Test')
    }

    def 'test filter expression equals'() {
        given: 'a filter expression'
        def expression = FilterExpression.equalTo('organizer', organiser.value)
                & FilterExpression.equalTo('attendee', attendee.value)

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        new ComponentFilter().predicate(expression).test(event)
    }

    def 'test filter expression comparison'() {
        given: 'a filter expression'
        def filter = FilterExpression.lessThanEqual('due', '20210801T000000')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
            due '20210727T000000'
        }

        expect: 'filter matches the event'
        new ComponentFilter().predicate(filter).test(event)
    }

    @Ignore
    def 'test filter expression comparison function'() {
        given: 'a filter expression'
        def filter = FilterExpression.lessThanEqual('due', 'now(-P1D)')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
            due '20210727T000000'
        }

        expect: 'filter matches the event'
        new ComponentFilter().predicate(filter).test(event)
    }

    def 'test filter expression in'() {
        given: 'a filter expression'
        def filter = FilterExpression.in('organizer', [organiser.value, attendee.value])

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        new ComponentFilter().predicate(filter).test(event)
    }

    def 'test filter expression contains'() {
        given: 'a filter expression'
        def filter = FilterExpression.contains('attendee', 'example.com')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        new ComponentFilter().predicate(filter).test(event)
    }

    def 'test filter expression starts with'() {
        given: 'a filter expression'
        def filter = FilterExpression.startsWith('attendee', 'Mailto:A')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        new ComponentFilter().predicate(filter).test(event)
    }

    def 'test filter expression missing'() {
        given: 'a filter expression'
        def filter = FilterExpression.notExists('due')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        new ComponentFilter().predicate(filter).test(event)
    }

    def 'test filter expression not missing'() {
        given: 'a filter expression'
        def filter = FilterExpression.exists('attendee')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'filter matches the event'
        new ComponentFilter().predicate(filter).test(event)
    }

    def 'test filter expression parsing'() {
        given: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
            sequence('2')
        }

        expect: 'filter matches the event'
        new ComponentFilter().predicate(FilterExpression.parse(expression)).test(event) == expectedResult

        where: 'filter expression'
        expression                                                          | expectedResult
//        "organizer= '${organiser.value}' and attendee ='${attendee.value}'" | true
//        "organizer in ['${organiser.value}', '${attendee.value}']"          | true
        'attendee contains "example.com"'                                   | true
        'attendee contains "C@example.com"'                                 | false
//        'organizer startsWith "Mailto:B"'                                   | true
        'due not exists'                                                    | true
        'organizer not exists'                                              | false
        'attendee exists'                                                   | true
        'request-status not exists'                                         | true
        'sequence > 1'                                                      | true
    }

    def 'test filter expressions with sets'() {
        given: 'a filter expression using sets'
        def filter = FilterExpression.in(Property.SUMMARY, ['Test'] as Set)

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
            summary(summary)
        }

        expect: 'filter matches the event'
        new ComponentFilter().predicate(filter).test(event)
    }

    def 'test filter expressions with boolean literals'() {
        given: 'a filter expression using boolean literals to make an expression never match'
        def neverMatch = BooleanExpression.FALSE & FilterExpression.exists('organizer')

        and: 'an expression to always match'
        def alwaysMatch = BooleanExpression.TRUE | FilterExpression.exists('dtstart')

        and: 'an event'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
            summary(summary)
        }

        expect: 'filter never matches the event'
        !new ComponentFilter().predicate(neverMatch).test(event)

        and: 'filter always matches the event'
        new ComponentFilter().predicate(alwaysMatch).test(event)
    }
}
