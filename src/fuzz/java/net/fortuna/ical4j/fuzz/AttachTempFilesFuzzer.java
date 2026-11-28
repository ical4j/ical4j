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
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.property.Attach;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 10 — inline ATTACH values (ENCODING=BASE64/8BIT/unknown, VALUE=BINARY, FMTTYPE, valid or invalid base64, folded,
 * empty) in a VEVENT, parsed, written, copied and dropped. Every parsed ATTACH has a URI or a binary and is written
 * without exception; a valid base64 value decodes to the reference bytes (java.util.Base64 MIME decoder) and its
 * written value re-decodes to the same bytes; once the calendar is dropped, no temporary file and no
 * DeleteOnExitHook entry is left behind (the leak is reported once per run, the files of each input are removed).
 */
public class AttachTempFilesFuzzer {

    private static final String[] ENCODINGS = {"BASE64", "base64", "Base64", "8BIT", "7BIT", "BINARY",
            "QUOTED-PRINTABLE", "X-UUENCODE", "\"BASE64\"", ""};
    private static File tmpDir;
    private static Set<String> hookFiles;
    private static Class<?> hookClass;
    private static boolean leakReported;

    @SuppressWarnings("unchecked")
    public static void fuzzerInitialize() throws Exception {
        Ical.hints(false);
        tmpDir = new File(System.getProperty("java.io.tmpdir"));
        Class<?> hook = Class.forName("java.io.DeleteOnExitHook");
        hookClass = hook;
        Field files = hook.getDeclaredField("files");
        files.setAccessible(true);
        hookFiles = (Set<String>) files.get(null);
    }

    private static Set<String> attachFiles() {
        String[] names = tmpDir.list((dir, name) -> name.startsWith("ical4j-attach"));
        return names == null ? new HashSet<>() : new HashSet<>(Arrays.asList(names));
    }

    /** DeleteOnExitHook#add is static synchronized: its set is guarded by the class. */
    private static int hookSize() {
        synchronized (hookClass) {
            return hookFiles.size();
        }
    }

    private static String fold(String line) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < line.length(); i += 70) {
            if (i > 0) {
                b.append("\r\n ");
            }
            b.append(line, i, Math.min(line.length(), i + 70));
        }
        return b.toString();
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) throws IOException {
        Ical.hints(d.consumeBoolean());
        int n = d.consumeInt(1, 5);
        StringBuilder ics = new StringBuilder("BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//twake-fuzz//attach//EN\r\n"
                + "BEGIN:VEVENT\r\nUID:attach@twake-fuzz\r\nDTSTAMP:20240101T000000Z\r\nDTSTART:20240102T100000Z\r\n");
        List<byte[]> expected = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String encoding = d.pickValue(ENCODINGS);
            StringBuilder line = new StringBuilder("ATTACH");
            if (!encoding.isEmpty()) {
                line.append(";ENCODING=").append(encoding);
            }
            if (d.consumeBoolean()) {
                line.append(";VALUE=BINARY");
            }
            if (d.consumeBoolean()) {
                line.append(";FMTTYPE=").append(d.pickValue(new String[]{"image/png", "text/plain", "\"a;b\""}));
            }
            line.append(':');
            byte[] bytes = null;
            switch (d.consumeInt(0, 4)) {
                case 0:
                case 1:
                    bytes = d.consumeBytes(d.consumeInt(0, 600));
                    line.append(Base64.getEncoder().encodeToString(bytes));
                    break;
                case 2:
                    line.append(d.consumeString(80).replace("\r", "").replace("\n", ""));
                    break;
                case 3:
                    line.append(Base64.getEncoder().withoutPadding().encodeToString(d.consumeBytes(40)))
                            .append(d.pickValue(new String[]{"=", "==", "===", "*", "-_", ""}));
                    break;
                default:
                    break; // empty value
            }
            // a valid base64 value under ENCODING=BASE64 (any case) has a reference decoding
            expected.add(bytes != null && encoding.equalsIgnoreCase("BASE64") ? bytes : null);
            ics.append(d.consumeBoolean() ? fold(line.toString()) : line).append("\r\n");
        }
        ics.append("END:VEVENT\r\nEND:VCALENDAR\r\n");
        Ical.debug("calendar", ics);

        Set<String> filesBefore = attachFiles();
        int hookBefore = hookSize();
        boolean checked = false;
        try {
            check(ics.toString(), expected);
            checked = true;
        } finally {
            Set<String> created = attachFiles();
            created.removeAll(filesBefore);
            int hookGrowth = hookSize() - hookBefore;
            // the files of this input are removed, and their delete-on-exit entries, so that the run does not fill
            // the disk or the heap with the very leak it reports
            for (String name : created) {
                File f = new File(tmpDir, name);
                synchronized (hookClass) {
                    hookFiles.remove(f.getPath());
                }
                f.delete();
            }
            // (only when check() completed: a finding of its own is not to be hidden by this one)
            if (checked && (!created.isEmpty() || hookGrowth > 0) && (!leakReported || Ical.DEBUG)) {
                leakReported = true;
                throw new AssertionError("temporary file leak: parsing, writing, copying and dropping one calendar left "
                        + created.size() + " file(s) in java.io.tmpdir and " + hookGrowth
                        + " java.io.DeleteOnExitHook entries (deleted on JVM exit only)");
            }
        }
    }

    private static void check(String ics, List<byte[]> expected) throws IOException {
        Calendar calendar;
        try {
            calendar = Ical.parse(ics, false);
        } catch (ParserException e) {
            return;
        }
        if (calendar == null) {
            return;
        }
        Optional<VEvent> event = calendar.getComponent("VEVENT");
        if (event.isEmpty()) {
            return;
        }
        List<Property> attachments = event.get().getProperties(Property.ATTACH);
        for (int i = 0; i < attachments.size(); i++) {
            Attach a = (Attach) attachments.get(i);
            String written = a.toString(); // never throws
            if (a.getUri() == null && a.getBinary() == null) {
                throw new AssertionError("an ATTACH has neither a URI nor a binary: " + Ical.abbreviate(written, 200));
            }
            byte[] reference = i < expected.size() ? expected.get(i) : null;
            if (a.getBinary() != null) {
                byte[] data = a.getBinaryData();
                if (reference != null && attachments.size() == expected.size() && !Arrays.equals(data, reference)) {
                    throw new AssertionError("ATTACH binary differs from the base64 reference decoding: "
                            + data.length + " bytes instead of " + reference.length);
                }
                Optional<Encoding> encoding = a.getParameter(Parameter.ENCODING);
                if (encoding.isPresent() && Encoding.BASE64.equals(encoding.get()) && a.getValue() != null) {
                    byte[] again = Base64.getMimeDecoder().decode(a.getValue());
                    if (!Arrays.equals(again, data)) {
                        throw new AssertionError("the written ATTACH value does not re-decode to the same bytes");
                    }
                }
            }
        }
        Ical.write(calendar);
        Calendar copy = calendar.copy();
        copy.toString();
    }
}
