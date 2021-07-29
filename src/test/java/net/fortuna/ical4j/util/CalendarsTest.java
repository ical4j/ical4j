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
package net.fortuna.ical4j.util;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * $Id$
 * <p/>
 * Created on 10/11/2006
 * <p/>
 * Unit tests for {@link Calendars}.
 *
 * @author Ben Fortuna
 */
public class CalendarsTest extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(CalendarsTest.class);

    private String path;
    
    private URL resource;

    private Calendar[] calendars;

    private Calendar calendar;

    private int expectedCount;

    private Charset charset;

    private String expectedContentType;

    /**
     * @param testMethod
     * @param path
     */
    public CalendarsTest(String testMethod, String path) {
        super(testMethod);
        this.path = path;
        this.resource = getClass().getResource(path);
    }

    /**
     * @param testMethod
     */
    public CalendarsTest(String testMethod, Calendar[] calendars) {
        super(testMethod);
        this.calendars = calendars;
    }

    /**
     * @param testMethod
     */
    public CalendarsTest(String testMethod, Calendar calendar, int expectedCount) {
        super(testMethod);
        this.calendar = calendar;
        this.expectedCount = expectedCount;
    }

    /**
     * @param testMethod
     */
    public CalendarsTest(String testMethod, Calendar calendar, Charset charset, String expectedContentType) {
        super(testMethod);
        this.calendar = calendar;
        this.charset = charset;
        this.expectedContentType = expectedContentType;
    }

    /**
     * Test loading of calendars.
     *
     * @throws IOException
     * @throws ParserException
     */
    public void testLoad() throws IOException, ParserException {
        assertNotNull(Calendars.load(resource));
    }

    /**
     * Test loading of calendars.
     *
     * @throws IOException
     * @throws ParserException
     */
    public void testLoadFileNotFoundException() throws IOException, ParserException {
        try {
            Calendars.load(path);
            fail("Should throw FileNotFoundException");
        } catch (NoSuchFileException fnfe) {
            LOG.info("Caught exception: " + fnfe.getMessage());
        }
    }

    /**
     * Test loading of calendars.
     *
     * @throws IOException
     */
    public void testLoadParserException() throws IOException {
        try {
            Calendars.load(resource);
            fail("Should throw ParserException");
        } catch (ParserException pe) {
            LOG.info("Caught exception: " + pe.getMessage());
        }
    }

    /**
     * Test merging of calendars.
     */
    public void testMerge() throws IOException, ParserException {
        Calendar result = calendars[0];
        for (int i = 1; i < calendars.length; i++) {
            result = Calendars.merge(result, calendars[i]);
        }

        for (Calendar value : calendars) {
            for (Property p : value.getProperties()) {
                assertTrue("Property [" + p + "] not found in merged calendar",
                        result.getProperties().contains(p));
            }
            for (Component c : value.getComponents()) {
                assertTrue("Component [" + c + "] not found in merged calendar",
                        result.getComponents().contains(c));
            }
        }
    }

    /**
     * Test calendar split.
     */
    public void testSplit() throws IOException, ParserException {
        Calendar[] split = Calendars.split(calendar);
        assertEquals(expectedCount, split.length);
    }

    /**
     *
     */
    public void testGetContentType() {
        assertEquals(expectedContentType, Calendars.getContentType(calendar, charset));
    }
    
    /**
     * Ensures that after de-serialization a calendar can still be copied.
     */
    public void testShouldSerializeDeserializeCorrectly()
            throws IOException, ClassNotFoundException, ParseException, URISyntaxException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this.calendar);
        oos.flush();

        assertNotNull(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Calendar copy = (Calendar) ois.readObject();
        assertNotNull(copy);
        assertEquals(copy, this.calendar);
        Calendar newCopy = new Calendar(copy);
        assertEquals(newCopy, copy);
    }

    /**
     * @return
     * @throws ParserException
     * @throws IOException
     */
    public static TestSuite suite() throws IOException, ParserException {
        TestSuite suite = new TestSuite();

        suite.addTest(new CalendarsTest("testLoad", "/samples/valid/Australian32Holidays.ics"));
        suite.addTest(new CalendarsTest("testLoadFileNotFoundException", "/samples/valid/doesnt-exist.ics"));
        suite.addTest(new CalendarsTest("testLoadParserException", "/samples/invalid/google_aus_holidays.ics"));

        List<Calendar> calendars = new ArrayList<Calendar>();
        calendars.add(Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/valid/Australian32Holidays.ics"))));
        calendars.add(Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/valid/OZMovies.ics"))));
        suite.addTest(new CalendarsTest("testMerge", (Calendar[]) calendars.toArray(new Calendar[0])));

        Calendar calendar = Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/valid/Australian32Holidays.ics")));
        suite.addTest(new CalendarsTest("testSplit", calendar, 10));

        suite.addTest(new CalendarsTest("testGetContentType", calendar, null, "text/calendar"));

        calendar = Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/valid/OZMovies.ics")));
        suite.addTest(new CalendarsTest("testGetContentType", calendar, null, "text/calendar; method=PUBLISH"));
        suite.addTest(new CalendarsTest("testGetContentType", calendar, StandardCharsets.US_ASCII, "text/calendar; method=PUBLISH; charset=US-ASCII"));
        suite.addTest(new CalendarsTest("testShouldSerializeDeserializeCorrectly",
                Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/valid/Australian32Holidays.ics"))), -1));
        
        return suite;
    }
}
