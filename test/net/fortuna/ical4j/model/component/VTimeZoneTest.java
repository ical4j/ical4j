/*
 * $Id: VTimeZoneTest.java [5/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.ical4j.model.component;

import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 * A test case for VTimeZone.
 * 
 * @author benfortuna
 */
public class VTimeZoneTest extends TestCase {

    private static Log log = LogFactory.getLog(VTimeZoneTest.class);

    /**
     *  
     */
    public final void testGetDefault() {
        VTimeZone timezone = VTimeZone.getDefault();

        assertNotNull(timezone);

        log.info(timezone);
    }

    /**
     * Test creation of specific timezones.
     */
    public final void testGetVTimeZone() {
        TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
        
        VTimeZone timezone = VTimeZone.getVTimeZone(tz);

        assertNotNull(timezone);

        log.info(timezone);
    }

}