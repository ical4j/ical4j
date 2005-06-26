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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.fortuna.ical4j.model.parameter.Value;

/**
 * Defines a list of iCalendar dates.
 * 
 * @author Ben Fortuna
 */
public class DateList extends ArrayList implements Serializable {

    private static final long serialVersionUID = 5925108767897130313L;

    private Value type;

    private boolean utc;

    /**
     * Default constructor.
     * 
     * @param aType
     *            specifies the type of dates (either date or date-time)
     */
    public DateList(final Value aType) {
        this.type = aType;
    }

    /**
     * Parses the specified string representation to create a list of dates.
     * 
     * @param aValue
     *            a string representation of a list of dates
     * @param aType
     *            specifies the type of dates (either date or date-time)
     * @throws ParseException
     *             if an invalid date representation exists in the date list
     *             string
     */
    public DateList(final String aValue, final Value aType)
            throws ParseException {
        this.type = aType;

        for (StringTokenizer t = new StringTokenizer(aValue, ","); t
                .hasMoreTokens();) {
            if (type != null && Value.DATE.equals(type)) {
                add(new Date(t.nextToken()));
            } else {
                add(new DateTime(t.nextToken()));
            }
        }
    }
    
    /**
     * Constructs a new date list of the specified type containing
     * the dates in the specified list.
     * @param list a list of dates to include in the new list
     * @param type the type of the new list
     */
    public DateList(final DateList list, final Value type) {
        if (!Value.DATE.equals(type) && !Value.DATE_TIME.equals(type)) {
            throw new IllegalArgumentException(
                    "Type must be either DATE or DATE-TIME");
        }
        this.type = type;
        if (Value.DATE.equals(type)) {
            for (Iterator i = list.iterator(); i.hasNext();) {
                add(new Date((Date) i.next()));
            }
        }
        else if (Value.DATE_TIME.equals(type)) {
            for (Iterator i = list.iterator(); i.hasNext();) {
                add(new DateTime((Date) i.next()));
            }
        }
    }

    /**
     * @see java.util.AbstractCollection#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer();
        for (Iterator i = iterator(); i.hasNext();) {
            /*
             * if (type != null && Value.DATE.equals(type)) {
             * b.append(DateFormat.getInstance().format((Date) i.next())); }
             * else { b.append(DateTimeFormat.getInstance().format((Date)
             * i.next(), isUtc())); }
             */
            b.append(i.next());
            if (i.hasNext()) {
                b.append(',');
            }
        }
        return b.toString();
    }

    /**
     * Add a date to the list.
     * 
     * @param date
     *            the date to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final Date date) {
        if (Value.DATE_TIME.equals(getType()) && !(date instanceof DateTime)) {
            return add((Object) new DateTime(date));
        }
        return add((Object) date);
    }
    
    /**
     * Overrides superclass to throw an <code>IllegalArgumentException</code>
     * Where argument is not a <code>net.fortuna.ical4j.model.Date</code>.
     * @see List#add(E)
     */
    public final boolean add(final Object arg0) {
        if (!(arg0 instanceof Date)) {
            throw new IllegalArgumentException("Argument not a " + Date.class.getName());
        }
        return super.add(arg0);
    }

    /**
     * Remove a date from the list
     * 
     * @param date
     *            the date to remove
     * @return true if the list contained the specified date
     * @see List#remove(java.lang.Object)
     */
    public final boolean remove(final Date date) {
        return remove((Object) date);
    }

    /**
     * Returns the VALUE parameter specifying the type of dates (ie. date or
     * date-time) stored in this date list.
     * 
     * @return Returns a Value parameter.
     */
    public final Value getType() {
        return type;
    }

    /**
     * Indicates whether this list is in local or UTC format. This property will
     * have no affect if the type of the list is not DATE-TIME.
     * 
     * @return Returns true if in UTC format, otherwise false.
     */
    public final boolean isUtc() {
        return utc;
    }

    /**
     * Sets whether this list is in UTC or local time format.
     * 
     * @param utc
     *            The utc to set.
     */
    public final void setUtc(final boolean utc) {
        if (Value.DATE_TIME.equals(type)) {
            for (Iterator i = iterator(); i.hasNext();) {
                ((DateTime) i.next()).setUtc(utc);
            }
        }
        this.utc = utc;
    }
}
