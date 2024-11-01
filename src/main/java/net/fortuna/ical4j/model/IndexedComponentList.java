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

/**
 * $Id$
 *
 * Created on 4/02/2006
 *
 * Provides indexing of components on a specific property.
 * @author Ben Fortuna
 */
public class IndexedComponentList<T extends Component> {

    private final Map<String, List<T>> index;
    
    /**
     * Creates a new instance indexed on properties with the specified name.
     * @param list a list of components
     * @param propertyName the name of the properties to index on
     */
    public IndexedComponentList(final List<T> list, final String propertyName) {
        final Map<String, List<T>> indexedComponents = new HashMap<>();
        for (final var component : list) {
            for (final var property : component.getProperties(propertyName)) {
                List<T> components = indexedComponents.computeIfAbsent(property.getValue(), k -> new ArrayList<>());
                components.add(component);
            }
        }
        indexedComponents.keySet().forEach(p -> indexedComponents.put(p, Collections.unmodifiableList(indexedComponents.get(p))));
        this.index = Collections.unmodifiableMap(indexedComponents);
    }
    
    /**
     * Returns a list of components containing a property with the
     * specified value.
     * @param propertyValue the value of the property contained in the
     * returned components
     * @return a component list
     */
    public List<T> getComponents(final String propertyValue) {
        List<T> components = index.get(propertyValue);
        if (components == null) {
            components = Collections.emptyList();
        }
        return components;
    }
    
    /**
     * Returns the first component containing a property with the specified
     * value.
     * @param propertyValue the value of the property identified in the returned
     * component
     * @return a component or null if no component is found containing a property
     * with the specified value
     */
    public T getComponent(final String propertyValue) {
        final List<T> components = getComponents(propertyValue);
        if (!components.isEmpty()) {
            return components.get(0);
        }
        return null;
    }
}
