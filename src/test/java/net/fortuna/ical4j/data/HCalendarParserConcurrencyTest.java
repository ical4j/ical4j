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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * HCalendarParser converts hCalendar dates and date-times with {@link java.text.SimpleDateFormat}, which is not
 * thread-safe, so parses running on different threads must not share an instance.
 */
public class HCalendarParserConcurrencyTest {

    private static final int THREADS = 8;

    private static final int ITERATIONS = 50;

    @Test
    public void testConcurrentDateParsing() throws Exception {
        // each thread parses its own document, with a date-only DTSTART and a date-time DTEND..
        List<String> documents = new ArrayList<>();
        List<String> expected = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            String document = document(String.format("%04d-%02d-%02d", 1990 + i, 1 + i, 1 + i),
                    String.format("%04d-%02d-%02dT%02d:%02d:00+0%d:00", 2000 + i, 12 - i, 28 - i, i, 10 + i, i));
            documents.add(document);
            // the date-only value depends on the default zone, so take the expected values from a parse on this thread..
            expected.add(dates(document));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        try {
            CountDownLatch start = new CountDownLatch(1);
            List<Future<List<String>>> results = new ArrayList<>();
            for (String document : documents) {
                results.add(executorService.submit(() -> {
                    start.await();
                    List<String> parsed = new ArrayList<>();
                    for (int j = 0; j < ITERATIONS; j++) {
                        parsed.add(dates(document));
                    }
                    return parsed;
                }));
            }
            start.countDown();

            for (int i = 0; i < THREADS; i++) {
                for (String parsed : results.get(i).get(1, TimeUnit.MINUTES)) {
                    assertEquals(expected.get(i), parsed, "Dates parsed on thread " + i);
                }
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    private static String document(String dtStart, String dtEnd) {
        return "<div class=\"vcalendar\"><div class=\"vevent\">"
                + "<span class=\"uid\">uid@example.com</span>"
                + "<abbr class=\"dtstart\" title=\"" + dtStart + "\">start</abbr>"
                + "<abbr class=\"dtend\" title=\"" + dtEnd + "\">end</abbr>"
                + "<span class=\"summary\">Meeting</span>"
                + "</div></div>";
    }

    private static String dates(String document) throws Exception {
        Calendar calendar = new CalendarBuilder(new HCalendarParser()).build(new StringReader(document));
        VEvent event = calendar.<VEvent>getComponent(Component.VEVENT).orElseThrow();
        return event.getRequiredProperty(Property.DTSTART).getValue() + "/"
                + event.getRequiredProperty(Property.DTEND).getValue();
    }
}
