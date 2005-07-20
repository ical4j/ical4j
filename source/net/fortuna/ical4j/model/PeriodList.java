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
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Defines a list of iCalendar periods.
 * NOTE: By implementing the <code>java.util.SortedSet</code> interface period
 * lists will always be sorted according to natural ordering.
 * 
 * @author Ben Fortuna
 */
public class PeriodList extends TreeSet implements Serializable {
    
    private static final long serialVersionUID = -6319585959747194724L;

//    private List periods;

    /**
     * Default constructor.
     */
    public PeriodList() {
//        periods = new ArrayList();
    }

    /**
     * Parses the specified string representation to create
     * a list of periods.
     * @param aValue a string representation of a list of
     * periods
     * @throws ParseException thrown when an invalid string
     * representation of a period list is specified
     */
    public PeriodList(final String aValue) throws ParseException {
//        periods = new ArrayList();

        for (StringTokenizer t = new StringTokenizer(aValue, ","); t
                .hasMoreTokens();) {
            add(new Period(t.nextToken()));
        }
    }

    /**
     * @see java.util.AbstractCollection#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer();
        for (Iterator i = iterator(); i.hasNext();) {
            b.append(i.next().toString());
            if (i.hasNext()) {
                b.append(',');
            }
        }
        return b.toString();
    }

    /**
     * Add a period to the list.
     * @param period the period to add
     * @return true
     * @see java.util.List#add(java.lang.Object)
     */
    public final boolean add(final Period period) {
        return add((Object) period);
    }
    
    /**
     * Overrides superclass to throw an <code>IllegalArgumentException</code>
     * where argument is not a <code>net.fortuna.ical4j.model.Period</code>.
     * @see List#add(E)
     */
    public final boolean add(final Object arg0) {
        if (!(arg0 instanceof Period)) {
            throw new IllegalArgumentException("Argument not a " + Period.class.getName());
        }
        return super.add(arg0);
    }

    /**
     * @return boolean indicates if the list is empty
     * @see java.util.List#isEmpty()
     */
//    public final boolean isEmpty() {
//        return periods.isEmpty();
//    }

    /**
     * @return an iterator
     * @see java.util.List#iterator()
     */
//    public final Iterator iterator() {
//        return periods.iterator();
//    }

    /**
     * Remove a period from the list.
     * @param period the period to remove
     * @return true if the list contained the specified period
     * @see java.util.List#remove(java.lang.Object)
     */
    public final boolean remove(final Period period) {
        return remove((Object) period);
    }

    /**
     * @return the number of periods in the list
     * @see java.util.List#size()
     */
//    public final int size() {
//        return periods.size();
//    }
    
    /**
     * Returns a normalised version of this period list. Normalisation includes
     * combining overlapping periods, removing periods contained by other periods,
     * and combining adjacent periods.
     * NOTE: If the period list is already normalised then this period list is
     * returned.
     * @return a period list
     */
    public final PeriodList normalise() {
    	Period prevPeriod = null;
    	Period period = null;
        PeriodList newList = new PeriodList();
        boolean normalised = false;
    	for (Iterator i = iterator(); i.hasNext();) {
    		period = (Period) i.next();
    		if (prevPeriod != null) {
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
    	return this;
    }
    
    /**
     * A convenience method that adds all the periods in the specified list
     * to this list. Normalisation is also performed automatically after all
     * periods have been added.
     * @param periods
     */
    public final PeriodList add(final PeriodList periods) {
        if (periods != null) {
            PeriodList newList = new PeriodList();
            newList.addAll(this);
            for (Iterator i = periods.iterator(); i.hasNext();) {
                newList.add((Period) i.next());
            }
            return newList.normalise();
        }
        return this;
    }
    
    /**
     * Subtracts the intersection of this list with the specified list of periods
     * from this list and returns the results as a new period list. If no
     * intersection is identified this list is returned.
     * @param periods a list of periods to subtract from this list
     * @return a period list
     */
    public final PeriodList subtract(final PeriodList subtractions) {
        if (subtractions != null) {
            // intialise result list as identical to this..
            PeriodList result = new PeriodList();
            result.addAll(this);
            boolean intersects = false;
            // for each subtracted period update the resulting period
            // list..
            for (Iterator i = subtractions.iterator(); i.hasNext();) {
                Period subtraction = (Period) i.next();
                PeriodList tempResult = new PeriodList();
                for (Iterator j = result.iterator(); j.hasNext();) {
                    Period period = (Period) j.next();
                    if (subtraction.contains(period)) {
                        intersects = true;
                        // period is consumed by subtraction..
                        continue;
                    }
                    else if (subtraction.intersects(period)) {
                        DateTime newPeriodStart;
                        DateTime newPeriodEnd;
                        if (subtraction.getStart().before(period.getStart())) {
                            newPeriodStart = subtraction.getEnd();
                            newPeriodEnd = period.getEnd();
                        }
                        else if (subtraction.getEnd().before(period.getEnd())) {
                            newPeriodStart = period.getStart();
                            newPeriodEnd = subtraction.getStart();
                        }
                        else {
                            // subtraction consumed by period..
                            // initialise head period..
                            newPeriodStart = period.getStart();
                            newPeriodEnd = subtraction.getStart();
                            tempResult.add(new Period(newPeriodStart, newPeriodEnd));
                            // initialise tail period..
                            newPeriodStart = subtraction.getEnd();
                            newPeriodEnd = period.getEnd();
                        }
                        tempResult.add(new Period(newPeriodStart, newPeriodEnd));
                        intersects = true;
                    }
                    else {
                        tempResult.add(period);
                    }
                }
                result = tempResult;
            }
            // only return new list if intersection has ocurred..
            if (intersects) {
                return result;
            }
        }
        return this;
    }
}
