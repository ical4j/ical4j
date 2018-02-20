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

        when: 'the freebusys are published'
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

        when: 'the freebusys are published'
        def calendar = userAgent.request(vfreeBusy, vfreeBusy2)

        then: 'the calendar object contains method = REQUEST'
        calendar.getProperty(Property.METHOD) == Method.REQUEST

        and: 'the sequence property is present on all components'
        calendar.components.each { it.getProperty(Property.SEQUENCE) }
    }

    def "Reply"() {
        given: 'a freebusy request'
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
        given: 'a freebusy'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a freebusy recurrence is added'
        def calendar = userAgent.add(vfreeBusy)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Cancel"() {
        given: 'a freebusy'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a freebusy recurrence is cancelled'
        def calendar = userAgent.cancel(vfreeBusy)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Refresh"() {
        given: 'a freebusy'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a freebusy refresh is generated'
        def calendar = userAgent.refresh(vfreeBusy)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Counter"() {
        given: 'a freebusy request'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a freebusy counter is generated'
        def calendar = userAgent.counter(vfreeBusy)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "DeclineCounter"() {
        given: 'a freebusy counter'
        def vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a freebusy decline-counter is generated'
        def calendar = userAgent.declineCounter(vfreeBusy)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }
}