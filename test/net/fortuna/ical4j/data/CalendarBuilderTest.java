/*
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 *
 * Copyright (c) 2004 Ben Fortuna
 */
package net.fortuna.ical4j.data;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.FileOnlyFilter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.Description;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Test case for iCalendarBuilder.
 *
 * @author benf
 */
public class CalendarBuilderTest extends TestCase {

    private static Log log = LogFactory.getLog(CalendarBuilderTest.class);

    private String filename;

    private CalendarBuilder builder;

    /**
     * Constructor.
     *
     * @param method
     *            name of method to run in test case
     * @param file
     *            an iCalendar filename
     */
    public CalendarBuilderTest(final String method, final String file) {

        super(method);

        this.filename = file;

        builder = new CalendarBuilder();
    }

    /**
     * Class to test for Calendar build(InputStream).
     */
    public final void testBuildInputStream() throws Exception {

        System.setProperty("ical4j.unfolding.relaxed", "true");

        FileInputStream fin = new FileInputStream(filename);

        Calendar calendar = null;

        try {
            calendar = builder.build(fin);
        } catch (IOException e) {
            log.warn("File: " + filename, e);
        } catch (ParserException e) {
            log.warn("File: " + filename, e);
        }

        assertNotNull("File [" + filename + "] invalid", calendar);

        try {
            calendar.validate();
        } catch (ValidationException e) {
            assertTrue("Calendar file " + filename + " isn't valid:\n" + e.getMessage(), false);
        }

        if (log.isInfoEnabled()) {
            log.info("File: " + filename);

            if (log.isDebugEnabled()) {
                log.debug("Calendar:\n=========\n" + calendar.toString());

                for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                    Component c = (Component) i.next();

                    Description description = (Description) c.getProperties().getProperty(Property.DESCRIPTION);

                    if (description != null) {
                        log.debug("Description [" + description.getValue() + "]");
                    }
                }
            }
        }
    }

    /**
     * Test suite.
     * @return test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        List testFiles = new ArrayList();

        testFiles.addAll(Arrays.asList(new File("etc/samples/valid").listFiles(new FileOnlyFilter())));
        testFiles.addAll(Arrays.asList(new File("etc/samples/invalid").listFiles(new FileOnlyFilter())));

        for (int i = 0; i < testFiles.size(); i++) {
            log.info("Sample [" + testFiles.get(i) + "]");
			suite.addTest(new CalendarBuilderTest("testBuildInputStream", ((File)testFiles.get(i)).getPath()));
        }

        return suite;
    }
}