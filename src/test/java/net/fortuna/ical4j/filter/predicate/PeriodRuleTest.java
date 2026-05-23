/**
 * Copyright (c) 2004-2021, Ben Fortuna
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
package net.fortuna.ical4j.filter.predicate;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.FilterTest;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.TimeZone;
import java.util.stream.Stream;

/**
 * $Id$
 * <p/>
 * Created on 2/02/2006
 * <p/>
 * Unit tests for the period filter rule.
 *
 * @author Ben Fortuna
 */
public class PeriodRuleTest {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodRuleTest.class);

    @AfterEach
    void tearDown() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }

    @ParameterizedTest(name = "filteredIsEmpty")
    @MethodSource("filteredIsEmptyData")
    public void testFilteredIsEmpty(Filter<CalendarComponent> filter, Collection<CalendarComponent> collection) {
        FilterTest.assertFilteredIsEmpty(filter, collection);
    }

    @ParameterizedTest(name = "filteredIsNotEmpty")
    @MethodSource("filteredIsNotEmptyData")
    public void testFilteredIsNotEmpty(Filter<CalendarComponent> filter, Collection<CalendarComponent> collection) {
        FilterTest.assertFilteredIsNotEmpty(filter, collection);
    }

    @ParameterizedTest(name = "filteredSize [{2}]")
    @MethodSource("filteredSizeData")
    public void testFilteredSize(Filter<CalendarComponent> filter, Collection<CalendarComponent> collection,
                                 int expectedFilteredSize) {
        FilterTest.assertFilteredSize(filter, collection, expectedFilteredSize);
    }

    static Stream<Arguments> filteredIsNotEmptyData() throws IOException, ParserException {
        Stream.Builder<Arguments> rows = Stream.builder();

        // April 1, 2004 — two-week period — Australian TV Melbourne calendar
        CalendarBuilder builder = new CalendarBuilder();
        Calendar australianTv = builder.build(PeriodRuleTest.class.getResourceAsStream(
                "/samples/valid/Australian_TV_Melbourne.ics"));
        ZonedDateTime apr1 = ZonedDateTime.now().withYear(2004).withMonth(4).withDayOfMonth(1);
        Period<ZonedDateTime> twoWeeks = new Period<>(apr1, java.time.Period.ofWeeks(2));
        rows.add(Arguments.of(new Filter<>(new PeriodRule<>(twoWeeks)), australianTv.getComponents()));

        // friday13-NOT calendar over 52 weeks
        Calendar exCal = Calendars.load(PeriodRuleTest.class.getResource("/samples/valid/friday13-NOT.ics"));
        ZonedDateTime startDt = ZonedDateTime.now().withYear(1997).withMonth(9).withDayOfMonth(2)
                .withHour(9).withMinute(0).withSecond(0);
        Period<ZonedDateTime> fiftyTwoWeeks = new Period<>(startDt, java.time.Period.ofWeeks(52));
        rows.add(Arguments.of(new Filter<>(new PeriodRule<>(fiftyTwoWeeks)), exCal.getComponents()));

        // Asia/Singapore: load with relaxed unfolding
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        Calendar singaporeCal = Calendars.load(PeriodRuleTest.class.getResource("/samples/valid/2207678.ics"));
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);

        DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE;
        ZonedDateTime day = ZonedDateTime.of(2008, 10, 31, 0, 0, 0, 0,
                TimeZone.getTimeZone("Etc/GMT").toZoneId());

        // friday
        Period<ZonedDateTime> oneDay = new Period<>(day, java.time.Duration.ofDays(1));
        LOG.info("period: " + oneDay + " (" + dateFormat.format(oneDay.getStart()) + ")");
        rows.add(Arguments.of(new Filter<>(new PeriodRule<>(oneDay)),
                singaporeCal.getComponents(Component.VEVENT)));

        // friday a week later
        day = day.plusDays(7);
        Period<ZonedDateTime> oneDay2 = new Period<>(day, java.time.Duration.ofDays(1));
        LOG.info("period: " + oneDay2 + " (" + dateFormat.format(oneDay2.getStart()) + ")");
        rows.add(Arguments.of(new Filter<>(new PeriodRule<>(oneDay2)),
                singaporeCal.getComponents(Component.VEVENT)));

        return rows.build();
    }

    static Stream<Arguments> filteredIsEmptyData() throws IOException, ParserException {
        Stream.Builder<Arguments> rows = Stream.builder();

        // friday13 sample over one week starting 1997-09-02
        Calendar exCal = Calendars.load(PeriodRuleTest.class.getResource("/samples/valid/friday13.ics"));
        ZonedDateTime startDt = ZonedDateTime.now().withYear(1997).withMonth(9).withDayOfMonth(2)
                .withHour(9).withMinute(0).withSecond(0);
        Period<ZonedDateTime> oneWeek = new Period<>(startDt, java.time.Period.ofWeeks(1));
        rows.add(Arguments.of(new Filter<>(new PeriodRule<>(oneWeek)), exCal.getComponents()));

        // Asia/Singapore: saturday filter -> no matching VEVENTs
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        Calendar singaporeCal = Calendars.load(PeriodRuleTest.class.getResource("/samples/valid/2207678.ics"));
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);

        DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE;
        ZonedDateTime day = ZonedDateTime.of(2008, 11, 1, 0, 0, 0, 0,
                TimeZone.getTimeZone("Etc/GMT").toZoneId());

        // saturday
        Period<ZonedDateTime> oneDay = new Period<>(day, java.time.Duration.ofDays(1));
        LOG.info("period: " + oneDay + " (" + dateFormat.format(oneDay.getStart()) + ")");
        rows.add(Arguments.of(new Filter<>(new PeriodRule<>(oneDay)),
                singaporeCal.getComponents(Component.VEVENT)));

        // saturday a week later
        day = day.plusDays(7);
        Period<ZonedDateTime> oneDay2 = new Period<>(day, java.time.Duration.ofDays(1));
        LOG.info("period: " + oneDay2 + " (" + dateFormat.format(oneDay2.getStart()) + ")");
        rows.add(Arguments.of(new Filter<>(new PeriodRule<>(oneDay2)),
                singaporeCal.getComponents(Component.VEVENT)));

        return rows.build();
    }

    static Stream<Arguments> filteredSizeData() {
        Stream.Builder<Arguments> rows = Stream.builder();

        // testFilteringAllDayEvents: a single all-day event on Jan 25; iterate every day in January,
        // expect 1 match on Jan 25 and 0 on every other day. Matches original suite()'s while-loop.
        LocalDate jan25 = LocalDate.now().withMonth(1).withDayOfMonth(25);
        LocalDate jan26 = jan25.withDayOfMonth(26);

        VEvent event = new VEvent(jan25, jan26, "mid jan event");
        ComponentList<CalendarComponent> components = new ComponentList<>(Collections.singletonList(event));

        ZonedDateTime ruleDate = LocalDate.now().withMonth(1).withDayOfMonth(1)
                .atStartOfDay().atZone(ZoneId.systemDefault());
        while (ruleDate.getMonth() == Month.JANUARY) {
            PeriodRule<CalendarComponent, ZonedDateTime> rule = new PeriodRule<>(
                    new Period<>(ruleDate, java.time.Period.ofDays(1), CalendarDateFormat.DATE_FORMAT));
            Filter<CalendarComponent> filter = new Filter<>(rule);
            if (ruleDate.getDayOfMonth() == 25) {
                rows.add(Arguments.of(filter, components.getAll(), 1));
            } else {
                rows.add(Arguments.of(filter, components.getAll(), 0));
            }
            ruleDate = ruleDate.plusDays(1);
        }

        return rows.build();
    }
}
