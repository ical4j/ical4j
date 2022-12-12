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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * $Id$ [23-Apr-2004]
 *
 * Defines a list of iCalendar periods. NOTE: By implementing the
 * <code>java.util.SortedSet</code> interface period lists will always be
 * sorted according to natural ordering.
 * 
 * @author Ben Fortuna
 */
public class PeriodList implements Set<Period>, Serializable {

	private static final long serialVersionUID = -2317587285790834492L;

	private final Set<Period> periods;
    
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
        	periods = Collections.emptySet();
        }
        else {
        	periods = new TreeSet<Period>();
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
            add(new Period(t.nextToken()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return stream().map(Period::toString).collect(Collectors.joining(","));
    }

    /**
     * Add a period to the list.
     * 
     * @param period
     *            the period to add
     * @return true
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public final boolean add(final Period period) {
        if (isUtc()) {
            period.setUtc(true);
        }
        else {
            period.setTimeZone(timezone);
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
    @Override
    public final boolean remove(final Object period) {
        return periods.remove(period);
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
        Period period;
        final PeriodList newList = new PeriodList(isUtc());
        if (timezone != null) {
            newList.setTimeZone(timezone);
        }
        boolean normalised = false;
        for (Period period1 : this) {
            period = period1;
            if (period.isEmpty()) {
                period = prevPeriod;
                normalised = true;
            } else if (prevPeriod != null) {
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
                } else {
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
            newList.addAll(periods);
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

        for (final Period subtraction : subtractions) {
            for (final Period period : result) {
                tmpResult.addAll(period.subtract(subtraction));
            }
            result = tmpResult;
            tmpResult = new PeriodList();
        }

        return result;
    }

    /**
     * Indicates whether this list is in local or UTC format.
     * @return Returns true if in UTC format, otherwise false.
     */
    public final boolean isUtc() {
        return utc;
    }

    public boolean isUnmodifiable() {
		return unmodifiable;
	}
    
    /**
     * Sets whether this list is in UTC or local time format.
     * @param utc The utc to set.
     */
    public final void setUtc(final boolean utc) {
        for (final Period period : this) {
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
        for (final Period period : this) {
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
	@Override
	public boolean addAll(Collection<? extends Period> arg0) {
		for (Period p : arg0) {
			add(p);
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		periods.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object o) {
		return periods.contains(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> arg0) {
		return periods.containsAll(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return periods.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Period> iterator() {
		return periods.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Collection<?> arg0) {
		return periods.removeAll(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean retainAll(Collection<?> arg0) {
		return periods.retainAll(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return periods.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		return periods.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T[] toArray(T[] arg0) {
		return periods.toArray(arg0);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PeriodList)) {
			return false;
		}
		final PeriodList rhs = (PeriodList) obj;
		return new EqualsBuilder().append(periods, rhs.periods)
			.append(timezone, rhs.timezone)
			.append(utc, rhs.utc)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(periods)
			.append(timezone)
			.append(utc)
			.toHashCode();
	}
}
