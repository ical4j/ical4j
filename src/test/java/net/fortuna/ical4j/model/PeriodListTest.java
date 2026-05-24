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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created on 13/02/2005
 *
 * $Id$
 *
 * @author Ben Fortuna
 */
public class PeriodListTest {

    private final Logger log = LoggerFactory.getLogger(PeriodListTest.class);

    /**
     *
     */
    @ParameterizedTest(name = "equals")
    @MethodSource("equalsData")
    public void testEquals(PeriodList<LocalDate> periodList, PeriodList<LocalDate> expectedPeriodList) {
        assertEquals(expectedPeriodList, periodList);
    }

    static Stream<Arguments> equalsData() {
        // create ranges that are intervals
        LocalDate begin1994 = LocalDate.now().withYear(1994).withMonth(1).withDayOfMonth(1);
        LocalDate end1994 = begin1994.withMonth(12).withDayOfMonth(31);
        LocalDate jan1994 = end1994.withMonth(1).withDayOfMonth(22);
        LocalDate feb1994 = jan1994.withMonth(2).withDayOfMonth(15);
        LocalDate mar1994 = feb1994.withMonth(3).withDayOfMonth(4);
        LocalDate apr1994 = mar1994.withMonth(4).withDayOfMonth(12);
        LocalDate may1994 = apr1994.withMonth(5).withDayOfMonth(19);
        LocalDate jun1994 = may1994.withMonth(6).withDayOfMonth(21);
        LocalDate jul1994 = jun1994.withMonth(7).withDayOfMonth(28);
        LocalDate aug1994 = jul1994.withMonth(8).withDayOfMonth(20);
        LocalDate sep1994 = aug1994.withMonth(9).withDayOfMonth(17);
        LocalDate oct1994 = sep1994.withMonth(10).withDayOfMonth(29);
        LocalDate nov1994 = oct1994.withMonth(11).withDayOfMonth(11);
        LocalDate dec1994 = nov1994.withMonth(12).withDayOfMonth(2);

        Period<LocalDate> monthJanuary = new Period<>(jan1994, feb1994);
        Period<LocalDate> monthFebruary = new Period<>(feb1994, mar1994);
        Period<LocalDate> monthMarch = new Period<>(mar1994, apr1994);
        Period<LocalDate> monthApril = new Period<>(apr1994, may1994);
        Period<LocalDate> monthMay = new Period<>(may1994, jun1994);
        Period<LocalDate> monthJune = new Period<>(jun1994, jul1994);
        Period<LocalDate> monthJuly = new Period<>(jul1994, aug1994);
        Period<LocalDate> monthAugust = new Period<>(aug1994, sep1994);
        Period<LocalDate> monthSeptember = new Period<>(sep1994, oct1994);
        Period<LocalDate> monthOctober = new Period<>(oct1994, nov1994);
        Period<LocalDate> monthNovember = new Period<>(nov1994, dec1994);
        Period<LocalDate> monthDecember = new Period<>(dec1994, end1994);
        Period<LocalDate> head1994 = new Period<>(begin1994, jan1994);
        Period<LocalDate> tail1994 = new Period<>(dec1994, end1994);

        // create sets that contain the ranges
        List<Period<LocalDate>> oddMonths = new ArrayList<>();
        oddMonths.add(monthJanuary);
        oddMonths.add(monthMarch);
        oddMonths.add(monthMay);
        oddMonths.add(monthJuly);
        oddMonths.add(monthSeptember);
        oddMonths.add(monthNovember);
        List<Period<LocalDate>> tailSet = new ArrayList<>();
        tailSet.add(tail1994);

        PeriodList<LocalDate> evenMonths = new PeriodList<LocalDate>(CalendarDateFormat.DATE_FORMAT)
            .add(monthFebruary)
            .add(monthApril)
            .add(monthJune)
            .add(monthAugust)
            .add(monthOctober)
            .add(monthDecember);

        PeriodList<LocalDate> headSet = new PeriodList<LocalDate>(CalendarDateFormat.DATE_FORMAT)
            .add(head1994);

        PeriodList<LocalDate> empty1 = new PeriodList<>(CalendarDateFormat.DATE_FORMAT);
        PeriodList<LocalDate> empty2 = new PeriodList<>(CalendarDateFormat.DATE_FORMAT);

        return Stream.of(
                Arguments.of(evenMonths.subtract(null), evenMonths),
                Arguments.of(empty1.subtract(empty2), empty1),
                Arguments.of(headSet.subtract(empty1), headSet),
                Arguments.of(evenMonths.subtract(empty1), evenMonths)
        );
    }

    /**
     * Test timezone functionality.
     */
    @Test
    public void testTimezone() {
        PeriodList<Instant> list = new PeriodList<>(CalendarDateFormat.UTC_DATE_TIME_FORMAT);

        for (int i = 0; i < 5; i++) {
            Instant start = Instant.now();
            Instant end = start.plusSeconds(ChronoUnit.DAYS.getDuration().getSeconds());

            list = list.add(new Period<>(start, end));
        }

        log.info("Timezone test - period list: [" + list + "]");

        list.getPeriods().forEach(p -> {
            assertTrue(p.toString().endsWith("Z"));
            assertTrue(p.toString().endsWith("Z"));
        });
    }

    /**
     * Unit tests for {@link PeriodList#normalise()}.
     */
    @Test
    public void testNormalise() {
        // test a list of periods consuming no time..
        ZonedDateTime start = ZonedDateTime.now();

        PeriodList<ZonedDateTime> periods = new PeriodList<ZonedDateTime>()
            .add(new Period<>(start, start))
            .add(new Period<>(start, start));

        assertTrue(periods.normalise().getPeriods().isEmpty());
    }
}
