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

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.util.Strings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * $Id$
 * <p/>
 * Created on 10/12/2006
 * <p/>
 * Unit tests for {@link RDate}.
 *
 * @author Ben Fortuna
 */
public class RDateTest {

//    private static final Logger LOGLoggerLogFactoLoggergetLog(RDateTest.class);

    private ZoneId timezone;

    @Before
    public void setUp() throws Exception {
        timezone = TimeZoneRegistry.getGlobalZoneId("Australia/Melbourne");
    }

    @Test
    public void testSetTimeZone() {
        RDate<Instant> rDate = new RDate(new ArrayList<>());
        
        /*
        try {
            rDate.setTimeZone(timezone);
            fail("Should throw UnsupportedOperationException");
        }
        catch (UnsupportedOperationException uoe) {
            LOG.info("Caught exception: " + uoe.getMessage());
        }

        try {
            rDate.setTimeZone(null);
            fail("Should throw UnsupportedOperationException");
        }
        catch (UnsupportedOperationException uoe) {
            LOG.info("Caught exception: " + uoe.getMessage());
        }
        */

        rDate.add(new TzId(timezone.getId()));
        assertEquals(timezone.getId(), rDate.getParameters().getFirst(Parameter.TZID).get().getValue());
    }

    @Test
    @Ignore
    public void testToString() throws Exception {
        RDate rDate = new RDate(new ParameterList(), "20121212T121212Z");
        assertEquals(Property.RDATE + ":20121212T121212Z" + Strings.LINE_SEPARATOR, rDate.toString());

        rDate = new RDate<>(new ParameterList(), DateList.parse("20121212T121212Z,20121213T121212Z"));
        assertEquals(Property.RDATE + ":20121212T121212Z,20121213T121212Z" + Strings.LINE_SEPARATOR, rDate.toString());

        rDate = new RDate();
        rDate.setValue("20121212T121212Z");
        assertEquals(Property.RDATE + ":20121212T121212Z" + Strings.LINE_SEPARATOR, rDate.toString());
    }

}
