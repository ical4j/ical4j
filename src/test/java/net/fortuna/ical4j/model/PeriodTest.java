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

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$
 *
 * <p>Period Tester.</p>
 *
 * </p>Tests the behaviour of the Period class to make sure it acts in
 * the expected way.</p>
 * @see net.fortuna.ical4j.model.Period
 */
public class PeriodTest extends TestCase {
    
    private static final Log LOG = LogFactory.getLog(PeriodTest.class);

    private Period period;
    
    private DateTime expectedDate;
    
    private Period expectedPeriod;
    
    private TimeZone expectedTimezone;
    
    private boolean expectedIsUtc;
    
    /**
     * @param period
     * @param expectedDate
     */
    public PeriodTest(String testMethod, Period period, DateTime expectedDate) {
    	super(testMethod);
    	this.period = period;
    	this.expectedDate = expectedDate;
    }
    
    /**
     * @param testMethod
     * @param period
     * @param expectedPeriod
     */
    public PeriodTest(String testMethod, Period period, Period expectedPeriod) {
    	super(testMethod);
    	this.period = period;
    	this.expectedPeriod = expectedPeriod;
    }
    
    /**
     * @param testMethod
     * @param period
     * @param expectedTimezone
     */
    public PeriodTest(String testMethod, Period period, TimeZone expectedTimezone) {
        super(testMethod);
        this.period = period;
        this.expectedTimezone = expectedTimezone;
    }

    /**
     * @param testMethod
     * @param period
     * @param expectedIsUtc
     */
    public PeriodTest(String testMethod, Period period, boolean expectedIsUtc) {
        super(testMethod);
        this.period = period;
        this.expectedIsUtc = expectedIsUtc;
    }
    
    /**
     * @param testMethod
     * @param period
     */
    public PeriodTest(String testMethod, Period period) {
    	super(testMethod);
    	this.period = period;
    }
    
    public PeriodTest(String name)
    {
        super(name);
    }

    /**
     * 
     */
    public void testGetStart() {
    	assertEquals(expectedDate, period.getStart());
    }

    /**
     * 
     */
    public void testGetEnd() {
    	assertEquals(expectedDate, period.getEnd());
    }

    /**
     * 
     */
    public void testGetEndTimeZone() {
        assertEquals(expectedTimezone, period.getEnd().getTimeZone());
    }

    /**
     * 
     */
    public void testGetEndIsUtc() {
        assertEquals(expectedIsUtc, period.getEnd().isUtc());
    }

    /**
     * date is before
     * date is during
     * date is after
     * date is at start
     * date is at end
     * @throws Exception
     */
    public void testIncludes()  {
    	assertTrue(period.includes(expectedDate));
    }
    
    /**
     * 
     */
    public void testNotIncludes() {
    	assertFalse(period.includes(expectedDate));
    }

    /**
     * test date before range
     * test date after range
     * test date during range
     * test beginning of range
     * test end of range
     * @throws Exception
     */
    /*
    public void testBeforeWithDate() throws Exception
    {
        assertTrue("before() claims March isn't before May",
                monthMarch.before(may1994));
        assertFalse("before() claims May is before March",
                monthMay.before(mar1994));
        assertFalse("before() claims Winter is before March",
                winter.before(mar1994));
        assertFalse("before() claims March month is before its beginning",
                monthMarch.before(mar1994));
        assertTrue("before() claims March month isn't before its end",
                monthMarch.before(apr1994));
    }
    */

    /**
     * 
     */
    public void testBefore() {
    	assertTrue(period.before(expectedPeriod));
    }

    /**
     * 
     */
    public void testNotBefore() {
    	assertFalse(period.before(expectedPeriod));
    }

    /**
     * 
     */
    public void testAfter() {
    	assertTrue(period.after(expectedPeriod));
    }

    /**
     * 
     */
    public void testNotAfter() {
    	assertFalse(period.after(expectedPeriod));
    }
    
    /**
     * test date before range
     * test date after range
     * test date during range
     * test beginning of range
     * test end of range
     * @throws Exception
     */
    /*
    public void testAfterWithDate() throws Exception
    {
        assertFalse("after() claims March is after May",
                monthMarch.after(may1994));
        assertTrue("after() claims May isn't after March",
                monthMay.after(mar1994));
        assertFalse("after() claims Winter is after March",
                winter.after(mar1994));
        assertTrue("after() claims March month isn't after its beginning",
                monthMarch.after(mar1994));
        assertFalse("after() claims March month is after its end",
                monthMarch.after(apr1994));
    }
    */

    /**
     * test range before
     * test range after
     * test range adjacent before
     * test range adjacent after
     * test overlap
     * test reverse overlap
     * test range contained within
     * test range contains this one
     *
     * @throws Exception
     */
    public void testIntersects() {
    	assertTrue(period.intersects(expectedPeriod));
    }

    /**
     * 
     */
    public void testNotIntersects() {
    	assertFalse(period.intersects(expectedPeriod));
    }
    
    /**
     * test range before
     * test range after
     * test range adjacent before
     * test range adjacent after
     * test overlap
     * test reverse overlap
     * test range contained within
     * test range contains this one
     *
     * @throws Exception
     */
    /*public void testAdjacent() throws Exception
    {
        assertFalse("adjacent() claims March month is adjacent May month",
                monthMarch.adjacent(monthMay));
        assertFalse("adjacent() claims May month is adjacent March month",
                monthMay.adjacent(monthMarch));
        assertTrue("adjacent() claims March month isn't adjacent April month",
                monthMarch.adjacent(monthApril));
        assertTrue("adjacent() claims April month isn't adjacent March month",
                monthApril.adjacent(monthMarch));
        assertFalse("adjacent() claims overlapping halves are adjacent each other",
                firstHalf.adjacent(lastHalf));
        assertFalse("adjacent() claims disordered halves are adjacent each other",
                lastHalf.adjacent(firstHalf));
        assertFalse("adjacent() claims Winter is adjacent March month",
                winter.adjacent(monthMarch));
        assertFalse("adjacent() claims March month is adjacent Winter",
                monthMarch.adjacent(winter));

    }*/

    /**
     * test range before
     * test range after
     * test range adjacent before
     * test range adjacent after
     * test overlap
     * test reverse overlap
     * test range contained within
     * test range contains this one
     *
     * @throws Exception
     */
    public void testContains()  {
    	assertTrue(period.contains(expectedPeriod));
    }

    /**
     * 
     */
    public void testNotContains()  {
    	assertFalse(period.contains(expectedPeriod));
    }
    
    /**
     * test range before
     * test range after
     * test range adjacent before
     * test range adjacent after
     * test overlap
     * test reverse overlap
     * test range contained within
     * test range contains this one
     *
     * @throws Exception
     */
    public void testEquals()  {
    	assertEquals(expectedPeriod, period);
    }

    /**
     * test dissimilar types
     * test with null
     * test range before
     * test range after
     * test range adjacent before
     * test range adjacent after
     * test overlap
     * test reverse overlap
     * test range contained within
     * test range contains this one
     * test ranges that are the same
     * test ranges start the same, end earlier
     * test ranges start the same, end later
     *
     * @throws Exception
     */
    public void testCompareTo() throws Exception
    {
    	/*
        try {
            monthMarch.compareTo(this);
        } catch (ClassCastException cce) {
            // Exception expected, ignore
        }
        try {
            monthMarch.compareTo(null);
        } catch (ClassCastException cce) {
            // Exception expected, ignore
        }
        assertTrue("compareTo() claims March month is greater than May month",
                monthMarch.compareTo(monthMay) < 0);
        assertTrue("compareTo() claims May month is less than March month",
                monthMay.compareTo(monthMarch) > 0);
        assertTrue("compareTo() claims March month is greater than April month",
                monthMarch.compareTo(monthApril) < 0);
        assertTrue("compareTo() claims April month is less than March month",
                monthApril.compareTo(monthMarch) > 0);
        assertTrue("compareTo() claims first half is greater than the last",
                firstHalf.compareTo(lastHalf) < 0);
        assertTrue("compareTo() claims second half is less than the first",
                lastHalf.compareTo(firstHalf) > 0);
        assertTrue("compareTo() claims Winter is greater than March month",
                winter.compareTo(monthMarch) < 0);
        assertTrue("compareTo() claims March month is less than Winter",
                monthMarch.compareTo(winter) > 0);
        assertTrue("compareTo() claims year1994 is not the same as duplicate",
                year1994.compareTo(duplicateRange) == 0);
        assertTrue("compareTo() claims April month is greater than Spring",
                monthApril.compareTo(spring) < 0);
        assertTrue("compareTo() claims Spring is less than April month",
                spring.compareTo(monthApril) > 0);

		*/
    }

    /**
     * Testing of timezone functionality.
     */
    public void testTimezone() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        DateTime start = new DateTime(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 1);
//        cal.setTimeZone(TimeZone.getTimeZone(TimeZones.UTC_ID));
        DateTime end = new DateTime(cal.getTime());
        end.setUtc(true);
        
        Period p = new Period(start, end);
        
        LOG.info("Timezone test - period: [" + p + "]");
        
        assertFalse(p.getStart().isUtc());
        // end utc flag should be automatically set to same as start..
        assertFalse(p.getEnd().isUtc());
        
        start.setUtc(true);
        p = new Period(start, end);
        
        LOG.info("Timezone test - period: [" + p + "]");
        
        assertTrue(p.getStart().isUtc());
        assertTrue(p.getEnd().isUtc());
        
        p.setUtc(false);
        
        LOG.info("Timezone test - period: [" + p + "]");
        
        assertFalse(p.getStart().isUtc());
        assertFalse(p.getEnd().isUtc());
        
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Australia/Melbourne");
        
        p.setUtc(true);
        p.setTimeZone(timezone);
        
        assertFalse(p.getStart().isUtc());
        assertFalse(p.getEnd().isUtc());
        assertEquals(timezone, p.getStart().getTimeZone());
        assertEquals(timezone, p.getEnd().getTimeZone());
    }
    
    /**
     * Unit tests for {@link Period#isEmpty()}.
     */
    public void testIsEmpty() {
        Calendar cal = Calendar.getInstance();
        DateTime start = new DateTime(cal.getTime());
        assertTrue(new Period(start, start).isEmpty());
        assertTrue(new Period(start, new Dur(0)).isEmpty());
        
        cal.add(Calendar.SECOND, 1);
        assertFalse(new Period(start, new DateTime(cal.getTime())).isEmpty());
        assertFalse(new Period(start, new Dur(0, 0, 0, 1)).isEmpty());
    }
    
    /**
     * @return
     * @throws ParseException 
     */
    public static Test suite() throws ParseException {
    	TestSuite suite = new TestSuite();

        java.util.Calendar cal = new GregorianCalendar(1980,
                java.util.Calendar.JANUARY, 23);
        DateTime past = new DateTime(cal.getTime().getTime());
        cal.set(2022, java.util.Calendar.FEBRUARY, 23);
        DateTime future = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.JANUARY, 1);
        DateTime begin1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.DECEMBER, 31);
        DateTime end1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.MARCH, 4);
        DateTime mar1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.APRIL, 12);
        DateTime apr1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.MAY, 19);
        DateTime may1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.JUNE, 22);
        DateTime jun1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.JULY, 29);
        DateTime jul1994 = new DateTime(cal.getTime().getTime());
        Period year1994 = new Period(begin1994, end1994);
        Period monthMarch = new Period(mar1994, apr1994);
        Period monthApril = new Period(apr1994, may1994);
        Period monthMay = new Period(may1994, jun1994);
        Period firstHalf = new Period(begin1994, jun1994);
        Period lastHalf = new Period(may1994, end1994);
        Period winter = new Period(begin1994, apr1994);
        Period spring = new Period(apr1994, jul1994);
        Period marchToMay = new Period(mar1994, jun1994);
        Period marchToApril = new Period(mar1994, may1994);
//        Period duplicateRange = new Period(begin1994, end1994);
    	
        Period testPeriod;

        /*
        long testMillis;
        long todayMillis;
        todayMillis = today.getTime();
        testPeriod = new DateRange();
        testMillis = testRange.getStartDate().getTime();
        assertTrue("Uninitialized start date should have been set to NOW",
                (todayMillis - testMillis) < 5000);

        testRange = new DateRange();
        testMillis = testRange.getEndDate().getTime();
        assertTrue("Uninitialized end date should have been set to NOW",
                (todayMillis - testMillis) < 5000);
        */

//        testPeriod = new Period(past, (DateTime) null);
//        suite.addTest(new PeriodTest("testGetStart", testPeriod, past));

        /*
        testMillis = testPeriod.getEnd().getTime();
        assertTrue("No end date with set start date should have been set to NOW",
                (todayMillis - testMillis) < 5000);
        */

        testPeriod = new Period(past, future);
        suite.addTest(new PeriodTest("testGetStart", testPeriod, past));
        suite.addTest(new PeriodTest("testGetEnd", testPeriod, future));

        /*
        testRange = new DateRange();
        testRange.setEndDate(future);
        testMillis = testRange.getStartDate().getTime();
        assertTrue("No start date with set end date should have been NOW",
                (todayMillis - testMillis) < 5000);

        testRange = new DateRange();
        testRange.setEndDate(past);
        assertEquals(past, testRange.getEndDate());

        testRange.setStartDate(future);
        assertEquals(past, testRange.getStartDate());
        assertEquals(future, testRange.getEndDate());
        */

        suite.addTest(new PeriodTest("testNotIncludes", year1994, past));
        suite.addTest(new PeriodTest("testIncludes", year1994, mar1994));
        suite.addTest(new PeriodTest("testNotIncludes", year1994, future));
        suite.addTest(new PeriodTest("testIncludes", year1994, begin1994));
        suite.addTest(new PeriodTest("testIncludes", year1994, end1994));
        
        suite.addTest(new PeriodTest("testBefore", monthMarch, monthMay));
        suite.addTest(new PeriodTest("testNotBefore", monthMay, monthMarch));
        suite.addTest(new PeriodTest("testNotBefore", winter, monthMarch));
        suite.addTest(new PeriodTest("testNotBefore", monthMarch, winter));
        suite.addTest(new PeriodTest("testNotBefore", firstHalf, lastHalf));
        suite.addTest(new PeriodTest("testNotBefore", lastHalf, firstHalf));
        // because month march end is same as month april start, march is not
        // before april..
        suite.addTest(new PeriodTest("testNotBefore", monthMarch, monthApril));
        suite.addTest(new PeriodTest("testNotBefore", monthApril, monthMarch));
        
        suite.addTest(new PeriodTest("testNotAfter", monthMarch, monthMay));
        suite.addTest(new PeriodTest("testAfter", monthMay, monthMarch));
        suite.addTest(new PeriodTest("testNotAfter", winter, monthMarch));
        suite.addTest(new PeriodTest("testNotAfter", monthMarch, winter));
        suite.addTest(new PeriodTest("testNotAfter", firstHalf, lastHalf));
        suite.addTest(new PeriodTest("testNotAfter", lastHalf, firstHalf));
        suite.addTest(new PeriodTest("testNotAfter", monthMarch, monthApril));
        // because month march end is same as month april start, april is not
        // after march..
        suite.addTest(new PeriodTest("testNotAfter", monthApril, monthMarch));
    	
        suite.addTest(new PeriodTest("testNotIntersects", monthMarch, monthMay));
        suite.addTest(new PeriodTest("testNotIntersects", monthMay, monthMarch));
        suite.addTest(new PeriodTest("testNotIntersects", monthMarch, monthApril));
        suite.addTest(new PeriodTest("testNotIntersects", monthApril, monthMarch));
        suite.addTest(new PeriodTest("testIntersects", firstHalf, lastHalf));
        suite.addTest(new PeriodTest("testIntersects", lastHalf, firstHalf));
        suite.addTest(new PeriodTest("testIntersects", winter, monthMarch));
        suite.addTest(new PeriodTest("testIntersects", monthMarch, winter));
    	
        suite.addTest(new PeriodTest("testNotContains", monthMarch, monthMay));
        suite.addTest(new PeriodTest("testNotContains", monthMay, monthMarch));
        suite.addTest(new PeriodTest("testNotContains", monthMarch, monthApril));
        suite.addTest(new PeriodTest("testNotContains", monthApril, monthMarch));
        suite.addTest(new PeriodTest("testNotContains", firstHalf, lastHalf));
        suite.addTest(new PeriodTest("testNotContains", lastHalf, firstHalf));
        suite.addTest(new PeriodTest("testContains", winter, monthMarch));
        suite.addTest(new PeriodTest("testNotContains", monthMarch, winter));
        
        suite.addTest(new PeriodTest("testEquals", monthMarch.add(monthMay), marchToMay));
        suite.addTest(new PeriodTest("testEquals", monthMay.add(monthMarch), marchToMay));
        suite.addTest(new PeriodTest("testEquals", monthMarch.add(monthApril), marchToApril));
        suite.addTest(new PeriodTest("testEquals", monthApril.add(monthMarch), marchToApril));
        suite.addTest(new PeriodTest("testEquals", firstHalf.add(lastHalf), year1994));
        suite.addTest(new PeriodTest("testEquals", lastHalf.add(firstHalf), year1994));
        suite.addTest(new PeriodTest("testEquals", winter.add(monthMarch), winter));
        suite.addTest(new PeriodTest("testEquals",  monthMarch.add(winter), winter));
        
        // test period contained by subtraction..
        suite.addTest(new PeriodListTest("testIsEmpty", firstHalf.subtract(year1994)));
        suite.addTest(new PeriodListTest("testIsEmpty", winter.subtract(winter)));
        
        // test non-intersecting periods..
        suite.addTest(new PeriodListTest("testContains", winter.subtract(spring), winter));
        suite.addTest(new PeriodListTest(winter.subtract(spring), 1));
        
        // test intersecting periods..
        PeriodList aprToMay = marchToMay.subtract(marchToApril);
        suite.addTest(new PeriodListTest(aprToMay, 1));
        
        // test subtraction contained by period..
        suite.addTest(new PeriodListTest(year1994.subtract(monthApril), 2));
        
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        DateTime start = new DateTime("20081115T163800", registry.getTimeZone("Australia/Melbourne"));
        suite.addTest(new PeriodTest("testGetEndTimeZone", new Period(start, new Dur(1)), registry.getTimeZone("Australia/Melbourne")));
        
        start = new DateTime(start);
        start.setUtc(true);
        suite.addTest(new PeriodTest("testGetEndIsUtc", new Period(start, new Dur(1)), true));
        
        // other tests..
        suite.addTest(new PeriodTest("testTimezone"));
        suite.addTest(new PeriodTest("testEquals"));
        
        return suite;
    }
}
