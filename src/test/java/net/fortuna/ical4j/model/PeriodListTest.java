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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created on 13/02/2005
 *
 * $Id$
 *
 * @author Ben Fortuna
 */
public class PeriodListTest extends TestCase {

    private static Log log = LogFactory.getLog(PeriodListTest.class);

    private PeriodList periodList;

    private PeriodList expectedPeriodList;
    
    private int expectedSize;
    
    private Period expectedPeriod;
    
    /**
     * @param periodList
     * @param periodList2
     */
    public PeriodListTest(PeriodList periodList, PeriodList expectedPeriodList) {
        super("testEquals");
        this.periodList = periodList;
        this.expectedPeriodList = expectedPeriodList;
    }
    
    /**
     * @param periodList
     * @param expectedSize
     */
    public PeriodListTest(PeriodList periodList, int expectedSize) {
        super("testSize");
        this.periodList = periodList;
        this.expectedSize = expectedSize;
    }
    
    /**
     * @param periodList
     * @param expectedFirstPeriod
     */
    public PeriodListTest(String testMethod, PeriodList periodList, Period expectedPeriod) {
        super(testMethod);
        this.periodList = periodList;
        this.expectedPeriod = expectedPeriod;
    }
    
    /**
     * @param testMethod
     * @param periodList
     */
    public PeriodListTest(String testMethod, PeriodList periodList) {
    	super(testMethod);
    	this.periodList = periodList;
    }
    
    /**
     * @param testMethod
     */
    public PeriodListTest(String testMethod) {
        super(testMethod);
    }
    
    /**
     * 
     */
    public void testEquals() {
        assertEquals(expectedPeriodList, periodList);
    }
    
    /**
     * 
     */
    public void testSize() {
        assertEquals(expectedSize, periodList.size());
    }
    
    /**
     * 
     */
    public void testIsEmpty() {
    	assertTrue(periodList.isEmpty());
    }
    
    /**
     * 
     */
    public void testFirstPeriodEquals() {
        assertEquals(expectedPeriod, periodList.toArray()[0]);
    }
    
    public void testContains() {
    	assertTrue(periodList.contains(expectedPeriod));
    }
    
    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        // create ranges that are intervals
        java.util.Calendar cal = new GregorianCalendar(1994,
                java.util.Calendar.JANUARY, 1);
        DateTime begin1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.DECEMBER, 31);
        DateTime end1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.JANUARY, 22);
        DateTime jan1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.FEBRUARY, 15);
        DateTime feb1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.MARCH, 4);
        DateTime mar1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.APRIL, 12);
        DateTime apr1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.MAY, 19);
        DateTime may1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.JUNE, 21);
        DateTime jun1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.JULY, 28);
        DateTime jul1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.AUGUST, 20);
        DateTime aug1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.SEPTEMBER, 17);
        DateTime sep1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.OCTOBER, 29);
        DateTime oct1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.NOVEMBER, 11);
        DateTime nov1994 = new DateTime(cal.getTime().getTime());
        cal.set(1994, java.util.Calendar.DECEMBER, 2);
        DateTime dec1994 = new DateTime(cal.getTime().getTime());
        Period monthJanuary = new Period(jan1994, feb1994);
        Period monthFebruary = new Period(feb1994, mar1994);
        Period monthMarch = new Period(mar1994, apr1994);
        Period monthApril = new Period(apr1994, may1994);
        Period monthMay = new Period(may1994, jun1994);
        Period monthJune = new Period(jun1994, jul1994);
        Period monthJuly = new Period(jul1994, aug1994);
        Period monthAugust = new Period(aug1994, sep1994);
        Period monthSeptember = new Period(sep1994, oct1994);
        Period monthOctober = new Period(oct1994, nov1994);
        Period monthNovember = new Period(nov1994, dec1994);
        Period monthDecember = new Period(dec1994, end1994);
        Period head1994 = new Period(begin1994, jan1994);
        Period tail1994 = new Period(dec1994, end1994);

        // create sets that contain the ranges
        PeriodList oddMonths = new PeriodList();
        oddMonths.add(monthJanuary);
        oddMonths.add(monthMarch);
        oddMonths.add(monthMay);
        oddMonths.add(monthJuly);
        oddMonths.add(monthSeptember);
        oddMonths.add(monthNovember);
        PeriodList tailSet = new PeriodList();
        tailSet.add(tail1994);

        /*
         * assertNull("Removing null from a null set should return null", empty1.subtract(null)); assertNull("Removing
         * from a null set should return null", normalizer.subtractDateRanges(null, headSet));
         */
        PeriodList evenMonths = new PeriodList();
        evenMonths.add(monthFebruary);
        evenMonths.add(monthApril);
        evenMonths.add(monthJune);
        evenMonths.add(monthAugust);
        evenMonths.add(monthOctober);
        evenMonths.add(monthDecember);
        
        PeriodList headSet = new PeriodList();
        headSet.add(head1994);
        
        PeriodList empty1 = new PeriodList();
        PeriodList empty2 = new PeriodList();
        
        suite.addTest(new PeriodListTest(evenMonths.subtract(null), evenMonths));
        suite.addTest(new PeriodListTest(empty1.subtract(empty2), empty1));
        suite.addTest(new PeriodListTest(headSet.subtract(empty1), headSet));
        suite.addTest(new PeriodListTest(evenMonths.subtract(empty1), evenMonths));
        
        // add disjoint ranges..
        PeriodList periodList1 = new PeriodList();
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);
        PeriodList periodList2 = new PeriodList();
        periodList2.add(monthJuly);
        periodList2.add(monthNovember);

        /*
         * SortedSet normalizedSet = normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);
         */
        PeriodList sum = periodList1.add(periodList2);
        suite.addTest(new PeriodListTest(sum, 2));
//        Period lonePeriod = (Period) sum.toArray()[0];
//        assertEquals(lonePeriod.getStart(), jul1994);
//        assertEquals(lonePeriod.getEnd(), aug1994);
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum, new Period(jul1994, aug1994)));

        // add one range containing another..
        periodList1 = new PeriodList();
        periodList1.add(monthOctober);
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList();
        periodList2.add(monthNovember);

        /*
         * SortedSet normalizedSet = normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);
         */
        sum = periodList1.add(periodList2);
        suite.addTest(new PeriodListTest(sum, 1));
//        Period lonePeriod = (Period) sum.toArray()[0];
//        assertEquals(lonePeriod.getStart(), oct1994);
//        assertEquals(lonePeriod.getEnd(), end1994);
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum, new Period(oct1994, end1994)));

        // Test Intersecting Periods
        periodList1 = new PeriodList();
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList();
        periodList2.add(monthOctober);
        periodList2.add(monthNovember);

        /*
         * SortedSet normalizedSet = normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);
         */
        sum = periodList1.add(periodList2);
        suite.addTest(new PeriodListTest(sum, 1));
//        Period lonePeriod = (Period) sum.toArray()[0];
//        assertEquals(lonePeriod.getStart(), oct1994);
//        assertEquals(lonePeriod.getEnd(), end1994);
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum, new Period(oct1994, end1994)));

        // Test adding adjacent periods.
        periodList1 = new PeriodList();
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList();
        periodList2.add(monthOctober);

        /*
         * SortedSet normalizedSet = normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);
         */
        sum = periodList1.add(periodList2);
        suite.addTest(new PeriodListTest(sum, 1));
//        Period lonePeriod = (Period) sum.toArray()[0];
//        assertEquals(lonePeriod.getStart(), oct1994);
//        assertEquals(lonePeriod.getEnd(), end1994);
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum, new Period(oct1994, end1994)));

        // Test adding the same range twice
        periodList1 = new PeriodList();
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList();
        periodList2.add(monthOctober);
        periodList2.add(monthNovember);

        /*
         * SortedSet normalizedSet1 = normalizer.addDateRanges(dateRangeSet1, dateRangeSet2); SortedSet normalizedSet2 =
         * normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);
         */
        PeriodList sum1 = periodList1.add(periodList2);
        suite.addTest(new PeriodListTest(sum1, 1));
//        Period lonePeriod1 = (Period) sum1.toArray()[0];
//        assertEquals(lonePeriod1.getStart(), oct1994);
//        assertEquals(lonePeriod1.getEnd(), end1994);
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum1, new Period(oct1994, end1994)));

        PeriodList sum2 = periodList1.add(periodList2);
        suite.addTest(new PeriodListTest(sum2, 1));
//        Period lonePeriod2 = (Period) sum2.toArray()[0];
//        assertEquals(lonePeriod2.getStart(), oct1994);
//        assertEquals(lonePeriod2.getEnd(), end1994);
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum2, new Period(oct1994, end1994)));

        // Test subtract a containing date range set..
        periodList1 = new PeriodList();
        periodList1.add(monthSeptember);
        periodList1.add(monthOctober);
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList();
        periodList2.add(monthOctober);
        periodList2.add(monthNovember);

        /*
         * SortedSet normalizedSet = normalizer.subtractDateRanges(dateRangeSet1, dateRangeSet2);
         */
        sum = periodList1.subtract(periodList2);
        suite.addTest(new PeriodListTest(sum, 2));
//        Period lonePeriod1 = (Period) sum.toArray()[0];
//        assertEquals(lonePeriod1.getStart(), sep1994);
//        assertEquals(lonePeriod1.getEnd(), oct1994);
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum, new Period(sep1994, oct1994)));

        // FIXME: don't use asserts here..
        Period lonePeriod2 = (Period) sum.toArray()[1];
        assertEquals(lonePeriod2.getStart(), dec1994);
        assertEquals(lonePeriod2.getEnd(), end1994);

        // Test removing a Disjoint Set of Date Ranges..
        periodList1 = new PeriodList();
        periodList1.add(monthSeptember);
        periodList1.add(monthOctober);
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList();
        periodList2.add(monthApril);
        periodList2.add(monthMay);

        /*
         * SortedSet normalizedSet = normalizer.subtractDateRanges(dateRangeSet1, dateRangeSet2);
         */
        sum = periodList1.subtract(periodList2);
        suite.addTest(new PeriodListTest(sum, periodList1));

        // SubtractSameRangesTwice...
        periodList1 = new PeriodList();
        periodList1.add(monthSeptember);
        periodList1.add(monthOctober);
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList();
        periodList2.add(monthOctober);
        periodList2.add(monthNovember);

        PeriodList expectedResult = new PeriodList();
        expectedResult.add(monthSeptember);
        expectedResult.add(monthDecember);

        /*
         * SortedSet normalizedSet = normalizer.subtractDateRanges(dateRangeSet1, dateRangeSet2);
         */
        sum = periodList1.subtract(periodList2);
        suite.addTest(new PeriodListTest(sum, expectedResult));

        /*
         * normalizedSet = normalizer.subtractDateRanges(dateRangeSet1, dateRangeSet2);
         */
        sum = periodList1.subtract(periodList2);
        suite.addTest(new PeriodListTest(sum, expectedResult));
        
        // other tests..
        suite.addTest(new PeriodListTest("testTimezone"));
        suite.addTest(new PeriodListTest("testNormalise"));
        
        return suite;
    }

    public final void testPeriodListSort() {
        PeriodList periods = new PeriodList();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 25);
        periods.add(new Period(new DateTime(), new DateTime(cal.getTime()
                .getTime())));
        periods.add(new Period(new DateTime(cal.getTime().getTime()), new Dur(
                0, 2, 0, 0)));
        periods.add(new Period(new DateTime(), new Dur(0, 2, 0, 0)));
        periods.add(new Period(new DateTime(), new Dur(0, 1, 0, 0)));

        // log.info("Unsorted list: " + periods);

        // Collections.sort(periods);

        log.info("Sorted list: " + periods);
    }

    /**
     * test null init and add test null init test null add test empty init and add test empty init test empty add
     * @throws Exception
     */
    /*
     * public void testEmptyAddPeriods() throws Exception { SortedSet empty1 = new TreeSet(); SortedSet empty2 = new
     * TreeSet(); assertNull("Normalizing null sets should return null", normalizer.addDateRanges(null, null));
     * assertEquals(headSet, normalizer.addDateRanges(null, headSet)); assertEquals(evenMonths,
     * normalizer.addDateRanges(evenMonths, null)); assertEquals(empty1, normalizer.addDateRanges(empty1, empty2));
     * assertEquals(headSet, normalizer.addDateRanges(empty1, headSet)); assertEquals(evenMonths,
     * normalizer.addDateRanges(evenMonths, empty1)); }
     */

    /**
     * Test null dateList Test empty dateList Test Jan/Feb/Mar dateList
     * @throws Exception
     */
    /*
     * public void testCreateDateRangeSet() throws Exception { // Test null dateList
     * assertNull(normalizer.createDateRangeSet(null, 0)); // Test empty dateList DateList emptyDateList = new
     * DateList(Value.DATE_TIME); assertEquals(normalizer.createDateRangeSet(emptyDateList, 0).size(), 0); // Test
     * Jan/Feb/Mar dateList DateList dateList1 = new DateList(Value.DATE_TIME); final long EIGHT_HOURS = 1000 * 60 * 60 *
     * 8; dateList1.add(jan1994); // Jan 22 dateList1.add(feb1994); // Feb 15 dateList1.add(mar1994); // Mar 4 SortedSet
     * dateRangeSet = normalizer.createDateRangeSet(dateList1, EIGHT_HOURS); Object[] objArray = dateRangeSet.toArray();
     * DateRange[] dateRangeArray = new DateRange[objArray.length]; for (int i = 0; i < objArray.length; i++) {
     * dateRangeArray[i] = (DateRange) objArray[i]; } assertEquals(dateRangeArray[0].getStartDate(), jan1994);
     * assertEquals(dateRangeArray[0].getEndDate(), new Date(jan1994.getTime() + EIGHT_HOURS));
     * assertEquals(dateRangeArray[1].getStartDate(), feb1994); assertEquals(dateRangeArray[1].getEndDate(), new
     * Date(feb1994.getTime() + EIGHT_HOURS)); assertEquals(dateRangeArray[2].getStartDate(), mar1994);
     * assertEquals(dateRangeArray[2].getEndDate(), new Date(mar1994.getTime() + EIGHT_HOURS)); }
     */

    /**
     * Test null ranges
     * @throws Exception
     */
    /*
     * public void testAddNullDateRanges() throws Exception { // Test null ranges.
     * assertNull(normalizer.addDateRanges(null, null)); }
     */

    /**
     * Test subtract null range sets.
     * @throws Exception
     */
    /*
     * public void testSubtractNullDateRanges() throws Exception { assertNull(normalizer.subtractDateRanges(null,
     * null)); }
     */

    /**
     * Test timezone functionality.
     */
    public void testTimezone() {
//        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance()
//                .createRegistry();
//        TimeZone timezone = registry.getTimeZone("Australia/Melbourne");

        PeriodList list = new PeriodList(true);
        java.util.Calendar cal = java.util.Calendar.getInstance();

        for (int i = 0; i < 5; i++) {
            DateTime start = new DateTime(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
            DateTime end = new DateTime(cal.getTime());

            list.add(new Period(start, end));
        }
        
        log.info("Timezone test - period list: [" + list + "]");
        
        for (Iterator i = list.iterator(); i.hasNext();) {
            Period p = (Period) i.next();
            assertTrue(p.getStart().isUtc());
            assertTrue(p.getEnd().isUtc());
        }
    }
    
    /**
     * Unit tests for {@link PeriodList#normalise()}.
     */
    public void testNormalise() {
        // test a list of periods consuming no time..
        PeriodList periods = new PeriodList();
        DateTime start = new DateTime();
        periods.add(new Period(start, start));
        DateTime start2 = new DateTime();
        periods.add(new Period(start2, start2));
        
        assertTrue(periods.normalise().isEmpty());
    }
}
