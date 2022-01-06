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

import java.util.*;
import java.util.stream.Collectors;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Accessor implementation for a list of iCalendar properties.
 * @author Ben Fortuna
 */
public class PropertyList implements ContentCollection<Property> {

    private final List<Property> properties;

    /**
     * Default constructor.
     */
    public PropertyList() {
        this(Collections.emptyList());
    }

    /**
     * Creates an unmodifiable copy of the specified property list.
     * @param properties a property list
     */
    
    public PropertyList(List<Property> properties) {
        this.properties = Collections.unmodifiableList(properties);
    }

    @Override
    public ContentCollection<Property> add(Property content) {
        List<Property> copy = new ArrayList<>(properties);
        copy.add(content);
        return new PropertyList(copy);
    }

    @Override
    public ContentCollection<Property> addAll(Collection<Property> content) {
        List<Property> copy = new ArrayList<>(properties);
        copy.addAll(content);
        return new PropertyList(copy);
    }

    @Override
    public ContentCollection<Property> remove(Property content) {
        List<Property> copy = new ArrayList<>(properties);
        if (copy.remove(content)) {
            return new PropertyList(copy);
        } else {
            return this;
        }
    }

    @Override
    public ContentCollection<Property> removeAll(String... name) {
        List<String> names = Arrays.asList(name);
        List<Property> copy = new ArrayList<>(properties);
        if (copy.removeIf(p -> names.contains(p.getName()))) {
            return new PropertyList(copy);
        } else {
            return this;
        }
    }

    @Override
    public ContentCollection<Property> replace(Property content) {
        List<Property> copy = new ArrayList<>(properties);
        copy.removeIf(p -> p.getName().equals(content.getName()));
        copy.add(content);
        return new PropertyList(copy);
    }

    @Override
    public List<Property> getAll() {
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return properties.stream().map(Property::toString).collect(Collectors.joining(""));
    }

    /**
     * Returns the first property of specified name.
     * @param aName name of property to return
     * @return a property or null if no matching property found
     *
     * @deprecated use {@link PropertyList#getFirst(String)}
     */
    @Deprecated
    public final <T extends Property> Optional<T> getProperty(final String aName) {
        return getFirst(aName);
    }

    /**
     * Returns a list of properties with the specified name.
     * @param name name of properties to return
     * @return a property list
     *
     * @deprecated use {@link PropertyList#get(String...)}
     */
    @Deprecated
    public final List<Property> getProperties(final String name) {
        return get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyList that = (PropertyList) o;
        return Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }
}
