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

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.parameter.Value;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ben Fortuna
 */
public class RecurTest extends TestCase {
    
    private static Log log = LogFactory.getLog(RecurTest.class);

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
        log.info(recur.getDates(start, end, Value.DATE));
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
        log.info(recur.getDates(start, end, Value.DATE_TIME));        
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
        Date start = new Date(cal.getTime().getTime());
        cal.add(Calendar.YEAR, 2);
        Date end = new Date(cal.getTime().getTime());
        log.info(recur);
        log.info(recur.getDates(new Date(testCal.getTime().getTime()), start, end, Value.DATE_TIME));
    }


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
}
