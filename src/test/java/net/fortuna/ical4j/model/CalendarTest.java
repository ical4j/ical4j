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
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created on 16/03/2005
 *
 * $Id$
 *
 * @author Ben
 *
 *         A test case for creating calendars.
 */
public class CalendarTest {

    private Calendar calendar;

    @Before
    public void setUp() {
        calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        VEvent vEvent = new VEvent();
        vEvent.getProperties().add(new Uid("1"));
        calendar.getComponents().add(vEvent);
    }

    @Test
    public void testValid() throws ValidationException {
        
        calendar.validate();
    }

    @Test
    public void testValid2() throws ParseException, IOException, URISyntaxException {

        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();

        VTimeZone tz = registry.getTimeZone("Australia/Melbourne").getVTimeZone();
        TzId tzParam = new TzId(tz.getProperty(Property.TZID).getValue());
        calendar.getComponents().add(tz);

        // Add events, etc..
        Calendar copyCalendar = new Calendar(calendar);
        java.util.Calendar calStart = java.util.Calendar.getInstance();
        calStart.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
        calStart.set(java.util.Calendar.HOUR_OF_DAY, 9);
        calStart.clear(java.util.Calendar.MINUTE);
        calStart.clear(java.util.Calendar.SECOND);

        java.util.Calendar calEnd = java.util.Calendar.getInstance();
        calEnd.setTime(calStart.getTime());
        calEnd.add(java.util.Calendar.YEAR, 1);

        VEvent week1UserA = new VEvent(new Date(calStart.getTime().getTime()),
                java.time.Duration.ofHours(8), "Week 1 - User A");
        week1UserA.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        week1UserA.getProperty(Property.DTSTART).getParameters().replace(Value.DATE);

        Recur week1UserARecur = new Recur(Recur.WEEKLY, new Date(calEnd.getTime().getTime()));
        week1UserARecur.setInterval(3);
        week1UserARecur.getDayList().add(WeekDay.MO);
        week1UserARecur.getDayList().add(WeekDay.TU);
        week1UserARecur.getDayList().add(WeekDay.WE);
        week1UserARecur.getDayList().add(WeekDay.TH);
        week1UserARecur.getDayList().add(WeekDay.FR);
        week1UserARecur.getHourList().add(new Integer(9));
        week1UserA.getProperties().add(new RRule(week1UserARecur));
        week1UserA.getProperties().add(new Uid("000001@modularity.net.au"));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);

        VEvent week2UserB = new VEvent(new Date(calStart.getTime().getTime()),
                java.time.Duration.ofHours(8), "Week 2 - User B");
        week2UserB.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        week2UserB.getProperty(Property.DTSTART).getParameters().replace(Value.DATE);

        Recur week2UserBRecur = new Recur(Recur.WEEKLY, new Date(calEnd.getTime().getTime()));
        week2UserBRecur.setInterval(3);
        week2UserBRecur.getDayList().add(WeekDay.MO);
        week2UserBRecur.getDayList().add(WeekDay.TU);
        week2UserBRecur.getDayList().add(WeekDay.WE);
        week2UserBRecur.getDayList().add(WeekDay.TH);
        week2UserBRecur.getDayList().add(WeekDay.FR);
        week2UserBRecur.getHourList().add(new Integer(9));
        week2UserB.getProperties().add(new RRule(week2UserBRecur));
        week2UserB.getProperties().add(new Uid("000002@modularity.net.au"));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);

        VEvent week3UserC = new VEvent(new Date(calStart.getTime().getTime()),
                java.time.Duration.ofHours(8), "Week 3 - User C");
        week3UserC.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        week3UserC.getProperty(Property.DTSTART).getParameters().replace(Value.DATE);

        Recur week3UserCRecur = new Recur(Recur.WEEKLY, new Date(calEnd.getTime().getTime()));
        week3UserCRecur.setInterval(3);
        week3UserCRecur.getDayList().add(WeekDay.MO);
        week3UserCRecur.getDayList().add(WeekDay.TU);
        week3UserCRecur.getDayList().add(WeekDay.WE);
        week3UserCRecur.getDayList().add(WeekDay.TH);
        week3UserCRecur.getDayList().add(WeekDay.FR);
        week3UserCRecur.getHourList().add(new Integer(9));
        week3UserC.getProperties().add(new RRule(week3UserCRecur));
        week3UserC.getProperties().add(new Uid("000003@modularity.net.au"));

        copyCalendar.getComponents().add(week1UserA);
        copyCalendar.getComponents().add(week2UserB);
        copyCalendar.getComponents().add(week3UserC);

        // test event date ranges..
        copyCalendar = new Calendar(calendar);

        calStart = java.util.Calendar.getInstance();
        calStart.set(java.util.Calendar.YEAR, 2006);
        calStart.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY);
        calStart.set(java.util.Calendar.DAY_OF_MONTH, 1);
        calStart.set(java.util.Calendar.HOUR_OF_DAY, 9);
        calStart.clear(java.util.Calendar.MINUTE);
        calStart.clear(java.util.Calendar.SECOND);

        calEnd = java.util.Calendar.getInstance();
        calEnd.setTime(calStart.getTime());
        calEnd.add(java.util.Calendar.YEAR, 1);

        week1UserA = new VEvent(new Date(calStart.getTime().getTime()),
                java.time.Duration.ofHours(8), "Week 1 - User A");
        week1UserA.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        week1UserA.getProperty(Property.DTSTART).getParameters().replace(Value.DATE);

        week1UserARecur = new Recur(Recur.WEEKLY, new Date(calEnd.getTime().getTime()));
        week1UserARecur.setInterval(3);
        week1UserARecur.getDayList().add(new WeekDay(WeekDay.MO, 0));
        week1UserARecur.getDayList().add(new WeekDay(WeekDay.TU, 0));
        week1UserARecur.getDayList().add(new WeekDay(WeekDay.WE, 0));
        week1UserARecur.getDayList().add(new WeekDay(WeekDay.TH, 0));
        week1UserARecur.getDayList().add(new WeekDay(WeekDay.FR, 0));
        week1UserARecur.getHourList().add(new Integer(9));
        week1UserA.getProperties().add(new RRule(week1UserARecur));
        week1UserA.getProperties().add(new Uid("000001@modularity.net.au"));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);

        week2UserB = new VEvent(new Date(calStart.getTime().getTime()),
                java.time.Duration.ofHours(8), "Week 2 - User B");
        week2UserB.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        week2UserB.getProperty(Property.DTSTART).getParameters().replace(Value.DATE);

        week2UserBRecur = new Recur(Recur.WEEKLY, new Date(calEnd.getTime().getTime()));
        week2UserBRecur.setInterval(3);
        week2UserBRecur.getDayList().add(new WeekDay(WeekDay.MO, 0));
        week2UserBRecur.getDayList().add(new WeekDay(WeekDay.TU, 0));
        week2UserBRecur.getDayList().add(new WeekDay(WeekDay.WE, 0));
        week2UserBRecur.getDayList().add(new WeekDay(WeekDay.TH, 0));
        week2UserBRecur.getDayList().add(new WeekDay(WeekDay.FR, 0));
        week2UserBRecur.getHourList().add(new Integer(9));
        week2UserB.getProperties().add(new RRule(week2UserBRecur));
        week2UserB.getProperties().add(new Uid("000002@modularity.net.au"));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);

        week3UserC = new VEvent(new Date(calStart.getTime().getTime()),
                java.time.Duration.ofHours(8), "Week 3 - User C");
        week3UserC.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        week3UserC.getProperty(Property.DTSTART).getParameters().replace(Value.DATE);

        week3UserCRecur = new Recur(Recur.WEEKLY, new Date(calEnd.getTime().getTime()));
        week3UserCRecur.setInterval(3);
        week3UserCRecur.getDayList().add(new WeekDay(WeekDay.MO, 0));
        week3UserCRecur.getDayList().add(new WeekDay(WeekDay.TU, 0));
        week3UserCRecur.getDayList().add(new WeekDay(WeekDay.WE, 0));
        week3UserCRecur.getDayList().add(new WeekDay(WeekDay.TH, 0));
        week3UserCRecur.getDayList().add(new WeekDay(WeekDay.FR, 0));
        week3UserCRecur.getHourList().add(new Integer(9));
        week3UserC.getProperties().add(new RRule(week3UserCRecur));
        week3UserC.getProperties().add(new Uid("000003@modularity.net.au"));

        copyCalendar.getComponents().add(week1UserA);
        copyCalendar.getComponents().add(week2UserB);
        copyCalendar.getComponents().add(week3UserC);
        copyCalendar.validate();

    }

    @Test
    public void shouldCorrectCalendarBody() throws IOException, ParserException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        String[] calendarNames = { "yahoo1.txt", "yahoo2.txt", "outlook1.txt", "outlook2.txt", "apple.txt" };
        for (String calendarName : calendarNames) {
            Calendar calendar = buildCalendar(calendarName);
            calendar.conformToRfc5545();
            try {
                calendar.validate();
            } catch (ValidationException e) {
                e.printStackTrace();
                fail("Validation failed for " + calendarName);
            }
        }
    }

    @Test
    public void shouldCorrectMsSpecificTimeZones() throws IOException, ParserException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        String actuals[] = { "timezones/outlook1.txt", "timezones/outlook2.txt" };
        String expecteds[] = { "timezones/outlook1_expected.txt", "timezones/outlook2_expected.txt" };

        for (int i = 0; i < actuals.length; i++) {
            Calendar actual = buildCalendar(actuals[i]);
            actual.conformToRfc5545();
            Calendar expected = buildCalendar(expecteds[i]);
            assertEquals("on from " + expecteds[i] + " and " + actuals[i] + " failed.", expected, actual);
        }
    }

    @Test
    public void shouldCorrectDTStampByAddingUTCTimezone() {
        String calendarName = "dtstamp/invalid.txt";
        try {
            Calendar actual = buildCalendar(calendarName);
            actual.conformToRfc5545();
        } catch (IllegalAccessException | InvocationTargetException | IOException | ParserException e) {
            e.printStackTrace();
            fail("RFC transformation failed for " + calendarName);
        }
    }

    @Test
    public void shouldSetTimezoneToUtcForNoTZdescription() {
        String actualCalendar = "outlook/TZ-no-description.txt";
        try {
            Calendar actual = buildCalendar(actualCalendar);
            actual.conformToRfc5545();
            Calendar expected = buildCalendar("outlook/TZ-set-to-utc.txt");
            assertEquals(expected.toString(), actual.toString());
            assertEquals(expected, actual);
        } catch (Exception e) {
            e.printStackTrace();
            fail("RFC transformation failed for " + actualCalendar);
        }
    }

    private Calendar buildCalendar(String file) throws IOException, ParserException {
        InputStream is = getClass().getResourceAsStream(file);
        CalendarBuilder cb = new CalendarBuilder();
        Calendar calendar = cb.build(is);
        is.close();
        return calendar;
    }
}
