package net.fortuna.ical4j.model.property


import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.ParameterList
import spock.lang.Specification

import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0

class VersionSpec extends Specification {

    Version.Factory factory = []

    def 'test factory use of constants'() {
        when: 'factory is invoked with a constant value'
        def version = factory.createProperty(value)

        then: 'the returned value is the constant instance'
        version.is(constantInstance)

        where:
        value   | constantInstance
        '2.0' | VERSION_2_0
    }

    def 'a value [#value] is split into min [#min] and max [#max] versions'() {
        when:
        def parsed = new Version(new ParameterList(), value)
        def set = new Version()
        set.value = value

        then:
        [parsed, set].every { it.minVersion == min && it.maxVersion == max && it.value == value }

        where:
        value     | min   | max
        '2.0'     | null  | '2.0'
        '1.0;2.0' | '1.0' | '2.0'
        '2;2.0'   | '2'   | '2.0'
        ';2.0'    | ''    | '2.0'
    }

    def 'setting a value without a min version clears the previous one'() {
        given:
        def version = new Version(new ParameterList(), '1.0;2.0')

        when:
        version.value = '2.0'

        then:
        version.minVersion == null
        version.value == '2.0'
    }

    def 'a min and max version survive a write, re-parse and copy'() {
        given:
        def ics = 'BEGIN:VCALENDAR\r\nPRODID:-//x//y//EN\r\nVERSION:1.0;2.0\r\nEND:VCALENDAR\r\n'

        when:
        def calendar = new CalendarBuilder().build(new StringReader(ics))
        def out = new StringWriter()
        new CalendarOutputter(false).output(calendar, out)
        def reparsed = new CalendarBuilder().build(new StringReader(out.toString()))

        then:
        out.toString().contains('VERSION:1.0;2.0\r\n')
        reparsed.getRequiredProperty('VERSION').value == '1.0;2.0'
        reparsed.copy().getRequiredProperty('VERSION').value == '1.0;2.0'
    }
}
