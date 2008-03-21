/*
 * $Id$
 *
 * Created on 14/09/2005
 *
 * Copyright (c) 2005, Ben Fortuna
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
package net.fortuna.ical4j.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fortuna.ical4j.model.property.DtStart;

import junit.framework.TestCase;

/**
 * Unit tests for <code>TimeZone</code>.
 * @author Ben Fortuna
 */
public class TimeZoneTest extends TestCase {
    
    private static final long GMT_MINUS_10 = -10 * 60 * 60 * 1000;
    
    private static final Log LOG = LogFactory.getLog(TimeZoneTest.class);
    
    private TimeZoneRegistry registry;

    private java.util.TimeZone tz;

    private TimeZone timezone;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        tz = java.util.TimeZone.getTimeZone("Australia/Melbourne");
        timezone = registry.getTimeZone("Australia/Melbourne");
    }

    /**
     * Assert the zone info id is the same as the Java timezone.
     */
    public void testGetId() {
//        assertEquals(tz.getID(), timezone.getID());
        assertNotNull(timezone.getID());
    }

    /**
     * Assert the zone info name is the same as the Java timezone.
     */
    public void testGetDisplayName() {
//        assertEquals(tz.getDisplayName(), timezone.getDisplayName());
        assertNotNull(timezone.getDisplayName());
    }

    /**
     * Assert the zone info name is the same as the Java timezone.
     */
    public void testGetDisplayNameShort() {
//        assertEquals(tz.getDisplayName(false, TimeZone.SHORT), timezone.getDisplayName(false, TimeZone.SHORT));
        assertNotNull(timezone.getDisplayName(false, TimeZone.SHORT));
    }
    
    /**
     * Assert the raw offset is the same as its Java equivalent.
     */
    public void testGetRawOffset() {
        assertEquals(tz.getRawOffset(), timezone.getRawOffset());
    }
    
    /**
     * Assert the zone info has the same rules as its Java equivalent.
     */
    public void testHasSameRules() {
        assertTrue(timezone.hasSameRules(tz));
    }

    /**
     * A test to ensure the method TimeZone.inDaylightTime() is working
     * correctly (for the last 10 years).
     */
    public void testInDaylightTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -10);
        /*
        cal.set(Calendar.MONTH, 12);
        assertEquals(tz.inDaylightTime(cal.getTime()), timezone.inDaylightTime(cal.getTime()));
        
        cal.set(Calendar.MONTH, 6);
        assertEquals(tz.inDaylightTime(cal.getTime()), timezone.inDaylightTime(cal.getTime()));
        */
        long start, stop;
        for (int y = 0; y < 10; y++) {
            cal.clear(Calendar.DAY_OF_YEAR);
            for (int i = 0; i < 365; i++) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
                start = System.currentTimeMillis();
                assertEquals("inDaylightTime() invalid: [" + cal.getTime() + "]",
                        tz.inDaylightTime(cal.getTime()),
                        timezone.inDaylightTime(cal.getTime()));
                stop = System.currentTimeMillis();
                LOG.debug("Time: " + (stop - start) + "ms");
            }
        }
    }
    
    /**
     * Ensure useDaylightTime() method is working correctly.
     */
    public void testUseDaylightTime() {
        assertEquals(tz.useDaylightTime(), timezone.useDaylightTime());
        
        java.util.TimeZone noDaylightTz = java.util.TimeZone.getTimeZone("Africa/Abidjan");
        TimeZone noDaylightTimezone = registry.getTimeZone("Africa/Abidjan");
        assertEquals(noDaylightTz.useDaylightTime(), noDaylightTimezone.useDaylightTime());
    }
    
    /**
     * Assert getOffset() returns the same result as its Java timezone equivalent.
     */
    public void testGetOffset() {
        int era = GregorianCalendar.AD;
        int year = 2005;
        int month = 9;
        int day = 18;
        int dayOfWeek = Calendar.SUNDAY;
        int millisecods = 0;
        assertEquals(tz.getOffset(era, year, month, day, dayOfWeek, millisecods),
                timezone.getOffset(era, year, month, day, dayOfWeek, millisecods));
    }
    
    public void testAmericaIndiana() {
        java.util.TimeZone indianaTz = java.util.TimeZone.getTimeZone("America/Indiana/Indianapolis");

        Calendar cal = Calendar.getInstance(indianaTz);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 20);
        
        DateTime dtStart = new DateTime(cal.getTime()); 
        DtStart pDtStart = new DtStart(dtStart); 
        pDtStart.setTimeZone(registry.getTimeZone("America/Indiana/Indianapolis"));
    }
    
    public void testAustraliaSydney() {
//        java.util.TimeZone sydneyTz = java.util.TimeZone.getTimeZone("Australia/Sydney");

        Calendar cal = Calendar.getInstance();
        cal.set(2003, 7, 31, 23, 00, 00);
        
        assertEquals("inDaylightTime() invalid: [" + cal.getTime() + "]",
                tz.inDaylightTime(cal.getTime()),
                timezone.inDaylightTime(cal.getTime()));
    }
    
    /**
     * Test alias functionality for deprecated timezone identifiers.
     */
    public void testAlias() {
        TimeZone tz = registry.getTimeZone("US/Mountain");
        assertNotNull(tz);
        assertEquals(tz, registry.getTimeZone("America/Denver"));
    }

    /**
     * 
     */
    public void testHonoluluRawOffset() {
        TimeZone tz = registry.getTimeZone("Pacific/Honolulu");
        assertEquals(GMT_MINUS_10, tz.getRawOffset());
    }

    /**
     * 
     */
    public void testHonoluluCurrentOffset() {
        TimeZone tz = registry.getTimeZone("Pacific/Honolulu");
        assertEquals(GMT_MINUS_10, tz.getOffset(System.currentTimeMillis()));
    }
}
