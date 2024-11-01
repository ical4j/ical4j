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
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines an iCalendar VAVAILABILITY component.
 *
 * <pre>
   Component Name:  VAVAILABILITY

   Purpose:  Provide a grouping of component properties that describe
      the availability associated with a calendar user.

   Format Definition:  A "VAVAILABILITY" calendar component is defined
      by the following notation:

          availabilityc  = "BEGIN" ":" "VAVAILABILITY" CRLF
                           availabilityprop *availablec
                           "END" ":" "VAVAILABILITY" CRLF

          availabilityprop  = *(

                            ; the following are REQUIRED,
                            ; but MUST NOT occur more than once

                            dtstamp / dtstart / uid

                            ; the following are OPTIONAL,
                            ; but MUST NOT occur more than once

                            busytype / created / last-mod /
                            organizer / seq / summary / url /

                            ; either 'dtend' or 'duration' may appear
                            ; in a 'availabilityprop', but 'dtend' and
                            ; 'duration' MUST NOT occur in the same
                            ; 'availabilityprop'

                            dtend / duration /

                            ; the following are OPTIONAL,
                            ; and MAY occur more than once

                            categories / comment / contact / x-prop

                            )

 *
 * </pre>
 *
 * @author Ben Fortuna
 * @author Mike Douglass
 */
public class VAvailability extends CalendarComponent implements ComponentContainer<Available> {

	private static final long serialVersionUID = -3001603309266267258L;

    /**
     * Default constructor.
     */
    public VAvailability() {
        this(true);
    }

    public VAvailability(boolean initialise) {
        super(VAVAILABILITY);
        if (initialise) {
            add(new DtStamp());
        }
    }

    /**
     * Constructs a new instance containing the specified properties.
     * @param properties a list of properties
     */
    public VAvailability(final PropertyList properties) {
        super(VAVAILABILITY, properties);
    }

    /**
     * Constructor.
     * @param properties a list of properties
     * @param available a list of available components
     */
    public VAvailability(final PropertyList properties, final ComponentList<Available> available) {
        super(VAVAILABILITY, properties, available);
    }

    /**
     * Returns the list of available times.
     * @return a component list
     */
    public final List<Available> getAvailable() {
        return getComponents();
    }

    /**
     *
     * @return Returns the underlying component list.
     */
    @Override
    public ComponentList<Available> getComponentList() {
        //noinspection unchecked
        return (ComponentList<Available>) components;
    }

    @Override
    public void setComponentList(ComponentList<Available> components) {
        this.components = components;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResult validate(final boolean recurse) throws ValidationException {
        var result = ComponentValidator.VAVAILABILITY.validate(this);
        if (recurse) {
            result = result.merge(validateProperties());
        }
        return result;
    }

    @Override
    protected ComponentFactory<VAvailability> newFactory() {
        return new Factory();
    }

    @Override
    public Component copy() {
        return newFactory().createComponent(new PropertyList(getProperties().parallelStream()
                        .map(Property::copy).collect(Collectors.toList())),
                new ComponentList<>(getComponents().parallelStream()
                        .map(Component::copy).collect(Collectors.toList())));
    }

    /**
     * Default factory.
     */
    public static class Factory extends Content.Factory implements ComponentFactory<VAvailability> {

        public Factory() {
            super(VAVAILABILITY);
        }

        @Override
        public VAvailability createComponent() {
            return new VAvailability(false);
        }

        @Override
        public VAvailability createComponent(PropertyList properties) {
            return new VAvailability(properties);
        }

        @Override @SuppressWarnings("unchecked")
        public VAvailability createComponent(PropertyList properties, ComponentList<?> subComponents) {
            return new VAvailability(properties, (ComponentList<Available>) subComponents);
        }
    }
}
