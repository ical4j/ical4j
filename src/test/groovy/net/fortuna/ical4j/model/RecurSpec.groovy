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
		def startDate = new Date(start)
		def endDate = new Date(end)
		def expectedDates = []
		expected.each {
			expectedDates << new DateTime(it)
		}

		expect:
		recur.getDates(startDate, endDate, Value.DATE_TIME) == expectedDates
		
		where:
		rule					| start			| end			| expected
		'FREQ=WEEKLY;BYDAY=MO'	| '20110101'	| '20110201'	| ['20110103T000000', '20110110T000000', '20110117T000000', '20110124T000000', '20110131T000000']
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
		rule			 | year   | start  | end    || expected
		'2,29,30,31'	 | '2011' | '0101' | '0503'	|| ['0102', '0129', '0130', '0131', '0202', '0302', '0329', '0330', '0331', '0402', '0429', '0430', '0502']
        '2,29,30,31'     | '2012' | '0101' | '0503' || ['0102', '0129', '0130', '0131', '0202', '0229', '0302', '0329', '0330', '0331', '0402', '0429', '0430', '0502']
		'-2,-29,-30,-31' | '2011' | '0101' | '0503'	|| ['0101', '0102', '0103', '0130', '0227', '0301', '0302', '0303', '0330', '0401', '0402', '0429', '0501', '0502']
        '-2,-29,-30,-31' | '2012' | '0101' | '0503' || ['0101', '0102', '0103', '0130', '0201', '0228', '0301', '0302', '0303', '0330', '0401', '0402', '0429', '0501', '0502']
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
		'2,365,366'		| '20110101'	| '20131231'	|| ['20110102', '20111231', '20120102', '20121230', '20121231', '20130102']
		'-1,-365,-366'	| '20110101'	| '20131231'	|| ['20110101', '20111231', '20120101', '20120102', '20121231', '20130101']
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
		new Recur(frequency: Recur.WEEKLY) as String == 'FREQ=WEEKLY'
		new Recur(frequency: Recur.MONTHLY, interval: 3) as String == 'FREQ=MONTHLY;INTERVAL=3'
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

	def 'verify handling empty rule parts'() {
		setup: 'parse recurrence rule'
		def recur = new Recur(rule)

		expect:
		recur as String == parsedString

		where:
		rule							| parsedString
		'FREQ=WEEKLY;BYDAY=;INTERVAL=1'	| 'FREQ=WEEKLY;INTERVAL=1'
	}
}
