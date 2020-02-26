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
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.util.TimeZones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.*;

import static net.fortuna.ical4j.model.WeekDay.*;

/**
 * Created on 14/02/2005
 *
 * $Id$
 *
 * @author Ben Fortuna
 */
public class RecurTest<T extends Temporal> extends TestCase {

    private static Logger log = LoggerFactory.getLogger(RecurTest.class);

    private static final Locale testLocale = Locale.US;

    private TimeZone originalDefault;
    
    private Recur<T> recur;
    
    private T periodStart;
    
    private T periodEnd;
    
    private Value value;
    
    private int expectedCount;
    
    private T seed;
    
    private T expectedDate;
    
    private int calendarField;
    
    private int expectedCalendarValue;
    
    private ZoneId expectedTimeZone;
    
    private String recurrenceString;
    
    private Frequency expectedFrequency;
    
    private int expectedInterval;
    
    private WeekDayList expectedDayList;
    
    /**
     * @param testMethod
     * @param recur
     * @param periodStart
     * @param periodEnd
     * @param value
     */
    public RecurTest(String testMethod, Recur<T> recur, T seed, T periodStart, T periodEnd, Value value) {
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
    public RecurTest(Recur<T> recur, T periodStart, T periodEnd, Value value, int expectedCount) {
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
    public RecurTest(Recur<T> recur, T seed, T periodStart, T periodEnd, Value value, int expectedCount) {
        this("testGetDatesCount", recur, seed, periodStart, periodEnd, value);
        this.expectedCount = expectedCount;
    }
    
    /**
     * @param recur
     * @param seed
     * @param periodStart
     * @param expectedDate
     */
    public RecurTest(Recur<T> recur, T seed, T periodStart, T expectedDate) {
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
    public RecurTest(Recur<T> recur, T periodStart, T periodEnd, Value value,
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
    public RecurTest(Recur<T> recur, T periodStart, T periodEnd, Value value, ZoneId expectedTimeZone) {
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
    public RecurTest(String recurrenceString, Frequency expectedFrequency, int expectedInterval, WeekDayList expectedDayList) {
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
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Australia/Melbourne"));
        List<T> dates = null;
        if (seed != null) {
            dates = recur.getDates(seed, periodStart, periodEnd);
        }
        else {
            dates = recur.getDates(periodStart, periodEnd);
        }
        assertEquals(expectedCount, dates.size());
        // assertTrue("Date list exceeds expected count", dates.size() <= expectedCount);
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
        List<T> dates = recur.getDates(periodStart, periodEnd);
        Calendar cal;
        if ((value != null) && (value == Value.DATE)) {
            cal = Calendar.getInstance(TimeZones.getDateTimeZone());
        } else {
            cal = Calendar.getInstance();
        }

        dates.forEach(date -> {
            cal.setTime(Date.from(Instant.from(date)));
            assertEquals(expectedCalendarValue, cal.get(calendarField));
        });
    }

    /**
     * 
     */
    public void testGetDatesOrdering() {
        List<T> dl1 = recur.getDates(periodStart, periodEnd);
        T prev = null;
        T event = null;
        for(int i=0; i<dl1.size(); i++) {
            prev = event;
            event = dl1.get(i);
            log.debug("Occurence "+i+" at "+event);
            assertTrue(prev == null || !Instant.from(prev).isAfter(Instant.from(event)));
        }
    }
    
    /**
     * 
     */
    public void testGetDatesNotEmpty() {
        assertFalse(recur.getDates(periodStart, periodEnd).isEmpty());
    }
    
    /**
     * 
     */
    public void testGetDatesTimeZone() {
        List<T> dates = recur.getDates(periodStart, periodEnd);
        dates.forEach(date -> {
            assertEquals(expectedTimeZone, ((DateTime) date).getTimeZone());
        });
    }
    
    /**
     * @throws ParseException
     */
    public void testInvalidRecurrenceString() throws ParseException {
        try {
            new Recur<T>(recurrenceString);
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
        Recur<T> recur = new Recur<>(recurrenceString);
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
        Recur<ZonedDateTime> recur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.YEARLY).count(-1).interval(2)
                .monthList(new NumberList("1")).dayList(new WeekDayList(SU))
                .hourList(new NumberList("8,9")).minuteList(new NumberList("30")).build();

        ZonedDateTime seed = ZonedDateTime.of(1997, 2, 5,
                8, 30, 0, 0, ZoneId.systemDefault());
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = start.plusYears(2);
        log.debug(recur.toString());
        
        List<ZonedDateTime> dates = recur.getDates(seed, start, end);
        log.debug(dates.toString());
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
        Recur<ZonedDateTime> recur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.MONTHLY).count(-1)
                .dayList(new WeekDayList(MO, TU, WE, TH, FR))
                .setPosList(new NumberList("-1")).build();
        log.debug(recur.toString());

        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = start.plusYears(2);
        
        List<ZonedDateTime> dates = recur.getDates(start, end);
        log.debug(dates.toString());
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
        Recur<T> recur = new Recur.Builder<T>().frequency(Frequency.MONTHLY).count(4).interval(1)
                .monthDayList(new NumberList("2")).build();
        assertEquals("FREQ=MONTHLY;INTERVAL=1;COUNT=4;BYMONTHDAY=2", recur.toString());

        // FREQ=MONTHLY;INTERVAL=2;COUNT=4;BYDAY=2MO
        recur = new Recur.Builder<T>().frequency(Frequency.MONTHLY).count(4).interval(2)
                .dayList(new WeekDayList(new WeekDay(MO, 2))).build();
        assertEquals("FREQ=MONTHLY;INTERVAL=2;COUNT=4;BYDAY=2MO", recur.toString());
        
        // FREQ=YEARLY;COUNT=4;BYMONTH=2;BYMONTHDAY=3
        recur = new Recur.Builder<T>().frequency(Frequency.YEARLY).count(4)
                .monthList(new NumberList("2")).monthDayList(new NumberList("3")).build();
        assertEquals("FREQ=YEARLY;COUNT=4;BYMONTH=2;BYMONTHDAY=3", recur.toString());
        
        // FREQ=YEARLY;COUNT=4;BYMONTH=2;BYDAY=2SU
        recur = new Recur.Builder<T>().frequency(Frequency.YEARLY).count(4)
                .monthList(new NumberList("2")).dayList(new WeekDayList(new WeekDay(SU, 2))).build();
        assertEquals("FREQ=YEARLY;COUNT=4;BYMONTH=2;BYDAY=2SU", recur.toString());
    }

    public void testGetDatesRalph() throws ParseException {
        Recur<Instant> recur = new Recur<>("FREQ=WEEKLY;WKST=MO;INTERVAL=1;UNTIL=20051003T000000Z;BYDAY=MO,WE");

        Calendar queryStartDate = new
        GregorianCalendar(TimeZone.getTimeZone(TimeZones.UTC_ID));
        queryStartDate.set(2005, Calendar.SEPTEMBER, 3, 0,
        0, 0);

        Calendar queryEndDate = new
        GregorianCalendar(TimeZone.getTimeZone(TimeZones.UTC_ID));
        queryEndDate.set(2005, Calendar.OCTOBER, 31, 23,
        59, 0);

        List<Instant> dateList = recur.getDates(queryStartDate.getTime().toInstant(),
                queryStartDate.getTime().toInstant(),
                queryEndDate.getTime().toInstant());

        log.debug(dateList.toString());
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
        Recur<ZonedDateTime> recur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.DAILY).count(10).interval(2).build();
        log.debug(recur.toString());

        ZonedDateTime start = ZonedDateTime.now().withYear(2018).withMonth(12).withDayOfMonth(16);
        ZonedDateTime end = start.plusDays(10);
        log.debug(recur.getDates(start, end).toString());

        recur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.DAILY).until(end).interval(2).build();
        log.info(recur.toString());
        log.debug(recur.getDates(start, end).toString());

        recur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.WEEKLY).until(end)
                .interval(2).dayList(new WeekDayList(MO)).build();
        log.debug(recur.toString());

        List<ZonedDateTime> dates = recur.getDates(start, end);
        log.debug(dates.toString());
        
        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, 5));
        
        // testGetNextDate..
        recur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.DAILY).count(3).build();
        TemporalAdapter<ZonedDateTime> seed = TemporalAdapter.parse("20080401");
        TemporalAdapter<ZonedDateTime> firstDate = TemporalAdapter.parse("20080402");
        TemporalAdapter<ZonedDateTime> secondDate = TemporalAdapter.parse("20080403");
        
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), seed.getTemporal(), firstDate.getTemporal()));
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), firstDate.getTemporal(), secondDate.getTemporal()));
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), secondDate.getTemporal(), null));
        
        // test DateTime
        TemporalAdapter<ZonedDateTime> until = TemporalAdapter.parse("20080421T063000Z");
        recur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.WEEKLY).until(until.getTemporal()).build();
        seed = TemporalAdapter.parse("20080407T063000");
        firstDate = TemporalAdapter.parse("20080414T063000");
        secondDate = TemporalAdapter.parse("20080421T063000");
        
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), seed.getTemporal(), firstDate.getTemporal()));
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), firstDate.getTemporal(), secondDate.getTemporal()));
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), secondDate.getTemporal(), null));
        
        // Test BYDAY rules..
        recur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.DAILY).count(10)
                .interval(1).dayList(new WeekDayList(MO, TU, WE, TH, FR)).build();
        log.debug(recur.toString());
        
        start = ZonedDateTime.now();
        end = start.plusDays(10);
        
        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, 10));

        // Test BYDAY recurrence rules..
        String rrule = "FREQ=MONTHLY;WKST=SU;INTERVAL=2;BYDAY=5TU";
        recur = new Recur<>(rrule);

        start = ZonedDateTime.now().withSecond(0);
        end = start.plusYears(2);

        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, Calendar.WEEK_OF_MONTH, 5));

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
        recur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.WEEKLY).count(10)
            .dayList(new WeekDayList(TU, TH)).build();
        log.debug(recur.toString());

        ZonedDateTime newseed = ZonedDateTime.now().withYear(1997).withMonth(9).withHour(9).withMinute(0).withSecond(0);
        start = newseed;
        end = newseed.plusYears(2);

        suite.addTest(new RecurTest<>(recur, newseed, start, end, Value.DATE, 0));

        // testRecurGetDates..
        recur = new Recur<>("FREQ=WEEKLY;INTERVAL=1;BYDAY=SA");

        TemporalAdapter<ZonedDateTime> periodStart = TemporalAdapter.parse("20050101");
        TemporalAdapter<ZonedDateTime> periodEnd = TemporalAdapter.parse("20060101");
        suite.addTest(new RecurTest<>(recur, periodStart.getTemporal(),
                periodEnd.getTemporal(), null, Calendar.DAY_OF_WEEK, Calendar.SATURDAY));

        // Test ordering of returned dates..
        String s1 = "FREQ=WEEKLY;COUNT=75;INTERVAL=2;BYDAY=SU,MO,TU;WKST=SU"; 
        Recur<ZonedDateTime> rec = new Recur<>(s1);
        ZonedDateTime d1 = ZonedDateTime.now();
        ZonedDateTime d2 = d1.plusYears(1);
         
        suite.addTest(new RecurTest<>("testGetDatesOrdering", rec, null, d1, d2, Value.DATE_TIME));

        // testMonthByDay..
        rrule = "FREQ=MONTHLY;UNTIL=20061220;INTERVAL=1;BYDAY=3WE";
        Recur<LocalDate> localDateRecur = new Recur<>(rrule);

        LocalDate startDate = LocalDate.of(2006, 12, 1);
        LocalDate endDate = startDate.plusYears(1);

        suite.addTest(new RecurTest<>("testGetDatesNotEmpty", localDateRecur, null, startDate,
                endDate, Value.DATE));

        // testAlternateTimeZone..
        rrule = "FREQ=WEEKLY;BYDAY=WE;BYHOUR=12;BYMINUTE=0";
        recur = new Recur<>(rrule);

//        TimeZone originalDefault = TimeZone.getDefault();
//        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));

        start = ZonedDateTime.now(TimeZone.getTimeZone("America/Los_Angeles").toZoneId()).withSecond(0);
        ParameterList tzParams = new ParameterList();
        tzParams.add(new TzId(start.getZone().getId()));
        DtStart<ZonedDateTime> dtStart = new DtStart<>(tzParams, start);
        end = start.plusMonths(2);
        DtEnd<ZonedDateTime> dtEnd = new DtEnd<>(end);
        
        suite.addTest(new RecurTest<>(recur, dtStart.getDate(), dtEnd.getDate(), Value.DATE_TIME,
                TimeZoneRegistry.getGlobalZoneId("America/Los_Angeles")));

        // testFriday13Recur..
        rrule = "FREQ=MONTHLY;BYDAY=FR;BYMONTHDAY=13";
        recur = new Recur<>(rrule);
        
        start = ZonedDateTime.now().withYear(1997).withMonth(1).withDayOfMonth(1);
        end = start.withYear(2000);

        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, Calendar.DAY_OF_MONTH, 13));
        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, Calendar.DAY_OF_WEEK, Calendar.FRIDAY));

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

        start = ZonedDateTime.now().withYear(2006).withMonth(4).withDayOfMonth(10);
        end = start.withYear(2008).withMonth(2).withDayOfMonth(6);

        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, Calendar.DAY_OF_MONTH, 4));
        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, Calendar.MONTH, 1));
        
        // Unit test for recurrence generation where seed month-date specified is
        // not valid for recurrence instances (e.g. feb 31).
        rrule = "FREQ=YEARLY;BYMONTH=2;BYMONTHDAY=4;BYDAY=MO,TU,WE,TH,FR,SA,SU";
        recur = new Recur(rrule);

        start = ZonedDateTime.now().withYear(2006).withMonth(12).withDayOfMonth(31);
        end = start.withYear(2008).withMonth(12).withDayOfMonth(31);

        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, 2));
        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, Calendar.DAY_OF_MONTH, 4));
        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, Calendar.MONTH, 1));

        // Unit test for recurrence representing each half-hour..
        rrule = "FREQ=DAILY;BYHOUR=9,10,11,12,13,14,15,16;BYMINUTE=0,30";
        recur = new Recur(rrule);

        start = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0);
        end = start.plusDays(1);

        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE_TIME, 16));

        // Test creation of recur instances..
        String recurString = "FREQ=MONTHLY;INTERVAL=2;BYDAY=3MO";
        WeekDayList expectedDayList = new WeekDayList();
        expectedDayList.add(new WeekDay(MO, 3));
        
        suite.addTest(new RecurTest(recurString, Frequency.MONTHLY, 2, expectedDayList));

        // testCountMonthsWith31Days..
        recur = new Recur("FREQ=MONTHLY;BYMONTHDAY=31");
        start = ZonedDateTime.now();
        end = start.plusYears(1);

        suite.addTest(new RecurTest<>(recur, start, end, Value.DATE, 7));
        
        // Ensure the first result from getDates is the same as getNextDate..
        recur = new Recur("FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,WE,TH");
        seed = TemporalAdapter.parse("20081103T070000");
        periodStart = TemporalAdapter.parse("20081109T210000");
        periodEnd = TemporalAdapter.parse("20100104T210000");

        Locale currentLocale = Locale.getDefault();
        List<ZonedDateTime> getDatesResult;
        try {
            Locale.setDefault(testLocale);
            getDatesResult = recur.getDates(seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal());
        } finally {
            Locale.setDefault(currentLocale);
        }
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), getDatesResult.get(0)));

        recur = new Recur("FREQ=WEEKLY;BYDAY=MO");
        seed = TemporalAdapter.parse("20081212");
        periodStart = TemporalAdapter.parse("20081211");
        periodEnd = TemporalAdapter.parse("20081215");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal()));

        recur = new Recur("FREQ=YEARLY;BYMONTH=4;BYDAY=1SU");
        periodEnd = TemporalAdapter.parse("20090405T070000");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal()));

        // rrule never matching any candidate  - should reach limit
        recur = new Recur("FREQ=DAILY;COUNT=60;BYDAY=TU,TH;BYSETPOS=2");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), start, end, Value.DATE, 0));

        // rrule with negative bymonthday
        recur = new Recur("FREQ=YEARLY;COUNT=4;INTERVAL=2;BYMONTH=1,2,3;BYMONTHDAY=-1");
        periodEnd = TemporalAdapter.parse("20100131T070000");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal()));
        
        // rather uncommon rule
        recur = new Recur("FREQ=YEARLY;BYWEEKNO=1,2,3,4");

        seed = TemporalAdapter.parse("20130101T120000Z");
        periodStart = TemporalAdapter.parse("20130101T120000Z");
        periodEnd = TemporalAdapter.parse("20130123T120000Z");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal(), Value.DATE_TIME, 4));

        periodStart = TemporalAdapter.parse("20160101T120000Z");
        periodEnd = TemporalAdapter.parse("20160123T120000Z");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal(), Value.DATE_TIME, 3));
        
        recur = new Recur("FREQ=DAILY;COUNT=3;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR");
        seed = TemporalAdapter.parse("20131215T000000Z");
        periodStart = TemporalAdapter.parse("20131215T000000Z");
        periodEnd = TemporalAdapter.parse("20180101T120000Z");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal(), Value.DATE_TIME, 3));
        
        // rrule with bymonth and count. Should return correct number of occurrences near the end of its perioud.
        recur = new Recur("FREQ=MONTHLY;COUNT=3;INTERVAL=1;BYMONTH=1,9,10,12;BYMONTHDAY=12");

        seed = TemporalAdapter.parse("20150917T000000Z");
        periodStart = TemporalAdapter.parse("20160101T000000Z");
        periodEnd = TemporalAdapter.parse("20160201T000000Z");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal(), Value.DATE, 1));

        periodStart = TemporalAdapter.parse("20160201T000000Z");
        periodEnd = TemporalAdapter.parse("20160301T000000Z");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal(), Value.DATE, 0));

        // rrule with bymonth, byday and bysetpos. Issue #39
        recur = new Recur("FREQ=MONTHLY;WKST=MO;INTERVAL=1;BYMONTH=2,3,9,10;BYMONTHDAY=28,29,30,31;BYSETPOS=-1");
        seed = TemporalAdapter.parse("20150701T000000");
        periodStart = TemporalAdapter.parse("20150701T000000");
        periodEnd = TemporalAdapter.parse("20150930T000000");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal()));

        // test getting valid recurrence at tip of smart increment
        // feb 29 2020 monthly with only valid month by february should return feb 28 2021
        recur = new Recur("FREQ=MONTHLY;BYMONTH=2;INTERVAL=1");
        seed = TemporalAdapter.parse("20200229T000000");
        periodStart = TemporalAdapter.parse("20200229T000000");
        periodEnd = TemporalAdapter.parse("20240229T000000");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal()));

        // test hitting limit when getting invalid next recurrence
        recur = new Recur("FREQ=MONTHLY;BYMONTH=2;BYMONTHDAY=30;INTERVAL=1");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), null));

        // test hitting leap year appropriately
        recur = new Recur("FREQ=YEARLY;BYMONTHDAY=29;INTERVAL=1");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal()));

        // test correct hit on first incrementation
        recur = new Recur("FREQ=YEARLY;INTERVAL=4");
        suite.addTest(new RecurTest<>(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal()));

        // last working day starting from may 31 2020 should return jun 30 2020
        seed = TemporalAdapter.parse("20200531T000000");
        periodStart = TemporalAdapter.parse("20200531T000000");
        periodEnd = TemporalAdapter.parse("20200630T000000");
        recur = new Recur("FREQ=MONTHLY;BYDAY=MO,TU,WE,TH,FR;BYSETPOS=-1");
        suite.addTest(new RecurTest(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal()));

        // 5th sunday monthly starting from aug 31 2020 should return nov 29 2020
        seed = TemporalAdapter.parse("20200831T000000");
        periodStart = TemporalAdapter.parse("20200831T000000");
        periodEnd = TemporalAdapter.parse("20201129T000000");
        recur = new Recur("FREQ=MONTHLY;BYDAY=SU;BYSETPOS=5");
        suite.addTest(new RecurTest(recur, seed.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal()));

        return suite;
    }
}
