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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.TimeZones;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$
 *
 * Created on 30/06/2005
 *
 * @author Ben Fortuna
 *
 */
public class DateTimeTest extends TestCase {

    private static Log log = LogFactory.getLog(DateTimeTest.class);
    
    private static TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();

    private DateTime dateTime;
    
    private String expectedToString;

    // static {
        // TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
    // }
    
    /**
     * @param testMethod
     */
    public DateTimeTest(String testMethod) {
        super(testMethod);
    }
    
    /**
     * Default constructor.
     */
    public DateTimeTest(DateTime dateTime, String expectedToString) {
        super("testToString");
        this.dateTime = dateTime;
        this.expectedToString = expectedToString;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        // ensure relaxing parsing is disabled for these tests..
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false);
    }
    
    /**
     * 
     */
    public void testToString() {
        assertNotNull("Null input date", dateTime);
        assertEquals("Incorrect string representation", expectedToString, dateTime.toString());
    }

    /*
     * Class under test for void DateTime(String)
     */
    public void testDateTimeString() throws Exception {
        try {
            new DateTime("20050630");
            fail("Should throw ParseException");
        }
        catch (ParseException pe) {
            log.info("Exception occurred: " + pe.getMessage());
        }
        
        try {
            new DateTime("20000402T020000",
                    registry.getTimeZone("America/Los_Angeles"));
            fail("Should throw ParseException");
        }
        catch (ParseException pe) {
            log.info("Exception occurred: " + pe.getMessage());
        }
    }
    
    /**
     * Test equality of DateTime instances created using different constructors.
     * @throws ParseException
     */
    public void testDateTimeEquals() throws ParseException {
        DateTime date1 = new DateTime("20050101T093000");
    
        Calendar calendar = Calendar.getInstance(); //TimeZone.getTimeZone("Etc/UTC"));
        calendar.clear();
        calendar.set(2005, 0, 1, 9, 30, 00);
        calendar.set(Calendar.MILLISECOND, 1);
        DateTime date2 = new DateTime(calendar.getTime());
    
        assertEquals(date1.hashCode(), date2.hashCode());
        assertEquals(date1.toString(), date2.toString());
        assertEquals(date1, date2);
    }
    
    /**
     * Test that equality of two DateTime instances created using different constructors
     *  implies equality of hashCode.
     * @throws ParseException
     */
    public void testDateTimeHashCode() throws ParseException {
        TimeZone tz1 = TimeZoneRegistryFactory.getInstance().createRegistry().getTimeZone("Europe/Paris");
        TimeZone tz2 = (TimeZone)tz1.clone();
        DateTime date1 = new DateTime("20050101T093000", tz1);
        DateTime date2 = new DateTime("20050101T093000", tz2);
        // verify that if equals() == true, hashCode must match also
        // was not the case previously as hashCode() was taking the TimeZone object
        // into consideration when equals() was not.
        assertEquals(date1, date2);
        assertEquals(date1.hashCode(), date2.hashCode());
    }

    /**
     * Test UTC date-times.
     */
    public void testUtc() throws ParseException {
        // ordinary date..
        DateTime date1 = new DateTime("20050101T093000");
        assertFalse(date1.isUtc());
        
        // UTC date..
        DateTime date2 = new DateTime(true);
        assertTrue(date2.isUtc());
        
        TimeZone utcTz = registry.getTimeZone(TimeZones.UTC_ID);
        utcTz.setID(TimeZones.UTC_ID);
        
        // UTC timezone, but not UTC..
        DateTime date3 = new DateTime("20050101T093000", utcTz);
//        date3.setUtc(false);
        assertFalse(date3.isUtc());
        
        DateTime date4 = new DateTime();
        date4.setUtc(true);
        assertTrue(date4.isUtc());
        date4.setUtc(false);
        assertFalse(date4.isUtc());

        DateTime date5 = new DateTime(false);
        date5.setTimeZone(utcTz);
        assertFalse(date5.isUtc());
    }
    
    public String getName() {
        if (StringUtils.isNotEmpty(expectedToString)) {
            return super.getName() + " [" + expectedToString + "]";
        }
        return super.getName();
    }
    
    /**
     * @return
     */
    public static TestSuite suite() throws ParseException {
        TestSuite suite = new TestSuite();

        // test DateTime(long)..
        DateTime dt = new DateTime(0);
        dt.setUtc(true);
//      dt.setTimeZone(TimeZoneRegistryFactory.getInstance().createRegistry().getTimeZone(TimeZones.GMT_ID));
//      assertEquals("19700101T000000", dt.toString());
        suite.addTest(new DateTimeTest(dt, "19700101T000000Z"));

        // test DateTime(Date)..
        Calendar cal = Calendar.getInstance(); //TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.YEAR, 1984);
        // months are zero-based..
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 34);
        suite.addTest(new DateTimeTest(new DateTime(cal.getTime()), "19840417T031534"));

        // test DateTime(String)..
        suite.addTest(new DateTimeTest(new DateTime("20000827T020000"), "20000827T020000"));
        suite.addTest(new DateTimeTest(new DateTime("20070101T080000"), "20070101T080000"));
        suite.addTest(new DateTimeTest(new DateTime("20050630T093000"), "20050630T093000"));
        suite.addTest(new DateTimeTest(new DateTime("20050630T093000Z"), "20050630T093000Z"));
        suite.addTest(new DateTimeTest(new DateTime("19390901T000000"), "19390901T000000"));
        
        suite.addTest(new DateTimeTest(new DateTime("20000402T020000",
                registry.getTimeZone("Australia/Melbourne")), "20000402T020000"));
        suite.addTest(new DateTimeTest(new DateTime("20000402T020000"), "20000402T020000"));
        
        DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
//        Calendar cal = Calendar.getInstance(); //java.util.TimeZone.getTimeZone("America/Los_Angeles"));
        cal.clear();
        cal.set(2000, 0, 1, 2, 0, 0);
        for (int i = 0; i < 365; i++) {
            String dateString = df.format(cal.getTime());
            suite.addTest(new DateTimeTest(new DateTime(dateString), dateString)); 
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        suite.addTest(new DateTimeTest(new DateTime("20071104T000000",
                registry.getTimeZone("America/Los_Angeles")), "20071104T000000"));
        
        // other tests..
        suite.addTest(new DateTimeTest("testDateTimeString"));
        suite.addTest(new DateTimeTest("testDateTimeEquals"));
        suite.addTest(new DateTimeTest("testDateTimeHashCode"));
        suite.addTest(new DateTimeTest("testUtc"));
        
        return suite;
    }
}
