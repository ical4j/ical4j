/*
 * Created on 16/03/2005
 *
 * $Id$
 *
 * Copyright (c) 2005, Ben Fortuna
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

import java.util.SortedSet;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ben
 *
 * A test case for creating calendars.
 */
public class CalendarTest extends TestCase {

    private static Log log = LogFactory.getLog(Calendar.class);

    /*
     * Class under test for void Calendar()
     */
    public void testCalendar() throws ValidationException {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        
        // Add events, etc..
        VTimeZone tz = VTimeZone.getDefault();
        TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());
        
        java.util.Calendar calStart = java.util.Calendar.getInstance();
        calStart.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
        calStart.set(java.util.Calendar.HOUR_OF_DAY, 9);
        calStart.clear(java.util.Calendar.MINUTE);
        calStart.clear(java.util.Calendar.SECOND);

        java.util.Calendar calEnd = java.util.Calendar.getInstance();
        calEnd.setTime(calStart.getTime());
        calEnd.add(java.util.Calendar.YEAR, 1);
        
        VEvent week1UserA = new VEvent(calStart.getTime(), 1000 * 60 * 60 * 8, "Week 1 - User A");
        week1UserA.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);
        
        Recur week1UserARecur = new Recur(Recur.WEEKLY, calEnd.getTime());
        week1UserARecur.setInterval(3);
        week1UserARecur.getDayList().add(WeekDay.MO);
        week1UserARecur.getDayList().add(WeekDay.TU);
        week1UserARecur.getDayList().add(WeekDay.WE);
        week1UserARecur.getDayList().add(WeekDay.TH);
        week1UserARecur.getDayList().add(WeekDay.FR);
        week1UserARecur.getHourList().add(new Integer(9));
        week1UserA.getProperties().add(new RRule(week1UserARecur));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        
        VEvent week2UserB = new VEvent(calStart.getTime(), 1000 * 60 * 60 * 8, "Week 2 - User B");
        week2UserB.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);
        
        Recur week2UserBRecur = new Recur(Recur.WEEKLY, calEnd.getTime());
        week2UserBRecur.setInterval(3);
        week2UserBRecur.getDayList().add(WeekDay.MO);
        week2UserBRecur.getDayList().add(WeekDay.TU);
        week2UserBRecur.getDayList().add(WeekDay.WE);
        week2UserBRecur.getDayList().add(WeekDay.TH);
        week2UserBRecur.getDayList().add(WeekDay.FR);
        week2UserBRecur.getHourList().add(new Integer(9));
        week2UserB.getProperties().add(new RRule(week2UserBRecur));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        
        VEvent week3UserC = new VEvent(calStart.getTime(), 1000 * 60 * 60 * 8, "Week 3 - User C");
        week3UserC.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);
        
        Recur week3UserCRecur = new Recur(Recur.WEEKLY, calEnd.getTime());
        week3UserCRecur.setInterval(3);
        week3UserCRecur.getDayList().add(WeekDay.MO);
        week3UserCRecur.getDayList().add(WeekDay.TU);
        week3UserCRecur.getDayList().add(WeekDay.WE);
        week3UserCRecur.getDayList().add(WeekDay.TH);
        week3UserCRecur.getDayList().add(WeekDay.FR);
        week3UserCRecur.getHourList().add(new Integer(9));
        week3UserC.getProperties().add(new RRule(week3UserCRecur));
        
        calendar.getComponents().add(week1UserA);
        calendar.getComponents().add(week2UserB);
        calendar.getComponents().add(week3UserC);
        
        calendar.validate();
        
        log.info(calendar);
    }
    
    public void testGetEventDateRanges() throws ValidationException {

        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        // Add events, etc..
        VTimeZone tz = VTimeZone.getDefault();
        TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());

        java.util.Calendar calStart = java.util.Calendar.getInstance();
        calStart.set(java.util.Calendar.YEAR, 2006);
        calStart.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY);
        calStart.set(java.util.Calendar.DAY_OF_MONTH, 1);
        calStart.set(java.util.Calendar.HOUR_OF_DAY, 9);
        calStart.clear(java.util.Calendar.MINUTE);
        calStart.clear(java.util.Calendar.SECOND);

        java.util.Calendar calEnd = java.util.Calendar.getInstance();
        calEnd.setTime(calStart.getTime());
        calEnd.add(java.util.Calendar.YEAR, 1);

        VEvent week1UserA = new VEvent(calStart.getTime(), 1000 * 60 * 60 * 8, "Week 1 - User A");
        week1UserA.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);

        Recur week1UserARecur = new Recur(Recur.WEEKLY, calEnd.getTime());
        week1UserARecur.setInterval(3);
        week1UserARecur.getDayList().add(new WeekDay(WeekDay.MO, 0));
        week1UserARecur.getDayList().add(new WeekDay(WeekDay.TU, 0));
        week1UserARecur.getDayList().add(new WeekDay(WeekDay.WE, 0));
        week1UserARecur.getDayList().add(new WeekDay(WeekDay.TH, 0));
        week1UserARecur.getDayList().add(new WeekDay(WeekDay.FR, 0));
        week1UserARecur.getHourList().add(new Integer(9));
        week1UserA.getProperties().add(new RRule(week1UserARecur));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);

        VEvent week2UserB = new VEvent(calStart.getTime(), 1000 * 60 * 60 * 8, "Week 2 - User B");
        week2UserB.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);

        Recur week2UserBRecur = new Recur(Recur.WEEKLY, calEnd.getTime());
        week2UserBRecur.setInterval(3);
        week2UserBRecur.getDayList().add(new WeekDay(WeekDay.MO, 0));
        week2UserBRecur.getDayList().add(new WeekDay(WeekDay.TU, 0));
        week2UserBRecur.getDayList().add(new WeekDay(WeekDay.WE, 0));
        week2UserBRecur.getDayList().add(new WeekDay(WeekDay.TH, 0));
        week2UserBRecur.getDayList().add(new WeekDay(WeekDay.FR, 0));
        week2UserBRecur.getHourList().add(new Integer(9));
        week2UserB.getProperties().add(new RRule(week2UserBRecur));

        calStart.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        calEnd.add(java.util.Calendar.WEEK_OF_YEAR, 1);

        VEvent week3UserC = new VEvent(calStart.getTime(), 1000 * 60 * 60 * 8, "Week 3 - User C");
        week3UserC.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);

        Recur week3UserCRecur = new Recur(Recur.WEEKLY, calEnd.getTime());
        week3UserCRecur.setInterval(3);
        week3UserCRecur.getDayList().add(new WeekDay(WeekDay.MO, 0));
        week3UserCRecur.getDayList().add(new WeekDay(WeekDay.TU, 0));
        week3UserCRecur.getDayList().add(new WeekDay(WeekDay.WE, 0));
        week3UserCRecur.getDayList().add(new WeekDay(WeekDay.TH, 0));
        week3UserCRecur.getDayList().add(new WeekDay(WeekDay.FR, 0));
        week3UserCRecur.getHourList().add(new Integer(9));
        week3UserC.getProperties().add(new RRule(week3UserCRecur));

        calendar.getComponents().add(week1UserA);
        calendar.getComponents().add(week2UserB);
        calendar.getComponents().add(week3UserC);

        calendar.validate();


        // Start the logic testing.
        java.util.Calendar queryStartCal = java.util.Calendar.getInstance();
        java.util.Calendar queryEndCal = java.util.Calendar.getInstance();

        queryStartCal.set(2006, java.util.Calendar.JULY, 1, 9, 0, 0);
        queryEndCal.set(2006, java.util.Calendar.AUGUST, 1, 9, 0, 0);

        SortedSet dateRangeSet =
                        calendar.getEventDateRanges(queryStartCal.getTime(),
                                                    queryEndCal.getTime());

        log.info(dateRangeSet);
    }
}
