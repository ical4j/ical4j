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
package net.fortuna.ical4j.util;

import java.util.Map;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;

/**
 * $Id$
 * 
 * Created on 9/03/2006
 * 
 * A set of keys used to enable compatibility features.
 * 
 * @author Ben Fortuna
 */
public final class CompatibilityHints {

    /**
     * A system property key to enable relaxed unfolding. Relaxed unfolding is enabled by setting this system property
     * to "true".
     */
    public static final String KEY_RELAXED_UNFOLDING = "ical4j.unfolding.relaxed";

    /**
     * A system property key to enable relaxed parsing. Relaxed parsing is enabled by setting this system property to
     * "true".
     */
    public static final String KEY_RELAXED_PARSING = "ical4j.parsing.relaxed";

    /**
     * A system property key to enable relaxed validation. Relaxed validation disables validation of certain conformance
     * rules that many iCalendar implementations do not conform to. Relaxed validation is enabled by setting this system
     * property to "true".
     */
    public static final String KEY_RELAXED_VALIDATION = "ical4j.validation.relaxed";

    /**
     * A system property key used to enable compatibility with Outlook/Exchange-generated iCalendar files. Outlook
     * compatibility is enabled by setting this system property to "true".
     */
    public static final String KEY_OUTLOOK_COMPATIBILITY = "ical4j.compatibility.outlook";

    /**
     * A system property key used to enable compatibility with Lotus Notes-generated iCalendar files. Notes
     * compatibility is enabled by setting this system property to "true".
     */
    public static final String KEY_NOTES_COMPATIBILITY = "ical4j.compatibility.notes";

    private static final Map HINTS = new ConcurrentHashMap();
    // preload known hints from the configurator
    static {
        setHintEnabled(KEY_RELAXED_UNFOLDING, "true".equals(Configurator.getProperty(KEY_RELAXED_UNFOLDING)));
        setHintEnabled(KEY_RELAXED_PARSING, "true".equals(Configurator.getProperty(KEY_RELAXED_PARSING)));
        setHintEnabled(KEY_RELAXED_VALIDATION, "true".equals(Configurator.getProperty(KEY_RELAXED_VALIDATION)));
        setHintEnabled(KEY_OUTLOOK_COMPATIBILITY, "true".equals(Configurator.getProperty(KEY_OUTLOOK_COMPATIBILITY)));
        setHintEnabled(KEY_NOTES_COMPATIBILITY, "true".equals(Configurator.getProperty(KEY_NOTES_COMPATIBILITY)));
    }

    /**
     * Constructor made private to enforce static nature.
     */
    private CompatibilityHints() {
    }

    /**
     * @param key
     *            a compatibility hint key
     * @param enabled
     *            indicates whether to enable or disable the compatibility hint
     */
    public static void setHintEnabled(final String key, final boolean enabled) {
        HINTS.put(key, Boolean.valueOf(enabled));
    }

    /**
     * @param key
     *            a compatibility hint key
     */
    public static void clearHintEnabled(final String key) {
        HINTS.remove(key);
    }

    /**
     * @param key
     *            a compatibility hint key
     * @return true if the specified compatibility hint is enabled, otherwise false
     */
    public static boolean isHintEnabled(final String key) {
        if (HINTS.get(key) != null) {
            return ((Boolean) HINTS.get(key)).booleanValue();
        }
        return "true".equals(Configurator.getProperty(key));
    }
}
