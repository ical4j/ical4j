package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * RFC 5545 §3.3.5: a local time that occurs twice at a DST transition (an overlap) denotes the first occurrence,
 * i.e. the offset in force before the transition. Recurrence instances falling in an overlap must therefore carry
 * the earlier offset regardless of how the candidate was derived (seed increment, BYDAY expansion, WKST).
 * See issue #716.
 */
class RecurDstOverlapSpec extends Specification {

    static final ZoneId LA = ZoneId.of('America/Los_Angeles')
    static final ZoneOffset PDT = ZoneOffset.ofHours(-7)
    static final ZoneOffset PST = ZoneOffset.ofHours(-8)

    def 'weekly BYDAY instance in the fall-back overlap carries the pre-transition offset (#716)'() {
        given: 'the rule from the issue, with and without WKST=SU'
        Recur<ZonedDateTime> rrule = new Recur<>("FREQ=WEEKLY;INTERVAL=1;BYDAY=FR,SA,SU,MO${wkst}")
        ZonedDateTime seed = ZonedDateTime.of(2000, 2, 1, 1, 30, 0, 0, LA)
        Period<ZonedDateTime> period = new Period<>(
                Instant.parse('2019-10-20T20:00:00Z').atZone(LA),
                Instant.parse('2019-11-10T00:00:00Z').atZone(LA))

        when:
        List<ZonedDateTime> list = rrule.getDates(seed, period)
        def nov3 = list.find { it.toLocalDate() == ZonedDateTime.of(2019, 11, 3, 1, 30, 0, 0, LA).toLocalDate() }

        then: '2019-11-03T01:30 occurs twice; the instance is the first occurrence (-07:00)'
        nov3.offset == PDT
        nov3 == ZonedDateTime.of(2019, 11, 3, 1, 30, 0, 0, LA)

        and: 'instances either side of the transition are unaffected'
        list.find { it.toLocalDate().toString() == '2019-11-02' }.offset == PDT
        list.find { it.toLocalDate().toString() == '2019-11-04' }.offset == PST

        where:
        wkst << ['', ';WKST=SU', ';WKST=MO']
    }

    def 'daily instance in the fall-back overlap carries the pre-transition offset regardless of seed offset'() {
        given: 'a daily rule seeded in standard time (PST) and in daylight time (PDT)'
        Recur<ZonedDateTime> rrule = new Recur<>('FREQ=DAILY')
        ZonedDateTime seed = ZonedDateTime.of(seedYear, seedMonth, 1, 1, 30, 0, 0, LA)
        Period<ZonedDateTime> period = new Period<>(
                ZonedDateTime.of(2019, 11, 2, 0, 0, 0, 0, LA),
                ZonedDateTime.of(2019, 11, 5, 0, 0, 0, 0, LA))

        when:
        List<ZonedDateTime> list = rrule.getDates(seed, period)

        then:
        list*.offset == [PDT, PDT, PST]
        list[1] == ZonedDateTime.of(2019, 11, 3, 1, 30, 0, 0, LA)

        where:
        seedYear | seedMonth
        2019     | 2
        2019     | 7
    }

    def 'OffsetDateTime seed keeps its fixed offset across the transition'() {
        given:
        Recur<OffsetDateTime> rrule = new Recur<>('FREQ=DAILY')
        OffsetDateTime seed = OffsetDateTime.of(2019, 11, 1, 1, 30, 0, 0, PDT)
        Period<OffsetDateTime> period = new Period<>(
                OffsetDateTime.of(2019, 11, 2, 0, 0, 0, 0, PDT),
                OffsetDateTime.of(2019, 11, 5, 0, 0, 0, 0, PDT))

        expect:
        rrule.getDates(seed, period)*.offset == [PDT, PDT, PDT]
    }

    def 'spring-forward gap still shifts forward by the gap length'() {
        given: 'a daily rule at 02:00, which does not exist on 2025-03-09'
        Recur<ZonedDateTime> rrule = new Recur<>('FREQ=DAILY')
        ZonedDateTime seed = ZonedDateTime.of(2025, 3, 8, 2, 0, 0, 0, LA)
        Period<ZonedDateTime> period = new Period<>(
                ZonedDateTime.of(2025, 3, 8, 0, 0, 0, 0, LA),
                ZonedDateTime.of(2025, 3, 10, 12, 0, 0, 0, LA))

        expect:
        rrule.getDates(seed, period)*.toString() == [
                '2025-03-08T02:00-08:00[America/Los_Angeles]',
                '2025-03-09T03:00-07:00[America/Los_Angeles]',
                '2025-03-10T02:00-07:00[America/Los_Angeles]']
    }
}
