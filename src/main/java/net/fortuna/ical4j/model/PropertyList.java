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
import java.util.stream.Collectors;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines a list of iCalendar properties.
 * @author Ben Fortuna
 */
public class PropertyList<T extends Property> extends ArrayList<T> implements Serializable {

    private static final long serialVersionUID = -8875923766224921031L;

    /**
     * Default constructor.
     */
    public PropertyList() {
    }

    /**
     * Creates a new instance with the specified initial capacity.
     * @param initialCapacity the initial capacity of the list
     */
    public PropertyList(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates a deep copy of the specified property list.
     * @param properties a property list
     * @throws ParseException where property data cannot be parsed
     * @throws IOException where property data cannot be read
     * @throws URISyntaxException where a property contains an invalid URI
     */
    
    @SuppressWarnings("unchecked")
    public PropertyList(PropertyList<? extends T> properties) throws ParseException, IOException, URISyntaxException {
        super();
        for ( T p: properties) {
            add((T)p.copy());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return stream().map(Property::toString).collect(Collectors.joining(""));
    }

    /**
     * Returns the first property of specified name.
     * @param aName name of property to return
     * @return a property or null if no matching property found
     */
    public final <R> R getProperty(final String aName) {
        for (final T p : this) {
            if (p.getName().equalsIgnoreCase(aName)) {
                return (R) p;
            }
        }
        return null;
    }

    /**
     * Returns a list of properties with the specified name.
     * @param name name of properties to return
     * @return a property list
     */
    @SuppressWarnings("unchecked")
    public final <C extends T> PropertyList<C> getProperties(final String name) {
        final PropertyList<C> list = new PropertyList<C>();
        for (final T p : this) {
            if (p.getName().equalsIgnoreCase(name)) {
                list.add((C) p);
            }
        }
        return list;
    }

    /**
     * Add a property to the list.
     * @param property the property to add
     * @return true
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public final boolean add(final T property) {
        return super.add(property);
    }

    /**
     * Remove a property from the list.
     * @param property the property to remove
     * @return true if the list contained the specified property
     * @see java.util.List#remove(java.lang.Object)
     */
    public final boolean remove(final Property property) {
        return super.remove(property);
    }
}
