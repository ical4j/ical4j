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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import net.fortuna.ical4j.model.parameter.Value;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * $Id$ [23-Apr-2004]
 *
 * Defines a list of iCalendar dates. If no value type is specified a list
 * defaults to DATE-TIME instances.
 * @author Ben Fortuna
 */
public class DateList implements List, Serializable {

	private static final long serialVersionUID = -3700862452550012357L;

	private final Value type;
    
    private final List dates;

    private TimeZone timeZone;
    
    private boolean utc;

    /**
     * Default constructor.
     */
    public DateList() {
    	this(false);
    }

    public DateList(final boolean unmodifiable) {
    
        this.type = Value.DATE_TIME;
        if (unmodifiable) {
        	dates = Collections.EMPTY_LIST;
        }
        else {
            dates = new ArrayList();
        }
    }

    /**
     * @param aType the type of dates contained by the instance
     */
    public DateList(final Value aType) {
        this(aType, null);
    }
    
    /**
     * Default constructor.
     * 
     * @param aType
     *            specifies the type of dates (either date or date-time)
     * @param timezone the timezone to apply to dates contained by the instance
     */
    public DateList(final Value aType, final TimeZone timezone) {
        if (aType == null) {
            this.type = Value.DATE_TIME;
        } else {
            this.type = aType;
        }
        this.timeZone = timezone;
        dates = new ArrayList();
    }

    /**
     * @param aValue a string representation of a date list
     * @param aType the date types contained in the instance
     * @throws ParseException where the specified string is not a valid date list
     */
    public DateList(final String aValue, final Value aType) throws ParseException {
        this(aValue, aType, null);
    }
    
    /**
     * Parses the specified string representation to create a list of dates.
     * 
     * @param aValue
     *            a string representation of a list of dates
     * @param aType
     *            specifies the type of dates (either date or date-time)
     * @param timezone the timezone to apply to contained dates
     * @throws ParseException
     *             if an invalid date representation exists in the date list
     *             string
     */
    public DateList(final String aValue, final Value aType, final TimeZone timezone)
            throws ParseException {
    	
        this(aType, timezone);
        final StringTokenizer t = new StringTokenizer(aValue, ",");
        while (t.hasMoreTokens()) {
            if (Value.DATE.equals(type)) {
                add((Object) new Date(t.nextToken()));
            }
            else {
                add((Object) new DateTime(t.nextToken(), timezone));
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
        dates = new ArrayList();
        
        if (Value.DATE.equals(type)) {
            for (final Iterator i = list.iterator(); i.hasNext();) {
                add(new Date((Date) i.next()));
            }
        }
        else {
            for (final Iterator i = list.iterator(); i.hasNext();) {
                add(new DateTime((Date) i.next()));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        for (final Iterator i = iterator(); i.hasNext();) {
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
     * Add a date to the list. The date will be updated to reflect the
     * timezone of this list.
     * @param date the date to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final Date date) {
        if (date instanceof DateTime) {
            if (isUtc()) {
                ((DateTime) date).setUtc(true);
            }
            else {
                ((DateTime) date).setTimeZone(getTimeZone());
            }
        }
        else if (!Value.DATE.equals(getType())) {
            final DateTime dateTime = new DateTime(date);
            dateTime.setTimeZone(getTimeZone());
            return add((Object) dateTime);
        }
        return add((Object) date);
    }
    
    /**
     * Overrides superclass to throw an <code>IllegalArgumentException</code>
     * Where argument is not a <code>net.fortuna.ical4j.model.Date</code>.
     * @param date the date to add
     * @return true if the object was added, otherwise false
     * @see List#add(E)
     */
    public final boolean add(final Object date) {
        if (!(date instanceof Date)) {
            throw new IllegalArgumentException("Argument not a " + Date.class.getName());
        }
        return dates.add(date);
    }

    /**
     * Remove a date from the list.
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
        if (!Value.DATE.equals(type)) {
            for (final Iterator i = iterator(); i.hasNext();) {
                ((DateTime) i.next()).setUtc(utc);
            }
        }
        this.timeZone = null;
        this.utc = utc;
    }
    
    /**
     * Applies the specified timezone to all dates in the list.
     * All dates added to this list will also have this timezone
     * applied.
     * @param timeZone a timezone to apply to contained dates
     */
    public final void setTimeZone(final TimeZone timeZone) {
        if (!Value.DATE.equals(type)) {
            for (final Iterator i = iterator(); i.hasNext();) {
                ((DateTime) i.next()).setTimeZone(timeZone);
            }
        }
        this.timeZone = timeZone;
        this.utc = false;
    }

    /**
     * @return Returns the timeZone.
     */
    public final TimeZone getTimeZone() {
        return timeZone;
    }

	public final void add(int arg0, Object arg1) {
		dates.add(arg0, arg1);
	}

	public final boolean addAll(Collection arg0) {
		return dates.addAll(arg0);
	}

	public final boolean addAll(int arg0, Collection arg1) {
		return dates.addAll(arg0, arg1);
	}

	public final void clear() {
		dates.clear();
	}

	public final boolean contains(Object o) {
		return dates.contains(o);
	}

	public final boolean containsAll(Collection arg0) {
		return dates.containsAll(arg0);
	}

	public final Object get(int index) {
		return dates.get(index);
	}

	public final int indexOf(Object o) {
		return dates.indexOf(o);
	}

	public final boolean isEmpty() {
		return dates.isEmpty();
	}

	public final Iterator iterator() {
		return dates.iterator();
	}

	public final int lastIndexOf(Object o) {
		return dates.lastIndexOf(o);
	}

	public final ListIterator listIterator() {
		return dates.listIterator();
	}

	public final ListIterator listIterator(int index) {
		return dates.listIterator(index);
	}

	public final Object remove(int index) {
		return dates.remove(index);
	}

	public final boolean remove(Object o) {
		return dates.remove(o);
	}

	public final boolean removeAll(Collection arg0) {
		return dates.removeAll(arg0);
	}

	public final boolean retainAll(Collection arg0) {
		return dates.retainAll(arg0);
	}

	public final Object set(int arg0, Object arg1) {
		return dates.set(arg0, arg1);
	}

	public final int size() {
		return dates.size();
	}

	public final List subList(int fromIndex, int toIndex) {
		return dates.subList(fromIndex, toIndex);
	}

	public final Object[] toArray() {
		return dates.toArray();
	}

	public final Object[] toArray(Object[] arg0) {
		return dates.toArray(arg0);
	}
	
	public final boolean equals(Object obj) {
		if (!getClass().isAssignableFrom(obj.getClass())) {
			return false;
		}
		final DateList rhs = (DateList) obj;
		return new EqualsBuilder().append(dates, rhs.dates)
			.append(type, rhs.type)
			.append(timeZone, rhs.timeZone)
			.append(utc, utc)
			.isEquals();
	}
	
	public final int hashCode() {
		return new HashCodeBuilder().append(dates)
			.append(type)
			.append(timeZone)
			.append(utc)
			.toHashCode();
	}
}
