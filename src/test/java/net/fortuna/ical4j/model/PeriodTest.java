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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.TimeZone;

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

    private static final Logger LOG = LoggerFactory.getLogger(PeriodTest.class);

    private Period period;
    
    private Temporal expectedDate;
    
    private Period expectedPeriod;
    
    private ZoneId expectedTimezone;
    
    private boolean expectedIsUtc;

    private TimeZone originalDefault;

    /**
     * @param period
     * @param expectedDate
     */
    public PeriodTest(String testMethod, Period period, Temporal expectedDate) {
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
    public PeriodTest(String testMethod, Period period, ZoneId expectedTimezone) {
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

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        originalDefault = java.util.TimeZone.getDefault();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        TimeZone.setDefault(originalDefault);
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
    	assertTrue(period.toInterval().isBefore(expectedPeriod.toInterval()));
    }

    /**
     * 
     */
    public void testNotBefore() {
    	assertFalse(period.toInterval().isBefore(expectedPeriod.toInterval()));
    }

    /**
     * 
     */
    public void testAfter() {
    	assertTrue(period.toInterval().isAfter(expectedPeriod.toInterval()));
    }

    /**
     * 
     */
    public void testNotAfter() {
    	assertFalse(period.toInterval().isAfter(expectedPeriod.toInterval()));
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
    	assertTrue(period.toInterval().overlaps(expectedPeriod.toInterval()));
    }

    /**
     * 
     */
    public void testNotIntersects() {
    	assertFalse(period.toInterval().overlaps(expectedPeriod.toInterval()));
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
    	assertTrue(period.toInterval().encloses(expectedPeriod.toInterval()));
    }

    /**
     * 
     */
    public void testNotContains()  {
    	assertFalse(period.toInterval().encloses(expectedPeriod.toInterval()));
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
        // change default tz to non-UTC timezone.
        java.util.TimeZone originalTzDefault = java.util.TimeZone.getDefault();
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Australia/Melbourne"));

        ZonedDateTime start = ZonedDateTime.now().plusYears(1);
        ZonedDateTime end = start.withZoneSameInstant(ZoneId.of("UTC"));

        Period<ZonedDateTime> p = new Period<>(start, end);
        
        LOG.info("Timezone test - period: [" + p + "]");
    }
    
    /**
     * Unit tests for {@link Period#isEmpty()}.
     */
    public void testIsEmpty() {
        ZonedDateTime start = ZonedDateTime.now();
        assertTrue(new Period<>(start, start).isEmpty());
        assertTrue(new Period<>(start, java.time.Period.ZERO).isEmpty());
        
        assertFalse(new Period<>(start, start.withSecond(1)).isEmpty());
        assertFalse(new Period<>(start, java.time.Duration.ofSeconds(1)).isEmpty());
    }
    
    /**
     * @return
     */
    public static Test suite() {
    	TestSuite suite = new TestSuite();

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

        // test period contained by subtraction..
//        suite.addTest(new PeriodListTest("testIsEmpty", firstHalf.subtract(year1994)));
//        suite.addTest(new PeriodListTest("testIsEmpty", winter.subtract(winter)));
        
        // test non-intersecting periods..
//        suite.addTest(new PeriodListTest("testContains", winter.subtract(spring), winter));
//        suite.addTest(new PeriodListTest(winter.subtract(spring), 1));
        
        // test intersecting periods..
//        PeriodList<ZonedDateTime> aprToMay = marchToMay.subtract(marchToApril);
//        suite.addTest(new PeriodListTest(aprToMay, 1));
        
        // test subtraction contained by period..
//        suite.addTest(new PeriodListTest(year1994.subtract(monthApril), 2));

//        ZoneId zoneId = TimeZoneRegistry.getGlobalZoneId("Australia/Melbourne");
//        ZonedDateTime start = ((LocalDateTime) TemporalAdapter.parse("20081115T163800").getTemporal())
//                .atZone(zoneId);
//        suite.addTest(new PeriodTest("testGetEndTimeZone", new Period<>(start, java.time.Period.ofWeeks(1)),
//                zoneId));
        
//        start = start.withZoneSameInstant(ZoneId.of("UTC"));
//        suite.addTest(new PeriodTest("testGetEndIsUtc", new Period<>(start, java.time.Period.ofWeeks(1)),
//                true));
        
        // other tests..
        suite.addTest(new PeriodTest("testTimezone"));
        suite.addTest(new PeriodTest("testEquals"));
        
        return suite;
    }
}
