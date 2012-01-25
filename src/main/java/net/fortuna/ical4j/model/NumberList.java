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

import net.fortuna.ical4j.util.Numbers;

/**
 * $Id$ [29-May-2004]
 *
 * Defines a list of numbers.
 * 
 * @author Ben Fortuna
 */
public class NumberList extends ArrayList implements Serializable {
    
    private static final long serialVersionUID = -1667481795613729889L;

    private final int minValue;
    
    private final int maxValue;

    private final boolean allowsNegativeValues;
    
    /**
     * Default constructor.
     */
    public NumberList() {
    	this(Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    /**
     * Constructor with limits.
     * @param minValue the minimum allowable value
     * @param maxValue the maximum allowable value
     * @param allowsNegativeValues indicates whether negative values are allowed
     */
    public NumberList(int minValue, int maxValue, boolean allowsNegativeValues) {
    	this.minValue = minValue;
    	this.maxValue = maxValue;
        this.allowsNegativeValues = allowsNegativeValues;
    }

    /**
     * Constructor.
     * @param aString a string representation of a number list
     */
    public NumberList(final String aString) {
    	this(aString, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }
    
    /**
     * @param aString a string representation of a number list
     * @param minValue the minimum allowable value
     * @param maxValue the maximum allowable value
     * @param allowsNegativeValues indicates whether negative values are allowed
     */
    public NumberList(final String aString, int minValue, int maxValue, boolean allowsNegativeValues) {
    	this(minValue, maxValue, allowsNegativeValues);
        final StringTokenizer t = new StringTokenizer(aString, ",");
        while (t.hasMoreTokens()) {
        	final int value = Numbers.parseInt(t.nextToken());
            add(new Integer(value));
        }
    }

    /**
     * @param aNumber a number to add to the list
     * @return true if the number was added, otherwise false
     */
    public final boolean add(final Integer aNumber) {
        int abs = aNumber.intValue();
        if ((abs >> 31 | -abs >>> 31) < 0) {
            if (!allowsNegativeValues) {
                throw new IllegalArgumentException("Negative value not allowed: " + aNumber);
            }
            abs = Math.abs(abs);
        }
    	if (abs < minValue || abs > maxValue) {
    		throw new IllegalArgumentException(
    		        "Value not in range [" + minValue + ".." + maxValue + "]: " + aNumber);
    	}
        return add((Object) aNumber);
    }
    
    /**
     * Overrides superclass to throw an <code>IllegalArgumentException</code>
     * where argument is not a <code>java.lang.Integer</code>.
     * @param arg0 an object to add
     * @return true if the object was added, otherwise false
     * @see List#add(E)
     */
    public final boolean add(final Object arg0) {
        if (!(arg0 instanceof Integer)) {
            throw new IllegalArgumentException("Argument not a " + Integer.class.getName());
        }
        return super.add(arg0);
    }

    /**
     * @param aNumber a number to remove from the list
     * @return true if the number was removed, otherwise false
     */
    public final boolean remove(final Integer aNumber) {
        return remove((Object) aNumber);
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
