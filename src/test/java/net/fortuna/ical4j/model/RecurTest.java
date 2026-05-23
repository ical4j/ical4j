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
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.transform.recurrence.Frequency;
import net.fortuna.ical4j.util.TimeZones;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Stream;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.OCTOBER;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SEPTEMBER;
import static java.util.Calendar.WEEK_OF_MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;
import static net.fortuna.ical4j.model.WeekDay.FR;
import static net.fortuna.ical4j.model.WeekDay.MO;
import static net.fortuna.ical4j.model.WeekDay.SU;
import static net.fortuna.ical4j.model.WeekDay.TH;
import static net.fortuna.ical4j.model.WeekDay.TU;
import static net.fortuna.ical4j.model.WeekDay.WE;
import static net.fortuna.ical4j.transform.recurrence.Frequency.DAILY;
import static net.fortuna.ical4j.transform.recurrence.Frequency.WEEKLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created on 14/02/2005
 *
 * $Id$
 *
 * @author Ben Fortuna
 */
@Disabled("Failed after re-enabling JUnit 3/4 tests")
public class RecurTest {

    private static final Logger log = LoggerFactory.getLogger(RecurTest.class);

    private static final Locale testLocale = Locale.US;

    private static final TimeZoneRegistry tzReg = TimeZoneRegistryFactory.getInstance().createRegistry();

    private TimeZone originalDefault;

    @BeforeEach
    void setUp() {
        originalDefault = TimeZone.getDefault();
    }

    @AfterEach
    void tearDown() {
        TimeZone.setDefault(originalDefault);
    }

    /**
     *
     */
    @ParameterizedTest(name = "getDatesCount [{4}]")
    @MethodSource("getDatesCountData")
    public <T extends Temporal> void testGetDatesCount(Recur<T> recur, T seed, T periodStart, T periodEnd, Value value, int expectedCount) {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Australia/Melbourne"));
        List<T> dates;
        if (seed != null) {
            dates = recur.getDates(seed, periodStart, periodEnd);
        } else {
            dates = recur.getDates(periodStart, periodEnd);
        }
        assertEquals(expectedCount, dates.size());
    }

    @ParameterizedTest(name = "getDatesMaxTime")
    @MethodSource("getDatesMaxTimeData")
    public <T extends Temporal> void testGetDatesMaxTime(Recur<T> recur, T seed, T periodStart, T periodEnd, Value value, int expectedCount, long maxTime) {
        long t0 = System.currentTimeMillis();
        List<T> dates = recur.getDates(seed, periodStart, periodEnd);
        long dt = System.currentTimeMillis() - t0;

        String message = String.format("maxTime exceeded %dms", maxTime);
        assertEquals(maxTime, Math.max(dt, maxTime), message);
        assertEquals(expectedCount, dates.size());
    }

    /**
     *
     */
    @ParameterizedTest(name = "getNextDate")
    @MethodSource("getNextDateData")
    public <T extends Temporal> void testGetNextDate(Recur<T> recur, T seed, T periodStart, T expectedDate) {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(testLocale);
            assertEquals(expectedDate, recur.getNextDate(seed, periodStart));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    /**
     *
     */
    @ParameterizedTest(name = "getDatesCalendarField")
    @MethodSource("getDatesCalendarFieldData")
    public <T extends Temporal> void testGetDatesCalendarField(Recur<T> recur, T periodStart, T periodEnd, Value value,
                                                                int calendarField, int expectedCalendarValue) {
        List<T> dates = recur.getDates(periodStart, periodEnd);
        Calendar cal;
        if ((value != null) && (value == Value.DATE)) {
            cal = getInstance(TimeZones.getDateTimeZone());
        } else {
            cal = getInstance();
        }

        dates.forEach(date -> {
            cal.setTime(Date.from(TemporalAdapter.toLocalTime(date, ZoneId.systemDefault()).toInstant()));
            assertEquals(expectedCalendarValue, cal.get(calendarField));
        });
    }

    /**
     *
     */
    @ParameterizedTest(name = "getDatesOrdering")
    @MethodSource("getDatesOrderingData")
    public <T extends Temporal> void testGetDatesOrdering(Recur<T> recur, T seed, T periodStart, T periodEnd, Value value) {
        List<T> dl1 = recur.getDates(periodStart, periodEnd);
        T prev;
        T event = null;
        for (int i = 0; i < dl1.size(); i++) {
            prev = event;
            event = dl1.get(i);
            log.debug("Occurence " + i + " at " + event);
            assertTrue(prev == null || !Instant.from(prev).isAfter(Instant.from(event)));
        }
    }

    /**
     *
     */
    @ParameterizedTest(name = "getDatesNotEmpty")
    @MethodSource("getDatesNotEmptyData")
    public <T extends Temporal> void testGetDatesNotEmpty(Recur<T> recur, T seed, T periodStart, T periodEnd, Value value) {
        assertFalse(recur.getDates(periodStart, periodEnd).isEmpty());
    }

    /**
     *
     */
    @ParameterizedTest(name = "getDatesTimeZone")
    @MethodSource("getDatesTimeZoneData")
    public <T extends Temporal> void testGetDatesTimeZone(Recur<T> recur, T periodStart, T periodEnd, Value value, ZoneId expectedTimeZone) {
        List<T> dates = recur.getDates(periodStart, periodEnd);
        dates.forEach(date -> {
            assertEquals(expectedTimeZone, ((ZonedDateTime) date).getZone());
        });
    }

    /**
     * @throws ParseException
     */
    @ParameterizedTest(name = "invalidRecurrenceString [{0}]")
    @MethodSource("invalidRecurrenceStringData")
    public <T extends Temporal> void testInvalidRecurrenceString(String recurrenceString) throws ParseException {
        try {
            new Recur<T>(recurrenceString);
            fail("IllegalArgumentException not thrown!");
        } catch (IllegalArgumentException e) {
            // expected
            log.info("Caught exception: " + e.getMessage());
        }
    }

    static Stream<Arguments> invalidRecurrenceStringData() {
        return Stream.of(
                Arguments.of("BYDAY=MO,TU,WE,TH,FR"),
                Arguments.of("FREQ=FORTNIGHTLY;BYDAY=MO,TU,WE,TH,FR"),
                Arguments.of("FREQ=YEARLY;BYMONTH=0"),
                Arguments.of("FREQ=YEARLY;BYMONTHDAY=-400"),
                Arguments.of(""),
                Arguments.of(Recur.WEEKLY),
                Arguments.of("FREQ"),
                Arguments.of("FREQ=WEEKLY;BYDAY=xx")
        );
    }

    /**
     * @throws ParseException
     */
    @ParameterizedTest(name = "recurrenceString [{0}]")
    @MethodSource("recurrenceStringData")
    public <T extends Temporal> void testRecurrenceString(String recurrenceString, Frequency expectedFrequency,
                                                          int expectedInterval, WeekDayList expectedDayList) throws ParseException {
        Recur<T> recur = new Recur<>(recurrenceString);
        assertEquals(expectedFrequency, recur.getFrequency());
        assertEquals(expectedInterval, recur.getInterval());
        assertEquals(expectedDayList, recur.getDayList());
    }

    static Stream<Arguments> recurrenceStringData() {
        // Test creation of recur instances..
        String recurString = "FREQ=MONTHLY;INTERVAL=2;BYDAY=3MO";
        WeekDayList expectedDayList = new WeekDayList();
        expectedDayList.add(new WeekDay(MO, 3));

        return Stream.of(
                Arguments.of(recurString, Frequency.MONTHLY, 2, expectedDayList)
        );
    }

    static Stream<Arguments> getDatesCountData() throws ParseException {
        Stream.Builder<Arguments> builder = Stream.builder();

        // testGetDates..
        Recur<ZonedDateTime> everySecondDay = new Recur.Builder<ZonedDateTime>().frequency(DAILY).count(10)
                .interval(2).build();

        ZonedDateTime start = ZonedDateTime.now().withYear(2018).withMonth(12).withDayOfMonth(16);
        ZonedDateTime end = start.plusDays(10);

        Recur<ZonedDateTime> everySecondDayUntil = new Recur.Builder<ZonedDateTime>().frequency(DAILY).until(end).interval(2).build();

        builder.add(Arguments.of(everySecondDayUntil, null, start, end, Value.DATE, 6));

        // Test BYDAY rules..
        Recur<ZonedDateTime> weekDays = new Recur.Builder<ZonedDateTime>().frequency(DAILY).count(10).interval(1)
                .dayList(MO, TU, WE, TH, FR).build();

        start = ZonedDateTime.of(2022, 8, 18, 11, 3, 0, 0, ZoneId.systemDefault());
        end = start.plusDays(10);
        builder.add(Arguments.of(weekDays, null, start, end, Value.DATE, 7));

        // testGetDates everyTuesdayThursday
        Recur<ZonedDateTime> everyTuesdayThursday = new Recur.Builder<ZonedDateTime>().frequency(WEEKLY).count(10)
                .dayList(TU, TH).build();
        start = ZonedDateTime.now().withYear(1997).withMonth(9).withHour(9).withMinute(0).withSecond(0);
        end = start.plusYears(2);
        builder.add(Arguments.of(everyTuesdayThursday, start, start, end, Value.DATE, 10));

        // february4th count 2
        Recur<ZonedDateTime> february4th = new Recur<>("FREQ=YEARLY;BYMONTH=2;BYMONTHDAY=4;BYDAY=MO,TU,WE,TH,FR,SA,SU");
        start = ZonedDateTime.now().withYear(2006).withMonth(12).withDayOfMonth(31);
        end = start.withYear(2008).withMonth(12).withDayOfMonth(31);
        builder.add(Arguments.of(february4th, null, start, end, Value.DATE, 2));

        // nineToFourThirty 16
        Recur<ZonedDateTime> nineToFourThirty = new Recur<>("FREQ=DAILY;BYHOUR=9,10,11,12,13,14,15,16;BYMINUTE=0,30");
        start = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0);
        end = start.plusDays(1);
        builder.add(Arguments.of(nineToFourThirty, null, start, end, Value.DATE_TIME, 16));

        // thirtyFirstOfEachMonth 7
        Recur<ZonedDateTime> thirtyFirstOfEachMonth = new Recur<>("FREQ=MONTHLY;BYMONTHDAY=31");
        start = ZonedDateTime.now();
        end = start.plusYears(1);
        builder.add(Arguments.of(thirtyFirstOfEachMonth, null, start, end, Value.DATE, 7));

        TemporalAdapter<ZonedDateTime> seed = TemporalAdapter.parse("20081103T070000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodStart = TemporalAdapter.parse("20081109T210000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodEnd = TemporalAdapter.parse("20100104T210000", ZoneId.systemDefault());

        // rrule never matching any candidate  - should reach limit
        Recur<ZonedDateTime> everySecondTuesdayThursday = new Recur<>("FREQ=DAILY;COUNT=60;BYDAY=TU,TH;BYSETPOS=2");
        builder.add(Arguments.of(everySecondTuesdayThursday, seed.getTemporal(), start, end, Value.DATE, 0));

        // rather uncommon rule
        Recur<ZonedDateTime> firstFourWeeksOfYear = new Recur<>("FREQ=YEARLY;BYWEEKNO=1,2,3,4");

        TemporalAdapter<ZonedDateTime> seed2 = TemporalAdapter.parse("20130101T120000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodStart2 = TemporalAdapter.parse("20130101T120000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodEnd2 = TemporalAdapter.parse("20130123T120000", ZoneId.systemDefault());
        builder.add(Arguments.of(firstFourWeeksOfYear, seed2.getTemporal(), periodStart2.getTemporal(), periodEnd2.getTemporal(),
                Value.DATE_TIME, 4));

        // Test issue: https://github.com/ical4j/ical4j/issues/576
        Recur<LocalDate> everySecondWeek = new Recur<>("FREQ=YEARLY;BYDAY=WE;BYWEEKNO=1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31,33,35,37,39,41,43,45,47,49,51,53");

        TemporalAdapter<LocalDate> firstDate = TemporalAdapter.parse("20220501");
        TemporalAdapter<LocalDate> secondDate = TemporalAdapter.parse("20230501");
        builder.add(Arguments.of(everySecondWeek, null, firstDate.getTemporal(), secondDate.getTemporal(),
                Value.DATE, 27));

        Recur<ZonedDateTime> threeWeekdays = new Recur<>("FREQ=DAILY;COUNT=3;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR");

        TemporalAdapter<ZonedDateTime> seed3 = TemporalAdapter.parse("20131215T000000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodStart3 = TemporalAdapter.parse("20131215T000000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodEnd3 = TemporalAdapter.parse("20180101T120000", ZoneId.systemDefault());
        builder.add(Arguments.of(threeWeekdays, seed3.getTemporal(),
                periodStart3.getTemporal(), periodEnd3.getTemporal(), Value.DATE_TIME, 3));

        TemporalAdapter<ZonedDateTime> periodStart4 = TemporalAdapter.parse("20160101T120000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodEnd4 = TemporalAdapter.parse("20160123T120000", ZoneId.systemDefault());
        builder.add(Arguments.of(firstFourWeeksOfYear, seed3.getTemporal(), periodStart4.getTemporal(), periodEnd4.getTemporal(),
                Value.DATE_TIME, 3));

        return builder.build();
    }

    static Stream<Arguments> getDatesMaxTimeData() throws ParseException {
        Stream.Builder<Arguments> builder = Stream.builder();

        // check maxTime
        Recur<ZonedDateTime> everyThursdayUntil = new Recur<>("FREQ=WEEKLY;WKST=MO;UNTIL=20160901T230000;INTERVAL=1;BYDAY=TH");
        builder.add(Arguments.of(everyThursdayUntil, TemporalAdapter.parse("20160414T100000").getTemporal(),
                TemporalAdapter.parse("20110713T213021").getTemporal(), TemporalAdapter.parse("20230713T213021").getTemporal(),
                Value.DATE_TIME, 21, 100L));

        // check maxTime
        Recur<ZonedDateTime> firstSaturdayOfMonth = new Recur<>("FREQ=MONTHLY;WKST=MO;INTERVAL=1;BYDAY=1SA");
        builder.add(Arguments.of(firstSaturdayOfMonth, TemporalAdapter.parse("20160507T090000").getTemporal(),
                TemporalAdapter.parse("20110713T213022").getTemporal(), TemporalAdapter.parse("20260713T213022").getTemporal(),
                Value.DATE_TIME, 123, 100L));

        // check maxTime
        Recur<ZonedDateTime> everyWednesday = new Recur<>("FREQ=WEEKLY;WKST=MO;INTERVAL=1;BYDAY=WE");
        builder.add(Arguments.of(everyWednesday, TemporalAdapter.parse("20160427T160000").getTemporal(),
                TemporalAdapter.parse("20110713T213022").getTemporal(), TemporalAdapter.parse("20230713T213022").getTemporal(),
                Value.DATE_TIME, 377, 100L));

        // check maxTime
        Recur<ZonedDateTime> everyThursday = new Recur<>("FREQ=WEEKLY;WKST=MO;INTERVAL=1;BYDAY=TU");
        builder.add(Arguments.of(everyThursday, TemporalAdapter.parse("20200324T200000").getTemporal(),
                TemporalAdapter.parse("20110714T083812").getTemporal(), TemporalAdapter.parse("20230714T083812").getTemporal(),
                Value.DATE_TIME, 173, 100L));

        return builder.build();
    }

    static Stream<Arguments> getNextDateData() throws ParseException {
        Stream.Builder<Arguments> builder = Stream.builder();

        // testGetNextDate..
        Recur<LocalDate> everyDay = new Recur.Builder<LocalDate>().frequency(DAILY).count(3).build();
        TemporalAdapter<LocalDate> seedDate = TemporalAdapter.parse("20080401");
        TemporalAdapter<LocalDate> firstDate = TemporalAdapter.parse("20080402");
        TemporalAdapter<LocalDate> secondDate = TemporalAdapter.parse("20080403");

        builder.add(Arguments.of(everyDay, seedDate.getTemporal(), seedDate.getTemporal(), firstDate.getTemporal()));
        builder.add(Arguments.of(everyDay, seedDate.getTemporal(), firstDate.getTemporal(), secondDate.getTemporal()));
        builder.add(Arguments.of(everyDay, seedDate.getTemporal(), secondDate.getTemporal(), null));

        // test DateTime
        Recur<ZonedDateTime> weeklyUntil = new Recur.Builder<ZonedDateTime>().frequency(WEEKLY)
                .until(TemporalAdapter.parse("20080421T063000", ZoneId.systemDefault()).getTemporal()).build();
        TemporalAdapter<ZonedDateTime> seed = TemporalAdapter.parse("20080407T063000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> firstDateTime = TemporalAdapter.parse("20080414T063000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> secondDateTime = TemporalAdapter.parse("20080421T063000", ZoneId.systemDefault());

        builder.add(Arguments.of(weeklyUntil, seed.getTemporal(), seed.getTemporal(), firstDateTime.getTemporal()));
        builder.add(Arguments.of(weeklyUntil, seed.getTemporal(), firstDateTime.getTemporal(), secondDateTime.getTemporal()));
        builder.add(Arguments.of(weeklyUntil, seed.getTemporal(), secondDateTime.getTemporal(), null));

        // Ensure the first result from getDates is the same as getNextDate..
        Recur<ZonedDateTime> everyThirdMonWedThu = new Recur<>("FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,WE,TH");
        TemporalAdapter<ZonedDateTime> seed2 = TemporalAdapter.parse("20081103T070000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodStart = TemporalAdapter.parse("20081109T210000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodEnd = TemporalAdapter.parse("20100104T210000", ZoneId.systemDefault());

        Locale currentLocale = Locale.getDefault();
        List<ZonedDateTime> getDatesResult;
        try {
            Locale.setDefault(testLocale);
            getDatesResult = everyThirdMonWedThu.getDates(seed2.getTemporal(), periodStart.getTemporal(), periodEnd.getTemporal());
        } finally {
            Locale.setDefault(currentLocale);
        }
        builder.add(Arguments.of(everyThirdMonWedThu, seed2.getTemporal(), periodStart.getTemporal(), getDatesResult.get(0)));

        Recur<LocalDate> everyMonday = new Recur<>("FREQ=WEEKLY;BYDAY=MO");
        TemporalAdapter<LocalDate> seedDate2 = TemporalAdapter.parse("20081212");
        TemporalAdapter<LocalDate> firstDate2 = TemporalAdapter.parse("20081211");
        TemporalAdapter<LocalDate> secondDate2 = TemporalAdapter.parse("20081215");
        builder.add(Arguments.of(everyMonday, seedDate2.getTemporal(), firstDate2.getTemporal(), secondDate2.getTemporal()));

        Recur<ZonedDateTime> firstSundayOfApril = new Recur<>("FREQ=YEARLY;BYMONTH=4;BYDAY=1SU");
        TemporalAdapter<ZonedDateTime> periodEnd2 = TemporalAdapter.parse("20090405T070000", ZoneId.systemDefault());
        builder.add(Arguments.of(firstSundayOfApril, seed2.getTemporal(), periodStart.getTemporal(), periodEnd2.getTemporal()));

        // rrule with negative bymonthday
        Recur<ZonedDateTime> everySecondYearLastDayOfMonth = new Recur<>("FREQ=YEARLY;COUNT=4;INTERVAL=2;BYMONTH=1,2,3;BYMONTHDAY=-1");
        TemporalAdapter<ZonedDateTime> periodEnd3 = TemporalAdapter.parse("20100131T070000", ZoneId.systemDefault());
        builder.add(Arguments.of(everySecondYearLastDayOfMonth, seed2.getTemporal(), periodStart.getTemporal(), periodEnd3.getTemporal()));

        // rrule with bymonth, byday and bysetpos. Issue #39
        Recur<ZonedDateTime> lastDayOfFebMarSepOct = new Recur<>("FREQ=MONTHLY;WKST=MO;INTERVAL=1;BYMONTH=2,3,9,10;BYMONTHDAY=28,29,30,31;BYSETPOS=-1");
        TemporalAdapter<ZonedDateTime> seed3 = TemporalAdapter.parse("20150701T000000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodStart3 = TemporalAdapter.parse("20150701T000000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodEnd4 = TemporalAdapter.parse("20150930T000000", ZoneId.systemDefault());
        builder.add(Arguments.of(lastDayOfFebMarSepOct, seed3.getTemporal(), periodStart3.getTemporal(), periodEnd4.getTemporal()));

        // test getting valid recurrence at tip of smart increment
        // feb 29 2020 monthly with only valid month by february should return feb 28 2021
        Recur<ZonedDateTime> everySecondMonth = new Recur<>("FREQ=MONTHLY;BYMONTH=2;INTERVAL=1");
        TemporalAdapter<ZonedDateTime> seed4 = TemporalAdapter.parse("20200229T000000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodStart4 = TemporalAdapter.parse("20200229T000000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodEnd5 = TemporalAdapter.parse("20240229T000000", ZoneId.systemDefault());
        builder.add(Arguments.of(everySecondMonth, seed4.getTemporal(), periodStart4.getTemporal(), periodEnd5.getTemporal()));

        // test hitting limit when getting invalid next recurrence
        Recur<ZonedDateTime> everySecondMonth30th = new Recur<>("FREQ=MONTHLY;BYMONTH=2;BYMONTHDAY=30;INTERVAL=1");
        builder.add(Arguments.of(everySecondMonth30th, seed4.getTemporal(), periodStart4.getTemporal(), null));

        // test hitting leap year appropriately
        Recur<ZonedDateTime> every29th = new Recur<>("FREQ=YEARLY;BYMONTHDAY=29;INTERVAL=1");
        builder.add(Arguments.of(every29th, seed4.getTemporal(), periodStart4.getTemporal(), periodEnd5.getTemporal()));

        // test correct hit on first incrementation
        Recur<ZonedDateTime> everyFourthYear = new Recur<>("FREQ=YEARLY;INTERVAL=4");
        builder.add(Arguments.of(everyFourthYear, seed4.getTemporal(), periodStart4.getTemporal(), periodEnd5.getTemporal()));

        // last working day starting from may 31 2020 should return jun 30 2020
        TemporalAdapter<ZonedDateTime> seed5 = TemporalAdapter.parse("20200531T000000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodStart5 = TemporalAdapter.parse("20200531T000000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodEnd6 = TemporalAdapter.parse("20200731T000000", ZoneId.systemDefault());
        Recur<ZonedDateTime> lastWeekdayOfMonth = new Recur<>("FREQ=MONTHLY;BYDAY=MO,TU,WE,TH,FR;BYSETPOS=-1");
        builder.add(Arguments.of(lastWeekdayOfMonth, seed5.getTemporal(), periodStart5.getTemporal(), periodEnd6.getTemporal()));

        // 5th sunday monthly starting from aug 31 2020 should return nov 29 2020
        TemporalAdapter<ZonedDateTime> seed6 = TemporalAdapter.parse("20200831T000000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodStart6 = TemporalAdapter.parse("20200831T000000", ZoneId.systemDefault());
        TemporalAdapter<ZonedDateTime> periodEnd7 = TemporalAdapter.parse("20210131T000000", ZoneId.systemDefault());
        Recur<ZonedDateTime> fifthSundayOfMonth = new Recur<>("FREQ=MONTHLY;BYDAY=SU;BYSETPOS=5");
        builder.add(Arguments.of(fifthSundayOfMonth, seed6.getTemporal(), periodStart6.getTemporal(), periodEnd7.getTemporal()));

        return builder.build();
    }

    static Stream<Arguments> getDatesCalendarFieldData() throws ParseException {
        Stream.Builder<Arguments> builder = Stream.builder();

        // Test BYDAY recurrence rules..
        Recur<ZonedDateTime> fifthTuesday = new Recur<>("FREQ=MONTHLY;WKST=SU;INTERVAL=2;BYDAY=5TU");
        ZonedDateTime start = ZonedDateTime.now().withSecond(0);
        ZonedDateTime end = start.plusYears(2);
        builder.add(Arguments.of(fifthTuesday, start, end, Value.DATE, WEEK_OF_MONTH, 5));

        // testRecurGetDates..
        Recur<LocalDate> everySaturday = new Recur<>("FREQ=WEEKLY;INTERVAL=1;BYDAY=SA");
        TemporalAdapter<LocalDate> periodStartDate = TemporalAdapter.parse("20050101");
        TemporalAdapter<LocalDate> periodEndDate = TemporalAdapter.parse("20060101");
        builder.add(Arguments.of(everySaturday, periodStartDate.getTemporal(),
                periodEndDate.getTemporal(), null, DAY_OF_WEEK, SATURDAY));

        // testFriday13Recur..
        Recur<ZonedDateTime> friday13th = new Recur<>("FREQ=MONTHLY;BYDAY=FR;BYMONTHDAY=13");
        ZonedDateTime start2 = ZonedDateTime.now().withYear(1997).withMonth(1).withDayOfMonth(1);
        ZonedDateTime end2 = start2.withYear(2000);
        builder.add(Arguments.of(friday13th, start2, end2, Value.DATE, DAY_OF_MONTH, 13));
        builder.add(Arguments.of(friday13th, start2, end2, Value.DATE, DAY_OF_WEEK, FRIDAY));

        // Unit test for recurrence every 4th february..
        Recur<ZonedDateTime> february4th = new Recur<>("FREQ=YEARLY;BYMONTH=2;BYMONTHDAY=4;BYDAY=MO,TU,WE,TH,FR,SA,SU");
        ZonedDateTime start3 = ZonedDateTime.now().withYear(2006).withMonth(4).withDayOfMonth(10);
        ZonedDateTime end3 = start3.withYear(2008).withMonth(2).withDayOfMonth(6);
        builder.add(Arguments.of(february4th, start3, end3, Value.DATE, DAY_OF_MONTH, 4));
        builder.add(Arguments.of(february4th, start3, end3, Value.DATE, MONTH, 1));

        ZonedDateTime start4 = ZonedDateTime.now().withYear(2006).withMonth(12).withDayOfMonth(31);
        ZonedDateTime end4 = start4.withYear(2008).withMonth(12).withDayOfMonth(31);
        builder.add(Arguments.of(february4th, start4, end4, Value.DATE, DAY_OF_MONTH, 4));
        builder.add(Arguments.of(february4th, start4, end4, Value.DATE, MONTH, 1));

        return builder.build();
    }

    static Stream<Arguments> getDatesOrderingData() throws ParseException {
        // Test ordering of returned dates..
        String s1 = "FREQ=WEEKLY;COUNT=75;INTERVAL=2;BYDAY=SU,MO,TU;WKST=SU";
        Recur<ZonedDateTime> everySecondSunMonTue = new Recur<>(s1);
        ZonedDateTime d1 = ZonedDateTime.now();
        ZonedDateTime d2 = d1.plusYears(1);
        return Stream.of(
                Arguments.of(everySecondSunMonTue, null, d1, d2, Value.DATE_TIME)
        );
    }

    static Stream<Arguments> getDatesNotEmptyData() throws ParseException {
        // testMonthByDay..
        Recur<LocalDate> thirdWednesdayOfMonth = new Recur<>("FREQ=MONTHLY;UNTIL=20061220;INTERVAL=1;BYDAY=3WE");
        LocalDate startDate = LocalDate.of(2006, 12, 1);
        LocalDate endDate = startDate.plusYears(1);
        return Stream.of(
                Arguments.of(thirdWednesdayOfMonth, null, startDate, endDate, Value.DATE)
        );
    }

    static Stream<Arguments> getDatesTimeZoneData() throws ParseException {
        // testAlternateTimeZone..
        Recur<ZonedDateTime> everyWednesdayNoon = new Recur<>("FREQ=WEEKLY;BYDAY=WE;BYHOUR=12;BYMINUTE=0");

        ZonedDateTime start = ZonedDateTime.now(TimeZoneRegistry.getGlobalZoneId("America/Los_Angeles")).withSecond(0);
        DtStart<ZonedDateTime> dtStart = new DtStart<>(start).add(new TzId(start.getZone().getId()));
        ZonedDateTime end = start.plusMonths(2);
        DtEnd<ZonedDateTime> dtEnd = new DtEnd<>(end);

        return Stream.of(
                Arguments.of(everyWednesdayNoon, dtStart.getDate(), dtEnd.getDate(), Value.DATE_TIME,
                        TimeZoneRegistry.getGlobalZoneId("America/Los_Angeles"))
        );
    }
}
