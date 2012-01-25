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

import java.text.ParseException;

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.PropertyTest;

/**
 * $Id$
 *
 * Created on: 24/11/2008
 *
 * @author fortuna
 */
public class PriorityTest extends PropertyTest {

    /**
     * @param property
     * @param expectedValue
     */
    public PriorityTest(Priority priority, String expectedValue) {
        super(priority, expectedValue);
    }

    /**
     * @param testMethod
     * @param property
     */
    public PriorityTest(String testMethod, Priority property) {
        super(testMethod, property);
    }

    /**
     * @return
     * @throws ParseException
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new PriorityTest(Priority.UNDEFINED, "0"));
        suite.addTest(new PriorityTest(Priority.HIGH, "1"));
        suite.addTest(new PriorityTest(Priority.MEDIUM, "5"));
        suite.addTest(new PriorityTest(Priority.LOW, "9"));
        
        suite.addTest(new PriorityTest("testValidation", Priority.UNDEFINED));
        suite.addTest(new PriorityTest("testValidation", Priority.HIGH));
        suite.addTest(new PriorityTest("testValidation", Priority.MEDIUM));
        suite.addTest(new PriorityTest("testValidation", Priority.LOW));
        
        suite.addTest(new PriorityTest("testEquals", Priority.UNDEFINED));
        suite.addTest(new PriorityTest("testEquals", Priority.HIGH));
        suite.addTest(new PriorityTest("testEquals", Priority.MEDIUM));
        suite.addTest(new PriorityTest("testEquals", Priority.LOW));
        
        suite.addTest(new PriorityTest("testImmutable", Priority.UNDEFINED));
        suite.addTest(new PriorityTest("testImmutable", Priority.HIGH));
        suite.addTest(new PriorityTest("testImmutable", Priority.MEDIUM));
        suite.addTest(new PriorityTest("testImmutable", Priority.LOW));
        
        return suite;
    }

}
