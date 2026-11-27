/*
 *  Copyright (c) 2022, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarDateFormatTest extends Specification {

    def 'test parsing with an explicit ISO date formatter'() {
        given: 'a calendar date format with explicit formatter'
        CalendarDateFormat calendarDateFormat = [DateTimeFormatter.ISO_DATE]

        expect: 'parsed value matches expected'
        def parsed = calendarDateFormat.parse(dateString)
        LocalDate.from(parsed).isEqual(expectedValue)

        where:
        dateString      | expectedValue
        '1975-07-17'    | LocalDate.of(1975, 7, 17)
    }

    def 'equivalent temporal queries work as map keys'() {
        given:
        def first = queryType.getDeclaredConstructor().newInstance()
        def second = queryType.getDeclaredConstructor().newInstance()
        def values = [(first): 'value']

        expect:
        first == second
        values[second] == 'value'

        where:
        queryType << [CalendarDateFormat.LocalDateTemporalQuery,
                      CalendarDateFormat.LocalDateTimeTemporalQuery,
                      CalendarDateFormat.OffsetDateTimeTemporalQuery,
                      CalendarDateFormat.InstantTemporalQuery]
    }

    def 'equivalent date formats work as map keys'() {
        given:
        def first = new CalendarDateFormat('yyyyMMdd', queryType.getDeclaredConstructor().newInstance())
        def second = new CalendarDateFormat('yyyyMMdd', queryType.getDeclaredConstructor().newInstance())
        def values = [(first): 'value']

        expect:
        first == second
        values[second] == 'value'

        where:
        queryType << [CalendarDateFormat.LocalDateTemporalQuery,
                      CalendarDateFormat.LocalDateTimeTemporalQuery,
                      CalendarDateFormat.OffsetDateTimeTemporalQuery,
                      CalendarDateFormat.InstantTemporalQuery]
    }

    def 'serialized date formats remain usable as map keys'() {
        given:
        def bytes = new ByteArrayOutputStream()
        bytes.withObjectOutputStream { it.writeObject(original) }
        def restored = new ByteArrayInputStream(bytes.toByteArray()).withObjectInputStream {
            it.readObject()
        }
        def values = [(original): 'value']

        expect:
        restored == original
        values[restored] == 'value'

        where:
        original << [CalendarDateFormat.DATE_FORMAT,
                     CalendarDateFormat.FLOATING_DATE_TIME_FORMAT,
                     CalendarDateFormat.UTC_DATE_TIME_FORMAT,
                     CalendarDateFormat.RELAXED_DATE_TIME_FORMAT,
                     CalendarDateFormat.DEFAULT_PARSE_FORMAT]
    }

    def 'formats without queries retain their equality and hash behavior'() {
        given:
        def first = new CalendarDateFormat('yyyyMMdd')
        def equivalent = new CalendarDateFormat('yyyyMMdd')
        def different = new CalendarDateFormat('yyyy-MM-dd')
        def values = [(first): 'value']

        expect:
        first == equivalent
        first != different
        values[equivalent] == 'value'
        values[different] == null
    }

    def 'temporal queries preserve their existing serialization identifiers'() {
        expect:
        ObjectStreamClass.lookup(queryType).serialVersionUID == serialVersionUID

        where:
        queryType                                      | serialVersionUID
        CalendarDateFormat.LocalDateTemporalQuery       | 7339970039696488632L
        CalendarDateFormat.LocalDateTimeTemporalQuery   | 408655839257738041L
        CalendarDateFormat.OffsetDateTimeTemporalQuery  | -2140574181387153175L
        CalendarDateFormat.InstantTemporalQuery         | -5217778645190648482L
    }
}
