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

import java.net.URISyntaxException;
import junit.framework.TestSuite;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Uid;

/**
 * $Id$
 *
 * Created on: 25/11/2008
 *
 * @author fortuna
 */
public class VToDoTest extends CalendarComponentTest {

    /**
     * @param testMethod
     * @param component
     */
    public VToDoTest(String testMethod, VToDo component) {
        super(testMethod, component);
    }

    /**
     * @return
     */
    public static TestSuite suite() throws URISyntaxException {
        TestSuite suite = new TestSuite();

        VToDo td = new VToDo();
        suite.addTest(new VToDoTest("testIsCalendarComponent", td));

        // iCalendar validation
        suite.addTest(new VToDoTest("testValidationException", td));
        VToDo validTd = new VToDo();
        validTd.getProperties().add(new Uid("12"));
        suite.addTest(new VToDoTest("testValidation", validTd));

        // iTIP REPLY validation
        suite.addTest(new VToDoTest("testReplyValidationException", new VToDo()));
        VToDo replyTd = new VToDo();
        replyTd.getProperties().add(new Attendee("mailto:jane@example.com"));
        replyTd.getProperties().add(new Organizer("mailto:joe@example.com"));
        replyTd.getProperties().add(new Uid("12"));
        suite.addTest(new VToDoTest("testReplyValidation", replyTd));

        return suite;
    }

}
