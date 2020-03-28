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
package net.fortuna.ical4j.model.component;

import junit.framework.TestSuite;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.*;
import net.fortuna.ical4j.validate.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static net.fortuna.ical4j.model.WeekDay.*;
import static org.junit.Assert.assertNotEquals;

/**
 * $Id: VEventTest.java [28/09/2004]
 * <p/>
 * A test case for VEvents.
 *
 * @author Ben Fortuna
 */
public class VEventTest<T extends Temporal> extends CalendarComponentTest {

    private static Logger log = LoggerFactory.getLogger(VEventTest.class);

    private VEvent event;

    private Period<T> period;

    private T date;

    private TzId tzParam;

    /**
     * @param testMethod
     */
    public VEventTest(String testMethod) {
        super(testMethod, null);
    }

    /**
     * @param testMethod
     * @param event
     */
    public VEventTest(String testMethod, VEvent event) {
        super(testMethod, event);
        this.event = event;
    }

    /**
     * @param testMethod
     * @param component
     * @param period
     */
    public VEventTest(String testMethod, VEvent component, Period<T> period) {
        this(testMethod, component);
        this.period = period;
    }

    /**
     * @param testMethod
     * @param component
     * @param date
     */
    public VEventTest(String testMethod, VEvent component, T date) {
        this(testMethod, component);
        this.date = date;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp();
        // relax validation to avoid UID requirement..
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        TimeZoneRegistryFactory timeZoneRegistryFactory = TimeZoneRegistryFactory.getInstance();
        TimeZoneRegistry registry = timeZoneRegistryFactory.createRegistry();
        // create tzid parameter..
        tzParam = new TzId(registry.getTimeZone("Australia/Melbourne").getID());
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        // relax validation to avoid UID requirement..
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
        super.tearDown();
    }

    /**
     * @param resourceString
     * @return
     */
    private net.fortuna.ical4j.model.Calendar loadCalendar(String resourceString)
            throws IOException, ParserException, ValidationException {

        net.fortuna.ical4j.model.Calendar calendar = Calendars.load(
                getClass().getResource(resourceString));
        calendar.validate();

        log.info("Resource: " + resourceString);

        if (log.isDebugEnabled()) {
            log.debug("Calendar:\n=========\n" + calendar.toString());
        }
        return calendar;
    }

    /**
     *
     */
    public final void testChristmas() {
        // create event start date..
        LocalDate christmasDay = LocalDate.now().withMonth(12).withDayOfMonth(25);

        DtStart<LocalDate> start = new DtStart<>(christmasDay);
        start.getParameters().add(tzParam);
        start.getParameters().add(Value.DATE);

        Summary summary = new Summary("Christmas Day; \n this is a, test\\");

        VEvent christmas = new VEvent();
        christmas.getProperties().add(start);
        christmas.getProperties().add(summary);

        log.info(christmas.toString());
    }

    /**
     * Test creating an event with an associated timezone.
     */
    public final void testMelbourneCup() {
        ZoneId timezone = TimeZoneRegistry.getGlobalZoneId("Australia/Melbourne");

        ZonedDateTime dt = ZonedDateTime.now(timezone).withYear(2005).withMonth(11).withDayOfMonth(1)
                .withHour(15).withMinute(0).withSecond(0);
        VEvent melbourneCup = new VEvent(dt, "Melbourne Cup");

        log.info(melbourneCup.toString());
    }

    public final void test2() {
        LocalDate christmasDay = LocalDate.now().withMonth(12).withDayOfMonth(25);

        VEvent christmas = new VEvent(christmasDay, "Christmas Day");

        // initialise as an all-day event..
        christmas.getProperty(Property.DTSTART).get().getParameters().add(Value.DATE);

        // add timezone information..
        christmas.getProperty(Property.DTSTART).get().getParameters().add(tzParam);

        log.info(christmas.toString());
    }

    public final void test3() {
        // tomorrow..
        ZonedDateTime tomorrow = ZonedDateTime.now().withDayOfMonth(1).withHour(9).withMinute(30);

        VEvent meeting = new VEvent(tomorrow, java.time.Duration.ofHours(1), "Progress Meeting");

        // add timezone information..
        meeting.getProperty(Property.DTSTART).get().getParameters().add(tzParam);

        log.info(meeting.toString());
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
            event.getConsumedTime(null);
            fail("Should've thrown an exception.");
        } catch (RuntimeException re) {
            log.info("Expecting an exception here.");
        }

        // Test Start 04/01/2005, End One month later.
        // Query Calendar Start and End Dates.
        ZonedDateTime queryStart = ZonedDateTime.now().withYear(2005).withMonth(4).withDayOfMonth(1)
                .withHour(14).withMinute(47).withSecond(0).withNano(0);

        ZonedDateTime queryEnd = ZonedDateTime.now().withYear(2005).withMonth(5).withDayOfMonth(1)
                .withHour(7).withMinute(15).withSecond(0).withNano(0);

        ZonedDateTime week1EndDate = ZonedDateTime.now().withYear(2005).withMonth(4).withDayOfMonth(8)
                .withHour(11).withMinute(15).withSecond(0).withNano(0);

        ZonedDateTime week4StartDate = ZonedDateTime.now().withYear(2005).withMonth(4).withDayOfMonth(24)
                .withHour(14).withMinute(47).withSecond(0).withNano(0);

        // This range is monday to friday every three weeks, starting from
        // March 7th 2005, which means for our query dates we need
        // April 18th through to the 22nd.
        List<Period<ZonedDateTime>> weeklyPeriods = event.getConsumedTime(new Period<>(queryStart, queryEnd));
//        PeriodList dailyPeriods = dailyWeekdayEvents.getConsumedTime(queryStart, queryEnd);
//                                                      week1EndDate.getTime());
//        dailyPeriods.addAll(dailyWeekdayEvents.getConsumedTime(week4Start, queryEnd));

        Calendar expectedCal = Calendar.getInstance(TimeZone.getTimeZone(TimeZones.GMT_ID));
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
//        assertEquals(dailyPeriods, weeklyPeriods);

    }


    /**
     * Test whether you can select weekdays using a daily frequency.
     * <p/>
     * This test really belongs in RecurTest, but the weekly range test
     * in this VEventTest matches so perfectly with the daily range test
     * that should produce the same results for some weeks that it was
     * felt leveraging the existing test code was more important.
     * <p/>
     * This addresses bug
     * <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1203990&group_id=107024&atid=646395">1203990</a>
     */
    public final void testGetConsumedTimeDaily() throws Exception {

        // Test Starts 04/03/2005, Ends One week later.
        // Query Calendar Start and End Dates.
        ZonedDateTime queryStartDate = ZonedDateTime.of(2005, 4, 3,
                5, 12, 0, 0, ZoneId.systemDefault());
        ZonedDateTime queryEndDate = ZonedDateTime.of(2005, 4, 10,
                21, 55, 0, 0, ZoneId.systemDefault());

        // This range is Monday to Friday every day (which has a filtering
        // effect), starting from March 7th 2005. Our query dates are
        // April 3rd through to the 10th.
//        PeriodList weeklyPeriods =
//                event.getConsumedTime(new DateTime(queryStartDate.getTime()),
//                        new DateTime(queryEndDate.getTime()));
        List<Period<ZonedDateTime>> dailyPeriods = event.getConsumedTime(new Period<>(queryStartDate, queryEndDate));

        ZonedDateTime expectedStartOfFirstRange = ZonedDateTime.of(2005, 4, 4,
                9, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime expectedEndOfFirstRange = ZonedDateTime.of(2005, 4, 4,
                17, 0, 0, 0, ZoneId.systemDefault());

        assertNotNull(dailyPeriods);
        assertTrue(dailyPeriods.size() > 0);
        Period firstPeriod = (Period) dailyPeriods.toArray()[0];
        assertEquals(expectedStartOfFirstRange, firstPeriod.getStart());
        assertEquals(expectedEndOfFirstRange, firstPeriod.getEnd());
//        assertEquals(weeklyPeriods, dailyPeriods);
    }


    /**
     * Test whether you can select weekdays using a monthly frequency.
     * <p/>
     * This test really belongs in RecurTest, but the weekly range test
     * in this VEventTest matches so perfectly with the daily range test
     * that should produce the same results for some weeks that it was
     * felt leveraging the existing test code was more important.
     * <p/>
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
        ZonedDateTime queryStartDate = ZonedDateTime.of(2005, 4, 3,
                5, 12, 0, 0, ZoneId.systemDefault());
        ZonedDateTime queryEndDate = ZonedDateTime.of(2005, 4, 17,
                21, 55, 0, 0, ZoneId.systemDefault());

        // This range is Monday to Friday every month (which has a multiplying
        // effect), starting from March 7th 2005. Our query dates are
        // April 3rd through to the 17th.
        List<Period<ZonedDateTime>> monthlyPeriods = event.getConsumedTime(new Period<>(queryStartDate, queryEndDate));
//        PeriodList dailyPeriods =
//                dailyWeekdayEvents.getConsumedTime(new DateTime(queryStartDate.getTime()),
//                        new DateTime(queryEndDate.getTime()));

        ZonedDateTime expectedStartOfFirstRange = ZonedDateTime.of(2005, 4, 4,
                9, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime expectedEndOfFirstRange = ZonedDateTime.of(2005, 4, 4,
                17, 0, 0, 0, ZoneId.systemDefault());
        assertNotNull(monthlyPeriods);
        assertTrue(monthlyPeriods.size() > 0);
        Period firstPeriod = (Period) monthlyPeriods.toArray()[0];
        assertEquals(expectedStartOfFirstRange, firstPeriod.getStart());
        assertEquals(expectedEndOfFirstRange, firstPeriod.getEnd());
//        assertEquals(dailyPeriods, monthlyPeriods);
    }


    public final void testGetConsumedTime2() throws Exception {
        String resource = "/samples/valid/derryn.ics";

        net.fortuna.ical4j.model.Calendar calendar = loadCalendar(resource);

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusWeeks(4);

        //Date end = new Date(start.getTime() + (1000 * 60 * 60 * 24 * 7 * 4));
        calendar.getComponents().forEach(calendarComponent -> {
            if (calendarComponent instanceof VEvent) {
                List<Period<LocalDate>> consumed = ((VEvent) calendarComponent).getConsumedTime(
                        new Period<>(start, end));

                log.debug("Event [" + calendarComponent + "]");
                log.debug("Consumed time [" + consumed + "]");
            }
        });
    }

    public final void testGetConsumedTime3() throws Exception {
        String resource = "/samples/valid/calconnect10.ics";

        net.fortuna.ical4j.model.Calendar calendar = loadCalendar(resource);

        Optional<VEvent> vev = calendar.getComponent(Component.VEVENT);

        LocalDate start = (LocalDate) vev.get().getStartDate().get().getDate();
        LocalDate latest = LocalDate.now().plusYears(1);

        List<Period<LocalDate>> pl = vev.get().getConsumedTime(new Period<>(start, latest));
        assertTrue(!pl.isEmpty());
    }

    /**
     * Test COUNT rules.
     */
    public void testGetConsumedTimeByCount() {
        Recur recur = new Recur.Builder().frequency(Frequency.WEEKLY).count(3)
            .interval(1).dayList(new WeekDayList(SU)).build();
        log.info(recur.toString());

        ZonedDateTime start = ZonedDateTime.now().withDayOfMonth(8);
//        cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, 10);
        ZonedDateTime end = start.plusHours(1);
//        log.info(recur.getDates(start, end, Value.DATE_TIME));

        RRule rrule = new RRule(recur);
        VEvent event = new VEvent(start, end, "Test recurrence COUNT");
        event.getProperties().add(rrule);
        log.info(event.toString());

        ZonedDateTime rangeStart = ZonedDateTime.now();
        ZonedDateTime rangeEnd = rangeStart.plusWeeks(4);

        log.info(event.getConsumedTime(new Period<Temporal>(rangeStart, rangeEnd)).toString());
    }

    /**
     * A test to confirm that the end date is calculated correctly
     * from a given start date and duration.
     */
    public final void testEventEndDate() {
        LocalDate startDate = LocalDate.now();
        log.info("Start date: " + startDate);
        VEvent event = new VEvent(startDate,
                java.time.Duration.ofDays(3), "3 day event");
        Temporal endDate = event.getEndDate().get().getDate();
        log.info("End date: " + endDate);
        assertEquals(startDate.plusDays(3), endDate);
    }

    /**
     * A test to confirm that the end date is calculated correctly
     * from a given start date and duration, even when timezone is specified.
     */
    public final void testEventEndDateWithTimeZone() throws ParseException {
        TimeZone timezone = new TimeZoneRegistryImpl().getTimeZone("Asia/Seoul");
        TemporalAdapter startDateTime = TemporalAdapter.parse("20181003T130000", timezone.toZoneId());
        log.info("Start date: " + startDateTime);
        VEvent event = new VEvent(startDateTime.getTemporal(),
                java.time.Duration.ofHours(1), "1 hour event");
        assertEquals(startDateTime.getTemporal(), event.getEndDate().get().getDate());
    }

    /**
     * Test to ensure that EXDATE properties are correctly applied.
     *
     * @throws ParseException
     */
    public void testGetConsumedTimeWithExDate() throws ParseException {

        VEvent event1 = new VEvent(TemporalAdapter.parse("20050103T080000").getTemporal(),
                java.time.Duration.ofMinutes(15), "Event 1");

        Recur rRuleRecur = new Recur("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR");
        RRule rRule = new RRule(rRuleRecur);
        event1.getProperties().add(rRule);

        List<Parameter> parameterList = new ArrayList<>();
        parameterList.add(Value.DATE);
        ExDate exDate = new ExDate(parameterList, "20050106");
        event1.getProperties().add(exDate);

        TemporalAdapter start = TemporalAdapter.parse("20050106");
        TemporalAdapter end = TemporalAdapter.parse("20050107");
        List<Period<Temporal>> list = event1.getConsumedTime(new Period<>(start.getTemporal(), end.getTemporal()));
        assertTrue(list.isEmpty());
    }

    /**
     * Test to ensure that EXDATE properties are correctly applied.
     *
     * @throws ParseException
     */
    public void testGetConsumedTimeWithExDate2() throws IOException, ParserException {
        InputStream in = getClass().getResourceAsStream("/samples/valid/friday13.ics");
        net.fortuna.ical4j.model.Calendar calendar = new CalendarBuilder().build(in);

        Optional<VEvent> event = calendar.getComponent(Component.VEVENT);

        LocalDate start = LocalDate.now().withYear(1997).withMonth(8).withDayOfMonth(2);
        LocalDate end = start.withDayOfMonth(4);

        List<Period<LocalDate>> periods = event.get().getConsumedTime(new Period<>(start, end));
        assertTrue(periods.isEmpty());
    }

    /**
     * Test equality of events with different alarm sub-components.
     */
    public void testEquals() {
        String summary = "test event";
        PropertyList props = new PropertyList();
        props.add(new DtStart<>(Instant.now()));
        props.add(new Summary(summary));

        VEvent e1 = new VEvent(props);
        VEvent e2 = new VEvent(props);

        assertEquals(e1, e2);

        e2.getAlarms().add(new VAlarm());

        assertNotEquals(e1, e2);
    }

    /**
     *
     */
    public void testCalculateRecurrenceSetNotEmpty() {
        List<Period<T>> recurrenceSet = event.calculateRecurrenceSet(period);
        assertTrue(!recurrenceSet.isEmpty());
    }

    /**
     * Unit tests for {@link VEvent#getOccurrence(Temporal)}.
     */
    public void testGetOccurrence() throws IOException, ParseException, URISyntaxException {
        VEvent occurrence = event.getOccurrence(date);
        assertNotNull(occurrence);
        assertEquals(event.getUid(), occurrence.getUid());
    }

    /**
     * @return
     * @throws ValidationException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws IOException
     * @throws ParserException
     */
    public static TestSuite suite() throws ValidationException, ParseException, IOException, URISyntaxException, ParserException {
        UidGenerator uidGenerator = new RandomUidGenerator();

        ZonedDateTime weekday9AM = ZonedDateTime.now().withYear(2005).withMonth(3).withDayOfMonth(7)
                .withHour(9).withMinute(0).withSecond(0).withNano(0);

        ZonedDateTime weekday5PM = ZonedDateTime.now().withYear(2005).withMonth(3).withDayOfMonth(7)
                .withHour(17).withMinute(0).withSecond(0).withNano(0);

        // Do the recurrence until December 31st.
        LocalDate until = LocalDate.now().withYear(2005).withMonth(12).withDayOfMonth(31);

        // 9:00AM to 5:00PM Rule using weekly
        Recur recurWeekly = new Recur.Builder().frequency(Frequency.WEEKLY).until(until)
            .dayList(new WeekDayList(MO, TU, WE, TH, FR))
            .interval(1).weekStartDay(MO).build();
        RRule rruleWeekly = new RRule(recurWeekly);

        // 9:00AM to 5:00PM Rule using daily frequency
        Recur recurDaily = new Recur.Builder().frequency(Frequency.DAILY).until(until)
            .dayList(new WeekDayList(MO, TU, WE, TH, FR))
            .interval(1).weekStartDay(MO).build();
        RRule rruleDaily = new RRule(recurDaily);

        // 9:00AM to 5:00PM Rule using monthly frequency
        Recur recurMonthly = new Recur.Builder().frequency(Frequency.MONTHLY).until(until)
            .dayList(new WeekDayList(MO, TU, WE, TH, FR))
            .interval(1).weekStartDay(MO).build();
        RRule rruleMonthly = new RRule(recurMonthly);

        Summary summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED WEEKLY");

        List<Parameter> tzParams = new ArrayList<>();
        tzParams.add(new TzId("Australia/Melbourne"));

        VEvent weekdayNineToFiveEvents = new VEvent();
        weekdayNineToFiveEvents.getProperties().add(rruleWeekly);
        weekdayNineToFiveEvents.getProperties().add(summary);
        DtStart dtStart = new DtStart<>(tzParams, weekday9AM);
//        dtStart.getParameters().add(Value.DATE);
        weekdayNineToFiveEvents.getProperties().add(dtStart);
        DtEnd dtEnd = new DtEnd<>(tzParams, weekday5PM);
//        dtEnd.getParameters().add(Value.DATE);
        weekdayNineToFiveEvents.getProperties().add(dtEnd);
        weekdayNineToFiveEvents.getProperties().add(uidGenerator.generateUid());
        // ensure event is valid..
        weekdayNineToFiveEvents.validate();

        summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED DAILY");

        VEvent dailyWeekdayEvents = new VEvent();
        dailyWeekdayEvents.getProperties().add(rruleDaily);
        dailyWeekdayEvents.getProperties().add(summary);
        DtStart dtStart2 = new DtStart<>(tzParams, weekday9AM);
//        dtStart2.getParameters().add(Value.DATE);
        dailyWeekdayEvents.getProperties().add(dtStart2);
        DtEnd dtEnd2 = new DtEnd<>(tzParams, weekday5PM);
//        dtEnd2.getParameters().add(Value.DATE);
        dailyWeekdayEvents.getProperties().add(dtEnd2);
        dailyWeekdayEvents.getProperties().add(uidGenerator.generateUid());
        // ensure event is valid..
        dailyWeekdayEvents.validate();

        summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED MONTHLY");

        VEvent monthlyWeekdayEvents = new VEvent();
        monthlyWeekdayEvents.getProperties().add(rruleMonthly);
        monthlyWeekdayEvents.getProperties().add(summary);
        DtStart dtStart3 = new DtStart<>(tzParams, weekday9AM);
//        dtStart3.getParameters().add(Value.DATE);
        monthlyWeekdayEvents.getProperties().add(dtStart3);
        DtEnd dtEnd3 = new DtEnd<>(tzParams, weekday5PM);
//        dtEnd3.getParameters().add(Value.DATE);
        monthlyWeekdayEvents.getProperties().add(dtEnd3);
        monthlyWeekdayEvents.getProperties().add(uidGenerator.generateUid());
        // ensure event is valid..
        monthlyWeekdayEvents.validate();

        // enable relaxed parsing to allow copying of invalid events..
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);

        TestSuite suite = new TestSuite();

        //testCalculateRecurrenceSet..
        LocalDateTime periodStart = (LocalDateTime) TemporalAdapter.parse("20050101T000000").getTemporal();
        LocalDateTime periodEnd = (LocalDateTime) TemporalAdapter.parse("20051231T235959").getTemporal();
        Period<LocalDateTime> period = new Period<>(periodStart, periodEnd);
        suite.addTest(new VEventTest<>("testCalculateRecurrenceSetNotEmpty", weekdayNineToFiveEvents, period));

        //testGetOccurrence..
        suite.addTest(new VEventTest<>("testGetOccurrence", weekdayNineToFiveEvents,
                weekdayNineToFiveEvents.getStartDate().get().getDate()));

        //testGetConsumedTime..
        suite.addTest(new VEventTest("testGetConsumedTime", weekdayNineToFiveEvents));
        suite.addTest(new VEventTest("testGetConsumedTimeDaily", dailyWeekdayEvents));
        suite.addTest(new VEventTest("testGetConsumedTimeMonthly", monthlyWeekdayEvents));

        //test event validation..
        UidGenerator ug = new RandomUidGenerator();
        Uid uid = ug.generateUid();

        List<Parameter> startParams = new ArrayList<>();
        tzParams.add(new TzId(ZoneId.systemDefault().getId()));
        DtStart<ZonedDateTime> start = new DtStart<>(startParams, ZonedDateTime.now());

        DtEnd<ZonedDateTime> end = new DtEnd<>(ZonedDateTime.now());
        VEvent event = new VEvent();

        event.getProperties().add(uid);
        event.getProperties().add(start);
        event.getProperties().add(end);
        suite.addTest(new VEventTest("testValidation", event));

        event = (VEvent) event.copy();
//        start = (DtStart) event.getProperty(Property.DTSTART);
        start = new DtStart<>(ZonedDateTime.now());
        start.getParameters().add(Value.DATE_TIME);
        event.getProperties().remove(event.getProperty(Property.DTSTART).get());
        event.getProperties().add(start);
        suite.addTest(new VEventTest("testValidationException", event));

        // test 1..
        event = (VEvent) event.copy();
        start = (DtStart<ZonedDateTime>) event.getProperty(Property.DTSTART).get();
        start.getParameters().add(Value.DATE);
        suite.addTest(new VEventTest("testValidationException", event));

//        event = (VEvent) event.copy();
//        start = (DtStart) event.getProperty(Property.DTSTART);
//        start.getParameters().remove(Value.DATE_TIME);
//        end = (DtEnd) event.getProperty(Property.DTEND);
//        end.getParameters().replace(Value.DATE_TIME);
//        suite.addTest(new VEventTest("testValidation", event));

        // test 2..
        event = event.copy();
        start = (DtStart<ZonedDateTime>) event.getProperty(Property.DTSTART).get();
        start.getParameters().add(Value.DATE_TIME);
        end = (DtEnd<ZonedDateTime>) event.getProperty(Property.DTEND).get();
        end.getParameters().add(Value.DATE);
        suite.addTest(new VEventTest("testValidationException", event));

        // test 3..
//        event = (VEvent) event.copy();
//        start = (DtStart) event.getProperty(Property.DTSTART);
//        start.getParameters().remove(Value.DATE);
//        end = (DtEnd) event.getProperty(Property.DTEND);
//        end.getParameters().replace(Value.DATE);
//        suite.addTest(new VEventTest("testValidationException", event));

        // disable relaxed parsing after copying invalid events..
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);

        suite.addTest(new VEventTest("testChristmas"));
        suite.addTest(new VEventTest("testMelbourneCup"));
        suite.addTest(new VEventTest("testGetConsumedTime2"));
        suite.addTest(new VEventTest("testGetConsumedTime3"));
        suite.addTest(new VEventTest("testGetConsumedTimeByCount"));
        suite.addTest(new VEventTest("testEventEndDate"));
        suite.addTest(new VEventTest("testGetConsumedTimeWithExDate"));
        suite.addTest(new VEventTest("testGetConsumedTimeWithExDate2"));
        suite.addTest(new VEventTest("testIsCalendarComponent", event));
        suite.addTest(new VEventTest("testEquals"));
//        suite.addTest(new VEventTest("testValidation"));

        // use relaxed unfolding for samples..
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);

        // test iTIP validation..
//        File[] testFiles = new File("etc/samples/valid").listFiles((FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
        URL[] testFiles = new URL[]{VEventTest.class.getResource("/samples/valid/calconnect.ics"), VEventTest.class.getResource("/samples/valid/calconnect10.ics")};
        for (URL testFile : testFiles) {
            log.info("Sample [" + testFile + "]");
            net.fortuna.ical4j.model.Calendar calendar = Calendars.load(testFile);
            if (Method.PUBLISH.equals(calendar.getProperty(Property.METHOD).get())) {
                calendar.getComponents(Component.VEVENT).forEach(calendarComponent -> {
                    suite.addTest(new VEventTest("testPublishValidation", (VEvent) calendarComponent));
                });
            } else if (Method.REQUEST.equals(calendar.getProperty(Property.METHOD).get())) {
                calendar.getComponents(Component.VEVENT).forEach(calendarComponent -> {
                    suite.addTest(new VEventTest("testRequestValidation", (VEvent) calendarComponent));
                });
            }
        }

        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);

        return suite;
    }
}
