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
package net.fortuna.ical4j.model;

import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtStart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$
 *
 * Created on 14/09/2005
 *
 * Unit tests for <code>TimeZone</code>.
 * @author Ben Fortuna
 */
public class TimeZoneTest extends TestCase {

    private static final long GMT_PLUS_4 = 4 * 60 * 60 * 1000;
    
    private static final long GMT_PLUS_10 = 10 * 60 * 60 * 1000;

    private static final long GMT_MINUS_10 = -10 * 60 * 60 * 1000;

    private static final long GMT_MINUS_1030 = -630 * 60 * 1000;
    
    private static final long GMT_MINUS_103126 = GMT_MINUS_1030 - (1 * 60 * 1000) - (26 * 1000);

    private static final Log LOG = LogFactory.getLog(TimeZoneTest.class);

    private TimeZoneRegistry registry;
    
    private java.util.TimeZone tz;

    private TimeZone timezone;

    private String expectedTimezoneId;
    
    private boolean expectedUseDaylightTime;
    
    private int expectedDstSavings;
    
    private long expectedRawOffset;
    
    private Date date;
    
    private long expectedOffset;
    
    private boolean expectedInDaylight;

    /**
     * @param testMethod
     * @param timezoneId
     */
    public TimeZoneTest(String testMethod, String timezoneId) {
        super(testMethod);
        // java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/Los_Angeles"));
        registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        tz = java.util.TimeZone.getTimeZone(timezoneId);
        timezone = registry.getTimeZone(timezoneId);
    }

    /**
     * @param testMethod
     * @param timezoneId
     * @param expectedTimezoneId
     */
    public TimeZoneTest(String testMethod, String timezoneId, String expectedTimezoneId) {
        this(testMethod, timezoneId);
        this.expectedTimezoneId = expectedTimezoneId;
    }
    
    /**
     * @param testMethod
     * @param timezoneId
     * @param expectedUseDaylightTime
     */
    public TimeZoneTest(String testMethod, String timezoneId, boolean expectedUseDaylightTime) {
        this(testMethod, timezoneId);
        this.expectedUseDaylightTime = expectedUseDaylightTime;
    }
    
    /**
     * @param testMethod
     * @param timezoneId
     * @param expectedDstSavings
     */
    public TimeZoneTest(String testMethod, String timezoneId, int expectedDstSavings) {
        this(testMethod, timezoneId);
        this.expectedDstSavings = expectedDstSavings;
    }
    
    /**
     * @param testMethod
     * @param timezoneId
     * @param expectedRawOffset
     */
    public TimeZoneTest(String testMethod, String timezoneId, long expectedRawOffset) {
        this(testMethod, timezoneId);
        this.expectedRawOffset = expectedRawOffset;
    }
    
    /**
     * @param testMethod
     * @param timezoneId
     * @param date
     * @param expectedOffset
     */
    public TimeZoneTest(String testMethod, String timezoneId, Date date, long expectedOffset) {
        this(testMethod, timezoneId);
        this.date = date;
        this.expectedOffset = expectedOffset;
    }

    /**
     * @param testMethod
     * @param timezoneId
     * @param date
     * @param expectedInDaylight
     */
    public TimeZoneTest(String testMethod, String timezoneId, Date date, boolean expectedInDaylight) {
        this(testMethod, timezoneId);
        this.date = date;
        this.expectedInDaylight = expectedInDaylight;
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
//    protected void setUp() throws Exception {
//        super.setUp();
//        registry = TimeZoneRegistryFactory.getInstance().createRegistry();
//        tz = java.util.TimeZone.getTimeZone("Australia/Melbourne");
//        timezone = registry.getTimeZone("Australia/Melbourne");
//    }

    /**
     * Assert the zone info id is the same as the Java timezone.
     */
    public void testGetId() {
        // assertEquals(tz.getID(), timezone.getID());
        assertNotNull(timezone.getID());
        if (expectedTimezoneId != null) {
            assertEquals(expectedTimezoneId, timezone.getID());
        }
    }

    /**
     * Assert the zone info name is the same as the Java timezone.
     */
    public void testGetDisplayName() {
        // assertEquals(tz.getDisplayName(), timezone.getDisplayName());
        assertNotNull(timezone.getDisplayName());
    }

    /**
     * Assert the zone info name is the same as the Java timezone.
     */
    public void testGetDisplayNameShort() {
        // assertEquals(tz.getDisplayName(false, TimeZone.SHORT), timezone.getDisplayName(false, TimeZone.SHORT));
        assertNotNull(timezone.getDisplayName(false, TimeZone.SHORT));
    }

    /**
     * Assert the raw offset is the same as its Java equivalent.
     */
    public void testGetRawOffset() {
        assertEquals(expectedRawOffset, timezone.getRawOffset());
        assertEquals(tz.getRawOffset(), timezone.getRawOffset());
    }

    /**
     * Assert the zone info has the same rules as its Java equivalent.
     */
    public void testHasSameRules() {
        assertTrue(timezone.hasSameRules(tz));
    }

    /**
     * A test to ensure the method TimeZone.inDaylightTime() is working correctly (for the last 10 years).
     */
    public void testInDaylightTime() {
        if (date != null) {
            assertEquals(expectedInDaylight, timezone.inDaylightTime(date));
        }
        else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -10);
            /*
             * cal.set(Calendar.MONTH, 12); assertEquals(tz.inDaylightTime(cal.getTime()),
             * timezone.inDaylightTime(cal.getTime())); cal.set(Calendar.MONTH, 6);
             * assertEquals(tz.inDaylightTime(cal.getTime()), timezone.inDaylightTime(cal.getTime()));
             */
            long start, stop;
            for (int y = 0; y < 10; y++) {
                cal.clear(Calendar.DAY_OF_YEAR);
                for (int i = 0; i < 365; i++) {
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    start = System.currentTimeMillis();
                    assertEquals("inDaylightTime() invalid: [" + cal.getTime()
                            + "]", tz.inDaylightTime(cal.getTime()), timezone
                            .inDaylightTime(cal.getTime()));
                    stop = System.currentTimeMillis();
                    LOG.debug("Time: " + (stop - start) + "ms");
                }
            }
        }
    }

    /**
     * Ensure useDaylightTime() method is working correctly.
     */
    public void testUseDaylightTime() {
        assertEquals(expectedUseDaylightTime, timezone.useDaylightTime());
        assertEquals(tz.useDaylightTime(), timezone.useDaylightTime());
    }

    /**
     * Assert getOffset() returns the same result as its Java timezone equivalent.
     */
    public void testGetOffset() {
        if (date != null) {
            assertEquals(expectedOffset, timezone.getOffset(date.getTime()));
            assertEquals(tz.getOffset(date.getTime()), timezone.getOffset(date.getTime()));
        }
        else {
            int era = GregorianCalendar.AD;
            int year = 2005;
            int month = 9;
            int day = 18;
            int dayOfWeek = Calendar.SUNDAY;
            int millisecods = 0;
            assertEquals(tz
                    .getOffset(era, year, month, day, dayOfWeek, millisecods),
                    timezone.getOffset(era, year, month, day, dayOfWeek,
                            millisecods));
        }
    }

    public void testAmericaIndiana() {
        java.util.TimeZone indianaTz = java.util.TimeZone
                .getTimeZone("America/Indiana/Indianapolis");

        Calendar cal = Calendar.getInstance(indianaTz);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 20);

        DateTime dtStart = new DateTime(cal.getTime());
        DtStart pDtStart = new DtStart(dtStart);
        pDtStart.setTimeZone(registry
                .getTimeZone("America/Indiana/Indianapolis"));
    }

    public void testAustraliaSydney() {
        // java.util.TimeZone sydneyTz = java.util.TimeZone.getTimeZone("Australia/Sydney");

        Calendar cal = Calendar.getInstance();
        cal.set(2003, 7, 31, 23, 00, 00);

        assertEquals("inDaylightTime() invalid: [" + cal.getTime() + "]", tz
                .inDaylightTime(cal.getTime()), timezone.inDaylightTime(cal
                .getTime()));
    }

    /**
     * Test custom DST savings implementation.
     */
    public void testGetDSTSavings() {
        assertEquals(expectedDstSavings, timezone.getDSTSavings());
        assertEquals(tz.getDSTSavings(), timezone.getDSTSavings());
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#getName()
     */
    public String getName() {
        if (timezone != null) {
            return super.getName() + " [" + timezone.getID() + "]";
        }
        return super.getName();
    }

    public void testMinsk() throws Exception {
        String fixedDefinition =
        "BEGIN:VCALENDAR\r\n"
        + "BEGIN:VTIMEZONE\r\n"
        + "TZID:Europe/Minsk\r\n"
        + "X-S1CS-TZID-ALIAS:E. Europe Standard Time\r\n"
        + "BEGIN:STANDARD\r\n"
        + "TZOFFSETFROM:+0200\r\n"
        + "TZOFFSETTO:+0300\r\n"
        + "TZNAME:FET\r\n"
        + "DTSTART:20110327T020000\r\n"
        + "RDATE:20110327T020000\r\n"
        + "END:STANDARD\r\n"
        + "BEGIN:DAYLIGHT\r\n"
        + "TZOFFSETFROM:+0200\r\n"
        + "TZOFFSETTO:+0300\r\n"
        + "TZNAME:EEST\r\n"
        + "DTSTART:19920329T000000\r\n"
        + "RDATE:20020331T020000\r\n"
        + "RDATE:20030330T020000\r\n"
        + "RDATE:20040328T020000\r\n"
        + "RDATE:20050327T020000\r\n"
        + "RDATE:20060326T020000\r\n"
        + "RDATE:20070325T020000\r\n"
        + "RDATE:20080330T020000\r\n"
        + "RDATE:20090329T020000\r\n"
        + "RDATE:20100328T020000\r\n"
        + "END:DAYLIGHT\r\n"
        + "BEGIN:STANDARD\r\n"
        + "TZOFFSETFROM:+0300\r\n"
        + "TZOFFSETTO:+0200\r\n"
        + "TZNAME:EET\r\n"
        + "DTSTART:19910929T030000\r\n"
        + "RDATE:20021027T030000\r\n"
        + "RDATE:20031026T030000\r\n"
        + "RDATE:20041031T030000\r\n"
        + "RDATE:20051030T030000\r\n"
        + "RDATE:20061029T030000\r\n"
        + "RDATE:20071028T030000\r\n"
        + "RDATE:20081026T030000\r\n"
        + "RDATE:20091025T030000\r\n"
        + "RDATE:20101031T030000\r\n"
        + "END:STANDARD\r\n"
        + "END:VTIMEZONE\r\n"
        + "END:VCALENDAR\r\n";

        net.fortuna.ical4j.model.Calendar cal = new CalendarBuilder().build(new StringReader(fixedDefinition));
        VTimeZone vtz = (VTimeZone)cal.getComponent(VTimeZone.VTIMEZONE);
        TimeZone tzz = new TimeZone(vtz);
        DateTime d = new DateTime("20100428T140000Z");
        d.setTimeZone(tzz);
        assertEquals("20100428T170000", d.toString());
    }
    
    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(new TimeZoneTest("testMinsk", "Europe/Minsk"));
        suite.addTest(new TimeZoneTest("testGetId", "Australia/Melbourne"));
        suite.addTest(new TimeZoneTest("testGetId", "US/Mountain", "America/Denver"));
        suite.addTest(new TimeZoneTest("testGetId", "Asia/Calcutta", "Asia/Kolkata"));
        
        suite.addTest(new TimeZoneTest("testGetDisplayName", "Australia/Melbourne"));
        suite.addTest(new TimeZoneTest("testGetDisplayNameShort", "Australia/Melbourne"));
        
        suite.addTest(new TimeZoneTest("testGetRawOffset", "Australia/Melbourne", GMT_PLUS_10));
        suite.addTest(new TimeZoneTest("testGetRawOffset", "Pacific/Honolulu", GMT_MINUS_10));
        
        suite.addTest(new TimeZoneTest("testGetRawOffset", "Europe/Samara", GMT_PLUS_4));
        suite.addTest(new TimeZoneTest("testInDaylightTime", "Europe/Samara", false));
        suite.addTest(new TimeZoneTest("testGetOffset", "Europe/Samara", GMT_PLUS_4));
        
        suite.addTest(new TimeZoneTest("testHasSameRules", "Australia/Melbourne"));
        
        Calendar cal = Calendar.getInstance(java.util.TimeZone.getTimeZone("Australia/Melbourne"));
        cal.set(1999, 2, 28, 2, 1);
        // technically, 2:01am, Sunday 28 March 1999 doesn't exist, so it can't be in daylight time..
        suite.addTest(new TimeZoneTest("testInDaylightTime", "Australia/Melbourne", cal.getTime(), false));
        suite.addTest(new TimeZoneTest("testInDaylightTime", "Australia/Melbourne"));
        
        suite.addTest(new TimeZoneTest("testUseDaylightTime", "Australia/Melbourne", true));
        suite.addTest(new TimeZoneTest("testUseDaylightTime", "Africa/Abidjan", false));
        suite.addTest(new TimeZoneTest("testGetDSTSavings", "Australia/Melbourne", 3600000));
        
        suite.addTest(new TimeZoneTest("testGetOffset", "Australia/Melbourne"));
        //testHonoluluCurrentOffset..
        suite.addTest(new TimeZoneTest("testGetOffset", "Pacific/Honolulu", new Date(), GMT_MINUS_10));
        //testHonoluluHistoricalOffset..
        cal = new GregorianCalendar(1925, 0, 1);
//        suite.addTest(new TimeZoneTest("testGetOffset", "Pacific/Honolulu", cal.getTime(), GMT_MINUS_1030));
        //testHonoluluPreHistoricOffset..
        cal = new GregorianCalendar(1800, 0, 1);
//        suite.addTest(new TimeZoneTest("testGetOffset", "Pacific/Honolulu", cal.getTime(), GMT_MINUS_103126));
        cal = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
        cal.set(2010, 9, 30, 4, 0, 0);
        suite.addTest(new TimeZoneTest("testInDaylightTime", "Europe/Paris", cal.getTime(), true));
        cal.set(2010, 9, 31, 4, 0, 0);
        suite.addTest(new TimeZoneTest("testInDaylightTime", "Europe/Paris", cal.getTime(), false));
        cal.set(2010, 9, 31, 4, 0, 0);
        suite.addTest(new TimeZoneTest("testInDaylightTime", "America/Bahia", cal.getTime(), false));
        cal.set(2002, 11, 04, 4, 0, 0);
        suite.addTest(new TimeZoneTest("testInDaylightTime", "America/Bahia", cal.getTime(), true));
        return suite;
    }
}
