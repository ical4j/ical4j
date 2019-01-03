package net.fortuna.ical4j.model

import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

class TemporalAmountAdapterTest extends Specification {

    def "verify string representation"() {
        expect:
        new TemporalAmountAdapter(duration) as String == expectedValue

        where:
        duration                    | expectedValue
        Duration.ofHours(4)         | "PT4H"
        Duration.ofHours(-4)         | "-PT4H"
        Duration.ofDays(12)         | "P12D"
        java.time.Period.ofDays(12) | "P12D"
        java.time.Period.ofWeeks(7) | "P7W"
        java.time.Period.ofDays(365) | "P365D"
        java.time.Period.ofDays(364) | "P52W"
        java.time.Period.ofYears(1) | "P52W"
        java.time.Period.ofMonths(6) | "P24W"
        Duration.ofDays(15).plusHours(5).plusSeconds(20)    | 'P15DT5H0M20S'
    }

    def 'verify temporalamount creation'() {
        expect:
        TemporalAmountAdapter.from(duration).duration == expectedTemporalAmount

        where:
        duration       | expectedTemporalAmount
        new Dur(1, 2, 3, 4)  | Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4)
        new Dur('PT-8H')  | Duration.ofHours(-8)
        new Dur(5)  | java.time.Period.ofWeeks(5)
        new Dur(-9)  | java.time.Period.ofWeeks(-9)
    }

    @Unroll
    def 'validate string representation: #dur'() {
        expect: 'derived string representation equals expected'
        dur.toString() == expectedString

        where:
        dur						| expectedString
        TemporalAmountAdapter.from(new Dur(33))				| 'P33W'
        TemporalAmountAdapter.from(new Dur('-P2D'))			| '-P2D'
        TemporalAmountAdapter.from(new Dur(-2, 0, 0, 0))	| '-P2D'
    }

    @Unroll
    def 'verify duration plus time operations: #duration'() {
        expect: 'derived end time value equals expected'
        TemporalAmountAdapter.from(new Dur(duration)).getTime(new DateTime(start)) == new DateTime(expectedEnd)

        where:
        duration	| start				| expectedEnd
        'P1D' | '20110326T110000' | '20110327T110000'
    }

    @Unroll
    def 'verify duration plus time operations in different timezones: #duration'() {
        setup: 'initialise timezone registry'
        def tzRegistry = TimeZoneRegistryFactory.instance.createRegistry()

        expect: 'derived end time value equals expected'
        def tz = tzRegistry.getTimeZone(timezone)
        TemporalAmountAdapter.from(new Dur(duration)).getTime(new DateTime(start, tz)) == new DateTime(expectedEnd, tz)

        where:
        duration	| timezone					| start				| expectedEnd
        'P1D' | 'America/Los_Angeles' | '20110326T110000' | '20110327T110000'
    }

    @Unroll
    def 'verify duration plus time operations in different timezones with overridden platform default: #duration'() {
        setup: 'override platform default timezone'
        def originalPlatformTz = TimeZone.default
        TimeZone.default = TimeZone.getTimeZone('Europe/Paris')

        and: 'initialise timezone registry'
        def tzRegistry = TimeZoneRegistryFactory.instance.createRegistry()

        expect: 'derived end time value equals expected'
        def tz = tzRegistry.getTimeZone(timezone)
        TemporalAmountAdapter.from(new Dur(duration)).getTime(new DateTime(start, tz)) == new DateTime(expectedEnd, tz)

        cleanup: 'restore platform default timezone'
        TimeZone.default = originalPlatformTz

        where:
        duration	| timezone					| start				| expectedEnd
        'P1D' | 'America/Los_Angeles' | '20110326T110000' | '20110327T110000'
    }

    @Unroll
    def 'verify duration plus date operations: #duration'() {
        expect: 'derived end date value equals expected'
        TemporalAmountAdapter.from(new Dur(duration)).getTime(new Date(start)) == new Date(expectedEnd)

        where:
        duration	| start				| expectedEnd
        'P1D' | '20110312' | '20110313'
        'P1D' | '20110313' | '20110314'
    }

    @Unroll
    def 'verify duration plus date operations with overriden platform default timezone: #duration'() {
        setup: 'override platform default timezone'
        def originalPlatformTz = TimeZone.default
        TimeZone.default = TimeZone.getTimeZone('America/New_York')

        expect: 'derived end date value equals expected'
        TemporalAmountAdapter.from(new Dur(duration)).getTime(new Date(start)) == new Date(expectedEnd)

        cleanup: 'restore platform default timezone'
        TimeZone.default = originalPlatformTz

        where:
        duration	| start				| expectedEnd
        'P1D' | '20110312' | '20110313'
        'P1D' | '20110313' | '20110314'
    }

    def 'extension module test: plus'() {
        expect:
        TemporalAmountAdapter.from(new Dur('P1D')).duration + TemporalAmountAdapter.from(new Dur('P1D')).duration == TemporalAmountAdapter.from(new Dur('P2D')).duration
    }

    def 'extension module test: negative'() {
        expect:
        -TemporalAmountAdapter.from(new Dur('P1D')).duration == TemporalAmountAdapter.from(new Dur('-P1D')).duration
    }

}
