package net.fortuna.ical4j.data

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.parameter.Cn
import net.fortuna.ical4j.model.parameter.Member
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.Location
import spock.lang.Specification

/**
 * Parameter values read by {@link CalendarParserImpl}: quoted-strings and comma-separated lists
 * (RFC 5545 section 3.2).
 */
class CalendarParserImplParameterSpec extends Specification {

    private static VEvent parseEvent(String... contentLines) {
        def ics = (['BEGIN:VCALENDAR', 'VERSION:2.0', 'PRODID:-//test//EN', 'BEGIN:VEVENT', 'UID:1',
                    'DTSTAMP:20240101T000000Z', 'DTSTART:20240102T100000Z'] + contentLines.toList()
                + ['END:VEVENT', 'END:VCALENDAR', '']).join('\r\n')
        Calendar calendar = new CalendarBuilder().build(new StringReader(ics))
        calendar.getComponent('VEVENT').get()
    }

    def 'a backslash before the closing quote of [#line] is a literal character'() {
        when:
        def event = parseEvent(line)
        Attendee attendee = event.getRequiredProperty(Property.ATTENDEE)

        then: 'the quote closes the value and the next content line is parsed on its own'
        attendee.getRequiredParameter('CN').value == cn
        attendee.calAddress == URI.create('mailto:a@example.com')
        event.getProperty(Property.UID).isPresent()

        where:
        line                                          | cn
        'ATTENDEE;CN="C:\\":mailto:a@example.com'     | 'C:\\'
        'ATTENDEE;CN="a\\nb:c":mailto:a@example.com'  | 'a\\nb:c'
        'ATTENDEE;CN="x\\\\y:z":mailto:a@example.com' | 'x\\\\y:z'
    }

    def 'a quoted value ending in a backslash survives a write and re-parse'() {
        given:
        def event = parseEvent('ATTENDEE:mailto:a@example.com')
        Attendee attendee = event.getRequiredProperty(Property.ATTENDEE)
        attendee.add(new Cn('BEGIN:VALARM\\'))

        when:
        def written = attendee.toString().trim()
        def reparsed = parseEvent(written).getRequiredProperty(Property.ATTENDEE)

        then:
        reparsed.getRequiredParameter('CN').value == 'BEGIN:VALARM\\'
    }

    def 'every value of a multi-valued quoted parameter belongs to the parameter'() {
        when:
        def event = parseEvent(
                'ATTENDEE;MEMBER="mailto:projectA@example.com","mailto:projectB@example.com":mailto:janedoe@example.com',
                'LOCATION;MEMBER="mailto:a@example.com","mailto:b@example.com":Room 1')
        Attendee attendee = event.getRequiredProperty(Property.ATTENDEE)
        Location location = event.getRequiredProperty(Property.LOCATION)

        then:
        attendee.calAddress == URI.create('mailto:janedoe@example.com')
        (attendee.getRequiredParameter('MEMBER') as Member).groups == [
                URI.create('mailto:projectA@example.com'), URI.create('mailto:projectB@example.com')]
        location.value == 'Room 1'
        (location.getRequiredParameter('MEMBER') as Member).groups.size() == 2
    }

    def 'a multi-valued parameter written by ical4j survives a re-parse'() {
        given:
        def event = parseEvent('ATTENDEE:mailto:janedoe@example.com')
        Attendee attendee = event.getRequiredProperty(Property.ATTENDEE)
        attendee.add(new Member([URI.create('mailto:a@example.com'), URI.create('mailto:b@example.com')]))

        when:
        Attendee reparsed = parseEvent(attendee.toString().trim()).getRequiredProperty(Property.ATTENDEE)

        then:
        reparsed.calAddress == URI.create('mailto:janedoe@example.com')
        (reparsed.getRequiredParameter('MEMBER') as Member).groups.size() == 2
    }

    def 'an unquoted comma-separated parameter value is kept whole'() {
        when:
        def location = parseEvent('LOCATION;X-LIST=a,b,c:Room 1').getRequiredProperty(Property.LOCATION)

        then:
        location.getRequiredParameter('X-LIST').value == 'a,b,c'
        location.value == 'Room 1'
    }
}
