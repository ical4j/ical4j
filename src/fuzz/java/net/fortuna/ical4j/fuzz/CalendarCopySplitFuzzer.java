/**
 * Copyright (c) 2026, Ben Fortuna
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

package net.fortuna.ical4j.fuzz;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentContainer;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ImmutableProperty;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.XProperty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.DateTimeException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 13 — Calendar#copy, #split, #merge, #getUid on a parsed calendar (byte 0 as in 01). copy() throws nothing and
 * gives a deeply equal calendar with the same text; mutating the copy (a property added, a parameter added, a value
 * set, an alarm added) leaves the original's text unchanged; split() gives back exactly the components with a UID,
 * each in the calendar of its UID with the VTIMEZONEs its TZIDs reference and no METHOD, the original keeping its
 * METHOD; merge(c) holds the properties and components of both. Only getUid may throw (ConstraintViolationException).
 */
public class CalendarCopySplitFuzzer {

    private static final String OTHER = "BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//twake-fuzz//other//EN\r\n"
            + "METHOD:PUBLISH\r\nX-OTHER:1\r\nBEGIN:VEVENT\r\nUID:other@twake-fuzz\r\nDTSTAMP:20240101T000000Z\r\n"
            + "DTSTART:20240102T100000Z\r\nSUMMARY:other\r\nEND:VEVENT\r\nEND:VCALENDAR\r\n";

    private static Calendar other;
    private static String otherText;

    public static void fuzzerInitialize() throws Exception {
        Ical.hints(false);
        other = Ical.parse(OTHER, false);
        otherText = other.toString();
    }

    public static void fuzzerTestOneInput(byte[] input) {
        if (input.length < 1) {
            return;
        }
        int selector = input[0] & 0xff;
        Ical.hints((selector & 1) != 0);
        Calendar calendar;
        try {
            calendar = Ical.builder((selector & 2) != 0).build(new ByteArrayInputStream(input, 1, input.length - 1));
        } catch (ParserException | IOException e) {
            return;
        }
        if (calendar == null) {
            return;
        }
        String text;
        try {
            text = calendar.toString();
        } catch (DateTimeException e) {
            // a lazily parsed date that cannot be written: scenario 01's finding
            return;
        }

        // copy: equal, same text
        Calendar copy;
        try {
            copy = calendar.copy();
        } catch (UnsupportedOperationException e) {
            // Calendar#copy runs Property#copy in a parallelStream: the stack differs at each run, so the defect is
            // rethrown from here, one signature per kind (the library frame is in the message)
            String where = firstLibraryFrame(e);
            if (String.valueOf(e.getMessage()).contains("custom properties")) {
                throw new AssertionError("copy() throws on a non-X- custom property: " + e.getMessage() + " at " + where);
            }
            throw new AssertionError("copy() throws UnsupportedOperationException: " + e.getMessage() + " at " + where);
        }
        String diff = Diff.calendars(calendar, copy);
        if (diff != null) {
            throw new AssertionError("copy() differs from the original: " + diff);
        }
        if (!copy.equals(calendar)) {
            throw new AssertionError("copy() is not equals() to the original");
        }
        if (!copy.toString().equals(text)) {
            throw new AssertionError("copy().toString() differs from the original's:\n" + Ical.abbreviate(text, 1500)
                    + "\n--- vs ---\n" + Ical.abbreviate(copy.toString(), 1500));
        }

        // mutating the copy must not touch the original
        mutate(copy);
        if (!calendar.toString().equals(text)) {
            throw new AssertionError("mutating the copy changed the original:\n" + Ical.abbreviate(text, 1500)
                    + "\n--- became ---\n" + Ical.abbreviate(calendar.toString(), 1500));
        }

        try {
            calendar.getUid();
        } catch (ConstraintViolationException e) {
            // zero or several UIDs: documented
        }

        checkSplit(calendar, text);
        checkMerge(calendar, text);
    }

    private static String firstLibraryFrame(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            for (StackTraceElement f : t.getStackTrace()) {
                if (f.getClassName().startsWith("net.fortuna.ical4j")) {
                    return f.toString();
                }
            }
        }
        return "?";
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void mutate(Calendar copy) {
        copy.add(new XProperty("X-TWAKE-FUZZ-CAL", "mutated"));
        for (CalendarComponent c : copy.getComponents()) {
            c.add(new XProperty("X-TWAKE-FUZZ", "mutated"));
            // constants (VERSION:2.0, STATUS:CONFIRMED...) are immutable by design: only mutable properties are changed
            List<Property> props = c.getProperties();
            Optional<Property> first = props.stream().filter(p -> !(p instanceof ImmutableProperty)).findFirst();
            if (first.isPresent()) {
                first.get().add(new XParameter("X-TWAKE-FUZZ", "mutated"));
                for (Property p : props) {
                    if ((p instanceof XProperty || p instanceof Description) && !(p instanceof ImmutableProperty)) {
                        p.setValue("mutated");
                        break;
                    }
                }
            }
            for (Component sub : Diff.subComponents(c)) {
                sub.add(new XProperty("X-TWAKE-FUZZ-SUB", "mutated"));
            }
            if (c instanceof VEvent) {
                VAlarm alarm = new VAlarm(java.time.Duration.ofMinutes(-15));
                alarm.add(new Action("DISPLAY"));
                ((ComponentContainer) c).add(alarm);
            }
        }
    }

    private static void checkSplit(Calendar calendar, String text) {
        Optional<Property> method = calendar.getProperty(Property.METHOD);
        Calendar[] parts = calendar.split();
        if (!calendar.toString().equals(text)) {
            throw new AssertionError("split() changed the original calendar:\n" + Ical.abbreviate(text, 1500)
                    + "\n--- became ---\n" + Ical.abbreviate(calendar.toString(), 1500));
        }
        if (method.isPresent() && calendar.getProperty(Property.METHOD).isEmpty()) {
            throw new AssertionError("split() removed METHOD from the original");
        }
        if (parts.length == 1 && parts[0] == calendar) {
            return; // at most one component, or only VTIMEZONEs: the original, unchanged
        }
        Set<Component> expected = Collections.newSetFromMap(new IdentityHashMap<>());
        for (CalendarComponent c : calendar.getComponents()) {
            if (!(c instanceof VTimeZone) && c.getUid().isPresent()) {
                expected.add(c);
            }
        }
        Set<Component> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Calendar part : parts) {
            if (part.getProperty(Property.METHOD).isPresent()) {
                throw new AssertionError("a split calendar has METHOD");
            }
            Uid uid = null;
            for (CalendarComponent c : part.getComponents()) {
                if (c instanceof VTimeZone) {
                    continue;
                }
                if (!expected.contains(c)) {
                    throw new AssertionError("split() produced a component the original has not (or has no UID): "
                            + c.getName());
                }
                if (!seen.add(c)) {
                    throw new AssertionError("split() put a component in two calendars: " + c.getName());
                }
                Uid u = c.getUid().orElseThrow();
                if (uid == null) {
                    uid = u;
                } else if (!uid.equals(u)) {
                    throw new AssertionError("a split calendar mixes UIDs " + uid.getValue() + " and " + u.getValue());
                }
                for (Property p : c.getProperties()) {
                    Optional<TzId> tzid = p.getParameter(Parameter.TZID);
                    if (tzid.isPresent() && hasTimeZone(calendar, tzid.get().getValue())
                            && !hasTimeZone(part, tzid.get().getValue())) {
                        throw new AssertionError("split calendar of UID " + u.getValue() + " lacks the VTIMEZONE of "
                                + p.getName() + ";TZID=" + tzid.get().getValue());
                    }
                }
            }
        }
        if (seen.size() != expected.size()) {
            throw new AssertionError("split() lost " + (expected.size() - seen.size()) + " component(s) with a UID");
        }
    }

    private static boolean hasTimeZone(Calendar calendar, String tzid) {
        for (CalendarComponent c : calendar.getComponents()) {
            if (c instanceof VTimeZone) {
                Optional<Property> id = c.getProperty(Property.TZID);
                if (id.isPresent() && tzid.equals(id.get().getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void checkMerge(Calendar calendar, String text) {
        Calendar merged = calendar.merge(other);
        if (!calendar.toString().equals(text)) {
            throw new AssertionError("merge() changed the original calendar");
        }
        if (!other.toString().equals(otherText)) {
            throw new AssertionError("merge() changed its argument");
        }
        for (Property p : calendar.getProperties()) {
            String n = p.getName();
            if (!n.equals(Property.VERSION) && !n.equals(Property.METHOD) && !n.equals(Property.PRODID)
                    && !merged.getProperties().contains(p)) {
                throw new AssertionError("merge() lost the property " + n + " of the original");
            }
        }
        for (Property p : other.getProperties()) {
            if (!merged.getProperties().contains(p)) {
                throw new AssertionError("merge() lost the property " + p.getName() + " of its argument");
            }
        }
        for (CalendarComponent c : calendar.getComponents()) {
            if (!merged.getComponents().contains(c)) {
                throw new AssertionError("merge() lost a " + c.getName() + " of the original");
            }
        }
        for (CalendarComponent c : other.getComponents()) {
            if (!merged.getComponents().contains(c)) {
                throw new AssertionError("merge() lost the " + c.getName() + " of its argument");
            }
        }
    }
}
