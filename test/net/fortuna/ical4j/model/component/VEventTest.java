/*
 * $Id: VEventTest.java [28/09/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.ical4j.model.component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

import junit.framework.TestCase;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.ValidationException;
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
    
    private VEvent dailyWeekdayEvents = null;
    
    private VEvent monthlyWeekdayEvents = null;

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
        Date until = new Date(untilCal.getTime().getTime());

        // 9:00AM to 5:00PM Rule using weekly
        Recur recurWeekly = new Recur(Recur.WEEKLY, until);
        recurWeekly.getDayList().add(WeekDay.MO);
        recurWeekly.getDayList().add(WeekDay.TU);
        recurWeekly.getDayList().add(WeekDay.WE);
        recurWeekly.getDayList().add(WeekDay.TH);
        recurWeekly.getDayList().add(WeekDay.FR);
        recurWeekly.setInterval(1);
        recurWeekly.setWeekStartDay(WeekDay.MO.getDay());
        RRule rruleWeekly = new RRule(recurWeekly);

        // 9:00AM to 5:00PM Rule using daily frequency
        Recur recurDaily = new Recur(Recur.DAILY, until);
        recurDaily.getDayList().add(WeekDay.MO);
        recurDaily.getDayList().add(WeekDay.TU);
        recurDaily.getDayList().add(WeekDay.WE);
        recurDaily.getDayList().add(WeekDay.TH);
        recurDaily.getDayList().add(WeekDay.FR);
        recurDaily.setInterval(1);
        recurDaily.setWeekStartDay(WeekDay.MO.getDay());
        RRule rruleDaily = new RRule(recurDaily);

        // 9:00AM to 5:00PM Rule using monthly frequency
        Recur recurMonthly = new Recur(Recur.MONTHLY, until);
        recurMonthly.getDayList().add(WeekDay.MO);
        recurMonthly.getDayList().add(WeekDay.TU);
        recurMonthly.getDayList().add(WeekDay.WE);
        recurMonthly.getDayList().add(WeekDay.TH);
        recurMonthly.getDayList().add(WeekDay.FR);
        recurMonthly.setInterval(1);
        recurMonthly.setWeekStartDay(WeekDay.MO.getDay());
        RRule rruleMonthly = new RRule(recurMonthly);

        Summary summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED WEEKLY");

        weekdayNineToFiveEvents = new VEvent();
        weekdayNineToFiveEvents.getProperties().add(rruleWeekly);
        weekdayNineToFiveEvents.getProperties().add(summary);
        weekdayNineToFiveEvents.getProperties().add(
                                        new DtStart(new Date(weekday9AM.getTime().getTime())));
        weekdayNineToFiveEvents.getProperties().add(
                                        new DtEnd(new Date(weekday5PM.getTime().getTime())));

        summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED DAILY");

        dailyWeekdayEvents = new VEvent();
        dailyWeekdayEvents.getProperties().add(rruleDaily);
        dailyWeekdayEvents.getProperties().add(summary);
        dailyWeekdayEvents.getProperties().add(
                                        new DtStart(new Date(weekday9AM.getTime().getTime())));
        dailyWeekdayEvents.getProperties().add(
                                        new DtEnd(new Date(weekday5PM.getTime().getTime())));

        summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED MONTHLY");

        monthlyWeekdayEvents = new VEvent();
        monthlyWeekdayEvents.getProperties().add(rruleMonthly);
        monthlyWeekdayEvents.getProperties().add(summary);
        monthlyWeekdayEvents.getProperties().add(
                                        new DtStart(new Date(weekday9AM.getTime().getTime())));
        monthlyWeekdayEvents.getProperties().add(
                                        new DtEnd(new Date(weekday5PM.getTime().getTime())));
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

        DtStart start = new DtStart(new Date(calendar.getTime().getTime()));
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

        VEvent christmas = new VEvent(new Date(cal.getTime().getTime()), "Christmas Day");

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

        VEvent meeting = new VEvent(new Date(cal.getTime().getTime()), new Dur(0, 1, 0, 0), "Progress Meeting");

        // add timezone information..
        VTimeZone tz = VTimeZone.getDefault();
        TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());
        meeting.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);       

        log.info(meeting);
    }
    

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
        queryEndDate.set(2005, Calendar.MAY, 1, 07, 15, 0);
        queryEndDate.set(Calendar.MILLISECOND, 0);
        Calendar week1EndDate = Calendar.getInstance();
        week1EndDate.set(2005, Calendar.APRIL, 8, 11, 15, 0);
        week1EndDate.set(Calendar.MILLISECOND, 0);
        Calendar week4StartDate = Calendar.getInstance();
        week4StartDate.set(2005, Calendar.APRIL, 24, 14, 47, 0);
        week4StartDate.set(Calendar.MILLISECOND, 0);

        // This range is monday to friday every three weeks, starting from
        // March 7th 2005, which means for our query dates we need
        // April 18th through to the 22nd.
        PeriodList weeklyPeriods =
                weekdayNineToFiveEvents.getConsumedTime(new Date(queryStartDate.getTime().getTime()),
                        new Date(queryEndDate.getTime().getTime()));
        PeriodList dailyPeriods =
                dailyWeekdayEvents.getConsumedTime(new Date(queryStartDate.getTime().getTime()),
                        new Date(queryEndDate.getTime().getTime()));
//                                                      week1EndDate.getTime());
        dailyPeriods.addAll(dailyWeekdayEvents.getConsumedTime(new Date(week4StartDate.getTime().getTime()),
                new Date(queryEndDate.getTime().getTime())));

        Calendar expectedCal = Calendar.getInstance();
        expectedCal.set(2005, Calendar.APRIL, 4, 9, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedStartOfFirstRange = new Date(expectedCal.getTime().getTime());
        expectedCal.set(2005, Calendar.APRIL, 4, 17, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedEndOfFirstRange = new Date(expectedCal.getTime().getTime());
        assertNotNull(weeklyPeriods);
        assertTrue(weeklyPeriods.size() > 0);
        Period firstPeriod = (Period) weeklyPeriods.toArray()[0];
        assertEquals(expectedStartOfFirstRange, firstPeriod.getStart());
        assertEquals(expectedEndOfFirstRange, firstPeriod.getEnd());
        assertEquals(dailyPeriods, weeklyPeriods);

    }


    /**
     * Test whether you can select weekdays using a daily frequency.
     * <p>
     * This test really belongs in RecurTest, but the weekly range test
     * in this VEventTest matches so perfectly with the daily range test
     * that should produce the same results for some weeks that it was
     * felt leveraging the existing test code was more important.
     * <p>
     * This addresses bug
     * <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1203990&group_id=107024&atid=646395">1203990</a>
     *
     */
   public final void testGetConsumedTimeDaily() throws Exception {

        // Test Starts 04/03/2005, Ends One week later.
        // Query Calendar Start and End Dates.
        Calendar queryStartDate = Calendar.getInstance();
        queryStartDate.set(2005, Calendar.APRIL, 3, 05, 12, 0);
        queryStartDate.set(Calendar.MILLISECOND, 0);
        Calendar queryEndDate = Calendar.getInstance();
        queryEndDate.set(2005, Calendar.APRIL, 10, 21, 55, 0);
        queryEndDate.set(Calendar.MILLISECOND, 0);

        // This range is Monday to Friday every day (which has a filtering
        // effect), starting from March 7th 2005. Our query dates are
        // April 3rd through to the 10th.
        PeriodList weeklyPeriods =
                weekdayNineToFiveEvents.getConsumedTime(new Date(queryStartDate.getTime().getTime()),
                        new Date(queryEndDate.getTime().getTime()));
        PeriodList dailyPeriods =
                dailyWeekdayEvents.getConsumedTime(new Date(queryStartDate.getTime().getTime()),
                        new Date(queryEndDate.getTime().getTime()));
        Calendar expectedCal = Calendar.getInstance();
        expectedCal.set(2005, Calendar.APRIL, 4, 9, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedStartOfFirstRange = new Date(expectedCal.getTime().getTime());
        expectedCal.set(2005, Calendar.APRIL, 4, 17, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedEndOfFirstRange = new Date(expectedCal.getTime().getTime());
        assertNotNull(dailyPeriods);
        assertTrue(dailyPeriods.size() > 0);
        Period firstPeriod = (Period) dailyPeriods.toArray()[0];
        assertEquals(expectedStartOfFirstRange, firstPeriod.getStart());
        assertEquals(expectedEndOfFirstRange, firstPeriod.getEnd());
        assertEquals(weeklyPeriods, dailyPeriods);
    }


    /**
     * Test whether you can select weekdays using a monthly frequency.
     * <p>
     * This test really belongs in RecurTest, but the weekly range test
     * in this VEventTest matches so perfectly with the daily range test
     * that should produce the same results for some weeks that it was
     * felt leveraging the existing test code was more important.
     * <p>
     * Section 4.3.10 of the iCalendar specification RFC 2445 reads:
     * <pre>
     * If an integer modifier is not present, it means all days of
     * this type within the specified frequency.
     * </pre>
     * This test ensures compliance.
     */
   public final void testGetConsumedTimeMonthly() throws Exception {

        // Test Starts 04/03/2005, Ends two weeks later.
        // Query Calendar Start and End Dates.
        Calendar queryStartDate = Calendar.getInstance();
        queryStartDate.set(2005, Calendar.APRIL, 3, 05, 12, 0);
        queryStartDate.set(Calendar.MILLISECOND, 0);
        Calendar queryEndDate = Calendar.getInstance();
        queryEndDate.set(2005, Calendar.APRIL, 17, 21, 55, 0);
        queryEndDate.set(Calendar.MILLISECOND, 0);

        // This range is Monday to Friday every month (which has a multiplying
        // effect), starting from March 7th 2005. Our query dates are
        // April 3rd through to the 17th.
        PeriodList monthlyPeriods =
                monthlyWeekdayEvents.getConsumedTime(new Date(queryStartDate.getTime().getTime()),
                        new Date(queryEndDate.getTime().getTime()));
        PeriodList dailyPeriods =
                dailyWeekdayEvents.getConsumedTime(new Date(queryStartDate.getTime().getTime()),
                        new Date(queryEndDate.getTime().getTime()));
        Calendar expectedCal = Calendar.getInstance();
        expectedCal.set(2005, Calendar.APRIL, 4, 9, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedStartOfFirstRange = new Date(expectedCal.getTime().getTime());
        expectedCal.set(2005, Calendar.APRIL, 4, 17, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedEndOfFirstRange = new Date(expectedCal.getTime().getTime());
        assertNotNull(monthlyPeriods);
        assertTrue(monthlyPeriods.size() > 0);
        Period firstPeriod = (Period) monthlyPeriods.toArray()[0];
        assertEquals(expectedStartOfFirstRange, firstPeriod.getStart());
        assertEquals(expectedEndOfFirstRange, firstPeriod.getEnd());
        assertEquals(dailyPeriods, monthlyPeriods);
        }


    public final void testGetConsumedTime2() throws Exception {
        String filename = "etc/samples/valid/derryn.ics";
        
        FileInputStream fin = new FileInputStream(filename);
        
        CalendarBuilder builder = new CalendarBuilder();

        net.fortuna.ical4j.model.Calendar calendar = null;

        try {
            calendar = builder.build(fin);
        } catch (IOException e) {
            log.warn("File: " + filename, e);
        } catch (ParserException e) {
            log.warn("File: " + filename, e);
        }

        assertNotNull(calendar);

        try {
            calendar.validate();
        } catch (ValidationException e) {
            assertTrue("Calendar file " + filename + " isn't valid:\n" + e.getMessage(), false);
        }

        log.info("File: " + filename);

        if (log.isDebugEnabled()) {
            log.debug("Calendar:\n=========\n" + calendar.toString());
        }

        Date start = new Date();
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(start);
        endCal.add(Calendar.WEEK_OF_YEAR, 4);
//        Date end = new Date(start.getTime() + (1000 * 60 * 60 * 24 * 7 * 4));
        for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            Component c = (Component) i.next();
            
            if (c instanceof VEvent) {
                PeriodList consumed = ((VEvent) c).getConsumedTime(start, new Date(endCal.getTime().getTime()));
                
                log.debug("Event [" + c + "]");
                log.debug("Consumed time [" + consumed + "]");
            }
        }
    }
    
    /**
     * Test COUNT rules.
     */
    public void testGetConsumeTimeByCount() {
        Recur recur = new Recur(Recur.WEEKLY, 3);
        recur.setInterval(1);
        recur.getDayList().add(WeekDay.SU);
        log.info(recur);
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 8);
        Date start = new Date(cal.getTime().getTime());
//        cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, 10);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        Date end = new Date(cal.getTime().getTime());
//        log.info(recur.getDates(start, end, Value.DATE_TIME));
        
        RRule rrule = new RRule(recur);
        VEvent event = new VEvent(start, end, "Test recurrence COUNT");
        event.getProperties().add(rrule);
        log.info(event);
        
        Calendar rangeCal = Calendar.getInstance();
        Date rangeStart = new Date(rangeCal.getTime().getTime());
        rangeCal.add(Calendar.WEEK_OF_YEAR, 4);
        Date rangeEnd = new Date(rangeCal.getTime().getTime());
        
        log.info(event.getConsumedTime(rangeStart, rangeEnd));
    }
}