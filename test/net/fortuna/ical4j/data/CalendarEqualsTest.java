/*
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 *
 * Copyright (c) 2004 Ben Fortuna
 */
package net.fortuna.ical4j.data;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.FileOnlyFilter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test case for CalendarBuilder.
 *
 * @author benf
 */
public class CalendarEqualsTest extends TestCase {

    private File file;
    
    private boolean valid;
    
    /**
     * @param file
     * @param valid
     */
    public CalendarEqualsTest(File file, boolean valid) {
        super("testCalendarEquals");
        this.file = file;
        this.valid = valid;
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected final void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected final void tearDown() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, false);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, false);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, false);
    }

    /**
     *
     * @param file
     * @param valid true if file is supposed to be valid
     * @throws Exception
     */ 
    public void testCalendarEquals() throws Exception {
        
        FileInputStream fin = new FileInputStream(file);
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = null;

        boolean errorOccurred = false;
        Exception exception = null;

        try {
            calendar = builder.build(fin);
        } catch (IOException e) {
            exception = e;
            errorOccurred = true;
        } catch (ParserException e) {
            exception = e;
            errorOccurred = true;
        }

        if (errorOccurred) {
            if (valid) {
                throw new AssertionFailedError("Calendar file " + file.toString() + " isn't valid:\n" + exception.getMessage());
            }
        }

        if (calendar != null) {
            try {
                calendar.validate();
            } catch (ValidationException e) {
                exception = e;
                errorOccurred = true;
            }

            if (errorOccurred) {
                if (valid) {
                    throw new AssertionFailedError("Calendar file " + file.toString() + " isn't valid:\n" + exception.getMessage());
                }
            } else {
                if (! valid) {
                    throw new AssertionFailedError("Calendar file " + file.toString() + " isn't valid and shouldn't validate.");
                }
            }

            fin = new FileInputStream(file);
            Calendar reparsedCalendar = null;

            try {
                reparsedCalendar = builder.build(fin);
            } catch (IOException e) {
                exception = e;
                errorOccurred = true;
            } catch (ParserException e) {
                exception = e;
                errorOccurred = true;
            }

            assertTrue("Parsed calendar isn't equal to itself!  : " + file.toString(),
                    calendar.equals(reparsedCalendar));
        }
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#getName()
     */
    /**
     * Overridden to return the current iCalendar file under test.
     */
    public final String getName() {
        return super.getName() + " [" + file.getName() + "]";
    }

    /**
     * 
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        
        List testFiles = new ArrayList(Arrays.asList(new File("etc/samples/valid").listFiles(new FileOnlyFilter())));
        for (int i = 0; i < testFiles.size(); i++) {
            suite.addTest(new CalendarEqualsTest((File) testFiles.get(i), true));
        }
        return suite;
    }
}
