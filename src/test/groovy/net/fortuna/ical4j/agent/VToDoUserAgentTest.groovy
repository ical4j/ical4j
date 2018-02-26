package net.fortuna.ical4j.agent

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.*
import net.fortuna.ical4j.util.RandomUidGenerator
import net.fortuna.ical4j.util.UidGenerator
import spock.lang.Specification

class VToDoUserAgentTest extends Specification {

    ProdId prodId = ['-//Ben Fortuna//iCal4j 2.0//EN']
    UidGenerator uidGenerator = new RandomUidGenerator()
    Organizer org = []

    VToDoUserAgent userAgent = [prodId, org, uidGenerator]

    ContentBuilder builder = []

    def "Publish"() {
        given: 'multiple vtodo instances'
        def vtodo = builder.vtodo {
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            summary 'Task 1'
            priority(Priority.HIGH)
        }

        def vtodo2 = builder.vtodo {
            dtstamp()
            dtstart '20090811', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            summary 'Task 2'
            priority(Priority.LOW)
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
            attendee 'mailto:org@example.com'
            summary 'Task 1'
            priority(Priority.HIGH)
        }

        def vtodo2 = builder.vtodo {
            uid '1'
            dtstamp()
            dtstart '20090811', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            attendee 'mailto:org@example.com'
            summary 'Task 2'
            priority(Priority.LOW)
        }

        when: 'the events are published'
        def calendar = userAgent.request(vtodo, vtodo2)

        then: 'the calendar object contains method = REQUEST'
        calendar.getProperty(Property.METHOD) == Method.REQUEST

        and: 'the sequence property is present on all components'
        calendar.components.each { it.getProperty(Property.SEQUENCE) }
    }

    def "Delegate"() {
        given: 'a todo request'
        def request = builder.calendar {
            prodid(prodId)
            version(Version.VERSION_2_0)
            method(Method.REQUEST)
            vtodo {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
                attendee 'mailto:org@example.com'
                organizer 'mailto:org@example.com'
                summary 'Task 1'
                priority(Priority.HIGH)
            }
        }

        when: 'the request is delegated'
        def calendar = userAgent.delegate(request)

        then: 'the calendar object contains method = REQUEST'
        calendar.getProperty(Property.METHOD) == Method.REQUEST
    }

    def "Reply"() {
        given: 'an event request'
        def request = builder.calendar {
            prodid(prodId)
            version(Version.VERSION_2_0)
            method(Method.REQUEST)
            vtodo {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
                attendee 'mailto:org@example.com'
                organizer 'mailto:org@example.com'
            }
        }

        when: 'a reply is generated'
        def calendar = userAgent.reply(request)

        then: 'the calendar object contains method = REPLY'
        calendar.getProperty(Property.METHOD) == Method.REPLY
    }

    def "Add"() {
        given: 'a todo'
        def vtodo = builder.vtodo {
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            summary 'Task 1'
            priority(Priority.HIGH)
        }

        when: 'a todo recurrence is added'
        def calendar = userAgent.add(vtodo)

        then: 'the calendar object contains method = ADD'
        calendar.getProperty(Property.METHOD) == Method.ADD
    }

    def "Cancel"() {
        given: 'a todo'
        def vtodo = builder.vtodo {
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a todo recurrence is cancelled'
        def calendar = userAgent.cancel(vtodo)

        then: 'the calendar object contains method = CANCEL'
        calendar.getProperty(Property.METHOD) == Method.CANCEL
    }

    def "Refresh"() {
        given: 'a todo'
        def vtodo = builder.vtodo {
            dtstamp()
            action 'DISPLAY'
            attendee 'mailto:org@example.com'
        }

        when: 'a todo refresh is generated'
        def calendar = userAgent.refresh(vtodo)

        then: 'the calendar object contains method = REFRESH'
        calendar.getProperty(Property.METHOD) == Method.REFRESH
    }

    def "Counter"() {
        given: 'a todo request'
        def request = builder.calendar {
            prodid(prodId)
            version(Version.VERSION_2_0)
            method(Method.REQUEST)
            vtodo {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
                attendee 'mailto:org@example.com'
                organizer 'mailto:org@example.com'
                summary 'Task 1'
                priority(Priority.HIGH)
            }
        }

        when: 'a todo counter is generated'
        def calendar = userAgent.counter(request)

        then: 'the calendar object contains method = COUNTER'
        calendar.getProperty(Property.METHOD) == Method.COUNTER
    }

    def "DeclineCounter"() {
        given: 'a todo counter'
        def counter = builder.calendar {
            prodid(prodId)
            version(Version.VERSION_2_0)
            method(Method.COUNTER)
            vtodo {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
                attendee 'mailto:org@example.com'
                sequence '0'
            }
        }

        when: 'a todo decline-counter is generated'
        def calendar = userAgent.declineCounter(counter)

        then: 'the calendar object contains method = DECLINECOUNTER'
        calendar.getProperty(Property.METHOD) == Method.DECLINE_COUNTER
    }
}
