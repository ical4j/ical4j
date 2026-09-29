/*
 *  Copyright (c) 2025, Ben Fortuna
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

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.component.VTimeZone
import spock.lang.Specification

class TimeZoneUpdaterSchemeTest extends Specification {

    def 'file scheme tzurl is not fetched during update'() {
        given: 'a local file containing a foreign timezone definition'
        File target = File.createTempFile('tz-update-target', '.ics')
        target.deleteOnExit()
        target.text = '''BEGIN:VCALENDAR\r
PRODID:-//test//test//EN\r
VERSION:2.0\r
BEGIN:VTIMEZONE\r
TZID:Injected/FromFile\r
BEGIN:STANDARD\r
DTSTART:19700101T000000\r
TZOFFSETFROM:+0000\r
TZOFFSETTO:+0000\r
TZNAME:GMT\r
END:STANDARD\r
END:VTIMEZONE\r
END:VCALENDAR\r
'''

        and: 'a parsed timezone whose tzurl points at that local file'
        def ics = """BEGIN:VCALENDAR\r
PRODID:-//test//test//EN\r
VERSION:2.0\r
BEGIN:VTIMEZONE\r
TZID:Victim/Zone\r
TZURL:${target.toURI()}\r
BEGIN:STANDARD\r
DTSTART:19700101T000000\r
TZOFFSETFROM:+0000\r
TZOFFSETTO:+0000\r
TZNAME:GMT\r
END:STANDARD\r
END:VTIMEZONE\r
END:VCALENDAR\r
"""
        VTimeZone tz = new CalendarBuilder().build(new StringReader(ics)).getComponent('VTIMEZONE').get()

        when: 'the definition is updated'
        VTimeZone result = new TimeZoneUpdater().updateDefinition(tz)

        then: 'the local file is not read and the original definition is retained'
        result.getRequiredProperty('TZID').value == 'Victim/Zone'
    }

    def 'non-http schemes are rejected by openConnection'() {
        given: 'a timezone updater'
        TimeZoneUpdater updater = []

        when: 'a file url connection is requested'
        updater.openConnection(URI.create('file:///etc/hostname').toURL())

        then: 'the request is refused'
        thrown(IOException)
    }
}
