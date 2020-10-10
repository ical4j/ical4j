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

import net.fortuna.ical4j.util.Strings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * $Id$ [23-Apr-2004]
 *
 * Defines a list of iCalendar text elements.
 * @author Ben Fortuna
 */
public class TextList implements Serializable, Iterable<String> {

	private static final long serialVersionUID = -417427815871330636L;
	
	private List<String> texts;

    /**
     * Default constructor.
     */
    public TextList() {
        texts = new CopyOnWriteArrayList<String>();
    }

    /**
     * Parses the specified string representation to create a list of categories.
     * @param aValue a string representation of a list of categories
     */
    public TextList(final String aValue) {
        texts = new CopyOnWriteArrayList<String>();

        final Pattern pattern = Pattern.compile("(?:\\\\.|[^\\\\,]++)+");

        final Matcher matcher = pattern.matcher(aValue);

        while (matcher.find()){
            texts.add(Strings.unescape(matcher.group().replace("\\\\","\\")));
        }
    }

    /**
     * @param textValues an array of text values
     */
    public TextList(String[] textValues) {
        texts = Arrays.asList(textValues);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return texts.stream().map(t -> Strings.escape(t)).collect(Collectors.joining(","));
    }

    /**
     * Add an address to the list.
     * @param text the category to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final String text) {
        return texts.add(text);
    }

    /**
     * @return boolean indicates if the list is empty
     * @see List#isEmpty()
     */
    public final boolean isEmpty() {
        return texts.isEmpty();
    }

    /**
     * @return an iterator
     * @see List#iterator()
     */
    @Override
    public final Iterator<String> iterator() {
        return texts.iterator();
    }

    /**
     * Remove a text from the list.
     * @param text the text element to remove
     * @return true if the list contained the specified text element
     * @see List#remove(java.lang.Object)
     */
    public final boolean remove(final String text) {
        return texts.remove(text);
    }

    /**
     * @return the number of text elements in the list
     * @see List#size()
     */
    public final int size() {
        return texts.size();
    }
}
