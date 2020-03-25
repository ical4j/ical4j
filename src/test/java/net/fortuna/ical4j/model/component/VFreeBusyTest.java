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
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created on 10/02/2005
 *
 * @author Ben Fortuna
 */
public class VFreeBusyTest extends CalendarComponentTest {

    private static Logger log = LoggerFactory.getLogger(VFreeBusyTest.class);

    private TimeZoneRegistry registry;

    private VTimeZone tz;

    private TzId tzParam;

    private ComponentList<CalendarComponent> components;

    private VFreeBusy request;

    private FbType expectedFbType;

    private int expectedPeriodCount;

    private PeriodList expectedPeriods;

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
     * @param expectedPeriodCount
     */
    public VFreeBusyTest(String testMethod, VFreeBusy component, ComponentList<CalendarComponent> components, int expectedPeriodCount) {
        super(testMethod, component);
        this.request = component;
        this.components = components;
        this.expectedPeriodCount = expectedPeriodCount;
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
     * @param expectedPeriods
     */
    public VFreeBusyTest(String testMethod, VFreeBusy component, ComponentList<CalendarComponent> components, PeriodList expectedPeriods) {
        super(testMethod, component);
        this.request = component;
        this.components = components;
        this.expectedPeriods = expectedPeriods;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
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
    protected void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
        super.tearDown();
    }

    /*
     * Class under test for void VFreeBusy(ComponentList)
     */
    public final void testVFreeBusyComponentList() {
        ComponentList<CalendarComponent> components = new ComponentList<>();

        ZonedDateTime startDate = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(0), ZoneId.systemDefault());
        ZonedDateTime endDate = ZonedDateTime.now();

        List<Parameter> tzParams = new ArrayList<>();
        tzParams.add(new TzId(startDate.getZone().getId()));
        VEvent event = new VEvent();
        event.getProperties().add(new DtStart<>(tzParams, startDate));
        event.getProperties().add(new Duration(java.time.Duration.ofHours(1)));
        components.add(event);

        VEvent event2 = new VEvent();
        event2.getProperties().add(new DtStart<>(tzParams, startDate));
        event2.getProperties().add(new DtEnd<>(endDate));
        components.add(event2);

        VFreeBusy request = new VFreeBusy(startDate, endDate);

        VFreeBusy fb = new VFreeBusy(request, components);

        if (log.isDebugEnabled()) {
            log.debug("\n==\n" + fb.toString());
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
            log.debug("\n==\n" + fb.toString());
        }

        // request all free time between 1970 and now of duration 2 hours or
        // more..
        VFreeBusy requestFree = new VFreeBusy(startDate, endDate,
                java.time.Duration.ofHours(2));

        VFreeBusy fb2 = new VFreeBusy(requestFree, calendar.getComponents());

        if (log.isDebugEnabled()) {
            log.debug("\n==\n" + fb2.toString());
        }
    }

    public final void testVFreeBusyComponentList3() {
        ComponentList<CalendarComponent> components = new ComponentList<>();

        ZonedDateTime eventStart = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(0), ZoneId.systemDefault());

        VEvent event = new VEvent(eventStart, java.time.Duration.ofHours(1),
                "Progress Meeting");
        // VEvent event = new VEvent(startDate, cal.getTime(), "Progress
        // Meeting");
        // add timezone information..
        event.getProperty(Property.DTSTART).getParameters()
                .add(tzParam);
        components.add(event);

        // add recurrence..
        Recur recur = new Recur.Builder().frequency(Recur.Frequency.YEARLY).count(20)
                .monthList(new NumberList("1")).monthDayList(new NumberList("26"))
                .hourList(new NumberList("9")).minuteList(new NumberList("30")).build();
        event.getProperties().add(new RRule(recur));

        if (log.isDebugEnabled()) {
            log.debug("\n==\n" + event.toString());
        }

        ZonedDateTime requestEnd = ZonedDateTime.now();

        VFreeBusy request = new VFreeBusy(eventStart, requestEnd);

        VFreeBusy fb = new VFreeBusy(request, components);

        if (log.isDebugEnabled()) {
            log.debug("\n==\n" + fb.toString());
        }
    }

    public final void testVFreeBusyComponentList4() {
        ComponentList<CalendarComponent> components = new ComponentList<>();

        Instant startDate = Instant.now();
        Instant endDate = ZonedDateTime.now().plusDays(3).toInstant();

        VEvent event = new VEvent();
        event.getProperties().add(new DtStart<>(startDate));
        event.getProperties().add(new Duration(java.time.Duration.ofHours(1)));
        components.add(event);

        VEvent event2 = new VEvent();
        event2.getProperties().add(new DtStart<>(startDate));
        event2.getProperties().add(new DtEnd<>(endDate));
        components.add(event2);

        VFreeBusy request = new VFreeBusy(startDate, endDate,
                java.time.Duration.ofHours(1));

        VFreeBusy fb = new VFreeBusy(request, components);

        log.debug("\n==\n" + fb.toString());
    }

    public final void testAngelites() {
        log.info("angelites test:\n================");

        Calendar freeBusyTest = new Calendar();

        // add an event
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(-1);

        VEvent dteEnd = new VEvent(start, end, "DATE END INCLUDED");

        VEvent duration = new VEvent(start, java.time.Duration.ofHours(1), "DURATION");

        freeBusyTest.getComponents().add(dteEnd);
        freeBusyTest.getComponents().add(duration);

        Instant dtstart = Instant.now();
        Instant dtend = ZonedDateTime.now().plusDays(-2).toInstant();

        VFreeBusy getBusy = new VFreeBusy(dtstart, dtend);
        VFreeBusy requestFree = new VFreeBusy(dtstart, dtend, java.time.Duration.ofMinutes(30));

        log.debug("GET BUSY: \n" + getBusy.toString());
        log.debug("REQUEST FREE: \n" + requestFree.toString());

        Calendar FreeBusyTest2 = new Calendar();

        VFreeBusy replyBusy = new VFreeBusy(getBusy, freeBusyTest
                .getComponents());
        VFreeBusy replyFree = new VFreeBusy(requestFree, freeBusyTest
                .getComponents());

        log.debug("REPLY BUSY: \n" + replyBusy.toString());
        log.debug("REPLY FREE: \n" + replyFree.toString());

        FreeBusyTest2.getComponents().add(replyBusy);
        VFreeBusy replyBusy2 = new VFreeBusy(getBusy, FreeBusyTest2
                .getComponents());
        VFreeBusy replyFree2 = new VFreeBusy(requestFree, FreeBusyTest2
                .getComponents());

        log.debug("REPLY BUSY2: \n" + replyBusy2.toString());
        log.debug("REPLY FREE2: \n" + replyFree2.toString());
    }

    /**
     *
     */
    public void testFbType() {
        VFreeBusy result = new VFreeBusy(request, components);
        FreeBusy fb = result.getProperty(Property.FREEBUSY);
        assertTrue(fb.getParameter(Parameter.FBTYPE).equals(Optional.of(expectedFbType)));
    }

    /**
     *
     */
    public void testPeriodCount() {
        VFreeBusy result = new VFreeBusy(request, components);
        FreeBusy fb = (FreeBusy) result.getProperty(Property.FREEBUSY);
        if (expectedPeriodCount > 0) {
            assertEquals(expectedPeriodCount, fb.getPeriods().size());
        } else {
            assertNull(fb);
        }
    }

    /**
     *
     */
    public void testFreeBusyPeriods() {
        VFreeBusy result = new VFreeBusy(request, components);
        FreeBusy fb = (FreeBusy) result.getProperty(Property.FREEBUSY);
        assertEquals(expectedPeriods, fb.getPeriods());
    }

    /**
     * @return
     */
    public static TestSuite suite() throws ParseException, URISyntaxException,
            IOException {
        TestSuite suite = new TestSuite();

        suite.addTest(new VFreeBusyTest("testVFreeBusyComponentList"));
        suite.addTest(new VFreeBusyTest("testVFreeBusyComponentList2"));
        suite.addTest(new VFreeBusyTest("testVFreeBusyComponentList3"));
        suite.addTest(new VFreeBusyTest("testVFreeBusyComponentList4"));
        suite.addTest(new VFreeBusyTest("testAngelites"));

        // icalendar validation
        VFreeBusy fb = new VFreeBusy();
        suite.addTest(new VFreeBusyTest("testIsCalendarComponent", fb));
        suite.addTest(new VFreeBusyTest("testValidation", fb));

        // iTIP PUBLISH validation
        suite.addTest(new VFreeBusyTest("testPublishValidationException", new VFreeBusy()));
        VFreeBusy publishFb = new VFreeBusy();
        publishFb.getProperties().add(new DtStart("20091212T000000Z"));
        publishFb.getProperties().add(new DtEnd("20091212T235959Z"));
        publishFb.getProperties().add(new FreeBusy("20091212T140000Z/PT3H"));
        publishFb.getProperties().add(new Organizer("mailto:joe@example.com"));
        publishFb.getProperties().add(new Uid("12"));
        suite.addTest(new VFreeBusyTest("testPublishValidation", publishFb));

        // iTIP REPLY validation
        suite.addTest(new VFreeBusyTest("testReplyValidationException", new VFreeBusy()));
        VFreeBusy replyFb = new VFreeBusy();
        replyFb.getProperties().add(new DtStart("20091212T000000Z"));
        replyFb.getProperties().add(new DtEnd("20091212T235959Z"));
        replyFb.getProperties().add(new Organizer("mailto:joe@example.com"));
        replyFb.getProperties().add(new Attendee("mailto:joe@example.com"));
        replyFb.getProperties().add(new Uid("12"));
        suite.addTest(new VFreeBusyTest("testReplyValidation", replyFb));
        VFreeBusy invalDurFb = (VFreeBusy) replyFb.copy();
        invalDurFb.getProperties().add(new Duration(java.time.Duration.parse("PT1H")));
        suite.addTest(new VFreeBusyTest("testReplyValidationException", invalDurFb));
        VFreeBusy invalSeqFb = (VFreeBusy) replyFb.copy();
        invalSeqFb.getProperties().add(new Sequence("12"));
        suite.addTest(new VFreeBusyTest("testReplyValidationException", invalSeqFb));


        // A test for a request for free time where the VFreeBusy instance doesn't
        // consume any time in the specified range. Correct behaviour should see the
        // return of a VFreeBusy specifying the entire range as free.
        ComponentList<CalendarComponent> components = new ComponentList<>();
        VEvent event1 = new VEvent(TemporalAdapter.parse("20050101T080000").getTemporal(),
                java.time.Duration.ofMinutes(15), "Consultation 1");
        components.add(event1);

        Instant start = (Instant) TemporalAdapter.parse("20050103T000000Z").getTemporal();
        Instant end = (Instant) TemporalAdapter.parse("20050104T000000Z").getTemporal();

        VFreeBusy requestFree = new VFreeBusy(start, end, java.time.Duration.ofMinutes(5));
        // free/busy type should be FREE..
        suite.addTest(new VFreeBusyTest("testFbType", requestFree, components, FbType.FREE));

        // should be only one period..
        suite.addTest(new VFreeBusyTest("testPeriodCount", requestFree, components, 1));

        // period should be from the start to the end date..
        PeriodList<Instant> periods = new PeriodList<>();
        periods.add(new Period<>(start, end));
        suite.addTest(new VFreeBusyTest("testFreeBusyPeriods", requestFree, components, periods));

        //testBusyTime..
        components = new ComponentList<>();
        event1 = new VEvent((Temporal) TemporalAdapter.parse("20050103T080000Z").getTemporal(),
                java.time.Duration.ofHours(5), "Event 1");
        Recur rRuleRecur = new Recur("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR");
        RRule rRule = new RRule(rRuleRecur);
        event1.getProperties().add(rRule);
        components.add(event1);

        start = (Instant) TemporalAdapter.parse("20050104T110000Z").getTemporal();
        Period<Instant> period = new Period<>(start, java.time.Duration.ofMinutes(30));

        VFreeBusy request = new VFreeBusy(period.getStart(), period.getEnd());
        // FBTYPE is optional - defaults to BUSY..
//        suite.addTest(new VFreeBusyTest("testFbType", request, components, FbType.BUSY));
        suite.addTest(new VFreeBusyTest("testPeriodCount", request, components, 1));

        periods = new PeriodList<>();
        periods.add(new Period<>((Instant) TemporalAdapter.parse("20050104T080000Z").getTemporal(),
                java.time.Duration.parse("PT5H")));
        suite.addTest(new VFreeBusyTest("testFreeBusyPeriods", request, components, periods));

        // TODO: further work needed to "splice" events based on the amount
        // of time that intersects a free-busy request..
//        assertEquals(new DateTime("20050104T1100000Z"), busy1.getStart());
//        assertEquals("PT30M", busy1.getDuration().toString());

        request = new VFreeBusy(period.getStart(), period.getEnd(), java.time.Period.ZERO);
        suite.addTest(new VFreeBusyTest("testPeriodCount", request, components, 0));

        // anniversary-style events don't consume time..
        components = new ComponentList<>();
        event1 = new VEvent(TemporalAdapter.parse("20081225").getTemporal(), "Christmas 2008");
        components.add(event1);

        start = (Instant) TemporalAdapter.parse("20081225T110000Z").getTemporal();
        end = (Instant) TemporalAdapter.parse("20081225T113000Z").getTemporal();

        request = new VFreeBusy(start, end);
        suite.addTest(new VFreeBusyTest("testPeriodCount", request, components, 0));

        request = new VFreeBusy(start, end, java.time.Duration.ofMinutes(15));
        suite.addTest(new VFreeBusyTest("testFbType", request, components, FbType.FREE));
        suite.addTest(new VFreeBusyTest("testPeriodCount", request, components, 1));

        periods = new PeriodList<>();
        periods.add(new Period<>(start, java.time.Duration.ofMinutes(30)));
        suite.addTest(new VFreeBusyTest("testFreeBusyPeriods", request, components, periods));

        //some components are not in range
        components = new ComponentList<>();
        ZoneId zoneId = TimeZoneRegistry.getGlobalZoneId("America/Los_Angeles");
        Parameter tzP = new TzId(zoneId.getId());
        List<Parameter> pl = new ArrayList<>();
        pl.add(tzP);
        DtStart dts = new DtStart<>(pl, TemporalAdapter.parse("20130124T020000").getTemporal());
        dts.getParameters().add(tzP);
        VEvent e = new VEvent();
        e.getProperties().add(dts);
        e.getProperties().add(new Duration(java.time.Duration.parse("PT1H")));
        e.getProperties().add(new RRule("FREQ=DAILY"));
        components.add(e);
        period = new Period<>((Instant) TemporalAdapter.parse("20130124T110000Z").getTemporal(),
                (Instant) TemporalAdapter.parse("20130125T110000Z").getTemporal());
        request = new VFreeBusy(period.getStart(), period.getEnd());
        suite.addTest(new VFreeBusyTest("testPeriodCount", request, components, 1));

        return suite;
    }
}
