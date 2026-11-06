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

package net.fortuna.ical4j.model

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.component.VTimeZone
import spock.lang.Specification

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class TimeZoneRegistryConcurrencyTest extends Specification {

    private static VTimeZone vtz(int i) {
        def ics = ['BEGIN:VCALENDAR', 'VERSION:2.0', 'PRODID:-//ical4j//test//EN',
                   'BEGIN:VTIMEZONE', "TZID:Custom/Zone$i",
                   'BEGIN:STANDARD', 'DTSTART:19710101T000000',
                   'TZOFFSETFROM:+0100', 'TZOFFSETTO:+0000', 'RRULE:FREQ=YEARLY;BYMONTH=1',
                   'END:STANDARD', 'END:VTIMEZONE', 'END:VCALENDAR', ''].join('\r\n')
        new CalendarBuilder().build(new StringReader(ics)).getComponent(Component.VTIMEZONE).get() as VTimeZone
    }

    def 'getZoneId does not fail while another thread registers timezones'() {
        given: 'definitions prepared up front and a registry shared between threads'
        def count = 600
        def definitions = (0..<count).collect { new TimeZone(vtz(it)) }
        def registry = TimeZoneRegistryFactory.instance.createRegistry()
        definitions.take(50).each { registry.register(it) }

        and: 'collectors for any reader-thread failures'
        def failures = new CopyOnWriteArrayList<Throwable>()
        def running = new AtomicBoolean(true)
        def start = new CountDownLatch(1)

        when: 'one thread registers the remaining definitions while others resolve TZIDs'
        def writer = {
            start.await()
            try {
                definitions.drop(50).each { registry.register(it) }
            } finally {
                running.set(false)
            }
        }
        def reader = {
            start.await()
            while (running.get()) {
                for (int i = 0; i < 50; i++) {
                    try {
                        registry.getZoneId("Custom/Zone$i")
                    } catch (java.time.DateTimeException ignored) {
                        // a concurrently-registered id may not be visible yet; not a failure
                    } catch (Throwable t) {
                        failures.add(t)
                    }
                }
            }
        }

        def pool = Executors.newFixedThreadPool(5)
        def futures = ([writer] + (1..4).collect { reader }).collect { pool.submit(it as Runnable) }
        start.countDown()
        futures.each { it.get(120, TimeUnit.SECONDS) }
        pool.shutdown()

        then: 'concurrent iteration of the id map never throws (e.g. ConcurrentModificationException)'
        failures.isEmpty()
    }
}
