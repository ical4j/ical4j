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

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Encodable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

/**
 * $Id$ [18-Apr-2004]
 *
 * Defines a Value Data Type parameter.
 * @author Ben Fortuna
 */
public class Value extends Parameter implements Encodable {

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

    public static final Value UID = new Value("UID");
    
    public static final Value XML_REFERENCE = new Value("XML-REFERENCE");

    private final String value;

    /**
     * @param aValue a string representation of a value data type
     */
    public Value(final String aValue) {
        super(VALUE);
        this.value = Strings.unquote(aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return value;
    }

    public static class Factory extends Content.Factory implements ParameterFactory<Value> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(VALUE);
        }

        @Override
        public Value createParameter(final String value) {
            switch (value.toUpperCase()) {
                case VALUE_BINARY: return BINARY;
                case VALUE_BOOLEAN: return BOOLEAN;
                case VALUE_DATE: return DATE;
                case VALUE_CAL_ADDRESS: return CAL_ADDRESS;
                case VALUE_DATE_TIME: return DATE_TIME;
                case VALUE_DURATION: return DURATION;
                case VALUE_FLOAT: return FLOAT;
                case VALUE_INTEGER: return INTEGER;
                case VALUE_PERIOD: return PERIOD;
                case VALUE_RECUR: return RECUR;
                case VALUE_TEXT: return TEXT;
                case VALUE_TIME: return TIME;
                case VALUE_URI: return URI;
                case VALUE_UTC_OFFSET: return UTC_OFFSET;
            }
            return new Value(value);
        }
    }
}
