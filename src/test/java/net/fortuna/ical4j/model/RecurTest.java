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
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.util.TimeZones;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created on 14/02/2005
 *
 * $Id$
 *
 * @author Ben Fortuna
 */
public class RecurTest extends TestCase {
    
    private static Log log = LogFactory.getLog(RecurTest.class);

    private static final Locale testLocale = Locale.US;

    private TimeZone originalDefault;
    
    private Recur recur;
    
    private Date periodStart;
    
    private Date periodEnd;
    
    private Value value;
    
    private int expectedCount;
    
    private Date seed;
    
    private Date expectedDate;
    
    private int calendarField;
    
    private int expectedCalendarValue;
    
    private net.fortuna.ical4j.model.TimeZone expectedTimeZone;
    
    private String recurrenceString;
    
    private String expectedFrequency;
    
    private int expectedInterval;
    
    private WeekDayList expectedDayList;
    
    /**
     * @param testMethod
     * @param recur
     * @param periodStart
     * @param periodEnd
     * @param value
     */
    public RecurTest(String testMethod, Recur recur, Date seed, Date periodStart, Date periodEnd, Value value) {
        super(testMethod);
        this.recur = recur;
        this.seed = seed;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.value = value;
    }
    
    /**
     * @param recur
     * @param periodStart
     * @param periodEnd
     * @param value
     * @param expectedCount
     */
    public RecurTest(Recur recur, Date periodStart, Date periodEnd, Value value, int expectedCount) {
        this(recur, null, periodStart, periodEnd, value, expectedCount);
    }
    
    /**
     * @param recur
     * @param seed
     * @param periodStart
     * @param periodEnd
     * @param value
     * @param expectedCount
     */
    public RecurTest(Recur recur, Date seed, Date periodStart, Date periodEnd, Value value, int expectedCount) {
        this("testGetDatesCount", recur, seed, periodStart, periodEnd, value);
        this.expectedCount = expectedCount;
    }
    
    /**
     * @param recur
     * @param seed
     * @param periodStart
     * @param expectedDate
     */
    public RecurTest(Recur recur, Date seed, Date periodStart, Date expectedDate) {
        this("testGetNextDate", recur, seed, periodStart, null, null);
        this.expectedDate = expectedDate;
    }
    
    /**
     * @param recur
     * @param periodStart
     * @param periodEnd
     * @param value
     * @param calendarField
     * @param expectedCalendarValue
     */
    public RecurTest(Recur recur, Date periodStart, Date periodEnd, Value value,
            int calendarField, int expectedCalendarValue) {
        this("testGetDatesCalendarField", recur, null, periodStart, periodEnd, value);
        this.calendarField = calendarField;
        this.expectedCalendarValue = expectedCalendarValue;
    }

    /**
     * @param recur
     * @param periodStart
     * @param periodEnd
     * @param value
     * @param expectedTimeZone
     */
    public RecurTest(Recur recur, Date periodStart, Date periodEnd, Value value,
            net.fortuna.ical4j.model.TimeZone expectedTimeZone) {
        this("testGetDatesTimeZone", recur, null, periodStart, periodEnd, value);
        this.expectedTimeZone = expectedTimeZone;
    }
    
    /**
     * @param recurrenceString
     */
    public RecurTest(String recurrenceString) {
        super("testInvalidRecurrenceString");
        this.recurrenceString = recurrenceString;
    }

    /**
     * @param recurrenceString
     * @param expectedFrequency
     * @param expectedInterval
     * @param expectedDayList
     */
    public RecurTest(String recurrenceString, String expectedFrequency, int expectedInterval, WeekDayList expectedDayList) {
        super("testRecurrenceString");
        this.recurrenceString = recurrenceString;
        this.expectedFrequency = expectedFrequency;
        this.expectedInterval = expectedInterval;
        this.expectedDayList = expectedDayList;
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        originalDefault = TimeZone.getDefault();
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        TimeZone.setDefault(originalDefault);
    }

    /**
     * 
     */
    public void testGetDatesCount() {
        DateList dates = null;
        if (seed != null) {
            dates = recur.getDates(seed, periodStart, periodEnd, value);
        }
        else {
            dates = recur.getDates(periodStart, periodEnd, value);
        }
//        assertEquals(expectedCount, dates.size());
        assertTrue("Date list exceeds expected count", dates.size() <= expectedCount);
    }
    
    /**
     * 
     */
    public void testGetNextDate() {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(testLocale);
            assertEquals(expectedDate, recur.getNextDate(seed, periodStart));
        } finally {
            Locale.setDefault(defaultLocale);
        }
        
    }
    
    /**
     * 
     */
    public void testGetDatesCalendarField() {
        DateList dates = recur.getDates(periodStart, periodEnd, value);
        Calendar cal;
        if ((value != null) && (value == Value.DATE)) {
            cal = Calendar.getInstance(TimeZones.getDateTimeZone());
        } else {
            cal = Calendar.getInstance();
        }
         
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date recurrence = (Date) i.next();
            cal.setTime(recurrence);
            assertEquals(expectedCalendarValue, cal.get(calendarField));
        }
    }

    /**
     * 
     */
    public void testGetDatesOrdering() {
        DateList dl1 = recur.getDates(periodStart, periodEnd, value); 
        Date prev = null;
        Date event = null;
        for(int i=0; i<dl1.size(); i++) {
            prev = event;
            event = (Date) dl1.get(i); 
            log.debug("Occurence "+i+" at "+event);
            assertTrue(prev == null || !prev.after(event));
        }
    }
    
    /**
     * 
     */
    public void testGetDatesNotEmpty() {
        assertFalse(recur.getDates(periodStart, periodEnd, value).isEmpty());
    }
    
    /**
     * 
     */
    public void testGetDatesTimeZone() {
        DateList dates = recur.getDates(periodStart, periodEnd, value);
        for (Iterator i = dates.iterator(); i.hasNext();) {
            DateTime recurrence = (DateTime) i.next();
            assertEquals(expectedTimeZone, recurrence.getTimeZone());
        }
    }
    
    /**
     * @throws ParseException
     */
    public void testInvalidRecurrenceString() throws ParseException {
        try {
            new Recur(recurrenceString);
            fail("IllegalArgumentException not thrown!");
        }
        catch (IllegalArgumentException e) {
            // expected
            log.info("Caught exception: " + e.getMessage());
        }
    }
    
    /**
     * @throws ParseException
     */
    public void testRecurrenceString() throws ParseException {
        Recur recur = new Recur(recurrenceString);
        assertEquals(expectedFrequency, recur.getFrequency());
        assertEquals(expectedInterval, recur.getInterval());
        assertEquals(expectedDayList, recur.getDayList());
    }
    
    /**
     * 
     */
    public void testGetDatesWithBase() {
        /*
         *  Here is an example of evaluating multiple BYxxx rule parts.
         *
         *    DTSTART;TZID=US-Eastern:19970105T083000
         *    RRULE:FREQ=YEARLY;INTERVAL=2;BYMONTH=1;BYDAY=SU;BYHOUR=8,9;
         *     BYMINUTE=30
         */
        Calendar testCal = Calendar.getInstance();
        testCal.set(Calendar.YEAR, 1997);
        testCal.set(Calendar.MONTH, 1);
        testCal.set(Calendar.DAY_OF_MONTH, 5);
        testCal.set(Calendar.HOUR, 8);
        testCal.set(Calendar.MINUTE, 30);
        testCal.set(Calendar.SECOND, 0);
        
        Recur recur = new Recur(Recur.YEARLY, -1);
        recur.setInterval(2);
        recur.getMonthList().add(new Integer(1));
        recur.getDayList().add(WeekDay.SU);
        recur.getHourList().add(new Integer(8));
        recur.getHourList().add(new Integer(9));
        recur.getMinuteList().add(new Integer(30));

        Calendar cal = Calendar.getInstance();
        Date start = new DateTime(cal.getTime().getTime());
        cal.add(Calendar.YEAR, 2);
        Date end = new DateTime(cal.getTime().getTime());
        log.debug(recur);
        
        DateList dates = recur.getDates(new DateTime(testCal.getTime()), start, end, Value.DATE_TIME);
        log.debug(dates);
    }

    /*
    public void testSublistNegative() {
        List list = new LinkedList();
        list.add("1");
        list.add("2");
        list.add("3");
        assertSublistEquals(list, list, 0);
        assertSublistEquals(asList("3"), list, -1);
        assertSublistEquals(asList("2"), list, -2);
        assertSublistEquals(asList("1"), list, -3);
        assertSublistEquals(list, list, -4);
    }

    public void testSublistPositive() {
        List list = new LinkedList();
        list.add("1");
        list.add("2");
        list.add("3");
        assertSublistEquals(list, list, 0);
        assertSublistEquals(asList("1"), list, 1);
        assertSublistEquals(asList("2"), list, 2);
        assertSublistEquals(asList("3"), list, 3);
        assertSublistEquals(list, list, 4);
    }

    private void assertSublistEquals(List expected, List list, int offset) {
        List sublist = new LinkedList();
        Recur.sublist(list, offset, sublist);
        assertEquals(expected, sublist);
    }

    private List asList(Object o) {
        List list = new LinkedList();
        list.add(o);
        return list;
    }

    public void testSetPosNegative() throws Exception {
        Date[] dates = new Date[] { new Date(1), new Date(2), new Date(3) };
        Date[] expected = new Date[] { new Date(3), new Date(2) };
        assertSetPosApplied(expected, dates, "BYSETPOS=-1,-2");
    }

    public void testSetPosPositve() throws Exception {
        Date[] dates = new Date[] { new Date(1), new Date(2), new Date(3) };
        Date[] expected = new Date[] { new Date(2), new Date(3) };
        assertSetPosApplied(expected, dates, "BYSETPOS=2,3");
    }

    public void testSetPosOutOfBounds() throws Exception {
        Date[] dates = new Date[] { new Date(1) };
        Date[] expected = new Date[] {};
        assertSetPosApplied(expected, dates, "BYSETPOS=-2,2");
    }

    private void assertSetPosApplied(Date[] expected, Date[] dates, String rule)
            throws Exception {
        Recur recur = new Recur(rule);
        DateList expectedList = asDateList(expected);
        assertEquals(expectedList, recur.applySetPosRules(asDateList(dates)));
    }

    private DateList asDateList(Date[] dates) {
        DateList dateList = new DateList(Value.DATE);
        dateList.addAll(Arrays.asList(dates));
        return dateList;
    }
    */
    
    /**
     * This test confirms SETPOS rules are working correctly.
     * <pre>
     *      The BYSETPOS rule part specifies a COMMA character (US-ASCII decimal
     *      44) separated list of values which corresponds to the nth occurrence
     *      within the set of events specified by the rule. Valid values are 1 to
     *      366 or -366 to -1. It MUST only be used in conjunction with another
     *      BYxxx rule part. For example "the last work day of the month" could
     *      be represented as:
     *   
     *        RRULE:FREQ=MONTHLY;BYDAY=MO,TU,WE,TH,FR;BYSETPOS=-1
     * </pre>
     */
    public final void testSetPosProcessing() {
        Recur recur = new Recur(Recur.MONTHLY, -1);
        recur.getDayList().add(WeekDay.MO);
        recur.getDayList().add(WeekDay.TU);
        recur.getDayList().add(WeekDay.WE);
        recur.getDayList().add(WeekDay.TH);
        recur.getDayList().add(WeekDay.FR);
        recur.getSetPosList().add(new Integer(-1));
        log.debug(recur);

        Calendar cal = Calendar.getInstance();
        Date start = new DateTime(cal.getTime());
        cal.add(Calendar.YEAR, 2);
        Date end = new DateTime(cal.getTime());
        
        DateList dates = recur.getDates(start, end, Value.DATE_TIME);
        log.debug(dates);
    }
    
    public void testMgmill2001() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 11);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.YEAR, 2005);
        java.util.Date eventStart = cal.getTime();
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        java.util.Date rangeStart = cal.getTime();
        
        cal.set(Calendar.YEAR, 2009);
        java.util.Date rangeEnd = cal.getTime();
        
        // FREQ=MONTHLY;INTERVAL=1;COUNT=4;BYMONTHDAY=2
        Recur recur = new Recur(Recur.MONTHLY, 4);
        recur.setInterval(1);
        recur.getMonthDayList().add(new Integer(2));
        getDates(rangeStart, rangeEnd, eventStart, recur);
        
        // FREQ=MONTHLY;INTERVAL=2;COUNT=4;BYDAY=2MO
        recur = new Recur(Recur.MONTHLY, 4);
        recur.setInterval(2);
        recur.getDayList().add(new WeekDay(WeekDay.MO, 2));
        getDates(rangeStart, rangeEnd, eventStart, recur);
        
        // FREQ=YEARLY;COUNT=4;BYMONTH=2;BYMONTHDAY=3
        recur = new Recur(Recur.YEARLY, 4);
        recur.getMonthList().add(new Integer(2));
        recur.getMonthDayList().add(new Integer(3));
        getDates(rangeStart, rangeEnd, eventStart, recur);
        
        // FREQ=YEARLY;COUNT=4;BYMONTH=2;BYDAY=2SU
        recur = new Recur(Recur.YEARLY, 4);
        recur.getMonthList().add(new Integer(2));
        recur.getDayList().add(new WeekDay(WeekDay.SU, 2));
        getDates(rangeStart, rangeEnd, eventStart, recur);
    }
    
    private void getDates(java.util.Date startRange, java.util.Date endRange, java.util.Date eventStart, Recur recur) { 
        
        net.fortuna.ical4j.model.Date start = new net.fortuna.ical4j.model.Date(startRange); 
        net.fortuna.ical4j.model.Date end = new net.fortuna.ical4j.model.Date(endRange); 
        net.fortuna.ical4j.model.Date seed = new net.fortuna.ical4j.model.Date(eventStart); 
         
        DateList dates = recur.getDates(seed, start, end, Value.DATE); 
        for (int i=0; i<dates.size(); i++) { 
            log.info("date_" + i + " = " + dates.get(i).toString()); 
        } 
    }
    
    public void testGetDatesRalph() throws ParseException {
        Recur recur = new
        Recur("FREQ=WEEKLY;WKST=MO;INTERVAL=1;UNTIL=20051003T000000Z;BYDAY=MO,WE");

        Calendar queryStartDate = new
        GregorianCalendar(TimeZone.getTimeZone(TimeZones.UTC_ID));
        queryStartDate.set(2005, Calendar.SEPTEMBER, 3, 0,
        0, 0);

        Calendar queryEndDate = new
        GregorianCalendar(TimeZone.getTimeZone(TimeZones.UTC_ID));
        queryEndDate.set(2005, Calendar.OCTOBER, 31, 23,
        59, 0);

        DateList dateList = recur.getDates(new
        DateTime(queryStartDate.getTime()), new
        DateTime(queryStartDate.getTime()), new
        DateTime(queryEndDate.getTime()), Value.DATE_TIME);
        
        log.debug(dateList);
    }
    
    /**
     * 
     */
    /*
    public void testInvalidRule() throws ParseException {
//        String rrule = "FREQ=DAILY;COUNT=60;BYDAY=TU,TH;BYSETPOS=2";
//        Recur recur = new Recur(rrule);

        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.HOUR);
//        java.util.Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
//        java.util.Date end = cal.getTime();

//        DateList recurrences = recur.getDates(new DateTime(start),
//                new DateTime(end), Value.DATE_TIME);
    }
    */
    
    /**
     * @return
     * @throws ParseException 
     */
    public static TestSuite suite() throws ParseException {
        TestSuite suite = new TestSuite();

        // java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Europe/Paris"));

        // testGetDates..
        Recur recur = new Recur(Recur.DAILY, 10);
        recur.setInterval(2);
        log.debug(recur);
        
        Calendar cal = Calendar.getInstance();
        Date start = new Date(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, 10);
        Date end = new Date(cal.getTime().getTime());
        log.debug(recur.getDates(start, end, Value.DATE_TIME));        
        
        recur.setUntil(new Date(cal.getTime().getTime()));
        log.info(recur);
        log.debug(recur.getDates(start, end, Value.DATE_TIME));
        
        recur.setFrequency(Recur.WEEKLY);
        recur.getDayList().add(WeekDay.MO);
        log.debug(recur);

        DateList dates = recur.getDates(start, end, Value.DATE);
        log.debug(dates);
        
        suite.addTest(new RecurTest(recur, start, end, Value.DATE, 10));
        
        // testGetNextDate..
        recur = new Recur(Recur.DAILY, 3);
        Date seed = new Date("20080401"); 
        Date firstDate = new Date("20080402");
        Date secondDate = new Date("20080403");
        
        suite.addTest(new RecurTest(recur, seed, seed, firstDate));
        suite.addTest(new RecurTest(recur, seed, firstDate, secondDate));
        suite.addTest(new RecurTest(recur, seed, secondDate, null));
        
        // test DateTime
        recur = new Recur(Recur.WEEKLY, new DateTime("20080421T063000"));
        seed = new DateTime("20080407T063000");
        firstDate = new DateTime("20080414T063000");
        secondDate = new DateTime("20080421T063000");
        
        suite.addTest(new RecurTest(recur, seed, seed, firstDate));
        suite.addTest(new RecurTest(recur, seed, firstDate, secondDate));
        suite.addTest(new RecurTest(recur, seed, secondDate, null));
        
        // Test BYDAY rules..
        recur = new Recur(Recur.DAILY, 10);
        recur.setInterval(1);
        recur.getDayList().add(WeekDay.MO);
        recur.getDayList().add(WeekDay.TU);
        recur.getDayList().add(WeekDay.WE);
        recur.getDayList().add(WeekDay.TH);
        recur.getDayList().add(WeekDay.FR);
        log.debug(recur);
        
        cal = Calendar.getInstance();
        start = new Date(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, 10);
        end = new Date(cal.getTime().getTime());
        
        suite.addTest(new RecurTest(recur, start, end, Value.DATE, 10));

        // Test BYDAY recurrence rules..
        String rrule = "FREQ=MONTHLY;WKST=SU;INTERVAL=2;BYDAY=5TU";
        recur = new Recur(rrule);

        cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        start = new Date(cal.getTime());
        cal.add(Calendar.YEAR, 2);
        end = new Date(cal.getTime());

        suite.addTest(new RecurTest(recur, start, end, Value.DATE, Calendar.WEEK_OF_MONTH, 5));

        /*
         * This test creates a rule outside of the specified boundaries to
         * confirm that the returned date list is empty.
         * <pre>
         *  Weekly on Tuesday and Thursday for 5 weeks: 
         *
         *  DTSTART;TZID=US-Eastern:19970902T090000 
         *  RRULE:FREQ=WEEKLY;UNTIL=19971007T000000Z;WKST=SU;BYDAY=TU,TH 
         *  or 
         *
         *  RRULE:FREQ=WEEKLY;COUNT=10;WKST=SU;BYDAY=TU,TH 
         *
         *  ==> (1997 9:00 AM EDT)September 2,4,9,11,16,18,23,25,30;October 2 
         * </pre>
         */
        recur = new Recur(Recur.WEEKLY, 10);
        recur.getDayList().add(WeekDay.TU);
        recur.getDayList().add(WeekDay.TH);
        log.debug(recur);
        
        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1997);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 2);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        
        seed = new DateTime(cal.getTime());

        cal = Calendar.getInstance();
        start = new DateTime(cal.getTime());
        cal.add(Calendar.YEAR, 2);
        end = new DateTime(cal.getTime());
        
        suite.addTest(new RecurTest(recur, seed, start, end, Value.DATE, 0));

        // testRecurGetDates..
        recur = new Recur("FREQ=WEEKLY;INTERVAL=1;BYDAY=SA");
        start = new Date("20050101");
        end = new Date("20060101");
    
        suite.addTest(new RecurTest(recur, start, end, null, Calendar.DAY_OF_WEEK, Calendar.SATURDAY));

        // Test ordering of returned dates..
        String s1 = "FREQ=WEEKLY;COUNT=75;INTERVAL=2;BYDAY=SU,MO,TU;WKST=SU"; 
        Recur rec = new Recur(s1); 
        Date d1 = new Date(); 
        cal = Calendar.getInstance(); 
        cal.add(Calendar.YEAR,1); 
        Date d2 = new Date(cal.getTimeInMillis()); 
         
        suite.addTest(new RecurTest("testGetDatesOrdering", rec, null, d1, d2, Value.DATE_TIME));

        // testMonthByDay..
        rrule = "FREQ=MONTHLY;UNTIL=20061220;INTERVAL=1;BYDAY=3WE";
        recur = new Recur(rrule);

        cal = Calendar.getInstance(TimeZones.getDateTimeZone());
        cal.set(2006, 11, 1);
        start = new Date(cal.getTime());
        cal.add(Calendar.YEAR, 1);
        
        suite.addTest(new RecurTest("testGetDatesNotEmpty", recur, null, start, new Date(cal.getTime()), Value.DATE));

        // testAlternateTimeZone..
        rrule = "FREQ=WEEKLY;BYDAY=WE;BYHOUR=12;BYMINUTE=0";
        recur = new Recur(rrule);

//        TimeZone originalDefault = TimeZone.getDefault();
//        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        
        TimeZoneRegistry tzreg = TimeZoneRegistryFactory.getInstance().createRegistry();
        cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.clear(Calendar.SECOND);
        start = new DateTime(cal.getTime());
        DtStart dtStart = new DtStart(start);
        dtStart.setTimeZone(tzreg.getTimeZone("America/Los_Angeles"));
        cal.add(Calendar.MONTH, 2);
        end = new DateTime(cal.getTime());
        DtEnd dtEnd = new DtEnd(end);
        
        suite.addTest(new RecurTest(recur, dtStart.getDate(), dtEnd.getDate(), Value.DATE_TIME, tzreg.getTimeZone("America/Los_Angeles")));

        // testFriday13Recur..
        rrule = "FREQ=MONTHLY;BYDAY=FR;BYMONTHDAY=13";
        recur = new Recur(rrule);
        
        cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.set(1997, 0, 1);
        start = new Date(cal.getTime());
        cal.set(2000, 0, 1);
        end = new Date(cal.getTime());

        suite.addTest(new RecurTest(recur, start, end, Value.DATE, Calendar.DAY_OF_MONTH, 13));
        suite.addTest(new RecurTest(recur, start, end, Value.DATE, Calendar.DAY_OF_WEEK, Calendar.FRIDAY));

        // testNoFrequency..
        suite.addTest(new RecurTest("BYDAY=MO,TU,WE,TH,FR"));
        
        // testUnknownFrequency..
        suite.addTest(new RecurTest("FREQ=FORTNIGHTLY;BYDAY=MO,TU,WE,TH,FR"));

        // various invalid values
        suite.addTest(new RecurTest("FREQ=YEARLY;BYMONTH=0"));
        suite.addTest(new RecurTest("FREQ=YEARLY;BYMONTHDAY=-400"));
        suite.addTest(new RecurTest(""));
        suite.addTest(new RecurTest(Recur.WEEKLY));
        suite.addTest(new RecurTest("FREQ"));
        suite.addTest(new RecurTest("FREQ=WEEKLY;BYDAY=xx"));

        // Unit test for recurrence every 4th february..
        rrule = "FREQ=YEARLY;BYMONTH=2;BYMONTHDAY=4;BYDAY=MO,TU,WE,TH,FR,SA,SU";
        recur = new Recur(rrule);
        
        cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.set(2006, 3, 10);
        start = new Date(cal.getTime());
        cal.set(2008, 1, 6);
        end = new Date(cal.getTime());
        
        suite.addTest(new RecurTest(recur, start, end, Value.DATE, Calendar.DAY_OF_MONTH, 4));
        suite.addTest(new RecurTest(recur, start, end, Value.DATE, Calendar.MONTH, 1));
        
        // Unit test for recurrence generation where seed month-date specified is
        // not valid for recurrence instances (e.g. feb 31).
        rrule = "FREQ=YEARLY;BYMONTH=2;BYMONTHDAY=4;BYDAY=MO,TU,WE,TH,FR,SA,SU";
        recur = new Recur(rrule);
        
        cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.set(2006, 11, 31);
        start = new Date(cal.getTime());
        cal.set(2008, 11, 31);
        end = new Date(cal.getTime());
        
        suite.addTest(new RecurTest(recur, start, end, Value.DATE, 2));
        suite.addTest(new RecurTest(recur, start, end, Value.DATE, Calendar.DAY_OF_MONTH, 4));
        suite.addTest(new RecurTest(recur, start, end, Value.DATE, Calendar.MONTH, 1));

        // Unit test for recurrence representing each half-hour..
        rrule = "FREQ=DAILY;BYHOUR=9,10,11,12,13,14,15,16;BYMINUTE=0,30";
        recur = new Recur(rrule);

        cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.HOUR);
        start = new DateTime(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        end = new DateTime(cal.getTime());

        suite.addTest(new RecurTest(recur, start, end, Value.DATE_TIME, 16));

        // Test creation of recur instances..
        String recurString = "FREQ=MONTHLY;INTERVAL=2;BYDAY=3MO";
        WeekDayList expectedDayList = new WeekDayList();
        expectedDayList.add(new WeekDay(WeekDay.MO, 3));
        
        suite.addTest(new RecurTest(recurString, Recur.MONTHLY, 2, expectedDayList));

        // testCountMonthsWith31Days..
        recur = new Recur("FREQ=MONTHLY;BYMONTHDAY=31");
        cal = Calendar.getInstance();
        start = new DateTime(cal.getTime());
        cal.add(Calendar.YEAR, 1);
        end = new DateTime(cal.getTime());
        
        suite.addTest(new RecurTest(recur, start, end, Value.DATE, 7));
        
        // Ensure the first result from getDates is the same as getNextDate..
        recur = new Recur("FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,WE,TH");
        seed = new DateTime("20081103T070000");
        Date periodStart = new DateTime("20081109T210000Z");
        Date periodEnd = new DateTime("20100104T210000Z");

        Locale currentLocale = Locale.getDefault();
        Date getDatesFirstResult;
        try {
            Locale.setDefault(testLocale);
            getDatesFirstResult = (Date) recur.getDates(seed, periodStart, periodEnd, Value.DATE_TIME).get(0);
        } finally {
            Locale.setDefault(currentLocale);
        }
        suite.addTest(new RecurTest(recur, seed, periodStart, getDatesFirstResult));

        recur = new Recur("FREQ=WEEKLY;BYDAY=MO");
        suite.addTest(new RecurTest(recur, new Date("20081212"), new Date("20081211"), new Date("20081215")));

        recur = new Recur("FREQ=YEARLY;BYMONTH=4;BYDAY=1SU");
        suite.addTest(new RecurTest(recur, seed, periodStart, new DateTime("20090405T070000")));

        // rrule never matching any candidate  - should reach limit
        recur = new Recur("FREQ=DAILY;COUNT=60;BYDAY=TU,TH;BYSETPOS=2");
        suite.addTest(new RecurTest(recur, seed, start, end, Value.DATE, 0));

        // rrule with negative bymonthday
        recur = new Recur("FREQ=YEARLY;COUNT=4;INTERVAL=2;BYMONTH=1,2,3;BYMONTHDAY=-1");
        suite.addTest(new RecurTest(recur, seed, periodStart, new DateTime("20100131T070000")));
        
        return suite;
    }
}
