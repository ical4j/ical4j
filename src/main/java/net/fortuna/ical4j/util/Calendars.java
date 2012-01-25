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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.IndexedComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Uid;

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
        final FileInputStream fin = new FileInputStream(filename);
        final CalendarBuilder builder = new CalendarBuilder();
        return builder.build(fin);
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
        final Calendar result = new Calendar();
        result.getProperties().addAll(c1.getProperties());
        for (final Iterator i = c2.getProperties().iterator(); i.hasNext();) {
            final Property p = (Property) i.next();
            if (!result.getProperties().contains(p)) {
                result.getProperties().add(p);
            }
        }
        result.getComponents().addAll(c1.getComponents());
        for (final Iterator i = c2.getComponents().iterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            if (!result.getComponents().contains(c)) {
                result.getComponents().add(c);
            }
        }
        return result;
    }

    /**
     * Wraps a component in a calendar.
     * @param component the component to wrap with a calendar
     * @return a calendar containing the specified component
     */
    public static Calendar wrap(final Component component) {
        final ComponentList components = new ComponentList();
        components.add(component);
        return new Calendar(components);
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
        if (calendar.getComponents().size() <= 1
                || calendar.getComponents(Component.VTIMEZONE).size() == calendar.getComponents().size()) {
            return new Calendar[] {calendar};
        }
        
        final IndexedComponentList timezones = new IndexedComponentList(calendar.getComponents(Component.VTIMEZONE),
                Property.TZID);
        
        final Map calendars = new HashMap();
        for (final Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            if (c instanceof VTimeZone) {
                continue;
            }
            
            final Uid uid = (Uid) c.getProperty(Property.UID);
            
            Calendar uidCal = (Calendar) calendars.get(uid);
            if (uidCal == null) {
                uidCal = new Calendar(calendar.getProperties(), new ComponentList());
                // remove METHOD property for split calendars..
                for (final Iterator mp = uidCal.getProperties(Property.METHOD).iterator(); mp.hasNext();) {
                    uidCal.getProperties().remove(mp.next());
                }
                calendars.put(uid, uidCal);
            }
            
            for (final Iterator j = c.getProperties().iterator(); j.hasNext();) {
                final Property p = (Property) j.next();
                final TzId tzid = (TzId) p.getParameter(Parameter.TZID);
                if (tzid != null) {
                    final VTimeZone timezone = (VTimeZone) timezones.getComponent(tzid.getValue());
                    if (!uidCal.getComponents().contains(timezone)) {
                        uidCal.getComponents().add(timezone);
                    }
                }
            }
            uidCal.getComponents().add(c);
        }
        return (Calendar[]) calendars.values().toArray(new Calendar[calendars.values().size()]);
    }
    
    /**
     * Returns a unique identifier as specified by components in the provided calendar.
     * @param calendar a calendar instance
     * @return the UID property
     * @throws ConstraintViolationException if zero or more than one unique identifer is found in the specified calendar
     */
    public static Uid getUid(final Calendar calendar) throws ConstraintViolationException {
        Uid uid = null;
        for (final Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            for (final Iterator j = c.getProperties(Property.UID).iterator(); j.hasNext();) {
                final Uid foundUid = (Uid) j.next();
                if (uid != null && !uid.equals(foundUid)) {
                    throw new ConstraintViolationException("More than one UID found in calendar");
                }
                uid = foundUid;
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
        final StringBuffer b = new StringBuffer("text/calendar");
        
        final Method method = (Method) calendar.getProperty(Property.METHOD);
        if (method != null) {
            b.append("; method=");
            b.append(method.getValue());
        }
        
        if (charset != null) {
            b.append("; charset=");
            b.append(charset);
        }
        return b.toString();
    }
}
