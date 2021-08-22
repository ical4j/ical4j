/**
 * Copyright (c) 2004-2021, Ben Fortuna
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
package net.fortuna.ical4j.filter.predicate

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.Organizer
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by fortuna on 24/07/2017.
 */
class PropertyEqualToRuleTest extends Specification {

    @Shared
    ContentBuilder builder

    @Shared
    Organizer organiser

    @Shared
    Attendee attendee

    def setupSpec() {
        builder = new ContentBuilder()
        organiser = builder.organizer('Mailto:B@example.com')
        attendee = builder.attendee('Mailto:A@example.com')
    }

    def "Evaluate"() {
        given: 'a component with two properties'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'a property rule matches when applied'
        rule.test(event)

        where:
        rule << [new PropertyEqualToRule<VEvent>(organiser), new PropertyEqualToRule<VEvent>(attendee)]
    }
}
