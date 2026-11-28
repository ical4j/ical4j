package net.fortuna.ical4j

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.data.ParserException
import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.component.Standard
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.parameter.Cn
import net.fortuna.ical4j.model.property.*
import net.fortuna.ical4j.transform.compliance.TzHelper
import net.fortuna.ical4j.util.Numbers
import net.fortuna.ical4j.util.Strings
import spock.lang.Specification

import java.time.*
import java.time.format.DateTimeParseException

/**
 * Regressions for crashes found by the contributed fuzz harnesses (src/fuzz): each input used to fail with a
 * NullPointerException, ClassCastException or StringIndexOutOfBoundsException.
 */
class FuzzFindingsBatch2Spec extends Specification {

    private static Calendar parse(String... lines) {
        new CalendarBuilder().build(new StringReader((lines.toList() + ['']).join('\r\n')))
    }

    private static VEvent parseEvent(String... lines) {
        parse(*(['BEGIN:VCALENDAR', 'VERSION:2.0', 'PRODID:-//test//EN', 'BEGIN:VEVENT', 'UID:1@example.com']
                + lines.toList() + ['END:VEVENT', 'END:VCALENDAR'])).getComponent(Component.VEVENT).get() as VEvent
    }

    def 'an empty date value [#line] is written back empty'() {
        given:
        def event = parseEvent(line)

        expect:
        event.toString().contains("${line}\r\n")

        where:
        line << ['DTSTAMP:', 'DTSTART:', 'DTSTART;TZID=Europe/Paris:', 'DTSTART;TZID=Unknown/Zone:']
    }

    def 'an event with an empty DTSTART [#line] derives no end date'() {
        expect:
        parseEvent(line, 'DURATION:PT1H').getEndDate(true) == Optional.empty()

        where:
        line << ['DTSTART:', 'DTSTART;TZID=Europe/Paris:']
    }

    def 'a BYDAY item shorter than two characters [#value] is rejected'() {
        when:
        new WeekDay(value)

        then:
        thrown(IllegalArgumentException)

        where:
        value << ['0', 'M', '']
    }

    def 'an empty numeric rule part [#rule] is rejected'() {
        when:
        new Recur<>(rule)

        then:
        thrown(IllegalArgumentException)

        where:
        rule << ['FREQ=MONTHLY;BYMONTHDAY=;INTERVAL=1', 'FREQ=DAILY;BYHOUR=1,,2', 'FREQ=YEARLY;BYSETPOS=']
    }

    def 'Numbers.parseInt rejects an empty value with NumberFormatException'() {
        when:
        Numbers.parseInt('')

        then:
        thrown(NumberFormatException)
    }

    def 'a period without a slash [#value] is rejected with DateTimeParseException'() {
        when:
        Period.parse(value)

        then:
        thrown(DateTimeParseException)

        where:
        value << ['P1W', '20240101T000000Z', '']
    }

    def 'a period list without a slash is rejected with DateTimeParseException'() {
        when:
        PeriodList.parse('P1W')

        then:
        thrown(DateTimeParseException)
    }

    def 'a period mixing a DATE start and a DATE-TIME end is rejected with DateTimeParseException'() {
        when:
        PeriodList.parse('20240101/20240101T090000Z')

        then:
        thrown(DateTimeParseException)
    }

    def 'a duration between a DATE and a DATE-TIME is not computed as a calendar period'() {
        when:
        TemporalAmountAdapter.from(LocalDate.of(2024, 1, 1), OffsetDateTime.of(2024, 1, 1, 9, 0, 0, 0, ZoneOffset.UTC))

        then:
        thrown(DateTimeException)
    }

    def 'an invalid UTC offset [#value] is rejected'() {
        when:
        new UtcOffset(value)

        then:
        thrown(IllegalArgumentException)

        where:
        value << ['+99:9', '+1:00', '-9980', '+0160', '+010075', '+01:0000', '+01a0']
    }

    def 'a valid UTC offset [#value] is parsed'() {
        expect:
        new UtcOffset(value).offset == expected

        where:
        value     | expected
        '+0100'   | 3_600_000L
        '-0530'   | -19_800_000L
        '+010030' | 3_630_000L
        '+01:30'  | 5_400_000L
    }

    def 'a lone double quote is not a quoted string'() {
        expect:
        Strings.unquote('"') == '"'
        new Cn('"').value == '"'
        Strings.unquote('""') == ''
    }

    def 'a lone double quote parameter value survives a write and re-parse'() {
        given:
        def event = parseEvent('DTSTAMP:20240101T000000Z', 'ATTENDEE;CN=^\':mailto:a@example.com')

        expect:
        event.getRequiredProperty(Property.ATTENDEE).getRequiredParameter(Parameter.CN).value == '"'
    }

    def 'a PERIOD-valued RDATE has no dates'() {
        given:
        def event = parseEvent('DTSTAMP:20250101T000000Z', 'DTSTART:20250101T100000Z',
                'RDATE;VALUE=PERIOD:20250102T100000Z/20250102T110000Z')
        RDate<?> rdate = event.getRequiredProperty(Property.RDATE)

        expect:
        rdate.periods.isPresent()
        rdate.dates == []
    }

    def 'a TZID of only quotes [#value] is unknown'() {
        expect:
        TzHelper.getCorrectedTimeZoneIdFrom(value) == null

        where:
        value << ['""', '""""']
    }

    def 'a top-level #name is reported as not allowed at the calendar level'() {
        when:
        parse('BEGIN:VCALENDAR', "BEGIN:${name}", *props, "END:${name}", 'END:VCALENDAR')

        then:
        def e = thrown(ParserException)
        !(e.cause instanceof ClassCastException)
        e.message.contains("${name} is not allowed at the calendar level")

        where:
        name       | props
        'VALARM'   | ['ACTION:DISPLAY']
        'STANDARD' | ['DTSTART:19700101T000000', 'TZOFFSETFROM:+0100', 'TZOFFSETTO:+0100']
    }

    def 'an observance RDATE with a DATE value is an onset at the start of that day'() {
        given:
        def standard = new Standard()
        standard.add(new DtStart<>(LocalDateTime.of(1970, 1, 1, 0, 0)))
        standard.add(new TzOffsetFrom(ZoneOffset.ofHours(1)))
        standard.add(new TzOffsetTo(ZoneOffset.ofHours(1)))
        standard.add(new RDate<>(new ParameterList(), '19800101'))

        expect:
        standard.getLatestOnset(Instant.parse('1990-01-01T00:00:00Z')) ==
                OffsetDateTime.of(1980, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(1))
    }

    def 'a VTIMEZONE whose observance has a DATE RDATE is registered'() {
        when:
        def calendar = parse('BEGIN:VCALENDAR', 'BEGIN:VTIMEZONE', 'TZID:Test/Zone',
                'BEGIN:STANDARD', 'DTSTART:19700101T000000', 'TZOFFSETFROM:+0100', 'TZOFFSETTO:+0100',
                'RDATE:19800101', 'END:STANDARD',
                'BEGIN:STANDARD', 'DTSTART:19700101T000000', 'TZOFFSETFROM:+0100', 'TZOFFSETTO:+0100',
                'END:STANDARD', 'END:VTIMEZONE', 'END:VCALENDAR')

        then:
        calendar.getComponent(Component.VTIMEZONE).isPresent()
    }
}
