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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentListAccessor;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A deep comparison of two calendars: properties (name, decoded value, parameters in order, group prefix) and
 * components, sub-components included (Component#equals ignores them). Returns where they first differ, or null.
 */
public final class Diff {

    private Diff() {
    }

    public static String calendars(Calendar a, Calendar b) {
        String d = properties(a.getProperties(), b.getProperties(), "VCALENDAR");
        return d != null ? d : components(a.getComponents(), b.getComponents(), "VCALENDAR");
    }

    private static String names(List<?> items) {
        return items.stream().map(o -> o instanceof Property ? ((Property) o).getName()
                : ((Component) o).getName()).collect(Collectors.joining(",", "[", "]"));
    }

    public static String properties(List<Property> a, List<Property> b, String where) {
        if (a.size() != b.size()) {
            return where + ": " + a.size() + " properties " + names(a) + " vs " + b.size() + " " + names(b);
        }
        for (int i = 0; i < a.size(); i++) {
            String d = property(a.get(i), b.get(i), where);
            if (d != null) {
                return d;
            }
        }
        return null;
    }

    public static String property(Property p, Property q, String where) {
        String at = where + "/" + p.getName();
        if (!p.getName().equalsIgnoreCase(q.getName())) {
            return where + ": property " + p.getName() + " became " + q.getName();
        }
        if (!Objects.equals(p.getPrefix(), q.getPrefix())) {
            return at + ": group prefix [" + p.getPrefix() + "] became [" + q.getPrefix() + "]";
        }
        if (!Objects.equals(p.getValue(), q.getValue())) {
            return at + ": value [" + Ical.abbreviate(String.valueOf(p.getValue()), 200) + "] became ["
                    + Ical.abbreviate(String.valueOf(q.getValue()), 200) + "]";
        }
        List<Parameter> pa = p.getParameterList().getAll();
        List<Parameter> qa = q.getParameterList().getAll();
        if (pa.size() != qa.size()) {
            return at + ": parameters " + pa + " became " + qa;
        }
        for (int j = 0; j < pa.size(); j++) {
            Parameter x = pa.get(j);
            Parameter y = qa.get(j);
            if (!x.getName().equalsIgnoreCase(y.getName()) || !Objects.equals(x.getValue(), y.getValue())) {
                return at + ": parameter " + x.getName() + "=[" + Ical.abbreviate(String.valueOf(x.getValue()), 200)
                        + "] became " + y.getName() + "=[" + Ical.abbreviate(String.valueOf(y.getValue()), 200) + "]";
            }
        }
        return null;
    }

    public static String components(List<? extends Component> a, List<? extends Component> b, String where) {
        if (a.size() != b.size()) {
            return where + ": " + a.size() + " components " + names(a) + " vs " + b.size() + " " + names(b);
        }
        for (int i = 0; i < a.size(); i++) {
            String d = component(a.get(i), b.get(i), where);
            if (d != null) {
                return d;
            }
        }
        return null;
    }

    public static String component(Component x, Component y, String where) {
        String at = where + "/" + x.getName() + "[" + x.getClass().getSimpleName() + "]";
        if (!x.getName().equalsIgnoreCase(y.getName())) {
            return where + ": component " + x.getName() + " became " + y.getName();
        }
        if (x.getClass() != y.getClass()) {
            return at + ": became a " + y.getClass().getSimpleName();
        }
        String d = properties(x.getProperties(), y.getProperties(), at);
        if (d != null) {
            return d;
        }
        return components(subComponents(x), subComponents(y), at);
    }

    public static List<? extends Component> subComponents(Component c) {
        if (c instanceof ComponentListAccessor) {
            return ((ComponentListAccessor<?>) c).getComponentList().getAll();
        }
        return List.of();
    }
}
