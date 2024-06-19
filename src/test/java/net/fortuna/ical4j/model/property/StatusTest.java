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

import static net.fortuna.ical4j.model.property.immutable.ImmutableStatus.*;

/**
 * $Id$
 *
 * Created on: 24/11/2008
 *
 * @author fortuna
 */
public class StatusTest extends PropertyTest {

    /**
     * @param property
     * @param expectedValue
     */
    public StatusTest(Status status, String expectedValue) {
        super(status, expectedValue);
    }

    /**
	 * @param testMethod
	 * @param property
	 */
	public StatusTest(String testMethod, Status property) {
		super(testMethod, property);
	}

	/**
     * @return
     * @throws ParseException
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new StatusTest(VEVENT_CANCELLED, "CANCELLED"));
        suite.addTest(new StatusTest(VEVENT_CONFIRMED, "CONFIRMED"));
        suite.addTest(new StatusTest(VEVENT_TENTATIVE, "TENTATIVE"));
        suite.addTest(new StatusTest(VJOURNAL_CANCELLED, "CANCELLED"));
        suite.addTest(new StatusTest(VJOURNAL_DRAFT, "DRAFT"));
        suite.addTest(new StatusTest(VJOURNAL_FINAL, "FINAL"));
        suite.addTest(new StatusTest(VTODO_CANCELLED, "CANCELLED"));
        suite.addTest(new StatusTest(VTODO_COMPLETED, "COMPLETED"));
        suite.addTest(new StatusTest(VTODO_IN_PROCESS, "IN-PROCESS"));
        suite.addTest(new StatusTest(VTODO_NEEDS_ACTION, "NEEDS-ACTION"));
        
        suite.addTest(new StatusTest("testValidation", VEVENT_CANCELLED));
        suite.addTest(new StatusTest("testValidation", VEVENT_CONFIRMED));
        suite.addTest(new StatusTest("testValidation", VEVENT_TENTATIVE));
        suite.addTest(new StatusTest("testValidation", VJOURNAL_CANCELLED));
        suite.addTest(new StatusTest("testValidation", VJOURNAL_DRAFT));
        suite.addTest(new StatusTest("testValidation", VJOURNAL_FINAL));
        suite.addTest(new StatusTest("testValidation", VTODO_CANCELLED));
        suite.addTest(new StatusTest("testValidation", VTODO_COMPLETED));
        suite.addTest(new StatusTest("testValidation", VTODO_IN_PROCESS));
        suite.addTest(new StatusTest("testValidation", VTODO_NEEDS_ACTION));
        
        suite.addTest(new StatusTest("testEquals", VEVENT_CANCELLED));
        suite.addTest(new StatusTest("testEquals", VEVENT_CONFIRMED));
        suite.addTest(new StatusTest("testEquals", VEVENT_TENTATIVE));
        suite.addTest(new StatusTest("testEquals", VJOURNAL_CANCELLED));
        suite.addTest(new StatusTest("testEquals", VJOURNAL_DRAFT));
        suite.addTest(new StatusTest("testEquals", VJOURNAL_FINAL));
        suite.addTest(new StatusTest("testEquals", VTODO_CANCELLED));
        suite.addTest(new StatusTest("testEquals", VTODO_COMPLETED));
        suite.addTest(new StatusTest("testEquals", VTODO_IN_PROCESS));
        suite.addTest(new StatusTest("testEquals", VTODO_NEEDS_ACTION));
        
        suite.addTest(new StatusTest("testImmutable", VEVENT_CANCELLED));
        suite.addTest(new StatusTest("testImmutable", VEVENT_CONFIRMED));
        suite.addTest(new StatusTest("testImmutable", VEVENT_TENTATIVE));
        suite.addTest(new StatusTest("testImmutable", VJOURNAL_CANCELLED));
        suite.addTest(new StatusTest("testImmutable", VJOURNAL_DRAFT));
        suite.addTest(new StatusTest("testImmutable", VJOURNAL_FINAL));
        suite.addTest(new StatusTest("testImmutable", VTODO_CANCELLED));
        suite.addTest(new StatusTest("testImmutable", VTODO_COMPLETED));
        suite.addTest(new StatusTest("testImmutable", VTODO_IN_PROCESS));
        suite.addTest(new StatusTest("testImmutable", VTODO_NEEDS_ACTION));
        return suite;
    }

}
