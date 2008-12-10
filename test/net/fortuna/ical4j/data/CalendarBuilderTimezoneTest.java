/*
 * Copyright (c) 2004 Ben Fortuna
 */
package net.fortuna.ical4j.data;

import java.io.FileInputStream;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.util.CompatibilityHints;

/**
 * $Id: CalendarBuilderTimezoneTest.java [Jul 1, 2008]
 *
 * Test case for CalendarBuilder and handling of icalendar streams
 * where VTIMZONES are included after other components.
 *
 * @author randy
 */
public class CalendarBuilderTimezoneTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected final void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_VALIDATION, true);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected final void tearDown() throws Exception {
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_UNFOLDING, false);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_NOTES_COMPATIBILITY, false);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_VALIDATION, false);
    }
    

   /**
     * Test that VTIMEZONES that are included after VEVENT 
     * are correctly handled and that dates defined before the
     * VTIMEZONE are parsed properly.
     */
    public void testVTimeZoneAfterVEvent() throws Exception {

        // Evolution includes VTIMEZONE defs after VEVENT defs,
        // which is allowed by RFC-2445
        FileInputStream fin = new FileInputStream(
                "etc/samples/valid/evolution.ics");
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = null;

        calendar = builder.build(fin);
        assertNotNull("Calendar is null", calendar);
        ComponentList comps = calendar.getComponents(Component.VEVENT);
        assertTrue("VEVENT not found", comps.size() == 1);
        VEvent vevent = (VEvent) comps.get(0);

        DtStart dtstart = vevent.getStartDate();
        DateTime dateTime = (DateTime) dtstart.getDate();

        assertEquals("date value not correct", "20080624T130000", dtstart
                .getValue());
        assertNotNull("timezone not present", dateTime.getTimeZone());
        assertEquals("timezone not correct",
                "/softwarestudio.org/Tzfile/America/Chicago", dateTime
                        .getTimeZone().getID());

    }
}
