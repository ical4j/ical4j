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

package net.fortuna.ical4j.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateRange;
import net.fortuna.ical4j.model.parameter.Value;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * DateRangeNormalizer Tester.
 * @see DateRangeNormalizer
 */
public class DateRangeNormalizerTest extends TestCase {

    private DateRangeNormalizer normalizer = DateRangeNormalizer.getInstance();
    private Date begin1994;
    private Date end1994;
    private Date jan1994;
    private Date feb1994;
    private Date mar1994;
    private Date apr1994;
    private Date may1994;
    private Date jun1994;
    private Date jul1994;
    private Date aug1994;
    private Date sep1994;
    private Date oct1994;
    private Date nov1994;
    private Date dec1994;
    private DateRange year1994;
    private DateRange monthJanuary;
    private DateRange monthFebruary;
    private DateRange monthMarch;
    private DateRange monthApril;
    private DateRange monthMay;
    private DateRange monthJune;
    private DateRange monthJuly;
    private DateRange monthAugust;
    private DateRange monthSeptember;
    private DateRange monthOctober;
    private DateRange monthNovember;
    private DateRange monthDecember;
    private DateRange head1994;
    private DateRange tail1994;
    private DateRange firstHalf; // from year begin to jun
    private DateRange lastHalf; // jul to year end
    private DateRange firstQuarter; // jan feb mar
    private DateRange secondQuarter; // apr may jun
    private DateRange thirdQuarter; // jul aug sep
    private DateRange fourthQuarter; // oct nov dec
    private DateRange fiscal1994; //jan - dec
    private DateRange winter; // head jan feb
    private DateRange spring; // mar apr may jun
    private DateRange summer; // jul aug sep oct
    private DateRange fall; // nov dec tail
    private DateRange marchToMay; // mar apr may
    private DateRange aprilToJune; // apr may jun
    private DateRange marchToApril; // mar apr
    private DateRange februaryToMay; //feb mar apr may
    private SortedSet oddMonths;
    private SortedSet evenMonths;
    private SortedSet headSet;
    private SortedSet tailSet;

    public DateRangeNormalizerTest(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        // create ranges that are intervals
        java.util.Calendar cal = new GregorianCalendar(1994,
                java.util.Calendar.JANUARY, 1);
        begin1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.DECEMBER, 31);
        end1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.JANUARY, 22);
        jan1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.FEBRUARY, 15);
        feb1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.MARCH, 4);
        mar1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.APRIL, 12);
        apr1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.MAY, 19);
        may1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.JUNE, 21);
        jun1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.JULY, 28);
        jul1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.AUGUST, 20);
        aug1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.SEPTEMBER, 17);
        sep1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.OCTOBER, 29);
        oct1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.NOVEMBER, 11);
        nov1994 = cal.getTime();
        cal.set(1994, java.util.Calendar.DECEMBER, 2);
        dec1994 = cal.getTime();
        year1994 = new DateRange();
        year1994.setStartDate(begin1994);
        year1994.setEndDate(end1994);
        monthJanuary = new DateRange();
        monthJanuary.setStartDate(jan1994);
        monthJanuary.setEndDate(feb1994);
        monthFebruary = new DateRange();
        monthFebruary.setStartDate(feb1994);
        monthFebruary.setEndDate(mar1994);
        monthMarch = new DateRange();
        monthMarch.setStartDate(mar1994);
        monthMarch.setEndDate(apr1994);
        monthApril = new DateRange();
        monthApril.setStartDate(apr1994);
        monthApril.setEndDate(may1994);
        monthMay = new DateRange();
        monthMay.setStartDate(may1994);
        monthMay.setEndDate(jun1994);
        monthJune = new DateRange();
        monthJune.setStartDate(jun1994);
        monthJune.setEndDate(jul1994);
        monthJuly = new DateRange();
        monthJuly.setStartDate(jul1994);
        monthJuly.setEndDate(aug1994);
        monthAugust = new DateRange();
        monthAugust.setStartDate(aug1994);
        monthAugust.setEndDate(sep1994);
        monthSeptember = new DateRange();
        monthSeptember.setStartDate(sep1994);
        monthSeptember.setEndDate(oct1994);
        monthOctober = new DateRange();
        monthOctober.setStartDate(oct1994);
        monthOctober.setEndDate(nov1994);
        monthNovember = new DateRange();
        monthNovember.setStartDate(nov1994);
        monthNovember.setEndDate(dec1994);
        monthDecember = new DateRange();
        monthDecember.setStartDate(dec1994);
        monthDecember.setEndDate(end1994);
        head1994 = new DateRange();
        head1994.setStartDate(begin1994);
        head1994.setEndDate(jan1994);
        tail1994 = new DateRange();
        tail1994.setStartDate(dec1994);
        tail1994.setEndDate(end1994);
        firstHalf = new DateRange();
        firstHalf.setStartDate(begin1994);
        firstHalf.setEndDate(jun1994);
        lastHalf = new DateRange();
        lastHalf.setStartDate(jul1994);
        lastHalf.setEndDate(end1994);
        firstQuarter = new DateRange();
        firstQuarter.setStartDate(jan1994);
        firstQuarter.setEndDate(apr1994);
        secondQuarter = new DateRange();
        secondQuarter.setStartDate(apr1994);
        secondQuarter.setEndDate(jul1994);
        thirdQuarter = new DateRange();
        thirdQuarter.setStartDate(jul1994);
        thirdQuarter.setEndDate(oct1994);
        fourthQuarter = new DateRange();
        fourthQuarter.setStartDate(oct1994);
        fourthQuarter.setEndDate(dec1994);
        fiscal1994 = new DateRange();
        fiscal1994.setStartDate(jan1994);
        fiscal1994.setEndDate(dec1994);
        winter = new DateRange();
        winter.setStartDate(begin1994);
        winter.setEndDate(mar1994);
        spring = new DateRange();
        spring.setStartDate(mar1994);
        spring.setEndDate(jul1994);
        summer = new DateRange();
        summer.setStartDate(jul1994);
        summer.setEndDate(nov1994);
        fall = new DateRange();
        fall.setStartDate(nov1994);
        fall.setEndDate(end1994);
        marchToMay = new DateRange();
        marchToMay.setStartDate(mar1994);
        marchToMay.setEndDate(jun1994);
        marchToApril = new DateRange();
        marchToApril.setStartDate(mar1994);
        marchToApril.setEndDate(may1994);
        aprilToJune = new DateRange();
        aprilToJune.setStartDate(apr1994);
        aprilToJune.setEndDate(jul1994);
        februaryToMay = new DateRange();
        februaryToMay.setStartDate(feb1994);
        februaryToMay.setEndDate(jun1994);

        // create sets that contain the ranges
        oddMonths = new TreeSet();
        oddMonths.add(monthJanuary);
        oddMonths.add(monthMarch);
        oddMonths.add(monthMay);
        oddMonths.add(monthJuly);
        oddMonths.add(monthSeptember);
        oddMonths.add(monthNovember);
        evenMonths = new TreeSet();
        evenMonths.add(monthFebruary);
        evenMonths.add(monthApril);
        evenMonths.add(monthJune);
        evenMonths.add(monthAugust);
        evenMonths.add(monthOctober);
        evenMonths.add(monthDecember);
        headSet = new TreeSet();
        headSet.add(head1994);
        tailSet = new TreeSet();
        tailSet.add(tail1994);
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * test null init and add
     * test null init
     * test null add
     * test empty init and add
     * test empty init
     * test empty add
     *
     * @throws Exception
     */
    public void testEmptyAddDateRanges() throws Exception
    {
        SortedSet empty1 = new TreeSet();
        SortedSet empty2 = new TreeSet();
        assertNull("Normalizing null sets should return null",
                normalizer.addDateRanges(null, null));
        assertEquals(headSet, normalizer.addDateRanges(null, headSet));
        assertEquals(evenMonths, normalizer.addDateRanges(evenMonths, null));
        assertEquals(empty1, normalizer.addDateRanges(empty1, empty2));
        assertEquals(headSet, normalizer.addDateRanges(empty1, headSet));
        assertEquals(evenMonths, normalizer.addDateRanges(evenMonths, empty1));
    }

    /**
     * test null curr and remove
     * test null curr
     * test null remove
     * test empty curr and remove
     * test empty curr
     * test empty remove
     *
     * @throws Exception
     */
    public void testEmptySubtractDateRanges() throws Exception
    {
        SortedSet empty1 = new TreeSet();
        SortedSet empty2 = new TreeSet();
        assertNull("Removing null from a null set should return null",
                normalizer.subtractDateRanges(null, null));
        assertNull("Removing from a null set should return null",
                normalizer.subtractDateRanges(null, headSet));
        assertEquals(evenMonths, normalizer.subtractDateRanges(evenMonths, null));
        assertEquals(empty1, normalizer.subtractDateRanges(empty1, empty2));
        assertEquals(headSet, normalizer.subtractDateRanges(headSet, empty1));
        assertEquals(evenMonths, normalizer.subtractDateRanges(evenMonths, empty1));
    }

    /**
     *
     * Test null dateList
     * Test empty dateList
     * Test Jan/Feb/Mar dateList
     *
     * @throws Exception
     */
    public void testCreateDateRangeSet() throws Exception {

        // Test null dateList
        assertNull(normalizer.createDateRangeSet(null, 0));

        // Test empty dateList
        DateList emptyDateList = new DateList(Value.DATE_TIME);
        assertEquals(normalizer.createDateRangeSet(emptyDateList, 0).size(), 0);

        // Test Jan/Feb/Mar dateList
        DateList dateList1 = new DateList(Value.DATE_TIME);
        final long EIGHT_HOURS = 1000 * 60 * 60 * 8;

        dateList1.add(jan1994);    // Jan 22
        dateList1.add(feb1994);    // Feb 15
        dateList1.add(mar1994);    // Mar 4

        SortedSet dateRangeSet =
                        normalizer.createDateRangeSet(dateList1, EIGHT_HOURS);
        Object[] objArray = dateRangeSet.toArray();
        DateRange[] dateRangeArray = new DateRange[objArray.length];

        for (int i = 0; i < objArray.length; i++) {
            dateRangeArray[i] = (DateRange) objArray[i];
        }

        assertEquals(dateRangeArray[0].getStartDate(), jan1994);
        assertEquals(dateRangeArray[0].getEndDate(),
                     new Date(jan1994.getTime() + EIGHT_HOURS));

        assertEquals(dateRangeArray[1].getStartDate(), feb1994);
        assertEquals(dateRangeArray[1].getEndDate(),
                     new Date(feb1994.getTime() + EIGHT_HOURS));

        assertEquals(dateRangeArray[2].getStartDate(), mar1994);
        assertEquals(dateRangeArray[2].getEndDate(),
                     new Date(mar1994.getTime() + EIGHT_HOURS));
    }

    /**
     * Test null ranges
     *
     * @throws Exception
     */
    public void testAddNullDateRanges() throws Exception {

        // Test null ranges.
        assertNull(normalizer.addDateRanges(null, null));
    }

    /**
     * add disjoint ranges
     *
     * @throws Exception
     */
    public void testAddDisjointRange() throws Exception {

        SortedSet dateRangeSet1 = new TreeSet();
        SortedSet dateRangeSet2 = new TreeSet();

        dateRangeSet1.add(monthNovember);
        dateRangeSet1.add(monthDecember);

        dateRangeSet2.add(monthJuly);
        dateRangeSet2.add(monthNovember);

        SortedSet normalizedSet =
                        normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);

        assertEquals(normalizedSet.size(), 2);
        DateRange loneDateRange = (DateRange) normalizedSet.toArray()[0];
        assertEquals(loneDateRange.getStartDate(), jul1994);
        assertEquals(loneDateRange.getEndDate(), aug1994);
    }

    /**
     * add one range containing another
     *
     * @throws Exception
     */
    public void testAddContainingRange() throws Exception {

        SortedSet dateRangeSet1 = new TreeSet();
        SortedSet dateRangeSet2 = new TreeSet();

        dateRangeSet1.add(monthOctober);
        dateRangeSet1.add(monthNovember);
        dateRangeSet1.add(monthDecember);

        dateRangeSet2.add(monthNovember);

        SortedSet normalizedSet =
                        normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);

        assertEquals(normalizedSet.size(), 1);
        DateRange loneDateRange = (DateRange) normalizedSet.toArray()[0];
        assertEquals(loneDateRange.getStartDate(), oct1994);
        assertEquals(loneDateRange.getEndDate(), end1994);
    }

    /**
     * add overlapping ranges
     *
     * @throws Exception
     */
    public void testAddOverlappingDateRanges() throws Exception {

        
        // Test Overlapping Ranges
        SortedSet dateRangeSet1 = new TreeSet();
        SortedSet dateRangeSet2 = new TreeSet();

        dateRangeSet1.add(monthNovember);
        dateRangeSet1.add(monthDecember);

        dateRangeSet2.add(monthOctober);
        dateRangeSet2.add(monthNovember);
        SortedSet normalizedSet =
                        normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);

        assertEquals(normalizedSet.size(), 1);
        DateRange loneDateRange = (DateRange) normalizedSet.toArray()[0];
        assertEquals(loneDateRange.getStartDate(), oct1994);
        assertEquals(loneDateRange.getEndDate(), end1994);
    }

    /**
     * add adjacent ranges
     *
     * @throws Exception
     */
    public void testAddAdjacentRanges() throws Exception {

        // Test adding adjacent ranges.
        SortedSet dateRangeSet1 = new TreeSet();
        SortedSet dateRangeSet2 = new TreeSet();

        dateRangeSet1.add(monthNovember);
        dateRangeSet1.add(monthDecember);

        dateRangeSet2.add(monthOctober);

        SortedSet normalizedSet =
                        normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);

        assertEquals(normalizedSet.size(), 1);
        DateRange loneDateRange = (DateRange) normalizedSet.toArray()[0];
        assertEquals(loneDateRange.getStartDate(), oct1994);
        assertEquals(loneDateRange.getEndDate(), end1994);
    }

    /**
     * add the same range twice
     *
     * @throws Exception
     */
    public void testAddSameRangeTwice() throws Exception {

        // Test adding the same range twice
        SortedSet dateRangeSet1 = new TreeSet();
        SortedSet dateRangeSet2 = new TreeSet();

        dateRangeSet1.add(monthNovember);
        dateRangeSet1.add(monthDecember);

        dateRangeSet2.add(monthOctober);
        dateRangeSet2.add(monthNovember);
        SortedSet normalizedSet1 =
                        normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);
        SortedSet normalizedSet2 =
                        normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);

        assertEquals(normalizedSet1.size(), 1);
        DateRange loneDateRange1 = (DateRange) normalizedSet1.toArray()[0];
        assertEquals(loneDateRange1.getStartDate(), oct1994);
        assertEquals(loneDateRange1.getEndDate(), end1994);

        assertEquals(normalizedSet2.size(), 1);
        DateRange loneDateRange2 = (DateRange) normalizedSet2.toArray()[0];
        assertEquals(loneDateRange2.getStartDate(), oct1994);
        assertEquals(loneDateRange2.getEndDate(), end1994);
    }

    /**
     * Test subtract null range sets.
     *
     * @throws Exception
     */
    public void testSubtractNullDateRanges() throws Exception {
        assertNull(normalizer.subtractDateRanges(null, null));
    }

    /**
     * Test subtract a containing date range set.
     *
     * @throws Exception
     */
    public void testSubtractContainingRange() throws Exception {

        SortedSet dateRangeSet1 = new TreeSet();
        SortedSet dateRangeSet2 = new TreeSet();

        dateRangeSet1.add(monthSeptember);
        dateRangeSet1.add(monthOctober);
        dateRangeSet1.add(monthNovember);
        dateRangeSet1.add(monthDecember);

        dateRangeSet2.add(monthOctober);
        dateRangeSet2.add(monthNovember);

        SortedSet normalizedSet =
                   normalizer.subtractDateRanges(dateRangeSet1, dateRangeSet2);

        assertEquals(normalizedSet.size(), 2);
        DateRange loneDateRange1 = (DateRange) normalizedSet.toArray()[0];
        assertEquals(loneDateRange1.getStartDate(), sep1994);
        assertEquals(loneDateRange1.getEndDate(), oct1994);

        DateRange loneDateRange2 = (DateRange) normalizedSet.toArray()[1];
        assertEquals(loneDateRange2.getStartDate(), dec1994);
        assertEquals(loneDateRange2.getEndDate(), end1994);
    }

    /**
     * Test removing a Disjoint Set of Date Ranges.
     *
     * @throws Exception
     */
    public void testSubtractDisjointDateRanges() throws Exception {

        SortedSet dateRangeSet1 = new TreeSet();
        SortedSet dateRangeSet2 = new TreeSet();

        dateRangeSet1.add(monthSeptember);
        dateRangeSet1.add(monthOctober);
        dateRangeSet1.add(monthNovember);
        dateRangeSet1.add(monthDecember);

        dateRangeSet2.add(monthApril);
        dateRangeSet2.add(monthMay);

        SortedSet normalizedSet =
                   normalizer.subtractDateRanges(dateRangeSet1, dateRangeSet2);

        assertEquals(normalizedSet, dateRangeSet1);
    }

    /**
     *
     *
     * @throws Exception
     */
    public void testSubtractSameRangesTwice() throws Exception {

        SortedSet dateRangeSet1 = new TreeSet();
        SortedSet dateRangeSet2 = new TreeSet();
        SortedSet expectedResultSet = new TreeSet();

        expectedResultSet.add(monthSeptember);
        expectedResultSet.add(monthDecember);

        dateRangeSet1.add(monthSeptember);
        dateRangeSet1.add(monthOctober);
        dateRangeSet1.add(monthNovember);
        dateRangeSet1.add(monthDecember);

        dateRangeSet2.add(monthOctober);
        dateRangeSet2.add(monthNovember);

        SortedSet normalizedSet =
                   normalizer.subtractDateRanges(dateRangeSet1, dateRangeSet2);

        assertEquals(normalizedSet, expectedResultSet);

        normalizedSet =
                   normalizer.subtractDateRanges(dateRangeSet1, dateRangeSet2);

        assertEquals(normalizedSet, expectedResultSet);
    }

    public static Test suite() {
        return new TestSuite(DateRangeNormalizerTest.class);
    }
}
