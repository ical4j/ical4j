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
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import junit.framework.TestSuite;

import net.fortuna.ical4j.model.PropertyTest;

/**
 * Created on 16/03/2005
 *
 * $Id$
 *
 * @author Ben
 *
 * Tests related to the property VERSION
 */
public class VersionTest extends PropertyTest {

    private Version version;
    
    /**
     * @param property
     * @param expectedValue
     */
    public VersionTest(Version property, String expectedValue) {
        super(property, expectedValue);
        this.version = property;
    }

    /**
     * @param testMethod
     * @param property
     */
    public VersionTest(String testMethod, Version property) {
        super(testMethod, property);
        this.version = property;
    }

    /*
     * Test that the constant VERSION_2_0 is immutable.
     */
    public void testImmutable() throws IOException, URISyntaxException, ParseException {
        super.testImmutable();
        
        try {
            version.setMinVersion("3.0");
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
        
        try {
            version.setMaxVersion("5.0");
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
    }

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new VersionTest(Version.VERSION_2_0, "2.0"));
        suite.addTest(new VersionTest("testImmutable", Version.VERSION_2_0));
        suite.addTest(new VersionTest("testValidation", Version.VERSION_2_0));
        suite.addTest(new VersionTest("testEquals", Version.VERSION_2_0));
        return suite;
    }
}
