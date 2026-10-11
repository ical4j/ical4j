/*
 *  Copyright (c) 2022, Ben Fortuna
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
import net.fortuna.ical4j.model.property.Attach
import spock.lang.Shared
import spock.lang.Specification

import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Paths

class CalendarValidatorImplTest extends Specification {

    @Shared
    ContentBuilder builder

    def setupSpec() {
        builder = new ContentBuilder()
    }

    def 'test validation with custom rules'() {
        given: 'a validator instance'
        CalendarValidatorImpl validator = []

        and: 'a calendar instance'
        def file = Files.newByteChannel(Paths.get('gradle/wrapper/gradle-wrapper.jar'))
        def calendar = builder.calendar() {
            prodid '-//Ben Fortuna//iCal4j 1.0//EN'
            version '2.0'
            vevent {
                uid '1'
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach new Attach(file.map(FileChannel.MapMode.READ_ONLY, 0, file.size()))
            }
        }

        when: 'validation applied to calendar instance'
        validator.validate(calendar)

        then: 'result is as expected'
        notThrown(ValidationException)
    }

    def 'component iTIP validation honours the recurse flag (#363)'() {
        given: 'a METHOD:REQUEST calendar whose VEVENT lacks the ORGANIZER required by RFC 5546'
        def calendar = builder.calendar() {
            prodid '-//Ben Fortuna//iCal4j 1.0//EN'
            version '2.0'
            method 'REQUEST'
            vevent {
                uid '1'
                dtstamp()
                dtstart '20240101T100000Z'
                summary 'No organizer'
                attendee 'mailto:attendee@example.com'
            }
        }

        expect: 'non-recursive validation reports no errors, since components are not validated'
        !calendar.validate(false).hasErrors()

        and: 'the calendar-level validator alone does not descend into components'
        !new CalendarValidatorImpl().validate(calendar).hasErrors()

        when: 'recursive validation is applied'
        def result = calendar.validate(true)

        then: 'the missing ORGANIZER is reported exactly once, not duplicated'
        result.hasErrors()
        result.entries.count { it.message.contains('ORGANIZER') } == 1
    }
}
