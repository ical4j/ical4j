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
import java.util.*;
import java.util.stream.Collectors;

/**
 * $Id$ [23-Apr-2004]
 *
 * Defines a list of iCalendar text elements.
 * @author Ben Fortuna
 */
public class TextList implements Serializable {

	private static final long serialVersionUID = -417427815871330636L;

	private final Set<String> texts;

    /**
     * Default constructor.
     */
    public TextList() {
        texts = Collections.emptySet();
    }

    /**
     * Parses the specified string representation to create a list of categories.
     * @param aValue a string representation of a list of categories
     */
    public TextList(final String aValue) {
        // split on unescaped commas with a plain scan rather than a regular expression, as a repeated
        // group recurses once per escape sequence and overflows the stack on long values. Each item is
        // then unescaped exactly once..
        List<String> values = new ArrayList<>();
        final var item = new StringBuilder();
        for (int i = 0; i < aValue.length(); i++) {
            final char c = aValue.charAt(i);
            if (c == '\\' && i + 1 < aValue.length()) {
                item.append(c).append(aValue.charAt(++i));
            } else if (c == ',') {
                addItem(values, item);
            } else {
                item.append(c);
            }
        }
        addItem(values, item);
        texts = new LinkedHashSet<>(values);
    }

    private static void addItem(List<String> values, StringBuilder item) {
        if (item.length() > 0) {
            values.add(Strings.unescape(item.toString()));
            item.setLength(0);
        }
    }

    public TextList(List<String> texts) {
        this.texts = new LinkedHashSet<>(texts);
    }

    /**
     * @param textValues an array of text values
     */
    public TextList(String...textValues) {
        texts = new LinkedHashSet<>(Arrays.asList(textValues));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return texts.stream().map(Strings::escape).collect(Collectors.joining(","));
    }

    /**
     * Add an address to the list.
     * @param text the category to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final TextList add(final String text) {
        List<String> newlist = new ArrayList<>(texts);
        newlist.add(text);
        return new TextList(newlist);
    }

    /**
     * Remove a text from the list.
     * @param text the text element to remove
     * @return true if the list contained the specified text element
     * @see List#remove(java.lang.Object)
     */
    public final TextList remove(final String text) {
        List<String> newlist = new ArrayList<>(texts);
        if (newlist.remove(text)) {
            return new TextList(newlist);
        } else {
            return this;
        }
    }

    public Set<String> getTexts() {
        return texts;
    }
}
