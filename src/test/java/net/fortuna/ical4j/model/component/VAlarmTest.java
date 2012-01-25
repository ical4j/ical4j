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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.ComponentTest;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.parameter.FmtType;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Trigger;

/**
 * $Id$
 *
 * Created on 8/02/2006
 *
 * Unit tests for VAlarm component.
 * @author Ben Fortuna
 */
public class VAlarmTest extends ComponentTest {
    
    private VAlarm alarm;
    
    /**
     * @param component
     */
    public VAlarmTest(String testMethod, VAlarm component) {
        super(testMethod, component);
        this.alarm = component;
    }
    
    /**
     * @return
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws ParseException 
     */
    public static TestSuite suite() throws ParseException, IOException, URISyntaxException {
        TestSuite suite = new TestSuite();
        
        VAlarm alarm = new VAlarm();
        alarm.getProperties().add(new Trigger(new DateTime(System.currentTimeMillis())));
        
        suite.addTest(new VAlarmTest("testIsCalendarComponent", alarm));
        suite.addTest(new VAlarmTest("testValidationException", alarm));

        alarm = (VAlarm) alarm.copy();
        alarm.getProperties().add(Action.DISPLAY);
        alarm.getProperties().add(new Description("Testing display"));
        suite.addTest(new VAlarmTest("testValidation", alarm));
        
        // Test duration/repeat validation..
        alarm = new VAlarm(new Dur(0, 2, 0, 0));
        alarm.getProperties().add(Action.DISPLAY);
        alarm.getProperties().add(new Description("Testing display"));
        Duration duration = new Duration(new Dur(0, 0, 2, 0));
        alarm.getProperties().add(duration);
        suite.addTest(new VAlarmTest("testValidationException", alarm));
        
        alarm = (VAlarm) alarm.copy();
        alarm.getProperties().add(new Repeat(2));
        suite.addTest(new VAlarmTest("testValidation", alarm));
        
        alarm = (VAlarm) alarm.copy();
        alarm.getProperties().remove(duration);
        suite.addTest(new VAlarmTest("testValidationException", alarm));
        
        //testValidationEmail..
        alarm = new VAlarm(new Dur(-2, 0, 0, 0));
        alarm.getProperties().add(Action.EMAIL);
        alarm.getProperties().add(new Attendee("mailto:john_doe@example.com"));
        alarm.getProperties().add(new Summary("*** REMINDER: SEND AGENDA FOR WEEKLY STAFF MEETING ***"));
        alarm.getProperties().add(new Description("A draft agenda needs to be sent out to the attendees " 
                    + "to the weekly managers meeting (MGR-LIST). Attached is a " 
                    + "pointer the document template for the agenda file."));

        Attach attachment = new Attach(new URI("http://example.com/templates/agenda.doc"));
        attachment.getParameters().add(new FmtType("application/msword"));
        alarm.getProperties().add(attachment);
        suite.addTest(new VAlarmTest("testValidation", alarm));
        
        return suite;
    }
}
