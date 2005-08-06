/*
 * $Id$ [23-Apr-2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.util;

import java.util.regex.Pattern;

/**
 * Utility methods for working with parameters.
 * 
 * @author Ben Fortuna
 */
public final class StringUtils {

    private static final Pattern CHECK_ESCAPE = Pattern.compile("[,;\"\n\\\\]");

    private static final Pattern CHECK_UNESCAPE = Pattern.compile("\\\\");

    private static final Pattern ESCAPE_PATTERN_1 =
        Pattern.compile("([,;\"])");

    private static final Pattern ESCAPE_PATTERN_2 =
        Pattern.compile("[\r\n]+");

    private static final Pattern ESCAPE_PATTERN_3 =
        Pattern.compile("\\\\");

    private static final Pattern UNESCAPE_PATTERN_1 =
        Pattern.compile("\\\\([,;\"])");

    private static final Pattern UNESCAPE_PATTERN_2 =
        Pattern.compile("\\\\n", Pattern.CASE_INSENSITIVE);

    private static final Pattern UNESCAPE_PATTERN_3 =
        Pattern.compile("\\\\\\\\");

    /**
     * Constructor made private to prevent instantiation.
     */
    private StringUtils() {
    }

    /**
     * Convenience method for adding quotes. The specified
     * object is converted to a string representation by
     * calling its <code>toString()</code> method.
     * @param aValue an object to quote
     * @return a quoted string
     */
    public static String quote(final Object aValue) {
        if (aValue != null) {
            return "\"" + aValue + "\"";
        }

        return "\"\"";
    }

    /**
     * Convenience method for removing surrounding quotes
     * from a string value.
     * @param aValue a string to remove quotes from
     * @return an un-quoted string
     */
    public static String unquote(final String aValue) {
        if (aValue != null && aValue.startsWith("\"") && aValue.endsWith("\"")) {
            return aValue.substring(0, aValue.length() - 1).substring(1);
        }

        return aValue;
    }

    /**
     * Convenience method for escaping special characters.
     * @param aValue a string value to escape
     * @return an escaped representation of the specified
     * string
     */
    public static String escape(final String aValue) {
        if (aValue != null && CHECK_ESCAPE.matcher(aValue).find()) {
            return ESCAPE_PATTERN_1.matcher(
                    ESCAPE_PATTERN_2.matcher(
                            ESCAPE_PATTERN_3.matcher(aValue).replaceAll("\\\\\\\\"))
                        .replaceAll("\\\\n"))
                .replaceAll("\\\\$1");
/*
            return aValue.replaceAll("\\\\", "\\\\\\\\")
                            .replaceAll(";", "\\\\;")
                            .replaceAll(",", "\\\\,")
                            .replaceAll("\n", "\\\\n")
                            .replaceAll("\"", "\\\\\"");
*/
        }

        return aValue;
    }

    /**
     * Convenience method for replacing escaped special characters
     * with their original form.
     * @param aValue a string value to unescape
     * @return a string representation of the specified
     * string with escaped characters replaced with their
     * original form
     */
    public static String unescape(final String aValue) {
        if (aValue != null && CHECK_UNESCAPE.matcher(aValue).find()) {
            return UNESCAPE_PATTERN_3.matcher(
                    UNESCAPE_PATTERN_2.matcher(
                            UNESCAPE_PATTERN_1.matcher(aValue).replaceAll("$1"))
                        .replaceAll("\n"))
                .replaceAll("\\\\");
/*
            return aValue.replaceAll("\\\\\"", "\"")
                            .replaceAll("\\\\N", "\n")
                            .replaceAll("\\\\n", "\n")
                            .replaceAll("\\\\,", ",")
                            .replaceAll("\\\\;", ";")
                            .replaceAll("\\\\\\\\", "\\\\");
*/
        }

        return aValue;
    }
    
    /**
     * Wraps <code>java.lang.String.valueOf()</code> to return an empty string
     * where the specified object is null.
     * @param object
     * @return
     */
    public static String valueOf(final Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }
}
