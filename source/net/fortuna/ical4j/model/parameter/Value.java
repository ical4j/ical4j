/*
 * $Id$ [18-Apr-2004]
 *
 * Copyright (c) 2005, Ben Fortuna
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
package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Parameter;

/**
 * Defines a Value Data Type parameter.
 * @author benfortuna
 */
public class Value extends Parameter {

    public static final Value DATE_TIME = new Value("DATE-TIME");

    public static final Value DATE = new Value("DATE");

    public static final Value DURATION = new Value("DURATION");

    public static final Value BINARY = new Value("BINARY");

    public static final Value PERIOD = new Value("PERIOD");

    private String value;

    /**
     * @param aValue a string representation of a value data
     * type
     */
    public Value(final String aValue) {
        super(VALUE);
        this.value = aValue;
    }

    /* (non-Javadoc)
	 * @see net.fortuna.ical4j.model.Parameter#getValue()
	 */
	public String getValue() {
		return value;
	}
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof Value) {
            return getValue().equals(((Value) arg0).getValue());
        }
        return super.equals(arg0);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        return getValue().hashCode();
    }
}
