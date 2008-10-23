/*
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 *
 * Copyright (c) 2004 Ben Fortuna
 */
package net.fortuna.ical4j.data;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
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

    private FileInputStream fin;
    
    private CalendarBuilder builder;

    /**
     * Constructor.
     *
     * @param method
     *            name of method to run in test case
     * @param file
     *            an iCalendar filename
     * @throws FileNotFoundException 
     */
    public CalendarBuilderTest(String testMethod, final String file) throws FileNotFoundException {
        super(testMethod);
        this.filename = file;
        this.fin = new FileInputStream(filename);
        builder = new CalendarBuilder();
    }

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
     * @throws IOException
     * @throws ParserException
     * @throws ValidationException
     */
    public void testBuildValid() throws IOException, ParserException, ValidationException {
        Calendar calendar = builder.build(fin);
        calendar.validate();
    }
    
    /**
     * @throws IOException
     * @throws ParserException
     */
    public void testBuildInvalid() throws IOException {
        try {
            Calendar calendar = builder.build(fin);
            calendar.validate();
            fail("Should throw ParserException or ValidationException");
        }
        catch (ValidationException ve) {
            log.trace("Caught exception: [" + filename + "," + ve.getMessage() + "]");
        }
        catch (ParserException ve) {
            log.trace("Caught exception: [" + filename + "," + ve.getMessage() + "]");
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
     * @throws FileNotFoundException 
     */
    public static Test suite() throws FileNotFoundException {
        TestSuite suite = new TestSuite();

        File[] testFiles = null;

        // single test..
//        suite.addTest(new CalendarBuilderTest("testBuildInputStream",
//                new File("etc/samples/valid/Session6.ics").getPath(), true));

        // valid tests..
        testFiles = new File("etc/samples/valid").listFiles((FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
        for (int i = 0; i < testFiles.length; i++) {
            log.info("Sample [" + testFiles[i] + "]");
            suite.addTest(new CalendarBuilderTest("testBuildValid", testFiles[i].getPath()));
        }
        
        // invalid tests..
        testFiles = new File("etc/samples/invalid").listFiles((FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
        for (int i = 0; i < testFiles.length; i++) {
            log.info("Sample [" + testFiles[i] + "]");
            suite.addTest(new CalendarBuilderTest("testBuildInvalid", testFiles[i].getPath()));
        }

        return suite;
    }
}
