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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * $Id$
 * <p/>
 * Created on 11/11/2006
 * <p/>
 * Unit tests for {@link CalendarParserImpl}.
 *
 * @author Ben Fortuna
 */
public class CalendarParserImplTest extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(CalendarParserImplTest.class);

    private URL resource;

    private int expectedErrorLineNo;

    /**
     * @param resouces a calendar resource
     * @param expectedErrorLineNo
     */
    public CalendarParserImplTest(String resourceString, int expectedErrorLineNo) {
        super("testParserException");
        this.resource = getClass().getResource(resourceString);
        this.expectedErrorLineNo = expectedErrorLineNo;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
    }

    /**
     * Test the accuracy of parser exception line number.
     *
     * @throws IOException
     */
    public void testParserException() throws IOException {
        try {
            Calendars.load(resource);
            fail("Should throw ParserException: [" + resource + "]");
        } catch (ParserException pe) {
            LOG.info(pe.getMessage());
            assertEquals(expectedErrorLineNo, pe.getLineNo());
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
        return super.getName() + " [" + resource + "]";
    }

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new CalendarParserImplTest("/samples/invalid/google_aus_holidays.ics", 11));
        suite.addTest(new CalendarParserImplTest("/samples/invalid/13-MoonPhase.ics", 215));
        suite.addTest(new CalendarParserImplTest("/samples/invalid/overlaps.ics", 1));
        suite.addTest(new CalendarParserImplTest("/samples/invalid/schedule-unstable.ics", 196));
        suite.addTest(new CalendarParserImplTest("/samples/invalid/zidestoreical4jbomb.ics", 10));
        return suite;
    }
}
