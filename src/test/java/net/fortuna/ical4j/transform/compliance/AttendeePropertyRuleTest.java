/*
 *  Copyright (c) 2024, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.transform.compliance;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.property.Attendee;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class AttendeePropertyRuleTest {

    private ParameterList params;


    @Before
    public void setup() {
        params = new ParameterList(Collections.singletonList(new Cn("Mobile Media")));
    }

    @Test
    public void shouldCorrectlyRemoveApostrophes() throws URISyntaxException {
        Attendee attendee = new Attendee(params, "mailto:'mobile-media-applications@1und1.de'");
        RuleManager.applyTo(attendee);
        assertEquals("mailto:mobile-media-applications@1und1.de", attendee.getValue());
    }

    @Test
    public void shouldLeaveAttendeeAsItIs() throws URISyntaxException {
        Attendee attendee = new Attendee(params, "mailto:mobile-media-applications@1und1.de");
        RuleManager.applyTo(attendee);
        assertEquals("mailto:mobile-media-applications@1und1.de", attendee.getValue());
    }

    @Test
    public void shouldNotThrowExceptionIfAttendeeIsEmpty() throws URISyntaxException {
        RuleManager.applyTo(new Attendee());
    }

    @Test
    public void shouldNotThrowExceptionIfOneApostrophe() throws URISyntaxException {
        RuleManager.applyTo(new Attendee("mailto:'"));
    }

    @Test
    public void shouldNotThrowExceptionIfTwoApostrophes() throws URISyntaxException {
        RuleManager.applyTo(new Attendee("mailto:''"));
    }

    @Test
    public void shouldNotDoAnythingIfAnotherScheem() throws URISyntaxException {
        String value = "http://something";
        Attendee attende = new Attendee(value);
        RuleManager.applyTo(attende);
        assertEquals(value, attende.getValue());
    }

}
