package net.fortuna.ical4j.agent

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Method
import spock.lang.Specification

class VFreeBusyUserAgentTest extends Specification {

    VFreeBusyUserAgent userAgent = []

    ContentBuilder builder = []

    def "Publish"() {
        given: 'multiple vfreeBusy instances'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        def vfreeBusy2 = builder.vfreebusy {
            uid '2'
            dtstamp()
            dtstart '20090811', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'the events are published'
        def calendar = userAgent.publish(vfreeBusy, vfreeBusy2)

        then: 'the calendar object contains method = PUBLISH'
        calendar.getProperty(Property.METHOD) == Method.PUBLISH

        and: 'the sequence property is present on all components'
        calendar.components.each { it.getProperty(Property.SEQUENCE) }
    }

    def "Request"() {
        given: 'multiple vfreeBusy instances'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        def vfreeBusy2 = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090811', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'the events are published'
        def calendar = userAgent.request(vfreeBusy, vfreeBusy2)

        then: 'the calendar object contains method = REQUEST'
        calendar.getProperty(Property.METHOD) == Method.REQUEST

        and: 'the sequence property is present on all components'
        calendar.components.each { it.getProperty(Property.SEQUENCE) }
    }

    def "Reply"() {
        given: 'an event request'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a reply is generated'
        def calendar = userAgent.reply(vfreeBusy)

        then: 'the calendar object contains method = REPLY'
        calendar.getProperty(Property.METHOD) == Method.REPLY
    }

    def "Add"() {
        given: 'an event'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event recurrence is added'
        def calendar = userAgent.add(vfreeBusy)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Cancel"() {
        given: 'an event'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event recurrence is cancelled'
        def calendar = userAgent.cancel(vfreeBusy)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Refresh"() {
        given: 'an event'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event refresh is generated'
        def calendar = userAgent.refresh(vfreeBusy)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Counter"() {
        given: 'an event request'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event counter is generated'
        def calendar = userAgent.counter(vfreeBusy)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "DeclineCounter"() {
        given: 'an event counter'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'an event decline-counter is generated'
        def calendar = userAgent.declineCounter(vfreeBusy)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }
}
