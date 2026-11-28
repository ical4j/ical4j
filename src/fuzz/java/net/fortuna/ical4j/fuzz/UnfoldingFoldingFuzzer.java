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

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import net.fortuna.ical4j.data.FoldingWriter;
import net.fortuna.ical4j.data.UnfoldingReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 17 — UnfoldingReader#read() / #read(char[], int, int) and FoldingWriter#write. Unfolding: whatever the read
 * schedule (single chars, chunks of 1–8192 at any offset, through a BufferedReader of the same size or not), the text
 * read equals the RFC 5545 §3.1 reference (every CRLF followed by a space or tab removed; in relaxed mode also a bare
 * LF followed by one), and read(char[]) never returns 0 before the end. Folding: the reference unfolding of the
 * FoldingWriter output gives back the written content lines, no physical line exceeds the fold length, no surrogate
 * pair is split.
 */
public class UnfoldingFoldingFuzzer {

    private static final Pattern STRICT = Pattern.compile("\r\n[ \t]");
    private static final Pattern RELAXED = Pattern.compile("\r\n[ \t]|\n[ \t]");
    private static final String[] PIECES = {"\r\n ", "\r\n\t", "\n ", "\n\t", "\r\n", "\r", "\n", " ", "\t",
            "\r\n \r\n ", "\r\r\n ", "😀", "\uD83D", "\uDE00", "é", "BEGIN:VEVENT", "a", "SUMMARY:x"};
    private static final int SIZE = 8192 + 32;

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    private static String text(FuzzedDataProvider d, int maxParts) {
        StringBuilder b = new StringBuilder();
        int parts = d.consumeInt(0, maxParts);
        for (int i = 0; i < parts; i++) {
            if (d.consumeBoolean()) {
                b.append(d.pickValue(PIECES));
            } else {
                b.append(d.consumeString(12));
            }
        }
        return b.toString();
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) throws IOException {
        boolean relaxed = d.consumeBoolean();
        if (d.consumeBoolean()) {
            unfolding(d, relaxed);
        } else {
            folding(d);
        }
    }

    private static void unfolding(FuzzedDataProvider d, boolean relaxed) throws IOException {
        boolean buffered = d.consumeBoolean();
        int ops = d.consumeInt(1, 16);
        int[] offsets = new int[ops];
        int[] lengths = new int[ops];
        for (int i = 0; i < ops; i++) {
            // length 0: a single read()
            lengths[i] = d.consumeBoolean() ? d.consumeInt(0, 8) : d.consumeInt(0, 8192);
            offsets[i] = d.consumeInt(0, 3) == 0 ? d.consumeInt(1, 16) : 0;
        }
        String text = text(d, 64);
        String expected = (relaxed ? RELAXED : STRICT).matcher(text).replaceAll("");

        // the pushback buffer is as large as the largest read, as the class documentation requires
        UnfoldingReader unfolding = new UnfoldingReader(new StringReader(text), SIZE, relaxed);
        Reader in = buffered ? new BufferedReader(unfolding, SIZE) : unfolding;
        StringBuilder out = new StringBuilder();
        char[] buf = new char[SIZE + 32];
        int guard = 4 * text.length() + 64;
        for (int i = 0; ; i++) {
            if (i > guard) {
                throw new AssertionError("no progress after " + i + " reads; read so far ["
                        + Ical.abbreviate(out.toString(), 300) + "]");
            }
            int op = Math.min(i, ops - 1);
            int n;
            if (lengths[op] == 0) {
                int c = in.read();
                if (c < 0) {
                    break;
                }
                out.append((char) c);
            } else {
                n = in.read(buf, offsets[op], lengths[op]);
                if (n < 0) {
                    break;
                }
                if (n == 0) {
                    throw new AssertionError("read(cbuf, " + offsets[op] + ", " + lengths[op]
                            + ") returned 0 before the end of the stream, after [" + Ical.abbreviate(out.toString(), 300)
                            + "] of [" + Ical.abbreviate(text, 300) + "]");
                }
                out.append(buf, offsets[op], n);
            }
        }
        if (!out.toString().equals(expected)) {
            StringBuilder schedule = new StringBuilder();
            for (int i = 0; i < ops; i++) {
                schedule.append(lengths[i] == 0 ? "read()" : "read(" + offsets[i] + "," + lengths[i] + ")").append(' ');
            }
            throw new AssertionError("unfolded text differs from RFC 5545 §3.1 (" + (relaxed ? "relaxed" : "strict")
                    + ", " + (buffered ? "BufferedReader, " : "") + schedule + "): input [" + Ical.abbreviate(text, 300)
                    + "] expected [" + Ical.abbreviate(expected, 300) + "] read [" + Ical.abbreviate(out.toString(), 300)
                    + "]");
        }
    }

    private static void folding(FuzzedDataProvider d) throws IOException {
        int foldLength = d.consumeBoolean() ? d.pickValue(new int[]{73, 75}) : d.consumeInt(3, 80);
        int limit = Math.min(foldLength, FoldingWriter.MAX_FOLD_LENGTH);
        // content lines: no CR/LF inside, none but the first starting with a space or tab (that would be a fold)
        List<String> lines = new ArrayList<>();
        int n = d.consumeInt(1, 6);
        for (int i = 0; i < n; i++) {
            String line = text(d, 16).replace("\r", "").replace("\n", "");
            while (i > 0 && (line.startsWith(" ") || line.startsWith("\t"))) {
                line = line.substring(1);
            }
            lines.add(line);
        }
        String content = String.join("\r\n", lines) + "\r\n";
        StringWriter sink = new StringWriter();
        try (FoldingWriter writer = new FoldingWriter(sink, foldLength)) {
            if (d.consumeBoolean()) {
                writer.write(content);
            } else {
                // in chunks, as Writer#write(String) callers may do
                int pos = 0;
                while (pos < content.length()) {
                    int len = Math.min(content.length() - pos, d.consumeInt(1, 40));
                    writer.write(content, pos, len);
                    pos += len;
                }
            }
        }
        String folded = sink.toString();
        String unfolded = STRICT.matcher(folded).replaceAll("");
        if (!unfolded.equals(content)) {
            throw new AssertionError("unfolding the FoldingWriter output (fold length " + foldLength
                    + ") does not give back the text: wrote [" + Ical.abbreviate(content, 300) + "] folded ["
                    + Ical.abbreviate(folded, 400) + "]");
        }
        String[] physical = folded.split("\r\n", -1);
        for (int i = 0; i < physical.length; i++) {
            String line = physical[i];
            if (line.length() > limit) {
                throw new AssertionError("physical line " + (i + 1) + " is " + line.length() + " chars, fold length "
                        + limit + ": [" + Ical.abbreviate(line, 120) + "]");
            }
            if (!line.isEmpty() && Character.isHighSurrogate(line.charAt(line.length() - 1)) && i + 1 < physical.length
                    && physical[i + 1].length() > 1 && physical[i + 1].charAt(0) == ' '
                    && Character.isLowSurrogate(physical[i + 1].charAt(1))) {
                throw new AssertionError("a fold splits a surrogate pair after physical line " + (i + 1));
            }
        }
    }
}
