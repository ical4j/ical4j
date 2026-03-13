/**
 * Copyright (c) 2012, Ben Fortuna
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
package net.fortuna.ical4j.data;

import net.fortuna.ical4j.util.Strings;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * $Id$
 * <p/>
 * Created on 21/01/2006
 * <p/>
 * Unit tests for FoldingWriter.
 *
 * @author Ben Fortuna
 */
class FoldingWriterTest {

    @Test
    void testLineLengthAppend() throws IOException {

        StringWriter sw = new StringWriter();
        try (FoldingWriter writer = new FoldingWriter(sw, 20)) {
            for (int i = 0; i < 25; i++) {
                writer.write(Integer.toString(i % 10));
            }
            assertEquals("01234567890123456789" + Strings.LINE_SEPARATOR + " 01234", sw.getBuffer().toString());
        }
    }

    @Test
    void testLineLength73() throws IOException {
        StringWriter sw = new StringWriter();

        try (FoldingWriter writer = new FoldingWriter(sw)) {
            writer.write("BEGIN:VCALENDAR");
            writer.write(Strings.LINE_SEPARATOR);
            writer.write("PRODID:-//Open Source Applications Foundation//NONSGML Scooby Server//EN");
            writer.write(Strings.LINE_SEPARATOR);
            writer.write("VERSION:2.0");

            assertEquals("BEGIN:VCALENDAR"
                    + Strings.LINE_SEPARATOR
                    + "PRODID:-//Open Source Applications Foundation//NONSGML Scooby Server//EN"
                    + Strings.LINE_SEPARATOR
                    + "VERSION:2.0",
                    sw.getBuffer().toString());
        }
    }

    @Test
    void testLineLength73Fold() throws IOException {
        StringWriter sw = new StringWriter();

        try (FoldingWriter writer = new FoldingWriter(sw)) {
            writer.write("BEGIN:VCALENDAR");
            writer.write(Strings.LINE_SEPARATOR);
            writer.write("PRODID:-//Open Source Applications Foundation//NONSGML Scooby Server Application//EN");
            writer.write(Strings.LINE_SEPARATOR);
            writer.write("VERSION:2.0");

            assertEquals("BEGIN:VCALENDAR"
                    + Strings.LINE_SEPARATOR
                    + "PRODID:-//Open Source Applications Foundation//NONSGML Scooby Server Appl"
                    + Strings.LINE_SEPARATOR
                    + " ication//EN"
                    + Strings.LINE_SEPARATOR
                    + "VERSION:2.0",
                    sw.getBuffer().toString());
        }
    }

    @Test
    void testMultibyteCharacterFolding() throws IOException {

        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 25; i++) {
            longText.append("🙂");
        }

        StringWriter sw = new StringWriter();
        try (FoldingWriter writer = new FoldingWriter(sw, 20)) {

            writer.write(longText.toString());

            assertEquals("🙂🙂🙂🙂🙂🙂🙂🙂🙂🙂"
                    + Strings.LINE_SEPARATOR
                    + " 🙂🙂🙂🙂🙂🙂🙂🙂🙂"
                    + Strings.LINE_SEPARATOR
                    + " 🙂🙂🙂🙂🙂🙂",
                    sw.getBuffer().toString());
        }
    }

    /**
     * Test that a line of exactly foldLength chars followed by CRLF does NOT
     * create an empty continuation line.
     *
     * RFC 5545 Section 3.1: "Lines of text SHOULD NOT be longer than 75 octets,
     * excluding the line break." - the line break doesn't count, so a line of
     * exactly 75 chars + CRLF is valid and should not be folded.
     *
     * @see <a href="https://github.com/ical4j/ical4j/issues/832">Issue #832</a>
     */
    @Test
    void testNoFoldAtExactMaxLength() throws IOException {
        StringWriter sw = new StringWriter();

        // PRODID: (7 chars) + 68 chars = 75 chars exactly
        String exactlyMaxLength = "PRODID:01234567890123456789012345678901234567890123456789012345678901234567";
        assertEquals(75, exactlyMaxLength.length());

        try (FoldingWriter writer = new FoldingWriter(sw, 75)) {
            writer.write(exactlyMaxLength);
            writer.write(Strings.LINE_SEPARATOR);
            writer.write("VERSION:2.0");
            writer.write(Strings.LINE_SEPARATOR);

            // Should NOT have an empty continuation line after PRODID
            assertEquals(exactlyMaxLength
                    + Strings.LINE_SEPARATOR
                    + "VERSION:2.0"
                    + Strings.LINE_SEPARATOR,
                    sw.getBuffer().toString());
        }
    }

}
