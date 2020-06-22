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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Uid;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * $Id$
 *
 * Created on 10/11/2006
 *
 * Utility method for working with {@link Calendar}s.
 * @author Ben Fortuna
 */
public final class Calendars {

    /**
     * Constructor made private to enforce static nature.
     */
    private Calendars() {
    }

    /**
     * Loads a calendar from the specified file.
     * @param filename the name of the file from which to load calendar data
     * @return returns a new calendar instance initialised from the specified file
     * @throws IOException occurs when there is an error reading the specified file
     * @throws ParserException occurs when the data in the specified file is invalid
     */
    public static Calendar load(final String filename) throws IOException, ParserException {
        return new CalendarBuilder().build(Files.newInputStream(Paths.get(filename)));
    }

    /**
     * Loads a calendar from the specified URL.
     * @param url the URL from which to load calendar data
     * @return returns a new calendar instance initialised from the specified URL
     * @throws IOException occurs when there is an error reading from the specified URL
     * @throws ParserException occurs when the data in the specified URL is invalid
     */
    public static Calendar load(final URL url) throws IOException, ParserException {
        final CalendarBuilder builder = new CalendarBuilder();
        return builder.build(url.openStream());
    }

    /**
     * Merge all properties and components from two specified calendars into one instance.
     * Note that the merge process is not very sophisticated, and may result in invalid calendar
     * data (e.g. multiple properties of a type that should only be specified once).
     * @param c1 the first calendar to merge
     * @param c2 the second calendar to merge
     * @return a Calendar instance containing all properties and components from both of the specified calendars
     */
    public static Calendar merge(final Calendar c1, final Calendar c2) {
        List<Property> mergedProperties = new ArrayList<>();
        List<CalendarComponent> mergedComponents = new ArrayList<>();
        mergedProperties.addAll(c1.getProperties().getAll());
        for (final Property p : c2.getProperties().getAll()) {
            if (!mergedProperties.contains(p)) {
                mergedProperties.add(p);
            }
        }
        mergedComponents.addAll(c1.getComponents().getAll());
        for (final CalendarComponent c : c2.getComponents().getAll()) {
            if (!mergedComponents.contains(c)) {
                mergedComponents.add(c);
            }
        }
        return new Calendar(new PropertyList(mergedProperties),
                new ComponentList<>(mergedComponents));
    }

    /**
     * Wraps a component in a calendar.
     * @param component the component to wrap with a calendar
     * @return a calendar containing the specified component
     */
    public static Calendar wrap(final CalendarComponent... component) {
        return wrap(new PropertyList(), component);
    }

    public static Calendar wrap(PropertyList properties, final CalendarComponent... component) {
        final ComponentList<CalendarComponent> components = new ComponentList<>(Arrays.asList(component));
        return new Calendar(properties, components);
    }
    
    /**
     * Splits a calendar object into distinct calendar objects for unique
     * identifers (UID).
     * @param calendar a calendar instance
     * @return an array of calendar objects
     */
    public static Calendar[] split(final Calendar calendar) {
        // if calendar contains one component or less, or is composed entirely of timezone
        // definitions, return the original calendar unmodified..
        if (calendar.getComponents().getAll().size() <= 1
                || calendar.getComponents().get(Component.VTIMEZONE).size() == calendar.getComponents().getAll().size()) {
            return new Calendar[] {calendar};
        }
        
        final List<VTimeZone> timezoneList = calendar.getComponents(Component.VTIMEZONE);
		final IndexedComponentList<VTimeZone> timezones = new IndexedComponentList<>(
        		timezoneList, Property.TZID);
        
        final Map<Uid, Calendar> calendars = new HashMap<Uid, Calendar>();
        for (final CalendarComponent c : calendar.getComponents().getAll()) {
            if (c instanceof VTimeZone) {
                continue;
            }
            
            final Optional<Uid> uid = c.getProperties().getFirst(Property.UID);
            if (uid.isPresent()) {
                Calendar uidCal = calendars.get(uid.get());
                if (uidCal == null) {
                    // remove METHOD property for split calendars..
                    PropertyList splitProps = (PropertyList) calendar.getProperties().removeAll(Property.METHOD);
                    uidCal = new Calendar(splitProps, new ComponentList<>());
                    calendars.put(uid.get(), uidCal);
                }

                for (final Property p : c.getProperties().getAll()) {
                    final Optional<TzId> tzid = p.getParameters().getFirst(Parameter.TZID);
                    if (tzid.isPresent()) {
                        final VTimeZone timezone = timezones.getComponent(tzid.get().getValue());
                        if (!uidCal.getComponents().getAll().contains(timezone)) {
                            uidCal.add(timezone);
                        }
                    }
                }
                uidCal.add(c);
            }
        }
        return calendars.values().toArray(new Calendar[0]);
    }
    
    /**
     * Returns a unique identifier as specified by components in the provided calendar.
     * @param calendar a calendar instance
     * @return the UID property
     * @throws ConstraintViolationException if zero or more than one unique identifer is found in the specified calendar
     */
    public static Uid getUid(final Calendar calendar) throws ConstraintViolationException {
        Uid uid = null;
        for (final Component c : calendar.getComponents().getAll()) {
            for (final Property foundUid : c.getProperties().get(Property.UID)) {
                if (uid != null && !uid.equals(foundUid)) {
                    throw new ConstraintViolationException("More than one UID found in calendar");
                }
                uid = (Uid) foundUid;
            }
        }
        if (uid == null) {
            throw new ConstraintViolationException("Calendar must specify a single unique identifier (UID)");
        }
        return uid;
    }
    
    /**
     * Returns an appropriate MIME Content-Type for the specified calendar object.
     * @param calendar a calendar instance
     * @param charset an optional encoding
     * @return a content type string
     */
    public static String getContentType(Calendar calendar, Charset charset) {
        final StringBuilder b = new StringBuilder("text/calendar");
        
        final Optional<Method> method = calendar.getProperties().getFirst(Property.METHOD);
        if (method.isPresent()) {
            b.append("; method=");
            b.append(method.get().getValue());
        }
        
        if (charset != null) {
            b.append("; charset=");
            b.append(charset);
        }
        return b.toString();
    }
}
