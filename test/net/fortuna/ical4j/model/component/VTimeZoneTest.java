/*
 * $Id: VTimeZoneTest.java [5/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.ical4j.model.component;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZoneRegistryImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A test case for VTimeZone.
 * 
 * @author benfortuna
 */
public class VTimeZoneTest extends TestCase {

    private static Log log = LogFactory.getLog(VTimeZoneTest.class);

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * 
     */
    public void testCreateDefinition() {
        VTimeZone tz = TimeZoneRegistryImpl.getInstance().getTimeZone("Australia/Melbourne").getVTimeZone();
        Calendar calendar = new Calendar();
        calendar.getComponents().add(tz);
        log.info(calendar);
    }
}