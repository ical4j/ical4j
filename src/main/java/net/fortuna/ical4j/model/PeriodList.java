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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * $Id$ [23-Apr-2004]
 *
 * Defines a list of iCalendar periods. NOTE: By implementing the
 * <code>java.util.SortedSet</code> interface period lists will always be
 * sorted according to natural ordering.
 * 
 * @author Ben Fortuna
 */
public class PeriodList implements Set, Serializable {

	private static final long serialVersionUID = -2317587285790834492L;

	private final Set periods;
    
    private TimeZone timezone;
    
    private boolean utc;

    private final boolean unmodifiable;

    /**
     * Default constructor.
     */
    public PeriodList() {
        this(true);
    }

    /**
     * @param utc indicates whether the period list is in UTC time
     */
    public PeriodList(boolean utc) {
    	this(utc, false);
    }

    /**
     * @param utc indicates whether the period list is in UTC time
     */
    public PeriodList(boolean utc, final boolean unmodifiable) {
        this.utc = utc;
        this.unmodifiable = unmodifiable;
        if (unmodifiable) {
        	periods = Collections.EMPTY_SET;
        }
        else {
        	periods = new TreeSet();
        }
    }
    
    /**
     * Parses the specified string representation to create a list of periods.
     * 
     * @param aValue
     *            a string representation of a list of periods
     * @throws ParseException
     *             thrown when an invalid string representation of a period list
     *             is specified
     */
    public PeriodList(final String aValue) throws ParseException {
        this();
        final StringTokenizer t = new StringTokenizer(aValue, ",");
        while (t.hasMoreTokens()) {
            add((Object) new Period(t.nextToken()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer b = new StringBuffer();
        for (final Iterator i = iterator(); i.hasNext();) {
            b.append(i.next().toString());
            if (i.hasNext()) {
                b.append(',');
            }
        }
        return b.toString();
    }

    /**
     * Add a period to the list.
     * 
     * @param period
     *            the period to add
     * @return true
     * @see java.util.List#add(java.lang.Object)
     */
    public final boolean add(final Period period) {
        if (isUtc()) {
            period.setUtc(true);
        }
        else {
            period.setTimeZone(timezone);
        }
        return add((Object) period);
    }

    /**
     * Overrides superclass to throw an <code>IllegalArgumentException</code>
     * where argument is not a <code>net.fortuna.ical4j.model.Period</code>.
     * @param period a period to add to the list
     * @return true if the period was added, otherwise false
     * @see java.util.List#add(E)
     */
    public final boolean add(final Object period) {
        if (!(period instanceof Period)) {
            throw new IllegalArgumentException("Argument not a "
                    + Period.class.getName());
        }
        return periods.add(period);
    }

    /**
     * Remove a period from the list.
     * 
     * @param period
     *            the period to remove
     * @return true if the list contained the specified period
     * @see java.util.List#remove(java.lang.Object)
     */
    public final boolean remove(final Period period) {
        return remove((Object) period);
    }

    /**
     * Returns a normalised version of this period list. Normalisation includes
     * combining overlapping periods, removing periods contained by other
     * periods, combining adjacent periods, and removing periods that consume
     * no time. NOTE: If the period list is
     * already normalised then this period list is returned.
     * 
     * @return a period list
     */
    public final PeriodList normalise() {
        Period prevPeriod = null;
        Period period = null;
        final PeriodList newList = new PeriodList(isUtc());
        if (timezone != null) {
            newList.setTimeZone(timezone);
        }
        boolean normalised = false;
        for (final Iterator i = iterator(); i.hasNext();) {
            period = (Period) i.next();
            if (period.isEmpty()) {
                period = prevPeriod;
                normalised = true;
            }
            else if (prevPeriod != null) {
                // ignore periods contained by other periods..
                if (prevPeriod.contains(period)) {
                    period = prevPeriod;
                    normalised = true;
                }
                // combine intersecting periods..
                else if (prevPeriod.intersects(period)) {
                    period = prevPeriod.add(period);
                    normalised = true;
                }
                // combine adjacent periods..
                else if (prevPeriod.adjacent(period)) {
                    period = prevPeriod.add(period);
                    normalised = true;
                }
                else {
                    // if current period is recognised as distinct
                    // from previous period, add the previous period
                    // to the list..
                    newList.add(prevPeriod);
                }
            }
            prevPeriod = period;
        }
        // remember to add the last period to the list..
        if (prevPeriod != null) {
            newList.add(prevPeriod);
        }
        // only return new list if normalisation
        // has ocurred..
        if (normalised) {
            return newList;
        }
        else {
            return this;
	}
    }

    /**
     * A convenience method that combines all the periods in the specified list to
     * this list. The result returned is a new PeriodList instance, except where
     * no periods are specified in the arguments. In such cases this instance is returned.
     * 
     * Normalisation is also performed automatically after all periods have been added.
     * 
     * @param periods a list of periods to add
     * @return a period list instance
     */
    public final PeriodList add(final PeriodList periods) {
        if (periods != null) {
            final PeriodList newList = new PeriodList();
            newList.addAll(this);
            for (final Iterator i = periods.iterator(); i.hasNext();) {
                newList.add((Period) i.next());
            }
            return newList.normalise();
        }
        return this;
    }

    /**
     * Subtracts the intersection of this list with the specified list of
     * periods from this list and returns the results as a new period list. If
     * no intersection is identified this list is returned.
     * 
     * @param subtractions
     *            a list of periods to subtract from this list
     * @return a period list
     */
    public final PeriodList subtract(final PeriodList subtractions) {
        if (subtractions == null || subtractions.isEmpty()) {
            return this;
        }
        
        PeriodList result = this;
        PeriodList tmpResult = new PeriodList();

        for (final Iterator i = subtractions.iterator(); i.hasNext();) {
            final Period subtraction = (Period) i.next();
            for (final Iterator j = result.iterator(); j.hasNext();) {
                final Period period = (Period) j.next();
                tmpResult.addAll(period.subtract(subtraction));
            }
            result = tmpResult;
            tmpResult = new PeriodList();
        }

        return result;
    }

    public final boolean isUnmodifiable() {
        return unmodifiable;
    }

    /**
     * Indicates whether this list is in local or UTC format.
     * @return Returns true if in UTC format, otherwise false.
     */
    public final boolean isUtc() {
        return utc;
    }

    /**
     * Sets whether this list is in UTC or local time format.
     * @param utc The utc to set.
     */
    public final void setUtc(final boolean utc) {
        for (final Iterator i = iterator(); i.hasNext();) {
            final Period period = (Period) i.next();
            period.setUtc(utc);
        }
        this.timezone = null;
        this.utc = utc;
    }
    
    /**
     * Applies the specified timezone to all dates in the list.
     * All dates added to this list will also have this timezone
     * applied.
     * @param timeZone the timezone for the period list
     */
    public final void setTimeZone(final TimeZone timeZone) {
        for (final Iterator i = iterator(); i.hasNext();) {
            final Period period = (Period) i.next();
            period.setTimeZone(timeZone);
        }
        this.timezone = timeZone;
        this.utc = false;
    }

    /**
     * @return Returns the timeZone.
     */
    public final TimeZone getTimeZone() {
        return timezone;
    }

	/**
	 * {@inheritDoc}
	 */
	public final boolean addAll(Collection arg0) {
		for (Iterator i = arg0.iterator(); i.hasNext();) {
			add(i.next());
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void clear() {
		periods.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean contains(Object o) {
		return periods.contains(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean containsAll(Collection arg0) {
		return periods.containsAll(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isEmpty() {
		return periods.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public final Iterator iterator() {
		return periods.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean remove(Object o) {
		return periods.remove(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean removeAll(Collection arg0) {
		return periods.removeAll(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean retainAll(Collection arg0) {
		return periods.retainAll(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public final int size() {
		return periods.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public final Object[] toArray() {
		return periods.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	public final Object[] toArray(Object[] arg0) {
		return periods.toArray(arg0);
	}
	
	public final boolean equals(Object obj) {
		if (!getClass().isAssignableFrom(obj.getClass())) {
			return false;
		}
		final PeriodList rhs = (PeriodList) obj;
		return new EqualsBuilder().append(periods, rhs.periods)
			.append(timezone, rhs.timezone)
			.append(utc, utc)
			.isEquals();
	}
	
	public final int hashCode() {
		return new HashCodeBuilder().append(periods)
			.append(timezone)
			.append(utc)
			.toHashCode();
	}
}
