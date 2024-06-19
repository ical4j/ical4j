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

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.PropertyTest;

import java.text.ParseException;

import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.*;

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
        suite.addTest(new MethodTest(ADD, "ADD"));
        suite.addTest(new MethodTest(CANCEL, "CANCEL"));
        suite.addTest(new MethodTest(COUNTER, "COUNTER"));
        suite.addTest(new MethodTest(DECLINE_COUNTER, "DECLINECOUNTER"));
        suite.addTest(new MethodTest(PUBLISH, "PUBLISH"));
        suite.addTest(new MethodTest(REFRESH, "REFRESH"));
        suite.addTest(new MethodTest(REPLY, "REPLY"));
        suite.addTest(new MethodTest(REQUEST, "REQUEST"));
        
        suite.addTest(new MethodTest("testValidation", ADD));
        suite.addTest(new MethodTest("testValidation", CANCEL));
        suite.addTest(new MethodTest("testValidation", COUNTER));
        suite.addTest(new MethodTest("testValidation", DECLINE_COUNTER));
        suite.addTest(new MethodTest("testValidation", PUBLISH));
        suite.addTest(new MethodTest("testValidation", REFRESH));
        suite.addTest(new MethodTest("testValidation", REPLY));
        suite.addTest(new MethodTest("testValidation", REQUEST));
        
        suite.addTest(new MethodTest("testEquals", ADD));
        suite.addTest(new MethodTest("testEquals", CANCEL));
        suite.addTest(new MethodTest("testEquals", COUNTER));
        suite.addTest(new MethodTest("testEquals", DECLINE_COUNTER));
        suite.addTest(new MethodTest("testEquals", PUBLISH));
        suite.addTest(new MethodTest("testEquals", REFRESH));
        suite.addTest(new MethodTest("testEquals", REPLY));
        suite.addTest(new MethodTest("testEquals", REQUEST));
        
        suite.addTest(new MethodTest("testImmutable", ADD));
        suite.addTest(new MethodTest("testImmutable", CANCEL));
        suite.addTest(new MethodTest("testImmutable", COUNTER));
        suite.addTest(new MethodTest("testImmutable", DECLINE_COUNTER));
        suite.addTest(new MethodTest("testImmutable", PUBLISH));
        suite.addTest(new MethodTest("testImmutable", REFRESH));
        suite.addTest(new MethodTest("testImmutable", REPLY));
        suite.addTest(new MethodTest("testImmutable", REQUEST));
        
        return suite;
    }

}
