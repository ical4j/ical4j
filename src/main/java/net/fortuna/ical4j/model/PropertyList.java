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

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines a list of iCalendar properties.
 * @author Ben Fortuna
 */
public class PropertyList extends ArrayList<Property> implements Serializable {

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
  public PropertyList(PropertyList properties) throws ParseException, IOException, URISyntaxException {
    super();
    for (Property p : properties) {
      add(p.copy());
    }
  }

  /**
   * {@inheritDoc}
   */
  public final String toString() {
    final StringBuilder buffer = new StringBuilder();
    for (Property property : this) {
      buffer.append(property.toString());
    }
    return buffer.toString();
  }

  /**
   * Returns the first property of specified name.
   * @param aName name of property to return
   * @return a property or null if no matching property found
   */
  public final Property getProperty(final String aName) {
    for (final Property p : this) {
      if (p.getName().equalsIgnoreCase(aName)) {
        return p;
      }
    }
    return null;
  }

  /**
   * Returns a list of properties with the specified name.
   * @param name name of properties to return
   * @return a property list
   */
  public final PropertyList getProperties(final String name) {
    final PropertyList list = new PropertyList();
    for (final Property p : this) {
      if (p.getName().equalsIgnoreCase(name)) {
        list.add(p);
      }
    }
    return list;
  }

  /**
   * Remove a property from the list.
   * @param property the property to remove
   * @return true if the list contained the specified property
   * @see java.util.List#remove(java.lang.Object)
   */
  public final boolean remove(final Property property) {
    return remove((Object) property);
  }
}
