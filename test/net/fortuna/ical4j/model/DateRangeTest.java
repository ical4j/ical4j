/*
 * $Id$
 *
 * Copyright (c) 2004, Ben Fortuna
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <p>DateRange Tester.</p>
 *
 * </p>Tests the behaviour of the DateRange class to make sure it acts in
 * the expected way.</p>
 * @see DateRange
 */
public class DateRangeTest extends TestCase
{
    private Date today;
    private Date past;
    private Date future;
    private Date begin1994;
    private Date end1994;
    private Date mar1994;
    private Date apr1994;
    private Date may1994;
    private Date jun1994;
    private Date jul1994;
    private DateRange year1994;
    private DateRange monthMarch;
    private DateRange monthApril;
    private DateRange monthMay;
    private DateRange firstHalf;
    private DateRange lastHalf;
    private DateRange winter;
    private DateRange spring;
    private DateRange marchToMay;
    private DateRange marchToApril;
    private DateRange duplicateRange;

    public DateRangeTest(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        java.util.Calendar cal = new GregorianCalendar(1980,
                java.util.Calendar.JANUARY, 23);
        past = cal.getTime();
        cal.set(2022, java.util.Calendar.FEBRUARY, 23);
        future = cal.getTime();
        cal.set(1994, java.util.Calendar.JANUARY, 1);
        begin1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.DECEMBER, 31);
        end1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.MARCH, 4);
        mar1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.APRIL, 12);
        apr1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.MAY, 19);
        may1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.JUNE, 22);
        jun1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.JULY, 29);
        jul1994 = cal.getTime();
        year1994 = new DateRange();
        year1994.setStartDate(begin1994);
        year1994.setEndDate(end1994);
        monthMarch = new DateRange();
        monthMarch.setStartDate(mar1994);
        monthMarch.setEndDate(apr1994);
        monthApril = new DateRange();
        monthApril.setStartDate(apr1994);
        monthApril.setEndDate(may1994);
        monthMay = new DateRange();
        monthMay.setStartDate(may1994);
        monthMay.setEndDate(jun1994);
        firstHalf = new DateRange();
        firstHalf.setStartDate(begin1994);
        firstHalf.setEndDate(jun1994);
        lastHalf = new DateRange();
        lastHalf.setStartDate(may1994);
        lastHalf.setEndDate(end1994);
        winter = new DateRange();
        winter.setStartDate(begin1994);
        winter.setEndDate(apr1994);
        spring = new DateRange();
        spring.setStartDate(apr1994);
        spring.setEndDate(jul1994);
        marchToMay = new DateRange();
        marchToMay.setStartDate(mar1994);
        marchToMay.setEndDate(jun1994);
        marchToApril = new DateRange();
        marchToApril.setStartDate(mar1994);
        marchToApril.setEndDate(may1994);
        duplicateRange = new DateRange();
        duplicateRange.setStartDate(begin1994);
        duplicateRange.setEndDate(end1994);
        today = new Date();
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
        today = null;
        past = null;
        future = null;
    }


    /**
     * <ul>
     * <li>Get the start date when none set</li>
     * <li>Get the end date when none set</li>
     * <li>Get the start date when start date set but not end date</li>
     * <li>Get the end date when start date set but not end date</li>
     * <li>Get the start date when end date set but not start</li>
     * <li>Get the end date when end date set but not start</li>
     * <li>Get the start date when start date before end date</li>
     * <li>Get the end date when start date before end date</li>
     * <li>Get the start date when start date after end date</li>
     * <li>Get the end date when start date after end date</li>
     * </ul>
     * @throws Exception
     */
    public void testGetStartEndDate() throws Exception
    {
        long testMillis;
        long todayMillis;
        todayMillis = today.getTime();
        DateRange testRange;

        testRange = new DateRange();
        testMillis = testRange.getStartDate().getTime();
        assertTrue("Uninitialized start date should have been set to NOW",
                (todayMillis - testMillis) < 5000);

        testRange = new DateRange();
        testMillis = testRange.getEndDate().getTime();
        assertTrue("Uninitialized end date should have been set to NOW",
                (todayMillis - testMillis) < 5000);

        testRange = new DateRange();
        testRange.setStartDate(past);
        assertEquals(past, testRange.getStartDate());

        testMillis = testRange.getEndDate().getTime();
        assertTrue("No end date with set start date should have been set to NOW",
                (todayMillis - testMillis) < 5000);

        testRange.setEndDate(future);
        assertEquals(past, testRange.getStartDate());

        assertEquals(future, testRange.getEndDate());

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
    }

    /**
     * date is before
     * date is during
     * date is after
     * date is at start
     * date is at end
     * @throws Exception
     */
    public void testIncludes() throws Exception
    {
        assertFalse("includes() claims 1980 in 1994", year1994.includes(past));
        assertTrue("includes() claims march 1994 is not in 1994",
                year1994.includes(mar1994));
        assertFalse("includes() claims 2022 in 1994",
                year1994.includes(future));
        assertTrue("includes() claims Jan 1 1994 not in 1994",
                year1994.includes(begin1994));
        assertTrue("includes() claims Dec 31 1994 not in 1994",
                year1994.includes(end1994));
    }

    /**
     * test date before range
     * test date after range
     * test date during range
     * test beginning of range
     * test end of range
     * @throws Exception
     */
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

    /**
     * test range before range
     * test range after range
     * test range contained in range
     * test range containing range
     * test overlapping before range
     * test overlapping after range
     * test butted together at start ranges
     *
     * @throws Exception
     */
    public void testBeforeWithRange() throws Exception
    {
        assertTrue("before() claims March month isn't before May month",
                monthMarch.before(monthMay));
        assertFalse("before() claims May month is before March month",
                monthMay.before(monthMarch));
        assertFalse("before() claims Winter is before March month",
                winter.before(monthMarch));
        assertFalse("before() claims March month is before Winter",
                monthMarch.before(winter));
        assertFalse("before() claims overlapping halves are before each other",
                firstHalf.before(lastHalf));
        assertFalse("before() claims disordered halves are before each other",
                lastHalf.before(firstHalf));
        assertTrue("before() claims March month isn't before April month",
                monthMarch.before(monthApril));
        assertFalse("before() claims April month is before March month",
                monthApril.before(monthMarch));
    }

    /**
     * test date before range
     * test date after range
     * test date during range
     * test beginning of range
     * test end of range
     * @throws Exception
     */
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

    /**
     * test range before range
     * test range after range
     * test range contained in range
     * test range containing range
     * test overlapping before range
     * test overlapping after range
     * test butted together at start ranges
     * @throws Exception
     */
    public void testAfterWithRange() throws Exception
    {
        assertFalse("after() claims March month is after May month",
                monthMarch.after(monthMay));
        assertTrue("after() claims May month isn't after March month",
                monthMay.after(monthMarch));
        assertFalse("after() claims Winter is after March month",
                winter.after(monthMarch));
        assertFalse("after() claims March month is after Winter",
                monthMarch.after(winter));
        assertFalse("after() claims overlapping halves are after each other",
                firstHalf.after(lastHalf));
        assertFalse("after() claims disordered halves are after each other",
                lastHalf.after(firstHalf));
        assertFalse("after() claims March month is after April month",
                monthMarch.after(monthApril));
        assertTrue("after() claims April month isn't after March month",
                monthApril.after(monthMarch));

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
    public void testOverlaps() throws Exception
    {
        assertFalse("overlaps() claims March month is overlapping May month",
                monthMarch.overlaps(monthMay));
        assertFalse("overlaps() claims May month is overlapping March month",
                monthMay.overlaps(monthMarch));
        assertFalse("overlaps() claims March month is overlapping April month",
                monthMarch.overlaps(monthApril));
        assertFalse("overlaps() claims April month is overlapping March month",
                monthApril.overlaps(monthMarch));
        assertTrue("overlaps() claims overlapping halves are not overlapping each other",
                firstHalf.overlaps(lastHalf));
        assertTrue("overlaps() claims disordered halves are not overlapping each other",
                lastHalf.overlaps(firstHalf));
        assertTrue("overlaps() claims Winter isn't overlapping March month",
                winter.overlaps(monthMarch));
        assertTrue("overlaps() claims March month isn't overlapping Winter",
                monthMarch.overlaps(winter));
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
    public void testContains() throws Exception
    {
        assertFalse("contains() claims March month is containing May month",
                monthMarch.contains(monthMay));
        assertFalse("contains() claims May month is containing March month",
                monthMay.contains(monthMarch));
        assertFalse("contains() claims March month is containing April month",
                monthMarch.contains(monthApril));
        assertFalse("contains() claims April month is containing March month",
                monthApril.contains(monthMarch));
        assertFalse("contains() claims overlapping halves are containing each other",
                firstHalf.contains(lastHalf));
        assertFalse("contains() claims disordered halves are containing each other",
                lastHalf.contains(firstHalf));
        assertTrue("contains() claims Winter isn't containing March month",
                winter.contains(monthMarch));
        assertFalse("contains() claims March month is containing Winter",
                monthMarch.contains(winter));
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
    public void testAdd() throws Exception
    {
        assertEquals(marchToMay, monthMarch.add(monthMay));
        assertEquals(marchToMay, monthMay.add(monthMarch));
        assertEquals(marchToApril, monthMarch.add(monthApril));
        assertEquals(marchToApril, monthApril.add(monthMarch));
        assertEquals(year1994, firstHalf.add(lastHalf));
        assertEquals(year1994, lastHalf.add(firstHalf));
        assertEquals(winter, winter.add(monthMarch));
        assertEquals(winter,  monthMarch.add(winter));
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

    }

    public static Test suite()
    {
        return new TestSuite(DateRangeTest.class);
    }
}
