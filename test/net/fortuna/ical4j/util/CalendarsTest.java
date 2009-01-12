/**
 * Copyright (c) 2009, Ben Fortuna
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import junit.framework.TestCase;

/**
 * $Id$
 *
 * Created on 10/11/2006
 *
 * Unit tests for {@link Calendars}.
 * @author Ben Fortuna
 */
public class CalendarsTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(CalendarsTest.class);
    
    /**
     * Test loading of calendars.
     * @throws IOException
     * @throws ParserException
     */
    public void testLoad() throws IOException, ParserException {
        assertNotNull(Calendars.load(
                "etc/samples/valid/Australian32Holidays.ics"));
        
        try {
            Calendars.load("etc/samples/valid/doesnt-exist.ics");
            fail("Should throw FileNotFoundException");
        }
        catch (FileNotFoundException fnfe) {
            LOG.info("Caught exception: " + fnfe.getMessage());
        }
        
        try {
            Calendars.load("etc/samples/invalid/google_aus_holidays.ics");
            fail("Should throw ParserException");
        }
        catch (ParserException pe) {
            LOG.info("Caught exception: " + pe.getMessage());
        }
    }
    
    /**
     * Test merging of calendars.
     */
    public void testMerge() throws IOException, ParserException {
        Calendar calendar1 = Calendars.load(
                "etc/samples/valid/Australian32Holidays.ics");
        Calendar calendar2 = Calendars.load(
                "etc/samples/valid/OZMovies.ics");
        
        Calendar result = Calendars.merge(calendar1, calendar2);
        
        for (Iterator i = calendar1.getProperties().iterator(); i.hasNext();) {
            Object p = i.next();
            assertTrue("Property [" + p + "] not found in merged calendar",
                    result.getProperties().contains(p));
        }
        for (Iterator i = calendar1.getComponents().iterator(); i.hasNext();) {
            Object c = i.next();
            assertTrue("Component [" + c + "] not found in merged calendar",
                    result.getComponents().contains(c));
        }
        for (Iterator i = calendar2.getProperties().iterator(); i.hasNext();) {
            Object p = i.next();
            assertTrue("Property [" + p + "] not found in merged calendar",
                    result.getProperties().contains(p));
        }
        for (Iterator i = calendar2.getComponents().iterator(); i.hasNext();) {
            Object c = i.next();
            assertTrue("Component [" + c + "] not found in merged calendar",
                    result.getComponents().contains(c));
        }
    }
    
    /**
     * Test calendar split.
     */
    public void testSplit() throws IOException, ParserException {
        Calendar calendar = Calendars.load("etc/samples/valid/Australian32Holidays.ics");
        Calendar[] split = Calendars.split(calendar);
        assertEquals(10, split.length);
    }
}
