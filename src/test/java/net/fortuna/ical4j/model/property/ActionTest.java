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

import static net.fortuna.ical4j.model.property.immutable.ImmutableAction.*;

/**
 * Created: [19/11/2008]
 *
 * @author fortuna
 */
public class ActionTest extends PropertyTest {

    /**
     * @param property
     * @param expectedValue
     */
    public ActionTest(Action action, String expectedValue) {
        super(action, expectedValue);
    }

    /**
     * @param testMethod
     * @param property
     */
    public ActionTest(String testMethod, Action property) {
        super(testMethod, property);
    }

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ActionTest(AUDIO, "AUDIO"));
        suite.addTest(new ActionTest(DISPLAY, "DISPLAY"));
        suite.addTest(new ActionTest(EMAIL, "EMAIL"));
        suite.addTest(new ActionTest(PROCEDURE, "PROCEDURE"));

        suite.addTest(new ActionTest("testEquals", AUDIO));
        suite.addTest(new ActionTest("testEquals", DISPLAY));
        suite.addTest(new ActionTest("testEquals", EMAIL));
        suite.addTest(new ActionTest("testEquals", PROCEDURE));

        suite.addTest(new ActionTest("testValidation", AUDIO));
        suite.addTest(new ActionTest("testValidation", DISPLAY));
        suite.addTest(new ActionTest("testValidation", EMAIL));
        suite.addTest(new ActionTest("testValidation", PROCEDURE));

        suite.addTest(new ActionTest("testImmutable", AUDIO));
        suite.addTest(new ActionTest("testImmutable", DISPLAY));
        suite.addTest(new ActionTest("testImmutable", EMAIL));
        suite.addTest(new ActionTest("testImmutable", PROCEDURE));
        return suite;
    }
}
