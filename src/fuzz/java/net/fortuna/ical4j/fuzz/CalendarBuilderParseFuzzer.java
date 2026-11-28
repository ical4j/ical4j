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

/**
 * 01 — CalendarBuilder#build on raw bytes. Byte 0: bit 0 relaxed hints, bit 1 the suppress-invalid builder of the
 * side service. build returns or throws ParserException / IOException; toString() of the result never throws.
 * A NullPointerException or ClassCastException wrapped as the cause of a ParserException is reported too (low
 * severity: a factory relying on the parser's catch-all).
 */
public class CalendarBuilderParseFuzzer {

    /** The low-severity signature: a programming error the parser's catch-all turned into a ParserException. */
    public static class WrappedRuntimeDefect extends RuntimeException {
        WrappedRuntimeDefect(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    public static void fuzzerTestOneInput(byte[] input) {
        if (input.length < 1) {
            return;
        }
        int selector = input[0] & 0xff;
        Ical.hints((selector & 1) != 0);
        boolean suppress = (selector & 2) != 0;

        Calendar calendar;
        try {
            calendar = Ical.builder(suppress).build(new ByteArrayInputStream(input, 1, input.length - 1));
        } catch (ParserException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NullPointerException || cause instanceof ClassCastException) {
                throw new WrappedRuntimeDefect("ParserException wrapping " + cause.getClass().getName()
                        + " (low severity): " + e.getMessage(), cause);
            }
            return;
        } catch (IOException e) {
            return;
        }
        if (calendar != null) {
            calendar.toString();
        }
    }
}
