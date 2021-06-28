package net.fortuna.ical4j.model.parameter

import net.fortuna.ical4j.AbstractBuilderTest
import net.fortuna.ical4j.model.Calendar

/**
 * Created by fortuna on 6/09/15.
 */
class EmailTest extends AbstractBuilderTest {

    def 'assert value stored correctly'() {
        given: 'an email address'
        String address = 'someone@example.com'

        when: 'an email object is constructed'
        Email email = [address]

        then: 'the object value matches the original address'
        email.value == address
    }

    def 'assert factory is located correctly'() {
        given: 'a sample calendar input'
        String calendarString = '''
BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//ABC Corporation//NONSGML My Product//EN
BEGIN:VTODO
ATTENDEE;CN=Cyrus Daboo;EMAIL=cyrus@example.com:mailto:opaque-token-1234@example.com
END:VTODO
END:VCALENDAR
'''

        when: 'the input is parsed'
        Calendar calendar = builder.build(new StringReader(calendarString))

        then: 'a valid calendar is realised'
        calendar?.components[0].properties[0].getParameter('EMAIL').value == 'cyrus@example.com'
    }
}
