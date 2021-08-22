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
package net.fortuna.ical4j.model.component

import net.fortuna.ical4j.model.ContentBuilder
import spock.lang.Specification

class VEventSpec extends Specification {
	
	ContentBuilder builder = new ContentBuilder()

	def 'build event with location'() {
		setup:
		def calendar = builder.calendar {
			prodid('-//Ben Fortuna//iCal4j 1.0//EN')
			version('2.0')
			vevent {
				uid('1')
				dtstamp()
				dtstart('20110919T150000Z')
				dtend('20110921T100000Z')
				vlocation {
					uid '1'
				}
			}
		}

		expect:
		calendar.components[0].components.size() == 1
	}

	def 'build event with location and resource'() {
		setup:
		def calendar = builder.calendar {
			prodid('-//Ben Fortuna//iCal4j 1.0//EN')
			version('2.0')
			vevent {
				uid('1')
				dtstamp()
				dtstart('20110919T150000Z')
				dtend('20110921T100000Z')
				vlocation {
					uid '1-1'
				}
				vresource {
					uid '1-2'
				}
			}
		}

		expect:
		calendar.components[0].components.size() == 2
	}
}
