/*
 * $Id$
 *
 * Created on 11/11/2006
 *
 * Copyright (c) 2006, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.data;

import java.io.IOException;

import junit.framework.TestCase;
import net.fortuna.ical4j.util.Calendars;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Unit tests for {@link CalendarParserImpl}.
 * @author Ben Fortuna
 */
public class CalendarParserImplTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(
            CalendarParserImplTest.class);
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_UNFOLDING, false);
    }
    
    /**
     * Test the accuracy of parser exception line number.
     */
    public void testParseExceptionLineNo() throws IOException {
        try {
            loadCalendar("etc/samples/invalid/google_aus_holidays.ics");
        }
        catch (ParserException pe) {
            assertEquals(11, pe.getLineNo());
        }

        try {
            loadCalendar("etc/samples/invalid/13-MoonPhase.ics");
        }
        catch (ParserException pe) {
            assertEquals(215, pe.getLineNo());
        }

        try {
            loadCalendar("etc/samples/invalid/CalendarDataFile.ics");
        }
        catch (ParserException pe) {
            assertEquals(24, pe.getLineNo());
        }

        try {
            loadCalendar("etc/samples/invalid/overlaps.ics");
        }
        catch (ParserException pe) {
            assertEquals(1, pe.getLineNo());
        }

        try {
            loadCalendar("etc/samples/invalid/phpicalendar_sample.ics");
        }
        catch (ParserException pe) {
            assertEquals(166, pe.getLineNo());
        }

        try {
            loadCalendar("etc/samples/invalid/schedule-unstable.ics");
        }
        catch (ParserException pe) {
            assertEquals(196, pe.getLineNo());
        }

        try {
            loadCalendar("etc/samples/invalid/smallcluster.ics");
        }
        catch (ParserException pe) {
            assertEquals(2, pe.getLineNo());
        }

        try {
            loadCalendar("etc/samples/invalid/twinkle.ics");
        }
        catch (ParserException pe) {
            assertEquals(67, pe.getLineNo());
        }

        try {
            loadCalendar("etc/samples/invalid/zidestoreical4jbomb.ics");
        }
        catch (ParserException pe) {
            assertEquals(10, pe.getLineNo());
        }
    }
    
    /**
     * @param filename
     * @throws IOException
     * @throws ParserException
     */
    private void loadCalendar(String filename)
        throws IOException, ParserException {
        
        try {
            Calendars.load(filename);
            fail("Should throw ParserException: [" + filename + "]");
        }
        catch (ParserException pe) {
            LOG.info(pe.getMessage());
            throw pe;
        }
    }
}
