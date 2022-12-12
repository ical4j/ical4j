package net.fortuna.ical4j.filter


import spock.lang.Specification

class FilterExpressionTest extends Specification {

    def 'test invalid filter expression parsing'() {
        when: 'an attempt to parse an invalid filter'
        FilterExpression.parse(expression)

        then: 'exception thrown for invalid filter expressions'
        thrown(IllegalArgumentException)

        where: 'filter expression'
        expression << [
            "organizer= <invalidchar and attendee =example",
            "organizer in example, example",
            'attendee contains example.com',
            'missing due',
            '!organizer missing',
        ]
    }
}
