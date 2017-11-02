/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <p>
 * o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p>
 * o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
 * o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p>
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
package net.fortuna.ical4j.data;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Calendar;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 * <p/>
 * Test case for iCalendarBuilder.
 *
 * @author benf
 */
public class ConcurrencyTest extends TestCase {

    public void testConcurrency() throws Exception {


        final AtomicLong size = new AtomicLong();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try(FileInputStream fis = new FileInputStream("src/test/resources/samples/valid/lotr.ics");) {
                        Calendar calendar = new CalendarBuilder().build(fis);
                        Calendar cal = new Calendar(calendar);
                        size.addAndGet(cal.getComponents().size());
                    } catch (IOException | ParserException | URISyntaxException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        assertEquals(3700, size.longValue());
    }

    public void testConcurrencySingleInstanceOfCalendar() throws Exception {
        final Calendar calendar;
        try(FileInputStream fis = new FileInputStream("src/test/resources/samples/valid/lotr.ics");) {
            calendar = new CalendarBuilder().build(fis);
        } catch (IOException | ParserException e) {
            throw new RuntimeException(e.getMessage(), e);
        }


        final AtomicLong size = new AtomicLong();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Calendar cal = new Calendar(calendar);
                        size.addAndGet(cal.getComponents().size());
                    } catch (IOException | ParseException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        assertEquals(3700, size.longValue());
    }
}
