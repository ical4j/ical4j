/**
 * Copyright (c) 2026, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.data.FoldingWriter
import net.fortuna.ical4j.data.UnfoldingReader
import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.component.XComponent
import net.fortuna.ical4j.model.property.TzOffsetFrom
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneOffset

/**
 * Regression tests for the third batch of findings from the contributed fuzz harnesses (output and copy defects).
 */
class FuzzFindingsBatch3Spec extends Specification {

    static Calendar parse(String ics) {
        new CalendarBuilder().build(new StringReader(ics.replace('\n', '\r\n')))
    }

    // zoneoffsetadapter-negative-offset-under-one-hour-loses-sign
    def 'a negative offset under one hour keeps its sign'() {
        expect:
        new ZoneOffsetAdapter(ZoneOffset.of(offset)).toString() == expected

        where:
        offset      | expected
        '-00:34'    | '-0034'
        '-00:01:15' | '-000115'
        '-00:25:21' | '-002521'
        '+00:34'    | '+0034'
        '-05:00'    | '-0500'
        '+05:30'    | '+0530'
        'Z'         | '+0000'
    }

    def 'a TZOFFSETFROM under one hour west of UTC is written back unchanged'() {
        expect:
        new TzOffsetFrom('-0034').value == '-0034'
    }

    // calendar-copy-xcomponent-unsupportedoperationexception
    def 'a calendar holding an experimental component can be copied and merged'() {
        given:
        def calendar = parse('''BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//example//EN
BEGIN:X-ACME-NOTE
X-ACME-TEXT:hello
END:X-ACME-NOTE
END:VCALENDAR
''')

        when:
        def copy = calendar.copy()

        then:
        copy == calendar
        copy.toString() == calendar.toString()
        copy.components[0] instanceof XComponent
        !copy.components[0].is(calendar.components[0])

        when:
        calendar.merge(new Calendar())

        then:
        noExceptionThrown()
    }

    // property-copy-non-x-named-xproperty-unsupportedoperationexception
    def 'a component holding an unknown IANA property can be copied and expanded'() {
        given:
        def calendar = parse('''BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//test//EN
BEGIN:VEVENT
UID:1@example.com
DTSTAMP:20250101T000000Z
DTSTART:20250101T100000Z
DTEND:20250101T110000Z
FOO;X-P=1:bar
END:VEVENT
END:VCALENDAR
''')
        VEvent event = calendar.getComponent(Component.VEVENT).get()

        when:
        VEvent copy = event.copy()

        then:
        copy.getProperty('FOO').get().value == 'bar'
        copy.getProperty('FOO').get().getParameter('X-P').get().value == '1'
        copy == event

        and:
        event.getOccurrences(new Period(Instant.parse('2025-01-01T00:00:00Z'),
                Instant.parse('2025-01-02T00:00:00Z'))).size() == 1
    }

    // foldingwriter-counts-utf16-chars-not-utf8-octets
    def 'folded lines are at most 75 UTF-8 octets'() {
        given:
        def out = new StringWriter()

        when:
        new FoldingWriter(out, FoldingWriter.MAX_FOLD_LENGTH).withCloseable {
            it.write("SUMMARY:${text}\r\n")
        }

        then:
        out.toString().split('\r\n').every { it.getBytes(StandardCharsets.UTF_8).length <= 75 }
        out.toString().replace('\r\n ', '') == "SUMMARY:${text}\r\n"

        where:
        text << ['é' * 100, '日本語' * 40, '😀' * 30, 'aé日😀' * 20, 'x' * 200]
    }

    def 'an ASCII line is still folded at the fold length'() {
        given:
        def out = new StringWriter()

        when:
        new FoldingWriter(out, 10).withCloseable { it.write('0123456789ABCDEFGHIJ\r\n') }

        then:
        out.toString() == '0123456789\r\n ABCDEFGHI\r\n J\r\n'
    }

    // foldingwriter-splits-surrogate-pair-across-write-calls
    def 'a surrogate pair is not split when written across write calls'() {
        given:
        def out = new StringWriter()

        when:
        new FoldingWriter(out, 3).withCloseable { writer ->
            '😀😀\r\n'.toCharArray().each { writer.write((int) it) }
        }

        then:
        out.toString() == '😀\r\n 😀\r\n'
    }

    def 'a surrogate pair is not split by a chunk boundary at the default fold length'() {
        given:
        def text = 'SUMMARY:' + ('x' * 64) + '😀😀😀'
        def out = new StringWriter()

        when:
        new FoldingWriter(out).withCloseable { writer ->
            for (int i = 0; i < text.length(); i++) {
                writer.write(text, i, 1)
            }
        }

        then:
        !out.toString().split('\r\n ').any { it && (Character.isHighSurrogate(it[-1] as char)
                || Character.isLowSurrogate(it[0] as char)) }
        out.toString().replace('\r\n ', '') == text
    }

    // period-tostring-formats-end-with-start-format
    def 'a period is written with the form of each of its dates'() {
        expect:
        Period.parse(value).toString() == value

        where:
        value << ['20240101T090000Z/20240101', '20240101T090000Z/20240101T090000', '20240101/20240102',
                  '20240101T090000Z/PT1H', '20240101T090000/20240101T100000Z']
    }

    def 'a period list keeps the form of each period'() {
        expect:
        PeriodList.parse(value).toString() == value

        where:
        value << ['20240101/P1D', '20240101T090000Z/PT1H', '20240101T090000/PT1H',
                  '20240101T090000Z/PT1H,20240102T090000Z/20240102T100000Z']
    }

    def 'a UTC RDATE period is written back with its Z'() {
        given:
        def calendar = parse('''BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//test//EN
BEGIN:VEVENT
UID:a@test
DTSTAMP:20240101T000000Z
DTSTART:20240101T090000Z
RDATE;VALUE=PERIOD:20240102T090000Z/PT1H
END:VEVENT
END:VCALENDAR
''')

        expect:
        calendar.toString().contains('RDATE;VALUE=PERIOD:20240102T090000Z/PT1H\r\n')
    }

    def 'a date RDATE period can be written'() {
        given:
        def calendar = parse('''BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//test//EN
BEGIN:VEVENT
UID:a@test
DTSTAMP:20240101T000000Z
DTSTART:20240101T090000Z
RDATE;VALUE=PERIOD:20240101/P1D
END:VEVENT
END:VCALENDAR
''')

        expect:
        calendar.toString().contains('RDATE;VALUE=PERIOD:20240101/P1D\r\n')
    }

    // temporaladapter-numeric-offset-date-time-written-as-floating
    def 'a date-time with a numeric offset keeps its instant when written'() {
        given:
        def adapter = TemporalAdapter.parse('20240101T090000+0100')

        when:
        def back = TemporalAdapter.parse(adapter.toString())

        then:
        adapter.toString() == '20240101T080000Z'
        TemporalAdapter.isUtc(back.temporal)
        Instant.from(back.temporal) == Instant.parse('2024-01-01T08:00:00Z')
    }

    def 'a UTC date-time is still written with its Z'() {
        expect:
        TemporalAdapter.parse('20240101T090000Z').toString() == '20240101T090000Z'
    }

    // unfoldingreader-bulk-read-skips-fold-after-partial-pattern
    def 'a bulk read unfolds the same text as reading char by char'() {
        given:
        def single = new UnfoldingReader(new StringReader(text), 8192, relaxed)
        def byChar = new StringBuilder()
        for (int c; (c = single.read()) >= 0;) {
            byChar.append((char) c)
        }

        and:
        def bulk = new UnfoldingReader(new StringReader(text), 8192, relaxed)
        def byChunk = new StringBuilder()
        char[] buf = new char[chunk]
        for (int n; (n = bulk.read(buf, 0, buf.length)) >= 0;) {
            byChunk.append(buf, 0, n)
        }

        expect:
        byChar.toString() == expected
        byChunk.toString() == expected

        where:
        text                         | relaxed | chunk | expected
        'A:x\r\r\n y\r\n'            | false   | 8192  | 'A:x\ry\r\n'
        'A:x\r\r\n y\r\n'            | false   | 1     | 'A:x\ry\r\n'
        'A:x\r\r\n y\r\n'            | false   | 3     | 'A:x\ry\r\n'
        'A:ab\r\n c\r\n\td\r\nB:e\r\n' | false | 2     | 'A:abcd\r\nB:e\r\n'
        'A:ab\n c\r\nB:e\r\n'        | true    | 4     | 'A:abc\r\nB:e\r\n'
    }

    def 'a buffered reader over an unfolding reader reads unfolded lines'() {
        given:
        def reader = new BufferedReader(new UnfoldingReader(new StringReader('A:x\r\r\n y\r\nB:z\r\n'), 8192, false))

        expect:
        reader.readLine() == 'A:x'
        reader.readLine() == 'y'
        reader.readLine() == 'B:z'
        reader.readLine() == null
    }

    def 'a bulk read honours the offset'() {
        given:
        def reader = new UnfoldingReader(new StringReader('ab\r\n c'), 8192, false)
        char[] buf = new char[10]

        when:
        int n = reader.read(buf, 4, 6)

        then:
        n == 3
        new String(buf, 4, 3) == 'abc'
    }

    // PropertyCodec ignores \N (found by the strengthened scenario 08 oracle)
    def 'an escaped capital N decodes as a line break'() {
        expect:
        PropertyCodec.INSTANCE.decode(encoded) == decoded

        where:
        encoded       | decoded
        'a\\Nb'       | 'a\nb'
        'a\\nb'       | 'a\nb'
        'a\\\\Nb'     | 'a\\Nb'
        'a\\,b\\;c'   | 'a,b;c'
    }

    def 'a DESCRIPTION with an escaped capital N is parsed with a line break'() {
        given:
        def calendar = parse('''BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//test//EN
BEGIN:VEVENT
UID:a@test
DTSTAMP:20240101T000000Z
DESCRIPTION:line one\\Nline two
END:VEVENT
END:VCALENDAR
''')

        expect:
        calendar.getComponent(Component.VEVENT).get().getProperty(Property.DESCRIPTION).get().value ==
                'line one\nline two'
    }
}
