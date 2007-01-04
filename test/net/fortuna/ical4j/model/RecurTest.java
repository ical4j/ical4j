/*
 * Created on 14/02/2005
 *
 * $Id$
 *
 * Copyright (c) 2005, Ben Fortuna
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
import java.util.TimeZone;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.util.TimeZones;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ben Fortuna
 */
public class RecurTest extends TestCase {
    
    private static Log log = LogFactory.getLog(RecurTest.class);

    private TimeZone originalDefault;
    
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
    public void testGetDates() {
        Recur recur = new Recur(Recur.DAILY, 10);
        recur.setInterval(2);
        log.info(recur);
        
        Calendar cal = Calendar.getInstance();
        Date start = new Date(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, 10);
        Date end = new Date(cal.getTime().getTime());
        log.info(recur.getDates(start, end, Value.DATE_TIME));        
        
        recur.setUntil(new Date(cal.getTime().getTime()));
        log.info(recur);
        log.info(recur.getDates(start, end, Value.DATE_TIME));
        
        recur.setFrequency(Recur.WEEKLY);
        recur.getDayList().add(WeekDay.MO);
        log.info(recur);

        DateList dates = recur.getDates(start, end, Value.DATE);
        log.info(dates);
        
        assertTrue("Date list exceeds COUNT limit", dates.size() <= 10);
    }
    
    /**
     * Test BYDAY rules.
     */
    public void testGetDatesByDay() {
        Recur recur = new Recur(Recur.DAILY, 10);
        recur.setInterval(1);
        recur.getDayList().add(WeekDay.MO);
        recur.getDayList().add(WeekDay.TU);
        recur.getDayList().add(WeekDay.WE);
        recur.getDayList().add(WeekDay.TH);
        recur.getDayList().add(WeekDay.FR);
        log.info(recur);
        
        Calendar cal = Calendar.getInstance();
        Date start = new Date(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, 10);
        Date end = new Date(cal.getTime().getTime());
        
        DateList dates = recur.getDates(start, end, Value.DATE_TIME);
        log.info(dates);
        
        assertTrue("Date list exceeds COUNT limit", dates.size() <= 10);
    }
    
    /**
     * Test BYDAY recurrence rules..
     */
    public void testGetDatesByDay2() throws ParseException {
        String rrule = "FREQ=MONTHLY;WKST=SU;INTERVAL=2;BYDAY=5TU";
        Recur recur = new Recur(rrule);

        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        java.util.Date start = cal.getTime();
        cal.add(Calendar.YEAR, 2);
        java.util.Date end = cal.getTime();
        
        DateList recurrences = recur.getDates(new Date(start), new Date(end), Value.DATE);
        for (Iterator i = recurrences.iterator(); i.hasNext();) {
            Date recurrence = (Date) i.next();
            cal.setTime(recurrence);
            assertEquals(5, cal.get(Calendar.WEEK_OF_MONTH));
        }
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
        log.info(recur);
        
        DateList dates = recur.getDates(new DateTime(testCal.getTime()), start, end, Value.DATE_TIME);
        log.info(dates);
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
    public final void testBoundaryProcessing() {
        Recur recur = new Recur(Recur.WEEKLY, 10);
        recur.getDayList().add(WeekDay.TU);
        recur.getDayList().add(WeekDay.TH);
        log.info(recur);
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1997);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 2);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        
        Date seed = new DateTime(cal.getTime());

        cal = Calendar.getInstance();
        Date start = new DateTime(cal.getTime());
        cal.add(Calendar.YEAR, 2);
        Date end = new DateTime(cal.getTime());
        
        DateList dates = recur.getDates(seed, start, end, Value.DATE_TIME);
        log.info(dates);
        
        assertTrue(dates.isEmpty());
    }
    
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
        log.info(recur);

        Calendar cal = Calendar.getInstance();
        Date start = new DateTime(cal.getTime());
        cal.add(Calendar.YEAR, 2);
        Date end = new DateTime(cal.getTime());
        
        DateList dates = recur.getDates(start, end, Value.DATE_TIME);
        log.info(dates);
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
            System.out.println("date_" + i + " = " + dates.get(i).toString()); 
        } 
    }
    
    /**
     * @throws ParseException
     */
    public final void testRecurGetDates() throws ParseException {
        Recur recur = new Recur("FREQ=WEEKLY;INTERVAL=1;BYDAY=SA");
    
        Date start = new Date("20050101Z");
        Date end = new Date("20060101Z");
    
        DateList list = recur.getDates(start, end, null);
        for (int i = 0; i < list.size(); i++) {
            Date date = (Date) list.get(i);
//            Calendar calendar = Dates.getCalendarInstance(date);
            Calendar calendar = Calendar.getInstance(); //TimeZone.getTimeZone("Etc/UTC"));
            calendar.setTime(date);
            assertEquals(Calendar.SATURDAY, calendar.get(Calendar.DAY_OF_WEEK));
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
        
        log.info(dateList);
    }
    
    /**
     * Test ordering of returned dates.
     * @throws ParseException
     */
    public void testDateOrdering() throws ParseException {
        String s1 = "FREQ=WEEKLY;COUNT=75;INTERVAL=2;BYDAY=SU,MO,TU;WKST=SU"; 
        
        Recur rec = new Recur(s1); 
         
        Date d1 = new Date(); 
        Calendar cal = Calendar.getInstance(); 
        cal.add(Calendar.YEAR,1); 
        Date d2 = new Date(cal.getTimeInMillis()); 
         
        DateList dl1 = rec.getDates(d1,d2, Value.DATE_TIME); 
        
        Date prev = null;
        Date event = null;
        for(int i=0; i<dl1.size(); i++) {
            prev = event;
            event = (Date) dl1.get(i); 
            log.info("Occurence "+i+" at "+event);
            assertTrue(prev == null || !prev.after(event));
        }
    }
    
    /**
     * @throws ParseException
     */
    public void testMonthByDay() throws ParseException {
        String rrule = "FREQ=MONTHLY;UNTIL=20061220T000000;INTERVAL=1;BYDAY=3WE";
        Recur recur = new Recur(rrule);

        Calendar cal = Calendar.getInstance();
        cal.set(2006, 11, 1);
        Date start = new Date(cal.getTime());
        cal.add(Calendar.YEAR, 1);
        
        DateList recurrences = recur.getDates(start, new Date(cal.getTime()), Value.DATE);
        assertTrue(!recurrences.isEmpty());
    }
    
    /**
     * @throws ParseException
     */
    public void testAlternateTimeZone() throws ParseException {
        String rrule = "FREQ=WEEKLY;BYDAY=WE;BYHOUR=12;BYMINUTE=0";
        Recur recur = new Recur(rrule);

        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        TimeZoneRegistry tzreg = TimeZoneRegistryFactory.getInstance().createRegistry();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.clear(Calendar.SECOND);
        java.util.Date start = cal.getTime();
        DtStart dtStart = new DtStart(new DateTime(start));
        dtStart.setTimeZone(tzreg.getTimeZone("America/Los_Angeles"));
        cal.add(Calendar.MONTH, 2);
        java.util.Date end = cal.getTime();
        DtEnd dtEnd = new DtEnd(new DateTime(end));
        
        DateList recurrences = recur.getDates(dtStart.getDate(), dtEnd.getDate(), Value.DATE_TIME);
        for (Iterator i = recurrences.iterator(); i.hasNext();) {
            DateTime recurrence = (DateTime) i.next();
            assertEquals(tzreg.getTimeZone("America/Los_Angeles"), recurrence.getTimeZone());
        }
    }
    
    /**
     * @throws ParseException
     */
    public void testFriday13Recur() throws ParseException {
        String rrule = "FREQ=MONTHLY;BYDAY=FR;BYMONTHDAY=13";
        Recur recur = new Recur(rrule);
        
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.set(1997, 0, 1);
        java.util.Date start = cal.getTime();
        cal.set(2000, 0, 1);
        java.util.Date end = cal.getTime();
        
        DateList recurrences = recur.getDates(new Date(start), new Date(end), Value.DATE);
        for (Iterator i = recurrences.iterator(); i.hasNext();) {
            Date recurrence = (Date) i.next();
            cal.setTime(recurrence);
            assertEquals(13, cal.get(Calendar.DAY_OF_MONTH));
            assertEquals(Calendar.FRIDAY, cal.get(Calendar.DAY_OF_WEEK));
        }
    }
    
    public void testNoFrequency() throws ParseException {
        String rrule = "BYDAY=MO,TU,WE,TH,FR";
        try {
            new Recur(rrule);
            fail("IllegalArgumentException not thrown!");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
    
    public void testUnknownFrequency() throws ParseException {
        String rrule = "FREQ=FORTNIGHTLY;BYDAY=MO,TU,WE,TH,FR";
        try {
            new Recur(rrule);
            fail("IllegalArgumentException not thrown!");
        } catch (IllegalArgumentException e) {
            // expected
        }       
    }
    
    /**
     * Unit test for recurrence every 4th february.
     */
    public void testRecur4Feb() throws ParseException {
        String rrule = "FREQ=YEARLY;BYMONTH=2;BYMONTHDAY=4;BYDAY=MO,TU,WE,TH,FR,SA,SU";
        Recur recur = new Recur(rrule);
        
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.set(2006, 3, 10);
        java.util.Date start = cal.getTime();
        cal.set(2008, 1, 6);
        java.util.Date end = cal.getTime();
        
        DateList recurrences = recur.getDates(new Date(start), new Date(end), Value.DATE);
        assertEquals(2, recurrences.size());
        for (Iterator i = recurrences.iterator(); i.hasNext();) {
            Date recurrence = (Date) i.next();
            cal.setTime(recurrence);
            assertEquals(4, cal.get(Calendar.DAY_OF_MONTH));
            assertEquals(1, cal.get(Calendar.MONTH));
        }
    }
    
    /**
     * Unit test for recurrence generation where seed month-date specified is
     * not valid for recurrence instances (e.g. feb 31).
     */
    public void testRecur4Feb2() throws ParseException {
        String rrule = "FREQ=YEARLY;BYMONTH=2;BYMONTHDAY=4;BYDAY=MO,TU,WE,TH,FR,SA,SU";
        Recur recur = new Recur(rrule);
        
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.set(2006, 11, 31);
        java.util.Date start = cal.getTime();
        cal.set(2008, 11, 31);
        java.util.Date end = cal.getTime();
        
        DateList recurrences = recur.getDates(new Date(start), new Date(end), Value.DATE);
        assertEquals(2, recurrences.size());
        for (Iterator i = recurrences.iterator(); i.hasNext();) {
            Date recurrence = (Date) i.next();
            cal.setTime(recurrence);
            assertEquals(4, cal.get(Calendar.DAY_OF_MONTH));
            assertEquals(1, cal.get(Calendar.MONTH));
        }
    }
    
    /**
     * Unit test for recurrence representing each half-hour.
     * @throws ParseException
     */
    public void testRecurHalfHour() throws ParseException {
        String rrule = "FREQ=DAILY;BYHOUR=9,10,11,12,13,14,15,16;BYMINUTE=0,30";
        Recur recur = new Recur(rrule);

        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.HOUR);
        java.util.Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        java.util.Date end = cal.getTime();

        DateList recurrences = recur.getDates(new DateTime(start),
                new DateTime(end), Value.DATE_TIME);
        assertEquals(16, recurrences.size());
    }
}
