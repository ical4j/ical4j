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

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.predicate.PropertyEqualToRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * $Id$
 *
 * Created on 2/02/2006
 *
 * Unit tests for the filter implementation.
 * @author Ben Fortuna
 */
public class FilterTest {

    /**
     * Asserts that filtering {@code collection} with {@code filter} yields an empty result.
     */
    public static <T extends Component> void assertFilteredIsEmpty(Filter<T> filter, Collection<T> collection) {
        assertTrue(filter.filter(collection).isEmpty());
    }

    /**
     * Asserts that filtering {@code collection} with {@code filter} yields a non-empty result.
     */
    public static <T extends Component> void assertFilteredIsNotEmpty(Filter<T> filter, Collection<T> collection) {
        assertFalse(filter.filter(collection).isEmpty());
    }

    /**
     * Asserts that filtering {@code collection} with {@code filter} yields {@code expectedFilteredSize}
     * results.
     */
    public static <T extends Component> void assertFilteredSize(Filter<T> filter, Collection<T> collection,
                                                                int expectedFilteredSize) {
        assertEquals(expectedFilteredSize, filter.filter(collection).size());
    }

    @ParameterizedTest(name = "filteredSize [{2}]")
    @MethodSource("filteredSizeData")
    public void testFilteredSize(Filter<CalendarComponent> filter, Collection<CalendarComponent> collection,
                                 int expectedFilteredSize) {
        assertFilteredSize(filter, collection, expectedFilteredSize);
    }

    @ParameterizedTest(name = "filteredIsEmpty")
    @MethodSource("filteredIsEmptyData")
    public void testFilteredIsEmpty(Filter<CalendarComponent> filter, Collection<CalendarComponent> collection) {
        assertFilteredIsEmpty(filter, collection);
    }

    @SuppressWarnings("unchecked")
    private static FilterFixture fixture() throws URISyntaxException {
        Organizer organizer = new Organizer(new URI("Mailto:B@example.com"));
        Attendee a1 = new Attendee(new URI("Mailto:A@example.com"));
        Attendee a2 = new Attendee(new URI("Mailto:C@example.com"));

        var e1 = (VEvent) new VEvent().add(organizer).add(a1);
        var e2 = (VEvent) new VEvent().add(organizer).add(a2);
        var e3 = (VEvent) new VEvent().add(organizer).add(a1).add(a2);

        Calendar calendar = new Calendar(new ComponentList<>(Arrays.asList(e1, e2, e3)));

        Predicate<Component> organiserRuleMatch = new PropertyEqualToRule<>(organizer);
        Predicate<Component> attendee1RuleMatch = new PropertyEqualToRule<>(a1);
        Predicate<Component> organiserRuleNoMatch = new PropertyEqualToRule<>(
                new Organizer(new URI("Mailto:X@example.com")));
        Predicate<Component> attendeeRuleNoMatch = new PropertyEqualToRule<>(
                new Attendee(new URI("Mailto:X@example.com")));

        return new FilterFixture(calendar, organiserRuleMatch, attendee1RuleMatch,
                organiserRuleNoMatch, attendeeRuleNoMatch);
    }

    @SuppressWarnings("unchecked")
    static Stream<Arguments> filteredSizeData() throws URISyntaxException, FileNotFoundException,
            IOException, ParserException {
        FilterFixture fx = fixture();

        // MATCH_ALL: organiser AND attendee1 -> 2 events match
        Filter<CalendarComponent> f1 = new Filter<>(
                new Predicate[]{fx.organiserMatch, fx.attendee1Match}, Filter.MATCH_ALL);

        // MATCH_ANY: organiser OR attendee1 -> 3 events match
        Filter<CalendarComponent> f2 = new Filter<>(
                new Predicate[]{fx.organiserMatch, fx.attendee1Match}, Filter.MATCH_ANY);

        // MATCH_ANY: organiserNoMatch OR attendee1 -> 2 events match
        Filter<CalendarComponent> f3 = new Filter<>(
                new Predicate[]{fx.organiserNoMatch, fx.attendee1Match}, Filter.MATCH_ANY);

        // MATCH_ANY: organiser OR attendeeNoMatch -> 3 events match
        Filter<CalendarComponent> f4 = new Filter<>(
                new Predicate[]{fx.organiserMatch, fx.attendeeNoMatch}, Filter.MATCH_ANY);

        return Stream.of(
                Arguments.of(f1, fx.calendar.getComponents(), 2),
                Arguments.of(f2, fx.calendar.getComponents(), 3),
                Arguments.of(f3, fx.calendar.getComponents(), 2),
                Arguments.of(f4, fx.calendar.getComponents(), 3)
        );
    }

    @SuppressWarnings("unchecked")
    static Stream<Arguments> filteredIsEmptyData() throws URISyntaxException, FileNotFoundException,
            IOException, ParserException {
        FilterFixture fx = fixture();

        // MATCH_ALL: organiserNoMatch AND attendee1 -> empty
        Filter<CalendarComponent> f1 = new Filter<>(
                new Predicate[]{fx.organiserNoMatch, fx.attendee1Match}, Filter.MATCH_ALL);

        // MATCH_ALL: organiser AND attendeeNoMatch -> empty
        Filter<CalendarComponent> f2 = new Filter<>(
                new Predicate[]{fx.organiserMatch, fx.attendeeNoMatch}, Filter.MATCH_ALL);

        return Stream.of(
                Arguments.of(f1, fx.calendar.getComponents()),
                Arguments.of(f2, fx.calendar.getComponents())
        );
    }

    private static final class FilterFixture {
        final Calendar calendar;
        final Predicate<Component> organiserMatch;
        final Predicate<Component> attendee1Match;
        final Predicate<Component> organiserNoMatch;
        final Predicate<Component> attendeeNoMatch;

        FilterFixture(Calendar calendar,
                      Predicate<Component> organiserMatch,
                      Predicate<Component> attendee1Match,
                      Predicate<Component> organiserNoMatch,
                      Predicate<Component> attendeeNoMatch) {
            this.calendar = calendar;
            this.organiserMatch = organiserMatch;
            this.attendee1Match = attendee1Match;
            this.organiserNoMatch = organiserNoMatch;
            this.attendeeNoMatch = attendeeNoMatch;
        }
    }
}
