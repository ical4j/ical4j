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

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import spock.lang.Specification;

class TriggerSpec extends Specification {

	def 'verify trigger value is expected'() {
		expect: 'derived value is expected'
		new Trigger(value).value == expectedValue
		
		where:
		value							| expectedValue
		new Dur(0)						| 'PT0S'
		new DateTime('20110131T012647Z')| '20110131T012647Z'
	}

	def 'verify trigger date-time converts to UTC'() {
		setup: 'override platform default timezone'
		def originalPlatformTz = TimeZone.default
		TimeZone.default = TimeZone.getTimeZone('Australia/Melbourne')
		
		expect: 'derived value is expected'
		new Trigger(new DateTime(value)).value == expectedValue
		
		cleanup: 'restore platform default timezone'
		TimeZone.default = originalPlatformTz

		where:
		value				| expectedValue
		'20110131T012647'	| '20110130T142647Z'
	}
	
	def 'verify original date-time is not modified'() {
		setup:
		DateTime dateTime = []
		
		expect:
		Trigger trigger = [dateTime]
		assert !dateTime.utc
	}
}
