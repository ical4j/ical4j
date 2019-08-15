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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 13/02/2005
 *
 * $Id$
 *
 * @author Ben Fortuna
 */
public class PeriodListTest extends TestCase {

    private Logger log = LoggerFactory.getLogger(PeriodListTest.class);

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
        assertEquals(expectedSize, periodList.getPeriods().size());
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
        assertEquals(expectedPeriod, periodList.getPeriods().toArray()[0]);
    }
    
    public void testContains() {
    	assertTrue(periodList.getPeriods().contains(expectedPeriod));
    }
    
    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        // create ranges that are intervals
        ZonedDateTime begin1994 = ZonedDateTime.now().withYear(1994).withMonth(1).withDayOfMonth(1);
        ZonedDateTime end1994 = begin1994.withMonth(12).withDayOfMonth(31);
        ZonedDateTime jan1994 = end1994.withMonth(1).withDayOfMonth(22);
        ZonedDateTime feb1994 = jan1994.withMonth(2).withDayOfMonth(15);
        ZonedDateTime mar1994 = feb1994.withMonth(3).withDayOfMonth(4);
        ZonedDateTime apr1994 = mar1994.withMonth(4).withDayOfMonth(12);
        ZonedDateTime may1994 = apr1994.withMonth(5).withDayOfMonth(19);
        ZonedDateTime jun1994 = may1994.withMonth(6).withDayOfMonth(21);
        ZonedDateTime jul1994 = jun1994.withMonth(7).withDayOfMonth(28);
        ZonedDateTime aug1994 = jul1994.withMonth(8).withDayOfMonth(20);
        ZonedDateTime sep1994 = aug1994.withMonth(9).withDayOfMonth(17);
        ZonedDateTime oct1994 = sep1994.withMonth(10).withDayOfMonth(29);
        ZonedDateTime nov1994 = oct1994.withMonth(11).withDayOfMonth(11);
        ZonedDateTime dec1994 = nov1994.withMonth(12).withDayOfMonth(2);

        Period<ZonedDateTime> monthJanuary = new Period<>(jan1994, feb1994);
        Period<ZonedDateTime> monthFebruary = new Period<>(feb1994, mar1994);
        Period<ZonedDateTime> monthMarch = new Period<>(mar1994, apr1994);
        Period<ZonedDateTime> monthApril = new Period<>(apr1994, may1994);
        Period<ZonedDateTime> monthMay = new Period<>(may1994, jun1994);
        Period<ZonedDateTime> monthJune = new Period<>(jun1994, jul1994);
        Period<ZonedDateTime> monthJuly = new Period<>(jul1994, aug1994);
        Period<ZonedDateTime> monthAugust = new Period<>(aug1994, sep1994);
        Period<ZonedDateTime> monthSeptember = new Period<>(sep1994, oct1994);
        Period<ZonedDateTime> monthOctober = new Period<>(oct1994, nov1994);
        Period<ZonedDateTime> monthNovember = new Period<>(nov1994, dec1994);
        Period<ZonedDateTime> monthDecember = new Period<>(dec1994, end1994);
        Period<ZonedDateTime> head1994 = new Period<>(begin1994, jan1994);
        Period<ZonedDateTime> tail1994 = new Period<>(dec1994, end1994);

        // create sets that contain the ranges
        List<Period<ZonedDateTime>> oddMonths = new ArrayList<>();
        oddMonths.add(monthJanuary);
        oddMonths.add(monthMarch);
        oddMonths.add(monthMay);
        oddMonths.add(monthJuly);
        oddMonths.add(monthSeptember);
        oddMonths.add(monthNovember);
        List<Period<ZonedDateTime>> tailSet = new ArrayList<>();
        tailSet.add(tail1994);

        /*
         * assertNull("Removing null from a null set should return null", empty1.subtract(null)); assertNull("Removing
         * from a null set should return null", normalizer.subtractDateRanges(null, headSet));
         */
        PeriodList<ZonedDateTime> evenMonths = new PeriodList<>();
        evenMonths.add(monthFebruary);
        evenMonths.add(monthApril);
        evenMonths.add(monthJune);
        evenMonths.add(monthAugust);
        evenMonths.add(monthOctober);
        evenMonths.add(monthDecember);
        
        PeriodList<ZonedDateTime> headSet = new PeriodList<>();
        headSet.add(head1994);
        
        PeriodList<ZonedDateTime> empty1 = new PeriodList<>();
        PeriodList<ZonedDateTime> empty2 = new PeriodList<>();
        
        suite.addTest(new PeriodListTest(evenMonths.subtract(null), evenMonths));
        suite.addTest(new PeriodListTest(empty1.subtract(empty2), empty1));
        suite.addTest(new PeriodListTest(headSet.subtract(empty1), headSet));
        suite.addTest(new PeriodListTest(evenMonths.subtract(empty1), evenMonths));
        
        // add disjoint ranges..
        PeriodList<ZonedDateTime> periodList1 = new PeriodList<>();
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);
        PeriodList<ZonedDateTime> periodList2 = new PeriodList<>();
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
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum, new Period<>(jul1994, aug1994)));

        // add one range containing another..
        periodList1 = new PeriodList<>();
        periodList1.add(monthOctober);
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList<>();
        periodList2.add(monthNovember);

        /*
         * SortedSet normalizedSet = normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);
         */
        sum = periodList1.add(periodList2);
        suite.addTest(new PeriodListTest(sum, 1));
//        Period lonePeriod = (Period) sum.toArray()[0];
//        assertEquals(lonePeriod.getStart(), oct1994);
//        assertEquals(lonePeriod.getEnd(), end1994);
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum, new Period<>(oct1994, end1994)));

        // Test Intersecting Periods
        periodList1 = new PeriodList<>();
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList<>();
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
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum, new Period<>(oct1994, end1994)));

        // Test adding adjacent periods.
        periodList1 = new PeriodList<>();
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList<>();
        periodList2.add(monthOctober);

        /*
         * SortedSet normalizedSet = normalizer.addDateRanges(dateRangeSet1, dateRangeSet2);
         */
        sum = periodList1.add(periodList2);
        suite.addTest(new PeriodListTest(sum, 1));
//        Period lonePeriod = (Period) sum.toArray()[0];
//        assertEquals(lonePeriod.getStart(), oct1994);
//        assertEquals(lonePeriod.getEnd(), end1994);
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum, new Period<>(oct1994, end1994)));

        // Test adding the same range twice
        periodList1 = new PeriodList<>();
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList<>();
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
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum1, new Period<>(oct1994, end1994)));

        PeriodList sum2 = periodList1.add(periodList2);
        suite.addTest(new PeriodListTest(sum2, 1));
//        Period lonePeriod2 = (Period) sum2.toArray()[0];
//        assertEquals(lonePeriod2.getStart(), oct1994);
//        assertEquals(lonePeriod2.getEnd(), end1994);
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum2, new Period<>(oct1994, end1994)));

        // Test subtract a containing date range set..
        periodList1 = new PeriodList<>();
        periodList1.add(monthSeptember);
        periodList1.add(monthOctober);
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList<>();
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
        suite.addTest(new PeriodListTest("testFirstPeriodEquals", sum, new Period<>(sep1994, oct1994)));

        // FIXME: don't use asserts here..
        Period lonePeriod2 = (Period) sum.getPeriods().toArray()[1];
        assertEquals(lonePeriod2.getStart(), dec1994);
        assertEquals(lonePeriod2.getEnd(), end1994);

        // Test removing a Disjoint Set of Date Ranges..
        periodList1 = new PeriodList<>();
        periodList1.add(monthSeptember);
        periodList1.add(monthOctober);
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList<>();
        periodList2.add(monthApril);
        periodList2.add(monthMay);

        /*
         * SortedSet normalizedSet = normalizer.subtractDateRanges(dateRangeSet1, dateRangeSet2);
         */
        sum = periodList1.subtract(periodList2);
        suite.addTest(new PeriodListTest(sum, periodList1));

        // SubtractSameRangesTwice...
        periodList1 = new PeriodList<>();
        periodList1.add(monthSeptember);
        periodList1.add(monthOctober);
        periodList1.add(monthNovember);
        periodList1.add(monthDecember);

        periodList2 = new PeriodList<>();
        periodList2.add(monthOctober);
        periodList2.add(monthNovember);

        PeriodList<ZonedDateTime> expectedResult = new PeriodList<>();
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
        PeriodList<ZonedDateTime> periods = new PeriodList<>();
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = start.withDayOfMonth(25);
        periods.add(new Period<>(start, end));
        periods.add(new Period<>(end, java.time.Duration.ofHours(2)));
        periods.add(new Period<>(start, java.time.Duration.ofHours(2)));
        periods.add(new Period<>(start, java.time.Duration.ofHours(1)));

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

        PeriodList<ZonedDateTime> list = new PeriodList<>();

        for (int i = 0; i < 5; i++) {
            ZonedDateTime start = ZonedDateTime.now();
            ZonedDateTime end = start.plusDays(1);

            list.add(new Period<>(start, end));
        }
        
        log.info("Timezone test - period list: [" + list + "]");

        list.getPeriods().forEach(p -> {
            assertTrue(p.toString().endsWith("Z"));
            assertTrue(p.toString().endsWith("Z"));
        });
    }
    
    /**
     * Unit tests for {@link PeriodList#normalise()}.
     */
    public void testNormalise() {
        // test a list of periods consuming no time..
        PeriodList<ZonedDateTime> periods = new PeriodList<>();
        ZonedDateTime start = ZonedDateTime.now();
        periods.add(new Period<>(start, start));
        periods.add(new Period<>(start, start));
        
        assertTrue(periods.normalise().isEmpty());
    }
}
