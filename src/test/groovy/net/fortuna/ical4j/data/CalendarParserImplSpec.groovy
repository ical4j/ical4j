/**
 * Copyright (c) 2011, Ben Fortuna
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
package net.fortuna.ical4j.data

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.Calendars;
import spock.lang.Specification;

class CalendarParserImplSpec extends Specification {
	
	CalendarBuilder builder = new CalendarBuilder()
	
	def 'verify parsing of VEVENT properties'() {
		setup:
		String input = "BEGIN:VCALENDAR\r\nBEGIN:VEVENT\r\n$contentLines\r\nEND:VEVENT\r\nEND:VCALENDAR"
		
		expect:
		Calendar calendar = builder.build(new StringReader(input))
		assert calendar.components[0].properties[0] as String == expectedProperty
		
		where:
		contentLines																								| expectedProperty
		'ATTENDEE;ROLE=CHAIR;PARTSTAT=ACCEPTED;CN="participant";\r\n RSVP=FALSE:mailto:participant@somewhere.com'	| 'ATTENDEE;ROLE=CHAIR;PARTSTAT=ACCEPTED;CN=participant;RSVP=FALSE:mailto:participant@somewhere.com\r\n'
	}
	
	def 'verify parsing of calendar properties'() {
		setup:
		String input = "BEGIN:VCALENDAR\r\n$contentLines\r\nEND:VCALENDAR"
		
		expect:
		Calendar calendar = builder.build(new StringReader(input))
		assert calendar.properties[0] as String == expectedProperty
		
		where:
		contentLines																																			| expectedProperty
//		'PRODID;X-NO-QUOTES=a\nb;X-QUOTES="a\nb":sample'																										| 'PRODID;X-NO-QUOTES=a\\nb;X-QUOTES="a\\nb":sample\r\n'
		'X-APPLE-STRUCTURED-LOCATION;VALUE=URI;X-APPLE-ABUID="ab://Home";X-TITLE=1 Infinite Loop\nCupertino CA 95014\nUnited States:geo:37.331684,-122.030758'	| 'X-APPLE-STRUCTURED-LOCATION;VALUE=URI;X-APPLE-ABUID="ab://Home";X-TITLE=1 Infinite Loop\\nCupertino CA 95014\\nUnited States:geo:37.331684\\,-122.030758\r\n'
	}
}
