/**
 * Copyright (c) 2009, Ben Fortuna
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

import java.util.regex.Pattern;

/**
 * $Id$ [23-Apr-2004]
 *
 * Utility methods for working with parameters.
 * @author Ben Fortuna
 * <pre>
 * 4.3.11 Text
 * 
 *    Value Name: TEXT
 * 
 *    Purpose This value type is used to identify values that contain human
 *    readable text.
 * 
 *    Formal Definition: The character sets supported by this revision of
 *    iCalendar are UTF-8 and US ASCII thereof. The applicability to other
 *    character sets is for future work. The value type is defined by the
 *    following notation.
 * 
 *      text       = *(TSAFE-CHAR / ":" / DQUOTE / ESCAPED-CHAR)
 *      ; Folded according to description above
 * 
 *      ESCAPED-CHAR = "\\" / "\;" / "\," / "\N" / "\n")
 *         ; \\ encodes \, \N or \n encodes newline
 *         ; \; encodes ;, \, encodes ,
 * 
 *      TSAFE-CHAR = %x20-21 / %x23-2B / %x2D-39 / %x3C-5B
 *                   %x5D-7E / NON-US-ASCII
 *         ; Any character except CTLs not needed by the current
 *         ; character set, DQUOTE, ";", ":", "\", ","
 * 
 *      Note: Certain other character sets may require modification of the
 *      above definitions, but this is beyond the scope of this document.
 * 
 *    Description: If the property permits, multiple "text" values are
 *    specified by a COMMA character (US-ASCII decimal 44) separated list
 *    of values.
 * 
 *    The language in which the text is represented can be controlled by
 *    the "LANGUAGE" property parameter.
 * 
 *    An intentional formatted text line break MUST only be included in a
 *    "TEXT" property value by representing the line break with the
 *    character sequence of BACKSLASH (US-ASCII decimal 92), followed by a
 *    LATIN SMALL LETTER N (US-ASCII decimal 110) or a LATIN CAPITAL LETTER
 *    N (US-ASCII decimal 78), that is "\n" or "\N".
 * 
 *    The "TEXT" property values may also contain special characters that
 *    are used to signify delimiters, such as a COMMA character for lists
 *    of values or a SEMICOLON character for structured values. In order to
 *    support the inclusion of these special characters in "TEXT" property
 *    values, they MUST be escaped with a BACKSLASH character. A BACKSLASH
 *    character (US-ASCII decimal 92) in a "TEXT" property value MUST be
 *    escaped with another BACKSLASH character. A COMMA character in a
 *    "TEXT" property value MUST be escaped with a BACKSLASH character
 *    (US-ASCII decimal 92). A SEMICOLON character in a "TEXT" property
 *    value MUST be escaped with a BACKSLASH character (US-ASCII decimal
 *    92).  However, a COLON character in a "TEXT" property value SHALL NOT
 *    be escaped with a BACKSLASH character.Example: A multiple line value
 *    of:
 * 
 *      Project XYZ Final Review
 *      Conference Room - 3B
 *      Come Prepared.
 * 
 *    would be represented as:
 * 
 *      Project XYZ Final Review\nConference Room - 3B\nCome Prepared.
 * </pre>
 */
public final class Strings {

//    private static final Pattern CHECK_ESCAPE = Pattern.compile("[,;\"\n\\\\]");
    private static final Pattern CHECK_ESCAPE = Pattern.compile("[,;\n\\\\]");

    private static final Pattern CHECK_UNESCAPE = Pattern.compile("\\\\");

//    private static final Pattern ESCAPE_PATTERN_1 =
//        Pattern.compile("([,;\"])");
    private static final Pattern ESCAPE_PATTERN_1 =
        Pattern.compile("([,;])");

    private static final Pattern ESCAPE_PATTERN_2 =
        Pattern.compile("\r?\n");

    private static final Pattern ESCAPE_PATTERN_3 =
        Pattern.compile("\\\\");

    // include escaped quotes for backwards compatibility..
    private static final Pattern UNESCAPE_PATTERN_1 =
        Pattern.compile("\\\\([,;\"])");
//    private static final Pattern UNESCAPE_PATTERN_1 =
//        Pattern.compile("\\\\([,;])");

    private static final Pattern UNESCAPE_PATTERN_2 =
        Pattern.compile("\\\\n", Pattern.CASE_INSENSITIVE);

    private static final Pattern UNESCAPE_PATTERN_3 =
        Pattern.compile("\\\\\\\\");

    /**
     * Defines a regular expression representing all parameter strings that
     * should be quoted.
     */
    public static final Pattern PARAM_QUOTE_PATTERN = Pattern.compile("[:;,]");
    
    /**
     * A string used to denote the start (and end) of iCalendar content lines.
     */
    public static final String LINE_SEPARATOR = "\r\n";

    /**
     * Constructor made private to prevent instantiation.
     */
    private Strings() {
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
