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
package net.fortuna.ical4j.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * $Id$
 *
 * Created on 09/11/2008
 *
 * @author Ben
 *
 */
public class UnfoldingReaderTest extends TestCase {

    private static final int BUFFER_SIZE = 1024;
    
    private UnfoldingReader reader;
    
    /**
     * @param input
     */
    public UnfoldingReaderTest(String input) {
        this(new UnfoldingReader(new StringReader(input), BUFFER_SIZE));
    }
    
    /**
     * @param reader
     */
    public UnfoldingReaderTest(UnfoldingReader reader) {
        super("testUnfolding");
        this.reader = reader;
    }
    
    /**
     * @throws IOException
     */
    public void testUnfolding() throws IOException {
        BufferedReader b = new BufferedReader(reader, BUFFER_SIZE);
        String line = null;
        while ((line = b.readLine()) != null) {
            assertFalse(line.matches("^\\s.*"));
        }
    }
    
    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new UnfoldingReaderTest("a\r\n bc"));
        return suite;
    }
}
