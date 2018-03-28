package net.fortuna.ical4j.agent

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VFreeBusy
import net.fortuna.ical4j.model.property.Method
import net.fortuna.ical4j.model.property.Organizer
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Version
import net.fortuna.ical4j.util.RandomUidGenerator
import net.fortuna.ical4j.util.UidGenerator
import spock.lang.Specification

class VFreeBusyUserAgentTest extends Specification {

    ProdId prodId = ['-//Ben Fortuna//iCal4j 2.0//EN']
    UidGenerator uidGenerator = new RandomUidGenerator()
    Organizer organizer = []

    VFreeBusyUserAgent userAgent = [prodId, organizer, uidGenerator]

    ContentBuilder builder = []

    def "Publish"() {
        given: 'a calendar'
        def source = builder.calendar {
            prodid(prodId)
            version(Version.VERSION_2_0)
            method(Method.REQUEST)
            vevent {
                organizer 'mailto:org@example.com'
                summary 'Spring Equinox'
                dtstamp()
                dtstart '20090810T000000'
                dtend '20090810T120000'
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
                attendee 'mailto:org@example.com'
            }
        }

        and: 'a freebusy request'
        VFreeBusy vfreeBusy = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090801T000000'
            dtend '20090901T000000'
        }

        when: 'the result is generated and published'
        def result = new VFreeBusy(vfreeBusy, source.components)
        def calendar = userAgent.publish(result)

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
            dtstart '20090810T010000Z'
            dtend '20090810T030000Z'
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            attendee 'mailto:org@example.com'
        }

        def vfreeBusy2 = builder.vfreebusy {
            uid '1'
            dtstamp()
            dtstart '20090810T010000Z'
            dtend '20090810T030000Z'
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            attendee 'mailto:org@example.com'
        }

        when: 'the freebusys are published'
        def calendar = userAgent.request(vfreeBusy, vfreeBusy2)

        then: 'the calendar object contains method = REQUEST'
        calendar.getProperty(Property.METHOD) == Method.REQUEST

        and: 'the sequence property is present on all components'
        calendar.components.each { it.getProperty(Property.SEQUENCE) }
    }

    def "Delegate"() {
        given: 'a freebusy request'
        def request = builder.calendar {
            method(Method.REQUEST)
            vfreebusy {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        when: 'a freebusy delegate is generated'
        def calendar = userAgent.delegate(request)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Reply"() {
        given: 'a freebusy request'
        def request = builder.calendar {
            prodid(prodId)
            version(Version.VERSION_2_0)
            method(Method.REQUEST)
            vfreebusy {
                dtstamp()
                dtstart '20090810T010000Z'
                dtend '20090810T030000Z'
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
        given: 'a freebusy'
        def vfreeBusy = builder.vfreebusy {
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
        def request = builder.calendar {
            method(Method.REQUEST)
            vfreebusy {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        when: 'a freebusy counter is generated'
        def calendar = userAgent.counter(request)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "DeclineCounter"() {
        given: 'a freebusy counter'
        def counter = builder.calendar {
            method(Method.COUNTER)
            vfreebusy {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        when: 'a freebusy decline-counter is generated'
        def calendar = userAgent.declineCounter(counter)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }
}
