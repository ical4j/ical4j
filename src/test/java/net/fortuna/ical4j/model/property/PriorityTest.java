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

import static net.fortuna.ical4j.model.property.immutable.ImmutablePriority.*;

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
        suite.addTest(new PriorityTest(UNDEFINED, "0"));
        suite.addTest(new PriorityTest(HIGH, "1"));
        suite.addTest(new PriorityTest(MEDIUM, "5"));
        suite.addTest(new PriorityTest(LOW, "9"));
        
        suite.addTest(new PriorityTest("testValidation", UNDEFINED));
        suite.addTest(new PriorityTest("testValidation", HIGH));
        suite.addTest(new PriorityTest("testValidation", MEDIUM));
        suite.addTest(new PriorityTest("testValidation", LOW));
        
        suite.addTest(new PriorityTest("testEquals", UNDEFINED));
        suite.addTest(new PriorityTest("testEquals", HIGH));
        suite.addTest(new PriorityTest("testEquals", MEDIUM));
        suite.addTest(new PriorityTest("testEquals", LOW));
        
        suite.addTest(new PriorityTest("testImmutable", UNDEFINED));
        suite.addTest(new PriorityTest("testImmutable", HIGH));
        suite.addTest(new PriorityTest("testImmutable", MEDIUM));
        suite.addTest(new PriorityTest("testImmutable", LOW));
        
        return suite;
    }

}
