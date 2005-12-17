/*
 * $Id: VEventTest.java [28/09/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.ical4j.model.component;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentTest;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.util.TimeZones;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A test case for VEvents.
 * @author Ben Fortuna
 */
public class VEventTest extends ComponentTest {

    private static Log log = LogFactory.getLog(VEventTest.class);

    private TimeZoneRegistry registry;
    
    private VTimeZone tz;
    
    private TzId tzParam;

    private VEvent weekdayNineToFiveEvents = null;
    
    private VEvent dailyWeekdayEvents = null;
    
    private VEvent monthlyWeekdayEvents = null;

    public void setUp() throws Exception {
        super.setUp();
        registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        // create timezone property..
        tz = registry.getTimeZone("Australia/Melbourne").getVTimeZone();
        // create tzid parameter..
        tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());

        Calendar weekday9AM = getCalendarInstance();
        weekday9AM.set(2005, Calendar.MARCH, 7, 9, 0, 0);
        weekday9AM.set(Calendar.MILLISECOND, 0);

        Calendar weekday5PM = getCalendarInstance();
        weekday5PM.set(2005, Calendar.MARCH, 7, 17, 0, 0);
        weekday5PM.set(Calendar.MILLISECOND, 0);

        // Do the recurrence until December 31st.
        Calendar untilCal = getCalendarInstance();
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
        DtStart dtStart = new DtStart(new DateTime(weekday9AM.getTime().getTime()));
//        dtStart.getParameters().add(Value.DATE);
        weekdayNineToFiveEvents.getProperties().add(dtStart);
        DtEnd dtEnd = new DtEnd(new DateTime(weekday5PM.getTime().getTime()));
//        dtEnd.getParameters().add(Value.DATE);
        weekdayNineToFiveEvents.getProperties().add(dtEnd);
        // ensure event is valid..
        weekdayNineToFiveEvents.validate();

        summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED DAILY");

        dailyWeekdayEvents = new VEvent();
        dailyWeekdayEvents.getProperties().add(rruleDaily);
        dailyWeekdayEvents.getProperties().add(summary);
        DtStart dtStart2 = new DtStart(new DateTime(weekday9AM.getTime().getTime()));
//        dtStart2.getParameters().add(Value.DATE);
        dailyWeekdayEvents.getProperties().add(dtStart2);
        DtEnd dtEnd2 = new DtEnd(new DateTime(weekday5PM.getTime().getTime()));
//        dtEnd2.getParameters().add(Value.DATE);
        dailyWeekdayEvents.getProperties().add(dtEnd2);
        // ensure event is valid..
        dailyWeekdayEvents.validate();

        summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED MONTHLY");

        monthlyWeekdayEvents = new VEvent();
        monthlyWeekdayEvents.getProperties().add(rruleMonthly);
        monthlyWeekdayEvents.getProperties().add(summary);
        DtStart dtStart3 = new DtStart(new DateTime(weekday9AM.getTime().getTime()));
//        dtStart3.getParameters().add(Value.DATE);
        monthlyWeekdayEvents.getProperties().add(dtStart3);
        DtEnd dtEnd3 = new DtEnd(new DateTime(weekday5PM.getTime().getTime()));
//        dtEnd3.getParameters().add(Value.DATE);
        monthlyWeekdayEvents.getProperties().add(dtEnd3);
        // ensure event is valid..
        monthlyWeekdayEvents.validate();
    }
    
    /**
     * @return
     */
    private Calendar getCalendarInstance() {
        return Calendar.getInstance(); //java.util.TimeZone.getTimeZone(TimeZones.GMT_ID));
    }

    /**
     *  
     */
    public final void testChristmas() {
        // create event start date..
        java.util.Calendar calendar = getCalendarInstance();
        calendar.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 25);

        DtStart start = new DtStart(new Date(calendar.getTime()));
        start.getParameters().add(tzParam);
        start.getParameters().add(Value.DATE);

        Summary summary = new Summary("Christmas Day; \n this is a, test\\");

        VEvent christmas = new VEvent();
        christmas.getProperties().add(start);
        christmas.getProperties().add(summary);

        log.info(christmas);
    }
    
    /**
     * Test creating an event with an associated timezone.
     */
    public final void testMelbourneCup() {
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Australia/Melbourne");

        java.util.Calendar cal = java.util.Calendar.getInstance(timezone);
        cal.set(java.util.Calendar.YEAR, 2005);
        cal.set(java.util.Calendar.MONTH, java.util.Calendar.NOVEMBER);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 15);
        cal.clear(java.util.Calendar.MINUTE);
        cal.clear(java.util.Calendar.SECOND);

        DateTime dt = new DateTime(cal.getTime());
        dt.setTimeZone(timezone);
        VEvent melbourneCup = new VEvent(dt, "Melbourne Cup");
        
        log.info(melbourneCup);
    }

    public final void test2() {
        java.util.Calendar cal = getCalendarInstance();
        cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 25);

        VEvent christmas = new VEvent(new Date(cal.getTime()), "Christmas Day");

        // initialise as an all-day event..
        christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);

        // add timezone information..
        christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);

        log.info(christmas);
    }
    
    public final void test3() {
        java.util.Calendar cal = getCalendarInstance();
        // tomorrow..
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 9);
        cal.set(java.util.Calendar.MINUTE, 30);

        VEvent meeting = new VEvent(new DateTime(cal.getTime().getTime()), new Dur(0, 1, 0, 0), "Progress Meeting");

        // add timezone information..
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
        Calendar queryStartDate = getCalendarInstance();
        queryStartDate.set(2005, Calendar.APRIL, 1, 14, 47, 0);
        queryStartDate.set(Calendar.MILLISECOND, 0);
        DateTime queryStart = new DateTime(queryStartDate.getTime().getTime());
        
        Calendar queryEndDate = getCalendarInstance();
        queryEndDate.set(2005, Calendar.MAY, 1, 07, 15, 0);
        queryEndDate.set(Calendar.MILLISECOND, 0);
        DateTime queryEnd = new DateTime(queryEndDate.getTime().getTime());

        Calendar week1EndDate = getCalendarInstance();
        week1EndDate.set(2005, Calendar.APRIL, 8, 11, 15, 0);
        week1EndDate.set(Calendar.MILLISECOND, 0);
        
        Calendar week4StartDate = getCalendarInstance();
        week4StartDate.set(2005, Calendar.APRIL, 24, 14, 47, 0);
        week4StartDate.set(Calendar.MILLISECOND, 0);
        DateTime week4Start = new DateTime(week4StartDate.getTime().getTime());

        // This range is monday to friday every three weeks, starting from
        // March 7th 2005, which means for our query dates we need
        // April 18th through to the 22nd.
        PeriodList weeklyPeriods = weekdayNineToFiveEvents.getConsumedTime(queryStart, queryEnd);
        PeriodList dailyPeriods = dailyWeekdayEvents.getConsumedTime(queryStart, queryEnd);
//                                                      week1EndDate.getTime());
        dailyPeriods.addAll(dailyWeekdayEvents.getConsumedTime(week4Start, queryEnd));

        Calendar expectedCal = Calendar.getInstance(); //TimeZone.getTimeZone(TimeZones.GMT_ID));
        expectedCal.set(2005, Calendar.APRIL, 1, 9, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedStartOfFirstRange = new DateTime(expectedCal.getTime().getTime());
        expectedCal.set(2005, Calendar.APRIL, 1, 17, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedEndOfFirstRange = new DateTime(expectedCal.getTime().getTime());
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
        Calendar queryStartDate = getCalendarInstance();
        queryStartDate.set(2005, Calendar.APRIL, 3, 05, 12, 0);
        queryStartDate.set(Calendar.MILLISECOND, 0);
        Calendar queryEndDate = getCalendarInstance();
        queryEndDate.set(2005, Calendar.APRIL, 10, 21, 55, 0);
        queryEndDate.set(Calendar.MILLISECOND, 0);

        // This range is Monday to Friday every day (which has a filtering
        // effect), starting from March 7th 2005. Our query dates are
        // April 3rd through to the 10th.
        PeriodList weeklyPeriods =
                weekdayNineToFiveEvents.getConsumedTime(new DateTime(queryStartDate.getTime()),
                        new DateTime(queryEndDate.getTime()));
        PeriodList dailyPeriods =
                dailyWeekdayEvents.getConsumedTime(new DateTime(queryStartDate.getTime()),
                        new DateTime(queryEndDate.getTime()));
        Calendar expectedCal = getCalendarInstance();
        expectedCal.set(2005, Calendar.APRIL, 4, 9, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedStartOfFirstRange = new DateTime(expectedCal.getTime());
        expectedCal.set(2005, Calendar.APRIL, 4, 17, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedEndOfFirstRange = new DateTime(expectedCal.getTime());
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
        Calendar queryStartDate = getCalendarInstance();
        queryStartDate.set(2005, Calendar.APRIL, 3, 05, 12, 0);
        queryStartDate.set(Calendar.MILLISECOND, 0);
        Calendar queryEndDate = getCalendarInstance();
        queryEndDate.set(2005, Calendar.APRIL, 17, 21, 55, 0);
        queryEndDate.set(Calendar.MILLISECOND, 0);

        // This range is Monday to Friday every month (which has a multiplying
        // effect), starting from March 7th 2005. Our query dates are
        // April 3rd through to the 17th.
        PeriodList monthlyPeriods =
                monthlyWeekdayEvents.getConsumedTime(new DateTime(queryStartDate.getTime()),
                        new DateTime(queryEndDate.getTime()));
        PeriodList dailyPeriods =
                dailyWeekdayEvents.getConsumedTime(new DateTime(queryStartDate.getTime()),
                        new DateTime(queryEndDate.getTime()));
        Calendar expectedCal = getCalendarInstance();
        expectedCal.set(2005, Calendar.APRIL, 4, 9, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedStartOfFirstRange = new DateTime(expectedCal.getTime());
        expectedCal.set(2005, Calendar.APRIL, 4, 17, 0, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Date expectedEndOfFirstRange = new DateTime(expectedCal.getTime());
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
        Calendar endCal = getCalendarInstance();
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
        
        Calendar cal = getCalendarInstance();
        cal.set(Calendar.DAY_OF_MONTH, 8);
        Date start = new DateTime(cal.getTime());
//        cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, 10);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        Date end = new DateTime(cal.getTime());
//        log.info(recur.getDates(start, end, Value.DATE_TIME));
        
        RRule rrule = new RRule(recur);
        VEvent event = new VEvent(start, end, "Test recurrence COUNT");
        event.getProperties().add(rrule);
        log.info(event);
        
        Calendar rangeCal = getCalendarInstance();
        Date rangeStart = new DateTime(rangeCal.getTime());
        rangeCal.add(Calendar.WEEK_OF_YEAR, 4);
        Date rangeEnd = new DateTime(rangeCal.getTime());
        
        log.info(event.getConsumedTime(rangeStart, rangeEnd));
    }
    
    /**
     * A test to confirm that the end date is calculated correctly
     * from a given start date and duration.
     */
    public final void testEventEndDate() {
        Calendar cal = getCalendarInstance();
        Date startDate = new Date(cal.getTime());
        log.info("Start date: " + startDate);
        VEvent event = new VEvent(startDate, new Dur(3, 0, 0, 0), "3 day event");
        Date endDate = event.getEndDate().getDate();
        log.info("End date: " + endDate);
        cal.add(Calendar.DAY_OF_YEAR, 3);
        assertEquals(new Date(cal.getTime()), endDate);
    }
    
    /**
     * Test to ensure that EXDATE properties are correctly applied.
     * @throws ParseException
     */
    public void testGetConsumedTimeWithExDate() throws ParseException {

        VEvent event1 = new VEvent(new DateTime("20050103T080000"),
                new Dur(0, 0, 15, 0),
                "Event 1");
    
        Recur rRuleRecur = new Recur("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR");
        RRule rRule = new RRule(rRuleRecur);
        event1.getProperties().add(rRule);
    
        ParameterList parameterList = new ParameterList();
        parameterList.add(Value.DATE);
        ExDate exDate = new ExDate(parameterList, "20050106");
        event1.getProperties().add(exDate);
    
        Date start = new Date("20050106");
        Date end = new Date("20050107");
        PeriodList list = event1.getConsumedTime(start, end);
        assertTrue(list.isEmpty());
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.ComponentTest#testIsCalendarComponent()
     */
    public void testIsCalendarComponent() {
        assertIsCalendarComponent(new VEvent());
    }    
}
