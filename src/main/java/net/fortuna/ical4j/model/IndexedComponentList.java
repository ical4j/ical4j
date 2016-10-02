/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <p>
 * o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p>
 * o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
 * o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p>
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * $Id$
 *
 * Created on 4/02/2006
 *
 * Provides indexing of components on a specific property.
 * @author Ben Fortuna
 */
public class IndexedComponentList<T extends Component> {

  private final ComponentList<T> EMPTY_LIST = new ComponentList<T>();

  private Map<String, ComponentList<T>> index;

  /**
   * Creates a new instance indexed on properties with the specified name.
   * @param list a list of components
   * @param propertyName the name of the properties to index on
   */
  public IndexedComponentList(final ComponentList<T> list, final String propertyName) {
    final Map<String, ComponentList<T>> indexedComponents = new HashMap<String, ComponentList<T>>();
    for (final T component : list) {
      for (final Property property : component.getProperties(propertyName)) {
        ComponentList<T> components = indexedComponents.get(property.getValue());
        if (components == null) {
          components = new ComponentList<T>();
          indexedComponents.put(property.getValue(), components);
        }
        components.add(component);
      }
    }
    this.index = Collections.unmodifiableMap(indexedComponents);
  }

  /**
   * Returns a list of components containing a property with the
   * specified value.
   * @param propertyValue the value of the property contained in the
   * returned components
   * @return a component list
   */
  public ComponentList<T> getComponents(final String propertyValue) {
    ComponentList<T> components = index.get(propertyValue);
    if (components == null) {
      components = EMPTY_LIST;
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
    final ComponentList<T> components = getComponents(propertyValue);
    if (!components.isEmpty()) {
      return components.get(0);
    }
    return null;
  }
}
