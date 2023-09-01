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

package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import net.fortuna.ical4j.model.parameter.Feature
import net.fortuna.ical4j.model.parameter.Label
import net.fortuna.ical4j.model.parameter.Value
import spock.lang.Specification

class ConferenceTest extends Specification {

    def 'test conference constructor'() {
        expect: 'string result matches expected'
        def paramList = new ParameterList()
        paramList.addAll(params)
        new Conference(paramList, value) as String == expectedValue

        where:
        value                   | params                                    | expectedValue
        'https://example.com'                       | []                                        | 'CONFERENCE:https://example.com\r\n'
        'https://example.com'                       | [Value.URI, new Feature('AUDIO,VIDEO')]   | 'CONFERENCE;VALUE=URI;FEATURE="AUDIO,VIDEO":https://example.com\r\n'
        'https://chat.example.com/audio?id=123456'  | [Value.URI, new Feature('AUDIO,VIDEO'),
                                                       new Label('Attendee dial-in')]           | 'CONFERENCE;VALUE=URI;FEATURE="AUDIO,VIDEO";LABEL=Attendee dial-in:https://chat.example.com/audio?id=123456\r\n'
    }
}
