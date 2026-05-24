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
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.ComponentTest;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.transform.recurrence.Frequency;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static net.fortuna.ical4j.model.Property.FREEBUSY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created on 10/02/2005
 *
 * @author Ben Fortuna
 */
@Disabled("Failed after re-enabling JUnit 3/4 tests")
public class VFreeBusyTest {

    private static final Logger log = LoggerFactory.getLogger(VFreeBusyTest.class);

    private TzId tzParam;

    @BeforeEach
    void setUp() {
        // relax validation to avoid UID requirement..
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        TimeZoneRegistryFactory timeZoneRegistryFactory = TimeZoneRegistryFactory.getInstance();
        TimeZoneRegistry registry = timeZoneRegistryFactory.createRegistry();
        // create tzid parameter..
        tzParam = new TzId(registry.getTimeZone("Australia/Melbourne").getID());
    }

    @AfterEach
    void tearDown() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }

    /*
     * Class under test for void VFreeBusy(ComponentList)
     */
    @Test
    public final void testVFreeBusyComponentList() {
        ZonedDateTime startDate = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(0), ZoneId.systemDefault());
        ZonedDateTime endDate = ZonedDateTime.now();

        ParameterList tzParams = new ParameterList(Collections.singletonList(new TzId(startDate.getZone().getId())));
        var event = (VEvent) new VEvent().withProperty(new DtStart<>(tzParams, startDate))
                .withProperty(new Duration(java.time.Duration.ofHours(1))).getFluentTarget();

        var event2 = (VEvent) new VEvent().withProperty(new DtStart<>(tzParams, startDate))
                .withProperty(new DtEnd<>(endDate)).getFluentTarget();

        VFreeBusy request = new VFreeBusy(startDate.toInstant(), endDate.toInstant());

        ComponentList<CalendarComponent> components = new ComponentList<>(Arrays.asList(event, event2));
        VFreeBusy fb = new VFreeBusy(request, components.getAll());

        if (log.isDebugEnabled()) {
            log.debug("\n==\n" + fb);
        }
    }

    /*
     * Class under test for void VFreeBusy(ComponentList)
     */
    @Test
    public final void testVFreeBusyComponentList2() throws Exception {
        InputStream in = getClass().getResourceAsStream("/samples/invalid/core.ics");

        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(in);

        Instant startDate = Instant.ofEpochMilli(0);
        Instant endDate = Instant.now();

        // request all busy time between 1970 and now..
        VFreeBusy requestBusy = new VFreeBusy(startDate, endDate);

        VFreeBusy fb = new VFreeBusy(requestBusy, calendar.getComponents());

        if (log.isDebugEnabled()) {
            log.debug("\n==\n" + fb);
        }

        // request all free time between 1970 and now of duration 2 hours or
        // more..
        VFreeBusy requestFree = new VFreeBusy(startDate, endDate,
                java.time.Duration.ofHours(2));

        VFreeBusy fb2 = new VFreeBusy(requestFree, calendar.getComponents());

        if (log.isDebugEnabled()) {
            log.debug("\n==\n" + fb2);
        }
    }

    @Test
    public final void testVFreeBusyComponentList3() throws ConstraintViolationException {
        ZonedDateTime eventStart = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());

        VEvent event = new VEvent(eventStart, java.time.Duration.ofHours(1), "Progress Meeting");
        // add timezone information..
        event.getRequiredProperty(Property.DTSTART).add(tzParam);

        // add recurrence..
        Recur<ZonedDateTime> recur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.YEARLY).count(20)
                .monthList(new MonthList("1")).monthDayList(26)
                .hourList(9).minuteList(30).build();
        event.add(new RRule<>(recur));

        if (log.isDebugEnabled()) {
            log.debug("\n==\n" + event);
        }

        VFreeBusy request = new VFreeBusy(eventStart.toInstant(), Instant.now());

        ComponentList<CalendarComponent> components = new ComponentList<>(Collections.singletonList(event));
        VFreeBusy fb = new VFreeBusy(request, components.getAll());

        if (log.isDebugEnabled()) {
            log.debug("\n==\n" + fb);
        }
    }

    @Test
    public final void testVFreeBusyComponentList4() {
        Instant startDate = Instant.now();
        Instant endDate = ZonedDateTime.now().plusDays(3).toInstant();

        var event = (VEvent) new VEvent().withProperty(new DtStart<>(startDate))
                .withProperty(new Duration(java.time.Duration.ofHours(1))).getFluentTarget();

        var event2 = (VEvent) new VEvent().withProperty(new DtStart<>(startDate)).
                withProperty(new DtEnd<>(endDate)).getFluentTarget();

        VFreeBusy request = new VFreeBusy(startDate, endDate, java.time.Duration.ofHours(1));

        ComponentList<CalendarComponent> components = new ComponentList<>(Arrays.asList(event, event2));
        VFreeBusy fb = new VFreeBusy(request, components.getAll());

        log.debug("\n==\n" + fb);
    }

    @Test
    public final void testAngelites() {
        log.info("angelites test:\n================");

        // add an event
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = start.plusDays(1);

        Calendar freeBusyTest = new Calendar().withComponent(new VEvent(start, end, "DATE END INCLUDED"))
                .withComponent(new VEvent(start, java.time.Period.ofDays(1), "DURATION")).getFluentTarget();

        Instant dtstart = Instant.now();
        Instant dtend = ZonedDateTime.now().plusDays(2).toInstant();

        VFreeBusy getBusy = new VFreeBusy(dtstart, dtend);
        VFreeBusy requestFree = new VFreeBusy(dtstart, dtend, java.time.Period.ofDays(30));

        log.debug("GET BUSY: \n" + getBusy);
        log.debug("REQUEST FREE: \n" + requestFree);

        Calendar FreeBusyTest2 = new Calendar();

        VFreeBusy replyBusy = new VFreeBusy(getBusy, freeBusyTest.getComponents());
        VFreeBusy replyFree = new VFreeBusy(requestFree, freeBusyTest.getComponents());

        log.debug("REPLY BUSY: \n" + replyBusy);
        log.debug("REPLY FREE: \n" + replyFree);

        FreeBusyTest2.add(replyBusy);
        VFreeBusy replyBusy2 = new VFreeBusy(getBusy, FreeBusyTest2.getComponents());
        VFreeBusy replyFree2 = new VFreeBusy(requestFree, FreeBusyTest2.getComponents());

        log.debug("REPLY BUSY2: \n" + replyBusy2);
        log.debug("REPLY FREE2: \n" + replyFree2);
    }

    @ParameterizedTest(name = "isCalendarComponent")
    @MethodSource("isCalendarComponentData")
    public void testIsCalendarComponent(VFreeBusy component) {
        ComponentTest.assertIsCalendarComponent(component);
    }

    @ParameterizedTest(name = "validation")
    @MethodSource("validationData")
    public void testValidation(VFreeBusy component) throws ValidationException {
        ComponentTest.assertValidation(component);
    }

    @ParameterizedTest(name = "publishValidationException")
    @MethodSource("publishValidationExceptionData")
    public void testPublishValidationException(VFreeBusy component) {
        CalendarComponentTest.assertPublishValidationException(component);
    }

    @ParameterizedTest(name = "publishValidation")
    @MethodSource("publishValidationData")
    public void testPublishValidation(VFreeBusy component) throws ValidationException {
        CalendarComponentTest.assertPublishValidation(component);
    }

    @ParameterizedTest(name = "replyValidationException")
    @MethodSource("replyValidationExceptionData")
    public void testReplyValidationException(VFreeBusy component) {
        CalendarComponentTest.assertReplyValidationException(component);
    }

    @ParameterizedTest(name = "replyValidation")
    @MethodSource("replyValidationData")
    public void testReplyValidation(VFreeBusy component) throws ValidationException {
        CalendarComponentTest.assertReplyValidation(component);
    }

    @ParameterizedTest(name = "fbType")
    @MethodSource("fbTypeData")
    public void testFbType(VFreeBusy request, ComponentList<CalendarComponent> components, FbType expectedFbType) {
        VFreeBusy result = new VFreeBusy(request, components.getAll());
        Optional<FreeBusy> fb = result.getProperty(FREEBUSY);
        assertTrue(fb.isPresent()
                && fb.get().getParameter(Parameter.FBTYPE).equals(Optional.of(expectedFbType)));
    }

    @ParameterizedTest(name = "periodCount [{2}]")
    @MethodSource("periodCountData")
    public void testPeriodCount(VFreeBusy request, ComponentList<CalendarComponent> components, int expectedIntervalCount) {
        VFreeBusy result = new VFreeBusy(request, components.getAll());
        Optional<FreeBusy> fb = result.getProperty(FREEBUSY);
        if (expectedIntervalCount > 0) {
            assertTrue(fb.isPresent());
            assertEquals(expectedIntervalCount, fb.get().getIntervals().size());
        } else {
            assertFalse(fb.isPresent());
        }
    }

    @ParameterizedTest(name = "freeBusyPeriods")
    @MethodSource("freeBusyPeriodsData")
    public void testFreeBusyPeriods(VFreeBusy request, ComponentList<CalendarComponent> components,
                                    List<Interval> expectedIntervals) {
        VFreeBusy result = new VFreeBusy(request, components.getAll());
        Optional<FreeBusy> fb = result.getProperty(FREEBUSY);
        assertTrue(fb.isPresent());
        assertEquals(expectedIntervals, fb.get().getIntervals());
    }

    static Stream<Arguments> isCalendarComponentData() {
        return Stream.of(Arguments.of(new VFreeBusy()));
    }

    static Stream<Arguments> validationData() {
        return Stream.of(Arguments.of(new VFreeBusy()));
    }

    static Stream<Arguments> publishValidationExceptionData() {
        return Stream.of(Arguments.of(new VFreeBusy()));
    }

    static Stream<Arguments> publishValidationData() {
        var publishFb = (VFreeBusy) new VFreeBusy().withProperty(new DtStart<Instant>("20091212T000000Z"))
                .withProperty(new DtEnd<Instant>("20091212T235959Z"))
                .withProperty(new FreeBusy("20091212T140000Z/PT3H"))
                .withProperty(new Organizer("mailto:joe@example.com"))
                .withProperty(new Uid("12")).getFluentTarget();
        return Stream.of(Arguments.of(publishFb));
    }

    static Stream<Arguments> replyValidationExceptionData() throws URISyntaxException {
        var replyFb = (VFreeBusy) new VFreeBusy().withProperty(new DtStart<Instant>("20091212T000000Z"))
                .withProperty(new DtEnd<Instant>("20091212T235959Z"))
                .withProperty(new Organizer("mailto:joe@example.com"))
                .withProperty(new Attendee("mailto:joe@example.com"))
                .withProperty(new Uid("12")).getFluentTarget();
        var invalDurFb = (VFreeBusy) replyFb.copy().withProperty(new Duration(java.time.Duration.parse("PT1H")))
                .getFluentTarget();
        var invalSeqFb = (VFreeBusy) replyFb.copy().withProperty(new Sequence("12")).getFluentTarget();
        return Stream.of(
                Arguments.of(new VFreeBusy()),
                Arguments.of(invalDurFb),
                Arguments.of(invalSeqFb)
        );
    }

    static Stream<Arguments> replyValidationData() throws URISyntaxException {
        var replyFb = (VFreeBusy) new VFreeBusy().withProperty(new DtStart<Instant>("20091212T000000Z"))
                .withProperty(new DtEnd<Instant>("20091212T235959Z"))
                .withProperty(new Organizer("mailto:joe@example.com"))
                .withProperty(new Attendee("mailto:joe@example.com"))
                .withProperty(new Uid("12")).getFluentTarget();
        return Stream.of(Arguments.of(replyFb));
    }

    static Stream<Arguments> fbTypeData() throws ParseException {
        Stream.Builder<Arguments> rows = Stream.builder();

        VEvent event1 = new VEvent(TemporalAdapter.parse("20050101T080000").getTemporal(),
                java.time.Duration.ofMinutes(15), "Consultation 1");
        ComponentList<CalendarComponent> components = new ComponentList<>(Collections.singletonList(event1));

        Instant start = (Instant) TemporalAdapter.parse("20050103T000000Z").getTemporal();
        Instant end = (Instant) TemporalAdapter.parse("20050104T000000Z").getTemporal();

        VFreeBusy requestFree = new VFreeBusy(start, end, java.time.Duration.ofMinutes(5));
        // free/busy type should be FREE..
        rows.add(Arguments.of(requestFree, components, FbType.FREE));

        // anniversary-style events don't consume time..
        VEvent event2 = new VEvent(TemporalAdapter.parse("20081225").getTemporal(), "Christmas 2008");
        ComponentList<CalendarComponent> components2 = new ComponentList<>(Collections.singletonList(event2));

        Instant start2 = (Instant) TemporalAdapter.parse("20081225T110000Z").getTemporal();
        Instant end2 = (Instant) TemporalAdapter.parse("20081225T113000Z").getTemporal();

        VFreeBusy request2 = new VFreeBusy(start2, end2, java.time.Duration.ofMinutes(15));
        rows.add(Arguments.of(request2, components2, FbType.FREE));

        return rows.build();
    }

    static Stream<Arguments> periodCountData() throws ParseException {
        Stream.Builder<Arguments> rows = Stream.builder();

        // request 1 — single period in range
        VEvent event1 = new VEvent(TemporalAdapter.parse("20050101T080000").getTemporal(),
                java.time.Duration.ofMinutes(15), "Consultation 1");
        ComponentList<CalendarComponent> components = new ComponentList<>(Collections.singletonList(event1));

        Instant start = (Instant) TemporalAdapter.parse("20050103T000000Z").getTemporal();
        Instant end = (Instant) TemporalAdapter.parse("20050104T000000Z").getTemporal();

        VFreeBusy requestFree = new VFreeBusy(start, end, java.time.Duration.ofMinutes(5));
        rows.add(Arguments.of(requestFree, components, 1));

        // testBusyTime..
        var event12 = (VEvent) new VEvent(TemporalAdapter.parse("20050103T080000", ZoneId.systemDefault()).getTemporal(),
                java.time.Duration.ofHours(5), "Event 1")
                .withProperty(new RRule<>(new Recur<>("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR")))
                .getFluentTarget();
        ComponentList<CalendarComponent> components2 = new ComponentList<>(Collections.singletonList(event12));

        ZonedDateTime startZoned = TemporalAdapter.parse("20050104T110000", ZoneId.systemDefault()).getTemporal();
        Period<ZonedDateTime> period = new Period<>(startZoned, java.time.Duration.ofMinutes(30));

        VFreeBusy request2 = new VFreeBusy(period.getStart().toInstant(), period.getEnd().toInstant());
        rows.add(Arguments.of(request2, components2, 1));

        // request with zero duration — no periods
        VFreeBusy request3 = new VFreeBusy(period.getStart().toInstant(), period.getEnd().toInstant(),
                java.time.Period.ZERO);
        rows.add(Arguments.of(request3, components2, 0));

        // anniversary-style events don't consume time..
        VEvent event3 = new VEvent(TemporalAdapter.parse("20081225").getTemporal(), "Christmas 2008");
        ComponentList<CalendarComponent> components3 = new ComponentList<>(Collections.singletonList(event3));

        Instant start3 = (Instant) TemporalAdapter.parse("20081225T110000Z").getTemporal();
        Instant end3 = (Instant) TemporalAdapter.parse("20081225T113000Z").getTemporal();

        VFreeBusy request4 = new VFreeBusy(start3, end3);
        rows.add(Arguments.of(request4, components3, 0));

        VFreeBusy request5 = new VFreeBusy(start3, end3, java.time.Duration.ofMinutes(15));
        rows.add(Arguments.of(request5, components3, 1));

        // some components are not in range
        ZoneId zoneId = TimeZoneRegistry.getGlobalZoneId("America/Los_Angeles");
        Parameter tzP = new TzId(zoneId.getId());
        ParameterList pl = new ParameterList(Collections.singletonList(tzP));
        DtStart<Temporal> dts = new DtStart<>(pl, TemporalAdapter.parse("20130124T020000").getTemporal());
        var e = (VEvent) new VEvent().withProperty(dts).withProperty(new Duration(java.time.Duration.parse("PT1H")))
                .withProperty(new RRule<>("FREQ=DAILY")).getFluentTarget();
        ComponentList<CalendarComponent> components4 = new ComponentList<>(Collections.singletonList(e));

        VFreeBusy request6 = new VFreeBusy(TemporalAdapter.parse("20130124T110000Z").getTemporal(),
                TemporalAdapter.parse("20130125T110000Z").getTemporal());
        rows.add(Arguments.of(request6, components4, 1));

        return rows.build();
    }

    static Stream<Arguments> freeBusyPeriodsData() throws ParseException {
        Stream.Builder<Arguments> rows = Stream.builder();

        VEvent event1 = new VEvent(TemporalAdapter.parse("20050101T080000").getTemporal(),
                java.time.Duration.ofMinutes(15), "Consultation 1");
        ComponentList<CalendarComponent> components = new ComponentList<>(Collections.singletonList(event1));

        Instant start = (Instant) TemporalAdapter.parse("20050103T000000Z").getTemporal();
        Instant end = (Instant) TemporalAdapter.parse("20050104T000000Z").getTemporal();

        VFreeBusy requestFree = new VFreeBusy(start, end, java.time.Duration.ofMinutes(5));
        // period should be from the start to the end date..
        List<Interval> periods = Collections.singletonList(Interval.of(start, end));
        rows.add(Arguments.of(requestFree, components, periods));

        // testBusyTime..
        var event12 = (VEvent) new VEvent(TemporalAdapter.parse("20050103T080000", ZoneId.systemDefault()).getTemporal(),
                java.time.Duration.ofHours(5), "Event 1")
                .withProperty(new RRule<>(new Recur<>("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR")))
                .getFluentTarget();
        ComponentList<CalendarComponent> components2 = new ComponentList<>(Collections.singletonList(event12));

        ZonedDateTime startZoned = TemporalAdapter.parse("20050104T110000", ZoneId.systemDefault()).getTemporal();
        Period<ZonedDateTime> period = new Period<>(startZoned, java.time.Duration.ofMinutes(30));

        VFreeBusy request2 = new VFreeBusy(period.getStart().toInstant(), period.getEnd().toInstant());
        List<Interval> periods2 = Collections.singletonList(Interval.of((Instant) TemporalAdapter.parse("20050104T080000Z").getTemporal(),
                java.time.Duration.parse("PT5H")));
        rows.add(Arguments.of(request2, components2, periods2));

        // anniversary-style events don't consume time..
        VEvent event2 = new VEvent(TemporalAdapter.parse("20081225").getTemporal(), "Christmas 2008");
        ComponentList<CalendarComponent> components3 = new ComponentList<>(Collections.singletonList(event2));

        Instant start3 = (Instant) TemporalAdapter.parse("20081225T110000Z").getTemporal();
        Instant end3 = (Instant) TemporalAdapter.parse("20081225T113000Z").getTemporal();

        VFreeBusy request3 = new VFreeBusy(start3, end3, java.time.Duration.ofMinutes(15));
        List<Interval> periods3 = Collections.singletonList(Interval.of(start3, java.time.Duration.ofMinutes(30)));
        rows.add(Arguments.of(request3, components3, periods3));

        return rows.build();
    }
}
