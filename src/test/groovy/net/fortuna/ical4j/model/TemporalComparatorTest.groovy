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

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime

class TemporalComparatorTest extends Specification {

    def 'compare like temporals'() {
        given: 'a comparator instance'
        TemporalComparator comparator = []

        expect: 'comparison result matches expected'
        comparator.compare(o1, o2) > 0 == isO1AfterO2

        where:
        o1                  | o2                            | isO1AfterO2
        Instant.now()       | Instant.now()                 | false
        Instant.now()       | Instant.now().minusSeconds(1)                 | true
        LocalDate.now()     | LocalDate.now().plusDays(1)  | false
        LocalDate.now()     | LocalDate.now().minusDays(1)  | true
        LocalDateTime.now() | LocalDateTime.now()           | false
        LocalDateTime.now() | LocalDateTime.now().minusMinutes(1)           | true
        OffsetDateTime.now() | OffsetDateTime.now() | false
        OffsetDateTime.now() | OffsetDateTime.now().minusMinutes(1) | true
    }

    def 'compare unlike temporals'() {
        given: 'a comparator instance'
        TemporalComparator comparator = []

        expect: 'comparison result matches expected'
        comparator.compare(o1, o2) > 0 == isO1AfterO2

        where:
        o1                  | o2                            | isO1AfterO2
        Instant.now()       | LocalDate.now()                 | true
        Instant.now()       | LocalDate.now().plusDays(1)                 | false
        LocalDate.now()     | LocalDateTime.now()  | false
        LocalDate.now()     | LocalDateTime.now().minusDays(1)  | true
        LocalDateTime.now() | Instant.now()           | false
        LocalDateTime.now() | Instant.now().minusSeconds(1)           | true
    }
}
