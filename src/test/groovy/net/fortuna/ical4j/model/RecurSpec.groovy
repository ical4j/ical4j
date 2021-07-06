/**
 * Copyright (c) 2012, Ben Fortuna
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
package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.parameter.Value
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification
import spock.lang.Unroll

import static net.fortuna.ical4j.model.WeekDay.*

class RecurSpec extends Specification {

    def setupSpec() {
        System.setProperty 'net.fortuna.ical4j.timezone.date.floating', 'true'
    }

    def cleanupSpec() {
        System.clearProperty 'net.fortuna.ical4j.timezone.date.floating'
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)
    }

    @Unroll
    def 'verify recurrence rule: #rule'() {
        setup: 'parse recurrence rule'
        def recur = new Recur(rule)
        def startDate
        def endDate
        def expectedDates = []
        if (valueType == Value.DATE) {
            startDate = new Date(start)
            endDate = new Date(end)
            expected.each {
                expectedDates << new Date(it)
            }
        } else {
            startDate = new DateTime(start)
            endDate = new DateTime(end)
            expected.each {
                expectedDates << new DateTime(it)
            }
        }

        expect:
        recur.getDates(startDate, endDate, valueType) == expectedDates

        where:
        rule					| valueType | start			| end			| expected
        'FREQ=WEEKLY;BYDAY=MO'	| Value.DATE_TIME	| '20110101T000000'	| '20110201T000000'	| ['20110103T000000', '20110110T000000', '20110117T000000', '20110124T000000', '20110131T000000']
        'FREQ=DAILY;INTERVAL=14;WKST=MO;BYMONTH=10,12'	| Value.DATE | '20181011'	| '20181231'	| ['20181011', '20181025', '20181206', '20181220']
        'FREQ=WEEKLY;BYDAY=MO,TH,FR,SA,SU;BYHOUR=11;BYMINUTE=5'	| Value.DATE_TIME	| '20160325T110500'	| '20160329T121000'	| ['20160325T110500', '20160326T110500', '20160327T110500', '20160328T110500']
        'FREQ=WEEKLY;INTERVAL=1;BYDAY=FR;WKST=MO;UNTIL=20170127T003000Z'	| Value.DATE_TIME	| '20160727T0030000Z'	| '20170127T003000Z'	| ['20160729T003000Z',
                                                                                                                                                     '20160805T003000Z',
                                                                                                                                                     '20160812T003000Z',
                                                                                                                                                     '20160819T003000Z',
                                                                                                                                                     '20160826T003000Z',
                                                                                                                                                     '20160902T003000Z',
                                                                                                                                                     '20160909T003000Z',
                                                                                                                                                     '20160916T003000Z',
                                                                                                                                                     '20160923T003000Z',
                                                                                                                                                     '20160930T003000Z',
                                                                                                                                                     '20161007T003000Z',
                                                                                                                                                     '20161014T003000Z',
                                                                                                                                                     '20161021T003000Z',
                                                                                                                                                     '20161028T003000Z',
                                                                                                                                                     '20161104T003000Z',
                                                                                                                                                     '20161111T003000Z',
                                                                                                                                                     '20161118T003000Z',
                                                                                                                                                     '20161125T003000Z',
                                                                                                                                                     '20161202T003000Z',
                                                                                                                                                     '20161209T003000Z',
                                                                                                                                                     '20161216T003000Z',
                                                                                                                                                     '20161223T003000Z',
                                                                                                                                                     '20161230T003000Z',
                                                                                                                                                     '20170106T003000Z',
                                                                                                                                                     '20170113T003000Z',
                                                                                                                                                     '20170120T003000Z',
                                                                                                                                                     '20170127T003000Z']
        'FREQ=WEEKLY;WKST=MO;BYDAY=SU;BYHOUR=0;BYMINUTE=0'	| Value.DATE_TIME	| '20181020T000000'	| '20181120T000000'	| ['20181021T000000',
                                                                                                                                    '20181028T000000',
                                                                                                                                    '20181104T000000',
                                                                                                                                    '20181111T000000',
                                                                                                                                    '20181118T000000']
        'FREQ=DAILY;BYMONTH=1'	| Value.DATE	| '20000101'	| '20000201' | ['20000101',
                                                                                  '20000102',
                                                                                  '20000103',
                                                                                  '20000104',
                                                                                  '20000105',
                                                                                  '20000106',
                                                                                  '20000107',
                                                                                  '20000108',
                                                                                  '20000109',
                                                                                  '20000110',
                                                                                  '20000111',
                                                                                  '20000112',
                                                                                  '20000113',
                                                                                  '20000114',
                                                                                  '20000115',
                                                                                  '20000116',
                                                                                  '20000117',
                                                                                  '20000118',
                                                                                  '20000119',
                                                                                  '20000120',
                                                                                  '20000121',
                                                                                  '20000122',
                                                                                  '20000123',
                                                                                  '20000124',
                                                                                  '20000125',
                                                                                  '20000126',
                                                                                  '20000127',
                                                                                  '20000128',
                                                                                  '20000129',
                                                                                  '20000130',
                                                                                  '20000131']
        'FREQ=MONTHLY;WKST=MO;INTERVAL=1;BYMONTH=2,3,9,10;BYMONTHDAY=28,29,30,31;BYSETPOS=-1'	| Value.DATE	| '20150101'	| '20170101'	| ['20150228',
                                                                                                                                                    '20150331',
                                                                                                                                                    '20150930',
                                                                                                                                                    '20151031',
                                                                                                                                                    '20160229',
                                                                                                                                                    '20160331',
                                                                                                                                                    '20160930',
                                                                                                                                                    '20161031']
//		'FREQ=WEEKLY;UNTIL=20190225;INTERVAL=2;BYDAY=MO'	| Value.DATE	| '20181216'	| '20190225'	| ['20181217', '20181231', '20190114', '20190128', '20190211', '20190225']
//        'FREQ=DAILY;UNTIL=20130906' | Value.DATE_TIME    | '20130831T170001'    | '20200110T133320'    | []
        'FREQ=DAILY;UNTIL=20130906' | Value.DATE    | '20130831'    | '20200110'    | ['20130831', '20130901', '20130902', '20130903', '20130904', '20130905', '20130906']
    }

    @Unroll
    def 'verify byweekno recurrence rules without byday: #rule wkst: #wkst'() {
        setup: 'parse recurrence rule'
        def recur = new Recur("FREQ=YEARLY;BYWEEKNO=$rule;WKST=$wkst")
        def startDate = new Date(start)
        def endDate = new Date(end)
        def expectedDates = []
        expected.each {
            expectedDates << new Date(it)
        }

        expect:
        recur.getDates(startDate, endDate, Value.DATE) == expectedDates

        where:
        rule          | wkst | start      | end        || expected
        '2,52,53'     | 'MO' | '20110101' | '20131231' || ['20110115', '20111231', '20120114', '20121229', '20130112', '20131228']
        // If WKST is Wed, then we'll have 53 weeks in 2011.
        '2,52,53'     | 'WE' | '20110101' | '20131231' || ['20110108', '20111224', '20111231', '20120114', '20121229', '20130112', '20131228']
        '-2,-52,-53'  | 'MO' | '20110101' | '20131231' || ['20110108', '20111224', '20120107', '20121222', '20130105', '20131221']
        '-2,-52,-53'  | 'WE' | '20110101' | '20131231' || ['20110101', '20110108', '20111224', '20120107', '20121222', '20130105', '20131221']
    }

    @Unroll
    def 'verify byweekno recurrence rules: #rule wkst: #wkst byday: #byday'() {
        setup: 'parse recurrence rule'
        def recur = new Recur("FREQ=YEARLY;BYWEEKNO=$rule;WKST=$wkst;BYDAY=$byday")
        def startDate = new Date(start)
        def endDate = new Date(end)
        def expectedDates = []
        expected.each {
            expectedDates << new Date(it)
        }

        expect:
        recur.getDates(startDate, endDate, Value.DATE) == expectedDates

        where:
        rule          | wkst | byday   | start      | end        || expected
        '2,52,53'     | 'MO' | 'MO,TH' | '20110101' | '20131231' || ['20110110', '20110113', '20111226', '20111229', '20120109', '20120112', '20121224', '20121227', '20130107', '20130110', '20131223', '20131226']
        // If WKST is Wed, then we'll have 53 weeks in 2011.
        '2,52,53'     | 'WE' | 'MO,TH' | '20110101' | '20131231' || ['20110106', '20110110', '20111222', '20111226', '20111229', '20120102', '20120112', '20120116', '20121227', '20121231', '20130110', '20130114', '20131226', '20131230']
    }

    @Unroll
    def 'verify monthly bymonthday recurrence rules: #rule #year'() {
        setup: 'parse recurrence rule'
        def recur = new Recur("FREQ=MONTHLY;BYMONTHDAY=$rule")
        def startDate = new Date("${year}${start}")
        def endDate = new Date("${year}${end}")
        def expectedDates = []
        expected.each {
            expectedDates << new Date("${year}${it}")
        }

        expect:
        recur.getDates(startDate, endDate, Value.DATE) == expectedDates

        where:
        rule			          | year   | start  | end    || expected
        '2,29,30,31'	          | '2011' | '0101' | '0503' || ['0102', '0129', '0130', '0131', '0202', '0302', '0329', '0330', '0331', '0402', '0429', '0430', '0502']
        '2,29,30,31'              | '2012' | '0101' | '0503' || ['0102', '0129', '0130', '0131', '0202', '0229', '0302', '0329', '0330', '0331', '0402', '0429', '0430', '0502']
        '-2,-29,-30,-31'          | '2011' | '0101' | '0503' || ['0101', '0102', '0103', '0130', '0227', '0301', '0302', '0303', '0330', '0401', '0402', '0429', '0501', '0502', '0503']
        '-2,-29,-30,-31'          | '2012' | '0101' | '0503' || ['0101', '0102', '0103', '0130', '0201', '0228', '0301', '0302', '0303', '0330', '0401', '0402', '0429', '0501', '0502', '0503']
        '-1'                      | '2020' | '0131' | '0531' || ['0131', '0229', '0331', '0430', '0531']
        '-1'                      | '2019' | '0131' | '0531' || ['0131', '0228', '0331', '0430', '0531']
        '28,29,30,31;BYSETPOS=-1' | '2020' | '0131' | '0531' || ['0131', '0229', '0331', '0430', '0531']
        '28,29,30,31;BYSETPOS=-1' | '2019' | '0131' | '0531' || ['0131', '0228', '0331', '0430', '0531']
        '31'                      | '2020' | '0131' | '0531' || ['0131', '0331', '0531']
    }

    @Unroll
    def 'verify yearly bymonthday recurrence rules: #rule #start'() {
        setup: 'parse recurrence rule'
        def recur = new Recur("FREQ=YEARLY;BYMONTHDAY=$rule")
        def startDate = new Date(start)
        def endDate = new Date(end)
        def expectedDates = []
        expected.each {
            expectedDates << new Date(it)
        }

        expect:
        recur.getDates(startDate, endDate, Value.DATE) == expectedDates

        where:
        rule       | start      | end        || expected
        '2,-1'     | '20110101' | '20121231' || ['20110102', '20110131', '20120102', '20120131']
        '2,-1'     | '20110201' | '20121231' || ['20110202', '20110228', '20120202', '20120229']
    }

    @Unroll
    def 'verify byyearday recurrence rules: #rule'() {
        setup: 'parse recurrence rule'
        def recur = new Recur("FREQ=YEARLY;BYYEARDAY=$rule")
        def startDate = new Date(start)
        def endDate = new Date(end)
        def expectedDates = []
        expected.each {
            expectedDates << new Date(it)
        }

        expect:
        recur.getDates(startDate, endDate, Value.DATE) == expectedDates

        where:
        rule			| start			| end			|| expected
        '2,365,366'		| '20110101'	| '20131231'	|| ['20110102', '20111231', '20120102', '20121230', '20121231', '20130102', '20131231']
        '-1,-365,-366'	| '20110101'	| '20131231'	|| ['20110101', '20111231', '20120101', '20120102', '20121231', '20130101', '20131231']
        '2,32'			| '20110101'	| '20131231'	|| ['20110102', '20110201', '20120102', '20120201', '20130102', '20130201']
    }

    @Unroll
    def 'verify recurrence rule in different locales: #rule'() {
        setup: 'override platform default locale'
        def originalLocale = Locale.default
        Locale.default = Locale.FRANCE

        and: 'parse recurrence rule'
        def recur = new Recur(rule)
        def startDate = new Date(start)
        def endDate = new Date(end)
        def expectedDates = []
        expected.each {
            expectedDates << new DateTime(it)
        }

        expect:
        recur.getDates(startDate, endDate, Value.DATE_TIME) == expectedDates

        cleanup:
        Locale.default = originalLocale

        where:
        rule					| start			| end			| expected
        'FREQ=WEEKLY;BYDAY=MO'	| '20110101'	| '20110201'	| ['20110103T000000', '20110110T000000', '20110117T000000', '20110124T000000', '20110131T000000']
    }

    @Unroll
    def 'verify recurrence rule in different system timezones: #systemTimezone'() {
        setup: 'override platform default timezone'
        def originalTimezone = java.util.TimeZone.getDefault()
        java.util.TimeZone.setDefault(TimeZone.getTimeZone(systemTimezone))

        and: 'parse recurrence rule'
        def recur = new Recur(rule)
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry()
        def berlin = registry.getTimeZone("Europe/Berlin")
        def startDate = new DateTime(start, berlin)
        def endDate = new DateTime(end, berlin)
        def expectedDates = []
        expected.each {
            expectedDates << new DateTime(it, berlin)
        }

        expect:
        recur.getDates(startDate, endDate, Value.DATE_TIME) == expectedDates

        cleanup:
        java.util.TimeZone.setDefault(originalTimezone)

        where:
        systemTimezone			| rule															| start				| end				| expected
        'Europe/Berlin'			| 'FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR,SA,SU;BYHOUR=0;BYMINUTE=5'	| '20161029T000000'	| '20161102T000000'	| ['20161029T000500', '20161030T000500', '20161031T000500', '20161101T000500']
        'America/Phoenix'		| 'FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR,SA,SU;BYHOUR=0;BYMINUTE=5'	| '20161029T000000'	| '20161102T000000'	| ['20161029T000500', '20161030T000500', '20161031T000500', '20161101T000500']
        'America/St_Johns'		| 'FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR,SA,SU;BYHOUR=0;BYMINUTE=5'	| '20161029T000000'	| '20161102T000000'	| ['20161029T000500', '20161030T000500', '20161031T000500', '20161101T000500']
        'Africa/Johannesburg'	| 'FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR,SA,SU;BYHOUR=0;BYMINUTE=5'	| '20161029T000000'	| '20161102T000000'	| ['20161029T000500', '20161030T000500', '20161031T000500', '20161101T000500']
    }

    @Unroll
    def 'verify recurrence rule with a specified interval: #rule'() {
        setup: 'parse recurrence rule'
        def recur = new Recur(rule)
        def startDate = new Date(start)
        def endDate = new Date(end)
        def expectedDates = []
        expected.each {
            expectedDates << new DateTime(it)
        }

        expect:
        recur.getDates(startDate, endDate, Value.DATE_TIME) == expectedDates

        where:
        rule								| start			| end			| expected
        'FREQ=WEEKLY;INTERVAL=2;BYDAY=SU'	| '20110101'	| '20110201'	| ['20110102T000000', '20110116T000000', '20110130T000000']
    }

    @Unroll
    def 'verify recurrence rule with a specified WKST: #rule'() {
//		setup: 'configure floating date timezone'
//		System.setProperty('net.fortuna.ical4j.timezone.date.floating', 'true')

        setup: 'parse recurrence rule'
        def recur = new Recur(rule)
        def startDate = new Date(start)
        def endDate = new Date(end)
        def expectedDates = []
        expected.each {
            expectedDates << new DateTime(it)
        }

        expect:
        recur.getDates(startDate, endDate, Value.DATE_TIME) == expectedDates

        where:
        rule										| start			| end			| expected
        'FREQ=WEEKLY;INTERVAL=2;BYDAY=SU;WKST=SU'	| '20110101'	| '20110201'	| ['20110109T000000', '20110123T000000']
        'FREQ=WEEKLY;INTERVAL=2;BYDAY=SU,MO;WKST=MO'			| '20110306'	| '20110313'	| ['20110306T000000']
        'FREQ=WEEKLY;INTERVAL=2;BYDAY=SU,MO;WKST=SU'			| '20110306'	| '20110313'	| ['20110306T000000', '20110307T000000']
    }

    @Unroll
    def 'verify recurrence rule in different locales with a specified interval: #rule'() {
        setup: 'override platform default locale'
        def originalLocale = Locale.default
        Locale.default = Locale.FRANCE

        and: 'parse recurrence rule'
        def recur = new Recur(rule)
        def startDate = new Date(start)
        def endDate = new Date(end)
        def expectedDates = []
        expected.each {
            expectedDates << new DateTime(it)
        }

        expect:
        recur.getDates(startDate, endDate, Value.DATE_TIME) == expectedDates

        cleanup:
        Locale.default = originalLocale

        where:
        rule								| start			| end			| expected
        'FREQ=WEEKLY;INTERVAL=2;BYDAY=SU'	| '20110101'	| '20110201'	| ['20110102T000000', '20110116T000000', '20110130T000000']
    }

    def 'verify no-args constructor has no side-effects'() {
        expect:
        new Recur(frequency: Recur.Frequency.WEEKLY) as String == 'FREQ=WEEKLY'
        new Recur(frequency: Recur.Frequency.MONTHLY, interval: 3) as String == 'FREQ=MONTHLY;INTERVAL=3'
    }

    def 'verify behaviour when parsing unexpected rule parts'() {
        when:
        new Recur('X-BYMILLISECOND=300')

        then:
        thrown(IllegalArgumentException)
    }

    def 'verify relaxed behaviour when parsing unexpected rule parts'() {
        setup:
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

        when:
        Recur recur = ['FREQ=WEEKLY;X-BYMILLISECOND=300']

        then:
        recur.experimentalValues['X-BYMILLISECOND'] == '300'
    }

    def 'verify recur value string parsing'() {
        setup: 'parse recurrence rule'
        Recur recur = [rule]

        expect:
        recur as String == parsedString

        where:
        rule							                                    | parsedString
        'FREQ=WEEKLY;BYDAY=;INTERVAL=1'	                                    | 'FREQ=WEEKLY;INTERVAL=1'
        'RSCALE=CHINESE;FREQ=YEARLY'	                                    | 'RSCALE=CHINESE;FREQ=YEARLY'
        'RSCALE=ETHIOPIC;FREQ=MONTHLY;BYMONTH=13'	                        | 'RSCALE=ETHIOPIC;FREQ=MONTHLY;BYMONTH=13'
        'RSCALE=HEBREW;FREQ=YEARLY;BYMONTH=5L;BYMONTHDAY=8;SKIP=FORWARD'	| 'RSCALE=HEBREW;FREQ=YEARLY;BYMONTH=5L;BYMONTHDAY=8;SKIP=FORWARD'
        'RSCALE=GREGORIAN;FREQ=YEARLY;SKIP=FORWARD'	                        | 'RSCALE=GREGORIAN;FREQ=YEARLY;SKIP=FORWARD'
    }

    def 'test recur rule builder'() {
        given: 'a rule builder'
        Recur.Builder builder = []

        when: 'populated'
        Date until = new Date('20050307');

        Recur recurDaily = builder.frequency(Recur.Frequency.DAILY).until(until)
                .dayList(new WeekDayList(MO, TU, WE, TH, FR))
                .interval(1).weekStartDay(MO.getDay()).build();

        then: 'result is as expected'
        recurDaily as String == "FREQ=DAILY;WKST=MO;UNTIL=20050307;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR"
    }

    def 'test Recur.getNextDate() with different recurrence rules'() {
        given: 'a recurrence rule'
        Recur recur = [rule]

        expect: 'recur.getNextDate() returns the expected value'
        recur.getNextDate(seed, start) == expectedDate

        where:
        rule	| seed	| start	| expectedDate
        'FREQ=MONTHLY;COUNT=100;INTERVAL=1'	| new DateTime('20180329T025959')	| new DateTime('20170729T030000')	| new DateTime('20180329T025959')
    }
}
