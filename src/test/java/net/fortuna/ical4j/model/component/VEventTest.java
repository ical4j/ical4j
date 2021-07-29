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
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.transform.recurrence.Frequency;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    @Override
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
    @Override
    protected void tearDown() throws Exception {
        // relax validation to avoid UID requirement..
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);
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

        DtStart<LocalDate> start = new DtStart<>(christmasDay).add(tzParam).add(Value.DATE);

        Summary summary = new Summary("Christmas Day; \n this is a, test\\");

        VEvent christmas = new VEvent().add(start).add(summary);

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

    public final void test2() throws ConstraintViolationException {
        LocalDate christmasDay = LocalDate.now().withMonth(12).withDayOfMonth(25);

        VEvent christmas = new VEvent(christmasDay, "Christmas Day");

        // initialise as an all-day event..
        christmas.getProperties().getRequired(Property.DTSTART).add(Value.DATE);

        // add timezone information..
        christmas.getProperties().getRequired(Property.DTSTART).add(tzParam);

        log.info(christmas.toString());
    }

    public final void test3() throws ConstraintViolationException {
        // tomorrow..
        ZonedDateTime tomorrow = ZonedDateTime.now().withDayOfMonth(1).withHour(9).withMinute(30);

        VEvent meeting = new VEvent(tomorrow, java.time.Duration.ofHours(1), "Progress Meeting");

        // add timezone information..
        meeting.getProperties().getRequired(Property.DTSTART).add(tzParam);

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

        ZonedDateTime queryEnd = queryStart.withMonth(5).withHour(7).withMinute(15);

        ZonedDateTime week1EndDate = queryStart.withDayOfMonth(8).withHour(11).withMinute(15);

        ZonedDateTime week4StartDate = queryStart.withDayOfMonth(24).withHour(14).withMinute(47);

        // This range is monday to friday every three weeks, starting from
        // March 7th 2005, which means for our query dates we need
        // April 18th through to the 22nd.
        List<Period<ZonedDateTime>> weeklyPeriods = event.getConsumedTime(new Period<>(queryStart, queryEnd));
//        PeriodList dailyPeriods = dailyWeekdayEvents.getConsumedTime(queryStart, queryEnd);
//                                                      week1EndDate.getTime());
//        dailyPeriods.addAll(dailyWeekdayEvents.getConsumedTime(week4Start, queryEnd));
        ZonedDateTime expectedStartOfFirstRange = queryStart.withHour(9).withMinute(0);
        ZonedDateTime expectedEndOfFirstRange = queryStart.withHour(17).withMinute(0);

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

        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = start.plusWeeks(4);

        //Date end = new Date(start.getTime() + (1000 * 60 * 60 * 24 * 7 * 4));
        calendar.getComponents().getAll().forEach(calendarComponent -> {
            if (calendarComponent instanceof VEvent) {
                List<Period<ZonedDateTime>> consumed = ((VEvent) calendarComponent).getConsumedTime(
                        new Period<>(start, end));

                log.debug("Event [" + calendarComponent + "]");
                log.debug("Consumed time [" + consumed + "]");
            }
        });
    }

    public final void testGetConsumedTime3() throws Exception {
        String resource = "/samples/valid/calconnect10.ics";

        net.fortuna.ical4j.model.Calendar calendar = loadCalendar(resource);

        VEvent vev = calendar.getComponents().getRequired(Component.VEVENT);

        LocalDate start = (LocalDate) vev.getStartDate().get().getDate();
        LocalDate latest = LocalDate.now().plusYears(1);

        List<Period<LocalDate>> pl = vev.getConsumedTime(new Period<>(start, latest));
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
        event.add(rrule);
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
        VEvent event = new VEvent(startDate, java.time.Period.ofDays(3), "3 day event");
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

        Recur<LocalDateTime> rRuleRecur = new Recur<>("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR");
        RRule<LocalDateTime> rRule = new RRule<>(rRuleRecur);
        event1.add(rRule);

        ParameterList parameterList = new ParameterList(Collections.singletonList(Value.DATE));
        ExDate<LocalDateTime> exDate = new ExDate<>(parameterList, "20050106T000000");
        event1.add(exDate);

        TemporalAdapter<LocalDateTime> start = TemporalAdapter.parse("20050106T000000");
        TemporalAdapter<LocalDateTime> end = TemporalAdapter.parse("20050107T000000");
        List<Period<Temporal>> list = event1.getConsumedTime(new Period<>(start.getTemporal(), end.getTemporal()));
        assertTrue(list.isEmpty());
    }

    /**
     * Test to ensure that EXDATE properties are correctly applied.
     *
     * @throws ParseException
     */
    public void testGetConsumedTimeWithExDate2() throws IOException, ParserException, ConstraintViolationException {
        InputStream in = getClass().getResourceAsStream("/samples/valid/friday13.ics");
        net.fortuna.ical4j.model.Calendar calendar = new CalendarBuilder().build(in);

        VEvent event = calendar.getComponents().getRequired(Component.VEVENT);

        ZonedDateTime start = ZonedDateTime.now().withYear(1997).withMonth(8).withDayOfMonth(2);
        ZonedDateTime end = start.withDayOfMonth(4);

        List<Period<ZonedDateTime>> periods = event.getConsumedTime(new Period<>(start, end));
        assertTrue(periods.isEmpty());
    }

    /**
     * Test equality of events with different alarm sub-components.
     */
    public void testEquals() {
        String summary = "test event";
        PropertyList props = new PropertyList(Arrays.asList(new DtStart<>(Instant.now()), new Summary(summary)));

        VEvent e1 = new VEvent(props);
        VEvent e2 = new VEvent(props);

        assertEquals(e1, e2);

        e2.add(new VAlarm());

        assertNotEquals(e1, e2);
    }

    /**
     *
     */
    public void testCalculateRecurrenceSetNotEmpty() {
        Set<Period<T>> recurrenceSet = event.calculateRecurrenceSet(period);
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
    public static TestSuite suite() throws ValidationException, IOException, ParserException, ConstraintViolationException {
        UidGenerator uidGenerator = new RandomUidGenerator();

        ZonedDateTime weekday9AM = ZonedDateTime.now().withYear(2005).withMonth(3).withDayOfMonth(7)
                .withHour(9).withMinute(0).withSecond(0).withNano(0);

        ZonedDateTime weekday5PM = ZonedDateTime.now().withYear(2005).withMonth(3).withDayOfMonth(7)
                .withHour(17).withMinute(0).withSecond(0).withNano(0);

        // Do the recurrence until December 31st.
        ZonedDateTime until = ZonedDateTime.now().withYear(2005).withMonth(12).withDayOfMonth(31);

        // 9:00AM to 5:00PM Rule using weekly
        Recur<ZonedDateTime> recurWeekly = new Recur.Builder<ZonedDateTime>().frequency(Frequency.WEEKLY).until(until)
            .dayList(new WeekDayList(MO, TU, WE, TH, FR))
            .interval(1).weekStartDay(MO).build();
        RRule<ZonedDateTime> rruleWeekly = new RRule<>(recurWeekly);

        // 9:00AM to 5:00PM Rule using daily frequency
        Recur<ZonedDateTime> recurDaily = new Recur.Builder<ZonedDateTime>().frequency(Frequency.DAILY).until(until)
            .dayList(new WeekDayList(MO, TU, WE, TH, FR))
            .interval(1).weekStartDay(MO).build();
        RRule<ZonedDateTime> rruleDaily = new RRule<>(recurDaily);

        // 9:00AM to 5:00PM Rule using monthly frequency
        Recur<ZonedDateTime> recurMonthly = new Recur.Builder<ZonedDateTime>().frequency(Frequency.MONTHLY).until(until)
            .dayList(new WeekDayList(MO, TU, WE, TH, FR))
            .interval(1).weekStartDay(MO).build();
        RRule<ZonedDateTime> rruleMonthly = new RRule<>(recurMonthly);

        Summary summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED WEEKLY");

        ParameterList tzParams = new ParameterList(Collections.singletonList(new TzId("Australia/Melbourne")));

        VEvent weekdayNineToFiveEvents = new VEvent().add(rruleWeekly).add(summary);
        DtStart<ZonedDateTime> dtStart = new DtStart<>(tzParams, weekday9AM);
//        dtStart.getParameters().add(Value.DATE);
        weekdayNineToFiveEvents.add(dtStart);
        DtEnd<ZonedDateTime> dtEnd = new DtEnd<>(tzParams, weekday5PM);
//        dtEnd.getParameters().add(Value.DATE);
        weekdayNineToFiveEvents.add(dtEnd).add(uidGenerator.generateUid());
        // ensure event is valid..
        weekdayNineToFiveEvents.validate();

        summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED DAILY");

        VEvent dailyWeekdayEvents = new VEvent().add(rruleDaily).add(summary);
        DtStart<ZonedDateTime> dtStart2 = new DtStart<>(tzParams, weekday9AM);
//        dtStart2.getParameters().add(Value.DATE);
        dailyWeekdayEvents.add(dtStart2);
        DtEnd<ZonedDateTime> dtEnd2 = new DtEnd<>(tzParams, weekday5PM);
//        dtEnd2.getParameters().add(Value.DATE);
        dailyWeekdayEvents.add(dtEnd2).add(uidGenerator.generateUid());
        // ensure event is valid..
        dailyWeekdayEvents.validate();

        summary = new Summary("TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED MONTHLY");

        VEvent monthlyWeekdayEvents = new VEvent().add(rruleMonthly).add(summary);
        DtStart<ZonedDateTime> dtStart3 = new DtStart<>(tzParams, weekday9AM);
//        dtStart3.getParameters().add(Value.DATE);
        monthlyWeekdayEvents.add(dtStart3);
        DtEnd<ZonedDateTime> dtEnd3 = new DtEnd<>(tzParams, weekday5PM);
//        dtEnd3.getParameters().add(Value.DATE);
        monthlyWeekdayEvents.add(dtEnd3).add(uidGenerator.generateUid());
        // ensure event is valid..
        monthlyWeekdayEvents.validate();

        // enable relaxed parsing to allow copying of invalid events..
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);

        TestSuite suite = new TestSuite();

        //testCalculateRecurrenceSet..
        ZonedDateTime periodStart = (ZonedDateTime) TemporalAdapter.parse("20050101T000000",
                ZoneId.systemDefault()).getTemporal();
        ZonedDateTime periodEnd = (ZonedDateTime) TemporalAdapter.parse("20051231T235959",
                ZoneId.systemDefault()).getTemporal();
        Period<ZonedDateTime> period = new Period<>(periodStart, periodEnd);
        suite.addTest(new VEventTest<>("testCalculateRecurrenceSetNotEmpty", weekdayNineToFiveEvents, period));

        //testGetOccurrence..
        suite.addTest(new VEventTest<>("testGetOccurrence", weekdayNineToFiveEvents,
                weekdayNineToFiveEvents.getStartDate().get().getDate()));

        //testGetConsumedTime..
        suite.addTest(new VEventTest<>("testGetConsumedTime", weekdayNineToFiveEvents));
        suite.addTest(new VEventTest<>("testGetConsumedTimeDaily", dailyWeekdayEvents));
        suite.addTest(new VEventTest<>("testGetConsumedTimeMonthly", monthlyWeekdayEvents));

        //test event validation..
        UidGenerator ug = new RandomUidGenerator();
        Uid uid = ug.generateUid();

        ParameterList startParams = new ParameterList(Collections.singletonList(Value.DATE));
        DtStart<LocalDate> start = new DtStart<>(startParams, LocalDate.now());

        ParameterList endParams = new ParameterList(Collections.singletonList(Value.DATE));
        DtEnd<LocalDate> end = new DtEnd<>(endParams, LocalDate.now());
        VEvent event = new VEvent().add(uid).add(start).add(end);
        suite.addTest(new VEventTest<>("testValidation", event));

        event = event.copy();
//        start = (DtStart) event.getProperty(Property.DTSTART);
        startParams = new ParameterList(Collections.singletonList(Value.DATE_TIME));
        DtStart<ZonedDateTime> newstart = new DtStart<>(startParams, ZonedDateTime.now());
        event.replace(newstart);
        suite.addTest(new VEventTest<>("testValidationException", event));

        // test 1..
        event = event.copy();
        startParams = new ParameterList(Collections.singletonList(Value.DATE));
        newstart = new DtStart<>(startParams, event.getProperties().getRequired(Property.DTSTART).getValue());
        event.replace(newstart);
        suite.addTest(new VEventTest<>("testValidationException", event));

//        event = (VEvent) event.copy();
//        start = (DtStart) event.getProperty(Property.DTSTART);
//        start.getParameters().remove(Value.DATE_TIME);
//        end = (DtEnd) event.getProperty(Property.DTEND);
//        end.getParameters().replace(Value.DATE_TIME);
//        suite.addTest(new VEventTest<>("testValidation", event));

        // test 2..
        event = event.copy();
        startParams = new ParameterList(Collections.singletonList(Value.DATE_TIME));
        start = new DtStart<>(startParams, event.getProperties().getRequired(Property.DTSTART).getValue());
        event.replace(start);

        endParams = new ParameterList(Collections.singletonList(Value.DATE_TIME));
        end = new DtEnd<>(endParams, event.getProperties().getRequired(Property.DTEND).getValue());
        event.replace(end);
        suite.addTest(new VEventTest<>("testValidationException", event));

        // test 3..
//        event = (VEvent) event.copy();
//        start = (DtStart) event.getProperty(Property.DTSTART);
//        start.getParameters().remove(Value.DATE);
//        end = (DtEnd) event.getProperty(Property.DTEND);
//        end.getParameters().replace(Value.DATE);
//        suite.addTest(new VEventTest<>("testValidationException", event));

        // disable relaxed parsing after copying invalid events..
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);

        suite.addTest(new VEventTest<>("testChristmas"));
        suite.addTest(new VEventTest<>("testMelbourneCup"));
        suite.addTest(new VEventTest<>("testGetConsumedTime2"));
        suite.addTest(new VEventTest<>("testGetConsumedTime3"));
        suite.addTest(new VEventTest<>("testGetConsumedTimeByCount"));
        suite.addTest(new VEventTest<>("testEventEndDate"));
        suite.addTest(new VEventTest<>("testGetConsumedTimeWithExDate"));
        suite.addTest(new VEventTest<>("testGetConsumedTimeWithExDate2"));
        suite.addTest(new VEventTest<>("testIsCalendarComponent", event));
        suite.addTest(new VEventTest<>("testEquals"));
//        suite.addTest(new VEventTest<>("testValidation"));

        // use relaxed unfolding for samples..
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);

        // test iTIP validation..
//        File[] testFiles = new File("etc/samples/valid").listFiles((FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
        URL[] testFiles = new URL[]{VEventTest.class.getResource("/samples/valid/calconnect.ics"), VEventTest.class.getResource("/samples/valid/calconnect10.ics")};
        for (URL testFile : testFiles) {
            log.info("Sample [" + testFile + "]");
            net.fortuna.ical4j.model.Calendar calendar = Calendars.load(testFile);
            if (Method.PUBLISH.equals(calendar.getProperties().getRequired(Property.METHOD))) {
                calendar.getComponents().get(Component.VEVENT).forEach(calendarComponent -> {
                    suite.addTest(new VEventTest<>("testPublishValidation", (VEvent) calendarComponent));
                });
            } else if (Method.REQUEST.equals(calendar.getProperties().getRequired(Property.METHOD))) {
                calendar.getComponents().get(Component.VEVENT).forEach(calendarComponent -> {
                    suite.addTest(new VEventTest<>("testRequestValidation", (VEvent) calendarComponent));
                });
            }
        }

        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);

        return suite;
    }
}
