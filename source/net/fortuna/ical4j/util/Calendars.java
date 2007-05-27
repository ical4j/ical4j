/*
 * $Id$
 *
 * Created on 10/11/2006
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
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;

/**
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
    public static Calendar load(final String filename)
            throws IOException, ParserException {

        FileInputStream fin = new FileInputStream(filename);
        CalendarBuilder builder = new CalendarBuilder();
        return builder.build(fin);
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
        Calendar result = new Calendar();
        result.getProperties().addAll(c1.getProperties());
        for (Iterator i = c2.getProperties().iterator(); i.hasNext();) {
            Property p = (Property) i.next();
            if (!result.getProperties().contains(p)) {
                result.getProperties().add(p);
            }
        }
        result.getComponents().addAll(c1.getComponents());
        for (Iterator i = c2.getComponents().iterator(); i.hasNext();) {
            Component c = (Component) i.next();
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
        ComponentList components = new ComponentList();
        components.add(component);

        return new Calendar(components);
    }
}
