/*
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 *
 * Copyright (c) 2004 Ben Fortuna
 */
package net.fortuna.ical4j.data;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.Description;

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
     * Class to test for Calendar build(InputStream)
     * @throws IOException
     * @throws ParserException
     * @throws URISyntaxException
     * @throws ParseException
     */
    public final void testBuildInputStream() throws IOException,
            ParserException, URISyntaxException, ParseException {

        FileInputStream fin = new FileInputStream(filename);

        Calendar calendar = builder.build(fin);

        assertNotNull(calendar);

        log.info("Calendar:\n=========\n" + calendar.toString());

        for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            Component c = (Component) i.next();

            Description description = (Description) c.getProperties().getProperty(Property.DESCRIPTION);

            if (description != null) {
                log.info("Description [" + description.getValue() + "]");
            }
        }
    }

    /**
     * Test suite.
     * @return
     */
    public static Test suite() {

        TestSuite suite = new TestSuite();

        File[] samples = new File("c:/Development/workspace/ical4j/etc/samples").listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith("sunbird_sample.ics");
			}
		});

        for (int i = 0; i < samples.length; i++) {
            log.info("Sample [" + samples[i] + "]");
			suite.addTest(new CalendarBuilderTest("testBuildInputStream", samples[i].getPath()));
        }

        return suite;
    }
}