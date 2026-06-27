package net.fortuna.ical4j.data

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification

import java.nio.charset.Charset

/**
 * Created by fortuna on 4/07/2016.
 */
class CalendarBuilderSpec extends Specification {

    def cleanup() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING)
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)
    }

    def 'test relaxed parsing'() {
        given: 'a calendar object string'
        String cal2 = getClass().getResource('test-relaxed-parsing.ics').text

        and: 'relaxed parsing is enabled'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true)

        when: 'the calendar string is parsed'
        System.out.println(cal2)
        InputStream stream = new ByteArrayInputStream(cal2.getBytes(Charset.forName("UTF-8")))
        CalendarBuilder builder = new CalendarBuilder()
        Calendar calendar = builder.build(stream)

        then: 'the result is as expected'
        calendar as String == cal2.replaceAll('\n', '\r\n')
    }

    def 'test looping IcsParse'() {
        given: 'malformed calendar data'
        String badIcs =
                "BEGIN:VCALENDAR\r\n" +
                        "BEGIN:VEVENT\r\n" +
                        "DTSTART:20180915T130000\r\n" +
                        "DTEND:20180918T120000\r\n" +
                        "LOCATION:somewhere\r\n" +
                        "SUMMARY:Reservation 1234\r\n"

        and: 'relaxed parsing is enabled'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

        when: 'attempting to parse'
        new CalendarBuilder().build(new ByteArrayInputStream(badIcs.getBytes("utf-8")))

        then: 'expect thrown exception'
        thrown(ParserException)
    }

    def 'parse Apple iPhone OS 26 X-APPLE-STRUCTURED-LOCATION under varied compatibility hints'() {
        given: 'a calendar exported from iPhone OS 26 containing a folded X-APPLE-STRUCTURED-LOCATION property'
        String ics = getClass().getResource('apple-iphone-os-26-structured-location.ics').text

        and: 'compatibility hints toggled per row'
        if (relaxedUnfolding) CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true)
        if (relaxedParsing) CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

        when: 'the calendar is parsed'
        Calendar calendar = null
        ParserException failure = null
        try {
            calendar = new CalendarBuilder().build(new ByteArrayInputStream(ics.getBytes(Charset.forName('UTF-8'))))
        } catch (ParserException t) {
            failure = t
        }

        then: 'the outcome matches the documented behaviour for this configuration'
        // Failure modes:
        //  - Without relaxed unfolding the LF-only line endings aren't recognised
        //    as folded continuation lines, so the parameter parser trips on the
        //    embedded LF (decimal 10) where it expects `=` (61).
        //  - With relaxed unfolding but strict parsing, the parameters reassemble
        //    correctly but `X-TITLE=Hauptstraße 1-5\; 4041 Linz` embeds an
        //    escaped semicolon. RFC 5545 doesn't permit `;` in parameter values,
        //    so the parser sees `;` (59) where it expects `=` (61).
        //  - With both relaxed unfolding and relaxed parsing, the parser tolerates
        //    the escaped semicolon and the calendar parses successfully.
        if (succeeds) {
            assert failure == null
            assert calendar != null
        } else {
            assert failure != null
            assert failure.message.contains("Expected [61], read [${unexpectedByte}]")
        }

        where:
        label                         | relaxedUnfolding | relaxedParsing || succeeds | unexpectedByte
        'strict'                      | false            | false          || false    | 10
        'relaxed parsing only'        | false            | true           || false    | 10
        'relaxed unfolding only'      | true             | false          || false    | 59
        'relaxed unfolding + parsing' | true             | true           || true     | -1
    }

    def 'test parsing fidelity'() {
        given: 'a calendar object string'
        def input = '''BEGIN:VCALENDAR\r
BEGIN:VEVENT\r
DTSTAMP:20210618T114917Z\r
BEGIN:VALARM\r
TRIGGER:-P2D\r
END:VALARM\r
END:VEVENT\r
END:VCALENDAR\r\n'''

        when: 'string is parsed'
        Calendar calendar = new CalendarBuilder().build(new StringReader(input))

        then: 'output string matches input'
        def output = calendar as String
        output as String == input
    }
}
