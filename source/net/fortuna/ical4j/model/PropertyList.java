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
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Defines a list of iCalendar properties.
 *
 * @author benf
 */
public class PropertyList {

    private List properties;

    /**
     * Constructor.
     */
    public PropertyList() {
        properties = new ArrayList();
    }

    /**
     * @see java.util.AbstractCollection#toString()
     */
    public final String toString() {

        StringBuffer buffer = new StringBuffer();

        for (Iterator i = properties.iterator(); i.hasNext();) {

            buffer.append(i.next().toString());
        }

        return buffer.toString();
    }

    /**
     * Returns the first property of specified name.
     * @param aName name of property to return
     * @return a property or null if no matching property found
     */
    public final Property getProperty(final String aName) {

        for (Iterator i = properties.iterator(); i.hasNext();) {
            Property p = (Property) i.next();

            if (p.getName().equals(aName)) {
                return p;
            }
        }

        return null;
    }

    /**
     * Returns a list of properties with the specified name.
     * @param name
     *            name of properties to return
     * @return a property list
     */
    public final PropertyList getProperties(final String name) {
        PropertyList list = new PropertyList();

        for (Iterator i = properties.iterator(); i.hasNext();) {
            Property p = (Property) i.next();

            if (p.getName().equals(name)) {
                list.add(p);
            }
        }
        return list;
    }

    /**
     * Add a property to the list.
     * @param property the property to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final Property property) {
        return properties.add(property);
    }

    /**
     * @return boolean indicates if the list is empty
     * @see List#isEmpty()
     */
    public final boolean isEmpty() {
        return properties.isEmpty();
    }

    /**
     * @return an iterator
     * @see List#iterator()
     */
    public final Iterator iterator() {
        return properties.iterator();
    }

    /**
     * Remove a property from the list
     * @param property the property to remove
     * @return true if the list contained the specified property
     * @see List#remove(java.lang.Object)
     */
    public final boolean remove(final Property property) {
        return properties.remove(property);
    }

    /**
     * @return the number of properties in the list
     * @see List#size()
     */
    public final int size() {
        return properties.size();
    }
}