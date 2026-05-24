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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * $Id$ [17-Jan-2005]
 *
 * Unit test for StringUtils.
 * @author Chris Borrill
 */
public class StringsTest {

    private static final String SEMI_COLON = ";XXX;";
    private static final String ESCAPED_SEMI_COLON = "\\;XXX\\;";
    
    private static final String COMMA = ",XXX,";
    private static final String ESCAPED_COMMA = "\\,XXX\\,";

    private static final String QUOTE = "\"XXX\"";
    private static final String ESCAPED_QUOTE = "\\\"XXX\\\"";

    private static final String DOUBLE_BACKSLASH = "\\\\XXX\\\\";
    private static final String ESCAPED_DOUBLE_BACKSLASH = "\\\\\\\\XXX\\\\\\\\";
    
    private static final String NEWLINE = "\nXXX\n\n";
    private static final String ESCAPED_NEWLINE = "\\nXXX\\n\\n";

    @ParameterizedTest(name = "escape/unescape: {0}")
    @MethodSource("escapeUnescapeTestData")
    void testEscapeUnescape(String testString, String expectedValue) {
        final String value = Strings.escape(testString);
        assertEquals(expectedValue, value, "Escape failed");
        assertEquals(testString, Strings.unescape(value), "Unescape failed");
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> escapeUnescapeTestData() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(SEMI_COLON, ESCAPED_SEMI_COLON),
            org.junit.jupiter.params.provider.Arguments.of(COMMA, ESCAPED_COMMA),
            org.junit.jupiter.params.provider.Arguments.of(DOUBLE_BACKSLASH, ESCAPED_DOUBLE_BACKSLASH),
            org.junit.jupiter.params.provider.Arguments.of(NEWLINE, ESCAPED_NEWLINE),
            org.junit.jupiter.params.provider.Arguments.of("a\\nb", "a\\\\nb")
        );
    }

    /**
     * Test un-escaping of quotes (not part of spec, but remains for
     * backwards compatibility.
     */
    @Test
    void testUnEscapeQuote() {
        assertEquals(QUOTE, Strings.unescape(ESCAPED_QUOTE), "UnEscapeQuote");
    }

    /**
     * Unit testing of quotable parameter value strings.
     */
    @Test
    void testQuotableParamString() {
        assertFalse(Strings.PARAM_QUOTE_PATTERN.matcher("").find());
        assertTrue(Strings.PARAM_QUOTE_PATTERN.matcher(":").find());
        assertTrue(Strings.PARAM_QUOTE_PATTERN.matcher(";").find());
        assertTrue(Strings.PARAM_QUOTE_PATTERN.matcher(",").find());
        assertTrue(Strings.PARAM_QUOTE_PATTERN.matcher(
                "Pacific Time (US & Canada), Tijuana").find());
    }
}
