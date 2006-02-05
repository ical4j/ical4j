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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 * Unit tests for the filter implementation.
 * @author Ben Fortuna
 */
public class FilterTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(FilterTest.class);

    private Calendar calendar;
    
    private HasPropertyRule organiserRuleMatch;
    
    private HasPropertyRule organiserRuleNoMatch;
    
    private HasPropertyRule attendeeRuleMatch;
    
    private HasPropertyRule attendeeRuleNoMatch;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();
        calendar = builder.build(new FileReader("etc/samples/valid/incoming.ics"));
        organiserRuleMatch = new HasPropertyRule(new Organizer(new URI("Mailto:B@example.com")));
        organiserRuleNoMatch = new HasPropertyRule(new Organizer(new URI("Mailto:X@example.com")));
        attendeeRuleMatch = new HasPropertyRule(new Attendee(new URI("Mailto:A@example.com")));
        attendeeRuleNoMatch = new HasPropertyRule(new Attendee(new URI("Mailto:X@example.com")));
    }
    
    /**
     * @param filter
     * @return
     */
    private ComponentList filterComponents(Filter filter) {
        ComponentList filtered = (ComponentList) filter.filter(calendar.getComponents());
        
        LOG.info(filtered.size() + " matching component(s).");
        
        return filtered;
    }

    /**
     * Test filtering of a calendar.
     */
    public void testFilterMatchAll() throws URISyntaxException, IOException, ParserException {
        Filter filter = new Filter(new Rule[] {organiserRuleMatch, attendeeRuleMatch}, Filter.MATCH_ALL);
        ComponentList filtered = filterComponents(filter);
        assertTrue(!filtered.isEmpty());

        filter = new Filter(new Rule[] {organiserRuleNoMatch, attendeeRuleMatch}, Filter.MATCH_ALL);
        filtered = filterComponents(filter);
        assertTrue(filtered.isEmpty());

        filter = new Filter(new Rule[] {organiserRuleMatch, attendeeRuleNoMatch}, Filter.MATCH_ALL);
        filtered = filterComponents(filter);
        assertTrue(filtered.isEmpty());
    }

    /**
     * Test filtering of a calendar.
     */
    public void testFilterMatchAny() throws URISyntaxException, IOException, ParserException {
        Filter filter = new Filter(new Rule[] {organiserRuleMatch, attendeeRuleMatch}, Filter.MATCH_ANY);
        ComponentList filtered = filterComponents(filter);
        assertTrue(!filtered.isEmpty());

        filter = new Filter(new Rule[] {organiserRuleNoMatch, attendeeRuleMatch}, Filter.MATCH_ANY);
        filtered = filterComponents(filter);
        assertTrue(!filtered.isEmpty());

        filter = new Filter(new Rule[] {organiserRuleMatch, attendeeRuleNoMatch}, Filter.MATCH_ANY);
        filtered = filterComponents(filter);
        assertTrue(!filtered.isEmpty());
    }

}
