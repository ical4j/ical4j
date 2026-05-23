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

import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.TimeZones;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * $Id$
 * <p/>
 * Created on 30/06/2005
 *
 * @author Ben Fortuna
 */
public class DateTimeTest {

    private final Logger log = LoggerFactory.getLogger(DateTimeTest.class);

    private static final TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();

    @BeforeEach
    void setUp() {
        // ensure relaxing parsing is disabled for these tests..
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false);
    }

    @AfterEach
    void tearDown() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
    }

    /**
     *
     */
    @ParameterizedTest(name = "toString [{1}]")
    @MethodSource("toStringData")
    public void testToString(DateTime dateTime, String expectedToString) {
        assertNotNull(dateTime, "Null input date");
        assertEquals(expectedToString, dateTime.toString(), "Incorrect string representation");
    }

    static Stream<Arguments> toStringData() throws ParseException {
        Stream.Builder<Arguments> builder = Stream.builder();

        // test DateTime(long)..
        DateTime dt = new DateTime(0);
        dt.setUtc(true);
        builder.add(Arguments.of(dt, "19700101T000000Z"));

        // change default tz to non-UTC timezone.
        java.util.TimeZone originalTzDefault = java.util.TimeZone.getDefault();
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Australia/Melbourne"));

        // test DateTime(Date)..
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1984);
        // months are zero-based..
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 34);
        builder.add(Arguments.of(new DateTime(cal.getTime()), "19840417T031534"));
        java.util.TimeZone.setDefault(originalTzDefault);

        TimeZone tz = registry.getTimeZone("Australia/Melbourne");
        // test DateTime(String)..
        builder.add(Arguments.of(new DateTime("20000827T030000", tz), "20000827T030000"));
        builder.add(Arguments.of(new DateTime("20070101T080000", tz), "20070101T080000"));
        builder.add(Arguments.of(new DateTime("20050630T093000", tz), "20050630T093000"));
        builder.add(Arguments.of(new DateTime("20050630T093000Z"), "20050630T093000Z"));
        builder.add(Arguments.of(new DateTime("19390901T000000", tz), "19390901T000000"));

        builder.add(Arguments.of(new DateTime("20000402T020000", tz), "20000402T020000"));
        builder.add(Arguments.of(new DateTime("20000402T020000", tz), "20000402T020000"));

        DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        cal.clear();
        cal.set(2000, 0, 1, 2, 0, 0);
        for (int i = 0; i < 365; i++) {
            String dateString = df.format(cal.getTime());
            builder.add(Arguments.of(new DateTime(dateString), dateString));
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        builder.add(Arguments.of(new DateTime("20071104T000000",
                registry.getTimeZone("America/Los_Angeles")), "20071104T000000"));

        return builder.build();
    }

    /*
     * Class under test for void DateTime(String)
     */
    @Test
    public void testDateTimeString() throws Exception {
        try {
            new DateTime("20050630");
            fail("Should throw ParseException");
        } catch (ParseException pe) {
            log.info("Exception occurred: " + pe.getMessage());
        }
    }

    @ParameterizedTest(name = "relaxed [{1}]")
    @MethodSource("relaxedData")
    public void testRelaxed(String badlyFormated, String expectedToString) throws Exception {

        try {
            new DateTime(badlyFormated);
            fail("expected ParseException");
        } catch (ParseException pe) {
        }
        try {
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
            DateTime dt = new DateTime(badlyFormated);
            assertEquals(expectedToString, dt.toString());
        } catch (ParseException pe) {
            fail("exception not expected with relaxed parsing is used");
        }
    }

    static Stream<Arguments> relaxedData() {
        return Stream.of(
                Arguments.of("00001231T000000Z", "00011231T000000")
        );
    }

    /**
     * Test equality of DateTime instances created using different constructors.
     *
     * @throws ParseException
     */
    @Test
    public void testDateTimeEquals() throws ParseException {
        // change default tz to non-UTC timezone.
        java.util.TimeZone originalTzDefault = java.util.TimeZone.getDefault();
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Australia/Melbourne"));

        DateTime date1 = new DateTime("20050101T093000");

        Calendar calendar = Calendar.getInstance(); //TimeZone.getTimeZone("Etc/UTC"));
        calendar.clear();
        calendar.set(2005, 0, 1, 9, 30, 00);
        calendar.set(Calendar.MILLISECOND, 1);
        DateTime date2 = new DateTime(calendar.getTime());

        assertEquals(date1.hashCode(), date2.hashCode());
        assertEquals(date1.toString(), date2.toString());
        assertEquals(date1, date2);

        java.util.TimeZone.setDefault(originalTzDefault);
    }

    /**
     * Test that equality of two DateTime instances created using different constructors
     * implies equality of hashCode.
     *
     * @throws ParseException
     */
    @Test
    public void testDateTimeHashCode() throws ParseException {
        TimeZone tz1 = TimeZoneRegistryFactory.getInstance().createRegistry().getTimeZone("Europe/Paris");
        TimeZone tz2 = (TimeZone) tz1.clone();
        DateTime date1 = new DateTime("20050101T093000", tz1);
        DateTime date2 = new DateTime("20050101T093000", tz2);
        // verify that if equals() == true, hashCode must match also
        // was not the case previously as hashCode() was taking the TimeZone object
        // into consideration when equals() was not.
        assertEquals(date1, date2);
        assertEquals(date1.hashCode(), date2.hashCode());
    }

    /**
     * Test UTC date-times.
     */
    @Test
    public void testUtc() throws ParseException {
        // ordinary date..
        DateTime date1 = new DateTime("20050101T093000");
        assertFalse(date1.isUtc());

        // UTC date..
        DateTime date2 = new DateTime(true);
        assertTrue(date2.isUtc());

        TimeZone utcTz = registry.getTimeZone(TimeZones.UTC_ID);
        utcTz.setID(TimeZones.UTC_ID);

        // UTC timezone, but not UTC..
        DateTime date3 = new DateTime("20050101T093000", utcTz);
//        date3.setUtc(false);
        assertFalse(date3.isUtc());

        DateTime date4 = new DateTime();
        date4.setUtc(true);
        assertTrue(date4.isUtc());
        date4.setUtc(false);
        assertFalse(date4.isUtc());

        DateTime date5 = new DateTime(false);
        date5.setTimeZone(utcTz);
        assertFalse(date5.isUtc());
    }
}
