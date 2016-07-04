package net.fortuna.ical4j.data

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.ConstraintViolationException
import net.fortuna.ical4j.util.CompatibilityHints
import net.fortuna.ical4j.util.Strings
import org.apache.commons.lang.StringUtils
import spock.lang.Specification

import java.nio.charset.Charset

/**
 * Created by fortuna on 4/07/2016.
 */
class CalendarBuilderSpec extends Specification {

    def 'test relaxed parsing'() {
        given: 'a calendar object string'
        String cal2 = '''BEGIN:VCALENDAR
CALSCALE:GREGORIAN
PRODID:-//xxxx//xxxxxx//EN
VERSION:2.0
BEGIN:VTIMEZONE
TZID:(GMT-03:00)
BEGIN:STANDARD
DTSTART:16010101T230000
TZOFFSETFROM:-0200
TZOFFSETTO:-0300
RRULE:FREQ=YEARLY;BYMONTH=2;BYDAY=3SU
END:STANDARD
BEGIN:DAYLIGHT
TZOFFSETFROM:-0300
TZOFFSETTO:-0200
RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=4SU
END:DAYLIGHT
END:VTIMEZONE
BEGIN:VEVENT
UID:1047216278.801463535396580.JavaMail.root(a)xxxx.xxxxx.xxxx
LAST-MODIFIED:20160519T090547Z
DTSTAMP:20160519T090547Z
DTSTART;TZID="(GMT-03:00)":20160518T123000
DTEND;TZID="(GMT-03:00)":20160518T133000
X-MICROSOFT-CDO-ALLDAYEVENT:FALSE
X-MICROSOFT-CDO-IMPORTANCE:1
PRIORITY:5
TRANSP:OPAQUE
X-MICROSOFT-CDO-BUSYSTATUS:BUSY
SEQUENCE:0
SUMMARY:xxxxxxx S-xxxxx
X-SCALIX-LABEL:0
CLASS:PUBLIC
END:VEVENT
END:VCALENDAR
'''

        and: 'relaxed parsing is enabled'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true)

        when: 'the calendar string is parsed'
        System.out.println(cal2);
        InputStream stream = new ByteArrayInputStream(cal2.getBytes(Charset.forName("UTF-8")));
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = null;

        calendar = builder.build(stream);

        then: 'the result is as expected'
        calendar as String == cal2.replaceAll('\n', '\r\n')
    }
}
