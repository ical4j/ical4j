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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.DateFormat;
import net.fortuna.ical4j.util.DateTimeFormat;

/**
 * Defines a list of iCalendar dates.
 * @author benfortuna
 */
public class DateList {

    private Value type;
    private List dates;

    /**
     * Default constructor.
     * @param aType specifies the type of dates (either date or
     * date-time)
     */
    public DateList(final Value aType) {
        dates = new ArrayList();

        this.type = aType;
    }

    /**
     * Parses the specified string representation to create
     * a list of dates.
     * @param aValue a string representation of a list of
     * dates
     * @param aType specifies the type of dates (either date or
     * date-time)
     * @throws ParseException if an invalid date representation
     * exists in the date list string
     */
    public DateList(final String aValue, final Value aType)
        throws ParseException {
        dates = new ArrayList();

        this.type = aType;

        for (StringTokenizer t = new StringTokenizer(aValue, ","); t
                .hasMoreTokens();) {
            if (type != null && Value.DATE.equals(type.getValue())) {
                dates.add(DateFormat.getInstance().parse(t.nextToken()));
            }
            else {
                dates.add(DateTimeFormat.getInstance().parse(t.nextToken()));
            }
        }
    }

    /**
     * @see java.util.AbstractCollection#toString()
     */
    public final String toString() {

        StringBuffer b = new StringBuffer();

        for (Iterator i = dates.iterator(); i.hasNext();) {

            if (type != null && Value.DATE.equals(type.getValue())) {
                b.append(DateFormat.getInstance().format((Date) i.next()));
            }
            else {
                b.append(DateTimeFormat.getInstance().format((Date) i.next()));
            }

            if (i.hasNext()) {
                b.append(',');
            }
        }

        return b.toString();
    }

    /**
     * Add a date to the list.
     * @param date the date to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final Date date) {
        return dates.add(date);
    }

    /**
     * @return boolean indicates if the list is empty
     * @see List#isEmpty()
     */
    public final boolean isEmpty() {
        return dates.isEmpty();
    }

    /**
     * @return an iterator
     * @see List#iterator()
     */
    public final Iterator iterator() {
        return dates.iterator();
    }

    /**
     * Remove a date from the list
     * @param date the date to remove
     * @return true if the list contained the specified date
     * @see List#remove(java.lang.Object)
     */
    public final boolean remove(final Date date) {
        return dates.remove(date);
    }

    /**
     * @return the number of dates in the list
     * @see List#size()
     */
    public final int size() {
        return dates.size();
    }

    /**
     * Returns the VALUE parameter specifying the type
     * of dates (ie. date or date-time) stored in this
     * date list.
     * @return Returns a Value parameter.
     */
    public final Value getType() {
        return type;
    }
}
