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
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * $Id$
 *
 * Created on 20/06/2005
 *
 * @author Ben
 *
 */
public class DurTest {

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
    @ParameterizedTest(name = "toString [{1}]")
    @MethodSource("toStringData")
    public void testToString(Dur duration, String expectedString) {
        assertEquals(expectedString, duration.toString());
    }

    static Stream<Arguments> toStringData() throws ParseException {
        TimeZoneRegistry tzreg = new DefaultTimeZoneRegistryFactory().createRegistry();

        Calendar cal = Calendar.getInstance();
        cal.set(2005, 7, 1);
        Date start = cal.getTime();

        cal.add(Calendar.YEAR, 1);
        Dur dur365D = new Dur(start, cal.getTime());

        cal.setTime(start);
        cal.add(Calendar.WEEK_OF_YEAR, -5);
        Dur durMinus5W = new Dur(start, cal.getTime());

        cal.setTime(start);
        cal.add(Calendar.DAY_OF_WEEK, 11);
        Dur dur11D = new Dur(start, cal.getTime());

        cal.setTime(start);
        cal.add(Calendar.HOUR_OF_DAY, 25);
        Dur dur1DT1H = new Dur(start, cal.getTime());

        cal.setTime(start);
        cal.add(Calendar.MINUTE, -23);
        Dur durMinus23M = new Dur(start, cal.getTime());

        cal.setTime(start);
        cal.add(Calendar.SECOND, -5);
        Dur durMinus5S = new Dur(start, cal.getTime());

        cal.setTime(start);
        cal.add(Calendar.HOUR_OF_DAY, 25);
        cal.add(Calendar.MINUTE, -23);
        cal.add(Calendar.SECOND, -5);
        Dur dur1DT36M55S = new Dur(start, cal.getTime());

        cal.setTime(start);
        cal.add(Calendar.YEAR, -2);
        cal.add(Calendar.WEEK_OF_YEAR, 11);
        Dur durMinus654D = new Dur(start, cal.getTime());

        // test adjacent weeks..
        ZonedDateTime newstart = ZonedDateTime.now(TimeZoneRegistry.getGlobalZoneId("America/Los_Angeles"))
                .withYear(2005).withMonth(1).withDayOfMonth(1).withHour(12).withMinute(0);
        ParameterList tzParams = new ParameterList(Collections.singletonList(new TzId(newstart.getZone().getId())));
        DtStart<ZonedDateTime> dtStart = new DtStart<>(tzParams, newstart);
        DtEnd<ZonedDateTime> dtEnd = new DtEnd<>(newstart.withDayOfMonth(2).withHour(11).withMinute(59));
        Dur durAdjacent = new Dur(new DateTime(Date.from(dtStart.getDate().toInstant())), Date.from(dtEnd.getDate().toInstant()));

        // test accross Europe/Paris DST boundary should not matter
        Date startLA = new net.fortuna.ical4j.model.DateTime("20110326T110000", tzreg.getTimeZone("America/Los_Angeles"));
        DateTime endLA = new net.fortuna.ical4j.model.DateTime("20110327T110000", tzreg.getTimeZone("America/Los_Angeles"));
        Dur durCrossDST = new Dur(startLA, endLA);

        // test cross-year..
        Dur durCrossYear = new Dur(new net.fortuna.ical4j.model.Date("20061231"),
                new net.fortuna.ical4j.model.Date("20070101"));

        return Stream.of(
                Arguments.of(new Dur("PT15M"), "PT15M"),
                Arguments.of(new Dur(33), "P33W"),
                Arguments.of(dur365D, "P365D"),
                Arguments.of(durMinus5W, "-P5W"),
                Arguments.of(dur11D, "P11D"),
                Arguments.of(dur1DT1H, "P1DT1H"),
                Arguments.of(durMinus23M, "-PT23M"),
                Arguments.of(durMinus5S, "-PT5S"),
                Arguments.of(dur1DT36M55S, "P1DT36M55S"),
                Arguments.of(durMinus654D, "-P654D"),
                Arguments.of(durAdjacent, "PT23H59M"),
                Arguments.of(durCrossDST, "P1D"),
                Arguments.of(durCrossYear, "P1D"),
                // test negative duration..
                Arguments.of(new Dur(-1), "-P1W"),
                Arguments.of(new Dur(-1, 0, 0, 0), "-P1D"),
                Arguments.of(new Dur(0, -1, 0, 0), "-PT1H"),
                Arguments.of(new Dur(0, 0, -1, 0), "-PT1M"),
                Arguments.of(new Dur(0, 0, 0, -1), "-PT1S"),
                Arguments.of(new Dur(-1, 0, 0, -1), "-P1DT1S")
        );
    }

    /**
     *
     */
    @ParameterizedTest(name = "getTime [{0}]")
    @MethodSource("getTimeData")
    public void testGetTime(Dur duration, Date startTime, Date expectedTime) {
        assertEquals(expectedTime, duration.getTime(startTime));
    }

    static Stream<Arguments> getTimeData() throws ParseException {
        TimeZoneRegistry tzreg = new DefaultTimeZoneRegistryFactory().createRegistry();

        Calendar cal = Calendar.getInstance();
        Date startTime = cal.getTime();
        cal.add(Calendar.MINUTE, 15);
        Date expected15M = cal.getTime();

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2011);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 26);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startTime2 = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 27);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        Date expected1D = cal.getTime();

        return Stream.of(
                Arguments.of(new Dur("PT15M"), startTime, expected15M),
                Arguments.of(new Dur("P1D"), startTime2, expected1D),
                // tests around various Daylight Saving Times
                // EST change on 20110327T020000
                Arguments.of(new Dur("P1D"),
                        new DateTime("20110326T200000", tzreg.getTimeZone("Europe/Paris")),
                        new DateTime("20110327T200000", tzreg.getTimeZone("Europe/Paris"))),
                Arguments.of(new Dur("P1D"),
                        new DateTime("20110326T110000", tzreg.getTimeZone("America/Los_Angeles")),
                        new DateTime("20110327T110000", tzreg.getTimeZone("America/Los_Angeles"))),
                // PST change on 20110313T020000
                Arguments.of(new Dur("P1D"),
                        new DateTime("20110312T200000", tzreg.getTimeZone("America/Los_Angeles")),
                        new DateTime("20110313T200000", tzreg.getTimeZone("America/Los_Angeles"))),
                Arguments.of(new Dur("P1D"),
                        new DateTime("20110312T200000", tzreg.getTimeZone("Europe/Paris")),
                        new DateTime("20110313T200000", tzreg.getTimeZone("Europe/Paris")))
        );
    }

    /**
     *
     */
    @ParameterizedTest(name = "compareToGreater [{0}]")
    @MethodSource("compareToGreaterData")
    public void testCompareToGreater(Dur duration, Dur duration2) {
        assertTrue(duration.compareTo(duration2) > 0);
    }

    static Stream<Arguments> compareToGreaterData() {
        return Stream.of(
                Arguments.of(new Dur(1), new Dur(-1)),
                Arguments.of(new Dur(0, 0, 0, 3), new Dur(0, 0, 0, -5)),
                Arguments.of(new Dur(0, 0, 0, 5), new Dur(0, 0, 0, 3)),
                Arguments.of(new Dur(0, 0, 0, -3), new Dur(0, 0, 0, -5))
        );
    }
}
