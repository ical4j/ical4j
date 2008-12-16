/**
 * Copyright (c) 2008, Ben Fortuna
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

import junit.framework.TestCase;

/**
 * $Id$ [17-Jan-2005]
 *
 * Unit test for StringUtils.
 * @author Chris Borrill
 */
public class StringsTest extends TestCase {
    
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

    public void testEscapeSemiColon() {
        assertEquals("EscapeSemiColon", ESCAPED_SEMI_COLON, 
            Strings.escape(SEMI_COLON));
    }

    public void testUnEscapeSemiColon() {
        assertEquals("UnEscapeSemiColon", SEMI_COLON, 
            Strings.unescape(ESCAPED_SEMI_COLON));
    }

    public void testEscapeComma() {
        assertEquals("EscapeComma", ESCAPED_COMMA , Strings.escape(COMMA));
    }

    public void testUnEscapeComma() {
        assertEquals("UnEscapeComma", COMMA, Strings.unescape(ESCAPED_COMMA));
    }

    /*
    public void testEscapeQuote() {
        assertEquals("EscapeQuote", ESCAPED_QUOTE, Strings.escape(QUOTE));
    }
    */

    /**
     * Test un-escaping of quotes (not part of spec, but remains for
     * backwards compatibility.
     */
    public void testUnEscapeQuote() {
        assertEquals("UnEscapeQuote", QUOTE, Strings.unescape(ESCAPED_QUOTE));
    }

    public void testEscapeDoubleBackSlash() {
        assertEquals("EscapeDoubleBackSlash", ESCAPED_DOUBLE_BACKSLASH, 
            Strings.escape(DOUBLE_BACKSLASH));
    }

    public void testUnEscapeDoubleBackSlash() {
        assertEquals("UnEscapeDoubleBackSlash", DOUBLE_BACKSLASH, 
            Strings.unescape(ESCAPED_DOUBLE_BACKSLASH));
    }    

    public void testEscapeNewline() {
        assertEquals("EscapeNewline", ESCAPED_NEWLINE, Strings.escape(NEWLINE));
    }

    public void testUnEscapeNewline() {
        assertEquals("UnEscapeNewline", NEWLINE, Strings.unescape(ESCAPED_NEWLINE));
    }

    /**
     * Unit testing of quotable parameter value strings.
     */
    public void testQuotableParamString() {
        assertFalse(Strings.PARAM_QUOTE_PATTERN.matcher("").find());
        assertTrue(Strings.PARAM_QUOTE_PATTERN.matcher(":").find());
        assertTrue(Strings.PARAM_QUOTE_PATTERN.matcher(";").find());
        assertTrue(Strings.PARAM_QUOTE_PATTERN.matcher(",").find());
        assertTrue(Strings.PARAM_QUOTE_PATTERN.matcher(
                "Pacific Time (US & Canada), Tijuana").find());
    }
}
