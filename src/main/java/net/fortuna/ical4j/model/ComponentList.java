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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines a list of iCalendar components.
 * @author Ben Fortuna
 */
public class ComponentList<T extends Component> extends ArrayList<T> implements Serializable {

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
    @SuppressWarnings("unchecked")
	public ComponentList(ComponentList<? extends T> components) throws ParseException,
            IOException, URISyntaxException {

        for (T c : components) {
            add((T) c.copy());
        }
    }

    /**
     * Create  new component list containing the components in the specified list.
     *
     * @param components
     */
    public ComponentList(List<? extends T> components) {
        addAll(components);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return stream().map(Component::toString).collect(Collectors.joining(""));
    }

    /**
     * Returns the first component of specified name.
     * @param aName name of component to return
     * @return a component or null if no matching component found
     */
    public final T getComponent(final String aName) {
        for (final T c : this) {
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
    @SuppressWarnings("unchecked")
	public final <C extends T> List<C> getComponents(final String name) {
        final List<C> components = new ArrayList<>();
        for (final T c : this) {
            if (c.getName().equals(name)) {
                components.add((C) c);
            }
        }
        return Collections.unmodifiableList(components);
    }
}
