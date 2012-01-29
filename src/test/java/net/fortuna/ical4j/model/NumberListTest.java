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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * $Id$
 *
 * Created on 6/08/2005
 *
 * @author Ben
 *
 */
public class NumberListTest extends TestCase {
    
	private static final Log LOG = LogFactory.getLog(NumberListTest.class);
	
    private NumberList numberList;
    
    private int expectedSize;
    
    private String expectedString;
    
    private Integer validNumber;
    
    private Integer invalidNumber;
    
    /**
     * @param numberList
     * @param expectedSize
     */
    public NumberListTest(NumberList numberList, int expectedSize) {
        super("testSize");
        this.numberList = numberList;
        this.expectedSize = expectedSize;
    }
    
    /**
     * @param numberList
     * @param expectedString
     */
    public NumberListTest(NumberList numberList, String expectedString) {
        super("testToString");
        this.numberList = numberList;
        this.expectedString = expectedString;
    }
    
    /**
     * @param list
     * @param validNumber
     * @param invalidNumber
     */
    public NumberListTest(NumberList list, Integer validNumber, Integer invalidNumber) {
    	super("testBounds");
    	this.numberList = list;
    	this.validNumber = validNumber;
    	this.invalidNumber = invalidNumber;
    }
    
    public void testSize() {
        assertEquals(expectedSize, numberList.size());
    }
    
    public void testToString() {
        assertEquals(expectedString, numberList.toString());
    }
    
    public void testBounds() {
		numberList.add(validNumber);
    	try {
    		numberList.add(invalidNumber);
    	}
    	catch (IllegalArgumentException e) {
    		LOG.debug("Caught exception: " + e);
    	}
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return super.getName() + " [" + numberList + "]";
    }
    
    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new NumberListTest(new NumberList("1,1,2,4,5"), 5));
        suite.addTest(new NumberListTest(new NumberList("1,1,2,4,5"), "1,1,2,4,5"));

        suite.addTest(new NumberListTest(new NumberList("-9,-2,-3,3,5,6"), 6));
        suite.addTest(new NumberListTest(new NumberList("-9,-2,-3,3,5,6"), "-9,-2,-3,3,5,6"));
        
        suite.addTest(new NumberListTest(new NumberList("0,2,5,-2,-4,-5,+3"), 7));
        suite.addTest(new NumberListTest(new NumberList("0,2,5,-2,-4,-5,+3"), "0,2,5,-2,-4,-5,3"));
        suite.addTest(new NumberListTest(new NumberList("0,2,5,-2,-4,-5,+3", 0, 5, true), "0,2,5,-2,-4,-5,3"));
        
        suite.addTest(new NumberListTest(new NumberList(0, 1, false), new Integer(0), new Integer(-1)));
        suite.addTest(new NumberListTest(new NumberList("1", 0, 1, true), new Integer(0), new Integer(2)));
        
        return suite;
    }
}
