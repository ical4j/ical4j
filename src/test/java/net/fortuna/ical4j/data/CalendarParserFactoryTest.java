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

/**
 * $Id$
 *
 * Created on 18/11/2007
 *
 * @author fortuna
 *
 */
public class CalendarParserFactoryTest extends TestCase {

    private String originalParserFactory;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        originalParserFactory = System.getProperty(CalendarParserFactory.KEY_FACTORY_CLASS);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        if (originalParserFactory != null) {
            System.setProperty(CalendarParserFactory.KEY_FACTORY_CLASS,
                    originalParserFactory);
        }
        else {
            System.clearProperty(CalendarParserFactory.KEY_FACTORY_CLASS);
        }
    }
    
    /**
     * Test method for {@link net.fortuna.ical4j.data.CalendarParserFactory#createParser()}.
     */
    public void testCreateDefaultParser() {
        assertTrue(CalendarParserFactory.getInstance().createParser() instanceof CalendarParserImpl);
    }
    
    /**
     * Test method for {@link net.fortuna.ical4j.data.CalendarParserFactory#createParser()}.
     */
    /*
    public void testCreateHCalendarParser() {
        System.setProperty(CalendarParserFactory.KEY_FACTORY_CLASS,
                "net.fortuna.ical4j.data.HCalendarParserFactory");
        assertTrue(CalendarParserFactory.getInstance().createParser() instanceof HCalendarParser);
    }
    */

}
