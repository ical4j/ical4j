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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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

    private PeriodList<LocalDate> periodList;

    private PeriodList<LocalDate> expectedPeriodList;
    
    private int expectedSize;
    
    private Period<LocalDate> expectedPeriod;
    
    /**
     * @param periodList
     * @param periodList2
     */
    public PeriodListTest(PeriodList<LocalDate> periodList, PeriodList<LocalDate> expectedPeriodList) {
        super("testEquals");
        this.periodList = periodList;
        this.expectedPeriodList = expectedPeriodList;
    }
    
    /**
     * @param periodList
     * @param expectedSize
     */
    public PeriodListTest(PeriodList<LocalDate> periodList, int expectedSize) {
        super("testSize");
        this.periodList = periodList;
        this.expectedSize = expectedSize;
    }
    
    /**
     * @param periodList
     * @param expectedFirstPeriod
     */
    public PeriodListTest(String testMethod, PeriodList<LocalDate> periodList, Period<LocalDate> expectedPeriod) {
        super(testMethod);
        this.periodList = periodList;
        this.expectedPeriod = expectedPeriod;
    }
    
    /**
     * @param testMethod
     * @param periodList
     */
    public PeriodListTest(String testMethod, PeriodList<LocalDate> periodList) {
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
    	assertTrue(periodList.getPeriods().isEmpty());
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
        LocalDate begin1994 = LocalDate.now().withYear(1994).withMonth(1).withDayOfMonth(1);
        LocalDate end1994 = begin1994.withMonth(12).withDayOfMonth(31);
        LocalDate jan1994 = end1994.withMonth(1).withDayOfMonth(22);
        LocalDate feb1994 = jan1994.withMonth(2).withDayOfMonth(15);
        LocalDate mar1994 = feb1994.withMonth(3).withDayOfMonth(4);
        LocalDate apr1994 = mar1994.withMonth(4).withDayOfMonth(12);
        LocalDate may1994 = apr1994.withMonth(5).withDayOfMonth(19);
        LocalDate jun1994 = may1994.withMonth(6).withDayOfMonth(21);
        LocalDate jul1994 = jun1994.withMonth(7).withDayOfMonth(28);
        LocalDate aug1994 = jul1994.withMonth(8).withDayOfMonth(20);
        LocalDate sep1994 = aug1994.withMonth(9).withDayOfMonth(17);
        LocalDate oct1994 = sep1994.withMonth(10).withDayOfMonth(29);
        LocalDate nov1994 = oct1994.withMonth(11).withDayOfMonth(11);
        LocalDate dec1994 = nov1994.withMonth(12).withDayOfMonth(2);

        Period<LocalDate> monthJanuary = new Period<>(jan1994, feb1994);
        Period<LocalDate> monthFebruary = new Period<>(feb1994, mar1994);
        Period<LocalDate> monthMarch = new Period<>(mar1994, apr1994);
        Period<LocalDate> monthApril = new Period<>(apr1994, may1994);
        Period<LocalDate> monthMay = new Period<>(may1994, jun1994);
        Period<LocalDate> monthJune = new Period<>(jun1994, jul1994);
        Period<LocalDate> monthJuly = new Period<>(jul1994, aug1994);
        Period<LocalDate> monthAugust = new Period<>(aug1994, sep1994);
        Period<LocalDate> monthSeptember = new Period<>(sep1994, oct1994);
        Period<LocalDate> monthOctober = new Period<>(oct1994, nov1994);
        Period<LocalDate> monthNovember = new Period<>(nov1994, dec1994);
        Period<LocalDate> monthDecember = new Period<>(dec1994, end1994);
        Period<LocalDate> head1994 = new Period<>(begin1994, jan1994);
        Period<LocalDate> tail1994 = new Period<>(dec1994, end1994);

        // create sets that contain the ranges
        List<Period<LocalDate>> oddMonths = new ArrayList<>();
        oddMonths.add(monthJanuary);
        oddMonths.add(monthMarch);
        oddMonths.add(monthMay);
        oddMonths.add(monthJuly);
        oddMonths.add(monthSeptember);
        oddMonths.add(monthNovember);
        List<Period<LocalDate>> tailSet = new ArrayList<>();
        tailSet.add(tail1994);

        /*
         * assertNull("Removing null from a null set should return null", empty1.subtract(null)); assertNull("Removing
         * from a null set should return null", normalizer.subtractDateRanges(null, headSet));
         */
        PeriodList<LocalDate> evenMonths = new PeriodList<>();
        evenMonths.add(monthFebruary);
        evenMonths.add(monthApril);
        evenMonths.add(monthJune);
        evenMonths.add(monthAugust);
        evenMonths.add(monthOctober);
        evenMonths.add(monthDecember);
        
        PeriodList<LocalDate> headSet = new PeriodList<>();
        headSet.add(head1994);
        
        PeriodList<LocalDate> empty1 = new PeriodList<>();
        PeriodList<LocalDate> empty2 = new PeriodList<>();
        
        suite.addTest(new PeriodListTest(evenMonths.subtract(null), evenMonths));
        suite.addTest(new PeriodListTest(empty1.subtract(empty2), empty1));
        suite.addTest(new PeriodListTest(headSet.subtract(empty1), headSet));
        suite.addTest(new PeriodListTest(evenMonths.subtract(empty1), evenMonths));

        // other tests..
        suite.addTest(new PeriodListTest("testTimezone"));
        suite.addTest(new PeriodListTest("testNormalise"));
        
        return suite;
    }

    public final void testPeriodListSort() {
        PeriodList<LocalDate> periods = new PeriodList<>();
        LocalDate start = LocalDate.now();
        LocalDate end = start.withDayOfMonth(25);
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

        PeriodList<Instant> list = new PeriodList<>(CalendarDateFormat.UTC_DATE_TIME_FORMAT);

        for (int i = 0; i < 5; i++) {
            Instant start = Instant.now();
            Instant end = start.plusSeconds(ChronoUnit.DAYS.getDuration().getSeconds());

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
        
        assertTrue(periods.normalise().getPeriods().isEmpty());
    }
}
