package net.fortuna.ical4j.agent

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Method
import spock.lang.Specification

class VEventUserAgentTest extends Specification {

    VEventUserAgent userAgent = []

    ContentBuilder builder = []

    def "Publish"() {
        given: 'multiple vevent instances'
        def vevent = builder.vevent {
            uid '1'
            dtstamp()
            dtstart '20090810', parameters: parameters { value 'DATE' }
            action 'DISPLAY'
            attach'http://example.com/attachment', parameters: parameters { value 'URI' }
        }

        def vevent2 = builder.vevent {
            uid '2'
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
    }

    def "Reply"() {
    }

    def "Add"() {
    }

    def "Cancel"() {
    }

    def "Refresh"() {
    }

    def "Counter"() {
    }

    def "DeclineCounter"() {
    }
}
