/*
 *  Copyright (c) 2026, Ben Fortuna
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

package net.fortuna.ical4j.validate

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.property.DtStamp
import net.fortuna.ical4j.model.property.Uid
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification

class ComponentValidatorTest extends Specification {

    ContentBuilder builder = new ContentBuilder()

    def setup() {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, false)
    }

    def cleanup() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
    }

    def 'a duplicated required property is reported once, not twice (#692)'() {
        given: 'a VEVENT with two DTSTAMP properties'
        def event = builder.vevent {
            uid '1'
            dtstamp()
            dtstart '20240101T100000Z'
            summary 'Duplicate DTSTAMP'
        }
        event.add(new DtStamp())

        when: 'the component is validated'
        def result = event.validate()

        then: 'exactly one entry mentions DTSTAMP'
        result.hasErrors()
        result.entries.count { it.message.contains('DTSTAMP') } == 1
    }

    def 'duplicate UID is reported once for #component (#692)'() {
        given: 'a component with two UID properties'
        def c = builder."$component" {
            uid '1'
            dtstamp()
            dtstart '20240101T100000Z'
            dtend '20240101T110000Z'
        }
        c.add(new Uid('2'))

        when: 'the component is validated'
        def result = c.validate()

        then: 'exactly one entry mentions UID'
        result.entries.count { it.message.contains('UID') } == 1

        where:
        component << ['vevent', 'vfreebusy', 'vavailability']
    }
}
