/*
 * $Id: VEventTest.java [28/09/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.ical4j.model.component;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A test case for VTimeZone.
 * 
 * @author benfortuna
 */
public class VEventTest extends TestCase {

    private static Log log = LogFactory.getLog(VEventTest.class);

    /**
     *  
     */
    public final void test() {
        // create timezone property..
        VTimeZone tz = VTimeZone.getDefault();

        // create tzid parameter..
        TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID)
                .getValue());

        // create event start date..
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 25);

        DtStart start = new DtStart(calendar.getTime());
        start.getParameters().add(tzParam);
        start.getParameters().add(Value.DATE);

        Summary summary = new Summary("Christmas Day; \n this is a, test\\");

        VEvent christmas = new VEvent();
        christmas.getProperties().add(start);
        christmas.getProperties().add(summary);

        log.info(christmas);
    }

    public final void test2() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 25);

        VEvent christmas = new VEvent(cal.getTime(), "Christmas Day");

        // initialise as an all-day event..
        christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);

        // add timezone information..
        VTimeZone tz = VTimeZone.getDefault();
        TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());
        christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);

        log.info(christmas);
    }
    
    public final void test3() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        // tomorrow..
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 9);
        cal.set(java.util.Calendar.MINUTE, 30);

        VEvent meeting = new VEvent(cal.getTime(), 1000 * 60 * 60, "Progress Meeting");

        // add timezone information..
        VTimeZone tz = VTimeZone.getDefault();
        TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());
        meeting.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);       

        log.info(meeting);
    }
}