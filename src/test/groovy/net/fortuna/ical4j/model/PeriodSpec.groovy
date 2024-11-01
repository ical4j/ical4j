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

import spock.lang.Specification

import java.time.ZonedDateTime

class PeriodSpec extends Specification {

	static Period<ZonedDateTime> year1994, monthMarch ,  monthApril, monthMay, firstHalf, lastHalf, winter, spring,
			marchToMay , marchToApril, aprToMay, janToMay, junToDec

	def setupSpec() {
		ZonedDateTime past = ZonedDateTime.now().withYear(1980).withMonth(1).withDayOfMonth(23)
		ZonedDateTime future = past.withYear(2022).withMonth(2)
		ZonedDateTime begin1994 = future.withYear(1994).withMonth(1).withDayOfMonth(1)
		ZonedDateTime end1994 = begin1994.withMonth(12).withDayOfMonth(31)
		ZonedDateTime mar1994 = end1994.withMonth(3).withDayOfMonth(4)
		ZonedDateTime apr1994 = mar1994.withMonth(4).withDayOfMonth(12)
		ZonedDateTime may1994 = apr1994.withMonth(5).withDayOfMonth(19)
		ZonedDateTime jun1994 = may1994.withMonth(6).withDayOfMonth(22)
		ZonedDateTime jul1994 = jun1994.withMonth(7).withDayOfMonth(29)

		year1994 = new Period<>(begin1994, end1994)
		monthMarch = new Period<>(mar1994, apr1994)
		monthApril = new Period<>(apr1994, may1994)
		monthMay = new Period<>(may1994, jun1994)
		firstHalf = new Period<>(begin1994, jun1994)
		lastHalf = new Period<>(may1994, end1994)
		winter = new Period<>(begin1994, apr1994)
		spring = new Period<>(apr1994, jul1994)
		marchToMay = new Period<>(mar1994, jun1994)
		marchToApril = new Period<>(mar1994, may1994)
		aprToMay = new Period<>(apr1994, jun1994)
		janToMay = new Period<>(begin1994, may1994)
		junToDec = new Period<>(jun1994, end1994)
	}
	
	def 'extension module test: plus'() {
		expect:
        Period.parse('20110412T120000/P1D') + Period.parse('20110413T120000/P1D') == Period.parse('20110412T120000/20110414T120000')
	}
	
	def 'extension module test: minus'() {
		expect:
        Period.parse('20110412T120000/P1D') - Period.parse('20110412T130000/PT1H') == PeriodList.parse('20110412T120000/PT1H,20110412T140000/PT22H')
	}

	def 'test hashcode equality'() {
		given: 'a period'
		Period period1 = Period.parse '20140803T120100/P1D'

		and: 'a second identical period'
		Period period2 = Period.parse '20140803T120100/P1D'

		expect: 'object equality'
		period1 == period2

		and: 'hashcode equality'
		period1.hashCode() == period2.hashCode()
	}

	def 'test includes'() {
		expect: 'result of period inclusion test is as expected'
		period1.includes(date) == expectedIncludes

		where:
		period1	| date              | expectedIncludes
		year1994	| year1994.start | true
		year1994	| year1994.end | true
		year1994	| monthMarch.start | true
		year1994	| monthMarch.start.withYear(1980) | false
		year1994	| monthMarch.start.withYear(2047) | false
	}

	def 'test intersection'() {
		expect: 'result of period intersection test is as expected'
		period1.toInterval().overlaps(period2.toInterval()) == expectedIntersection

		where:
		period1	| period2              | expectedIntersection
		monthMarch	| monthMay | false
		monthMay	| monthMarch | false
		monthMarch	| monthApril | false
		monthApril	| monthMarch | false
		firstHalf	| lastHalf | true
		lastHalf	| firstHalf | true
		winter	| monthMarch | true
		monthMarch	| winter | true
	}

	def 'test contains'() {
		expect: 'result of period containment test is as expected'
		period1.toInterval().encloses(period2.toInterval()) == expectedContains

		where:
		period1	| period2              | expectedContains
		monthMarch	| monthMay | false
		monthMay	| monthMarch | false
		monthMarch	| monthApril | false
		monthApril	| monthMarch | false
		firstHalf	| lastHalf | false
		lastHalf	| firstHalf | false
		winter	| monthMarch | true
		monthMarch	| winter | false
	}

	def 'test addition'() {
		expect: 'result of period addition test is as expected'
		period1.add(period2) == expectedResult

		where:
		period1	| period2              | expectedResult
		monthMarch	| monthMay | marchToMay
		monthMay	| monthMarch | marchToMay
		monthMarch	| monthApril | marchToApril
		monthApril	| monthMarch | marchToApril
		firstHalf	| lastHalf | year1994
		lastHalf	| firstHalf | year1994
		winter	| monthMarch | winter
		monthMarch	| winter | winter
	}

	def 'test subtraction'() {
		expect: 'result of period subtraction test is as expected'
		period1.subtract(period2) == new PeriodList(expectedResult)

		where:
		period1	| period2              | expectedResult
		marchToMay	| marchToApril | [monthMay]
		monthMay	| monthMarch | [monthMay]
		monthMarch	| monthApril | [monthMarch]
		monthApril	| monthMarch | [monthApril]
		firstHalf	| lastHalf | [janToMay]
		lastHalf	| firstHalf | [junToDec]
//		winter	| monthMarch | [winter]
//		monthMarch	| winter | [winter]
	}

	def 'test before'() {
		expect: 'result of period before test is as expected'
		period1.toInterval().isBefore(period2.toInterval()) == expectedBefore

		where:
		period1	| period2              | expectedBefore
		monthMarch	| monthMay | true
		monthMay	| monthMarch | false
		monthMarch	| monthApril | true
		monthApril	| monthMarch | false
		firstHalf	| lastHalf | false
		lastHalf	| firstHalf | false
		winter	| monthMarch | false
		monthMarch	| winter | false
	}

	def 'test after'() {
		expect: 'result of period after test is as expected'
		period1.toInterval().isAfter(period2.toInterval()) == expectedAfter

		where:
		period1	| period2              | expectedAfter
		monthMarch	| monthMay | false
		monthMay	| monthMarch | true
		monthMarch	| monthApril | false
		monthApril	| monthMarch | true
		firstHalf	| lastHalf | false
		lastHalf	| firstHalf | false
		winter	| monthMarch | false
		monthMarch	| winter | false
	}
}
