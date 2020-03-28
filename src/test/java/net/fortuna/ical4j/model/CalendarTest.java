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

import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

import static net.fortuna.ical4j.model.WeekDay.*;

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
        TzId tzParam = new TzId(TimeZoneRegistry.getGlobalZoneId("Australia/Melbourne").getId());

        // Add events, etc..
        Calendar copyCalendar = new Calendar(calendar);

        ZonedDateTime start = ZonedDateTime.now().with(ChronoField.DAY_OF_WEEK,
                DayOfWeek.MONDAY.getValue()).withHour(9).withMinute(0).withSecond(0);

        ZonedDateTime end = start.plusYears(1);

        VEvent week1UserA = new VEvent(start, java.time.Duration.ofHours(8), "Week 1 - User A");
        week1UserA.getProperty(Property.DTSTART).get().getParameters().add(tzParam);
        week1UserA.getProperty(Property.DTSTART).get().getParameters().add(Value.DATE);

        Recur<ZonedDateTime> week1UserARecur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.WEEKLY)
                .until(end)
                .interval(3).dayList(new WeekDayList(MO, TU, WE, TH, FR))
                .hourList(new NumberList("9")).build();
        week1UserA.getProperties().add(new RRule(week1UserARecur));
        week1UserA.getProperties().add(new Uid("000001@modularity.net.au"));

        start = start.plusWeeks(1);
        end = end.plusWeeks(1);

        VEvent week2UserB = new VEvent(start, java.time.Duration.ofHours(8), "Week 2 - User B");
        week2UserB.getProperty(Property.DTSTART).get().getParameters().add(tzParam);
        week2UserB.getProperty(Property.DTSTART).get().getParameters().add(Value.DATE);

        Recur<ZonedDateTime> week2UserBRecur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.WEEKLY)
                .until(end).interval(3).dayList(new WeekDayList(MO, TU, WE, TH, FR))
                .hourList(new NumberList("9")).build();
        week2UserB.getProperties().add(new RRule(week2UserBRecur));
        week2UserB.getProperties().add(new Uid("000002@modularity.net.au"));

        start = start.plusWeeks(1);
        end = end.plusWeeks(1);

        VEvent week3UserC = new VEvent(start, java.time.Duration.ofHours(8), "Week 3 - User C");
        week3UserC.getProperty(Property.DTSTART).get().getParameters().add(tzParam);
        week3UserC.getProperty(Property.DTSTART).get().getParameters().add(Value.DATE);

        Recur<ZonedDateTime> week3UserCRecur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.WEEKLY)
                .until(end).interval(3).dayList(new WeekDayList(MO, TU, WE, TH, FR))
                .hourList(new NumberList("9")).build();
        week3UserC.getProperties().add(new RRule(week3UserCRecur));
        week3UserC.getProperties().add(new Uid("000003@modularity.net.au"));

        copyCalendar.getComponents().add(week1UserA);
        copyCalendar.getComponents().add(week2UserB);
        copyCalendar.getComponents().add(week3UserC);

        // test event date ranges..
        copyCalendar = new Calendar(calendar);

        start = ZonedDateTime.now().withYear(2006).withMonth(1).withDayOfMonth(1)
                .withHour(9).withMinute(0).withSecond(0);

        end = start.plusYears(1);

        week1UserA = new VEvent(start, java.time.Duration.ofHours(8), "Week 1 - User A");
        week1UserA.getProperty(Property.DTSTART).get().getParameters().add(tzParam);
        week1UserA.getProperty(Property.DTSTART).get().getParameters().add(Value.DATE);

        week1UserARecur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.WEEKLY)
                .until(end).interval(3)
                .dayList(new WeekDayList(new WeekDay(MO, 0), new WeekDay(TU, 0), new WeekDay(WE, 0), new WeekDay(TH, 0), new WeekDay(FR, 0)))
                .hourList(new NumberList("9")).build();
        week1UserA.getProperties().add(new RRule(week1UserARecur));
        week1UserA.getProperties().add(new Uid("000001@modularity.net.au"));

        start = start.plusWeeks(1);
        end = end.plusWeeks(1);

        week2UserB = new VEvent(start, java.time.Duration.ofHours(8), "Week 2 - User B");
        week2UserB.getProperty(Property.DTSTART).get().getParameters().add(tzParam);
        week2UserB.getProperty(Property.DTSTART).get().getParameters().add(Value.DATE);

        week2UserBRecur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.WEEKLY)
                .until(end).interval(3)
                .dayList(new WeekDayList(new WeekDay(MO, 0), new WeekDay(TU, 0), new WeekDay(WE, 0), new WeekDay(TH, 0), new WeekDay(FR, 0)))
                .hourList(new NumberList("9")).build();
        week2UserB.getProperties().add(new RRule(week2UserBRecur));
        week2UserB.getProperties().add(new Uid("000002@modularity.net.au"));

        start = start.plusWeeks(1);
        end = end.plusWeeks(1);

        week3UserC = new VEvent(start, java.time.Duration.ofHours(8), "Week 3 - User C");
        week3UserC.getProperty(Property.DTSTART).get().getParameters().add(tzParam);
        week3UserC.getProperty(Property.DTSTART).get().getParameters().add(Value.DATE);

        week3UserCRecur = new Recur.Builder<ZonedDateTime>().frequency(Frequency.WEEKLY)
                .until(end).interval(3)
                .dayList(new WeekDayList(new WeekDay(MO, 0), new WeekDay(TU, 0), new WeekDay(WE, 0), new WeekDay(TH, 0), new WeekDay(FR, 0)))
                .hourList(new NumberList("9")).build();
        week3UserC.getProperties().add(new RRule(week3UserCRecur));
        week3UserC.getProperties().add(new Uid("000003@modularity.net.au"));

        copyCalendar.getComponents().add(week1UserA);
        copyCalendar.getComponents().add(week2UserB);
        copyCalendar.getComponents().add(week3UserC);
        copyCalendar.validate();

    }
}
