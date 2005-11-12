/*
 * $Id$ [Apr 5, 2004]
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

import java.io.Serializable;
import java.util.Iterator;

import net.fortuna.ical4j.model.component.XComponent;

/**
 * Defines an iCalendar component. Subclasses of this class provide additional
 * validation and typed values for specific iCalendar components.
 *
 * @author Ben Fortuna
 */
public abstract class Component implements Serializable {

    public static final String BEGIN = "BEGIN";

    public static final String END = "END";

    public static final String VEVENT = "VEVENT";

    public static final String VTODO = "VTODO";

    public static final String VJOURNAL = "VJOURNAL";

    public static final String VFREEBUSY = "VFREEBUSY";

    public static final String VTIMEZONE = "VTIMEZONE";

    public static final String VALARM = "VALARM";

    public static final String EXPERIMENTAL_PREFIX = "X-";

    private String name;

    private PropertyList properties;

    /**
     * Constructs a new component containing no properties.
     * @param s a component name
     */
    protected Component(final String s) {
        this(s, new PropertyList());
    }

    /**
     * Constructor made protected to enforce the use of
     * <code>ComponentFactory</code> for component instantiation.
     * @param s component name
     * @param p a list of properties
     */
    protected Component(final String s, final PropertyList p) {
        this.name = s;
        this.properties = p;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(BEGIN);
        buffer.append(':');
        buffer.append(getName());
        buffer.append("\r\n");
        buffer.append(getProperties());
        buffer.append(END);
        buffer.append(':');
        buffer.append(getName());
        buffer.append("\r\n");

        return buffer.toString();
    }

    /**
     * @return Returns the name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Indicates whether this component is a top-level
     * calendar component.
     * @return a boolean value
     */
    public final boolean isCalendarComponent() {
        return VALARM.equals(getName())
            || VEVENT.equals(getName())
            || VFREEBUSY.equals(getName())
            || VJOURNAL.equals(getName())
            || VTIMEZONE.equals(getName())
            || VTODO.equals(getName())
            || (this instanceof XComponent);
    }

    /**
     * @return Returns the properties.
     */
    public final PropertyList getProperties() {
        return properties;
    }

    /**
     * Perform validation on a component and its properties.
     * @throws ValidationException
     *             where the component is not in a valid state
     */
    public final void validate() throws ValidationException {
        validate(true);
    }

    /**
     * Perform validation on a component.
     * @param recurse indicates whether to validate the component's
     * properties
     * @throws ValidationException
     *             where the component is not in a valid state
     */
    public abstract void validate(final boolean recurse) throws ValidationException;

    /**
     * Invoke validation on the component properties in its current state.
     * @throws ValidationException
     *             where any of the component properties is not in a valid state
     */
    protected final void validateProperties() throws ValidationException {
        for (Iterator i = getProperties().iterator(); i.hasNext();) {
            Property property = (Property) i.next();
            property.validate();
        }
    }

    /**
     * Two components are equal if and only if their
     * name and property lists are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof Component) {
            Component c = (Component) arg0;
            return getName().equals(c.getName())
                    && getProperties().equals(c.getProperties());
        }
        return super.equals(arg0);
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        return getName().hashCode() + getProperties().hashCode();
    }
}
