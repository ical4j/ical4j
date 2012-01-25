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
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.Available;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VAvailability;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.component.VVenue;
import net.fortuna.ical4j.model.component.XComponent;
import net.fortuna.ical4j.util.CompatibilityHints;

/**
 * $Id$ [05-Apr-2004]
 *
 * A factory for creating iCalendar components. Note that if relaxed parsing is enabled (via specifying the system
 * property: icalj.parsing.relaxed=true) illegal component names are allowed.
 * @author Ben Fortuna
 */
public final class ComponentFactory {

    private static ComponentFactory instance = new ComponentFactory();

    /**
     * Constructor made private to prevent instantiation.
     */
    private ComponentFactory() {
    }

    /**
     * @return Returns the instance.
     */
    public static ComponentFactory getInstance() {
        return instance;
    }

    /**
     * @param name a component name
     * @return a new component instance of the specified type
     */
    public Component createComponent(final String name) {
        return createComponent(name, new PropertyList());
    }

    /**
     * Creates a component.
     * @param name name of the component
     * @param properties a list of component properties
     * @return a component
     */
    public Component createComponent(final String name, final PropertyList properties) {
        Component component = null;
        if (Component.VALARM.equals(name)) {
            component = new VAlarm(properties);
        }
        else if (Component.VEVENT.equals(name)) {
            component = new VEvent(properties);
        }
        else if (Component.VFREEBUSY.equals(name)) {
            component = new VFreeBusy(properties);
        }
        else if (Component.VJOURNAL.equals(name)) {
            component = new VJournal(properties);
        }
        else if (Component.VTODO.equals(name)) {
            component = new VToDo(properties);
        }
        else if (Observance.STANDARD.equals(name)) {
            component = new Standard(properties);
        }
        else if (Observance.DAYLIGHT.equals(name)) {
            component = new Daylight(properties);
        }
        else if (Component.VTIMEZONE.equals(name)) {
            component = new VTimeZone(properties);
        }
        else if (Component.VVENUE.equals(name)) {
            component = new VVenue(properties);
        }
        else if (Component.VAVAILABILITY.equals(name)) {
            component = new VAvailability(properties);
        }
        else if (Component.AVAILABLE.equals(name)) {
            component = new Available(properties);
        }
        else if (isExperimentalName(name)) {
            component = new XComponent(name, properties);
        }
        else if (allowIllegalNames()) {
            component = new XComponent(name, properties);
        }
        else {
            throw new IllegalArgumentException("Illegal component [" + name
                    + "]");
        }
        return component;
    }

    /**
     * Creates a component which contains sub-components. Currently the only such component is VTIMEZONE.
     * @param name name of the component
     * @param properties a list of component properties
     * @param components a list of sub-components (namely standard/daylight timezones)
     * @return a component
     */
    public Component createComponent(final String name, final PropertyList properties,
            final ComponentList components) {
        
        if (components != null) {
            Component component = null;
            if (Component.VTIMEZONE.equals(name)) {
                component = new VTimeZone(properties, components);
            }
            else if (Component.VEVENT.equals(name)) {
                component = new VEvent(properties, components);
            }
            else {
                throw new IllegalArgumentException("Illegal component [" + name
                        + "]");
            }
            return component;
        }
        return createComponent(name, properties);
    }

    /**
     * @param name
     * @return
     */
    private boolean isExperimentalName(final String name) {
        return name.startsWith(Component.EXPERIMENTAL_PREFIX)
                && name.length() > Component.EXPERIMENTAL_PREFIX.length();
    }

    /**
     * @return true if non-standard names are allowed, otherwise false
     */
    protected boolean allowIllegalNames() {
        return CompatibilityHints
                .isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
    }
}
