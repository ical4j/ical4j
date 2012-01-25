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
package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.Period
import net.fortuna.ical4j.model.PeriodList;
import spock.lang.Specification


class RDateSpec extends Specification {
	
	def 'should add date-time value to list'() {
		setup: 'create new date-time'
		DateTime date = new DateTime('20110319T140400')
		
		and: 'add date-time to rdate'
		RDate rdate = new RDate()
		rdate.dates.add(date)
		
		expect: 'rdate list contains date-time'
		rdate.dates == [date]
	}
	
	def 'should throw exception when trying to add period value to default rdate instance'() {
		setup: 'create new period'
		Period period = new Period(new DateTime('20110319T140400'), new DateTime('20110319T180400'))
		
		when: 'add period to rdate'
		RDate rdate = new RDate()
		rdate.periods.add(period)
		
		then: 'exception is thrown'
		thrown(UnsupportedOperationException)
	}
	
	def 'should throw exception when trying to add date value to period rdate instance'() {
		setup: 'create new date-time'
		DateTime date = new DateTime('20110319T140400')
		
		when: 'add date to rdate'
		RDate rdate = new RDate(new PeriodList())
		rdate.dates.add(date)
		
		then: 'exception is thrown'
		thrown(UnsupportedOperationException)
	}
}
