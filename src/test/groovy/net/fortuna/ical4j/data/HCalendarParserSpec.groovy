package net.fortuna.ical4j.data

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Property
import spock.lang.Specification

class HCalendarParserSpec extends Specification {

    def 'line breaks in attribute values are not written as new content lines'() {
        given: 'an hCalendar document with a character-referenced line break in an attribute value'
        String html = """<html xmlns="http://www.w3.org/1999/xhtml"><body><div class="vcalendar">
            <abbr class="method" title="${method}">publish</abbr>
            <div class="vevent">
            <abbr class="dtstart" title="20240101T100000Z">1 January 2024</abbr>
            <span class="uid">1</span>
            <abbr class="status" title="${status}">confirmed</abbr>
            <img class="class" src="private.png" alt="${clazz}"/>
            </div></div></body></html>"""

        when: 'the document is parsed and the result written and parsed again'
        Calendar calendar = new CalendarBuilder(new HCalendarParser()).build(new StringReader(html))
        Calendar reparsed = new CalendarBuilder().build(new StringReader(calendar as String))

        then: 'the values are kept on a single line and no property is injected'
        calendar.getRequiredProperty(Property.METHOD).value == expectedMethod
        calendar.components[0].getRequiredProperty(Property.STATUS).value == expectedStatus
        calendar.components[0].getRequiredProperty(Property.CLASS).value == expectedClass
        reparsed.getProperties('X-INJECTED').isEmpty()
        reparsed.components[0].getProperties(Property.ATTENDEE).isEmpty()
        reparsed.components[0].getProperties('X-INJECTED').isEmpty()

        where:
        method                      | status                                                      | clazz                       || expectedMethod           | expectedStatus                                            | expectedClass
        'PUBLISH'                   | 'CONFIRMED'                                                 | 'PRIVATE'                   || 'PUBLISH'                | 'CONFIRMED'                                               | 'PRIVATE'
        'PUBLISH&#13;&#10;X-INJECTED:1' | 'CONFIRMED&#13;&#10;ATTENDEE:mailto:mallory@example.com' | 'PRIVATE&#10;X-INJECTED:1'  || 'PUBLISH X-INJECTED:1'   | 'CONFIRMED ATTENDEE:mailto:mallory@example.com'           | 'PRIVATE X-INJECTED:1'
    }
}
