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

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines a list of iCalendar components.
 * @author Ben Fortuna
 */
public class ComponentList extends ArrayList implements Serializable {

    private static final long serialVersionUID = 7308557606558767449L;

    /**
     * Default constructor.
     */
    public ComponentList() {
    }

    /**
     * Creates a new instance with the specified initial capacity.
     * @param initialCapacity the initial capacity of the list
     */
    public ComponentList(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates a deep copy of the specified component list.
     * @param components a component list to copy
     * @throws IOException where an error occurs reading component data
     * @throws ParseException where component data cannot be parsed
     * @throws URISyntaxException where component data contains an invalid URI
     */
    public ComponentList(ComponentList components) throws ParseException,
            IOException, URISyntaxException {

        for (final Iterator i = components.iterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            add(c.copy());
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer buffer = new StringBuffer();
        for (final Iterator i = iterator(); i.hasNext();) {
            buffer.append(i.next().toString());
        }
        return buffer.toString();
    }

    /**
     * Returns the first component of specified name.
     * @param aName name of component to return
     * @return a component or null if no matching component found
     */
    public final Component getComponent(final String aName) {
        for (final Iterator i = iterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            if (c.getName().equals(aName)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Returns a list containing all components with specified name.
     * @param name name of components to return
     * @return a list of components with the matching name
     */
    public final ComponentList getComponents(final String name) {
        final ComponentList components = new ComponentList();
        for (final Iterator i = iterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            if (c.getName().equals(name)) {
                components.add(c);
            }
        }
        return components;
    }

    /**
     * Add a component to the list.
     * @param component the component to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final Component component) {
        return add((Object) component);
    }

    /**
     * Overrides superclass to throw an <code>IllegalArgumentException</code> where argument is not a
     * <code>net.fortuna.ical4j.model.Component</code>.
     * @param component a component to add
     * @return true if the object was added, otherwise false
     * @see List#add(E)
     */
    public final boolean add(final Object component) {
        if (!(component instanceof Component)) {
            throw new IllegalArgumentException("Argument not a "
                    + Component.class.getName());
        }
        return super.add(component);
    }

    /**
     * @return boolean indicates if the list is empty
     * @see List#isEmpty()
     */
    // public final boolean isEmpty() {
    // return components.isEmpty();
    // }
    /**
     * @return an iterator
     * @see List#iterator()
     */
    // public final Iterator iterator() {
    // return components.iterator();
    // }
    /**
     * Remove a component from the list.
     * @param component the component to remove
     * @return true if the list contained the specified component
     * @see List#remove(java.lang.Object)
     */
    public final boolean remove(final Component component) {
        return remove((Object) component);
    }

    /**
     * @return the number of components in the list
     * @see List#size()
     */
    // public final int size() {
    // return components.size();
    // }
    /**
     * Provides a list containing all components contained in this component list.
     * @return a list
     */
    // public final List toList() {
    // return new ArrayList(components);
    // }
}
