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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.ComponentTest;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.time.Duration;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static net.fortuna.ical4j.model.WeekDay.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.PUBLISH;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * $Id: VEventTest.java [28/09/2004]
 * <p/>
 * A test case for VEvents.
 *
 * @author Ben Fortuna
 */
public class VEventTest {

    private static final Logger log = LoggerFactory.getLogger(VEventTest.class);

    private TzId tzParam;

    @BeforeEach
    public void setUp() {
        // relax validation to avoid UID requirement..
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        TimeZoneRegistryFactory timeZoneRegistryFactory = TimeZoneRegistryFactory.getInstance();
        TimeZoneRegistry registry = timeZoneRegistryFactory.createRegistry();
        // create tzid parameter..
        tzParam = new TzId(registry.getTimeZone("Australia/Melbourne").getID());
    }

    @AfterEach
    void tearDown() {
        // relax validation to avoid UID requirement..
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);
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
            log.debug("Calendar:\n=========\n" + calendar);
        }
        return calendar;
    }

    @Test
    public final void testChristmas() {
        // create event start date..
        LocalDate christmasDay = LocalDate.now().withMonth(12).withDayOfMonth(25);

        DtStart<LocalDate> start = new DtStart<>(christmasDay).add(tzParam).add(Value.DATE);

        Summary summary = new Summary("Christmas Day; \n this is a, test\\");

        var christmas = new VEvent().withProperty(start).withProperty(summary).getFluentTarget();

        log.info(christmas.toString());
    }

    /**
     * Test creating an event with an associated timezone.
     */
    @Test
    public final void testMelbourneCup() {
        ZoneId timezone = TimeZoneRegistry.getGlobalZoneId("Australia/Melbourne");

        ZonedDateTime dt = ZonedDateTime.now(timezone).withYear(2005).withMonth(11).withDayOfMonth(1)
                .withHour(15).withMinute(0).withSecond(0);
        VEvent melbourneCup = new VEvent(dt, "Melbourne Cup");

        log.info(melbourneCup.toString());
    }

    /**
     * Test Null Dates
     * Test Start today, End One month from now.
     *
     * @throws Exception
     */
    @ParameterizedTest(name = "getConsumedTime")
    @MethodSource("getConsumedTimeData")
    public final void testGetConsumedTime(VEvent event) throws Exception {

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

        // This range is monday to friday every three weeks, starting from
        // March 7th 2005, which means for our query dates we need
        // April 18th through to the 22nd.
        List<Period<ZonedDateTime>> weeklyPeriods = event.getConsumedTime(new Period<>(queryStart, queryEnd));
        ZonedDateTime expectedStartOfFirstRange = queryStart.withDayOfMonth(1).withHour(9).withMinute(0);
        ZonedDateTime expectedEndOfFirstRange = queryStart.withDayOfMonth(1).withHour(17).withMinute(0);

        assertNotNull(weeklyPeriods);
        assertTrue(weeklyPeriods.size() > 0);
        Period firstPeriod = (Period) weeklyPeriods.toArray()[0];
        assertEquals(expectedStartOfFirstRange, firstPeriod.getStart());
        assertEquals(expectedEndOfFirstRange, firstPeriod.getEnd());
    }


    /**
     * Test whether you can select weekdays using a daily frequency.
     */
    @ParameterizedTest(name = "getConsumedTimeDaily")
    @MethodSource("getConsumedTimeDailyData")
    public final void testGetConsumedTimeDaily(VEvent event) throws Exception {

        ZonedDateTime queryStartDate = ZonedDateTime.of(2005, 4, 3,
                5, 12, 0, 0, ZoneId.systemDefault());
        ZonedDateTime queryEndDate = ZonedDateTime.of(2005, 4, 10,
                21, 55, 0, 0, ZoneId.systemDefault());

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
    }


    /**
     * Test whether you can select weekdays using a monthly frequency.
     */
    @ParameterizedTest(name = "getConsumedTimeMonthly")
    @MethodSource("getConsumedTimeMonthlyData")
    public final void testGetConsumedTimeMonthly(VEvent event) throws Exception {

        ZonedDateTime queryStartDate = ZonedDateTime.of(2005, 4, 3,
                5, 12, 0, 0, ZoneId.systemDefault());
        ZonedDateTime queryEndDate = ZonedDateTime.of(2005, 4, 17,
                21, 55, 0, 0, ZoneId.systemDefault());

        List<Period<ZonedDateTime>> monthlyPeriods = event.getConsumedTime(new Period<>(queryStartDate, queryEndDate));

        ZonedDateTime expectedStartOfFirstRange = ZonedDateTime.of(2005, 4, 4,
                9, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime expectedEndOfFirstRange = ZonedDateTime.of(2005, 4, 4,
                17, 0, 0, 0, ZoneId.systemDefault());
        assertNotNull(monthlyPeriods);
        assertTrue(monthlyPeriods.size() > 0);
        Period firstPeriod = (Period) monthlyPeriods.toArray()[0];
        assertEquals(expectedStartOfFirstRange, firstPeriod.getStart());
        assertEquals(expectedEndOfFirstRange, firstPeriod.getEnd());
    }


    @Test
    public final void testGetConsumedTime2() throws Exception {
        String resource = "/samples/valid/derryn.ics";

        net.fortuna.ical4j.model.Calendar calendar = loadCalendar(resource);

        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = start.plusWeeks(4);

        calendar.getComponents().forEach(calendarComponent -> {
            if (calendarComponent instanceof VEvent) {
                List<Period<ZonedDateTime>> consumed = ((VEvent) calendarComponent).getConsumedTime(
                        new Period<>(start, end));

                log.debug("Event [" + calendarComponent + "]");
                log.debug("Consumed time [" + consumed + "]");
            }
        });
    }

    @Test
    public final void testGetConsumedTime3() throws Exception {
        String resource = "/samples/valid/calconnect10.ics";

        net.fortuna.ical4j.model.Calendar calendar = loadCalendar(resource);

        List<VEvent> vev = calendar.getComponents(Component.VEVENT);

        LocalDate start = (LocalDate) vev.get(0).getDateTimeStart().getDate();
        LocalDate latest = LocalDate.now().plusYears(1);

        List<Period<LocalDate>> pl = vev.get(0).getConsumedTime(new Period<>(start, latest));
        assertFalse(pl.isEmpty());
    }

    /**
     * Test COUNT rules.
     */
    @Test
    public void testGetConsumedTimeByCount() {
        Recur<?> recur = new Recur.Builder<>().frequency(Frequency.WEEKLY).count(3)
            .interval(1).dayList(SU).build();
        log.info(recur.toString());

        ZonedDateTime start = ZonedDateTime.now().withDayOfMonth(8);
        ZonedDateTime end = start.plusHours(1);

        RRule<?> rrule = new RRule<>(recur);
        var event = new VEvent(start, end, "Test recurrence COUNT").add(rrule);
        log.info(event.toString());

        ZonedDateTime rangeStart = ZonedDateTime.now();
        ZonedDateTime rangeEnd = rangeStart.plusWeeks(4);

        log.info(((VEvent) event).getConsumedTime(new Period<Temporal>(rangeStart, rangeEnd)).toString());
    }

    /**
     * A test to confirm that the end date is calculated correctly
     * from a given start date and duration.
     */
    @Test
    public final void testEventEndDate() {
        LocalDate startDate = LocalDate.now();
        log.info("Start date: " + startDate);
        VEvent event = new VEvent(startDate, java.time.Period.ofDays(3), "3 day event");
        Temporal endDate = event.getEndDate().orElseThrow().getDate();
        log.info("End date: " + endDate);
        assertEquals(startDate.plusDays(3), endDate);
    }

    /**
     * Test to ensure that EXDATE properties are correctly applied.
     */
    @Test
    public void testGetConsumedTimeWithExDate() throws ParseException {

        var event1 = (VEvent) new VEvent(TemporalAdapter.parse("20050103T080000").getTemporal(),
                java.time.Duration.ofMinutes(15), "Event 1")
                .withProperty(new RRule<>(new Recur<>("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR")))
                .withProperty(new ExDate<LocalDateTime>("20050106T000000")
                        .withParameter(Value.DATE).getFluentTarget())
                .getFluentTarget();

        TemporalAdapter<LocalDateTime> start = TemporalAdapter.parse("20050106T000000");
        TemporalAdapter<LocalDateTime> end = TemporalAdapter.parse("20050107T000000");
        List<Period<Temporal>> list = event1.getConsumedTime(new Period<>(start.getTemporal(), end.getTemporal()));
        assertEquals(list, Collections.singletonList(new Period<>(
                LocalDateTime.of(2005, 1, 6, 8, 0), Duration.ofMinutes(15))));
    }

    /**
     * Test to ensure that EXDATE properties are correctly applied.
     */
    @Test
    public void testGetConsumedTimeWithExDate2() throws IOException, ParserException, ConstraintViolationException {
        InputStream in = getClass().getResourceAsStream("/samples/valid/friday13.ics");
        net.fortuna.ical4j.model.Calendar calendar = new CalendarBuilder().build(in);

        List<VEvent> event = calendar.getComponents(Component.VEVENT);

        ZonedDateTime start = ZonedDateTime.now().withYear(1997).withMonth(8).withDayOfMonth(2);
        ZonedDateTime end = start.withDayOfMonth(4);

        List<Period<ZonedDateTime>> periods = event.get(0).getConsumedTime(new Period<>(start, end));
        assertTrue(periods.isEmpty());
    }

    /**
     * Test equality of events with different alarm sub-components.
     */
    @Test
    public void testEquals() {
        String summary = "test event";
        PropertyList props = new PropertyList(Arrays.asList(new DtStart<>(Instant.now()), new Summary(summary)));

        VEvent e1 = new VEvent(props);
        VEvent e2 = new VEvent(props);

        assertEquals(e1, e2);

        e2.add(new VAlarm());

        assertNotEquals(e1, e2);
    }

    @ParameterizedTest(name = "calculateRecurrenceSetNotEmpty")
    @MethodSource("calculateRecurrenceSetNotEmptyData")
    public <T extends Temporal> void testCalculateRecurrenceSetNotEmpty(VEvent event, Period<T> period) {
        Set<Period<T>> recurrenceSet = event.calculateRecurrenceSet(period);
        assertFalse(recurrenceSet.isEmpty());
    }

    /**
     * Unit tests for {@link VEvent#getOccurrence(Temporal)}.
     */
    @ParameterizedTest(name = "getOccurrence")
    @MethodSource("getOccurrenceData")
    public <T extends Temporal> void testGetOccurrence(VEvent event, T date) throws IOException, ParseException, URISyntaxException {
        VEvent occurrence = event.getOccurrence(date);
        assertNotNull(occurrence);
        assertEquals(event.getUid(), occurrence.getUid());
    }

    @ParameterizedTest(name = "validation")
    @MethodSource("validationData")
    public void testValidation(VEvent component) throws ValidationException {
        ComponentTest.assertValidation(component);
    }

    @ParameterizedTest(name = "validationException")
    @MethodSource("validationExceptionData")
    public void testValidationException(VEvent component) {
        ComponentTest.assertValidationException(component);
    }

    @ParameterizedTest(name = "isCalendarComponent")
    @MethodSource("isCalendarComponentData")
    public void testIsCalendarComponent(VEvent component) {
        ComponentTest.assertIsCalendarComponent(component);
    }

    @ParameterizedTest(name = "publishValidation")
    @MethodSource("publishValidationData")
    public void testPublishValidation(VEvent component) throws ValidationException {
        CalendarComponentTest.assertPublishValidation(component);
    }

    @ParameterizedTest(name = "requestValidation")
    @MethodSource("requestValidationData")
    public void testRequestValidation(VEvent component) throws ValidationException {
        CalendarComponentTest.assertRequestValidation(component);
    }

    // ---------- @MethodSource providers ----------

    private static VEvent weekdayNineToFive(Frequency frequency, UidGenerator uidGenerator,
                                            ZonedDateTime weekday9AM, ZonedDateTime weekday5PM,
                                            ZonedDateTime until, String summary) throws ValidationException {
        Recur<ZonedDateTime> recur = new Recur.Builder<ZonedDateTime>().frequency(frequency).until(until)
                .dayList(MO, TU, WE, TH, FR)
                .interval(1).weekStartDay(MO).build();
        RRule<ZonedDateTime> rrule = new RRule<>(recur);

        ParameterList tzParams = new ParameterList(Collections.singletonList(new TzId("Australia/Melbourne")));

        var event = (VEvent) new VEvent().withProperty(rrule)
                .withProperty(new Summary(summary))
                .withProperty(new DtStart<>(tzParams, weekday9AM))
                .withProperty(new DtEnd<>(tzParams, weekday5PM))
                .withProperty(uidGenerator.generateUid()).getFluentTarget();
        // ensure event is valid..
        event.validate();
        return event;
    }

    private static ZonedDateTime weekday9AM() {
        return ZonedDateTime.now().withYear(2005).withMonth(3).withDayOfMonth(7)
                .withHour(9).withMinute(0).withSecond(0).withNano(0);
    }

    private static ZonedDateTime weekday5PM() {
        return ZonedDateTime.now().withYear(2005).withMonth(3).withDayOfMonth(7)
                .withHour(17).withMinute(0).withSecond(0).withNano(0);
    }

    private static ZonedDateTime untilDate() {
        return ZonedDateTime.now().withYear(2005).withMonth(12).withDayOfMonth(31);
    }

    static Stream<Arguments> calculateRecurrenceSetNotEmptyData() throws ValidationException, ParseException {
        UidGenerator uidGenerator = new RandomUidGenerator();
        VEvent weekdayNineToFiveEvents = weekdayNineToFive(Frequency.WEEKLY, uidGenerator,
                weekday9AM(), weekday5PM(), untilDate(),
                "TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED WEEKLY");

        ZonedDateTime periodStart = TemporalAdapter.parse("20050101T000000",
                ZoneId.systemDefault()).getTemporal();
        ZonedDateTime periodEnd = TemporalAdapter.parse("20051231T235959",
                ZoneId.systemDefault()).getTemporal();
        Period<ZonedDateTime> period = new Period<>(periodStart, periodEnd);
        return Stream.of(Arguments.of(weekdayNineToFiveEvents, period));
    }

    static Stream<Arguments> getOccurrenceData() throws ValidationException {
        UidGenerator uidGenerator = new RandomUidGenerator();
        VEvent weekdayNineToFiveEvents = weekdayNineToFive(Frequency.WEEKLY, uidGenerator,
                weekday9AM(), weekday5PM(), untilDate(),
                "TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED WEEKLY");
        return Stream.of(Arguments.of(weekdayNineToFiveEvents,
                weekdayNineToFiveEvents.getDateTimeStart().getDate()));
    }

    static Stream<Arguments> getConsumedTimeData() throws ValidationException {
        UidGenerator uidGenerator = new RandomUidGenerator();
        VEvent weekdayNineToFiveEvents = weekdayNineToFive(Frequency.WEEKLY, uidGenerator,
                weekday9AM(), weekday5PM(), untilDate(),
                "TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED WEEKLY");
        return Stream.of(Arguments.of(weekdayNineToFiveEvents));
    }

    static Stream<Arguments> getConsumedTimeDailyData() throws ValidationException {
        UidGenerator uidGenerator = new RandomUidGenerator();
        VEvent dailyWeekdayEvents = weekdayNineToFive(Frequency.DAILY, uidGenerator,
                weekday9AM(), weekday5PM(), untilDate(),
                "TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED DAILY");
        return Stream.of(Arguments.of(dailyWeekdayEvents));
    }

    static Stream<Arguments> getConsumedTimeMonthlyData() throws ValidationException {
        UidGenerator uidGenerator = new RandomUidGenerator();
        VEvent monthlyWeekdayEvents = weekdayNineToFive(Frequency.MONTHLY, uidGenerator,
                weekday9AM(), weekday5PM(), untilDate(),
                "TEST EVENTS THAT HAPPEN 9-5 MON-FRI DEFINED MONTHLY");
        return Stream.of(Arguments.of(monthlyWeekdayEvents));
    }

    /**
     * Builds the validation/validationException/isCalendarComponent test data which all use
     * the same chain of {@code event.copy()} mutations from the original {@code suite()}.
     */
    private static class ValidationFixture {
        final VEvent validEvent;
        final VEvent invalidEvent;
        final VEvent finalEvent;

        ValidationFixture() {
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
            try {
                UidGenerator ug = new RandomUidGenerator();
                Uid uid = ug.generateUid();

                ParameterList startParams = new ParameterList(Collections.singletonList(Value.DATE));
                DtStart<LocalDate> start = new DtStart<>(startParams, LocalDate.now());

                ParameterList endParams = new ParameterList(Collections.singletonList(Value.DATE));
                DtEnd<LocalDate> end = new DtEnd<>(endParams, LocalDate.now());
                var event = (VEvent) new VEvent().withProperty(uid).withProperty(start).withProperty(end).getFluentTarget();
                this.validEvent = event;

                event = event.copy();
                ParameterList startParams2 = new ParameterList(Collections.singletonList(Value.DATE_TIME));
                DtStart<ZonedDateTime> newstart = new DtStart<>(startParams2, ZonedDateTime.now());
                event.replace(newstart);
                this.invalidEvent = event;

                // test 1..
                event = event.copy();
                ParameterList startParams3 = new ParameterList(Collections.singletonList(Value.DATE));
                DtStart<?> newstart3 = new DtStart<>(startParams3, event.getRequiredProperty(Property.DTSTART).getValue());
                event.replace(newstart3);

                // test 2..
                event = event.copy();
                ParameterList startParams4 = new ParameterList(Collections.singletonList(Value.DATE_TIME));
                DtStart<?> startA = new DtStart<>(startParams4, event.getRequiredProperty(Property.DTSTART).getValue());
                event.replace(startA);

                ParameterList endParams2 = new ParameterList(Collections.singletonList(Value.DATE_TIME));
                DtEnd<?> endA = new DtEnd<>(endParams2, event.getRequiredProperty(Property.DTEND).getValue());
                event.replace(endA);
                this.finalEvent = event;
            } finally {
                CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
            }
        }
    }

    static Stream<Arguments> validationData() {
        ValidationFixture fx = new ValidationFixture();
        return Stream.of(Arguments.of(fx.validEvent));
    }

    static Stream<Arguments> validationExceptionData() {
        ValidationFixture fx = new ValidationFixture();
        return Stream.of(Arguments.of(fx.invalidEvent));
    }

    static Stream<Arguments> isCalendarComponentData() {
        ValidationFixture fx = new ValidationFixture();
        return Stream.of(Arguments.of(fx.finalEvent));
    }

    private static Stream<Arguments> iTipValidationData(String method) throws IOException, ParserException {
        Stream.Builder<Arguments> rows = Stream.builder();
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
        try {
            URL[] testFiles = new URL[]{VEventTest.class.getResource("/samples/valid/calconnect.ics"),
                    VEventTest.class.getResource("/samples/valid/calconnect10.ics")};
            for (URL testFile : testFiles) {
                log.info("Sample [" + testFile + "]");
                net.fortuna.ical4j.model.Calendar calendar = Calendars.load(testFile);
                if ("PUBLISH".equals(method) && PUBLISH.equals(calendar.getRequiredProperty(Property.METHOD))) {
                    calendar.<VEvent>getComponents(Component.VEVENT).forEach(
                            calendarComponent -> rows.add(Arguments.of(calendarComponent)));
                } else if ("REQUEST".equals(method) && REQUEST.equals(calendar.getRequiredProperty(Property.METHOD))) {
                    calendar.<VEvent>getComponents(Component.VEVENT).forEach(
                            calendarComponent -> rows.add(Arguments.of(calendarComponent)));
                }
            }
        } finally {
            CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
            CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);
        }
        return rows.build();
    }

    static Stream<Arguments> publishValidationData() throws IOException, ParserException {
        return iTipValidationData("PUBLISH");
    }

    static Stream<Arguments> requestValidationData() throws IOException, ParserException {
        return iTipValidationData("REQUEST");
    }
}
