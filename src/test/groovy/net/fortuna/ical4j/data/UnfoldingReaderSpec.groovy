package net.fortuna.ical4j.data

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Attach
import spock.lang.Specification

import java.security.MessageDigest

class UnfoldingReaderSpec extends Specification {

    def 'verify unfolding encoded binary attachments'() {
        given: 'a calendar object string with an encoded binary attachment'
        def builder = new ContentBuilder()
        def calendar = builder.calendar() {
            prodid '-//Ben Fortuna//iCal4j 1.0//EN'
            version '2.0'
            vevent {
                uid '1'
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach new Attach(new File('gradle/wrapper/gradle-wrapper.jar').bytes)
            }
        }

        CalendarOutputter outputter = []
        def calendarString = new StringWriter()
        outputter.output(calendar, calendarString)

        when: 'the calendar object string is parsed'
        Calendar parsed = new CalendarBuilder().build(new StringReader(calendarString as String))

        then: 'the encoded binary is decoded correctly'
        def attach = parsed.components[0].getProperty(Property.ATTACH)
        def md5 = MessageDigest.getInstance("MD5")
        md5.digest(attach.binary) == md5.digest(new File('gradle/wrapper/gradle-wrapper.jar').bytes)
    }
}
