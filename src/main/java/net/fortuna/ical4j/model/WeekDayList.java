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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import net.fortuna.ical4j.util.CompatibilityHints;

/**
 * $Id$ [29-May-2004]
 *
 * Defines a list of days.
 * 
 * @author Ben Fortuna
 */
public class WeekDayList extends ArrayList implements Serializable {
    
    private static final long serialVersionUID = 1243262497035300445L;

    /**
     * Default constructor.
     */
    public WeekDayList() {
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
        final boolean outlookCompatibility =
            CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY);
        
        final StringTokenizer t = new StringTokenizer(aString, ",");
        while (t.hasMoreTokens()) {
            if (outlookCompatibility) {
                add(new WeekDay(t.nextToken().replaceAll(" ", "")));
            }
            else {
                add(new WeekDay(t.nextToken()));
            }
        }
    }

    /**
     * @param weekDay a day to add to the list
     * @return true if the week day is added, otherwise false
     */
    public final boolean add(final WeekDay weekDay) {
        return add((Object) weekDay);
    }
    
    /**
     * Overrides superclass to throw an <code>IllegalArgumentException</code>
     * where argument is not a <code>net.fortuna.ical4j.model.WeekDay</code>.
     * @param weekday a week day to add
     * @return true if the week day is added, otherwise false
     * @see List#add(E)
     */
    public final boolean add(final Object weekday) {
        if (!(weekday instanceof WeekDay)) {
            throw new IllegalArgumentException("Argument not a " + WeekDay.class.getName());
        }
        return super.add(weekday);
    }

    /**
     * @param weekDay a day to remove from the list
     * @return true if the week day is removed, otherwise false
     */
    public final boolean remove(final WeekDay weekDay) {
        return remove((Object) weekDay);
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        for (final Iterator i = iterator(); i.hasNext();) {
            b.append(i.next());
            if (i.hasNext()) {
                b.append(',');
            }
        }
        return b.toString();
    }
}
