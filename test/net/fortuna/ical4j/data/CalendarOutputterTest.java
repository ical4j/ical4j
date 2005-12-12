/*
 * $Id: CalendarOutputterTest.java [Apr 6, 2004]
 *
 * Copyright (c) 2004 Ben Fortuna
 */
package net.fortuna.ical4j.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.FileOnlyFilter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
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
    public CalendarOutputterTest(final String method, final String file) {
        super(method);
        this.filename = file;
    }

    /**
     * @throws Exception
     */
    public void testOutput() throws Exception {
        System.setProperty("ical4j.unfolding.relaxed", "true");

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

            BufferedReader bin = new BufferedReader(new UnfoldingReader(new FileReader(filename)));
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
        return filename;
    }

    /**
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        File[] testFiles = null;

        // valid tests..
        testFiles = new File("etc/samples/valid").listFiles(new FileOnlyFilter());
        for (int i = 0; i < testFiles.length; i++) {
            log.info("Sample [" + testFiles[i] + "]");
            suite.addTest(new CalendarOutputterTest("testOutput", testFiles[i].getPath()));
        }
        
        // invalid tests..
        testFiles = new File("etc/samples/invalid").listFiles(new FileOnlyFilter());
        for (int i = 0; i < testFiles.length; i++) {
            log.info("Sample [" + testFiles[i] + "]");
            suite.addTest(new CalendarOutputterTest("testOutput", testFiles[i].getPath()));
        }

        return suite;
    }
}
