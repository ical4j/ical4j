/**
 * Copyright (c) 2008, Ben Fortuna
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * $Id$ [23-Apr-2004]
 *
 * Defines a list of iCalendar resouces.
 * 
 * @author Ben Fortuna
 */
public class ResourceList implements Serializable {
    
    private static final long serialVersionUID = 8119742218197533770L;

    private List resources;

    /**
     * Default constructor.
     */
    public ResourceList() {
        resources = new ArrayList();
    }

    /**
     * Parses the specified string representation to create
     * a list of resources.
     * @param aValue a string representation of a list of
     * resources
     */
    public ResourceList(final String aValue) {
        resources = new ArrayList();

        for (StringTokenizer t = new StringTokenizer(aValue, ","); t
                .hasMoreTokens();) {
            resources.add(t.nextToken());
        }
    }

    /**
     * @see java.util.AbstractCollection#toString()
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        for (final Iterator i = resources.iterator(); i.hasNext();) {
            b.append(i.next());
            if (i.hasNext()) {
                b.append(',');
            }
        }
        return b.toString();
    }

    /**
     * Add a resource to the list.
     * @param resource the resource to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final String resource) {
        return resources.add(resource);
    }

    /**
     * @return boolean indicates if the list is empty
     * @see List#isEmpty()
     */
    public final boolean isEmpty() {
        return resources.isEmpty();
    }

    /**
     * @return an iterator
     * @see List#iterator()
     */
    public final Iterator iterator() {
        return resources.iterator();
    }

    /**
     * Remove a resource from the list.
     * @param resource the resource to remove
     * @return true if the list contained the specified resource
     * @see List#remove(java.lang.Object)
     */
    public final boolean remove(final String resource) {
        return resources.remove(resource);
    }

    /**
     * @return the number of resources in the list
     * @see List#size()
     */
    public final int size() {
        return resources.size();
    }
}
