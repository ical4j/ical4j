/*
 * $Id: VEventTest.java [28/09/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.ical4j.model.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;

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

        // create value parameter..
        Value type = new Value(Value.DATE);

        // create event start date..
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 25);

        DtStart start = new DtStart(calendar.getTime());
        start.getParameters().add(tzParam);
        start.getParameters().add(type);

        Summary summary = new Summary("Christmas Day; \n this is a, test\\");

        VEvent christmas = new VEvent();
        christmas.getProperties().add(start);
        christmas.getProperties().add(summary);

        log.info(christmas);
    }

}