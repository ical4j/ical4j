/*
 * $Id: VEventTest.java [28/09/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.ical4j.model.component;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A test case for VEvents.
 * 
 * @author benfortuna
 */
public class VEventTest extends TestCase {

    private static Log log = LogFactory.getLog(VEventTest.class);

    private VEvent weekdayNineToFiveEvents = null;


    public void setUp() throws Exception {
        super.setUp();

        Calendar weekday9AM = Calendar.getInstance();
        weekday9AM.set(2005, Calendar.MARCH, 7, 9, 0, 0);
        weekday9AM.set(Calendar.MILLISECOND, 0);

        Calendar weekday5PM = Calendar.getInstance();
        weekday5PM.set(2005, Calendar.MARCH, 7, 17, 0, 0);
        weekday5PM.set(Calendar.MILLISECOND, 0);

        // Do the recurrence until December 31st.
        Calendar untilCal = Calendar.getInstance();
        untilCal.set(2005, Calendar.DECEMBER, 31);
        untilCal.set(Calendar.MILLISECOND, 0);

        // 9:00AM to 5:00PM Rule
        Recur recur = new Recur(Recur.WEEKLY, untilCal.getTime());
        recur.getDayList().add(WeekDay.MO);
        recur.getDayList().add(WeekDay.TU);
        recur.getDayList().add(WeekDay.WE);
        recur.getDayList().add(WeekDay.TH);
        recur.getDayList().add(WeekDay.FR);
        recur.setInterval(3);
        recur.setWeekStartDay(WeekDay.MO.getDay());
        RRule rrule = new RRule(recur);

        Summary summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI");

        weekdayNineToFiveEvents = new VEvent();
        weekdayNineToFiveEvents.getProperties().add(rrule);
        weekdayNineToFiveEvents.getProperties().add(summary);
        weekdayNineToFiveEvents.getProperties().add(
                                        new DtStart(weekday9AM.getTime()));
        weekdayNineToFiveEvents.getProperties().add(
                                        new DtEnd(weekday5PM.getTime()));
    }

    /**
     *  
     */
    public final void test() {
        // create timezone property..
        VTimeZone tz = VTimeZone.getDefault();

        // create tzid parameter..
        TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID)
                .getValue());

        // create event start date..
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 25);

        DtStart start = new DtStart(calendar.getTime());
        start.getParameters().add(tzParam);
        start.getParameters().add(Value.DATE);

        Summary summary = new Summary("Christmas Day; \n this is a, test\\");

        VEvent christmas = new VEvent();
        christmas.getProperties().add(start);
        christmas.getProperties().add(summary);

        log.info(christmas);
    }

    public final void test2() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 25);

        VEvent christmas = new VEvent(cal.getTime(), "Christmas Day");

        // initialise as an all-day event..
        christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);

        // add timezone information..
        VTimeZone tz = VTimeZone.getDefault();
        TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());
        christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);

        log.info(christmas);
    }
    
    public final void test3() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        // tomorrow..
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 9);
        cal.set(java.util.Calendar.MINUTE, 30);

        VEvent meeting = new VEvent(cal.getTime(), 1000 * 60 * 60, "Progress Meeting");

        // add timezone information..
        VTimeZone tz = VTimeZone.getDefault();
        TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());
        meeting.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);       

        log.info(meeting);
    }
    

    /*
    public void testGetRecurringStartDates() {

        // Test Null Dates
        try {
            weekdayNineToFiveEvents.getRecurringStartDates(null, null);
            fail("Should've thrown an exception.");
        } catch (RuntimeException re) {
            // Expecting an exception here.
        }

        // Test Start 04/01/2005, End One month later.
        // Query Calendar Start and End Dates.
        Calendar queryStartDate = Calendar.getInstance();
        queryStartDate.set(2005, Calendar.APRIL, 1, 14, 47, 0);
        queryStartDate.set(Calendar.MILLISECOND, 0);
        Calendar queryEndDate = Calendar.getInstance();
        queryEndDate.set(2005, Calendar.MAY, 1, 11, 15, 0);
        queryEndDate.set(Calendar.MILLISECOND, 0);

        DateList startDateList =
                        weekdayNineToFiveEvents.getRecurringStartDates(
                              queryStartDate.getTime(), queryEndDate.getTime());

        assertNotNull(startDateList);
        assertTrue(startDateList.size() > 0);

        Calendar expectedCal = Calendar.getInstance();
        expectedCal.set(2005, Calendar.APRIL, 4, 9, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedFirstStart = expectedCal.getTime();
        Date firstStartDate = (Date) startDateList.get(0);
        assertEquals(expectedFirstStart.getTime(), firstStartDate.getTime());

    }
    */

    /**
     * Test Null Dates
     * Test Start today, End One month from now.
     *
     * @throws Exception
     */
   public final void testGetConsumedTime() throws Exception {

        // Test Null Dates
        try {
            weekdayNineToFiveEvents.getConsumedTime(null, null);
            fail("Should've thrown an exception.");
        } catch (RuntimeException re) {
            log.info("Expecting an exception here.");
        }

        // Test Start 04/01/2005, End One month later.
        // Query Calendar Start and End Dates.
        Calendar queryStartDate = Calendar.getInstance();
        queryStartDate.set(2005, Calendar.APRIL, 1, 14, 47, 0);
        queryStartDate.set(Calendar.MILLISECOND, 0);
        Calendar queryEndDate = Calendar.getInstance();
        queryEndDate.set(2005, Calendar.MAY, 1, 11, 15, 0);
        queryEndDate.set(Calendar.MILLISECOND, 0);

        // This range is monday to friday every three weeks, starting from
        // March 7th 2005, which means for our query dates we need
        // April 18th through to the 22nd.
        PeriodList periods =
                weekdayNineToFiveEvents.getConsumedTime(queryStartDate.getTime(),
                                                      queryEndDate.getTime());
        Calendar expectedCal = Calendar.getInstance();
        expectedCal.set(2005, Calendar.APRIL, 4, 9, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedStartOfFirstRange = expectedCal.getTime();
        expectedCal.set(2005, Calendar.APRIL, 4, 17, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedEndOfFirstRange = expectedCal.getTime();
        assertNotNull(periods);
        assertTrue(periods.size() > 0);
        Period firstPeriod = (Period) periods.toArray()[0];
        assertEquals(expectedStartOfFirstRange, firstPeriod.getStart());
        assertEquals(expectedEndOfFirstRange, firstPeriod.getEnd());

    }
}