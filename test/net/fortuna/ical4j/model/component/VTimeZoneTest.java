/*
 * $Id: VTimeZoneTest.java [5/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentTest;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A test case for VTimeZone.
 * 
 * @author benfortuna
 */
public class VTimeZoneTest extends ComponentTest {

    private static Log log = LogFactory.getLog(VTimeZoneTest.class);

    private TimeZoneRegistry registry;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = TimeZoneRegistryFactory.getInstance().createRegistry();
    }

    /**
     * 
     */
    public void testCreateDefinition() {
        VTimeZone tz = registry.getTimeZone("Australia/Melbourne").getVTimeZone();
        Calendar calendar = new Calendar();
        calendar.getComponents().add(tz);
        log.info(calendar);
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.ComponentTest#testIsCalendarComponent()
     */
    public void testIsCalendarComponent() {
        assertIsCalendarComponent(new VTimeZone());
    }
}
