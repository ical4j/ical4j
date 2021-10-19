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
package net.fortuna.ical4j.data

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.util.Calendars
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification

import static net.fortuna.ical4j.util.CompatibilityHints.KEY_RELAXED_PARSING
import static net.fortuna.ical4j.util.CompatibilityHints.KEY_RELAXED_UNFOLDING

class CalendarParserImplSpec extends Specification {
	
	def 'verify parsing of VEVENT properties'() {
		setup:
		String input = "BEGIN:VCALENDAR\r\nBEGIN:VEVENT\r\n$contentLines\r\nEND:VEVENT\r\nEND:VCALENDAR"
		
		expect:
		Calendar calendar = new CalendarBuilder().build(new StringReader(input))
		assert calendar.components[0].properties[0] as String == expectedProperty
		
		where:
		contentLines																								| expectedProperty
		'ATTENDEE;ROLE=CHAIR;PARTSTAT=ACCEPTED;CN="participant";\r\n RSVP=FALSE:mailto:participant@somewhere.com'	| 'ATTENDEE;ROLE=CHAIR;PARTSTAT=ACCEPTED;CN=participant;RSVP=FALSE:mailto:participant@somewhere.com\r\n'
	}
	
	def 'verify parsing of calendar properties'() {
		setup:
		String input = "BEGIN:VCALENDAR\r\n$contentLines\r\nEND:VCALENDAR"
		
		expect:
		Calendar calendar = new CalendarBuilder().build(new StringReader(input))
		assert calendar.properties[0] as String == expectedProperty
		
		where:
		contentLines																																			| expectedProperty
//		'PRODID;X-NO-QUOTES=a\nb;X-QUOTES="a\nb":sample'																										| 'PRODID;X-NO-QUOTES=a\\nb;X-QUOTES="a\\nb":sample\r\n'
		'X-APPLE-STRUCTURED-LOCATION;VALUE=URI;X-APPLE-ABUID="ab://Home";X-TITLE=1 Infinite Loop\nCupertino CA 95014\nUnited States:geo:37.331684,-122.030758'	| 'X-APPLE-STRUCTURED-LOCATION;VALUE=URI;X-APPLE-ABUID="ab://Home";X-TITLE=1 Infinite Loop^nCupertino CA 95014^nUnited States:geo:37.331684,-122.030758\r\n'
	}
	
	def 'verify parsing of calendar file'() {
		setup:
		compatibilityHints.each {
			CompatibilityHints.setHintEnabled(it, true)
		}
		
		expect:
		Calendar calendar = Calendars.load(CalendarParserImplSpec.getResource(resource))
		
		cleanup:
		compatibilityHints.each {
			CompatibilityHints.clearHintEnabled(it)
		}

		where:
		resource							| compatibilityHints
		'/samples/valid/bhav23-1.ics'	| []
		'/samples/invalid/bhav23-2.ics'	| [KEY_RELAXED_UNFOLDING, KEY_RELAXED_PARSING]
		'/samples/valid/blankTzid.ics'  | []
	}

	def 'verify parsing empty lines'() {
		setup:
		String input = "BEGIN:VCALENDAR\r\n\r\n$contentLines\r\nEND:VCALENDAR"
		compatibilityHints.each {
			CompatibilityHints.setHintEnabled(it, true)
		}

		expect:
		Calendar calendar = new CalendarBuilder().build(new StringReader(input))
		assert calendar.components[0].properties.size() == 24

		cleanup:
		compatibilityHints.each {
			CompatibilityHints.clearHintEnabled(it)
		}

		where:
		contentLines << ['''BEGIN:VEVENT

CLASS:
CREATED:20121015T070600Z
DTSTART:20121018T020000Z
LAST-MODIFIED:20140815T175058Z
LOCATION:somewhere
ORGANIZER;CN=somesystem:mailto:foo@bar.com
PRIORITY:5
DTSTAMP:20121015T070600Z
SEQUENCE:1
STATUS:CONFIRMED
SUMMARY:Scrum Gathering
\tTest Automation
TRANSP:TRANSPARENT
UID:a5f13918-e1a8-4035-ac92-44a412315b00
DTEND:20121018T040000Z
ATTENDEE;CN=foo@bar.com;PARTSTAT=NEEDS-ACTION;ROLE=REQ-PARTI
\tCIPANT;RSVP=TRUE:mailto:foo@bar.com;
X-ALT-DESC;FMTTYPE=text/html:<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//E
\tN">\\n<HTML>\\n<HEAD>\\n<META NAME="Generator" CONTENT="MS Exchange Server "v
\tersion 08.00.0681.000">\\n<TITLE></TITLE>\\n</HEAD>\\n<BODY>\\n<!-- Converted
\tfrom text/rtf format -->\\n\\n<P DIR=LTR ALIGN=JUSTIFY><SPAN LANG="en-us"><F
\tONT FACE="Times New Roman"><p><img border="0" alt="" src="http://example.com" />
:
:
:
X-MICROSOFT-CDO-BUSYSTATUS:FREE
X-MICROSOFT-CDO-INSTTYPE:0
X-MICROSOFT-CDO-INTENDEDSTATUS:BUSY
X-MICROSOFT-CDO-ALLDAYEVENT:FALSE
X-MICROSOFT-CDO-IMPORTANCE:1
X-MS-OLK-CONFTYPE:0
X-MICROSOFT-CDO-ATTENDEE-CRITICAL-CHANGE:20121015T070600Z
X-MICROSOFT-CDO-OWNER-CRITICAL-CHANGE:20121015T070600Z
BEGIN:VALARM

ACTION:DISPLAY
TRIGGER;VALUE=DURATION:-PT15M
DESCRIPTION:REMINDER
RELATED=START:-PT00H15M00S
END:VALARM

END:VEVENT
''']
		compatibilityHints << [[KEY_RELAXED_UNFOLDING, KEY_RELAXED_PARSING]]
	}
}
