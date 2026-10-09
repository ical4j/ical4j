package net.fortuna.ical4j.data

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Parameter
import net.fortuna.ical4j.model.parameter.Range
import net.fortuna.ical4j.model.parameter.XParameter
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification

/**
 * Parameters on experimental (X-) properties must not abort parsing when their value is not valid
 * for the standard parameter of the same name (#617).
 */
class ExperimentalPropertyParameterSpec extends Specification {

    def setup() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY)
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)
    }

    def cleanup() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY)
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)
    }

    def 'RANGE=ALL on an X- property is retained as an experimental parameter'() {
        given: 'a Lotus Notes style X-LOTUS-RECURID with RANGE=ALL and no compatibility hints'
        String ics = """BEGIN:VCALENDAR
VERSION:2.0
PRODID://Random Org
METHOD:CANCEL
CALSCALE:GREGORIAN
BEGIN:VEVENT
DTSTAMP:20250121T043511Z
UID:ASDASDASDASDASDASDASDas
SUMMARY:ABC
DTSTART:20250201T063000Z
DTEND:20250201T073000Z
CREATED:20250211T063000Z
SEQUENCE:1
TRANSP:OPAQUE
ORGANIZER;CN=abc@abc.com:mailto:abc@abc.com
LAST-MODIFIED:20250121T043511Z
X-LOTUS-RECURID;RANGE=ALL:20220309T173000Z
END:VEVENT
END:VCALENDAR
"""

        when: 'the calendar is parsed'
        Calendar calendar = new CalendarBuilder().build(new StringReader(ics))
        def recurId = calendar.getComponents()[0].getProperty('X-LOTUS-RECURID').get()

        then: 'the property and its RANGE parameter are retained'
        recurId.value == '20220309T173000Z'
        recurId.getParameter(Parameter.RANGE).isPresent()
        recurId.getParameter(Parameter.RANGE).get().value == 'ALL'
        recurId.getParameter(Parameter.RANGE).get() instanceof XParameter

        and: 'the parameter is written back unchanged'
        calendar.toString().contains('X-LOTUS-RECURID;RANGE=ALL:20220309T173000Z')
    }

    def 'RANGE=ALL on a standard property still fails in strict mode'() {
        given: 'a RECURRENCE-ID with an invalid RANGE value'
        String ics = """BEGIN:VCALENDAR
VERSION:2.0
PRODID://Random Org
BEGIN:VEVENT
DTSTAMP:20250121T043511Z
UID:1
DTSTART:20250201T063000Z
RECURRENCE-ID;RANGE=ALL:20220309T173000Z
END:VEVENT
END:VCALENDAR
"""

        when: 'the calendar is parsed'
        new CalendarBuilder().build(new StringReader(ics))

        then: 'parsing fails with the parameter error'
        ParserException e = thrown()
        e.message.contains('Invalid value [ALL]')
    }

    def 'RANGE=ALL on a standard property is still accepted with the Notes compatibility hint'() {
        given: 'Lotus Notes compatibility is enabled'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true)
        String ics = """BEGIN:VCALENDAR
VERSION:2.0
PRODID://Random Org
BEGIN:VEVENT
DTSTAMP:20250121T043511Z
UID:1
DTSTART:20250201T063000Z
RECURRENCE-ID;RANGE=ALL:20220309T173000Z
END:VEVENT
END:VCALENDAR
"""

        when: 'the calendar is parsed'
        Calendar calendar = new CalendarBuilder().build(new StringReader(ics))
        def recurId = calendar.getComponents()[0].getProperty('RECURRENCE-ID').get()

        then: 'the standard Range parameter is created'
        recurId.getParameter(Parameter.RANGE).get() instanceof Range
        recurId.getParameter(Parameter.RANGE).get().value == 'ALL'
    }
}
