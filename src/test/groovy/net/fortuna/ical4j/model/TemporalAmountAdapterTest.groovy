package net.fortuna.ical4j.model

import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class TemporalAmountAdapterTest extends Specification {

    def cleanup() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)
    }

    def "verify string representation"() {
        setup: 'Set default seed date for test consistency'
        def seed = LocalDateTime.parse("2021-04-01T00:00:00")

        expect:
        new TemporalAmountAdapter(duration).toString(seed) == expectedValue

        where:
        duration                    | expectedValue
        Duration.ofHours(4)         | "PT4H"
        Duration.ofHours(-4)         | "-PT4H"
        Duration.ofDays(12)         | "P12D"
        Duration.ofDays(12).plusMinutes(30)     | "P12DT30M"
        Duration.ofDays(12).plusMinutes(-30)    | "P11DT23H30M"
        Duration.ofDays(-12).plusMinutes(-30)    | "-P12DT30M"
        java.time.Period.ofDays(12) | "P12D"
        java.time.Period.ofWeeks(7) | "P7W"
        java.time.Period.ofDays(365) | "P365D"
        java.time.Period.ofDays(364) | "P52W"
        java.time.Period.ofYears(1) | "P52W"
        java.time.Period.ofMonths(6) | "P26W"
        java.time.Period.ofMonths(-6) | "-P26W"
        Duration.ofDays(15).plusHours(5).plusSeconds(20)    | 'P15DT5H0M20S'
    }

    def "verify relaxed string parsing"() {
        setup: 'enable relaxed parsing'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

        expect:
        TemporalAmountAdapter.parse(stringValue).duration == expectedDuration

        where:
        stringValue     | expectedDuration
        "P"             | java.time.Period.ZERO
        "PT"            | java.time.Period.ZERO
        "P90M"          | Duration.of(90, ChronoUnit.MINUTES)
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

    def 'test creation from  date range'() {
        setup: 'Override default timezone for test consistency'
        def originalTimezone = TimeZone.default
        TimeZone.default = TimeZone.getTimeZone('Australia/Melbourne')

        expect:
        TemporalAmountAdapter.fromDateRange(new DateTime(start), new DateTime(end)).duration == expectedTemporalAmount

        cleanup:
        TimeZone.default = originalTimezone

        where:
        start   | end   | expectedTemporalAmount
        '20200107T000000'   | '20200331T000000' | java.time.Period.ofWeeks(12)
    }

    @Unroll
    def 'validate string representation: #dur'() {
        expect: 'derived string representation equals expected'
        TemporalAmountAdapter.from(dur).toString() == expectedString

        where:
        dur						| expectedString
        new Dur(33)				| 'P33W'
        new Dur('-P2D')			| '-P2D'
        new Dur(-2, 0, 0, 0)	| '-P2D'
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
        TemporalAmountAdapter.from(-(new Dur('P1D'))).duration == TemporalAmountAdapter.from(new Dur('-P1D')).duration
    }

    def 'test hashcode equality'() {
        given: 'a temporal amount adapter'
        TemporalAmountAdapter adapter1 = TemporalAmountAdapter.parse('P1D')

        and: 'a second identical period'
        TemporalAmountAdapter adapter2 = TemporalAmountAdapter.parse('P1D')

        expect: 'object equality'
        adapter1 == adapter2

        and: 'hashcode equality'
        adapter1.hashCode() == adapter2.hashCode()
    }

    @Ignore
    def 'week period parsing and values'() {
        given: 'a one week amount adapter'
        TemporalAmountAdapter adapter1 = TemporalAmountAdapter.parse('P1W')

        and: 'a negative one week identical period'
        TemporalAmountAdapter adapter2 = TemporalAmountAdapter.parse('-P1W')
	    
        expect: 'same duration, except for the sign'
        adapter1.duration == -adapter2.duration
    }

    def 'testTemporalAmountAdapter_durationToString_DropsMinutes'() {
        expect: "P1DT1H4M" == TemporalAmountAdapter.parse("P1DT1H4M") as String
    }

    @Ignore
    def 'testTemporalAmountAdapter_Months'() {
        // https://github.com/ical4j/ical4j/issues/419
        // A month usually doesn't have 4 weeks = 4*7 days = 28 days (except February in non-leap years).
        expect: "P4W" != new TemporalAmountAdapter(java.time.Period.ofMonths(1)) as String
    }

    @Ignore
    def 'testTemporalAmountAdapter_Year'() {
        // https://github.com/ical4j/ical4j/issues/419
        // A year has 365 or 366 days, but never 52 weeks = 52*7 days = 364 days.
        expect: "P52W" != new TemporalAmountAdapter(java.time.Period.ofYears(1)) as String
    }
}
