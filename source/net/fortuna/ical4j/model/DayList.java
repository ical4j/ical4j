/*
 * $Id$ [29-May-2004]
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Defines a list of days.
 * @author benfortuna
 */
public class DayList implements Serializable {

    private List days;

    /**
     * Constructor.
     */
    public DayList() {
        days = new ArrayList();
    }

    /**
     * Constructor.
     * @param aString a string representation of a day list
     */
    public DayList(final String aString) {
        days = new ArrayList();

        for (StringTokenizer t = new StringTokenizer(aString, ","); t
                .hasMoreTokens();) {
            days.add(t.nextToken());
        }
    }

    /**
     * @param aDay a day to add to the list
     * @return
     */
    public final boolean add(final String aDay) {
        return days.add(aDay);
    }

    /**
     * @return
     */
    public final boolean isEmpty() {
        return days.isEmpty();
    }

    /**
     * @return
     */
    public final Iterator iterator() {
        return days.iterator();
    }

    /**
     * @param aDay a day to remove from the list
     * @return
     */
    public final boolean remove(final String aDay) {
        return days.remove(aDay);
    }

    /**
     * @return
     */
    public final int size() {
        return days.size();
    }


    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer();

        for (Iterator i = iterator(); i.hasNext();) {
            b.append(i.next());

            if (i.hasNext()) {
                b.append(',');
            }
        }

        return b.toString();
    }
}
