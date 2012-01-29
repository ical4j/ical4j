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

import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Configurator;
import spock.lang.IgnoreRest;
import spock.lang.Specification;

class RecurSpec extends Specification {

	def setupSpec() {
		System.setProperty 'net.fortuna.ical4j.timezone.date.floating', 'true'
	}
	
	def cleanupSpec() {
		System.clearProperty 'net.fortuna.ical4j.timezone.date.floating'
	}
	
	def 'verify recurrence rules for date-time'() {
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
	
	def 'verify recurrence rules in different locales'() {
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

	def 'verify recurrence rules with a specified interval'() {
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
	
	def 'verify recurrence rules with a specified WKST'() {
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

	def 'verify recurrence rules in different locales with a specified interval'() {
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
}
