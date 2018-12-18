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
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

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

        Recur week1UserARecur = new Recur.Builder().frequency(Frequency.WEEKLY)
                .until(new Date(calEnd.getTime().getTime()))
                .interval(3).dayList(new WeekDayList(MO, TU, WE, TH, FR))
                .hourList(new NumberList("9")).build();
        week1UserA.getProperties().add(new RRule(week1UserARecur));
        week1UserA.getProperties().add(new Uid("000001@modularity.net.au"));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);

        VEvent week2UserB = new VEvent(new Date(calStart.getTime().getTime()),
                java.time.Duration.ofHours(8), "Week 2 - User B");
        week2UserB.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        week2UserB.getProperty(Property.DTSTART).getParameters().replace(Value.DATE);

        Recur week2UserBRecur = new Recur.Builder().frequency(Frequency.WEEKLY)
                .until(new Date(calEnd.getTime().getTime())).interval(3).dayList(new WeekDayList(MO, TU, WE, TH, FR))
                .hourList(new NumberList("9")).build();
        week2UserB.getProperties().add(new RRule(week2UserBRecur));
        week2UserB.getProperties().add(new Uid("000002@modularity.net.au"));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);

        VEvent week3UserC = new VEvent(new Date(calStart.getTime().getTime()),
                java.time.Duration.ofHours(8), "Week 3 - User C");
        week3UserC.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        week3UserC.getProperty(Property.DTSTART).getParameters().replace(Value.DATE);

        Recur week3UserCRecur = new Recur.Builder().frequency(Frequency.WEEKLY)
                .until(new Date(calEnd.getTime().getTime())).interval(3).dayList(new WeekDayList(MO, TU, WE, TH, FR))
                .hourList(new NumberList("9")).build();
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

        week1UserARecur = new Recur.Builder().frequency(Frequency.WEEKLY)
                .until(new Date(calEnd.getTime().getTime())).interval(3)
                .dayList(new WeekDayList(new WeekDay(MO, 0), new WeekDay(TU, 0), new WeekDay(WE, 0), new WeekDay(TH, 0), new WeekDay(FR, 0)))
                .hourList(new NumberList("9")).build();
        week1UserA.getProperties().add(new RRule(week1UserARecur));
        week1UserA.getProperties().add(new Uid("000001@modularity.net.au"));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);

        week2UserB = new VEvent(new Date(calStart.getTime().getTime()),
                java.time.Duration.ofHours(8), "Week 2 - User B");
        week2UserB.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        week2UserB.getProperty(Property.DTSTART).getParameters().replace(Value.DATE);

        week2UserBRecur = new Recur.Builder().frequency(Frequency.WEEKLY)
                .until(new Date(calEnd.getTime().getTime())).interval(3)
                .dayList(new WeekDayList(new WeekDay(MO, 0), new WeekDay(TU, 0), new WeekDay(WE, 0), new WeekDay(TH, 0), new WeekDay(FR, 0)))
                .hourList(new NumberList("9")).build();
        week2UserB.getProperties().add(new RRule(week2UserBRecur));
        week2UserB.getProperties().add(new Uid("000002@modularity.net.au"));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);

        week3UserC = new VEvent(new Date(calStart.getTime().getTime()),
                java.time.Duration.ofHours(8), "Week 3 - User C");
        week3UserC.getProperty(Property.DTSTART).getParameters().replace(tzParam);
        week3UserC.getProperty(Property.DTSTART).getParameters().replace(Value.DATE);

        week3UserCRecur = new Recur.Builder().frequency(Frequency.WEEKLY)
                .until(new Date(calEnd.getTime().getTime())).interval(3)
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
