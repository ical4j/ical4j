/*
 * Created on 16/03/2005
 *
 * $Id$
 *
 * Copyright (c) 2005, Ben Fortuna
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.fortuna.ical4j.model;

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
        
        log.info(calendar);
    }

}
