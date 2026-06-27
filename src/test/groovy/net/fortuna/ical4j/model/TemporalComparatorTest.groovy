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
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class TemporalComparatorTest extends Specification {

    static final Instant ANCHOR_INSTANT = Instant.parse("2024-06-01T12:00:00Z")
    static final ZoneId SYDNEY = ZoneId.of("Australia/Sydney")

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
        ZonedDateTime.now() | ZonedDateTime.now() | false
        ZonedDateTime.now() | ZonedDateTime.now().minusMinutes(1) | true
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

    def 'same instant across types compares equal'() {
        given:
        TemporalComparator comparator = new TemporalComparator(ZoneOffset.UTC)
        def instant = ANCHOR_INSTANT
        def offsetDt = instant.atOffset(ZoneOffset.ofHours(10))
        def zonedSydney = instant.atZone(SYDNEY)
        def zonedUtc = instant.atZone(ZoneOffset.UTC)
        def localDt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)

        expect:
        comparator.compare(a, b) == 0
        comparator.compare(b, a) == 0

        where:
        a              | b
        ANCHOR_INSTANT | ANCHOR_INSTANT.atZone(SYDNEY)
        ANCHOR_INSTANT | ANCHOR_INSTANT.atOffset(ZoneOffset.ofHours(10))
        ANCHOR_INSTANT.atZone(SYDNEY) | ANCHOR_INSTANT.atZone(ZoneOffset.UTC)
        ANCHOR_INSTANT.atOffset(ZoneOffset.ofHours(10)) | ANCHOR_INSTANT.atZone(SYDNEY)
        LocalDateTime.ofInstant(ANCHOR_INSTANT, ZoneOffset.UTC) | ANCHOR_INSTANT.atZone(ZoneOffset.UTC)
        LocalDateTime.ofInstant(ANCHOR_INSTANT, ZoneOffset.UTC) | ANCHOR_INSTANT
    }

    def 'previously broken type pairs no longer throw and return correct sign'() {
        given:
        TemporalComparator comparator = new TemporalComparator(ZoneOffset.UTC)
        def earlier = ANCHOR_INSTANT
        def laterZdt = ANCHOR_INSTANT.plusSeconds(3600).atZone(SYDNEY)
        def laterLdt = LocalDateTime.ofInstant(ANCHOR_INSTANT.plusSeconds(3600), ZoneOffset.UTC)
        def earlierZdt = ANCHOR_INSTANT.atZone(SYDNEY)

        expect: 'Instant earlier than ZonedDateTime -> negative; reverse positive'
        comparator.compare(earlier, laterZdt) < 0
        comparator.compare(laterZdt, earlier) > 0

        and: 'LocalDateTime earlier than ZonedDateTime (resolved in UTC) -> negative; reverse positive'
        comparator.compare(LocalDateTime.ofInstant(earlier, ZoneOffset.UTC), laterZdt) < 0
        comparator.compare(laterZdt, LocalDateTime.ofInstant(earlier, ZoneOffset.UTC)) > 0

        and: 'OffsetDateTime vs ZonedDateTime ordering by instant'
        comparator.compare(earlier.atOffset(ZoneOffset.UTC), laterZdt) < 0
        comparator.compare(laterZdt, earlier.atOffset(ZoneOffset.UTC)) > 0

        and: 'ZonedDateTime vs ZonedDateTime ordering by instant'
        comparator.compare(earlierZdt, laterZdt) < 0
        comparator.compare(laterZdt, earlierZdt) > 0
    }

    def 'compare is sign-antisymmetric for all supported temporal type pairs'() {
        given:
        TemporalComparator comparator = new TemporalComparator(ZoneOffset.UTC)

        expect:
        Integer.signum(comparator.compare(a, b)) == -Integer.signum(comparator.compare(b, a))

        where:
        [a, b] << pairwise()
    }

    static List<List<Object>> pairwise() {
        def instantA = ANCHOR_INSTANT
        def instantB = ANCHOR_INSTANT.plusSeconds(3600)
        def offsetA = instantA.atOffset(ZoneOffset.ofHours(10))
        def offsetB = instantB.atOffset(ZoneOffset.ofHours(-5))
        def zonedA = instantA.atZone(SYDNEY)
        def zonedB = instantB.atZone(ZoneOffset.UTC)
        def localDtA = LocalDateTime.of(2024, 6, 1, 12, 0)
        def localDtB = LocalDateTime.of(2024, 6, 1, 13, 0)
        def localDateA = LocalDate.of(2024, 6, 1)
        def localDateB = LocalDate.of(2024, 6, 2)

        def values = [instantA, instantB, offsetA, offsetB, zonedA, zonedB,
                      localDtA, localDtB, localDateA, localDateB]

        def pairs = []
        for (a in values) {
            for (b in values) {
                if (!a.is(b)) {
                    pairs << [a, b]
                }
            }
        }
        pairs
    }
}
