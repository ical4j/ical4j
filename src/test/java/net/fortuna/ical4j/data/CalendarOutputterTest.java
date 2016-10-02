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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * $Id: CalendarOutputterTest.java [Apr 6, 2004]
 * <p/>
 * Test case for iCalendarOutputter.
 *
 * Ignored. Unable to simply assert that the output will be exactly the same as the raw input.
 *  - todo: Properties and parameters might not be same order in output
 *  - todo: Currently LF and CRLF are not checked correctly
 *  - todo: Outputter is folding lines to maximum 73 characters. This is forces by the FoldingWriter!›
 *  - todo: iCal4j does not seem to care about real input file encoding, this will make output different from input
 *  - todo: This test case seems to expect invalid input be returned from parser and outputter as same invalid input. What!?
 *
 * @author benf
 */
@Ignore
public class CalendarOutputterTest extends TestCase {

    private static Logger log = LoggerFactory.getLogger(CalendarOutputterTest.class);

    private String filename;

    /**
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
                    bout.write('\r');
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
