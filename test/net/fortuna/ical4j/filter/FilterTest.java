/*
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;

/**
 * $Id$
 *
 * Created on 2/02/2006
 *
 * Unit tests for the filter implementation.
 * @author Ben Fortuna
 */
public class FilterTest extends TestCase {

    private Filter filter;
    
    private Collection collection;
    
    private int expectedFilteredSize;
    
    /**
     * @param testMethod
     * @param filter
     * @param collection
     */
    public FilterTest(String testMethod, Filter filter, Collection collection) {
        super(testMethod);
        this.filter = filter;
        this.collection = collection;
    }

    /**
     * @param testMethod
     * @param filter
     * @param collection
     * @param expectedFilteredSize
     */
    public FilterTest(String testMethod, Filter filter, Collection collection, int expectedFilteredSize) {
        this(testMethod, filter, collection);
        this.expectedFilteredSize = expectedFilteredSize;
    }
    
    /**
     * 
     */
    public void testFilteredIsEmpty() {
        assertTrue(filter.filter(collection).isEmpty());
    }
    
    /**
     * 
     */
    public void testFilteredIsNotEmpty() {
        assertFalse(filter.filter(collection).isEmpty());
    }

    /**
     * 
     */
    public void testFilteredSize() {
        assertEquals(expectedFilteredSize, filter.filter(collection).size());
    }
    
    /**
     * @return
     * @throws ParserException 
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws URISyntaxException 
     */
    public static TestSuite suite() throws FileNotFoundException, IOException, ParserException, URISyntaxException {
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(new FileReader("etc/samples/valid/incoming.ics"));
        Rule organiserRuleMatch = new HasPropertyRule(new Organizer(new URI("Mailto:B@example.com")));
        Rule organiserRuleNoMatch = new HasPropertyRule(new Organizer(new URI("Mailto:X@example.com")));
        Rule attendeeRuleMatch = new HasPropertyRule(new Attendee(new URI("Mailto:A@example.com")));
        Rule attendeeRuleNoMatch = new HasPropertyRule(new Attendee(new URI("Mailto:X@example.com")));
        
        TestSuite suite = new TestSuite();
        //testFilterMatchAll..
        Filter filter = new Filter(new Rule[] {organiserRuleMatch, attendeeRuleMatch}, Filter.MATCH_ALL);
        suite.addTest(new FilterTest("testFilteredIsNotEmpty", filter, calendar.getComponents()));

        filter = new Filter(new Rule[] {organiserRuleNoMatch, attendeeRuleMatch}, Filter.MATCH_ALL);
        suite.addTest(new FilterTest("testFilteredIsEmpty", filter, calendar.getComponents()));

        filter = new Filter(new Rule[] {organiserRuleMatch, attendeeRuleNoMatch}, Filter.MATCH_ALL);
        suite.addTest(new FilterTest("testFilteredIsEmpty", filter, calendar.getComponents()));
        
        //testFilterMatchAny..
        filter = new Filter(new Rule[] {organiserRuleMatch, attendeeRuleMatch}, Filter.MATCH_ANY);
        suite.addTest(new FilterTest("testFilteredIsNotEmpty", filter, calendar.getComponents()));

        filter = new Filter(new Rule[] {organiserRuleNoMatch, attendeeRuleMatch}, Filter.MATCH_ANY);
        suite.addTest(new FilterTest("testFilteredIsNotEmpty", filter, calendar.getComponents()));

        filter = new Filter(new Rule[] {organiserRuleMatch, attendeeRuleNoMatch}, Filter.MATCH_ANY);
        suite.addTest(new FilterTest("testFilteredIsNotEmpty", filter, calendar.getComponents()));
        return suite;
    }
}
