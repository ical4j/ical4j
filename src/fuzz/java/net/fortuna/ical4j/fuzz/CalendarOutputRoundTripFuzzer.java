/**
 * Copyright (c) 2026, Ben Fortuna
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

package net.fortuna.ical4j.fuzz;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;

/**
 * 05 — parse, write (non-validating CalendarOutputter, FoldingWriter), parse. Byte 0 as in 01. For an accepted
 * input: the output re-parses under the same hints, the second model equals the first (deep: values, parameters,
 * sub-components), a second write is byte-identical, every physical line ends with CRLF, holds no other CR/LF,
 * is at most 75 chars, and no fold splits a surrogate pair. Lines over 75 UTF-8 octets are a separate minor
 * signature (FoldingWriter counts UTF-16 chars).
 */
public class CalendarOutputRoundTripFuzzer {

    /** The minor signature: a physical line within 75 chars but over 75 UTF-8 octets. */
    public static class LineOverSeventyFiveOctets extends AssertionError {
        LineOverSeventyFiveOctets(String message) {
            super(message);
        }
    }

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    private static final java.util.regex.Pattern NAME = java.util.regex.Pattern.compile("[A-Za-z0-9-]+");

    private static void checkNames(net.fortuna.ical4j.model.Component component) {
        checkNames(component.getProperties(), component.getName());
        if (component instanceof net.fortuna.ical4j.model.ComponentContainer) {
            ((net.fortuna.ical4j.model.ComponentContainer<?>) component).getComponents()
                    .forEach(c -> checkNames((net.fortuna.ical4j.model.Component) c));
        }
    }

    private static void checkNames(java.util.List<net.fortuna.ical4j.model.Property> properties, String component) {
        for (net.fortuna.ical4j.model.Property p : properties) {
            if (!NAME.matcher(p.getName()).matches()) {
                throw new AssertionError("illegal property name [" + p.getName() + "] in " + component);
            }
            for (net.fortuna.ical4j.model.Parameter q : p.getParameterList().getAll()) {
                if (!NAME.matcher(q.getName()).matches()) {
                    throw new AssertionError("illegal parameter name [" + q.getName() + "] in " + component + "/"
                            + p.getName());
                }
            }
        }
    }

    public static void fuzzerTestOneInput(byte[] input) throws IOException {
        if (input.length < 1) {
            return;
        }
        int selector = input[0] & 0xff;
        Ical.hints((selector & 1) != 0);
        boolean suppress = (selector & 2) != 0;

        Calendar first;
        try {
            first = Ical.builder(suppress).build(new ByteArrayInputStream(input, 1, input.length - 1));
        } catch (ParserException | IOException e) {
            return;
        }
        if (first == null) {
            return;
        }
        // a corruption of the first parse that is stable on re-parse is invisible to the round trip: check that
        // every name follows RFC 5545 3.1 (iana-token / x-name: ALPHA, DIGIT and "-")
        checkNames(first.getProperties(), "VCALENDAR");
        first.getComponents().forEach(CalendarOutputRoundTripFuzzer::checkNames);

        String out;
        try {
            out = Ical.write(first);
        } catch (DateTimeException e) {
            // a lazily parsed date that cannot be written: scenario 01's finding (toString() throws), not a drift
            return;
        }
        Ical.debug("output", out);

        Calendar second;
        try {
            second = Ical.parse(out, suppress);
        } catch (ParserException e) {
            throw new AssertionError("the output does not re-parse: " + e.getMessage() + "\n" + Ical.abbreviate(out, 2000), e);
        }
        if (second == null) {
            throw new AssertionError("the output re-parses to no calendar:\n" + Ical.abbreviate(out, 2000));
        }
        String diff = Diff.calendars(first, second);
        if (diff != null) {
            throw new AssertionError("re-parsed calendar differs: " + diff);
        }
        String again = Ical.write(second);
        if (!again.equals(out)) {
            throw new AssertionError("second write differs from the first:\n" + Ical.abbreviate(out, 1500)
                    + "\n--- vs ---\n" + Ical.abbreviate(again, 1500));
        }
        checkLines(out);
    }

    private static void checkLines(String out) {
        if (out.isEmpty()) {
            return;
        }
        if (!out.endsWith("\r\n")) {
            throw new AssertionError("output does not end with CRLF");
        }
        String[] lines = out.substring(0, out.length() - 2).split("\r\n", -1);
        String octetViolation = null;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.indexOf('\r') >= 0 || line.indexOf('\n') >= 0) {
                throw new AssertionError("bare CR or LF inside physical line " + (i + 1) + ": ["
                        + Ical.abbreviate(line, 200) + "]");
            }
            if (line.length() > 75) {
                throw new AssertionError("physical line " + (i + 1) + " is " + line.length() + " chars long");
            }
            if (i > 0 && line.startsWith(" ") && line.length() > 1 && Character.isLowSurrogate(line.charAt(1))
                    && !lines[i - 1].isEmpty() && Character.isHighSurrogate(lines[i - 1].charAt(lines[i - 1].length() - 1))) {
                throw new AssertionError("a fold splits a surrogate pair at line " + (i + 1));
            }
            int octets = line.getBytes(StandardCharsets.UTF_8).length;
            if (octets > 75 && octetViolation == null) {
                octetViolation = "physical line " + (i + 1) + " is " + line.length() + " chars but " + octets
                        + " UTF-8 octets (RFC 5545 §3.1: 75 octets)";
            }
        }
        if (octetViolation != null) {
            throw new LineOverSeventyFiveOctets(octetViolation);
        }
    }
}
