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

import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.RegEx;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * $Id$ [23-Apr-2004]
 *
 * Defines a list of iCalendar addresses.
 * @author Ben Fortuna
 */
public class AddressList implements Serializable {

    private static final long serialVersionUID = 81383256078213569L;

    private final List<URI> addresses;

    /**
     * Default constructor.
     */
    public AddressList() {
        addresses = Collections.emptyList();
    }

    /**
     * Parses the specified string representation to create a list of addresses.
     * @param aValue a string representation of a list of addresses
     * @throws URISyntaxException where the specified string is not a valid representation
     */
    public AddressList(final String aValue) throws URISyntaxException {
        this(aValue, CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING));
    }

    public AddressList(final String aValue, boolean allowInvalidAddress) throws URISyntaxException {
        List<URI> values = new ArrayList<>();
        for (String a : aValue.split(RegEx.COMMA_DELIMITED)) {
            try {
                values.add(new URI(Uris.encode(Strings.unquote(a))));
            } catch (URISyntaxException use) {
                // ignore invalid addresses if relaxed parsing is enabled..
                if (!allowInvalidAddress) {
                    throw use;
                }
            }
        }
        addresses = Collections.unmodifiableList(values);
    }

    public AddressList(List<URI> addresses) {
        this.addresses = Collections.unmodifiableList(addresses);
    }

    public List<URI> getAddresses() {
        return addresses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return toString(addresses);
    }

    public static String toString(List<URI> addresses) {
        return addresses.stream().map(Strings::quote).collect(Collectors.joining(","));
    }

    /**
     * Add an address to the list.
     * @param address the address to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final AddressList add(final URI address) {
        List<URI> newlist = new ArrayList<>(addresses);
        newlist.add(address);
        return new AddressList(newlist);
    }

    /**
     * Remove an address from the list.
     * @param address the address to remove
     * @return true if the list contained the specified address
     * @see List#remove(java.lang.Object)
     */
    public final AddressList remove(final URI address) {
        List<URI> newlist = new ArrayList<>(addresses);
        if (newlist.remove(address)) {
            return new AddressList(newlist);
        } else {
            return this;
        }
    }
}
