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


import net.fortuna.ical4j.model.property.LastModified
import net.fortuna.ical4j.util.RandomUidGenerator
import net.fortuna.ical4j.validate.ValidationException
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author fortuna
 *
 */
class ContentBuilderSpec extends Specification {

	@Shared
	ContentBuilder builder
	
	def setupSpec() {
		builder = new ContentBuilder()
	}
	
	def 'build ATTACH property and assert the result'() {
		expect:
		assert builder.attach('test') as String == 'ATTACH:test\r\n'
		assert builder.attach([value: 'test']) as String == 'ATTACH:test\r\n'
	}
	
	def 'build ATTENDEE property and assert the result'() {
		expect:
		assert builder.attendee('test') as String == 'ATTENDEE:test\r\n'
	}
	
	def 'build DTSTART property and assert the result'() {
		expect:
		assert builder.dtstart('20120808T150000') as String == 'DTSTART:20120808T150000\r\n'
	}

	def 'build DTSTART property with tzid parameter and assert the result'() {
		expect:
		assert builder.with {
            dtstart('20150321T193000') {
                tzid_ 'Australia/Lord_Howe'
            }
        } as String == 'DTSTART;TZID=Australia/Lord_Howe:20150321T193000\r\n'

	}

	def 'test build RFC7986 properties and params'() {
		expect:
		builder.image('https://example.com/images/weather-cloudy.png') {
			value 'URI'
			display 'BADGE,THUMBNAIL'
		} as String == 'IMAGE;VALUE=URI;DISPLAY="BADGE,THUMBNAIL":https://example.com/images/weather-cloudy.png\r\n'

		and:
		builder.attendee('mailto:opaque-token-1234@example.com') {
			cn 'Cyrus Daboo'
			email 'cyrus@example.com'
		} as String == 'ATTENDEE;CN=Cyrus Daboo;EMAIL=cyrus@example.com:mailto:opaque-token-1234@example.com\r\n'

		and:
		builder.conference('https://video-chat.example.com/;group-id=1234') {
			value 'URI'
			feature 'AUDIO,VIDEO'
			label 'Web video chat, access code=76543'
		} as String == 'CONFERENCE;VALUE=URI;FEATURE="AUDIO,VIDEO";LABEL="Web video chat, access code=76543":https://video-chat.example.com/;group-id=1234\r\n'

		and:
		builder.color('turquoise') as String == 'COLOR:turquoise\r\n'

		and:
		builder.source('https://example.com/holidays.ics') {
			value 'URI'
		} as String == 'SOURCE;VALUE=URI:https://example.com/holidays.ics\r\n'

		and:
		builder.refreshinterval('P1W') {
			value 'DURATION'
		} as String == 'REFRESH-INTERVAL;VALUE=DURATION:P1W\r\n'

		and:
		builder.name('Company Vacation Days') as String == 'NAME:Company Vacation Days\r\n'
	}

	def 'test build and validate calendar with RFC7986 properties'() {
		given: 'a calendar built with extension properties'
		def calendar = builder.calendar {
			prodid '-//Ben Fortuna//iCal4j 1.0//EN'
			version '2.0'
			uid new RandomUidGenerator().generateUid()
			lastmodified new LastModified(new DateTime())
			url 'https://example.com/calendar.ics'
			refreshinterval('P1W') {
				value 'DURATION'
			}
			source('https://example.com/holidays.ics') {
				value 'URI'
			}
			color 'turquoise'
			name 'Holiday Calendar'
			description 'A collection of holidays'
			categories 'Vacation'
			image('http://example.com/images/party.png') {
				value 'URI'
				display 'BADGE'
				fmttype 'image/png'
			}
			vevent {
				uid '1'
				dtstamp()
				dtstart '20090810', parameters: parameters { value 'DATE' }
				action 'DISPLAY'
				attach'http://example.com/attachment', parameters: parameters { value 'URI' }
			}
		}

		when: 'the calendar is validated'
		calendar.validate()

		then: 'no exception is thrown'
		notThrown(ValidationException)
	}
}
