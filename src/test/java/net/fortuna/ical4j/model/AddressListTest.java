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
package net.fortuna.ical4j.model;

import java.net.URISyntaxException;

import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * $Id$
 *
 * Created on 16/11/2005
 *
 */
public class AddressListTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(AddressListTest.class);
    
    private String value;
    
    private int expectedSize;
    
    private String[] compatibilityHints;
    
    /**
     * @param testMethod
     * @param value
     */
    public AddressListTest(String testMethod, String value, String[] compatibilityHints) {
    	this(testMethod, value, -1, compatibilityHints);
    }
    
    /**
     * @param testMethod
     * @param value
     * @param expectedSize
     */
    public AddressListTest(String testMethod, String value, int expectedSize, String[] compatibilityHints) {
    	super(testMethod);
    	this.expectedSize = expectedSize;
    	this.value = value;
    	this.compatibilityHints = compatibilityHints;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
    	for (int i = 0; i < compatibilityHints.length; i++) {
    		CompatibilityHints.setHintEnabled(compatibilityHints[i], true);
    	}
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
    	for (int i = 0; i < compatibilityHints.length; i++) {
    		CompatibilityHints.clearHintEnabled(compatibilityHints[i]);
    	}
    }
    
    /**
     * Assert three addresses parsed from value.
     * @throws URISyntaxException 
     */
    public void testSize() throws URISyntaxException {
    	AddressList addresses = new AddressList(value);
        assertEquals(expectedSize, addresses.size());
    }
    
    /**
     * Assert toString() produces identical address list string value.
     * @throws URISyntaxException 
     */
    public void testToString() throws URISyntaxException {
    	AddressList addresses = new AddressList(value);
        assertEquals(value, addresses.toString());
    }
    
    /**
     * Test invalid addresses are correctly handled.
     */
    public void testInvalidAddressList() throws URISyntaxException {
        try {
            new AddressList(value);
            fail("Should throw URISyntaxException");
        }
        catch (URISyntaxException use) {
            LOG.info("Caught exception: " + use.getMessage());
        }
    }
    
    /**
     * @return
     * @throws URISyntaxException
     */
    public static TestSuite suite() throws URISyntaxException {
    	TestSuite suite = new TestSuite();
        
        String value = "\"address1@example.com\",\"address2@example.com\",\"address3@example.com\"";
    	suite.addTest(new AddressListTest("testSize", value, 3, new String[] {}));
    	suite.addTest(new AddressListTest("testToString", value, new String[] {}));
    	
        value = "address1@example.com,<address2@example.com>,address3@example.com";
    	suite.addTest(new AddressListTest("testInvalidAddressList", value, new String[] {}));
    	suite.addTest(new AddressListTest("testSize", value, 2, new String[] {CompatibilityHints.KEY_RELAXED_PARSING}));
    	// Test broken when NOTES_COMPATIBILITY made more specific.. not sure this is a real-world case tho..
//    	suite.addTest(new AddressListTest("testSize", value, 3, new String[] {CompatibilityHints.KEY_NOTES_COMPATIBILITY}));
    	return suite;
    }
}
