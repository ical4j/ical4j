/*
 *  Copyright (c) 2023, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.component.VEvent
import spock.lang.Specification

class RecurrenceSupportTest extends Specification {

    def 'test get occurrences for recurring event'() {
        given: 'a recurring event'
        VEvent event = new ContentBuilder().vevent {
            summary('a recurring event')
            dtstart('20230101T010000')
            dtend('20230101T020000')
            rrule('FREQ=WEEKLY;INTERVAL=2;BYDAY=SU')
        }

        expect: 'calculated occurrences match expected'
        event.getOccurrences(Period.parse(period)).collect { it.getRecurrenceId().value} == expectedOccurrences

        where:
        period                  | expectedOccurrences
        '20230101T010000/P3W'   | ['20230101T010000', '20230115T010000']
    }
}
