package net.fortuna.ical4j.agent

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Method
import spock.lang.Specification

class VToDoUserAgentTest extends Specification {

    VToDoUserAgent userAgent = []

    ContentBuilder builder = []

    def "Publish"() {
        given: 'multiple vtodo instances'
        def vtodo = builder.vtodo {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        def vtodo2 = builder.vtodo {
            uid '2'
            dtstamp()
            dtstart '20090811', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'the events are published'
        def calendar = userAgent.publish(vtodo, vtodo2)

        then: 'the calendar object contains method = PUBLISH'
        calendar.getProperty(Property.METHOD) == Method.PUBLISH

        and: 'the sequence property is present on all components'
        calendar.components.each { it.getProperty(Property.SEQUENCE) }
    }

    def "Request"() {
        given: 'multiple vtodo instances'
        def vtodo = builder.vtodo {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        def vtodo2 = builder.vtodo {
            uid '1'
            dtstamp()
            dtstart '20090811', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'the events are published'
        def calendar = userAgent.request(vtodo, vtodo2)

        then: 'the calendar object contains method = REQUEST'
        calendar.getProperty(Property.METHOD) == Method.REQUEST

        and: 'the sequence property is present on all components'
        calendar.components.each { it.getProperty(Property.SEQUENCE) }
    }

    def "Reply"() {
        given: 'an event request'
        def vtodo = builder.vtodo {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a reply is generated'
        def calendar = userAgent.reply(vtodo)

        then: 'the calendar object contains method = REPLY'
        calendar.getProperty(Property.METHOD) == Method.REPLY
    }

    def "Add"() {
        given: 'an event'
        def vtodo = builder.vtodo {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event recurrence is added'
        def calendar = userAgent.add(vtodo)

        then: 'the calendar object contains method = ADD'
        calendar.getProperty(Property.METHOD) == Method.ADD
    }

    def "Cancel"() {
        given: 'an event'
        def vtodo = builder.vtodo {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event recurrence is cancelled'
        def calendar = userAgent.cancel(vtodo)

        then: 'the calendar object contains method = CANCEL'
        calendar.getProperty(Property.METHOD) == Method.CANCEL
    }

    def "Refresh"() {
        given: 'an event'
        def vtodo = builder.vtodo {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event refresh is generated'
        def calendar = userAgent.refresh(vtodo)

        then: 'the calendar object contains method = REFRESH'
        calendar.getProperty(Property.METHOD) == Method.REFRESH
    }

    def "Counter"() {
        given: 'an event request'
        def vtodo = builder.vtodo {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event counter is generated'
        def calendar = userAgent.counter(vtodo)

        then: 'the calendar object contains method = COUNTER'
        calendar.getProperty(Property.METHOD) == Method.COUNTER
    }

    def "DeclineCounter"() {
        given: 'an event counter'
        def vtodo = builder.vtodo {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event decline-counter is generated'
        def calendar = userAgent.declineCounter(vtodo)

        then: 'the calendar object contains method = DECLINECOUNTER'
        calendar.getProperty(Property.METHOD) == Method.DECLINE_COUNTER
    }
}
