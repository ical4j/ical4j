/*
 * $Id: CalendarOutputterTest.java [Apr 6, 2004]
 *
 * Copyright (c) 2004 Ben Fortuna
 */
package net.fortuna.ical4j.data;

import java.io.File;
import java.io.FileFilter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

/**
 * Test case for iCalendarOutputter.
 *
 * @author benf
 */
public class CalendarOutputterTest extends TestCase {

    private static Log log = LogFactory.getLog(CalendarOutputterTest.class);

    private String filename;

    public CalendarOutputterTest(String method, String file) {

        super(method);

        this.filename = file;
    }

    public void testOutput() throws Exception {

        CalendarBuilder builder = new CalendarBuilder();

        FileInputStream fin = new FileInputStream(filename);

        Calendar calendar = builder.build(fin);

        CalendarOutputter outputter = new CalendarOutputter();

        OutputStream out = new ByteArrayOutputStream();

        outputter.output(calendar, out);

        log.info(out.toString());

        BufferedReader bin = new BufferedReader(new UnfoldingReader(new FileReader(filename)));

        StringWriter rout = new StringWriter();

        BufferedWriter bout = new BufferedWriter(rout);

        String line = null;

        while ((line = bin.readLine()) != null) {
            bout.write(line);
            bout.write('\n');
        }

        bout.close();
        bin.close();

        String rawData = rout.getBuffer().toString();

        assertEquals(rawData, out.toString());
    }

    public static Test suite() {

        TestSuite suite = new TestSuite();

        File[] samples = new File("f:/development/workspace/ical4j/etc/samples").listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith("OZMovies.ics");
			}
		});

        for (int i = 0; i < samples.length; i++) {
			log.info("Sample [" + samples[i] + "]");

			suite.addTest(new CalendarOutputterTest("testOutput", samples[i].getPath()));
		}

        return suite;
    }
}