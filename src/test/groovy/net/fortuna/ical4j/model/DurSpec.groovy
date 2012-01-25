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

import spock.lang.Specification;

class DurSpec extends Specification {

	def 'validate string representation'() {
		expect: 'derived string representation equals expected'
		dur.toString() == expectedString
		
		where:
		dur						| expectedString
		new Dur(33)				| 'P33W'
		new Dur('-P2D')			| '-P2D'
		new Dur(-2, 0, 0, 0)	| '-P2D'
	}
	
	def 'verify duration plus time operations'() {
		expect: 'derived end time value equals expected'
		new Dur(duration).getTime(new DateTime(start)) == new DateTime(expectedEnd)
		
		where:
		duration	| start				| expectedEnd
		'1D'		| '20110326T110000'	| '20110327T110000'
	}
	
	def 'verify duration plus time operations in different timezones'() {
		setup: 'initialise timezone registry'
		def tzRegistry = TimeZoneRegistryFactory.instance.createRegistry()
		
		expect: 'derived end time value equals expected'
		def tz = tzRegistry.getTimeZone(timezone)
		new Dur(duration).getTime(new DateTime(start, tz)) == new DateTime(expectedEnd, tz)
		
		where:
		duration	| timezone					| start				| expectedEnd
		'1D'		| 'America/Los_Angeles'		| '20110326T110000'	| '20110327T110000'
	}

	def 'verify duration plus time operations in different timezones with overriden platform default'() {
		setup: 'override platform default timezone'
		def originalPlatformTz = TimeZone.default
		TimeZone.default = TimeZone.getTimeZone('Europe/Paris')
		
		and: 'initialise timezone registry'
		def tzRegistry = TimeZoneRegistryFactory.instance.createRegistry()
		
		expect: 'derived end time value equals expected'
		def tz = tzRegistry.getTimeZone(timezone)
		new Dur(duration).getTime(new DateTime(start, tz)) == new DateTime(expectedEnd, tz)
		
		cleanup: 'restore platform default timezone'
		TimeZone.default = originalPlatformTz

		where:
		duration	| timezone					| start				| expectedEnd
		'1D'		| 'America/Los_Angeles'		| '20110326T110000'	| '20110327T110000'
	}
	
	def 'verify duration plus date operations'() {
		expect: 'derived end date value equals expected'
		new Dur(duration).getTime(new Date(start)) == new Date(expectedEnd)
		
		where:
		duration	| start				| expectedEnd
		'1D'		| '20110312'		| '20110313'
		'1D'		| '20110313'		| '20110314'
	}

	def 'verify duration plus date operations with overriden platform default timezone'() {
		setup: 'override platform default timezone'
		def originalPlatformTz = TimeZone.default
		TimeZone.default = TimeZone.getTimeZone('America/New_York')

		expect: 'derived end date value equals expected'
		new Dur(duration).getTime(new Date(start)) == new Date(expectedEnd)
		
		cleanup: 'restore platform default timezone'
		TimeZone.default = originalPlatformTz

		where:
		duration	| start				| expectedEnd
		'1D'		| '20110312'		| '20110313'
		'1D'		| '20110313'		| '20110314'
	}
}
