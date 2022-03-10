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

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.ComponentTest;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.parameter.FmtType;
import net.fortuna.ical4j.model.property.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Collections;

/**
 * $Id$
 *
 * Created on 8/02/2006
 *
 * Unit tests for VAlarm component.
 * @author Ben Fortuna
 */
public class VAlarmTest extends ComponentTest {

    /**
     * @param component
     */
    public VAlarmTest(String testMethod, VAlarm component) {
        super(testMethod, component);
    }
    
    /**
     * @return
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws ParseException 
     */
    public static TestSuite suite() throws URISyntaxException {
        TestSuite suite = new TestSuite();
        
        VAlarm alarm = new VAlarm();
        alarm.add(new Trigger(Instant.now()));
        
        suite.addTest(new VAlarmTest("testIsCalendarComponent", alarm));
//        suite.addTest(new VAlarmTest("testValidationException", alarm));

        alarm = alarm.copy();
        alarm.add(Action.DISPLAY).add(new Description("Testing display"));
        suite.addTest(new VAlarmTest("testValidation", alarm));
        
        // Test duration/repeat validation..
        alarm = new VAlarm(java.time.Duration.ofHours(2));
        alarm.add(Action.DISPLAY)
                .add(new Description("Testing display"));
        Duration duration = new Duration(java.time.Duration.ofMinutes(2));
        alarm.add(duration);
//        suite.addTest(new VAlarmTest("testValidationException", alarm));
        
        alarm = alarm.copy();
        alarm.add(new Repeat(2));
        suite.addTest(new VAlarmTest("testValidation", alarm));
        
        alarm = alarm.copy();
        alarm.remove(duration);
//        suite.addTest(new VAlarmTest("testValidationException", alarm));
        
        //testValidationEmail..
        alarm = new VAlarm(java.time.Duration.ofDays(-2));
        alarm.add(Action.EMAIL)
                .add(new Attendee("mailto:john_doe@example.com"))
                .add(new Summary("*** REMINDER: SEND AGENDA FOR WEEKLY STAFF MEETING ***"))
                .add(new Description("A draft agenda needs to be sent out to the attendees "
                    + "to the weekly managers meeting (MGR-LIST). Attached is a " 
                    + "pointer the document template for the agenda file.")).getFluentTarget();

        ParameterList attachParams = new ParameterList(Collections.singletonList(
                new FmtType("application/msword")));
        Attach attachment = new Attach(attachParams,
                new URI("http://example.com/templates/agenda.doc"));
        alarm.add(attachment);
        suite.addTest(new VAlarmTest("testValidation", alarm));
        
        return suite;
    }
}
