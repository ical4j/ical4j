/*
 * $Id$ [23-Apr-2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Defines a list of iCalendar categories.
 * @author benfortuna
 */
public class CategoryList implements Serializable {
    
    private static final long serialVersionUID = 4387692697196974638L;

    private List categories;

    /**
     * Default constructor.
     */
    public CategoryList() {
        categories = new ArrayList();
    }

    /**
     * Parses the specified string representation to create
     * a list of categories.
     * @param aValue a string representation of a list of
     * categories
     */
    public CategoryList(final String aValue) {
        categories = new ArrayList();

        for (StringTokenizer t = new StringTokenizer(aValue, ","); t
                .hasMoreTokens();) {
            categories.add(t.nextToken());
        }
    }

    /**
     * @see java.util.AbstractCollection#toString()
     */
    public final String toString() {

        StringBuffer b = new StringBuffer();

        for (Iterator i = categories.iterator(); i.hasNext();) {

            b.append(i.next());

            if (i.hasNext()) {
                b.append(',');
            }
        }

        return b.toString();
    }

    /**
     * Add an address to the list.
     * @param category the category to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final String category) {
        return categories.add(category);
    }

    /**
     * @return boolean indicates if the list is empty
     * @see List#isEmpty()
     */
    public final boolean isEmpty() {
        return categories.isEmpty();
    }

    /**
     * @return an iterator
     * @see List#iterator()
     */
    public final Iterator iterator() {
        return categories.iterator();
    }

    /**
     * Remove a category from the list
     * @param category the category to remove
     * @return true if the list contained the specified category
     * @see List#remove(java.lang.Object)
     */
    public final boolean remove(final String category) {
        return categories.remove(category);
    }

    /**
     * @return the number of categories in the list
     * @see List#size()
     */
    public final int size() {
        return categories.size();
    }
}
