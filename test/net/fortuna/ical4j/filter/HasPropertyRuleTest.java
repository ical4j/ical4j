/*
 * $Id$
 *
 * Created on 2/02/2006
 *
 * Copyright (c) 2005, Ben Fortuna
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
package net.fortuna.ical4j.filter;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Unit tests for the has property filter rule.
 * @author Ben Fortuna
 */
public class HasPropertyRuleTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(HasPropertyRuleTest.class);
    
    private Calendar calendar;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();
        calendar = builder.build(new FileReader("etc/samples/valid/incoming.ics"));
    }
    
    /**
     * Test filtering of a calendar on organizer.
     */
    public void testFilterOrganizer() throws URISyntaxException, IOException, ParserException {
        Organizer organiser = new Organizer(new URI("Mailto:B@example.com"));
        Filter filter = new Filter(new HasPropertyRule(organiser));
        
        ComponentList filtered = (ComponentList) filter.filter(calendar.getComponents());
        assertTrue(!filtered.isEmpty());
        
        LOG.info(filtered.size() + " matching organizer(s).");
    }
    
    /**
     * Test filtering of a calendar on attendee.
     */
    public void testFilter() throws URISyntaxException, IOException, ParserException {
        Attendee attendee = new Attendee(new URI("Mailto:A@example.com"));
        Filter filter = new Filter(new HasPropertyRule(attendee));
        
        ComponentList filtered = (ComponentList) filter.filter(calendar.getComponents());
        assertTrue(!filtered.isEmpty());
        
        LOG.info(filtered.size() + " matching attendee(s).");
    }
}
