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

import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.model.property.TzId
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.zone.ZoneRulesProvider

class DateListTestSpec extends Specification {

    def 'test date list string parsing'() {
        expect: 'parsed string produces expected date list'
        DateList.parse(value) == expectedDateList

        where:
        value               | expectedDateList
        ''                  | DateList.emptyList()
        '20220617'          | new DateList<>(LocalDate.of(2022, 06, 17))
        '20220617,20220618' | new DateList<>(LocalDate.of(2022, 06, 17), LocalDate.of(2022, 06, 18))
        '20220617T140000'   | new DateList<>(LocalDateTime.of(2022, 06, 17, 14, 0))
        '20220617T140000Z'  | new DateList<>(LocalDateTime.of(2022, 06, 17, 14, 0).atOffset(ZoneOffset.UTC))
    }

    def 'test date list string parsing in another timezone'() {
        expect: 'parsed string produces expected date list'
        DateList.parse(value, ZoneId.of(zoneId)) == expectedDateList

        where:
        value               | zoneId            | expectedDateList
        '20220617T140000'   | 'Europe/London'   | new DateList<>(LocalDateTime.of(2022, 06, 17, 14, 0).atZone(ZoneId.of('Europe/London')))
    }

    def 'test date list string parsing with a custom timezone'() {
        given: 'a custom timezone registry'
        TimeZoneRegistry timeZoneRegistry = new TimeZoneRegistryImpl()

        and: 'a custom timezone'
        VTimeZone vTimeZone = timeZoneRegistry.getTimeZone('Europe/Amsterdam').getVTimeZone()
        vTimeZone.replace(new TzId('Europe/Atlantis'))

        TimeZone timeZone = new TimeZone(vTimeZone)
        timeZoneRegistry.register(timeZone)

        and: 'registered zone rules provider'
        ZoneRulesProvider.registerProvider(new ZoneRulesProviderImpl(timeZoneRegistry));

        and: 'corresponding tzid parameter'
        net.fortuna.ical4j.model.parameter.TzId tzIdParam = new net.fortuna.ical4j.model.parameter.TzId('Europe/Atlantis')

        expect: 'parsed string produces expected date list'
        DateList dateList = DateList.parse('20220617T140000', tzIdParam, timeZoneRegistry)
        dateList.dates == new DateList<>(LocalDateTime.of(2022, 06, 17, 14, 0).atZone(timeZoneRegistry.getZoneId('Europe/Atlantis'))).dates
    }
}
