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

package net.fortuna.ical4j.model.property

import spock.lang.Specification

import java.time.*
import java.time.temporal.Temporal

class DtStartTestSpec extends Specification {

    def 'test dtstart with default timezone'() {
        given: 'a floating date string'
        def date = '20220617T140000'

        and: 'a dtstart property with default timezone'
        DtStart<ZonedDateTime> dtstart = new DtStart<>()
        dtstart.setDefaultTimeZone(ZoneId.of('Asia/Singapore'))

        when: 'dtstart value is set from date string'
        dtstart.setValue(date)

        then: 'dtstart date is rendered local in the default timezone'
        dtstart.date.zone.id == 'Asia/Singapore'
    }

    def 'test dtstart string rep'() {
        expect: 'dtstart string matches expected'
        new DtStart<>(date) as String == expectedString

        where:
        date                                                | expectedString
        LocalDate.of(2023, 11, 10)                          | 'DTSTART;VALUE=DATE:20231110\r\n'
        LocalDateTime.of(2023, 11, 10, 1, 1, 1)             | 'DTSTART:20231110T010101\r\n'
        LocalDateTime.of(2023, 11, 10, 1, 1, 1)
                .atZone(ZoneId.of('Australia/Melbourne'))   | 'DTSTART;TZID=Australia/Melbourne:20231110T010101\r\n'
        LocalDateTime.of(2023, 11, 10, 1, 1, 1)
                .toInstant(ZoneOffset.UTC)                  | 'DTSTART:20231110T010101Z\r\n'
        ZonedDateTime.of(2023, 11, 15, 8, 30, 0, 0,
                ZoneId.of("America/Sao_Paulo"))             | 'DTSTART;TZID=America/Sao_Paulo:20231115T083000\r\n'
        ZonedDateTime.of(2023, 5, 15, 8, 30, 0, 0,
                ZoneId.of("Europe/London"))                 | 'DTSTART;TZID=Europe/London:20230515T083000\r\n'
        ZonedDateTime.of(2023, 12, 14, 17, 0, 0, 0,
                ZoneId.of("America/Los_Angeles"))           | 'DTSTART;TZID=America/Los_Angeles:20231214T170000\r\n'
        ZonedDateTime.of(2023, 12, 14, 17, 0, 0, 0,
                ZoneId.of("America/Toronto"))           | 'DTSTART;TZID=America/Toronto:20231214T170000\r\n'
    }

    def 'test factory creation'() {
        given: 'a factory'
        DtStart.Factory<LocalDate> factory = []

        expect: 'result matches expected'
        DtStart<Temporal> dtstart = factory.createProperty(date)
        dtstart.refreshParameters()
        dtstart as String == expectedValue

        where:
        date                | expectedValue
        '00011225'          | 'DTSTART;VALUE=DATE:00011225\r\n'
        '00011225T013000'   | 'DTSTART:00011225T013000\r\n'
    }
}
