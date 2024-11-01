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
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.transform.recurrence.Frequency;
import net.fortuna.ical4j.util.CompatibilityHints;
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

/**
 * Created on 10/02/2005
 *
 * @author Ben Fortuna
 */
public class VFreeBusyTest<T extends Temporal> extends CalendarComponentTest<T> {

    private static final Logger log = LoggerFactory.getLogger(VFreeBusyTest.class);

    private TimeZoneRegistry registry;

    private VTimeZone tz;

    private TzId tzParam;

    private ComponentList<CalendarComponent> components;

    private VFreeBusy request;

    private FbType expectedFbType;

    private int expectedIntervalCount;

    private List<Interval> expectedIntervals;

    /**
     * @param testMethod
     */
    public VFreeBusyTest(String testMethod) {
        this(testMethod, null);
    }

    /**
     * @param testMethod
     * @param component
     */
    public VFreeBusyTest(String testMethod, VFreeBusy component) {
        super(testMethod, component);
    }

    /**
     * @param testMethod
     * @param component
     * @param expectedIntervalCount
     */
    public VFreeBusyTest(String testMethod, VFreeBusy component, ComponentList<CalendarComponent> components, int expectedIntervalCount) {
        super(testMethod, component);
        this.request = component;
        this.components = components;
        this.expectedIntervalCount = expectedIntervalCount;
    }

    /**
     * @param testMethod
     * @param component
     * @param components
     * @param expectedFbType
     */
    public VFreeBusyTest(String testMethod, VFreeBusy component, ComponentList<CalendarComponent> components, FbType expectedFbType) {
        super(testMethod, component);
        this.request = component;
        this.components = components;
        this.expectedFbType = expectedFbType;
    }

    /**
     * @param testMethod
     * @param component
     * @param components
     * @param expectedIntervals
     */
    public VFreeBusyTest(String testMethod, VFreeBusy component, ComponentList<CalendarComponent> components,
                         List<Interval> expectedIntervals) {

        super(testMethod, component);
        this.request = component;
        this.components = components;
        this.expectedIntervals = expectedIntervals;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // relax validation to avoid UID requirement..
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        TimeZoneRegistryFactory timeZoneRegistryFactory = TimeZoneRegistryFactory.getInstance();
        registry = timeZoneRegistryFactory.createRegistry();
        // create tzid parameter..
        tzParam = new TzId(registry.getTimeZone("Australia/Melbourne").getID());
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
        super.tearDown();
    }

    /*
     * Class under test for void VFreeBusy(ComponentList)
     */
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

    public final void testVFreeBusyComponentList3() throws ConstraintViolationException {
        ZonedDateTime eventStart = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());

        VEvent event = new VEvent(eventStart, java.time.Duration.ofHours(1), "Progress Meeting");
        // VEvent event = new VEvent(startDate, cal.getTime(), "Progress
        // Meeting");
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

    /**
     *
     */
    public void testFbType() {
        VFreeBusy result = new VFreeBusy(request, components.getAll());
        Optional<FreeBusy> fb = result.getFreeBusyTime();
        assertTrue(fb.isPresent()
                && fb.get().getParameter(Parameter.FBTYPE).equals(Optional.of(expectedFbType)));
    }

    /**
     *
     */
//    @Ignore("Recurrences with FREQ=WEEKLY not compatible with Instant temporal type currently")
    public void testPeriodCount() {
        VFreeBusy result = new VFreeBusy(request, components.getAll());
        Optional<FreeBusy> fb = result.getFreeBusyTime();
        if (expectedIntervalCount > 0) {
            assertTrue(fb.isPresent());
            assertEquals(expectedIntervalCount, fb.get().getIntervals().size());
        } else {
            assertFalse(fb.isPresent());
        }
    }

    /**
     *
     */
//    @Ignore("Recurrences with FREQ=WEEKLY not compatible with Instant temporal type currently")
    public void testFreeBusyPeriods() {
        VFreeBusy result = new VFreeBusy(request, components.getAll());
        Optional<FreeBusy> fb = result.getFreeBusyTime();
        assertTrue(fb.isPresent());
        assertEquals(expectedIntervals, fb.get().getIntervals());
    }

    /**
     * @return
     */
    public static TestSuite suite() throws ParseException, URISyntaxException {
        TestSuite suite = new TestSuite();

        suite.addTest(new VFreeBusyTest<>("testVFreeBusyComponentList"));
        suite.addTest(new VFreeBusyTest<>("testVFreeBusyComponentList2"));
        suite.addTest(new VFreeBusyTest<>("testVFreeBusyComponentList3"));
        suite.addTest(new VFreeBusyTest<>("testVFreeBusyComponentList4"));
        suite.addTest(new VFreeBusyTest<>("testAngelites"));

        // icalendar validation
        VFreeBusy fb = new VFreeBusy();
        suite.addTest(new VFreeBusyTest<>("testIsCalendarComponent", fb));
        suite.addTest(new VFreeBusyTest<>("testValidation", fb));

        // iTIP PUBLISH validation
        suite.addTest(new VFreeBusyTest<>("testPublishValidationException", new VFreeBusy()));
        var publishFb = (VFreeBusy) new VFreeBusy().withProperty(new DtStart<Instant>("20091212T000000Z"))
                .withProperty(new DtEnd<Instant>("20091212T235959Z"))
                .withProperty(new FreeBusy("20091212T140000Z/PT3H"))
                .withProperty(new Organizer("mailto:joe@example.com"))
                .withProperty(new Uid("12")).getFluentTarget();
        suite.addTest(new VFreeBusyTest<>("testPublishValidation", publishFb));

        // iTIP REPLY validation
        suite.addTest(new VFreeBusyTest<>("testReplyValidationException", new VFreeBusy()));
        var replyFb = (VFreeBusy) new VFreeBusy().withProperty(new DtStart<Instant>("20091212T000000Z"))
                .withProperty(new DtEnd<Instant>("20091212T235959Z"))
                .withProperty(new Organizer("mailto:joe@example.com"))
                .withProperty(new Attendee("mailto:joe@example.com"))
                .withProperty(new Uid("12")).getFluentTarget();
        suite.addTest(new VFreeBusyTest<>("testReplyValidation", replyFb));
        var invalDurFb = (VFreeBusy) replyFb.copy().withProperty(new Duration(java.time.Duration.parse("PT1H")))
                .getFluentTarget();
        suite.addTest(new VFreeBusyTest<>("testReplyValidationException", invalDurFb));
        var invalSeqFb = (VFreeBusy) replyFb.copy().withProperty(new Sequence("12")).getFluentTarget();
        suite.addTest(new VFreeBusyTest<>("testReplyValidationException", invalSeqFb));


        // A test for a request for free time where the VFreeBusy instance doesn't
        // consume any time in the specified range. Correct behaviour should see the
        // return of a VFreeBusy specifying the entire range as free.
        VEvent event1 = new VEvent(TemporalAdapter.parse("20050101T080000").getTemporal(),
                java.time.Duration.ofMinutes(15), "Consultation 1");
        ComponentList<CalendarComponent> components = new ComponentList<>(Collections.singletonList(event1));

        Instant start = (Instant) TemporalAdapter.parse("20050103T000000Z").getTemporal();
        Instant end = (Instant) TemporalAdapter.parse("20050104T000000Z").getTemporal();

        VFreeBusy requestFree = new VFreeBusy(start, end, java.time.Duration.ofMinutes(5));
        // free/busy type should be FREE..
        suite.addTest(new VFreeBusyTest<>("testFbType", requestFree, components, FbType.FREE));

        // should be only one period..
        suite.addTest(new VFreeBusyTest<>("testPeriodCount", requestFree, components, 1));

        // period should be from the start to the end date..
        List<Interval> periods = Collections.singletonList(Interval.of(start, end));
//                TemporalAmountAdapter.between(start, end).getDuration()));
        suite.addTest(new VFreeBusyTest<>("testFreeBusyPeriods", requestFree, components, periods));

        //testBusyTime..
        var event12 = (VEvent) new VEvent(TemporalAdapter.parse("20050103T080000", ZoneId.systemDefault()).getTemporal(),
                java.time.Duration.ofHours(5), "Event 1")
                .withProperty(new RRule<>(new Recur<>("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR")))
                .getFluentTarget();
        components = new ComponentList<>(Collections.singletonList(event12));

        ZonedDateTime startZoned = TemporalAdapter.parse("20050104T110000", ZoneId.systemDefault()).getTemporal();
        Period<ZonedDateTime> period = new Period<>(startZoned, java.time.Duration.ofMinutes(30));

        VFreeBusy request = new VFreeBusy(period.getStart().toInstant(), period.getEnd().toInstant());
        // FBTYPE is optional - defaults to BUSY..
//        suite.addTest(new VFreeBusyTest<>("testFbType", request, components, FbType.BUSY));
        suite.addTest(new VFreeBusyTest<>("testPeriodCount", request, components, 1));

        periods = Collections.singletonList(Interval.of((Instant) TemporalAdapter.parse("20050104T080000Z").getTemporal(),
                java.time.Duration.parse("PT5H")));
        suite.addTest(new VFreeBusyTest<>("testFreeBusyPeriods", request, components, periods));

        // TODO: further work needed to "splice" events based on the amount
        // of time that intersects a free-busy request..
//        assertEquals(new DateTime("20050104T1100000Z"), busy1.getStart());
//        assertEquals("PT30M", busy1.getDuration().toString());

        request = new VFreeBusy(period.getStart().toInstant(), period.getEnd().toInstant(), java.time.Period.ZERO);
        suite.addTest(new VFreeBusyTest<>("testPeriodCount", request, components, 0));

        // anniversary-style events don't consume time..
        event1 = new VEvent(TemporalAdapter.parse("20081225").getTemporal(), "Christmas 2008");
        components = new ComponentList<>(Collections.singletonList(event1));

        start = (Instant) TemporalAdapter.parse("20081225T110000Z").getTemporal();
        end = (Instant) TemporalAdapter.parse("20081225T113000Z").getTemporal();

        request = new VFreeBusy(start, end);
        suite.addTest(new VFreeBusyTest<>("testPeriodCount", request, components, 0));

        request = new VFreeBusy(start, end, java.time.Duration.ofMinutes(15));
        suite.addTest(new VFreeBusyTest<>("testFbType", request, components, FbType.FREE));
        suite.addTest(new VFreeBusyTest<>("testPeriodCount", request, components, 1));

        periods = Collections.singletonList(Interval.of(start, java.time.Duration.ofMinutes(30)));
        suite.addTest(new VFreeBusyTest<>("testFreeBusyPeriods", request, components, periods));

        //some components are not in range
        ZoneId zoneId = TimeZoneRegistry.getGlobalZoneId("America/Los_Angeles");
        Parameter tzP = new TzId(zoneId.getId());
        ParameterList pl = new ParameterList(Collections.singletonList(tzP));
        DtStart<Temporal> dts = new DtStart<>(pl, TemporalAdapter.parse("20130124T020000").getTemporal());
        var e = (VEvent) new VEvent().withProperty(dts).withProperty(new Duration(java.time.Duration.parse("PT1H")))
                .withProperty(new RRule<>("FREQ=DAILY")).getFluentTarget();
        components = new ComponentList<>(Collections.singletonList(e));

        request = new VFreeBusy(TemporalAdapter.parse("20130124T110000Z").getTemporal(),
                TemporalAdapter.parse("20130125T110000Z").getTemporal());
        suite.addTest(new VFreeBusyTest<>("testPeriodCount", request, components, 1));

        return suite;
    }
}
