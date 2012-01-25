/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 *
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
        
        // uncomment for testing invalid calendars in relaxed parsing mode..
//        CompatibilityHints.setHintEnabled(
//                CompatibilityHints.KEY_RELAXED_PARSING, true);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected final void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
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
//        suite.addTest(new CalendarBuilderTest("testBuildValid",
//                new File("etc/samples/valid/oracle.ics").getPath()));

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
