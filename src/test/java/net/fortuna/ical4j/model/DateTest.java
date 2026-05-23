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

import net.fortuna.ical4j.util.TimeZones;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.ParseException;
import java.util.Calendar;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * $Id$
 *
 * Created on 30/06/2005
 *
 * @author Ben Fortuna
 *
 */
public class DateTest {

    /**
     *
     */
    @ParameterizedTest(name = "toString [{0}]")
    @MethodSource("toStringData")
    public void testToString(Date date, String expectedString) {
        assertEquals(expectedString, date.toString());
    }

    static Stream<Arguments> toStringData() throws ParseException {
        Calendar cal = Calendar.getInstance(TimeZones.getDateTimeZone());
        cal.clear();
        cal.set(Calendar.YEAR, 1984);
        // months are zero-based..
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        Date date1984 = new Date(cal.getTime());

        Calendar calendar = Calendar.getInstance(TimeZones.getDateTimeZone());
        calendar.clear();
        calendar.set(2005, 0, 1);
        calendar.set(Calendar.MILLISECOND, 1);
        Date dateCalendar = new Date(calendar.getTime());

        return Stream.of(
                Arguments.of(new Date(0L), "19700101"),
                Arguments.of(date1984, "19840417"),
                Arguments.of(new Date("20050630"), "20050630"),
                // Test equality of Date instances created using different constructors..
                Arguments.of(dateCalendar, new Date("20050101").toString())
        );
    }

    /**
     *
     */
    @ParameterizedTest(name = "equals [{0}]")
    @MethodSource("equalsData")
    public void testEquals(Date date, java.util.Date date2) {
        assertEquals(date2, date);
    }

    static Stream<Arguments> equalsData() throws ParseException {
        Calendar calendar = Calendar.getInstance(TimeZones.getDateTimeZone());
        calendar.clear();
        calendar.set(2005, 0, 1);
        calendar.set(Calendar.MILLISECOND, 1);
        Date dateCalendar = new Date(calendar.getTime());

        calendar = Calendar.getInstance(TimeZones.getDateTimeZone());
        calendar.clear();
        calendar.set(2005, 0, 1);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        java.util.Date timeClean = calendar.getTime();

        return Stream.of(
                Arguments.of(dateCalendar, new Date("20050101")),
                Arguments.of(new Date("20050101"), timeClean)
        );
    }
}
