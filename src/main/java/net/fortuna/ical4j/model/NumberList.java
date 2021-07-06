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

import net.fortuna.ical4j.util.Numbers;

import java.io.Serializable;
import java.time.temporal.ValueRange;
import java.util.*;
import java.util.stream.Collectors;

/**
 * $Id$ [29-May-2004]
 *
 * Defines a list of numbers.
 * 
 * @author Ben Fortuna
 */
public class NumberList extends ArrayList<Integer> implements Serializable {
    
    private static final long serialVersionUID = -1667481795613729889L;

    private final ValueRange valueRange;

    private final boolean allowsNegativeValues;
    
    /**
     * Default constructor.
     */
    public NumberList() {
    	this(ValueRange.of(Integer.MIN_VALUE, Integer.MAX_VALUE), true);
    }

    /**
     * Construct a number list restricted by the specified {@link ValueRange}.
     * @param valueRange a range defining the lower and upper bounds of allowed values
     * @param allowsNegativeValues allow negative values, where abs(value) is within the specified range
     */
    public NumberList(ValueRange valueRange, boolean allowsNegativeValues) {
        this.valueRange = valueRange;
        this.allowsNegativeValues = allowsNegativeValues;
    }

    /**
     * Constructor with limits.
     * @param minValue the minimum allowable value
     * @param maxValue the maximum allowable value
     * @param allowsNegativeValues indicates whether negative values are allowed
     *
     * @deprecated use {@link NumberList#NumberList(ValueRange, boolean)}
     */
    @Deprecated
    public NumberList(int minValue, int maxValue, boolean allowsNegativeValues) {
    	this(ValueRange.of(minValue, maxValue), allowsNegativeValues);
    }

    /**
     * Constructor.
     * @param aString a string representation of a number list
     */
    public NumberList(final String aString) {
    	this(aString, ValueRange.of(Integer.MIN_VALUE, Integer.MAX_VALUE), true);
    }

    /**
     * Construct a number list restricted by the specified {@link ValueRange}.
     * @param aString a string representation of a list of values
     * @param valueRange a range defining the lower and upper bounds of allowed values
     * @param allowsNegativeValues allow negative values, where abs(value) is within the specified range
     */
    public NumberList(final String aString, ValueRange valueRange, boolean allowsNegativeValues) {
        this(valueRange, allowsNegativeValues);
        addAll(Arrays.stream(aString.split(",")).map(Numbers::parseInt).collect(Collectors.toList()));
    }

    /**
     * @param aString a string representation of a number list
     * @param minValue the minimum allowable value
     * @param maxValue the maximum allowable value
     * @param allowsNegativeValues indicates whether negative values are allowed
     *
     * @deprecated use {@link NumberList#NumberList(String, ValueRange, boolean)}
     */
    @Deprecated
    public NumberList(final String aString, int minValue, int maxValue, boolean allowsNegativeValues) {
    	this(minValue, maxValue, allowsNegativeValues);
        final StringTokenizer t = new StringTokenizer(aString, ",");
        while (t.hasMoreTokens()) {
        	final int value = Numbers.parseInt(t.nextToken());
            add(value);
        }
    }

    /**
     * @param aNumber a number to add to the list
     * @return true if the number was added, otherwise false
     */
    @Override
    public final boolean add(final Integer aNumber) {
        int abs = aNumber;
        if ((abs >> 31 | -abs >>> 31) < 0) {
            if (!allowsNegativeValues) {
                throw new IllegalArgumentException("Negative value not allowed: " + aNumber);
            }
            abs = Math.abs(abs);
        }
    	if (!valueRange.isValidIntValue(abs)) {
    		throw new IllegalArgumentException("Value not in range [" + valueRange + "]: " + aNumber);
    	}
        return super.add(aNumber);
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        Optional<? extends Integer> negativeValue = c.stream().filter(v -> (v >> 31 | -v >>> 31) < 0)
                .findFirst();
        if (!allowsNegativeValues && negativeValue.isPresent()) {
            throw new IllegalArgumentException("Negative value not allowed: " + negativeValue.get());
        }

        Optional<? extends Integer> invalidValue = c.stream().filter(v -> !valueRange.isValidValue(Math.abs(v)))
                .findFirst();
        if (invalidValue.isPresent()) {
            throw new IllegalArgumentException(
                    "Value not in range [" + valueRange + "]: " + invalidValue);
        }
        return super.addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
