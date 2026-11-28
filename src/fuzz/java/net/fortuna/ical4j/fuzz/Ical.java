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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.data.ContentHandlerContext;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ZoneRulesProviderImpl;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Shared helpers of the ical4j harnesses: the compatibility hint sets of SCENARI.md, the two builders the consumers
 * use, the non-validating outputter, and the hygiene of the JVM-wide zone-id pool between inputs.
 */
public final class Ical {

    public static final String[] HINT_KEYS = {
            CompatibilityHints.KEY_RELAXED_PARSING, CompatibilityHints.KEY_RELAXED_UNFOLDING,
            CompatibilityHints.KEY_RELAXED_VALIDATION, CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY,
            CompatibilityHints.KEY_NOTES_COMPATIBILITY};

    /** In reproduce mode, the harnesses print the case they decoded from the input (FUZZ_MODE=reproduce). */
    public static final boolean DEBUG = "reproduce".equals(System.getenv("FUZZ_MODE"));

    private Ical() {
    }

    public static void debug(String what, Object value) {
        if (DEBUG) {
            System.err.println("[case] " + what + ": " + value);
        }
    }

    /** strict (false): every hint off; relaxed (true): relaxed parsing, unfolding, validation, Outlook and Notes. */
    public static void hints(boolean relaxed) {
        for (String key : HINT_KEYS) {
            CompatibilityHints.setHintEnabled(key, relaxed);
        }
    }

    public static boolean relaxed() {
        return CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
    }

    /**
     * The zone-id pool (1500 ids) is JVM-wide and only refilled once the registries of previous inputs are collected:
     * collect them before it runs low, so that one input never fails because of the inputs before it.
     */
    public static void reclaimZoneIds() {
        ZoneRulesProviderImpl.getInstance().ifPresent(p -> {
            if (p.getZoneIdPool().availableZoneIds() < 600) {
                System.gc();
                p.getZoneIdPool().cleanup();
            }
        });
    }

    /** suppressInvalid: the builder of the side service (invalid properties dropped), else `new CalendarBuilder()`. */
    public static CalendarBuilder builder(boolean suppressInvalid) {
        reclaimZoneIds();
        if (suppressInvalid) {
            return new CalendarBuilder(CalendarParserFactory.getInstance().get(),
                    new ContentHandlerContext().withSuppressInvalidProperties(true),
                    TimeZoneRegistryFactory.getInstance().createRegistry());
        }
        return new CalendarBuilder();
    }

    /** May return null: a stream with no complete VCALENDAR builds nothing. */
    public static Calendar parse(String text, boolean suppressInvalid) throws IOException, ParserException {
        return builder(suppressInvalid).build(new StringReader(text));
    }

    public static String write(Calendar calendar) throws IOException {
        StringWriter out = new StringWriter();
        new CalendarOutputter(false).output(calendar, out);
        return out.toString();
    }

    /** An exception the target let escape, rethrown with its JDK frames removed (see {@link #withoutJdkFrames}). */
    public static class Escaped extends RuntimeException {
        Escaped(String message) {
            super(message);
        }
    }

    /**
     * The same defect (e.g. a DateTimeParseException escaping a validation rule) goes through different JDK-internal
     * frames depending on the bad value, which splits one signature into many: the escaping exception is rethrown with
     * the frames of the JDK removed, so that the fuzzer deduplicates it by the ical4j code path only.
     */
    public static Escaped withoutJdkFrames(String context, Throwable e) {
        Escaped r = new Escaped(context + ": " + e.getClass().getName() + ": " + e.getMessage());
        r.setStackTrace(java.util.Arrays.stream(e.getStackTrace())
                .filter(f -> !f.getClassName().startsWith("java.") && !f.getClassName().startsWith("jdk.")
                        && !f.getClassName().startsWith("sun."))
                .toArray(StackTraceElement[]::new));
        return r;
    }

    public static String utf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String abbreviate(String s, int max) {
        String t = s.replace("\r", "\\r").replace("\n", "\\n");
        return t.length() <= max ? t : t.substring(0, max) + "...";
    }
}
