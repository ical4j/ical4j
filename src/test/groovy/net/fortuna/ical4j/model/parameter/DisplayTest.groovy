package net.fortuna.ical4j.model.parameter

import net.fortuna.ical4j.AbstractBuilderTest
import net.fortuna.ical4j.model.Calendar

/**
 * Created by fortuna on 6/09/15.
 */
class DisplayTest extends AbstractBuilderTest {

    def 'assert value stored correctly'() {
        given: 'a display value'
        String displayValue = 'GRAPHIC,THUMBNAIL'

        when: 'a display object is constructed'
        Display display = [displayValue]

        then: 'the object value matches the original address'
        display.value == displayValue
    }

    def 'assert factory is located correctly'() {
        given: 'a sample calendar input'
        String calendarString = '''BEGIN:VCALENDAR
VERSION:2.0\r
PRODID:-//ABC Corporation//NONSGML My Product//EN\r
BEGIN:VTODO\r
IMAGE;VALUE=URI;DISPLAY=BADGE,THUMBNAIL,;FMTTYPE=image/png:http://exa\r
 mple.com/images/weather-cloudy.png\r
END:VTODO\r
END:VCALENDAR\r
'''

        when: 'the input is parsed'
        Calendar calendar = builder.build(new StringReader(calendarString))

        then: 'a valid calendar is realised'
        calendar?.components[0].properties[0].getParameter('DISPLAY').value == 'BADGE,THUMBNAIL'
    }
}
