package net.fortuna.ical4j.model

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.property.Categories
import spock.lang.Specification

class TextListSpec extends Specification {

    def 'a long value with many escapes is parsed without overflowing the stack'() {
        given: 'a value made of 100,000 escaped commas, which is a single item'
        def value = 'a\\,' * 100_000

        when:
        def list = new TextList(value)

        then:
        list.texts as List == ['a,' * 100_000]
    }

    def 'a long CATEGORIES value does not end the parse with a StackOverflowError'() {
        given:
        def ics = "BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//test//EN\r\nBEGIN:VEVENT\r\nUID:1\r\n" +
                "DTSTAMP:20260101T000000Z\r\nCATEGORIES:${'a\\,' * 100_000}\r\nEND:VEVENT\r\nEND:VCALENDAR\r\n"

        when:
        def calendar = new CalendarBuilder().build(new StringReader(ics))
        Categories categories = calendar.getComponent('VEVENT').get().getRequiredProperty('CATEGORIES')

        then:
        categories.categories.texts.size() == 1
    }

    def 'escape sequences in [#value] are decoded once'() {
        expect:
        new TextList(value).texts as List == expected

        where:
        value                   | expected
        'C:\\\\new,a\\\\\\\\b'  | ['C:\\new', 'a\\\\b']
        'Projector\\\\;HDMI'    | ['Projector\\;HDMI']
        'a\\,b,c'               | ['a,b', 'c']
        'line\\nbreak,x\\;y'    | ['line\nbreak', 'x;y']
        'a,,b,'                 | ['a', 'b']
    }

    def 'an item [#item] survives a write and re-read'() {
        given:
        def written = new TextList([item, 'z'])

        expect:
        new TextList(written.toString()).texts as List == [item, 'z']

        where:
        item << ['\\', '\\\\', '\\n', '\\,', 'a\\;b', 'x\\"y', 'a,b', 'line\nbreak']
    }
}
