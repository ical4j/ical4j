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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;

/**
 * $Id$ [23-Apr-2004]
 *
 * Defines a list of iCalendar addresses.
 * @author Ben Fortuna
 */
public class AddressList implements Serializable {

    private static final long serialVersionUID = 81383256078213569L;

    private List addresses;

    /**
     * Default constructor.
     */
    public AddressList() {
        addresses = new CopyOnWriteArrayList();
    }

    /**
     * Parses the specified string representation to create a list of addresses.
     * @param aValue a string representation of a list of addresses
     * @throws URISyntaxException where the specified string is not a valid representation
     */
    public AddressList(final String aValue) throws URISyntaxException {
        addresses = new CopyOnWriteArrayList();
        final StringTokenizer t = new StringTokenizer(aValue, ",");
        while (t.hasMoreTokens()) {

            try {
                addresses.add(new URI(Uris.encode(Strings
                        .unquote(t.nextToken()))));
            }
            catch (URISyntaxException use) {
                // ignore invalid addresses if relaxed parsing is enabled..
                if (!CompatibilityHints.isHintEnabled(
                        CompatibilityHints.KEY_RELAXED_PARSING)) {

                    throw use;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        for (final Iterator i = addresses.iterator(); i.hasNext();) {
            b.append(Strings.quote(Uris.decode(Strings.valueOf(i.next()))));
            if (i.hasNext()) {
                b.append(',');
            }
        }
        return b.toString();
    }

    /**
     * Add an address to the list.
     * @param address the address to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final URI address) {
        return addresses.add(address);
    }

    /**
     * @return boolean indicates if the list is empty
     * @see List#isEmpty()
     */
    public final boolean isEmpty() {
        return addresses.isEmpty();
    }

    /**
     * @return an iterator
     * @see List#iterator()
     */
    public final Iterator iterator() {
        return addresses.iterator();
    }

    /**
     * Remove an address from the list.
     * @param address the address to remove
     * @return true if the list contained the specified address
     * @see List#remove(java.lang.Object)
     */
    public final boolean remove(final URI address) {
        return addresses.remove(address);
    }

    /**
     * @return the number of addresses in the list
     * @see List#size()
     */
    public final int size() {
        return addresses.size();
    }
}
