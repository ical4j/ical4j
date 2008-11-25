/*
 * $Id: VTimeZoneTest.java [5/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.ical4j.model.component;

import junit.framework.TestSuite;
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

    private VTimeZone tz;
    
    /**
     * @param testMethod
     * @param component
     */
    public VTimeZoneTest(String testMethod, VTimeZone component) {
        super(testMethod, component);
        this.tz = component;
    }

    /**
     * 
     */
    public void testCreateDefinition() {
        Calendar calendar = new Calendar();
        calendar.getComponents().add(tz);
        log.info(calendar);
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.ComponentTest#testIsCalendarComponent()
     */
    public void testIsCalendarComponent() {
        assertIsCalendarComponent(tz);
    }
    
    /**
     * @return
     */
    public static TestSuite suite() {
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TestSuite suite = new TestSuite();
        
        suite.addTest(new VTimeZoneTest("testCreateDefinition", registry.getTimeZone("Australia/Melbourne").getVTimeZone()));
        suite.addTest(new VTimeZoneTest("testIsCalendarComponent", new VTimeZone()));
        
        return suite;
    }
}
