/*
 *  Copyright (c) 2023, Ben Fortuna
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

import net.fortuna.ical4j.model.TimeZoneRegistry
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.Temporal

class DatePropertySpec extends Specification {

    @Shared
    TimeZoneRegistry timeZoneRegistry = TimeZoneRegistryFactory.instance.createRegistry()

    def 'test auto population of tzid'() {
        expect: 'tzid is populated with expected value'
        Optional<net.fortuna.ical4j.model.parameter.TzId> tzId = property.getParameter('TZID')
        tzId.ifPresent {it.toZoneId() == expectedTzid }

        where:
        property                                                                | expectedTzid
        createDtStart(ZonedDateTime.now(), null)                                | ZoneId.systemDefault()
        createDtStart(ZonedDateTime.of(
                LocalDateTime.of(2022, 12, 16, 18, 0, 0),
                ZoneId.of("Europe/Warsaw")), null)                              | ZoneId.of("Europe/Warsaw")
    }

    def 'test auto population of tzid with tz registry'() {
        expect: 'tzid is populated with expected value'
        Optional<net.fortuna.ical4j.model.parameter.TzId> tzId = property.getParameter('TZID')
        tzId.ifPresent {it.toZoneId(timeZoneRegistry) == expectedTzid }

        where:
        property                                                | expectedTzid
        createDtStart(ZonedDateTime.now(), timeZoneRegistry)    | ZoneId.systemDefault()
        createDtStart(ZonedDateTime.of(
                LocalDateTime.of(2022, 12, 16, 18, 0, 0),
                ZoneId.of("Europe/Warsaw")), null)                              | ZoneId.of("Europe/Warsaw")
    }

    def 'test auto population of parameters'() {
        given: 'a DTSTART initialised with a DATE-TIME value'
        DtStart dtStart = new DtStart(ZonedDateTime.of(
                LocalDateTime.of(2022, 12, 16, 18, 0, 0),
                ZoneId.of("Europe/Warsaw")))

        expect: 'a TZID parameter is populated'
        Optional<net.fortuna.ical4j.model.parameter.TzId> tzid = dtStart.getParameter('TZID')
        tzid.ifPresent { it.toZoneId() == ZoneId.of("Europe/Warsaw") }

        and: 'VALUE parameter is not populated'
        !dtStart.getParameter('VALUE').present
    }

    def 'test auto update of parameters'() {
        given: 'a DTSTART initialised with a DATE-TIME value'
        DtStart dtStart = new DtStart(ZonedDateTime.of(
                LocalDateTime.of(2022, 12, 16, 18, 0, 0),
                ZoneId.of("Europe/Warsaw")))

        and: 'set to a DATE value'
        dtStart.date = LocalDate.of(2022, 12, 16)

        expect:
        !dtStart.getParameter('TZID').present
        dtStart.getParameter('VALUE') == Optional.of(Value.DATE)
    }

    DtStart createDtStart(Temporal temporal, TimeZoneRegistry timeZoneRegistry) {
        DtStart dtStart = new DtStart()
        dtStart.setTimeZoneRegistry(timeZoneRegistry)
        dtStart.setDate(temporal)
        dtStart
    }
}
