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

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * $Id$ [17-Jan-2005]
 *
 * Unit test for StringUtils.
 * @author Chris Borrill
 */
public class UrisTest extends TestCase {

    private final String testString;
    
    private final String expectedValue;
    
    public UrisTest(String testString) {
        this(testString, testString);
    }
    
    public UrisTest(String testString, String expectedValue) {
        super("testCreateUri");
        this.testString = testString;
        this.expectedValue = expectedValue;
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_PARSING, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
    }
    
    protected void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);
        super.tearDown();
    }
    
    public void testCreateUri() throws Exception {
        assertEquals("create failed", expectedValue, Uris.create(testString).toString());
    }
    
    public String getName() {
        if (testString != null) {
            return super.getName() + " [" + testString + "]";
        }
        return super.getName();
    }
    
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new UrisTest("mailto:joe@example.com"));
        suite.addTest(new UrisTest("mailto:ga\u00eblle@example.com"));
        suite.addTest(new UrisTest("mailto:joe+titi@example.com"));
        suite.addTest(new UrisTest("mailto:joe%titi@example.com", "mailto:joe%25titi@example.com"));
        suite.addTest(new UrisTest("mailto:jack jill@example.com", "mailto:jack%20jill@example.com"));
        suite.addTest(new UrisTest("sms:caluser2@example.com,%20mailto:caluser2@example.com"));
        suite.addTest(new UrisTest("toto"));
        suite.addTest(new UrisTest(":toto", Uris.INVALID_SCHEME + ":" + ":toto"));
        suite.addTest(new UrisTest("toto:", Uris.INVALID_SCHEME + ":" + "toto:"));

        return suite;
    }
}
