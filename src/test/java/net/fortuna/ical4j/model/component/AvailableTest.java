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
package net.fortuna.ical4j.model.component;

import java.net.SocketException;

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.ComponentTest;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.util.UidGenerator;

/**
 * $Id$
 *
 * Created on: 25/11/2008
 *
 * @author fortuna
 */
public class AvailableTest extends ComponentTest {

    /**
     * @param component
     */
    public AvailableTest(String testMethod, Available component) {
        super(testMethod, component);
    }

    /**
     * @return
     * @throws SocketException 
     */
    public static TestSuite suite() throws SocketException {
        TestSuite suite = new TestSuite();

        Available a = new Available();
        suite.addTest(new AvailableTest("testIsNotCalendarComponent", a));
        suite.addTest(new AvailableTest("testValidationException", a));
        
        UidGenerator g = new UidGenerator("test");
        a = new Available();
        a.getProperties().add(g.generateUid());
        a.getProperties().add(new DtStart(new DateTime()));
        a.getProperties().add(new DtStamp());
        a.getProperties().add(new Duration(new Dur(1)));
        suite.addTest(new AvailableTest("testValidation", a));
        return suite;
    }
}
