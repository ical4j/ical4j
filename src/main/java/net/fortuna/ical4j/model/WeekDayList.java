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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * $Id$ [29-May-2004]
 *
 * Defines a list of days.
 * 
 * @author Ben Fortuna
 */
public class WeekDayList extends ArrayList<WeekDay> implements Serializable {
    
    private static final long serialVersionUID = 1243262497035300445L;

    /**
     * Default constructor.
     */
    public WeekDayList() {
    }

    public WeekDayList(WeekDay...weekDays) {
        addAll(Arrays.asList(weekDays));
    }

    /**
     * Creates a new instance with the specified initial capacity.
     * @param initialCapacity the initial capacity of the list
     */
    public WeekDayList(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructor.
     * @param aString a string representation of a day list
     */
    public WeekDayList(final String aString) {
        this(aString, CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY));
    }

    /**
     * Parse a weekday list from a string.
     * @param aString a string representation of a day list
     * @param stripWhitespace remove any whitespace from the string tokens before parsing
     */
    public WeekDayList(final String aString, boolean stripWhitespace) {
        final StringTokenizer t = new StringTokenizer(aString, ",");
        while (t.hasMoreTokens()) {
            if (stripWhitespace) {
                add(new WeekDay(t.nextToken().replaceAll(" ", "")));
            } else {
                add(new WeekDay(t.nextToken()));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return stream().map(WeekDay::toString).collect(Collectors.joining(","));
    }
}
