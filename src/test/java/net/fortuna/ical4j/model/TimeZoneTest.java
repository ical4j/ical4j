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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * $Id$
 * <p/>
 * Created on 14/09/2005
 * <p/>
 * Unit tests for <code>TimeZone</code>.
 *
 * @author Ben Fortuna
 */
public class TimeZoneTest {

    private static final long GMT_PLUS_4 = 4 * 60 * 60 * 1000;

    private static final long GMT_PLUS_10 = 10 * 60 * 60 * 1000;

    private static final long GMT_MINUS_10 = -10 * 60 * 60 * 1000;

    private static final Logger LOG = LoggerFactory.getLogger(TimeZoneTest.class);

    private static TimeZone timezoneFor(String timezoneId) {
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        return registry.getTimeZone(timezoneId);
    }

    /**
     * Assert the zone info id is the same as the Java timezone.
     */
    @ParameterizedTest(name = "getId [{0}]")
    @MethodSource("getIdData")
    public void testGetId(String timezoneId, String expectedTimezoneId) {
        TimeZone timezone = timezoneFor(timezoneId);
        assertNotNull(timezone.getID());
        if (expectedTimezoneId != null) {
            assertEquals(expectedTimezoneId, timezone.getID());
        }
    }

    static Stream<Arguments> getIdData() {
        return Stream.of(
                Arguments.of("Australia/Melbourne", null),
                Arguments.of("US/Mountain", "America/Denver"),
                Arguments.of("Asia/Calcutta", "Asia/Kolkata")
        );
    }

    /**
     * Assert the zone info name is the same as the Java timezone.
     */
    @ParameterizedTest(name = "getDisplayName [{0}]")
    @MethodSource("getDisplayNameData")
    public void testGetDisplayName(String timezoneId) {
        TimeZone timezone = timezoneFor(timezoneId);
        assertNotNull(timezone.getDisplayName());
    }

    static Stream<Arguments> getDisplayNameData() {
        return Stream.of(
                Arguments.of("Australia/Melbourne")
        );
    }

    /**
     * Assert the zone info name is the same as the Java timezone.
     */
    @ParameterizedTest(name = "getDisplayNameShort [{0}]")
    @MethodSource("getDisplayNameShortData")
    public void testGetDisplayNameShort(String timezoneId) {
        TimeZone timezone = timezoneFor(timezoneId);
        assertNotNull(timezone.getDisplayName(false, java.util.TimeZone.SHORT));
    }

    static Stream<Arguments> getDisplayNameShortData() {
        return Stream.of(
                Arguments.of("Australia/Melbourne")
        );
    }

    /**
     * Assert the raw offset is the same as its Java equivalent.
     */
    @ParameterizedTest(name = "getRawOffset [{0}]")
    @MethodSource("getRawOffsetData")
    public void testGetRawOffset(String timezoneId, long expectedRawOffset) {
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone(timezoneId);
        TimeZone timezone = timezoneFor(timezoneId);
        assertEquals(expectedRawOffset, timezone.getRawOffset());
        assertEquals(tz.getRawOffset(), timezone.getRawOffset());
    }

    static Stream<Arguments> getRawOffsetData() {
        return Stream.of(
                Arguments.of("Australia/Melbourne", GMT_PLUS_10),
                Arguments.of("Pacific/Honolulu", GMT_MINUS_10),
                Arguments.of("Europe/Samara", GMT_PLUS_4)
        );
    }

    /**
     * Assert the zone info has the same rules as its Java equivalent.
     */
    @ParameterizedTest(name = "hasSameRules [{0}]")
    @MethodSource("hasSameRulesData")
    public void testHasSameRules(String timezoneId) {
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone(timezoneId);
        TimeZone timezone = timezoneFor(timezoneId);
        assertTrue(timezone.hasSameRules(tz));
    }

    static Stream<Arguments> hasSameRulesData() {
        return Stream.of(
                Arguments.of("Australia/Melbourne")
        );
    }

    /**
     * A test to ensure the method TimeZone.inDaylightTime() is working correctly (for the last 10 years).
     */
    @ParameterizedTest(name = "inDaylightTime [{0}]")
    @MethodSource("inDaylightTimeData")
    public void testInDaylightTime(String timezoneId, Date date, boolean expectedInDaylight) {
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone(timezoneId);
        TimeZone timezone = timezoneFor(timezoneId);
        if (date != null) {
            assertEquals(expectedInDaylight, timezone.inDaylightTime(date));
        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -10);
            long start, stop;
            for (int y = 0; y < 10; y++) {
                cal.clear(Calendar.DAY_OF_YEAR);
                for (int i = 0; i < 365; i++) {
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    start = System.currentTimeMillis();
                    assertEquals(tz.inDaylightTime(cal.getTime()), timezone
                            .inDaylightTime(cal.getTime()), "inDaylightTime() invalid: [" + cal.getTime() + "]");
                    stop = System.currentTimeMillis();
                    LOG.debug("Time: " + (stop - start) + "ms");
                }
            }
        }
    }

    static Stream<Arguments> inDaylightTimeData() {
        Calendar cal = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
        cal.set(2010, 9, 30, 4, 0, 0);
        Date parisDate = cal.getTime();
        cal.set(2002, 11, 04, 4, 0, 0);
        Date bahiaDate = cal.getTime();
        return Stream.of(
                Arguments.of("Europe/Samara", null, false),
                Arguments.of("Europe/Paris", parisDate, true),
                Arguments.of("America/Bahia", bahiaDate, true)
        );
    }

    /**
     * Ensure useDaylightTime() method is working correctly.
     */
    @ParameterizedTest(name = "useDaylightTime [{0}]")
    @MethodSource("useDaylightTimeData")
    public void testUseDaylightTime(String timezoneId, boolean expectedUseDaylightTime) {
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone(timezoneId);
        TimeZone timezone = timezoneFor(timezoneId);
        assertEquals(expectedUseDaylightTime, timezone.useDaylightTime());
        assertEquals(tz.useDaylightTime(), timezone.useDaylightTime());
    }

    static Stream<Arguments> useDaylightTimeData() {
        return Stream.of(
                Arguments.of("Australia/Melbourne", true),
                Arguments.of("Africa/Abidjan", false)
        );
    }

    /**
     * Assert getOffset() returns the same result as its Java timezone equivalent.
     */
    @ParameterizedTest(name = "getOffset [{0}]")
    @MethodSource("getOffsetData")
    public void testGetOffset(String timezoneId, Date date, long expectedOffset) {
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone(timezoneId);
        TimeZone timezone = timezoneFor(timezoneId);
        if (date != null) {
            assertEquals(expectedOffset, timezone.getOffset(date.getTime()));
            assertEquals(tz.getOffset(date.getTime()), timezone.getOffset(date.getTime()));
        } else {
            int era = GregorianCalendar.AD;
            int year = 2015;
            int month = 9;
            int day = 18;
            int dayOfWeek = Calendar.SUNDAY;
            int millisecods = 0;
            assertEquals(tz.getOffset(era, year, month, day, dayOfWeek, millisecods),
                    timezone.getOffset(era, year, month, day, dayOfWeek, millisecods));
        }
    }

    static Stream<Arguments> getOffsetData() {
        return Stream.of(
                Arguments.of("Europe/Samara", null, GMT_PLUS_4),
                Arguments.of("Australia/Melbourne", null, 0L),
                Arguments.of("Pacific/Honolulu", new Date(), GMT_MINUS_10)
        );
    }

    /**
     * Test custom DST savings implementation.
     */
    @ParameterizedTest(name = "getDSTSavings [{0}]")
    @MethodSource("getDSTSavingsData")
    public void testGetDSTSavings(String timezoneId, int expectedDstSavings) {
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone(timezoneId);
        TimeZone timezone = timezoneFor(timezoneId);
        assertEquals(expectedDstSavings, timezone.getDSTSavings());
        assertEquals(tz.getDSTSavings(), timezone.getDSTSavings());
    }

    static Stream<Arguments> getDSTSavingsData() {
        return Stream.of(
                Arguments.of("Australia/Melbourne", 3600000)
        );
    }

    @ParameterizedTest(name = "zuluToLocal [{1}]")
    @MethodSource("zuluToLocalData")
    public void testZuluToLocal(String vtimezoneDef, String zuluDateTimeStr,
                                String expectedLocalDateTimeStr) throws Exception {
        net.fortuna.ical4j.model.Calendar cal = new CalendarBuilder().build(new StringReader(vtimezoneDef));
        List<VTimeZone> vtz = cal.getComponents(VTimeZone.VTIMEZONE);
        TimeZone timezone = new TimeZone(vtz.get(0));
        DateTime d = new DateTime(zuluDateTimeStr);
        d.setTimeZone(timezone);
        assertEquals(expectedLocalDateTimeStr, d.toString());
    }

    static Stream<Arguments> zuluToLocalData() {
        String minskDefinition =
                "BEGIN:VCALENDAR\r\n"
                        + "BEGIN:VTIMEZONE\r\n"
                        + "TZID:Europe/Minsk\r\n"
                        + "X-S1CS-TZID-ALIAS:E. Europe Standard Time\r\n"
                        + "BEGIN:STANDARD\r\n"
                        + "TZOFFSETFROM:+0200\r\n"
                        + "TZOFFSETTO:+0300\r\n"
                        + "TZNAME:FET\r\n"
                        + "DTSTART:20110327T020000\r\n"
                        + "RDATE:20110327T020000\r\n"
                        + "END:STANDARD\r\n"
                        + "BEGIN:DAYLIGHT\r\n"
                        + "TZOFFSETFROM:+0200\r\n"
                        + "TZOFFSETTO:+0300\r\n"
                        + "TZNAME:EEST\r\n"
                        + "DTSTART:19920329T000000\r\n"
                        + "RDATE:20020331T020000\r\n"
                        + "RDATE:20030330T020000\r\n"
                        + "RDATE:20040328T020000\r\n"
                        + "RDATE:20050327T020000\r\n"
                        + "RDATE:20060326T020000\r\n"
                        + "RDATE:20070325T020000\r\n"
                        + "RDATE:20080330T020000\r\n"
                        + "RDATE:20090329T020000\r\n"
                        + "RDATE:20100328T020000\r\n"
                        + "END:DAYLIGHT\r\n"
                        + "BEGIN:STANDARD\r\n"
                        + "TZOFFSETFROM:+0300\r\n"
                        + "TZOFFSETTO:+0200\r\n"
                        + "TZNAME:EET\r\n"
                        + "DTSTART:19910929T030000\r\n"
                        + "RDATE:20021027T030000\r\n"
                        + "RDATE:20031026T030000\r\n"
                        + "RDATE:20041031T030000\r\n"
                        + "RDATE:20051030T030000\r\n"
                        + "RDATE:20061029T030000\r\n"
                        + "RDATE:20071028T030000\r\n"
                        + "RDATE:20081026T030000\r\n"
                        + "RDATE:20091025T030000\r\n"
                        + "RDATE:20101031T030000\r\n"
                        + "END:STANDARD\r\n"
                        + "END:VTIMEZONE\r\n"
                        + "END:VCALENDAR\r\n";

        String weirdo = "BEGIN:VCALENDAR\n"
                + "BEGIN:VTIMEZONE\n"
                + "TZID:GMT +0100 (Standard) / GMT +0200 (Daylight)\n"
                + "BEGIN:DAYLIGHT\n"
                + "TZOFFSETTO:+020000\n"
                + "TZOFFSETFROM:+010000\n"
                + "DTSTART:20130331T030000\n"
                + "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\n"
                + "END:DAYLIGHT\n"
                + "BEGIN:STANDARD\n"
                + "TZOFFSETTO:+010000\n"
                + "TZOFFSETFROM:+020000\n"
                + "DTSTART:20131027T020000\n"
                + "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU\n"
                + "END:STANDARD\n"
                + "END:VTIMEZONE\n"
                + "END:VCALENDAR\n";

        return Stream.of(
                Arguments.of(minskDefinition, "20100428T140000Z", "20100428T170000"),
                Arguments.of(weirdo, "20130618T150000Z", "20130618T170000")
        );
    }
}
