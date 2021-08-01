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
package net.fortuna.ical4j.filter;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.predicate.PropertyEqualToRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * $Id$
 *
 * Created on 2/02/2006
 *
 * Unit tests for the filter implementation.
 * @author Ben Fortuna
 */
public class FilterTest<T extends Component> extends TestCase {

    private Filter<T> filter;
    
    private Collection<T> collection;
    
    private int expectedFilteredSize;
    
    /**
     * @param testMethod
     * @param filter
     * @param collection
     */
    public FilterTest(String testMethod, Filter<T> filter, Collection<T> collection) {
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
    public FilterTest(String testMethod, Filter<T> filter, Collection<T> collection, int expectedFilteredSize) {
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
    @SuppressWarnings("unchecked")
	public static TestSuite suite() throws FileNotFoundException, IOException, ParserException, URISyntaxException {
        Organizer organizer = new Organizer(new URI("Mailto:B@example.com"));
        Attendee a1 = new Attendee(new URI("Mailto:A@example.com"));
        Attendee a2 = new Attendee(new URI("Mailto:C@example.com"));
        
        VEvent e1 = new VEvent();
        e1.getProperties().add(organizer);
        e1.getProperties().add(a1);
        
        VEvent e2 = new VEvent();
        e2.getProperties().add(organizer);
        e2.getProperties().add(a2);
        
        VEvent e3 = new VEvent();
        e3.getProperties().add(organizer);
        e3.getProperties().add(a1);
        e3.getProperties().add(a2);
        
        Calendar calendar = new Calendar();
        calendar.getComponents().add(e1);
        calendar.getComponents().add(e2);
        calendar.getComponents().add(e3);
        
        Predicate<Component> organiserRuleMatch = new PropertyEqualToRule<>(organizer);
        Predicate<Component> attendee1RuleMatch = new PropertyEqualToRule<>(a1);

        Predicate<Component> organiserRuleNoMatch = new PropertyEqualToRule<>(new Organizer(new URI("Mailto:X@example.com")));
        Predicate<Component> attendeeRuleNoMatch = new PropertyEqualToRule<>(new Attendee(new URI("Mailto:X@example.com")));
        
        TestSuite suite = new TestSuite();
        //testFilterMatchAll..
        Filter<CalendarComponent> filter = new Filter<CalendarComponent>(new Predicate[] {organiserRuleMatch, attendee1RuleMatch}, Filter.MATCH_ALL);
        suite.addTest(new FilterTest<CalendarComponent>("testFilteredSize", filter, calendar.getComponents(), 2));

        filter = new Filter<CalendarComponent>(new Predicate[] {organiserRuleNoMatch, attendee1RuleMatch}, Filter.MATCH_ALL);
        suite.addTest(new FilterTest<CalendarComponent>("testFilteredIsEmpty", filter, calendar.getComponents()));

        filter = new Filter<CalendarComponent>(new Predicate[] {organiserRuleMatch, attendeeRuleNoMatch}, Filter.MATCH_ALL);
        suite.addTest(new FilterTest<CalendarComponent>("testFilteredIsEmpty", filter, calendar.getComponents()));
        
        //testFilterMatchAny..
        filter = new Filter<CalendarComponent>(new Predicate[] {organiserRuleMatch, attendee1RuleMatch}, Filter.MATCH_ANY);
        suite.addTest(new FilterTest<CalendarComponent>("testFilteredSize", filter, calendar.getComponents(), 3));

        filter = new Filter<CalendarComponent>(new Predicate[] {organiserRuleNoMatch, attendee1RuleMatch}, Filter.MATCH_ANY);
        suite.addTest(new FilterTest<CalendarComponent>("testFilteredSize", filter, calendar.getComponents(), 2));

        filter = new Filter<CalendarComponent>(new Predicate[] {organiserRuleMatch, attendeeRuleNoMatch}, Filter.MATCH_ANY);
        suite.addTest(new FilterTest<CalendarComponent>("testFilteredSize", filter, calendar.getComponents(), 3));
        return suite;
    }
}
