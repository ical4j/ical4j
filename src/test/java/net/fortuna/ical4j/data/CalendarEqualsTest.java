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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 *
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
    @Override
    protected final void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected final void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
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
        } catch (IOException | ParserException e) {
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
            builder = new CalendarBuilder();

            try {
                reparsedCalendar = builder.build(fin);
            } catch (IOException | ParserException e) {
                exception = e;
                errorOccurred = true;
            }

            assertEquals("Parsed calendar isn't equal to itself!  : " + file.toString(),
                    calendar, reparsedCalendar);
        }
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#getName()
     */
    /**
     * Overridden to return the current iCalendar file under test.
     */
    @Override
    public final String getName() {
        return super.getName() + " [" + file.getName() + "]";
    }

    /**
     * 
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        
        File[] testFiles = new File("src/test/resources/samples/valid").listFiles(f -> !f.isDirectory() && f.getName().endsWith(".ics"));
        for (File testFile : testFiles) {
            suite.addTest(new CalendarEqualsTest((File) testFile, true));
        }
        return suite;
    }
}
