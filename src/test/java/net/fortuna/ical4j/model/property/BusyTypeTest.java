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

/**
 * $Id$
 *
 * Created on 16/11/2008
 *
 * @author Ben
 */
public class BusyTypeTest extends PropertyTest {

    /**
     * @param property
     * @param expectedValue
     */
    public BusyTypeTest(BusyType busyType, String expectedValue) {
        super(busyType, expectedValue);
    }

    /**
     * @param testMethod
     * @param property
     */
    public BusyTypeTest(String testMethod, BusyType property) {
        super(testMethod, property);
    }

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new BusyTypeTest(new BusyType("value"), "value"));
        suite.addTest(new BusyTypeTest(BusyType.BUSY, "BUSY"));
        suite.addTest(new BusyTypeTest(BusyType.BUSY_TENTATIVE,
                "BUSY-TENTATIVE"));
        suite.addTest(new BusyTypeTest(BusyType.BUSY_UNAVAILABLE,
                "BUSY-UNAVAILABLE"));

        suite.addTest(new BusyTypeTest("testEquals", BusyType.BUSY));
        suite.addTest(new BusyTypeTest("testEquals", BusyType.BUSY_TENTATIVE));
        suite
                .addTest(new BusyTypeTest("testEquals",
                        BusyType.BUSY_UNAVAILABLE));

        suite.addTest(new BusyTypeTest("testValidation", BusyType.BUSY));
        suite.addTest(new BusyTypeTest("testValidation",
                BusyType.BUSY_TENTATIVE));
        suite.addTest(new BusyTypeTest("testValidation",
                BusyType.BUSY_UNAVAILABLE));

        suite.addTest(new BusyTypeTest("testImmutable", BusyType.BUSY));
        suite
                .addTest(new BusyTypeTest("testImmutable",
                        BusyType.BUSY_TENTATIVE));
        suite.addTest(new BusyTypeTest("testImmutable",
                BusyType.BUSY_UNAVAILABLE));
        return suite;
    }
}
