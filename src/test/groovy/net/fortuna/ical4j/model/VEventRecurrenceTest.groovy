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
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.VEvent;
import groovy.util.GroovyTestCase;

class VEventRecurrenceTest extends GroovyTestCase {

	void testCalculateRecurrenceSet() {
		VEvent event = new ContentBuilder().vevent {
			dtstart('20101113', parameters: parameters() {
				value('DATE')})
			dtend('20101114', parameters: parameters() {
				value('DATE')})
			rrule('FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA')
		}
		
		def dates = event.calculateRecurrenceSet(new Period('20101101T000000/20110101T000000'))
		
		def expected = new PeriodList(true)
		expected.add new Period('20101113T000000Z/P1D')
		expected.add new Period('20101129T000000Z/P1D')
		expected.add new Period('20101130T000000Z/P1D')
		expected.add new Period('20101204T000000Z/P1D')
		expected.add new Period('20101220T000000Z/P1D')
		expected.add new Period('20101221T000000Z/P1D')
		expected.add new Period('20101225T000000Z/P1D')
		
		println dates
		assert dates == expected
	}
}
