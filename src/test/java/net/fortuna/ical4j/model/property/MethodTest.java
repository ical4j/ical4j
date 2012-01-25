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
public class MethodTest extends PropertyTest {

    /**
     * @param property
     * @param expectedValue
     */
    public MethodTest(Method method, String expectedValue) {
        super(method, expectedValue);
    }

    /**
     * @param testMethod
     * @param property
     */
    public MethodTest(String testMethod, Method property) {
        super(testMethod, property);
    }

    /**
     * @return
     * @throws ParseException
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new MethodTest(Method.ADD, "ADD"));
        suite.addTest(new MethodTest(Method.CANCEL, "CANCEL"));
        suite.addTest(new MethodTest(Method.COUNTER, "COUNTER"));
        suite.addTest(new MethodTest(Method.DECLINE_COUNTER, "DECLINE-COUNTER"));
        suite.addTest(new MethodTest(Method.PUBLISH, "PUBLISH"));
        suite.addTest(new MethodTest(Method.REFRESH, "REFRESH"));
        suite.addTest(new MethodTest(Method.REPLY, "REPLY"));
        suite.addTest(new MethodTest(Method.REQUEST, "REQUEST"));
        
        suite.addTest(new MethodTest("testValidation", Method.ADD));
        suite.addTest(new MethodTest("testValidation", Method.CANCEL));
        suite.addTest(new MethodTest("testValidation", Method.COUNTER));
        suite.addTest(new MethodTest("testValidation", Method.DECLINE_COUNTER));
        suite.addTest(new MethodTest("testValidation", Method.PUBLISH));
        suite.addTest(new MethodTest("testValidation", Method.REFRESH));
        suite.addTest(new MethodTest("testValidation", Method.REPLY));
        suite.addTest(new MethodTest("testValidation", Method.REQUEST));
        
        suite.addTest(new MethodTest("testEquals", Method.ADD));
        suite.addTest(new MethodTest("testEquals", Method.CANCEL));
        suite.addTest(new MethodTest("testEquals", Method.COUNTER));
        suite.addTest(new MethodTest("testEquals", Method.DECLINE_COUNTER));
        suite.addTest(new MethodTest("testEquals", Method.PUBLISH));
        suite.addTest(new MethodTest("testEquals", Method.REFRESH));
        suite.addTest(new MethodTest("testEquals", Method.REPLY));
        suite.addTest(new MethodTest("testEquals", Method.REQUEST));
        
        suite.addTest(new MethodTest("testImmutable", Method.ADD));
        suite.addTest(new MethodTest("testImmutable", Method.CANCEL));
        suite.addTest(new MethodTest("testImmutable", Method.COUNTER));
        suite.addTest(new MethodTest("testImmutable", Method.DECLINE_COUNTER));
        suite.addTest(new MethodTest("testImmutable", Method.PUBLISH));
        suite.addTest(new MethodTest("testImmutable", Method.REFRESH));
        suite.addTest(new MethodTest("testImmutable", Method.REPLY));
        suite.addTest(new MethodTest("testImmutable", Method.REQUEST));
        
        return suite;
    }

}
