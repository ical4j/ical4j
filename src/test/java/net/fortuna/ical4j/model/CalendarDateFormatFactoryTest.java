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

import java.util.TimeZone;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.fortuna.ical4j.model.CalendarDateFormatFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * $Id$ [06-Apr-2004]
 *
 */
public class CalendarDateFormatFactoryTest extends TestCase {

    private static final boolean STRICT = false;
    
    private static final boolean LENIENT = true;

    private String pattern;
    
    private boolean lenient;
    
    private java.util.TimeZone[] timeZones;
    
    private String[] values;

    /**
     * @param testMethod
     * @param pattern
     * @param lenient
     * @param values
     */
    public CalendarDateFormatFactoryTest(String testMethod, String pattern, boolean lenient, String[] values) {
    	this(testMethod, pattern, lenient, null, values);
    }
    
    /**
     * @param pattern
     * @param lenient
     * @param timeZones
     * @param values
     */
    public CalendarDateFormatFactoryTest(String testMethod, String pattern, boolean lenient, java.util.TimeZone[] timeZones,
    		String[] values) {
    	super(testMethod);
    	this.pattern = pattern;
    	this.lenient = lenient;
    	this.timeZones = timeZones;
    	this.values = values;
    }

    public void testFallbackToSimpleDateFormat() throws Exception {
        SimpleDateFormat f = new SimpleDateFormat("HH");
        assertEquals(f, CalendarDateFormatFactory.getInstance("HH"));
    }

    private DateFormat getCalendarFormatForPattern(String pattern) {
        DateFormat cdf = CalendarDateFormatFactory.getInstance(pattern);
        assertTrue("didn't get calendar format for pattern: " + pattern,
                cdf.getClass().getName().startsWith(CalendarDateFormatFactory.class.getName()));
        return cdf;
    }

    /**
     * @throws ParseException
     */
    public void testParseSuccess() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        // Date instances are always in UTC..
        sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        DateFormat cdf = getCalendarFormatForPattern(pattern);
        sdf.setLenient(lenient);
        cdf.setLenient(lenient);

        for (int i = 0; i < timeZones.length; i++) {
            assertNotNull(timeZones[i]);
            cdf.setTimeZone(timeZones[i]);
            sdf.setTimeZone(cdf.getTimeZone());

            DateFormat clone = (DateFormat) cdf.clone();

            for (int j = 0; j < values.length; j++) {

                Date cdfResult = cdf.parse(values[j]);
                Date sdfResult = sdf.parse(values[j]);
                Date cloneResult = clone.parse(values[j]);
                assertEquals(sdfResult, cdfResult);
                assertEquals(sdfResult, cloneResult);

                // also test the formatter!
                Date d = sdf.parse(values[j]);
                assertEquals(sdf.format(d), cdf.format(d));
                assertEquals(sdf.format(d), clone.format(d));
            }
        }
    }

    /**
     * 
     */
    public void testParseFailure() {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        DateFormat cdf = getCalendarFormatForPattern(pattern);
        sdf.setLenient(lenient);
        cdf.setLenient(lenient);

        DateFormat clone = (DateFormat) cdf.clone();

        for (int i = 0; i < values.length; i++) {
            Exception sdfException = null;

            try {
                // sanity check, make sure simple date formatter fails too
                sdf.parse(values[i]);
                // CalendarDateFormats are a bit more strict than SimpleDateFormat..
//                fail("bad test -- expected simple date formatter to fail for value: " + values[i]);
            } catch (Exception e) {
                sdfException = e;
            }

            try {
                cdf.parse(values[i]);
                fail("expected a parse exception for value: " + values[i]);

            } catch (Exception e) {
                if (sdfException != null) {
                    assertEquals(sdfException.getClass().getName(), e.getClass().getName());
                }
            }

            try {
                clone.parse(values[i]);
                fail("expected a parse exception for value: " + values[i]);

            } catch (Exception e) {
                if (sdfException != null) {
                    assertEquals(sdfException.getClass().getName(), e.getClass().getName());
                }
            }

        }

    }

    /**
     * @return
     */
    public static TestSuite suite() {
    	TestSuite suite = new TestSuite();

        java.util.TimeZone[] tz = {TimeZone.getDefault(), TimeZone.getTimeZone("GMT"), TimeZone.getTimeZone("US/Eastern"), TimeZone.getTimeZone("US/Pacific")};

        suite.addTest(new CalendarDateFormatFactoryTest("testParseFailure", "yyyyMMdd'T'HHmmss", STRICT, new String[] {"1", "20081201T231370", "20081601T000000"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseSuccess", "yyyyMMdd'T'HHmmss", LENIENT, tz, new String[] {"20081201T231370", "20081601T000000", "20081201T000000xyz"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseSuccess", "yyyyMMdd'T'HHmmss", STRICT, tz, new String[] {"00010215T023456", "20081201T000000"}));

        suite.addTest(new CalendarDateFormatFactoryTest("testParseFailure", "yyyyMMdd'T'HHmmss'Z'", STRICT, new String[] {"1", "20081201T000000", "20083101T000000Z"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseSuccess", "yyyyMMdd'T'HHmmss'Z'", LENIENT, tz, new String[] {"20083101T000000Z", "20081201T000000Zxyz"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseSuccess", "yyyyMMdd'T'HHmmss'Z'", STRICT, tz, new String[] {"20081201T000000Z"}));

        suite.addTest(new CalendarDateFormatFactoryTest("testParseFailure", "yyyyMMdd", STRICT, new String[] {"1", "20081301"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseSuccess", "yyyyMMdd", LENIENT, tz, new String[] {"20081301", "20081201xyz"}));

        suite.addTest(new CalendarDateFormatFactoryTest("testParseFailure", "HHmmss", STRICT, new String[] {"1", "260000"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseSuccess", "HHmmss", LENIENT, tz, new String[] {"260000"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseSuccess", "HHmmss", STRICT, tz, new String[] {"021234", "233456"}));

        suite.addTest(new CalendarDateFormatFactoryTest("testParseFailure", "HHmmss'Z'", STRICT, new String[] {"1", "123456", "261234Z"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseSuccess", "HHmmss'Z'", LENIENT, tz, new String[] {"261234Z"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseSuccess", "HHmmss'Z'", STRICT, tz, new String[] {"021234Z"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseFailure", "HHmmss'Z'", STRICT, tz, new String[] {"233456Zzxy"}));
        suite.addTest(new CalendarDateFormatFactoryTest("testParseSuccess", "HHmmss'Z'", LENIENT, tz, new String[] {"233456Zzxy"}));
    	return suite;
    }
}
