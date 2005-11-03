/*
 * $Id$ [05-Apr-2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.component.XComponent;

/**
 * A factory for creating iCalendar components.
 *
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
    
    public Component createComponent(final String name) {
        return createComponent(name, new PropertyList());
    }

    /**
     * Creates a component.
     *
     * @param name
     *            name of the component
     * @param properties
     *            a list of component properties
     * @return a component
     */
    public Component createComponent(final String name,
            final PropertyList properties) {

        if (Component.VALARM.equals(name)) {
            return new VAlarm(properties);
        }
        else if (Component.VEVENT.equals(name)) {
            return new VEvent(properties);
        }
        else if (Component.VFREEBUSY.equals(name)) {
            return new VFreeBusy(properties);
        }
        else if (Component.VJOURNAL.equals(name)) {
            return new VJournal(properties);
        }
        else if (Component.VTODO.equals(name)) {
            return new VToDo(properties);
        }
        else if (Observance.STANDARD.equals(name)) {
            return new Standard(properties);
        }
        else if (Observance.DAYLIGHT.equals(name)) {
            return new Daylight(properties);
        }
        else if (Component.VTIMEZONE.equals(name)) {
            return new VTimeZone(properties);
        }
        else {
            throw new IllegalArgumentException("Unkown component [" + name
                    + "]");
        }
    }

    /**
     * Creates a component which contains sub-components. Currently the only
     * such component is VTIMEZONE.
     *
     * @param name
     *            name of the component
     * @param properties
     *            a list of component properties
     * @param components
     *            a list of sub-components (namely standard/daylight timezones)
     * @return a component
     */
    public Component createComponent(final String name,
            final PropertyList properties, final ComponentList components) {

        if (components != null) {

            if (Component.VTIMEZONE.equals(name)) {

                return new VTimeZone(properties, components);
            }
            else if (Component.VEVENT.equals(name)) {

                return new VEvent(properties, components);
            }
            else if (isExperimentalName(name)) {
                return new XComponent(name, properties);
            }
            else {
                throw new IllegalArgumentException("Unkown component [" + name
                        + "]");
            }
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
}
