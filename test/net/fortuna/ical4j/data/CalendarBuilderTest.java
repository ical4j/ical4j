/*
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 *
 * Copyright (c) 2004 Ben Fortuna
 */
package net.fortuna.ical4j.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.FileOnlyFilter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test case for iCalendarBuilder.
 *
 * @author benf
 */
public class CalendarBuilderTest extends TestCase {

    private static Log log = LogFactory.getLog(CalendarBuilderTest.class);

    private String filename;

    private CalendarBuilder builder;
    
    private boolean valid;

    /**
     * Constructor.
     *
     * @param method
     *            name of method to run in test case
     * @param file
     *            an iCalendar filename
     */
    public CalendarBuilderTest(final String method, final String file, final boolean valid) {
        super(method);
        this.filename = file;
        this.valid = valid;
        builder = new CalendarBuilder();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected final void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected final void tearDown() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, false);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, false);
    }
    
    /**
     * Class to test for Calendar build(InputStream).
     */
    public final void testBuildInputStream() throws IOException {
        FileInputStream fin = new FileInputStream(filename);

        Calendar calendar = null;

        try {
            calendar = builder.build(fin);
            assertNotNull("File [" + filename + "] invalid", calendar);
            try {
                calendar.validate();
                assertTrue("File [" + filename + "] valid", valid);
            } catch (ValidationException e) {
                log.warn("Calendar file [" + filename + "] is invalid.", e);
                assertFalse("File [" + filename + "] invalid", valid);
            }
        } catch (ParserException e) {
            log.warn("File: " + filename, e);
            assertFalse("File [" + filename + "] invalid", valid);
        }

        if (log.isInfoEnabled()) {
            log.info("File: " + filename);

            if (log.isDebugEnabled()) {
                log.debug("Calendar:\n=========\n" + calendar.toString());

                for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                    Component c = (Component) i.next();

                    Description description = (Description) c.getProperty(Property.DESCRIPTION);

                    if (description != null) {
                        log.debug("Description [" + description.getValue() + "]");
                    }
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#getName()
     */
    /**
     * Overridden to return the current iCalendar file under test.
     */
    public final String getName() {
        return super.getName() + " [" + filename + "]";
    }

    /**
     * Test suite.
     * @return test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        File[] testFiles = null;

        // valid tests..
        testFiles = new File("etc/samples/valid").listFiles(new FileOnlyFilter());
        for (int i = 0; i < testFiles.length; i++) {
            log.info("Sample [" + testFiles[i] + "]");
            suite.addTest(new CalendarBuilderTest("testBuildInputStream", testFiles[i].getPath(), true));
        }
        
        // invalid tests..
        testFiles = new File("etc/samples/invalid").listFiles(new FileOnlyFilter());
        for (int i = 0; i < testFiles.length; i++) {
            log.info("Sample [" + testFiles[i] + "]");
            suite.addTest(new CalendarBuilderTest("testBuildInputStream", testFiles[i].getPath(), false));
        }

        return suite;
    }
}
