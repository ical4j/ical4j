package net.fortuna.ical4j.model


import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.util.Calendars
import net.fortuna.ical4j.util.RandomUidGenerator
import spock.lang.Shared
import spock.lang.Specification

import java.nio.charset.Charset

class CalendarSpec extends Specification {

    @Shared
    ContentBuilder builder

    def setupSpec() {
        builder = []
    }

    def 'test calendar merge'() {
        given: 'a calendar'
        def calendar = builder.calendar {
            prodid '-//Ben Fortuna//iCal4j 1.0//EN'
            version '2.0'
            method 'PUBLISH'
            vevent {
                uid 'one'
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                duration 'P1D'
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        and: 'a second calendar'
        def calendar2 = builder.calendar {
            prodid '-//Ben Fortuna//iCal4j 1.0//EN'
            version '2.0'
            method 'PUBLISH'
            vevent {
                uid 'two'
                dtstamp()
                dtstart '20090910', parameters: parameters { value 'DATE' }
                duration 'P3D'
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        when: 'the calendars are merged'
        def merged = calendar.merge(calendar2)

        then: 'a combined calendar is the result'
        merged.getComponents().size() == 2
    }

    def 'test calendar split with a TZID that has no VTIMEZONE'() {
        given: 'a calendar with events referencing a timezone that is not defined in the calendar'
        def calendar = builder.calendar {
            prodid '-//Ben Fortuna//iCal4j 1.0//EN'
            version '2.0'
            vevent {
                uid 'one'
                dtstamp()
                dtstart '20240101T140000', parameters: parameters { tzid_ 'Australia/Melbourne' }
            }
            vevent {
                uid 'two'
                dtstamp()
                dtstart '20240102T140000', parameters: parameters { tzid_ 'Australia/Melbourne' }
            }
        }

        when: 'the calendar is split'
        def split = splitter(calendar)

        then: 'each split calendar contains only its event'
        split.length == 2
        split.every { it.components.size() == 1 && it.components[0]?.name == 'VEVENT' }

        where:
        splitter << [{ Calendar c -> c.split() }, { Calendar c -> Calendars.split(c) }]
    }

    def 'test calendar split'() {
        given: 'a calendar with two different events'
        def calendar = builder.calendar {
            prodid '-//Ben Fortuna//iCal4j 1.0//EN'
            version '2.0'
            method 'PUBLISH'
            vevent {
                uid 'one'
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                duration 'P1D'
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
            vevent {
                uid 'two'
                dtstamp()
                dtstart '20090910', parameters: parameters { value 'DATE' }
                duration 'P3D'
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        when: 'calendar is split'
        def split = calendar.split()

        then: 'two calendars are produced'
        split.size() == 2
    }

    def 'test get uid'() {
        given: 'a uid'
        def auid = new RandomUidGenerator().generateUid()

        and: 'a calendar'
        def calendar = builder.calendar {
            prodid '-//Ben Fortuna//iCal4j 1.0//EN'
            version '2.0'
            vevent {
                uid auid
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                duration 'P1D'
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        expect: 'uid is retrieved as expected'
        calendar.uid == auid
    }

    def 'test get content type'() {
        given: 'a calendar'
        def calendar = builder.calendar {
            prodid '-//Ben Fortuna//iCal4j 1.0//EN'
            version '2.0'
            method 'PUBLISH'
            vevent {
                uid 'auid'
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                duration 'P1D'
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
            }
        }

        expect: 'content type is retrieved as expected'
        calendar.getContentType(Charset.forName('utf-8')) == 'text/calendar; method=PUBLISH; charset=UTF-8'
    }

    def 'test bare carriage return in a text value does not inject a content line'() {
        given: 'a calendar with a summary containing a bare carriage return followed by a property line'
        def calendar = builder.calendar {
            prodid '-//Ben Fortuna//iCal4j 1.0//EN'
            version '2.0'
            vevent {
                uid 'auid'
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                summary 'Team sync\rATTENDEE:mailto:mallory@example.com'
            }
        }

        when: 'the calendar is serialised and parsed again'
        def parsed = new CalendarBuilder().build(new StringReader(calendar as String))
        def event = parsed.getComponents().first()

        then: 'the injected line is part of the summary rather than a new property'
        event.getProperties(Property.ATTENDEE).empty
        event.getProperty(Property.SUMMARY).get().value == 'Team sync\nATTENDEE:mailto:mallory@example.com'
    }
}
