/*
 *  Copyright (c) 2022-2022, Ben Fortuna
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

import net.fortuna.ical4j.AbstractTzurlIntegrationTest
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.util.Calendars

/**
 * Updates every bundled definition from a local tzurl server rather than the live tzurl.org service, so the
 * test doesn't depend on hundreds of network requests succeeding. {@code updateDefinition} returns the original
 * definition unchanged when an update fails, so the checks below also confirm the update really happened.
 */
class TimeZoneUpdaterTest extends AbstractTzurlIntegrationTest {

    def 'test update all tz definitions'() {
        given: 'a timezone updater'
        TimeZoneUpdater updater = []

        and: 'a bundled timezone definition'
        VTimeZone tz = Calendars.load(files).getComponent('VTIMEZONE').get()
        int requestsBefore = tzurlRequests.get()

        when: 'the definition is updated'
        VTimeZone updated = updater.updateDefinition(tz)

        then: 'the definition was fetched from the server'
        tzurlRequests.get() == requestsBefore + 1

        and: 'a new definition for the same zone was returned'
        updated !== tz
        updated.timeZoneId.value == tz.timeZoneId.value

        where:
        files << getInputFiles(new File('src/main/resources/zoneinfo'))
    }

    def getInputFiles(File root) {
        if (root.isDirectory()) {
            root.listFiles({it.name.endsWith('.ics') || it.isDirectory()} as FileFilter).collect {getInputFiles(it)}.flatten()
        } else {
            [root.absolutePath]
        }
    }
}
