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

package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attach;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

/**
 * Run in a forked JVM by {@code CalendarParserCharsetTest} with a non-UTF-8 default charset: reports the
 * characters a UTF-8 encoded stream parses to, and the bytes an inline attachment decodes to. Values are
 * printed as hex so the output itself doesn't depend on the charset.
 */
public class CalendarParserCharsetProbe {

    private static final String CALENDAR = "BEGIN:VCALENDAR\r\n"
            + "VERSION:2.0\r\n"
            + "PRODID:-//ical4j//test//EN\r\n"
            + "BEGIN:VEVENT\r\n"
            + "UID:charset@ical4j\r\n"
            + "SUMMARY:Café €\r\n"
            + "END:VEVENT\r\n"
            + "END:VCALENDAR\r\n";

    public static void main(String[] args) throws IOException, ParserException {
        var in = new ByteArrayInputStream(CALENDAR.getBytes(StandardCharsets.UTF_8));
        var handler = new SummaryHandler();
        new CalendarParserImpl().parse(in, handler);
        System.out.println("summary=" + hex(handler.summary.toCharArray()));

        var parameters = new ParameterList(Arrays.asList(Value.BINARY, Encoding.QUOTED_PRINTABLE));
        var attachData = new Attach(parameters, "café").getBinaryData();
        System.out.println("attach=" + hexBytes(attachData));
    }

    private static String hex(char[] chars) {
        var b = new StringBuilder();
        for (var c : chars) {
            b.append(String.format(Locale.ROOT, "%04x", (int) c));
        }
        return b.toString();
    }

    private static String hexBytes(byte[] bytes) {
        var b = new StringBuilder();
        for (var value : bytes) {
            b.append(String.format(Locale.ROOT, "%02x", value & 0xff));
        }
        return b.toString();
    }

    private static class SummaryHandler implements ContentHandler {

        private String summary = "";

        private boolean inSummary;

        @Override
        public void startCalendar() {
        }

        @Override
        public void endCalendar() {
        }

        @Override
        public void startComponent(String name) {
        }

        @Override
        public void endComponent(String name) {
        }

        @Override
        public void startProperty(String name) {
            inSummary = "SUMMARY".equals(name);
        }

        @Override
        public void propertyValue(String value) {
            if (inSummary) {
                summary = value;
            }
        }

        @Override
        public void endProperty(String name) {
            inSummary = false;
        }

        @Override
        public void parameter(String name, String value) {
        }
    }
}
