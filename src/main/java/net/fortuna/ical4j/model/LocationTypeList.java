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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * $Id LocationTypeList.java $ [23-Apr-2004]
 * 
 * Defines a list of iCalendar location types.
 * 
 * @author Ben Fortuna
 */
public class LocationTypeList implements Serializable, Iterable<String> {

    private static final long serialVersionUID = -9181735547604179160L;

    private List<String> locationTypes;

    /**
     * Default constructor.
     */
    public LocationTypeList() {
        locationTypes = new CopyOnWriteArrayList<String>();
    }

    /**
     * Parses the specified string representation to create a list of categories.
     * 
     * @param aValue
     *            a string representation of a list of categories
     */
    public LocationTypeList(final String aValue) {
        locationTypes = new CopyOnWriteArrayList<String>();

        final StringTokenizer t = new StringTokenizer(aValue, ",");
        while (t.hasMoreTokens()) {
            locationTypes.add(t.nextToken());
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuilder b = new StringBuilder();
        for (final Iterator<String> i = locationTypes.iterator(); i.hasNext();) {
            b.append(i.next());
            if (i.hasNext()) {
                b.append(',');
            }
        }
        return b.toString();
    }

    /**
     * Add a location type to the list.
     * 
     * @param locationType the location type to add
     * @return true if the object is added successfully
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final String locationType) {
        return locationTypes.add(locationType);
    }

    /**
     * @return boolean indicates if the list is empty
     * @see List#isEmpty()
     */
    public final boolean isEmpty() {
        return locationTypes.isEmpty();
    }

    /**
     * @return an iterator
     * @see List#iterator()
     */
    public final Iterator<String> iterator() {
        return locationTypes.iterator();
    }

    /**
     * Remove a locationType from the list.
     * 
     * @param locationType the location type to remove
     * @return true if the list contained the specified category
     * @see List#remove(java.lang.Object)
     */
    public final boolean remove(final String locationType) {
        return locationTypes.remove(locationType);
    }

    /**
     * @return the number of categories in the list
     * @see List#size()
     */
    public final int size() {
        return locationTypes.size();
    }
}
