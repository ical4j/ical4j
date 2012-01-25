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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

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
 * $Id: CalendarOutputterTest.java [Apr 6, 2004]
 *
 * Test case for iCalendarOutputter.
 *
 * @author benf
 */
public class CalendarOutputterTest extends TestCase {

    private static Log log = LogFactory.getLog(CalendarOutputterTest.class);

    private String filename;

    /**
     * @param method
     * @param file
     */
    public CalendarOutputterTest(final String file) {
        super("testOutput");
        this.filename = file;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected final void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected final void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
    }

    /**
     * @throws Exception
     */
    public void testOutput() throws Exception {
        try {
            CalendarBuilder builder = new CalendarBuilder();
            FileInputStream fin = new FileInputStream(filename);
            CalendarOutputter outputter = new CalendarOutputter(false, FoldingWriter.REDUCED_FOLD_LENGTH);
            OutputStream out = new ByteArrayOutputStream();

            Calendar calendar = null;
            try {
                calendar = builder.build(fin);
            } catch (IOException e) {
                log.error("Error while parsing: " + filename, e);
            } catch (ParserException e) {
                log.error("Error while parsing: " + filename, e);
            }

            assertNotNull(calendar);
            
            outputter.setValidating(false);
            outputter.output(calendar, out);

            if (log.isDebugEnabled()) {
                log.debug(out.toString());
            }

            BufferedReader bin = new BufferedReader(new UnfoldingReader(new FileReader(filename), 1024), 1024);
            StringWriter rout = new StringWriter();
            BufferedWriter bout = new BufferedWriter(rout);

            try {
                String line = null;
                while ((line = bin.readLine()) != null) {
                    bout.write(line);
                    bout.write('\n');
                }
            } finally {
                bout.close();
                bin.close();
            }

            String rawData = rout.toString();

            assertEquals("Output differed from expected: " + filename, rawData, out.toString());
        } catch (IOException e) {
            log.error("Error while parsing: " + filename, e);
            throw e;
        } catch (ValidationException e) {
            log.error("Error while parsing: " + filename, e);
            throw e;
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
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        File[] testFiles = null;

        // valid tests..
        testFiles = new File("etc/samples/valid").listFiles((FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
        for (int i = 0; i < testFiles.length; i++) {
            log.info("Sample [" + testFiles[i] + "]");
            suite.addTest(new CalendarOutputterTest(testFiles[i].getPath()));
        }
        
        // invalid tests..
        testFiles = new File("etc/samples/invalid").listFiles((FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
        for (int i = 0; i < testFiles.length; i++) {
            log.info("Sample [" + testFiles[i] + "]");
            suite.addTest(new CalendarOutputterTest(testFiles[i].getPath()));
        }

        return suite;
    }
}
