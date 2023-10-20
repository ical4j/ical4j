package net.fortuna.ical4j.util

import net.fortuna.ical4j.model.ContentBuilder
import spock.lang.Shared
import spock.lang.Specification

class CalendarsSpec extends Specification {

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
        def merged = Calendars.merge(calendar, calendar2)

        then: 'a combined calendar is the result'
        merged.getComponents().size() == 2
    }
}
