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
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryImpl;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.transform.compliance.TzHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 15 — TZID strings to time zones: TimeZoneRegistryImpl#getTimeZone (aliases, TimeZoneLoader, the lenient suffix
 * match), TimeZoneRegistry#getGlobalZoneId, TzId#toZoneId, TzHelper#getCorrectedTimeZoneIdFrom. getTimeZone returns
 * or throws IllegalArgumentException for a blank id, toZoneId returns or throws DateTimeException, nothing else; each
 * lookup takes under 1 s; a returned definition is a bundled, alias or JDK zone, never the canary placed beside
 * zoneinfo/ on the classpath (path traversal); DEFAULT_TIMEZONES does not grow with ids that resolve to nothing.
 */
public class TzidResolutionFuzzer {

    private static final String[] PIECES = {"/", "../", "./", "..", ".", "\\", "%2e", "%2e%2e/", "%2F", "Etc/", "GMT",
            "UTC", "Z", "mozilla.org", "/mozilla.org/20050126_1/", "/softwarestudio.org/Olson_20011030_5/",
            "softwarestudio.org", "Standard Time", "(UTC", "(UTC+01:00) ", "\"", "Europe/Paris", "America/New_York",
            "US/Eastern", "W. Europe Standard Time", "twake-fuzz-canary", "../twake-fuzz-canary", "GMT+0100", "+01:00",
            " ", "é", "\u0000", "zoneinfo/", "Asia/Tokyo", "Australia/Lord_Howe"};

    private static final Set<String> ALLOWED = new HashSet<>();
    private static Map<?, ?> defaultTimezones;
    private static long executions;

    public static void fuzzerInitialize() throws Exception {
        Ical.hints(false);
        try (BufferedReader r = new BufferedReader(new InputStreamReader(TzidResolutionFuzzer.class.getClassLoader()
                .getResourceAsStream("zoneinfo/tz.availableIds"), StandardCharsets.UTF_8))) {
            r.lines().map(String::trim).forEach(ALLOWED::add);
        }
        ALLOWED.addAll(ZoneId.getAvailableZoneIds());
        new TimeZoneRegistryImpl().getTimeZone("Europe/Paris"); // loads the alias tables
        ALLOWED.addAll(TimeZoneRegistry.ZONE_ALIASES.values());
        Field f = TimeZoneRegistryImpl.class.getDeclaredField("DEFAULT_TIMEZONES");
        f.setAccessible(true);
        defaultTimezones = (Map<?, ?>) f.get(null);
        if (TzidResolutionFuzzer.class.getClassLoader().getResource("twake-fuzz-canary.ics") == null) {
            throw new IllegalStateException("the canary resource is not on the classpath");
        }
    }

    private static String id(FuzzedDataProvider d) {
        switch (d.consumeInt(0, 4)) {
            case 0:
                return d.consumeString(60);
            case 1: {
                // many separators: the lenient suffix search runs over them
                StringBuilder b = new StringBuilder();
                int n = d.consumeInt(1, 3000);
                String sep = d.pickValue(new String[]{"/", "a/", "/b", "//"});
                for (int i = 0; i < n; i++) {
                    b.append(sep);
                }
                return b.append(d.pickValue(PIECES)).toString();
            }
            default: {
                StringBuilder b = new StringBuilder();
                int n = d.consumeInt(1, 6);
                for (int i = 0; i < n; i++) {
                    b.append(d.consumeBoolean() ? d.pickValue(PIECES) : d.consumeString(10));
                }
                return b.toString();
            }
        }
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) {
        boolean lenient = d.consumeBoolean();
        Ical.hints(d.consumeBoolean());
        String id = id(d);
        Ical.debug("id", id + " (lenient " + lenient + ")");
        TimeZoneRegistry registry = new TimeZoneRegistryImpl("zoneinfo/", lenient);
        int sizeBefore = defaultTimezones.size();

        long start = System.nanoTime();
        TimeZone tz;
        try {
            tz = registry.getTimeZone(id);
        } catch (IllegalArgumentException e) {
            if (!id.isBlank()) {
                throw new AssertionError("getTimeZone throws IllegalArgumentException for a non-blank id [" + id + "]", e);
            }
            tz = null;
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        if (tz != null) {
            String found = tz.getID();
            if ("TWAKE-FUZZ-CANARY".equals(found)) {
                throw new AssertionError("path traversal: TZID [" + id + "] loads the canary resource placed beside"
                        + " zoneinfo/");
            }
            if (!ALLOWED.contains(found) && !found.equals(id)) {
                throw new AssertionError("TZID [" + id + "] resolves to a definition [" + found + "] that is neither a"
                        + " bundled, an alias nor a JDK zone");
            }
        } else if (defaultTimezones.size() > sizeBefore) {
            throw new AssertionError("DEFAULT_TIMEZONES grew from " + sizeBefore + " to " + defaultTimezones.size()
                    + " for an id that resolves to nothing [" + Ical.abbreviate(id, 200) + "]");
        }

        if (!id.isEmpty()) {
            long t = System.nanoTime();
            try {
                new TzId(id).toZoneId(registry);
            } catch (DateTimeException e) {
                // documented
            }
            try {
                TimeZoneRegistry.getGlobalZoneId(id);
            } catch (DateTimeException e) {
                // documented
            }
            if (!id.isBlank()) {
                // a blank id: the IllegalArgumentException of getTimeZone, allowed
                TzHelper.getCorrectedTimeZoneIdFrom(id);
            }
            ms += (System.nanoTime() - t) / 1_000_000;
        }
        // the first lookups load resources and classes
        if (++executions > 50 && ms > 1000) {
            throw new AssertionError("resolving TZID [" + Ical.abbreviate(id, 200) + "] (" + id.length()
                    + " chars) took " + ms + " ms");
        }
    }
}
