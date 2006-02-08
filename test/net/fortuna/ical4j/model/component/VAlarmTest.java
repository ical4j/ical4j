/*
 * $Id$
 *
 * Created on 8/02/2006
 *
 * Copyright (c) 2006, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.ComponentTest;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.Trigger;

/**
 * Unit tests for VAlarm component.
 * @author Ben Fortuna
 */
public class VAlarmTest extends ComponentTest {
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.ComponentTest#testIsCalendarComponent()
     */
    public void testIsCalendarComponent() {
        assertIsCalendarComponent(new VAlarm());
    }
    
    /**
     * Test component validation.
     * @throws ValidationException
     */
    public void testValidation() throws ValidationException {
        VAlarm alarm = new VAlarm();
        alarm.getProperties().add(new Trigger(new DateTime(System.currentTimeMillis())));
        alarm.getProperties().add(Action.DISPLAY);
        assertValidationException(alarm);
        alarm.getProperties().add(new Description("Testing display"));
        alarm.validate();
    }

    /**
     * Test duration/repeat validation.
     * @throws ValidationException
     */
    public void testValidationDurationRepeat() throws ValidationException {
        VAlarm alarm = new VAlarm(new Dur(0, 2, 0, 0));
        alarm.getProperties().add(Action.DISPLAY);
        alarm.getProperties().add(new Description("Testing display"));
        Duration duration = new Duration(new Dur(0, 0, 2, 0));
        alarm.getProperties().add(duration);
        assertValidationException(alarm);
        alarm.getProperties().add(new Repeat(2));
        alarm.validate();
        alarm.getProperties().remove(duration);
        assertValidationException(alarm);
    }
}
