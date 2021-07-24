package net.fortuna.ical4j.data

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import spock.lang.Specification

import java.util.function.Consumer

class DefaultContentHandlerTest extends Specification {

    def 'test calendar creation'() {
        given: 'a calendar reference'
        def result

        and: 'a content handler'
        DefaultContentHandler contentHandler = [{result = it} as Consumer<Calendar>,
                                                TimeZoneRegistryFactory.instance.createRegistry()]

        when: 'a sequence of method calls to simulate calendar parsing are processed'
        contentHandler.startCalendar()
        contentHandler.startComponent('vevent')
        contentHandler.startProperty('dtstart')
        contentHandler.parameter('value', 'DATE')
        contentHandler.propertyValue('20181212')
        contentHandler.endProperty('dtstart')
        contentHandler.endComponent('vevent')
        contentHandler.endCalendar()

        then: 'the resulting calendar is as expected'
        result == new ContentBuilder().with {
            calendar {
                vevent {
                    dtstart '20181212', parameters: parameters { value 'DATE' }
                }
            }
        }
    }

    def 'test ignored property names'() {
        given: 'a calendar reference'
        def result

        and: 'a content handler instance with ignored properties'
        DefaultContentHandler contentHandler = [{result = it} as Consumer<Calendar>,
                                                TimeZoneRegistryFactory.instance.createRegistry(),
                                                new ContentHandlerContext().withIgnoredPropertyNames(['DTEND'])]

        when: 'a calendar is parsed'
        contentHandler.startCalendar()
        contentHandler.startComponent('vevent')
        contentHandler.startProperty('dtstart')
        contentHandler.parameter('value', 'DATE')
        contentHandler.propertyValue('20181212')
        contentHandler.endProperty('dtstart')
        contentHandler.startProperty('dtend')
        contentHandler.parameter('value', 'DATE')
        contentHandler.propertyValue('20181213')
        contentHandler.endProperty('dtend')
        contentHandler.endComponent('vevent')
        contentHandler.endCalendar()

        then: 'the resulting calendar doesn\'t include ignored properties'
        result == new ContentBuilder().with {
            calendar {
                vevent {
                    dtstart '20181212', parameters: parameters { value 'DATE' }
                }
            }
        }
    }
}
