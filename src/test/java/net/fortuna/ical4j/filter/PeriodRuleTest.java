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
package net.fortuna.ical4j.filter;

import junit.framework.TestSuite;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.util.CompatibilityHints;
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

/**
 * $Id$
 * <p/>
 * Created on 2/02/2006
 * <p/>
 * Unit tests for the period filter rule.
 *
 * @author Ben Fortuna
 */
public class PeriodRuleTest extends FilterTest<CalendarComponent> {

    private static Logger LOG = LoggerFactory.getLogger(PeriodRuleTest.class);

    /**
     * @param testMethod
     * @param filter
     * @param collection
     * @param expectedFilteredSize
     */
    public PeriodRuleTest(String testMethod, Filter<CalendarComponent> filter,
                          Collection<CalendarComponent> collection, int expectedFilteredSize) {
        super(testMethod, filter, collection, expectedFilteredSize);
    }

    /**
     * @param testMethod
     * @param filter
     * @param collection
     */
    public PeriodRuleTest(String testMethod, Filter<CalendarComponent> filter,
                          Collection<CalendarComponent> collection) {
        super(testMethod, filter, collection);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }

    /**
     * Test handling of recurrence rules.
     * @throws ParserException
     * @throws IOException
     */
    /*
    public void testRecurrenceRules() throws ParserException, IOException {
        Calendar rCal = Calendars.load("etc/samples/valid/LH1.ics");
        Period period = new Period(new DateTime("20060831T000000Z"), new DateTime("20070831T230000Z")); 
        Filter filter = new Filter(new PeriodRule(period)); 
        Collection tz = rCal.getComponents(Component.VTIMEZONE); 
        Collection zz = filter.filter(rCal.getComponents(Component.VEVENT));
        
        assertTrue(!zz.isEmpty());
//        assertEquals(26, zz.size());
    }
    */

    /**
     * @return
     * @throws ParserException
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static TestSuite suite() throws FileNotFoundException, IOException, ParserException {
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(PeriodRuleTest.class.getResourceAsStream("/samples/valid/Australian_TV_Melbourne.ics"));

        TestSuite suite = new TestSuite();

        //testFilter..
        // April 1, 2004
        ZonedDateTime apr1 = ZonedDateTime.now().withYear(2004).withMonth(4).withDayOfMonth(1);
        // period of two weeks..
        Period<ZonedDateTime> period = new Period<>(apr1, java.time.Period.ofWeeks(2));
        Filter<CalendarComponent> filter = new Filter<>(new PeriodRule<>(period));
//        ComponentList filtered = (ComponentList) filter.filter(calendar.getComponents());
//        assertTrue(!filtered.isEmpty());
        suite.addTest(new PeriodRuleTest("testFilteredIsNotEmpty", filter, calendar.getComponents().getAll()));

        //testFilteringAllDayEvents..
        LocalDate jan25 = LocalDate.now().withMonth(1).withDayOfMonth(25);
        LocalDate jan26 = jan25.withDayOfMonth(26);

        VEvent event = new VEvent(jan25, jan26, "mid jan event");

        ComponentList<CalendarComponent> components = new ComponentList<>(Collections.singletonList(event));

        ZonedDateTime ruleDate = LocalDate.now().withMonth(1).withDayOfMonth(1).atStartOfDay().atZone(ZoneId.systemDefault());
        while (ruleDate.getMonth() == Month.JANUARY) {
            PeriodRule<CalendarComponent, ZonedDateTime> rule = new PeriodRule<>(new Period<>(ruleDate, java.time.Period.ofDays(1),
                    CalendarDateFormat.DATE_FORMAT));
            filter = new Filter<>(rule);
            if (ruleDate.getDayOfMonth() == 25) {
                suite.addTest(new PeriodRuleTest("testFilteredSize", filter, components.getAll(), 1));
            } else {
                suite.addTest(new PeriodRuleTest("testFilteredSize", filter, components.getAll(), 0));
            }
            ruleDate = ruleDate.plusDays(1);
        }

        // Test exclusion of particular dates..
        Calendar exCal = Calendars.load(PeriodRuleTest.class.getResource("/samples/invalid/friday13.ics"));
        ZonedDateTime startDt = ZonedDateTime.now().withYear(1997).withMonth(9).withDayOfMonth(2)
                .withHour(9).withMinute(0).withSecond(0);
        period = new Period<>(startDt, java.time.Period.ofWeeks(1));
        filter = new Filter<>(new PeriodRule<>(period));
        suite.addTest(new PeriodRuleTest("testFilteredIsEmpty", filter, exCal.getComponents().getAll()));

        // Test exclusion of particular date patterns..
        exCal = Calendars.load(PeriodRuleTest.class.getResource("/samples/invalid/friday13-NOT.ics"));
        period = new Period<>(startDt, java.time.Period.ofWeeks(52));
        filter = new Filter<>(new PeriodRule<>(period));
        suite.addTest(new PeriodRuleTest("testFilteredIsNotEmpty", filter, exCal.getComponents().getAll()));

        // Asia/Singapore test..
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        calendar = Calendars.load(PeriodRuleTest.class.getResource("/samples/valid/2207678.ics"));
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Singapore");

        ZonedDateTime day = ZonedDateTime.of(2008, 10, 31, 0, 0, 0, 0,
                TimeZone.getTimeZone("Etc/GMT").toZoneId());
        DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE;

        // friday..
        period = new Period<>(day, java.time.Duration.ofDays(1));
        LOG.info("period: " + period + " (" + dateFormat.format(period.getStart()) + ")");
        filter = new Filter<>(new PeriodRule<>(period));
        suite.addTest(new PeriodRuleTest("testFilteredIsNotEmpty", filter, calendar.getComponents(Component.VEVENT)));

        // saturday..
        day = day.plusDays(1);
        period = new Period<>(day, java.time.Duration.ofDays(1));
        LOG.info("period: " + period + " (" + dateFormat.format(period.getStart()) + ")");
        filter = new Filter<>(new PeriodRule<>(period));
        suite.addTest(new PeriodRuleTest("testFilteredIsEmpty", filter, calendar.getComponents(Component.VEVENT)));

        // friday..
        day = day.plusDays(6);
        period = new Period<>(day, java.time.Duration.ofDays(1));
        LOG.info("period: " + period + " (" + dateFormat.format(period.getStart()) + ")");
        filter = new Filter<>(new PeriodRule<>(period));
        suite.addTest(new PeriodRuleTest("testFilteredIsNotEmpty", filter, calendar.getComponents(Component.VEVENT)));

        // saturday..
        day = day.plusDays(1);
        period = new Period<>(day, java.time.Duration.ofDays(1));
        LOG.info("period: " + period + " (" + dateFormat.format(period.getStart()) + ")");
        filter = new Filter<>(new PeriodRule<>(period));
        suite.addTest(new PeriodRuleTest("testFilteredIsEmpty", filter, calendar.getComponents(Component.VEVENT)));

        return suite;
    }
}
