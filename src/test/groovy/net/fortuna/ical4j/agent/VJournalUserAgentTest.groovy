package net.fortuna.ical4j.agent

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Method
import net.fortuna.ical4j.model.property.Organizer
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.util.RandomUidGenerator
import net.fortuna.ical4j.util.UidGenerator
import spock.lang.Specification

class VJournalUserAgentTest extends Specification {

    ProdId prodId = ['-//Ben Fortuna//iCal4j 2.0//EN']
    UidGenerator uidGenerator = new RandomUidGenerator()
    Organizer organizer = []

    VJournalUserAgent userAgent = [prodId, organizer, uidGenerator]

    ContentBuilder builder = []

    def "Publish"() {
        given: 'multiple vjournal instances'
        def vjournal = builder.vjournal {
            dtstamp()
            description 'Journal entry 1'
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        def vjournal2 = builder.vjournal {
            dtstamp()
            description 'Journal entry 2'
            dtstart '20090811', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'the journals are published'
        def calendar = userAgent.publish(vjournal, vjournal2)

        then: 'the calendar object contains method = PUBLISH'
        calendar.getProperty(Property.METHOD) == Method.PUBLISH

        and: 'the sequence property is present on all components'
        calendar.components.each { it.getProperty(Property.SEQUENCE) }
    }

    def "Request"() {
        given: 'a request'
        def vjournal = builder.vjournal {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        def vjournal2 = builder.vjournal {
            uid '1'
            dtstamp()
            dtstart '20090811', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'the journals are published'
        def calendar = userAgent.request(vjournal, vjournal2)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Delegate"() {
        given: 'a request'
        def request = builder.calendar {
            method(Method.REQUEST)
            vjournal {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        when: 'the journal request is delegated'
        def calendar = userAgent.delegate(request)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Reply"() {
        given: 'a journal request'
        def request = builder.calendar {
            method(Method.REQUEST)
            vjournal {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        when: 'a reply is generated'
        def calendar = userAgent.reply(request)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Add"() {
        given: 'a journal'
        def vjournal = builder.vjournal {
            dtstamp()
            description 'Journal entry 1'
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a journal recurrence is added'
        def calendar = userAgent.add(vjournal)

        then: 'the calendar object contains method = ADD'
        calendar.getProperty(Property.METHOD) == Method.ADD
    }

    def "Cancel"() {
        given: 'a journal'
        def vjournal = builder.vjournal {
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a journal recurrence is cancelled'
        def calendar = userAgent.cancel(vjournal)

        then: 'the calendar object contains method = CANCEL'
        calendar.getProperty(Property.METHOD) == Method.CANCEL
    }

    def "Refresh"() {
        given: 'a journal'
        def vjournal = builder.vjournal {
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        when: 'a journal refresh is generated'
        def calendar = userAgent.refresh(vjournal)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "Counter"() {
        given: 'a journal request'
        def request = builder.calendar {
            method(Method.REQUEST)
            vjournal {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        when: 'a journal counter is generated'
        def calendar = userAgent.counter(request)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }

    def "DeclineCounter"() {
        given: 'a journal counter'
        def counter = builder.calendar {
            method(Method.COUNTER)
            vjournal {
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        when: 'a journal decline-counter is generated'
        def calendar = userAgent.declineCounter(counter)

        then: 'an exception is thrown'
        thrown(UnsupportedOperationException)
    }
}
