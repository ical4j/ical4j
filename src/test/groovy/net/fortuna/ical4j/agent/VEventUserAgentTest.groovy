package net.fortuna.ical4j.agent

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Method
import net.fortuna.ical4j.model.property.Organizer
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Version
import net.fortuna.ical4j.util.RandomUidGenerator
import net.fortuna.ical4j.util.UidGenerator
import spock.lang.Specification

class VEventUserAgentTest extends Specification {

    ProdId prodId = ['-//Ben Fortuna//iCal4j 2.0//EN']
    UidGenerator uidGenerator = new RandomUidGenerator()
    Organizer org = []

    VEventUserAgent userAgent = [prodId, org, uidGenerator]

    ContentBuilder builder = []

    def "Publish"() {
        given: 'multiple vevent instances'
        def vevent = builder.vevent {
            summary 'Spring Equinox'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        def vevent2 = builder.vevent {
            summary 'Spring Equinox'
            dtstamp()
            dtstart '20090811', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'the events are published'
        def calendar = userAgent.publish(vevent, vevent2)

        then: 'the calendar object contains method = PUBLISH'
        calendar.getProperty(Property.METHOD) == Method.PUBLISH

        and: 'the sequence property is present on all components'
        calendar.components.each { it.getProperty(Property.SEQUENCE) }
    }

    def "Request"() {
        given: 'multiple vevent instances'
        def vevent = builder.vevent {
            uid '1'
            summary 'Spring Equinox'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            attendee 'mailto:org@example.com'
        }

        def vevent2 = builder.vevent {
            uid '1'
            summary 'Spring Equinox'
            dtstamp()
            dtstart '20090811', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            attendee 'mailto:org@example.com'
        }

        when: 'the events are published'
        def calendar = userAgent.request(vevent, vevent2)

        then: 'the calendar object contains method = REQUEST'
        calendar.getProperty(Property.METHOD) == Method.REQUEST

        and: 'the sequence property is present on all components'
        calendar.components.each { it.getProperty(Property.SEQUENCE) }
    }

    def "Delegate"() {
        def request = builder.calendar {
            prodid(prodId)
            version(Version.VERSION_2_0)
            method(Method.REQUEST)
            vevent {
                organizer 'mailto:org@example.com'
                summary 'Spring Equinox'
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
                attendee 'mailto:org@example.com'
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
            vevent {
                organizer 'mailto:org@example.com'
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
                attendee 'mailto:org@example.com'
            }
        }

        when: 'a reply is generated'
        def calendar = userAgent.reply(request)

        then: 'the calendar object contains method = REPLY'
        calendar.getProperty(Property.METHOD) == Method.REPLY
    }

    def "Add"() {
        given: 'an event'
        def vevent = builder.vevent {
            summary('Spring Equinox')
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event recurrence is added'
        def calendar = userAgent.add(vevent)

        then: 'the calendar object contains method = ADD'
        calendar.getProperty(Property.METHOD) == Method.ADD
    }

    def "Cancel"() {
        given: 'an event'
        def vevent = builder.vevent {
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event recurrence is cancelled'
        def calendar = userAgent.cancel(vevent)

        then: 'the calendar object contains method = CANCEL'
        calendar.getProperty(Property.METHOD) == Method.CANCEL
    }

    def "Refresh"() {
        given: 'an event'
        def vevent = builder.vevent {
            organizer 'mailto:org@example.com'
            dtstamp()
            action 'DISPLAY'
            attendee 'mailto:org@example.com'
        }

        when: 'an event refresh is generated'
        def calendar = userAgent.refresh(vevent)

        then: 'the calendar object contains method = REFRESH'
        calendar.getProperty(Property.METHOD) == Method.REFRESH
    }

    def "Counter"() {
        def request = builder.calendar {
            prodid(prodId)
            version(Version.VERSION_2_0)
            method(Method.REQUEST)
            vevent {
                summary 'Spring Equinox'
                organizer 'mailto:org@example.com'
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
                sequence '0'
            }
        }

        when: 'an event counter is generated'
        def calendar = userAgent.counter(request)

        then: 'the calendar object contains method = COUNTER'
        calendar.getProperty(Property.METHOD) == Method.COUNTER
    }

    def "DeclineCounter"() {
        given: 'an event counter'
        def counter = builder.calendar {
            prodid(prodId)
            version(Version.VERSION_2_0)
            method(Method.COUNTER)
            vevent {
                dtstamp()
                action 'DISPLAY'
            }
        }

        when: 'an event decline-counter is generated'
        def calendar = userAgent.declineCounter(counter)

        then: 'the calendar object contains method = DECLINECOUNTER'
        calendar.getProperty(Property.METHOD) == Method.DECLINE_COUNTER
    }
}
