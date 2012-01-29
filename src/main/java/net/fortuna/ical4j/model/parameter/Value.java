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
package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.util.Strings;

/**
 * $Id$ [18-Apr-2004]
 *
 * Defines a Value Data Type parameter.
 * @author Ben Fortuna
 */
public class Value extends Parameter {

    private static final long serialVersionUID = -7238642734500301768L;

    private static final String VALUE_BINARY = "BINARY";

    private static final String VALUE_BOOLEAN = "BOOLEAN";

    private static final String VALUE_CAL_ADDRESS = "CAL-ADDRESS";

    private static final String VALUE_DATE = "DATE";

    private static final String VALUE_DATE_TIME = "DATE-TIME";

    private static final String VALUE_DURATION = "DURATION";

    private static final String VALUE_FLOAT = "FLOAT";

    private static final String VALUE_INTEGER = "INTEGER";

    private static final String VALUE_PERIOD = "PERIOD";

    private static final String VALUE_RECUR = "RECUR";

    private static final String VALUE_TEXT = "TEXT";

    private static final String VALUE_TIME = "TIME";

    private static final String VALUE_URI = "URI";

    private static final String VALUE_UTC_OFFSET = "UTC-OFFSET";

    /**
     * Binary value type.
     */
    public static final Value BINARY = new Value(VALUE_BINARY);

    /**
     * Boolean value type.
     */
    public static final Value BOOLEAN = new Value(VALUE_BOOLEAN);

    /**
     * Calendar address value type.
     */
    public static final Value CAL_ADDRESS = new Value(VALUE_CAL_ADDRESS);

    /**
     * Date value type.
     */
    public static final Value DATE = new Value(VALUE_DATE);

    /**
     * Date-time value type.
     */
    public static final Value DATE_TIME = new Value(VALUE_DATE_TIME);

    /**
     * Duration value type.
     */
    public static final Value DURATION = new Value(VALUE_DURATION);

    /**
     * Float value type.
     */
    public static final Value FLOAT = new Value(VALUE_FLOAT);

    /**
     * Integer value type.
     */
    public static final Value INTEGER = new Value(VALUE_INTEGER);

    /**
     * Period value type.
     */
    public static final Value PERIOD = new Value(VALUE_PERIOD);

    /**
     * Recurrence value type.
     */
    public static final Value RECUR = new Value(VALUE_RECUR);

    /**
     * Text value type.
     */
    public static final Value TEXT = new Value(VALUE_TEXT);

    /**
     * Time value type.
     */
    public static final Value TIME = new Value(VALUE_TIME);

    /**
     * URI value type.
     */
    public static final Value URI = new Value(VALUE_URI);

    /**
     * UTC offset value type.
     */
    public static final Value UTC_OFFSET = new Value(VALUE_UTC_OFFSET);

    private String value;

    /**
     * @param aValue a string representation of a value data type
     */
    public Value(final String aValue) {
        super(VALUE, ParameterFactoryImpl.getInstance());
        this.value = Strings.unquote(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }
}
